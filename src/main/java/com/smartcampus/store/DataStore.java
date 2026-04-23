package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static final List<Room> ROOMS = new ArrayList<>();
    public static final List<Sensor> SENSORS = new ArrayList<>();

    // Map of sensorId -> list of readings for that sensor
    public static final Map<String, List<SensorReading>> READINGS = new HashMap<>();

    static {
        Room r1 = new Room("CLB-101", "Computing Lab", 45);
        Room r2 = new Room("LIB-202", "Library", 25);
        ROOMS.add(r1);
        ROOMS.add(r2);
        
        Sensor s1 = new Sensor("CO2-101", "CO2", "ACTIVE", 415.0, "CLB-101");
        Sensor s2 = new Sensor("OCC-202", "Occupancy", "ACTIVE", 12.0, "LIB-202");
        
        SENSORS.add(s1);
        SENSORS.add(s2);
        
        r1.getSensorIds().add("CO2-101");
        r2.getSensorIds().add("OCC-202");
        
    }

}