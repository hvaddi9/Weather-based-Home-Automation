package com.vaddi.hemanth.myhome;

/**
 * Created by Dell on 13-04-2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SettingsProperties {
    private boolean autoMode;
    private String email;
    private String name;
    private String password;
    private boolean temperatureUnit;

    public SettingsProperties() {
    }

    public SettingsProperties(boolean autoMode, String email, String name, String password, boolean temperatureUnit) {
        this.autoMode = autoMode;
        this.email = email;
        this.name = name;
        this.password = password;
        this.temperatureUnit = temperatureUnit;
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public String getEmail() {
        return email;
    }

    public boolean getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SettingsProperties{" +
                "autoMode=" + autoMode +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", temperatureUnit=" + temperatureUnit +
                '}';
    }
}
