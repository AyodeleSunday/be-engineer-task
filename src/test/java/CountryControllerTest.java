import com.engineer.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @Author Sunday Ayodele
 * @create 7/4/2023
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CountryControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetTopCities() throws Exception{
        mvc.perform(get("/top-cities?numOfCities=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.data").isArray());

    }
    @Test
    public void testCountryDetails() throws Exception{

        //this should return 200
        mvc.perform(get("/country-details?country=United Kingdom"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.data.currency").value("GBP"));
    }

    @Test
    public void testCountryDetailsNotFound() throws Exception{

        mvc.perform(get("/country-details?country=UK"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.error").value("10404"));

    }

    @Test
    public void testCountryStatesAndCities() throws Exception{

        //this should return 200
        mvc.perform(get("/get-states-and-cities?country=Nigeria"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.data.states").isArray());
    }

    @Test
    public void testStatesAndCitiesNotFound() throws Exception{

        mvc.perform(get("/get-states-and-cities?country=UK"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.error").value("10404"));

    }
    @Test
    public void testConvertCurrency() throws Exception{

        //this should return 200
        mvc.perform(get("/convert-currency?country=United Kingdom&amount=100.25&targetCurrency=USD"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.data.countryCurrency").value("GBP"));
    }

    @Test
    public void testConvertCurrencyBadRequest() throws Exception{

        mvc.perform(get("/convert-currency?country=United Kingdom&amount=1000.25&targetCurrency=USD"))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.error").value("10400"));

    }

    @Test
    public void testCurrencyPairNotFOund() throws Exception{

        mvc.perform(get("/convert-currency?country=Senegal&amount=100.25&targetCurrency=USD"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.error").value("10404"));

    }



}

