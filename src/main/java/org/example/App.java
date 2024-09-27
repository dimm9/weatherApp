package org.example;

import javax.swing.*;
import java.sql.SQLOutput;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WeatherFrame appFrame = new WeatherFrame();
                System.out.println(WeatherAPI.getLocationData("Berlin"));
                WeatherAPI.showTime();
            }
        });
    }
}