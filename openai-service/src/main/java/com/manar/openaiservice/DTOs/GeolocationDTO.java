package com.manar.openaiservice.DTOs;


import lombok.Data;

@Data
public class GeolocationDTO {
    private String location;
    private String weatherDescription;
    private String time;
    private Long explorerId;

}
