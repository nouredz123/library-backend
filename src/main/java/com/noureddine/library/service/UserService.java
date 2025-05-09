package com.noureddine.library.service;

import com.noureddine.library.dto.UserResponse;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }
    public Page<UserResponse> getUsers(String role, int page, int size, String sortBy, String direction) throws NotFoundException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage;
        if (role != null && !role.isEmpty()) {
            usersPage = userRepository.findByRole("ROLE_"+role.toUpperCase(), pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }


        if (usersPage.isEmpty()) {
            throw new NotFoundException("No Users found in the database");
        }

        return usersPage.map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole());
            response.setIdentifier(user.getIdentifier());
            response.setJoinDate(user.getJoinDate());
            response.setDateOfBirth(user.getDateOfBirth());
            response.setAccountStatus(user.getAccountStatus());
            response.setLastActiveDate(user.getLastActiveDate());
            response.setPhoneNumber(user.getPhoneNumber());
            // Convert image bytes to Base64 string
            if (user.getStudentCard() != null) {
                response.setCardBase64(Base64.getEncoder().encodeToString(user.getStudentCard()));
            } else {
                response.setCardBase64(null); // or a default value like empty string or placeholder image URL
            }
            return response;
        });
    }
    public Page<UserResponse> getAccountRequests(String status, int page, int size, String sortBy, String direction) throws NotFoundException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> requestsPage;
        if (status != null && !status.isEmpty()) {
            requestsPage = userRepository.findByRoleAndAccountStatus("ROLE_MEMBER",status.toUpperCase(), pageable);
        } else {
            requestsPage = userRepository.findByRole("ROLE_MEMBER", pageable);
        }

        if (requestsPage.isEmpty()) {
            throw new NotFoundException("No Requests found in the database");
        }
        return requestsPage.map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole());
            response.setIdentifier(user.getIdentifier());
            response.setJoinDate(user.getJoinDate());
            response.setDateOfBirth(user.getDateOfBirth());
            response.setAccountStatus(user.getAccountStatus());
            response.setLastActiveDate(user.getLastActiveDate());
            response.setPhoneNumber(user.getPhoneNumber());
            // Convert image bytes to Base64 string
            if (user.getStudentCard() != null) {
                response.setCardBase64(Base64.getEncoder().encodeToString(user.getStudentCard()));
            } else {
                response.setCardBase64(null); // or a default value like empty string or placeholder image URL
            }
            return response;
        });
    }
    public User getInfo(Long memberId) throws NotFoundException {
        return userRepository.findById(memberId).orElseThrow(()-> new NotFoundException("The user not found"));
    }
    public void deleteUser(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("The user not found"));
        if(!user.getRole().equals("ROLE_MEMBER")){
            throw new NotFoundException("You don not have the rights to delete a staff");
        }
        if(!borrowingRepository.findByMemberAndStatus(user, "PICKED_UP").isEmpty()){
           throw new NotFoundException("The user has not returned borrowings");
        }
        userRepository.deleteById(userId);
    }

    public void reviewAccount(Long userId, String status) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("User not found"));
        if(status == null || status.isEmpty()){
           throw new NotFoundException("Invalid status");
        }
        user.setAccountStatus(status.toUpperCase());
        userRepository.save(user);
    }
}
