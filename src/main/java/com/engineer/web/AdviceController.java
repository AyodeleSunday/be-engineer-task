package com.engineer.web;

import com.engineer.exception.DuplicateException;
import com.engineer.exception.InvalidParameterException;
import com.engineer.exception.NotFoundException;
import com.engineer.model.Error;
import com.engineer.model.Response;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Sunday Ayodele
 * @create 8/21/2021
 */
@Slf4j
@ControllerAdvice
public class AdviceController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationException(MethodArgumentNotValidException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10400");
        response.setMsg("Bad request");
        BindingResult result = e.getBindingResult();
        List<FieldError> errorList = result.getFieldErrors();
        List<Error> errors = new ArrayList<>();
        for (FieldError fieldError : errorList) {
            errors.add(new Error(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        response.setData(errors);
        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setMsg("10400");
        response.setMsg(e.getMessage());
        if (e.getCause() != null) {
            String message = e.getCause().getMessage();
            if (e.getCause() instanceof JsonMappingException) {
                List<JsonMappingException.Reference> references =
                        ((JsonMappingException) e.getCause()).getPath();

                if (!references.isEmpty()) {
                    JsonMappingException.Reference reference = references.get(0);

                    if (StringUtils.hasText(reference.getFieldName())) {
                        message = "Invalid " + reference.getFieldName();
                    }
                }
            }

            if (e.getCause() instanceof JsonParseException) {
                String[] arr = message.split("at");
                if (arr.length > 0) {
                    String temp = arr[0];
                    JsonParseException jpe = (JsonParseException) e.getCause();
                    message = temp + " [line: " + jpe.getLocation().getLineNr() + ", column: " + jpe.getLocation().getColumnNr() + "]";
                }
            }
            response.setMsg(message);
        }
        return response;
    }

    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleInvalidParameterException(InvalidParameterException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10400");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ResponseBody
    public Response handleAccessDeniedException(AccessDeniedException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10403");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Response handleNotFoundException(NotFoundException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10404");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Response handleHttpNotFoundException(HttpClientErrorException.NotFound e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10404");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public Response handleDuplicateException(DuplicateException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10409");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleInvalidBadRequestException(MissingServletRequestParameterException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10400");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleInvalidTypeMismatchException(TypeMismatchException e) {
        log.error("Error",e);
        Response response = new Response();
        response.setError("10400");
        response.setMsg(e.getMessage());
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response handleException(Exception e) {
        log.error("Error",e);

        Response response = new Response();
        response.setError("10500");
        response.setMsg(e.getMessage());
        return response;
    }
}
