package com.noureddine.library.service;

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


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }
    public Page<User> getUsers( String role, int page, int size, String sortBy, String direction) throws NotFoundException {
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

        return usersPage;
    }
    public User getInfo(Long memberId) throws NotFoundException {
        return userRepository.findById(memberId).orElseThrow(()-> new NotFoundException("The user not found"));
    }
    public void deleteUser(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("The user not found"));
        if(!user.getRole().equals("MEMBER")){
            throw new NotFoundException("You don not have the rights to delete a staff");
        }
        if(!borrowingRepository.findByMemberAndStatus(user, "PICKED_UP").isEmpty()){
           throw new NotFoundException("The user has not returned borrowings");
        }
        if(user.getRole().equals("ROLE_STAFF")){
            throw new NotFoundException("You can not delete a staff");
        }
        userRepository.deleteById(userId);
    }
}
