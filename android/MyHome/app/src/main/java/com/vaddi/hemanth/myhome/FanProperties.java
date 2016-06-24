package com.vaddi.hemanth.myhome;

/**
 * Created by Dell on 08-03-2016.
 */
public class FanProperties {
    int speed;
    boolean status;

    public FanProperties(){

    }
    public FanProperties(int speed, boolean status) {
        this.speed = speed;
        this.status = status;
    }
    public int getSpeed() {
        return speed;
    }

    public boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "FanProperties{" +
                "speed=" + speed +
                ", status=" + status +
                '}';
    }
}
