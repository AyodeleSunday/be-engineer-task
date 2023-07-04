package com.engineer.service;

import com.engineer.exception.NotFoundException;
import com.engineer.model.*;
import com.engineer.utils.CurrencyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author Sunday Ayodele
 * @create 7/3/2023
 */

@Component
@Slf4j
public class CountryService {

    private RestTemplate restTemplate;
    private CurrencyUtil currencyUtil;
    private String baseUrl;
    @Autowired
    public CountryService(RestTemplate restTemplate, CurrencyUtil currencyUtil, @Value("${base.url}") String baseUrl){
        this.restTemplate=restTemplate;
        this.currencyUtil=currencyUtil;
        this.baseUrl=baseUrl;
    }

    public Response getTopCities(int limit) throws Exception{

        CompletableFuture<Response<List<TopCitiesResponse>>> italyCities=getTopCities("Italy", limit);
        CompletableFuture<Response<List<TopCitiesResponse>>> nzCities=getTopCities("New Zealand", limit);
        CompletableFuture<Response<List<TopCitiesResponse>>> ghanaCities=getTopCities("Ghana", limit);

        List<TopCitiesResponse> topCities=new ArrayList<>(italyCities.get().getData().stream().collect(Collectors.toList()));
        topCities.addAll(nzCities.get().getData().stream().collect(Collectors.toList()));
        topCities.addAll(ghanaCities.get().getData().stream().collect(Collectors.toList()));

        topCities=topCities.stream().sorted(Comparator.comparing(x->x.getPopulationCounts().get(0).getValue())).collect(Collectors.toList());
        Collections.reverse(topCities);

        Response response=new Response();
        response.setError("00");
        response.setMsg("Success");

        if(topCities.size()>limit)  response.setData(topCities.subList(0,limit));

        else response.setData(topCities);
        return response;
    }

    public Response getCountryDetails(String country) throws Exception{
        //call get population endpoint to ge population value
        //Also use this to validate that the country exists
        CountryPopulationResponse population=getCountryPopulation(country).get();
        if(population==null) throw new NotFoundException("Country could not be found");
        //call get location to get location
        CompletableFuture<CountryLocation> location=getCountryLocation(country);
        //call get currency to get currency and ISO codes
        CompletableFuture<CurrencyResponse> currency=getCountryCurrency(country);
        //get capital city
        CompletableFuture<CapitalCityResponse> capitalCity=getCountryCapital(country);

        CountryDetails details=new CountryDetails();
        try{
            List<CountryPopulation> popCounts=population.getPopulationCounts();
            CountryPopulation countryPopulation=popCounts.stream().sorted(Comparator.comparing(x->x.getYear())).collect(Collectors.toList()).get(popCounts.size()-1);
            details.setPopulation(countryPopulation.getValue());

            CountryLocation countryLocation=location.get();
            details.setLatitude(countryLocation.getLatitude());
            details.setLongitude(countryLocation.getLongitude());

            CurrencyResponse currencyResponse= currency.get();
            details.setCurrency(currencyResponse.getCurrency());
            details.setIso2(currencyResponse.getIso2());
            details.setIso3(currencyResponse.getIso3());

            CapitalCityResponse capitalCityResponse= capitalCity.get();
            details.setCapitalCity(capitalCityResponse.getCapital());
        }
        catch (Exception ex){
            log.error("Error", ex);
            throw new Exception(ex.getMessage());
        }

        Response response=new Response();
        response.setData(details);
        response.setError("00");
        response.setMsg("Success");
        return response;
    }

    public Response getStateAndCities(String country) throws Exception{
        //Validate that country exist
        CountryPopulationResponse population=getCountryPopulation(country).get();
        if(population==null) throw new NotFoundException("Country could not be found");

        //Get all states and get cities in each - I would have preferred making the cities available when a state is sent
        CountryStatesAndCities statesAndCities=new CountryStatesAndCities();
        statesAndCities.setName(country);
        List<StateCities> stateCities= this.getCountryStates(country).get().parallelStream().map(x->{

            try{
                return new StateCities(x.getName(),getStateCities(x.getName(),country).get());
            }
            catch (Exception ex){
                log.error("Error while processing state {}",ex,x.getName());
            }
            return null;
        }).collect(Collectors.toList());
        statesAndCities.setStates(stateCities);

        Response response=new Response();
        response.setMsg("Success");
        response.setError("00");
        response.setData(statesAndCities);

        return response;
    }
    public Response convertCurrency(String amount, String targetCurrency, String country) throws Exception{
        CurrencyResponse currencyResponse=this.getCountryCurrency(country).get();
        if(currencyResponse==null) throw new NotFoundException("Country currency could not be found");
        String converted=currencyUtil.convertAmount(currencyResponse.getCurrency(),targetCurrency,amount);
        ConvertCurrencyResponse respData=new ConvertCurrencyResponse();
        respData.setAmount(converted.substring(1));
        respData.setCountryCurrency(currencyResponse.getCurrency());

        Response response=new Response();
        response.setMsg("Success");
        response.setError("00");
        response.setData(respData);

        return response;
    }



    @Async
    public CompletableFuture<Response<List<TopCitiesResponse>>> getTopCities(String country, int limit) {


        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/population/cities/filter/q")
                .queryParam("limit", limit)
                .queryParam("orderBy","population").queryParam("order","dsc").toUriString();

        url=url+"&country="+country;
        Response<List<TopCitiesResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<List<TopCitiesResponse>>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response);
    }


    @Async
    public CompletableFuture<CountryPopulationResponse> getCountryPopulation(String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/population/q").toUriString();

        url=url+"?country="+country;
        Response<CountryPopulationResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<CountryPopulationResponse>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response.getData());
    }

    @Async
    public CompletableFuture<CountryLocation> getCountryLocation(String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/positions/q").toUriString();

        url=url+"?country="+country;
        Response<Map<String,String>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<Map<String,String>>>() {
        }).getBody();
        CountryLocation location=null;

        if(response.getError().equalsIgnoreCase("false")){
            location=new CountryLocation();
            Map<String, String> resp=response.getData();
            location.setName(resp.get("name"));
            location.setIso2(resp.get("iso2"));
            location.setLatitude(Integer.parseInt(resp.get("lat")));
            location.setLongitude(Integer.parseInt(resp.get("long")));
        }
        return CompletableFuture.completedFuture(location);
    }

    @Async
    public CompletableFuture<CurrencyResponse> getCountryCurrency(String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/currency/q").toUriString();

        url=url+"?country="+country;
        Response<CurrencyResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<CurrencyResponse>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response.getData());
    }

    @Async
    public CompletableFuture<CapitalCityResponse> getCountryCapital(String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/capital/q").toUriString();

        url=url+"?country="+country;
        Response<CapitalCityResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<CapitalCityResponse>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response.getData());
    }

    @Async
    public CompletableFuture<List<State>> getCountryStates(String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/states/q").toUriString();

        url=url+"?country="+country;
        Response<CountryStates> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<CountryStates>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response.getData().getStates());
    }

    @Async
    public CompletableFuture<List<String>> getStateCities(String state, String country) {

        String url= UriComponentsBuilder.newInstance().scheme("https").host(baseUrl).path("/api/v0.1/countries/state/cities/q").toUriString();

        url=url+"?country="+country+"&state="+state;
        Response<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Response<List<String>>>() {
        }).getBody();

        return CompletableFuture.completedFuture(response.getData());
    }

}
