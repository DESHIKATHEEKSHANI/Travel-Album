package edu.icet.service.impl;

import edu.icet.dto.TripDTO;
import edu.icet.entity.Trip;
import edu.icet.repository.TripRepository;
import edu.icet.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        Trip trip = mapToEntity(tripDTO);

        // Generate a unique ID if not provided
        if (trip.getId() == null || trip.getId().isEmpty()) {
            trip.setId(UUID.randomUUID().toString());
        }

        // Set timestamps
        Date now = new Date();
        trip.setCreatedAt(now);
        trip.setUpdatedAt(now);

        // Calculate if trip is upcoming or past
        updateTripStatus(trip);

        Trip savedTrip = tripRepository.save(trip);
        return mapToDTO(savedTrip);
    }

    @Override
    public List<TripDTO> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getTripsByUsername(String username) {
        return tripRepository.findByUsername(username).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getUpcomingTrips(String username) {
        return tripRepository.findByUsernameAndIsUpcoming(username, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getPastTrips(String username) {
        return tripRepository.findByUsernameAndIsPast(username, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO> getPlanningTrips(String username) {
        return tripRepository.findByUsernameAndIsUpcomingAndIsPast(username, false, false).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TripDTO getTripById(String id) {
        Optional<Trip> tripOptional = tripRepository.findById(id);
        return tripOptional.map(this::mapToDTO).orElse(null);
    }

    @Override
    public TripDTO updateTrip(TripDTO tripDTO) {
        if (tripRepository.existsById(tripDTO.getId())) {
            Trip trip = mapToEntity(tripDTO);

            // Update timestamp
            trip.setUpdatedAt(new Date());

            // Update status
            updateTripStatus(trip);

            Trip updatedTrip = tripRepository.save(trip);
            return mapToDTO(updatedTrip);
        }
        return null;
    }

    @Override
    public boolean deleteTrip(String id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public TripDTO updatePackingList(String id, TripDTO.PackingListDTO packingList) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            // Update packing list
            Trip.PackingList entityPackingList = new Trip.PackingList();
            entityPackingList.setEssentials(packingList.getEssentials());
            entityPackingList.setClothing(packingList.getClothing());
            trip.setPackingList(entityPackingList);

            // Update timestamp
            trip.setUpdatedAt(new Date());

            Trip updatedTrip = tripRepository.save(trip);
            return mapToDTO(updatedTrip);
        }

        return null;
    }

    @Override
    public TripDTO addItineraryDay(String id, TripDTO.ItineraryDayDTO day) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            // Convert DTO to entity
            Trip.ItineraryDay entityDay = new Trip.ItineraryDay();
            entityDay.setDate(day.getDate());

            List<Trip.Activity> activities = day.getActivities().stream()
                    .map(activityDTO -> {
                        Trip.Activity activity = new Trip.Activity();
                        activity.setTime(activityDTO.getTime());
                        activity.setName(activityDTO.getName());
                        activity.setDescription(activityDTO.getDescription());
                        activity.setLocation(activityDTO.getLocation());
                        return activity;
                    })
                    .collect(Collectors.toList());

            entityDay.setActivities(activities);

            // Add to itinerary
            if (trip.getItinerary() == null) {
                trip.setItinerary(new ArrayList<>());
            }
            trip.getItinerary().add(entityDay);

            // Update timestamp
            trip.setUpdatedAt(new Date());

            Trip updatedTrip = tripRepository.save(trip);
            return mapToDTO(updatedTrip);
        }

        return null;
    }

    @Override
    public TripDTO updateItineraryDay(String id, int dayIndex, TripDTO.ItineraryDayDTO day) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            if (trip.getItinerary() != null && dayIndex >= 0 && dayIndex < trip.getItinerary().size()) {
                // Convert DTO to entity
                Trip.ItineraryDay entityDay = new Trip.ItineraryDay();
                entityDay.setDate(day.getDate());

                List<Trip.Activity> activities = day.getActivities().stream()
                        .map(activityDTO -> {
                            Trip.Activity activity = new Trip.Activity();
                            activity.setTime(activityDTO.getTime());
                            activity.setName(activityDTO.getName());
                            activity.setDescription(activityDTO.getDescription());
                            activity.setLocation(activityDTO.getLocation());
                            return activity;
                        })
                        .collect(Collectors.toList());

                entityDay.setActivities(activities);

                // Update the day
                trip.getItinerary().set(dayIndex, entityDay);

                // Update timestamp
                trip.setUpdatedAt(new Date());

                Trip updatedTrip = tripRepository.save(trip);
                return mapToDTO(updatedTrip);
            }
        }

        return null;
    }

    @Override
    public TripDTO deleteItineraryDay(String id, int dayIndex) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            if (trip.getItinerary() != null && dayIndex >= 0 && dayIndex < trip.getItinerary().size()) {
                // Remove the day
                trip.getItinerary().remove(dayIndex);

                // Update timestamp
                trip.setUpdatedAt(new Date());

                Trip updatedTrip = tripRepository.save(trip);
                return mapToDTO(updatedTrip);
            }
        }

        return null;
    }

    @Override
    public TripDTO addAccommodation(String id, TripDTO.AccommodationDTO accommodation) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            // Convert DTO to entity
            Trip.Accommodation entityAccommodation = new Trip.Accommodation();
            entityAccommodation.setName(accommodation.getName());
            entityAccommodation.setAddress(accommodation.getAddress());
            entityAccommodation.setCheckIn(accommodation.getCheckIn());
            entityAccommodation.setCheckOut(accommodation.getCheckOut());

            // Add to accommodations
            if (trip.getAccommodations() == null) {
                trip.setAccommodations(new ArrayList<>());
            }
            trip.getAccommodations().add(entityAccommodation);

            // Update timestamp
            trip.setUpdatedAt(new Date());

            Trip updatedTrip = tripRepository.save(trip);
            return mapToDTO(updatedTrip);
        }

        return null;
    }

    @Override
    public TripDTO updateTripDates(String id, Date startDate, Date endDate) {
        Optional<Trip> tripOptional = tripRepository.findById(id);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            trip.setStartDate(startDate);
            trip.setEndDate(endDate);

            // Calculate duration
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            int diffInDays = (int) (diffInMillies / (24 * 60 * 60 * 1000));
            trip.setDuration(diffInDays);

            // Update status
            updateTripStatus(trip);

            // Update timestamp
            trip.setUpdatedAt(new Date());

            Trip updatedTrip = tripRepository.save(trip);
            return mapToDTO(updatedTrip);
        }

        return null;
    }

    private void updateTripStatus(Trip trip) {
        Date now = new Date();
        trip.setUpcoming(trip.getStartDate().after(now));
        trip.setPast(trip.getEndDate().before(now));
    }

    // Mapping methods
    private Trip mapToEntity(TripDTO dto) {
        Trip trip = new Trip();
        trip.setId(dto.getId());
        trip.setDestination(dto.getDestination());
        trip.setStartDate(dto.getStartDate());
        trip.setEndDate(dto.getEndDate());
        trip.setDuration(dto.getDuration());
        trip.setBudget(dto.getBudget());
        trip.setDescription(dto.getDescription());
        trip.setImageUrl(dto.getImageUrl());
        trip.setUsername(dto.getUsername());
        trip.setUpcoming(dto.isUpcoming());
        trip.setPast(dto.isPast());
        trip.setCreatedAt(dto.getCreatedAt());
        trip.setUpdatedAt(dto.getUpdatedAt());

        // Map accommodations
        if (dto.getAccommodations() != null) {
            List<Trip.Accommodation> accommodations = dto.getAccommodations().stream()
                    .map(accomDto -> {
                        Trip.Accommodation accommodation = new Trip.Accommodation();
                        accommodation.setName(accomDto.getName());
                        accommodation.setAddress(accomDto.getAddress());
                        accommodation.setCheckIn(accomDto.getCheckIn());
                        accommodation.setCheckOut(accomDto.getCheckOut());
                        return accommodation;
                    })
                    .collect(Collectors.toList());
            trip.setAccommodations(accommodations);
        }

        // Map itinerary
        if (dto.getItinerary() != null) {
            List<Trip.ItineraryDay> itinerary = dto.getItinerary().stream()
                    .map(dayDto -> {
                        Trip.ItineraryDay day = new Trip.ItineraryDay();
                        day.setDate(dayDto.getDate());

                        if (dayDto.getActivities() != null) {
                            List<Trip.Activity> activities = dayDto.getActivities().stream()
                                    .map(activityDto -> {
                                        Trip.Activity activity = new Trip.Activity();
                                        activity.setTime(activityDto.getTime());
                                        activity.setName(activityDto.getName());
                                        activity.setDescription(activityDto.getDescription());
                                        activity.setLocation(activityDto.getLocation());
                                        return activity;
                                    })
                                    .collect(Collectors.toList());
                            day.setActivities(activities);
                        }

                        return day;
                    })
                    .collect(Collectors.toList());
            trip.setItinerary(itinerary);
        }

        // Map packing list
        if (dto.getPackingList() != null) {
            Trip.PackingList packingList = new Trip.PackingList();
            packingList.setEssentials(dto.getPackingList().getEssentials());
            packingList.setClothing(dto.getPackingList().getClothing());
            trip.setPackingList(packingList);
        }

        return trip;
    }

    private TripDTO mapToDTO(Trip entity) {
        TripDTO dto = new TripDTO();
        dto.setId(entity.getId());
        dto.setDestination(entity.getDestination());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDuration(entity.getDuration());
        dto.setBudget(entity.getBudget());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setUsername(entity.getUsername());
        dto.setUpcoming(entity.isUpcoming());
        dto.setPast(entity.isPast());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Map accommodations
        if (entity.getAccommodations() != null) {
            List<TripDTO.AccommodationDTO> accommodations = entity.getAccommodations().stream()
                    .map(accom -> {
                        TripDTO.AccommodationDTO accommodation = new TripDTO.AccommodationDTO();
                        accommodation.setName(accom.getName());
                        accommodation.setAddress(accom.getAddress());
                        accommodation.setCheckIn(accom.getCheckIn());
                        accommodation.setCheckOut(accom.getCheckOut());
                        return accommodation;
                    })
                    .collect(Collectors.toList());
            dto.setAccommodations(accommodations);
        }

        // Map itinerary
        if (entity.getItinerary() != null) {
            List<TripDTO.ItineraryDayDTO> itinerary = entity.getItinerary().stream()
                    .map(day -> {
                        TripDTO.ItineraryDayDTO dayDto = new TripDTO.ItineraryDayDTO();
                        dayDto.setDate(day.getDate());

                        if (day.getActivities() != null) {
                            List<TripDTO.ActivityDTO> activities = day.getActivities().stream()
                                    .map(activity -> {
                                        TripDTO.ActivityDTO activityDto = new TripDTO.ActivityDTO();
                                        activityDto.setTime(activity.getTime());
                                        activityDto.setName(activity.getName());
                                        activityDto.setDescription(activity.getDescription());
                                        activityDto.setLocation(activity.getLocation());
                                        return activityDto;
                                    })
                                    .collect(Collectors.toList());
                            dayDto.setActivities(activities);
                        }

                        return dayDto;
                    })
                    .collect(Collectors.toList());
            dto.setItinerary(itinerary);
        }

        // Map packing list
        if (entity.getPackingList() != null) {
            TripDTO.PackingListDTO packingList = new TripDTO.PackingListDTO();
            packingList.setEssentials(entity.getPackingList().getEssentials());
            packingList.setClothing(entity.getPackingList().getClothing());
            dto.setPackingList(packingList);
        }

        return dto;
    }
}