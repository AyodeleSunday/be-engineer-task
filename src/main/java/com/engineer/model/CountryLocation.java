package com.engineer.model;

import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class CountryLocation {
    private String name;
    private String iso2;
    private int longitude;
    private int latitude;
}
