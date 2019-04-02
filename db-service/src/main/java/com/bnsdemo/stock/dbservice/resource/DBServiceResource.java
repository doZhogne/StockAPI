package com.bnsdemo.stock.dbservice.resource;

import com.bnsdemo.stock.dbservice.model.Quote;
import com.bnsdemo.stock.dbservice.model.Quotes;
import com.bnsdemo.stock.dbservice.repository.QuotesRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/db")
public class DBServiceResource {

    private QuotesRepository quotesRepository;

    public DBServiceResource(QuotesRepository quotesRepository) {
        this.quotesRepository = quotesRepository;
    }

    @GetMapping("/{username}")
    public List<String> getQuotes(@PathVariable("username")
                                  final String username){

        return getQuotesByUserName(username);
    }

    @PostMapping("/delete/{username}")
    public List<String> delete(@PathVariable("username") final String username){

        List<Quote> quotes = quotesRepository.findByUserName(username);
        //quotesRepository.delete(quotes);
        quotes.forEach( quote -> quotesRepository.delete(quote));

        return getQuotesByUserName(username); //empty response
    }

    @PostMapping("/add")
    public List<String> add(@RequestBody final Quotes quotes){

        quotes.getQuotes().stream()
                .map(quote -> new Quote(quotes.getUserName(), quote))
                .forEach( quote ->
                    quotesRepository.save(quote)
                );

        return  getQuotesByUserName(quotes.getUserName());
    }


    private List<String> getQuotesByUserName(@PathVariable("username") String username) {
        return quotesRepository.findByUserName(username)
                .stream()
                .map(Quote::getQuote)
                .collect(Collectors.toList());
    }
}
