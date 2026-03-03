package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Weather(String resolvedAddress, List<Day> days) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Day(String datetime, double tempmax, double tempmin, double temp, String conditions){

        public Day{
            tempmax = convertToCelsius(tempmax);
            tempmin = convertToCelsius(tempmin);
            temp = convertToCelsius(temp);
        }

        private static double convertToCelsius(double f){
            return Math.round((f - 32) * 5 / 9);
        }
    }
}
