package edu.icet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    private String name;
    private String address;
    private Date checkIn;
    private Date checkOut;
}
