package com.vaddi.hemanth.myhome;

/**
 * Created by Dell on 27-01-2016.
 */
public class LEDProperties {
    int brightness;
    boolean status;

    public LEDProperties(){

    }
    public LEDProperties(int brightness, boolean status) {
        this.status = status;
        this.brightness = brightness;
    }
    public int getBrightness() {
        return brightness;
    }
    public boolean getStatus() {
        return status;
    }
    @Override
    public String toString() {
        return "LEDProperties{" +
                "brightness=" + brightness +
                ", status=" + status +
                '}';
    }
}
