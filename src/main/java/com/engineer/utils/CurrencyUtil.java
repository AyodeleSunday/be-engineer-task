package com.engineer.utils;

import com.engineer.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @Author Sunday Ayodele
 * @create 7/3/2023
 */

@Component
public class CurrencyUtil {



    private Double[][] currencyRates;
    private List<String> currencyCodes=new ArrayList<>();
    @Autowired
    public CurrencyUtil(@Value("${currency.file}") String fileName) throws Exception{
        //I want to do all the heavy lifting at start up

        //get all unique currency codes so I can initialize the matrix accurately
        //Alternative would have been to use arbitrarily large size

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();// to skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",",3);
                if(values.length!=3) continue;

                String currCode= StringUtils.capitalize(values[0]);
                if(!currencyCodes.contains(currCode)) currencyCodes.add(currCode);
                currCode= StringUtils.capitalize(values[1]);
                if(!currencyCodes.contains(currCode)) currencyCodes.add(currCode);

            }
        }
        currencyRates=new Double[currencyCodes.size()][currencyCodes.size()];
        //Now I have to read the file again. Sigh...

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",",3);
                if(values.length!=3) continue;

                String srcCurr= StringUtils.capitalize(values[0]);
                String targetCurr= StringUtils.capitalize(values[1]);
                BigDecimal rate=this.parse(values[2],Locale.US);

                //I can assume the currency codes are all present in currency code list
                currencyRates[currencyCodes.indexOf(srcCurr)][currencyCodes.indexOf(targetCurr)]=rate.doubleValue();
                currencyRates[currencyCodes.indexOf(targetCurr)][currencyCodes.indexOf(srcCurr)]=1/rate.doubleValue();

            }
        }
        //I will be filling up the array on demand
    }

    public BigDecimal parse(String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
    }

    public String convertAmount(String sourceCurrency, String targetCurrency, String amountStr) throws ParseException {

        if(sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            return amountStr;
        }
        BigDecimal amount=this.parse(amountStr,Locale.US);
        int sourceInd=currencyCodes.indexOf(StringUtils.capitalize(sourceCurrency));
        int targetInd=currencyCodes.indexOf(StringUtils.capitalize(targetCurrency));

        if(sourceInd ==-1 ||targetInd==-1) throw new NotFoundException("Source to Target currency conversion not available");
        Double rate=currencyRates[sourceInd][targetInd];
        //We need to compute values if selection is null
        if(rate==null){
            rate=fillRates(sourceInd, targetInd);
        }
        if(rate==null) throw new NotFoundException("Source to Target currency conversion could not be computed");

        Double converted=amount.doubleValue()*rate;
        BigDecimal value=BigDecimal.valueOf(converted);

        NumberFormat valueFormat = NumberFormat.getCurrencyInstance(Locale.US);
        valueFormat.setMinimumFractionDigits( 2 );
        valueFormat.setMaximumFractionDigits( 2 );
        return valueFormat.format(value.doubleValue());
    }
    public Double fillRates(int sourceInd, int targetInd){
        Double conversionRate=null;
        for(int i=0;i<currencyCodes.size();i++){
            Double sourceRef=currencyRates[sourceInd][i];
            Double targetRef=currencyRates[targetInd][i];

            if(sourceRef!=null && targetRef!=null){
                conversionRate=sourceRef/targetRef;
                currencyRates[sourceInd][targetInd]=conversionRate;
                currencyRates[targetInd][sourceInd]=targetRef/sourceRef;

                break;
            }
        }
        return conversionRate;
    }
}
