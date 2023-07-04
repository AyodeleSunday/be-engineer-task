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
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateException extends RuntimeException {

    private static final long serialVersionUID = -5919447122348400126L;
    private String code = "10409";

    /**
     * Creates a new instance of
     * <code>CmsServiceException</code> without detail message.
     */
    public DuplicateException() {
    }

    /**
     * Constructs an instance of
     * <code>CmsServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DuplicateException(String msg) {
        super(msg);
    }

    public DuplicateException(String code, String msg) {
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
