package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherAPI {
    //returns data by coordinates
    public static JSONObject getData(String location){
        JSONArray locationData = getLocationData(location);
        assert locationData != null;

        JSONObject locationInfo = (JSONObject) locationData.get(0);
        double latitude = (double) locationInfo.get("latitude");
        double longitude = (double) locationInfo.get("longitude");
        //url with location data
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        try {
            HttpURLConnection connection = getAPIResponse(url);
            if(connection.getResponseCode() != 200){
                System.out.println("ERROR: Could not establish connection with API");
                return null;
            }else{
                StringBuilder jsonFromUrl = new StringBuilder();
                Scanner scan = new Scanner(connection.getInputStream());
                if(scan.hasNextLine()){
                    jsonFromUrl.append(scan.nextLine());
                }
                scan.close();
                connection.disconnect();
                JSONParser parser = new JSONParser();
                JSONObject result = (JSONObject) parser.parse(jsonFromUrl.toString());
                JSONObject dataByHourly = (JSONObject) result.get("hourly");

                //get the current time index
                JSONArray timeALL = (JSONArray) dataByHourly.get("time");
                int index = findCurrentTimeIdx(timeALL);
                //temperature
                JSONArray temperatureData = (JSONArray) dataByHourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(index);

                //humidity
                JSONArray humidityData = (JSONArray) dataByHourly.get("relative_humidity_2m");
                long humidity = (long) humidityData.get(index);

                //weather code
                JSONArray weatherCodeData = (JSONArray) dataByHourly.get("weather_code");
                String weatherState = convertWeatherCode((Long) weatherCodeData.get(index));

                //wind speed
                JSONArray windSpeedData = (JSONArray) dataByHourly.get("wind_speed_10m");
                double windSpeed = (double) windSpeedData.get(index);

                //object what communicates with the frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_state", weatherState);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windSpeed);

                return weatherData;
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertWeatherCode(long weatherCodeData){
        String weatherState = "";
        if(weatherCodeData == 0L){
            weatherState = "Clear";
        }else if(weatherCodeData <= 3L && weatherCodeData > 0L){
            weatherState = "Cloudy";
        }else if(weatherCodeData >= 51L && weatherCodeData <= 67L || weatherCodeData >= 80L && weatherCodeData <= 99L){
            weatherState = "Rain";
        }else if(weatherCodeData >= 71L && weatherCodeData <= 77L){
            weatherState = "Snow";
        }
        return weatherState;
    }

    public static int findCurrentTimeIdx(JSONArray timeALL){
        LocalDateTime timeNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String timeNowFormatted = timeNow.format(formatter);
        for(int i=0; i< timeALL.size(); i++){
            if(timeALL.get(i).equals(timeNowFormatted)){
                return i;
            }
        }
        return 0;
    }

    public static void showTime(){
        LocalDateTime timeNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String dataFormatted = timeNow.format(formatter);
        System.out.println(dataFormatted);
    }

    //get geographic coordinates for location name
    public static JSONArray getLocationData(String location){
        //api request format is + instead of whitespace
        location = location.replaceAll(" ", "+");
        //api url, location parameter
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + location + "&count=10&language=en&format=json";
        //to make http requests(like api call) we need a http client np HTTPURLConnection
        try{
            HttpURLConnection connection = getAPIResponse(url);
            //HttpOK = 200
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                System.out.println("ERROR: Could not establish connection with API");
                return null;
            }else{
                StringBuilder jsonFromURL = new StringBuilder();
                Scanner scan = new Scanner(connection.getInputStream());
                if(scan.hasNextLine()){
                    jsonFromURL.append(scan.nextLine());
                }
                //string into jsonObj
                JSONParser parser = new JSONParser();
                JSONObject objectJSON = (JSONObject) parser.parse(jsonFromURL.toString());
                //list of locations data from api by name
                JSONArray locationData = (JSONArray) objectJSON.get("results"); //results is the first word of the api call
                return locationData;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static HttpURLConnection getAPIResponse(String urlReal) {
        HttpURLConnection resultConnection = null;
        try {
            //create connection
            URL url = new URL(urlReal);
            resultConnection = (HttpURLConnection) url.openConnection();
            resultConnection.setRequestMethod("GET");
            resultConnection.connect();
            return resultConnection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }
