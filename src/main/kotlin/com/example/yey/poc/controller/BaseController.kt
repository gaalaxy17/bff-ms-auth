package com.example.yey.poc.controller

import com.example.yey.poc.core.dto.AjaxResult
import com.example.yey.poc.core.exception.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.thymeleaf.util.StringUtils

open class BaseController {

    fun buildSucessfulAjaxResult(data: Any?): ResponseEntity<AjaxResult> {
        val result: AjaxResult = AjaxResult(message = "Sucesso", data);
        val response: ResponseEntity<AjaxResult> = ResponseEntity(result, HttpStatus.OK);
        return response;
    }

    fun buildErrorAjaxResult(e: Exception): ResponseEntity<AjaxResult> {
        var message = "Ocorreu um erro inesperado";
        if (!StringUtils.isEmpty(e.message)) {
            message = e.message!!;
        }
        val result: AjaxResult = AjaxResult(message, data = null);

        return try {
            val serviceException: ServiceException = e as ServiceException;
            val response: ResponseEntity<AjaxResult> = ResponseEntity(result, e.httpStatus);
            response;
        } catch (ex: Exception) {
            val response: ResponseEntity<AjaxResult> = ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
            response;
        }


    }
}