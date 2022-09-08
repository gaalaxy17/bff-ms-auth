package com.example.yey.poc.controller

import com.example.yey.poc.core.dto.AjaxResult
import com.example.yey.poc.core.dto.UserDTO
import com.example.yey.poc.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController(val userService: UserService) : BaseController() {
    @PostMapping("/v1/user")
    private fun save(@RequestBody userDto: UserDTO): ResponseEntity<AjaxResult> {
        return try {
            buildSucessfulAjaxResult(this.userService.save(userDto));
        } catch (e: Exception) {
            buildErrorAjaxResult(e);
        }
    }

    @PatchMapping("/v1/user/self")
    private fun patchSelfUser(@RequestBody userDto: UserDTO): ResponseEntity<AjaxResult> {
        return try {
            buildSucessfulAjaxResult(this.userService.patchSelfUser(userDto));
        } catch (e: Exception) {
            buildErrorAjaxResult(e);
        }
    }

    @GetMapping("/v1/user/{id}")
    private fun getUser(@PathVariable("id") id: String): ResponseEntity<AjaxResult> {
        return try {
            buildSucessfulAjaxResult(this.userService.getUser(id));
        } catch (e: Exception) {
            buildErrorAjaxResult(e);
        }
    }

    @GetMapping("/v1/users")
    private fun list(@RequestParam(required = false) id: String?, @RequestParam(required = false) username: String?,
                     @RequestParam(required = false) email: String?, @RequestParam(required = false) name: String?,
                     @RequestParam(required = false) isActive: Int?, @RequestParam(required = false) roleId: Int?,
                     @RequestParam(required = false) createdAt: Date?): ResponseEntity<AjaxResult> {
        return try {
            buildSucessfulAjaxResult(this.userService.list(id, username, email, name, isActive, roleId, createdAt));
        } catch (e: Exception) {
            buildErrorAjaxResult(e);
        }
    }

    @GetMapping("/v1/user/login")
    private fun login(@RequestParam("username") username: String, @RequestParam("password") password: String): ResponseEntity<AjaxResult> {
        return try {
            buildSucessfulAjaxResult(this.userService.login(username, password));
        } catch (e: Exception) {
            buildErrorAjaxResult(e);
        }
    }

}