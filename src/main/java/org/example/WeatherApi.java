package org.example;

import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApi {

    private final String zipCode;
    private final String api_url;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    private static final String API_KEY = System.getenv("WEATHER_API_KEY");
    private static final RedisClient jedis = RedisClient.builder()
            .hostAndPort("localhost", 6379)
            .build();

    public WeatherApi(String zipCode){
        this.zipCode = zipCode;

        if (API_KEY == null){
            throw new IllegalStateException("API_KEY is not set");
        }

        this.api_url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                this.zipCode + "?key=" + API_KEY + "&include=days&elements=datetime,tempmax,tempmin,temp,conditions";
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private HttpResponse<String> sendRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api_url))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Weather fetchWeatherData() throws IOException, InterruptedException {
        String cacheKey = "weather:" + zipCode;
        String cachedJson = jedis.get(cacheKey);

        if(cachedJson != null){
            return objectMapper.readValue(cachedJson, Weather.class);
        }

        HttpResponse<String> response = sendRequest();
        if (response.statusCode() != 200) {
            throw new AddressNotFoundException("Address is not found");
        }

        jedis.set(cacheKey, response.body(), SetParams.setParams().ex(3600));
        return objectMapper.readValue(response.body(), Weather.class);
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
