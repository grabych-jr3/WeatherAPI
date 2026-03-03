package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static void main() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter zip-code or name of your city: ");
        String zipCode = scanner.nextLine();

        WeatherApi weatherApi = new WeatherApi(zipCode);
        weatherApi.displayWeather();
    }
}
