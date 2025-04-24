package edu.icet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private String id;
    private String destination;
    private Date startDate;
    private Date endDate;
    private int duration;
    private double budget;
    private String description;
    private String imageUrl;
    private String username;
    private boolean isUpcoming;
    private boolean isPast;
    private List<AccommodationDTO> accommodations = new ArrayList<>();
    private List<ItineraryDayDTO> itinerary = new ArrayList<>();
    private PackingListDTO packingList = new PackingListDTO();
    private Date createdAt;
    private Date updatedAt;
}