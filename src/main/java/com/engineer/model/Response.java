package com.engineer.model;

import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class Response<T> {
    private String error;
    private String msg;
    private T data;
}
