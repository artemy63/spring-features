package org.artemy63.exceptionhandling.controller;

import org.artemy63.exceptionhandling.dto.ErrorResponseDTO;
import org.artemy63.exceptionhandling.dto.SuccessResponseDTO;
import org.artemy63.exceptionhandling.exception.HandledException;
import org.artemy63.exceptionhandling.exception.HandledGloballyException;
import org.artemy63.exceptionhandling.exception.UnHandledException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
@RequestMapping("/exception-handling/v1")
public class ExceptionHandlingV1Controller {

    @RequestMapping("/give-me-success")
    @ResponseBody
    public SuccessResponseDTO giveMeSuccess() {
        return SuccessResponseDTO.builder()
                .name("give-me-success")
                .data(SuccessResponseDTO.InnerSuccessResponseDTO.builder()
                        .name("success name")
                        .values(Collections.singletonList("one success value"))
                        .build())
                .build();
    }

    @RequestMapping("/give-me-handled-exception")
    @ResponseBody
    public SuccessResponseDTO giveMeHandledException(@RequestParam(required = false) String isThrowException) {
        if(Boolean.parseBoolean(isThrowException)) {
            throw new HandledException("ExceptionHandlingV1Controller#HandledException occurs :: ", this.getClass().getName());
        }
        return SuccessResponseDTO.builder()
                .name("give-me-success")
                .data(SuccessResponseDTO.InnerSuccessResponseDTO.builder()
                        .name("success name")
                        .values(Collections.singletonList("one success value"))
                        .build())
                .build();
    }

    @RequestMapping("/give-me-handled-globally-exception")
    @ResponseBody
    public SuccessResponseDTO giveMeHandledGloballyException(@RequestParam(required = false) String isThrowException) {
        if(Boolean.parseBoolean(isThrowException)) {
            throw new HandledGloballyException("ExceptionHandlingV1Controller#HandledGloballyException occurs :: ", this.getClass().getName());
        }
        return SuccessResponseDTO.builder()
                .name("give-me-success")
                .data(SuccessResponseDTO.InnerSuccessResponseDTO.builder()
                        .name("success name")
                        .values(Collections.singletonList("one success value"))
                        .build())
                .build();
    }

    @RequestMapping("/give-me-unhandled-exception")
    @ResponseBody
    public SuccessResponseDTO giveMeUnHandledException(@RequestParam(required = false) String isThrowException) {
        if(Boolean.parseBoolean(isThrowException)) {
            throw new UnHandledException("ExceptionHandlingV1Controller#UnHandledException occurs ::", "ExceptionHandlingV1Controller should not handle it!");
        }
        return SuccessResponseDTO.builder()
                .name("give-me-success")
                .data(SuccessResponseDTO.InnerSuccessResponseDTO.builder()
                        .name("success name")
                        .values(Collections.singletonList("one success value"))
                        .build())
                .build();
    }

    @ExceptionHandler(HandledException.class)
    @ResponseBody
    public ErrorResponseDTO handleException(HandledException e) {
        return ErrorResponseDTO.builder()
                .message(e.getMessage())
                .sourceClass(e.getProperty())
                .build();
    }
}
