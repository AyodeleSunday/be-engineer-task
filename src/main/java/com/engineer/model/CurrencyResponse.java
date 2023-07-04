package com.engineer.model;

import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class CurrencyResponse {
    private String name;
    private String currency;
    private String iso2;
    private String iso3;
}
