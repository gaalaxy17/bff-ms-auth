package com.example.yey.poc.core.dto

import java.util.*
import javax.persistence.Id

class UserDTO(val id: String?, val username: String?,
              val email: String?, val name: String?,
              val password: String?, val isActive: Int?,
              val roleId: Int?, val createdAt: Date?) {


}