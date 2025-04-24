package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDTO {
    private String name;
    private String address;
    private Date checkIn;
    private Date checkOut;
}
