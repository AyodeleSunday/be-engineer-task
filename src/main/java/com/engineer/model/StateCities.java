package com.engineer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
@AllArgsConstructor
public class StateCities {
    private String stateName;
    private List<String> cities;
}
