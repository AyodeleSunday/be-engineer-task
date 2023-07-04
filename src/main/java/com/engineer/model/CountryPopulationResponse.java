package com.engineer.model;

import lombok.Data;

import java.util.List;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class CountryPopulationResponse {
    private String country;
    private String code;
    private String iso3;
    private List<CountryPopulation> populationCounts;
}
