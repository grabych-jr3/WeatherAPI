package org.example;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class WeatherApi {

    private String zipCode;
    private final String API_KEY = System.getenv("WEATHER_API_KEY");;
    private String api_url;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public WeatherApi(String zipCode){
        this.zipCode = zipCode;
        this.api_url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                this.zipCode + "?key=" + API_KEY + "&include=days&elements=datetime,tempmax,tempmin,temp,conditions";
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public HttpResponse<String> sendRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api_url))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Weather fetchWeatherData() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest();

        if(response.statusCode() != 200){
            throw new AddressNotFoundException(zipCode + " address does not exist!");
        }

        return objectMapper.readValue(response.body(), new TypeReference<Weather>() {});
    }

    public void displayWeather() throws IOException, InterruptedException {
        Weather weather = fetchWeatherData();

        System.out.println("Address: " + weather.resolvedAddress() + "\n");
        for(Weather.Day d : weather.days()){
            System.out.println("Date: " + d.datetime() +
                    "\nMax temperature: " + d.tempmax() +
                    "\nMin temperature: " + d.tempmin() +
                    "\nAverage temperature: " + d.temp() +
                    "\nConditions: " + d.conditions());
            System.out.println("------------------------\n");
        }
    }
}
