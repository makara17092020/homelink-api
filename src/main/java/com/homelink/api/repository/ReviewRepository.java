package com.homelink.api.repository;

import com.homelink.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByUserIdAndRentalPostId(Long userId, Long rentalPostId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.rentalPost.id = :rentalPostId")
    Double getAverageRatingByRentalPostId(@Param("rentalPostId") Long rentalPostId);
    
}