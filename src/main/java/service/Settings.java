package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static Settings instance;
    private final Properties properties;

    private Settings() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("settings.properties"));
        } catch (IOException e) {
            System.err.println("Could not load settings.properties: " + e.getMessage());
        }
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getRepositoryType() {
        return properties.getProperty("Repository", "memory");
    }

    public String getUIType() {
        return properties.getProperty("UIType", "UI");
    }

    public String getDatabasePath() {
        return properties.getProperty("RepoLocation", "rentals.db");
    }

    public String getCarsPath() {
        return properties.getProperty("CarsFile", "cars.txt");
    }

    public String getRentalsPath() {
        return properties.getProperty("RentalsFile", "rentals.txt");
    }
}