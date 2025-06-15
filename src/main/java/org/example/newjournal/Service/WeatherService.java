package org.example.newjournal.Service;

import org.example.newjournal.api.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private static final String API_KEY ="e0c91fd356acf5f298c4390c2a84c54e";
    private static final String BASE_URL = "http://api.weatherstack.com/current?access_key=API_KEY&query=";

    @Autowired
    private RedisService redisService;
    @Autowired
    private RestTemplate restTemplate;
    public ResponseEntity<?> getWeather(String city) {
        System.out.println("City received: " + city);
        WeatherResponse weatherResponse = redisService.get("weather_of_" + city, WeatherResponse.class);
        if (weatherResponse != null) {
            System.out.println("Returning cached weather data for city: " + city);
            return ResponseEntity.ok(weatherResponse);
            
        }

        String finalUrl = BASE_URL.replace("API_KEY", API_KEY) + city;
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET, null, WeatherResponse.class);
        if (response.getBody() != null) {
            redisService.set("weather_of_" + city, response.getBody(), 300L);
        }
        System.out.println("Fetched new weather data for city: " + city);
        return ResponseEntity.ok(response.getBody());
    }
}
