package edu.icet.controller;

import edu.icet.dto.ImageResponseDTO;
import edu.icet.dto.TripDTO;
import edu.icet.service.SupabaseService;
import edu.icet.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;
    private final SupabaseService supabaseService;

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(
            @RequestBody TripDTO tripDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Creating new trip for user: {}", tripDTO.getUsername());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO createdTrip = tripService.createTrip(tripDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrip);
        } catch (Exception e) {
            log.error("Failed to create trip", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/with-image", consumes = "multipart/form-data")
    public ResponseEntity<TripDTO> createTripWithImage(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @RequestPart(value = "tripDTO", required = true) TripDTO tripDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Creating new trip with image for user: {}", tripDTO.getUsername());
        log.info("Content type of request: {}", file.getContentType());
        log.info("File name: {}", file.getOriginalFilename());
        log.info("File size: {}", file.getSize());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ImageResponseDTO imageResponse = supabaseService.uploadImage(file, tripDTO.getUsername());
            tripDTO.setImageUrl(imageResponse.getUrl());
            TripDTO createdTrip = tripService.createTrip(tripDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrip);
        } catch (Exception e) {
            log.error("Failed to create trip with image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TripDTO>> getAllTrips(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String filter,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Fetching trips for user: {} with filter: {}", username, filter);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<TripDTO> trips;
            if (username != null && !username.isEmpty()) {
                // Only apply filter if it's not null
                if (filter != null && !filter.isEmpty()) {
                    switch (filter) {
                        case "upcoming":
                            trips = tripService.getUpcomingTrips(username);
                            break;
                        case "past":
                            trips = tripService.getPastTrips(username);
                            break;
                        case "planning":
                            trips = tripService.getPlanningTrips(username);
                            break;
                        default:
                            trips = tripService.getTripsByUsername(username);
                            break;
                    }
                } else {
                    // No filter provided, get all trips for the user
                    trips = tripService.getTripsByUsername(username);
                }
            } else {
                trips = tripService.getAllTrips();
            }
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            log.error("Failed to fetch trips: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Fetching trip with ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO trip = tripService.getTripById(id);
            if (trip != null) {
                return ResponseEntity.ok(trip);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch trip", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(
            @PathVariable String id,
            @RequestBody TripDTO tripDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating trip with ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!id.equals(tripDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            TripDTO updatedTrip = tripService.updateTrip(tripDTO);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to update trip", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<TripDTO> updateTripImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating image for trip ID: {} for user: {}", id, username);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO existingTrip = tripService.getTripById(id);
            if (existingTrip == null) {
                return ResponseEntity.notFound().build();
            }

            ImageResponseDTO imageResponse = supabaseService.uploadImage(file, username);
            existingTrip.setImageUrl(imageResponse.getUrl());
            TripDTO updatedTrip = tripService.updateTrip(existingTrip);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to update trip image", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Deleting trip with ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean deleted = tripService.deleteTrip(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to delete trip", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/packing-list")
    public ResponseEntity<TripDTO> updatePackingList(
            @PathVariable String id,
            @RequestBody TripDTO.PackingListDTO packingList,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating packing list for trip ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.updatePackingList(id, packingList);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to update packing list", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/itinerary")
    public ResponseEntity<TripDTO> addItineraryDay(
            @PathVariable String id,
            @RequestBody TripDTO.ItineraryDayDTO day,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Adding itinerary day to trip ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.addItineraryDay(id, day);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to add itinerary day", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/itinerary/{dayIndex}")
    public ResponseEntity<TripDTO> updateItineraryDay(
            @PathVariable String id,
            @PathVariable int dayIndex,
            @RequestBody TripDTO.ItineraryDayDTO day,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating itinerary day {} for trip ID: {}", dayIndex, id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.updateItineraryDay(id, dayIndex, day);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to update itinerary day", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}/itinerary/{dayIndex}")
    public ResponseEntity<TripDTO> deleteItineraryDay(
            @PathVariable String id,
            @PathVariable int dayIndex,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Deleting itinerary day {} from trip ID: {}", dayIndex, id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.deleteItineraryDay(id, dayIndex);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to delete itinerary day", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/accommodations")
    public ResponseEntity<TripDTO> addAccommodation(
            @PathVariable String id,
            @RequestBody TripDTO.AccommodationDTO accommodation,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Adding accommodation to trip ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.addAccommodation(id, accommodation);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to add accommodation", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/dates")
    public ResponseEntity<TripDTO> updateTripDates(
            @PathVariable String id,
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating dates for trip ID: {}", id);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TripDTO updatedTrip = tripService.updateTripDates(id, startDate, endDate);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            log.error("Failed to update trip dates", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}