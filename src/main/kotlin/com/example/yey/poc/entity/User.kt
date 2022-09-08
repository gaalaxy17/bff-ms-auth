package com.example.yey.poc.entity

import java.util.*


class User(
        var id: String,
        var username: String,
        var email: String,
        var name: String,
        var password: String,
        var isActive: Int,
        var userRole: UserRole,
        var createdAt: Date) {
}