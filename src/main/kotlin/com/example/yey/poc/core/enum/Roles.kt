package com.example.yey.poc.core.enum

enum class Roles(val label: String, val value: Int) {
    ADMIN("Admin", 1), SUPPORT("Support", 2);

    companion object {
        fun getByValue(value: Int): Roles? {
            for (status in Roles.values()) {
                if (status.value == value) {
                    return status
                }
            }
            return null
        }
    }
}