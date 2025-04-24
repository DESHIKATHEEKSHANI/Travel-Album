package edu.icet.service;

import edu.icet.dto.TripDTO;

import java.util.Date;
import java.util.List;

public interface TripService {
    TripDTO createTrip(TripDTO tripDTO);
    List<TripDTO> getAllTrips();
    List<TripDTO> getTripsByUsername(String username);
    List<TripDTO> getUpcomingTrips(String username);
    List<TripDTO> getPastTrips(String username);
    List<TripDTO> getPlanningTrips(String username);
    TripDTO getTripById(String id);
    TripDTO updateTrip(TripDTO tripDTO);
    boolean deleteTrip(String id);
    TripDTO updatePackingList(String id, TripDTO.PackingListDTO packingList);
    TripDTO addItineraryDay(String id, TripDTO.ItineraryDayDTO day);
    TripDTO updateItineraryDay(String id, int dayIndex, TripDTO.ItineraryDayDTO day);
    TripDTO deleteItineraryDay(String id, int dayIndex);
    TripDTO addAccommodation(String id, TripDTO.AccommodationDTO accommodation);
    TripDTO updateTripDates(String id, Date startDate, Date endDate);
}