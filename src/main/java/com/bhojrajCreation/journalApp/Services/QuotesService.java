package com.bhojrajCreation.journalApp.Services;

import com.bhojrajCreation.journalApp.ApiResponse.QuoteResponse;
import com.bhojrajCreation.journalApp.Cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class QuotesService {
    @Value("${apiKey.quote}")
    public String apiKey;
    @Autowired
    private AppCache appCache;
    @Autowired
    private RestTemplate restTemplate;


    public List<QuoteResponse> quoteForYou() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", "A2Zqg0I+w4VkIZEWv3PgDw==cqYpwaZZN9jlH5Se");  // Set response type

        // Step 2: Create HttpEntity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<QuoteResponse>> response = restTemplate.exchange(
                appCache.App_Cache.get("quote_api"),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<QuoteResponse>>() {}
        );
        List<QuoteResponse> body = response.getBody();

        return body;
    }

}
