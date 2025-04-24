package edu.icet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trips")
public class Trip {
    @Id
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
    private List<Accommodation> accommodations = new ArrayList<>();
    private List<ItineraryDay> itinerary = new ArrayList<>();
    private PackingList packingList = new PackingList();
    private Date createdAt;
    private Date updatedAt;
}