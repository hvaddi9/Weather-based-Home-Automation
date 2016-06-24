package com.vaddi.hemanth.myhome;

/**
 * Created by Dell on 08-03-2016.
 */
public class HomeStatusProperties {
    String lastUpdate;
    String lightIntensity;
    String temperature;
    String weather;

    public HomeStatusProperties(){
    }

    public HomeStatusProperties(String lastUpdate, String lightIntensity, String temperature, String weather) {
        this.lastUpdate = lastUpdate;
        this.lightIntensity = lightIntensity;
        this.temperature = temperature;
        this.weather = weather;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getLightIntensity() {
        return lightIntensity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeather() {
        return weather;
    }
}
