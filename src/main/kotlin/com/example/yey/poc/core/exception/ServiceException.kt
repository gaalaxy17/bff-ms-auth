package com.example.yey.poc.core.exception

import org.springframework.http.HttpStatus

class ServiceException(message: String, val httpStatus: HttpStatus) : Exception(message) {

}