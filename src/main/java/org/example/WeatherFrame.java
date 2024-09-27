package org.example;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class WeatherFrame extends JFrame{

    private JSONObject weatherData;
    public static int WIDTH = 500, HEIGHT = 600;
    private ImageIcon cloud = new ImageIcon("src/main/java/img/cloud_not_rain.png");
    private ImageIcon sun = new ImageIcon("src/main/java/img/sun.png");
    private ImageIcon rain = new ImageIcon("src/main/java/img/cloud.png");
    private ImageIcon snow = new ImageIcon("src/main/java/img/snow.png");
    private ImageIcon wind = new ImageIcon("src/main/java/img/wind.png");
    JLabel mainIcon;

    public WeatherFrame() {
        super("weather app");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setResizable(false);
        addComponent();
        setVisible(true);
    }
    private void addComponent(){
        JTextField search = new JTextField();
        search.setFont(new Font("Sherif", Font.ITALIC, 16));
        search.setBounds(40, 15, 350, 30);
        add(search);

        mainIcon = new JLabel(sun);
        mainIcon.setBounds(140, 140, 180, 180);
        add(mainIcon);

        JLabel temperatureText = new JLabel("5 C");
        temperatureText.setFont(new Font("Arial", Font.BOLD, 18));
        temperatureText.setBounds(130, 90, 60, 40);
        add(temperatureText);

        JLabel humidity = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidity.setFont(new Font("Sherif", Font.BOLD, 16));
        humidity.setBounds(350, 400, 100, 100);
        add(humidity);

        JLabel humidityIcon = new JLabel(new ImageIcon("src/main/java/img/drop.png"));
        humidityIcon.setBounds(290, 420, 50, 50);
        add(humidityIcon);

        JLabel windspeed = new JLabel("<html><b>Wind speed</b> 100m/s</html>");
        windspeed.setFont(new Font("Sherif", Font.BOLD, 16));
        windspeed.setBounds(100, 400, 100, 100);
        add(windspeed);

        JLabel windspeedIcon = new JLabel(wind);
        windspeedIcon.setBounds(40, 420, 50, 50);
        add(windspeedIcon);

        JButton searchButton = new JButton(new ImageIcon("src/main/java/img/icon.png"));
        searchButton.setBounds(400, 15, 30, 30);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from the search field
                String userInput = search.getText();
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }
                weatherData = WeatherAPI.getData(userInput);
                assert weatherData != null;
                String weatherCondition = (String) weatherData.get("weather_state");
                if(Objects.equals(weatherCondition, "Clear")){
                    mainIcon.setIcon(sun);
                }else if(Objects.equals(weatherCondition, "Rain")){
                    mainIcon.setIcon(rain);
                }else if(Objects.equals(weatherCondition, "Cloudy")){
                    mainIcon.setIcon(cloud);
                }else if(Objects.equals(weatherCondition, "Snow")){
                    mainIcon.setIcon(snow);
                }
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                long humidityText = (long) weatherData.get("humidity");
                humidity.setText("<html><b>Humidity</b> " + humidityText + "%</html>");

                double windspeedText = (double) weatherData.get("windspeed");
                windspeed.setText("<html><b>Wind speed</b> "+ windspeedText + "m/s</html>");
            }
        });
        add(searchButton);
    }


}
