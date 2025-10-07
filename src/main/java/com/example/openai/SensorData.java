package com.example.openai;

/**
 * Dados dos sensores do viveiro de aquicultura.
 * 
 * @author Renato V. Mendes
 */
public class SensorData {
    private final double oxygenLevel;      // mg/L
    private final double waterTemperature; // °C
    private final double ph;               // pH
    private final double ammoniaLevel;     // mg/L
    private final String weatherForecast;  // Previsão climática
    private final String pondId;           // ID do viveiro
    
    public SensorData(double oxygenLevel, double waterTemperature, double ph, 
                     double ammoniaLevel, String weatherForecast, String pondId) {
        this.oxygenLevel = oxygenLevel;
        this.waterTemperature = waterTemperature;
        this.ph = ph;
        this.ammoniaLevel = ammoniaLevel;
        this.weatherForecast = weatherForecast;
        this.pondId = pondId;
    }
    
    // Getters
    public double getOxygenLevel() { return oxygenLevel; }
    public double getWaterTemperature() { return waterTemperature; }
    public double getPh() { return ph; }
    public double getAmmoniaLevel() { return ammoniaLevel; }
    public String getWeatherForecast() { return weatherForecast; }
    public String getPondId() { return pondId; }
    
    @Override
    public String toString() {
        return String.format("Viveiro %s - O2: %.1f mg/L, Temp: %.1f°C, pH: %.1f, NH3: %.1f mg/L, Clima: %s",
                pondId, oxygenLevel, waterTemperature, ph, ammoniaLevel, weatherForecast);
    }
}