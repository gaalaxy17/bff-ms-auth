package com.example.yey.poc.repository

import com.example.yey.poc.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface UserRepository : MongoRepository<User, String?> {
    fun findByUsername(username: String): Optional<User>
}