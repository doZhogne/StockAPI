package com.bnsdemo.stock.stockservice.resource;

import com.bnsdemo.stock.stockservice.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/stock")
public class StockResource {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/{username}")
    public List<Quote> getStock(@PathVariable("username") final String userName){

        //
        // Use the url for the db service from eureka so it can connect.
        //
        // 8Q7WME7M85DOJQY0
        //
        ResponseEntity<List<String>> quoteResponse = restTemplate.exchange("http://db-service/rest/db/" + userName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {});

        List<String> quotes = quoteResponse.getBody();
        return quotes
                .stream()
                .map(quote -> {
                    Stock stock = getStockPrice(quote);
                    return new Quote(quote, stock.getQuote().getPrice());
                })
                .collect(Collectors.toList());

    }

    private Stock getStockPrice(String quote) {
        try {
            return YahooFinance.get(quote);
        }catch (IOException e){
            e.printStackTrace();
            return new Stock(quote);
        }
    }
}
