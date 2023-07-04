/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engineer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Sunday.Ayodele
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {

    private static final long serialVersionUID = -5919447122348400126L;
    private String code = "10400";

    /**
     * Creates a new instance of
     * <code>InvalidParameterException</code> without detail message.
     */
    public InvalidParameterException() {
    }

    /**
     * Constructs an instance of
     * <code>InvalidParameterException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidParameterException(String msg) {
        super(msg);
    }

    public InvalidParameterException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
}
