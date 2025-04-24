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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccommodationDTO {
        private String name;
        private String address;
        private Date checkIn;
        private Date checkOut;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItineraryDayDTO {
        private Date date;
        private List<ActivityDTO> activities = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDTO {
        private String time;
        private String name;
        private String description;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackingListDTO {
        private List<String> essentials = new ArrayList<>();
        private List<String> clothing = new ArrayList<>();
    }
}