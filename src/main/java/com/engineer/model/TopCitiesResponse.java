package com.engineer.model;

import lombok.Data;

import java.util.List;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class TopCitiesResponse {
    private String city;
    private String country;
    private List<CityPopulation> populationCounts;
}
