package com.engineer.model;

import lombok.Data;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@Data
public class TopCitiesRequest {
    private int limit;
    private String order;
    private String orderBy;
    private String country;
}
