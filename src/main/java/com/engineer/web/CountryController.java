package com.engineer.web;

import com.engineer.exception.InvalidParameterException;
import com.engineer.model.Response;
import com.engineer.service.CountryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Sunday Ayodele
 * @create 7/2/2023
 */

@RestController
public class CountryController {

    @Autowired
    CountryService countryService;

    @ApiOperation(value = "This method returns top most populated states in Ghana, New Zealand and Italy")
    @GetMapping(path = "top-cities")
    public Response getTopCities(@RequestParam int numOfCities) throws Exception{
        return countryService.getTopCities(numOfCities);
    }

    @ApiOperation(value = "This API method returns some details about a country")
    @GetMapping(path = "country-details")
    public Response getCountryDetails(@RequestParam String country) throws Exception{
        return countryService.getCountryDetails(country);
    }

    @ApiOperation(value = "This API method returns all the states and cities within each state in a country")
    @GetMapping(path = "get-states-and-cities")
    public Response getStatesAndCities(@RequestParam String country) throws Exception{
        return countryService.getStateAndCities(country);
    }

    @ApiOperation(value = "This API method converts an amount in the countries currency to the provided target currency")
    @GetMapping(path = "convert-currency")
    public Response convertCurrency(@RequestParam String country, @RequestParam String amount, @RequestParam String targetCurrency) throws Exception{
        if(!amount.matches("^-?(\\d{1,3}\\s*?([.,]|$|\\s)\\s*?)+$")){
            throw new InvalidParameterException("Amount not in correct format");
        }
        if(!targetCurrency.matches("^[a-zA-Z]{3}$")) throw new InvalidParameterException("Target currency not in correct format");

        return countryService.convertCurrency(amount,targetCurrency,country);
    }
}
