import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherForecast {
    private static String API_KEY = "4fb1829475d68b06bfd95d8ae9f138b4"; // Primary API key
    private static final String BACKUP_API_KEY = "9cac7fed4068cbe8d2862b53574c445f"; // Backup API key
    private static boolean useBackupKey = false;

    public static List<JSONObject> get5DayForecast(String locationName) {
        JSONArray locationData = fetchLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("No location data found.");
            return null;
        }

        JSONObject firstLocation = (JSONObject) locationData.get(0);
        double latitude = (double) firstLocation.get("latitude");
        double longitude = (double) firstLocation.get("longitude");

        String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude +
                "&lon=" + longitude + "&appid=" + (useBackupKey ? BACKUP_API_KEY : API_KEY) + "&units=metric";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                if (!useBackupKey) {
                    System.out.println("Primary API key failed, trying backup...");
                    useBackupKey = true;
                    return get5DayForecast(locationName); // Retry with backup key
                }
                System.out.println("Failed to fetch forecast data with both keys.");
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
            JSONObject jsonObject = (JSONObject) parser.parse(resultJson.toString());

            JSONArray forecastArray = (JSONArray) jsonObject.get("list");
            List<JSONObject> forecastList = new ArrayList<>();

            for (Object obj : forecastArray) {
                JSONObject forecast = (JSONObject) obj;
                JSONObject main = (JSONObject) forecast.get("main");
                JSONArray weatherArray = (JSONArray) forecast.get("weather");
                JSONObject weather = (JSONObject) weatherArray.get(0);

                JSONObject processedForecast = new JSONObject();
                processedForecast.put("date", forecast.get("dt_txt"));
                processedForecast.put("temperature", main.get("temp"));
                processedForecast.put("weather_condition", weather.get("description"));

                forecastList.add(processedForecast);
            }

            return forecastList;
        } catch (Exception e) {
            if (!useBackupKey) {
                System.out.println("Error with primary API key, trying backup...");
                useBackupKey = true;
                return get5DayForecast(locationName); // Retry with backup key
            }
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray fetchLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=5&language=en&format=json";

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
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}