package com.noureddine.library.service;

import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getInfo(Long memberId) throws NotFoundException {
        User user = userRepository.findById(memberId).orElseThrow(()-> new NotFoundException("The user not found"));
        return user;
    }
}
