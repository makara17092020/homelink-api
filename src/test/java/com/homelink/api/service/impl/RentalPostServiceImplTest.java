package com.homelink.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.homelink.api.dto.request.UpdateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.User;
import com.homelink.api.exception.BadRequestException;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RentalPostServiceImplTest {

    @Mock
    RentalPostRepository rentalPostRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    RentalPostServiceImpl service;

    @Test
    void updateByOwner_success() {
        User owner = new User();
        owner.setUsername("agent1");
        owner.setFullName("Agent One");
        owner.setRoles(List.of("ROLE_AGENT"));

        RentalPost post = new RentalPost();
        post.setId(1L);
        post.setTitle("Old title");
        post.setDescription("Old desc");
        post.setPrice(new BigDecimal("100"));
        post.setAgent(owner);
        post.setImages(new ArrayList<>());

        when(userRepository.findByUsername("agent1")).thenReturn(Optional.of(owner));
        when(rentalPostRepository.findById(1L)).thenReturn(Optional.of(post));
        when(rentalPostRepository.save(any(RentalPost.class))).thenAnswer(i -> i.getArgument(0));

        UpdateRentalPostRequest req = new UpdateRentalPostRequest();
        req.setTitle("New title");
        req.setImageUrls(List.of("http://a.jpg", "http://b.jpg"));

        RentalPostResponse resp = service.updatePost(1L, req, "agent1");

        assertThat(resp.getTitle()).isEqualTo("New title");
        assertThat(resp.getImageUrls()).containsExactly("http://a.jpg", "http://b.jpg");
    }

    @Test
    void updateByNonOwner_nonAdmin_forbidden() {
        User owner = new User();
        owner.setUsername("agent1");
        owner.setRoles(List.of("ROLE_AGENT"));

        User other = new User();
        other.setUsername("user2");
        other.setRoles(List.of("ROLE_AGENT"));

        RentalPost post = new RentalPost();
        post.setId(2L);
        post.setAgent(owner);
        post.setImages(new ArrayList<>());

        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(other));
        when(rentalPostRepository.findById(2L)).thenReturn(Optional.of(post));

        UpdateRentalPostRequest req = new UpdateRentalPostRequest();
        req.setTitle("X");

        assertThrows(BadRequestException.class, () -> service.updatePost(2L, req, "user2"));
    }

    @Test
    void updateByAdmin_success() {
        User owner = new User();
        owner.setUsername("agent1");
        owner.setRoles(List.of("ROLE_AGENT"));

        User admin = new User();
        admin.setUsername("admin1");
        admin.setRoles(List.of("ROLE_ADMIN"));

        RentalPost post = new RentalPost();
        post.setId(3L);
        post.setTitle("Old");
        post.setAgent(owner);
        post.setImages(new ArrayList<>());

        when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(admin));
        when(rentalPostRepository.findById(3L)).thenReturn(Optional.of(post));
        when(rentalPostRepository.save(any(RentalPost.class))).thenAnswer(i -> i.getArgument(0));

        UpdateRentalPostRequest req = new UpdateRentalPostRequest();
        req.setDescription("Admin updated");

        RentalPostResponse resp = service.updatePost(3L, req, "admin1");

        assertThat(resp.getDescription()).isEqualTo("Admin updated");
    }
}
