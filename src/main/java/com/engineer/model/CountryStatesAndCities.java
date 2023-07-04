package com.engineer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class CountryStatesAndCities {
    private String name;
    private List<StateCities> states;
}
