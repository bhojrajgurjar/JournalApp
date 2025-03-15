package com.bhojrajCreation.journalApp.Services;

import com.bhojrajCreation.journalApp.ApiResponse.WeatherResponse;
import com.bhojrajCreation.journalApp.Cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
    @Value("${apiKey.weather}")
    public String apiKey;


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = redisService.get(city, WeatherResponse.class);
        if(weatherResponse!=null){
            return weatherResponse;
        }else{
            String finalApi = appCache.App_Cache.get("weather_api").replace("<city>", city).replace("<apiKey>", apiKey);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse body=response.getBody();
            if(body!=null){
                redisService.set(city,body,300l);
            }
            return body;
        }

    }
}
