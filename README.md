# Weather API

[**Weather API**](https://roadmap.sh/projects/weather-api-wrapper-service) - is a small application that fetches weather data from 3rd party weather API.

# :hammer_and_wrench: Technologies
+ Java 25
+ Maven
+ Redis
+ Jackson
+ [Visual Crossing Weather API](https://www.visualcrossing.com/weather-api/)

# :fire_engine: Quick setup:
```bash
# Clone repository
git clone https://github.com/grabych-jr3/WeatherAPI.git
```
### Environment variable
Don't forget to set the following environment variable: WEATHER_API_KEY=<your_api_key>

### Redis
**If you haven't installed Redis yet, check official documentation:**
[Redis documentation](https://redis.io/docs/latest/get-started/)

# :closed_lock_with_key: How it works?
```java
String cacheKey = "weather:" + zipCode;
String cachedJson = null;

try{
    cachedJson = jedis.get(cacheKey);
}catch (JedisConnectionException e){
    log.error("Redis connection failed during get");
}

if(cachedJson != null){
    return objectMapper.readValue(cachedJson, Weather.class);
}

HttpResponse<String> response = sendRequest();
if (response.statusCode() != 200) {
        throw new AddressNotFoundException("Address is not found");
}

try{
    jedis.set(cacheKey, response.body(), SetParams.setParams().ex(3600));
}catch (JedisConnectionException e){
    log.error("Redis connection failed during set");
}

return objectMapper.readValue(response.body(), Weather.class);
```
Redis stores data in key-value format. The application first checks whether we have cached weather by the key `weather:{zipCode}`.
If cached data is found, it is deserialized and returnes as a `Weather` object.
If no cached value exists, the application sends a request to the external weather API, retrieves the data, stores the response in Redis with TTL, and then returns the parsed `Weather` object.
