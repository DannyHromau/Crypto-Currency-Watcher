package com.dannyhromau.watcher.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;


@RestControllerAdvice
public class RestExceptionHandler {


        @ExceptionHandler({EntityNotFoundException.class})
        protected ResponseEntity notFoundHandler() {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


}
