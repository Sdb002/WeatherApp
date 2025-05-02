import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherApp {
    private static final String[] API_KEYS = {
            "4fb1829475d68b06bfd95d8ae9f138b4", // Primary key
            "9cac7fed4068cbe8d2862b53574c445f"  // Backup key
    };

    private static int currentApiKeyIndex = 0;

    public static JSONObject getWeatherData(String locationName) {
        System.out.println("Using API key: " + getCurrentApiKey()); //  print statement

        JSONArray locationData = fetchLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("No location data found.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");


        String urlString = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat=" + latitude + "&lon=" + longitude +
                "&appid=" + getCurrentApiKey() + "&units=metric";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error with key " + getCurrentApiKey() + ": " + conn.getResponseCode());
                if (conn.getResponseCode() == 401 || conn.getResponseCode() == 429)
                {
                    rotateApiKey();
                    return getWeatherData(locationName); // Retry with new key
                }
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

            JSONObject main = (JSONObject) resultJsonObj.get("main");

            JSONArray weather = (JSONArray) resultJsonObj.get("weather");

            JSONObject wind = (JSONObject) resultJsonObj.get("wind");

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", main.get("temp"));
            weatherData.put("humidity", main.get("humidity"));

            if (weather != null && !weather.isEmpty()) {

                JSONObject weatherInfo = (JSONObject) weather.get(0);

                weatherData.put("weather_condition", weatherInfo.get("main"));
                weatherData.put("weather_description", weatherInfo.get("description"));
            }

            if (wind != null) {
                double windSpeed = wind.get("speed") != null ?
                        ((Number) wind.get("speed")).doubleValue() : 0;
                weatherData.put("wind_speed", (int) Math.round(windSpeed * 3.6));
            } else {
                weatherData.put("wind_speed", 0);
            }

            return weatherData;
        } catch (Exception e) {

            System.err.println("Error with API key " + getCurrentApiKey() + ": " + e.getMessage());
            e.printStackTrace();
            // Try rotating key on any error
            rotateApiKey();
            return getWeatherData(locationName);
        }
    }

    private static String getCurrentApiKey() {
        return API_KEYS[currentApiKeyIndex];
    }

    private static void rotateApiKey() {
        currentApiKeyIndex = (currentApiKeyIndex + 1) % API_KEYS.length;
        System.out.println("Rotated to API key: " + getCurrentApiKey());
    }

    public static List<String> getLocationSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        JSONArray locationData = fetchLocationData(query);
        if (locationData != null) {
            for (Object obj : locationData) {
                JSONObject location = (JSONObject) obj;
                String name = (String) location.get("name");
                String country = (String) location.get("country");
                suggestions.add(name + ", " + country);
            }
        }
        return suggestions;
    }

    private static JSONArray fetchLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=5&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to Geolocation API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());
            return (JSONArray) resultsJsonObj.get("results");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}