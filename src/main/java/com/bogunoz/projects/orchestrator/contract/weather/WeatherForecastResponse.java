package com.bogunoz.projects.orchestrator.contract.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastResponse {
    private String weatherDescription;
    private float temperature;
    private float feelsLike;
    private float tempMin;
    private float tempMax;
    private int pressure;
    private int humidity;
}
