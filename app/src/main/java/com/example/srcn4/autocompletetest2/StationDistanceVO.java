package com.example.srcn4.autocompletetest2;

public class StationDistanceVO extends StationVO {

    private double distance;

    // 駅名と距離を使うコンストラクタ
    public StationDistanceVO (String name, double distance) {

        super(name);
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "StationDistanceVO{" +
                "name='" + getName() + '\'' +
                ", distance=" + distance +
                '}';
    }
}
