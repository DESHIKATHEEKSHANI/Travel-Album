package edu.icet.repository;

import edu.icet.entity.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {
    List<Trip> findByUsername(String username);
    List<Trip> findByUsernameAndIsUpcoming(String username, boolean isUpcoming);
    List<Trip> findByUsernameAndIsPast(String username, boolean isPast);
    List<Trip> findByUsernameAndIsUpcomingAndIsPast(String username, boolean isUpcoming, boolean isPast);
    List<Trip> findByStartDateAfter(Date date);
    List<Trip> findByEndDateBefore(Date date);
    List<Trip> findByDestinationContainingIgnoreCase(String destination);
}