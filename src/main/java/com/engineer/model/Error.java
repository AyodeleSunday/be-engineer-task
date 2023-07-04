package com.engineer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
@AllArgsConstructor
public class Error {

    private String fieldName;
    private String message;
}
