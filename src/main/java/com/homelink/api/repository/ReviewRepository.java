package com.homelink.api.repository;

import com.homelink.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> { // Changed UUID to Long to match your entity IDs

    // Finds a specific review by a user for a post (Useful for preventing duplicate reviews)
    Optional<Review> findByUserIdAndRentalPostId(Long userId, Long rentalPostId);

    // Fetches all reviews for a specific post (Required for your "get all posts" logic)
    List<Review> findByRentalPostId(Long rentalPostId);

    // Calculates the average rating
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.rentalPost.id = :rentalPostId")
    Double getAverageRatingByRentalPostId(@Param("rentalPostId") Long rentalPostId);
    
    // Counts how many users rated the property
    Long countByRentalPostId(Long rentalPostId);
}