package com.engineer.model;

import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */
@Data
public class CountryDetails {
    private long population;
    private String capitalCity;
    private String currency;
    private int longitude;
    private int latitude;
    private String iso3;
    private String iso2;
}
