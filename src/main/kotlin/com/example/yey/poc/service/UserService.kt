package com.example.yey.poc.service

import com.example.yey.poc.core.dto.UserDTO
import com.example.yey.poc.core.exception.ServiceException
import com.example.yey.poc.entity.User
import com.example.yey.poc.entity.UserRole
import com.example.yey.poc.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.http.HttpStatus
import org.springframework.security.config.Elements.JWT
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import org.springframework.web.client.HttpServerErrorException
import org.thymeleaf.util.StringUtils

import java.util.*
import org.springframework.data.mongodb.core.query.Query
import javax.swing.text.html.Option

@Service
class UserService(val userRepository: UserRepository, val mongoTemplate: MongoTemplate) {

    private var env: MutableMap<String, String> = System.getenv();
    var EXPIRATION_TIME: String? = env["EXPIRATION_TIME"];
    var SECRET: String? = env["SECRET"];

    fun save(userDto: UserDTO): String {

        val salt = BCrypt.gensalt(10);
        val hashPass = BCrypt.hashpw(userDto.password!!, salt);

        val user = User(UUID.randomUUID().toString(), userDto.username!!, userDto.email!!, userDto.name!!,
                hashPass, 1, UserRole(userDto.roleId!!, null), Date());
        this.userRepository.save(user);
        return user.id;
    }

    fun patchSelfUser(userDto: UserDTO): String {
        if (!StringUtils.isEmpty(userDto.id)) {

            val userOpt: Optional<User> = this.userRepository.findById(userDto.id!!);
            if (userOpt.isPresent) {

                val user: User = userOpt.get();

                user.name = userDto.name!!;
                user.email = userDto.email!!;
                user.isActive = userDto.isActive!!;
                if (userDto.roleId != null) {
                    user.userRole = UserRole(userDto.roleId, null);
                }

                if (!StringUtils.isEmpty(userDto.password)) {
                    val salt = BCrypt.gensalt(10);
                    val hashPass = BCrypt.hashpw(userDto.password!!, salt);
                    user.password = hashPass;
                }

                this.userRepository.save(user);
            }
        }
        throw ServiceException("Usuário não encontrado!", HttpStatus.NOT_FOUND);
    }

    fun getUser(id: String): UserDTO {
        val userOpt: Optional<User> = this.userRepository.findById(id);

        if (userOpt.isPresent) {
            val user = userOpt.get();
            val userDto: UserDTO = UserDTO(user.id, user.username, user.email, user.name, null, user.isActive, user.userRole.id, user.createdAt);
            return userDto;
        }

        throw ServiceException("Usuário não encontrado!", HttpStatus.NOT_FOUND);
    }

    fun list(id: String?, username: String?, email: String?, name: String?, isActive: Int?, roleId: Int?, createdAt: Date?): List<UserDTO> {
        val results: MutableList<UserDTO> = mutableListOf();

        val query = Query()
        if (!StringUtils.isEmpty(id)) {
            query.addCriteria(Criteria.where("id").`is`(id));
        }
        if (!StringUtils.isEmpty(email)) {
            query.addCriteria(Criteria.where("email").regex(email!!, "i"));
        }
        if (!StringUtils.isEmpty(name)) {
            query.addCriteria(Criteria.where("name").regex(name!!, "i"));
        }
        if (isActive != null) {
            query.addCriteria(Criteria.where("isActive").`is`(isActive))
        }
        if (roleId != null) {
            query.addCriteria(Criteria.where("roleId").`is`(roleId))
        }
        if (createdAt != null) {
            query.addCriteria(Criteria.where("createdAt").`is`(createdAt))
        }

        val users = mongoTemplate.find<User>(query);

        if (!CollectionUtils.isEmpty(users)) {
            for (user in users) {
                val dto: UserDTO = UserDTO(user.id, user.username, user.email, user.name, null, user.isActive, user.userRole.id, user.createdAt);
                results.add(dto);
            }
        }


        return results;
    }

    fun login(username: String, password: String): String {

        if (StringUtils.isEmpty(EXPIRATION_TIME)) {
            EXPIRATION_TIME = "360000";
        }

        if (StringUtils.isEmpty(SECRET)) {
            SECRET = "mydevkeysecret";
        }

        val userOpt: Optional<User> = this.userRepository.findByUsername(username);
        if (userOpt.isPresent) {
            val user = userOpt.get();

            val match = BCrypt.checkpw(password, user.password);

            if (!match) throw Exception("Credenciais Inválidas!");

            val claims: Claims = Jwts.claims();

            claims["id"] = user.id;
            claims["name"] = user.name;
            claims["email"] = user.email;
            claims["username"] = user.username;
            claims["roleId"] = user.userRole.id.toString();

            val jwt: String = Jwts.builder()
                    .setSubject("API Auth")
                    .setClaims(claims)
                    .setExpiration(Date(System.currentTimeMillis() + Integer.parseInt(EXPIRATION_TIME)))
                    .signWith(SignatureAlgorithm.HS512, SECRET)
                    .compact();

            return jwt;

        }

        throw ServiceException("Credenciais inválidas!", HttpStatus.UNAUTHORIZED);
    }
}