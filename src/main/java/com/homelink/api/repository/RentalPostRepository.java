package com.homelink.api.repository;

import com.homelink.api.entity.RentalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalPostRepository extends JpaRepository<RentalPost, Long> {
}