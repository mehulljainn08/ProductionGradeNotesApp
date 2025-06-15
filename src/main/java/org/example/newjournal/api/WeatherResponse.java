package org.example.newjournal.api;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WeatherResponse {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

private Current current;
@Data
public class Current{
   
    public int temperature;
    
    @JsonProperty("weather_descriptions")
    public ArrayList<String> weatherDescriptions;
    public int feelslike;
}




}
