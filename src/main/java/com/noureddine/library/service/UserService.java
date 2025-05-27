package com.noureddine.library.service;

import com.noureddine.library.dto.PageResponse;
import com.noureddine.library.dto.UserResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import com.noureddine.library.utils.SearchUtils;
import com.noureddine.library.utils.SortUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    public UserService(UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }

    public PageResponse<UserResponse> getUsers(String role, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<User> users;
        //check if the role is not null nor empty, if it is get only the users with that role else get all the users
        if (role != null && !role.isEmpty()){
            if (role.equalsIgnoreCase("member") || role.equalsIgnoreCase("staff")) {
                users = userRepository.findByRole("ROLE_" + role.toUpperCase());
            }else {
                throw new NotFoundException("Invalid role. Must be 'member' or 'staff'.");
            }
        }else{
            users = userRepository.findAll();
        }
        //check if the there are no users on the database throw an exception
        if(users.isEmpty()){
            throw new NotFoundException("There are no users");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = users.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the users list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(users, User::getId, descending);
                break;
            case "joindate":
                SortUtils.heapSort(users, User::getJoinDate, descending);
                break;
            case "fullname":
                SortUtils.heapSort(users, User::getFullName, descending);
                break;
            case "dateofbirth":
                SortUtils.heapSort(users, User::getDateOfBirth, descending);
                break;
            case "lastactivedate":
                SortUtils.heapSort(users, User::getLastActiveDate, descending);
                break;
            case "numberofborrowings":
                SortUtils.heapSort(users, User::getNumberOfBorrowings, descending);
                break;
            case "birthwilaya":
                SortUtils.heapSort(users, User::getBirthWilaya, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<UserResponse> usersPage = new PageResponse<>();
        usersPage.setTotalElements(totalElements);
        usersPage.setTotalPages(totalPages);
        usersPage.setPageSize(size);
        usersPage.setPageNumber(page);
        usersPage.setFirst(page == 0);
        usersPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<UserResponse> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, users.size());
        //
        for (int i = start; i < end; i++) {
            UserResponse response = new UserResponse();
            response.setId(users.get(i).getId());
            response.setUsername(users.get(i).getUsername());
            response.setEmail(users.get(i).getEmail());
            response.setFullName(users.get(i).getFullName());
            response.setRole(users.get(i).getRole());
            response.setIdentifier(users.get(i).getIdentifier());
            response.setJoinDate(users.get(i).getJoinDate());
            response.setDateOfBirth(users.get(i).getDateOfBirth());
            response.setAccountStatus(users.get(i).getAccountStatus());
            response.setLastActiveDate(users.get(i).getLastActiveDate());
            response.setPhoneNumber(users.get(i).getPhoneNumber());
            //Convert image bytes to Base64 string
            if (users.get(i).getStudentCard() != null) {
                response.setCardBase64(Base64.getEncoder().encodeToString(users.get(i).getStudentCard()));
            } else {
                response.setCardBase64(null); //or a default value like empty string or placeholder image URL
            }
            content.add(response);
        }
        usersPage.setContent(content);
        return usersPage;
    }
    public PageResponse<UserResponse> searchUsers(String keyword, String role, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<User> users;
        //check if the role is not null nor empty, if it is get only the users with that role else get all the users
        if (role != null && !role.isEmpty()){
            if (role.equalsIgnoreCase("member") || role.equalsIgnoreCase("staff")) {
                users = userRepository.searchUsersAndRole(keyword, "ROLE_" + role.toUpperCase());
            }else {
                throw new NotFoundException("Invalid role. Must be 'member' or 'staff'.");
            }
        }else{
            users = userRepository.searchUsers(keyword);
        }
        //check if the there are no users on the database throw an exception
        if(users.isEmpty()){
            throw new NotFoundException("There are no users");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = users.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the users list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(users, User::getId, descending);
                break;
            case "joindate":
                SortUtils.heapSort(users, User::getJoinDate, descending);
                break;
            case "fullname":
                SortUtils.heapSort(users, User::getFullName, descending);
                break;
            case "dateofbirth":
                SortUtils.heapSort(users, User::getDateOfBirth, descending);
                break;
            case "lastactivedate":
                SortUtils.heapSort(users, User::getLastActiveDate, descending);
                break;
            case "numberofborrowings":
                SortUtils.heapSort(users, User::getNumberOfBorrowings, descending);
                break;
            case "birthwilaya":
                SortUtils.heapSort(users, User::getBirthWilaya, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<UserResponse> usersPage = new PageResponse<>();
        usersPage.setTotalElements(totalElements);
        usersPage.setTotalPages(totalPages);
        usersPage.setPageSize(size);
        usersPage.setPageNumber(page);
        usersPage.setFirst(page == 0);
        usersPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<UserResponse> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, users.size());
        //
        for (int i = start; i < end; i++) {
            UserResponse response = new UserResponse();
            response.setId(users.get(i).getId());
            response.setUsername(users.get(i).getUsername());
            response.setEmail(users.get(i).getEmail());
            response.setFullName(users.get(i).getFullName());
            response.setRole(users.get(i).getRole());
            response.setIdentifier(users.get(i).getIdentifier());
            response.setJoinDate(users.get(i).getJoinDate());
            response.setDateOfBirth(users.get(i).getDateOfBirth());
            response.setAccountStatus(users.get(i).getAccountStatus());
            response.setLastActiveDate(users.get(i).getLastActiveDate());
            response.setPhoneNumber(users.get(i).getPhoneNumber());
            //Convert image bytes to Base64 string
            if (users.get(i).getStudentCard() != null) {
                response.setCardBase64(Base64.getEncoder().encodeToString(users.get(i).getStudentCard()));
            } else {
                response.setCardBase64(null); //or a default value like empty string or placeholder image URL
            }
            content.add(response);
        }
        usersPage.setContent(content);
        return usersPage;
    }

    public PageResponse<UserResponse> getAccountRequests(String status, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<User> requests;
        //check if the status is not null nor empty, if it is get only the requests with that status else get all the requests
        if (status != null && !status.isEmpty()){
            requests = userRepository.findByRoleAndAccountStatus("ROLE_MEMBER", status.toUpperCase());
        }else{
            requests = userRepository.findByRole("ROLE_MEMBER");
        }
        //check if the there are no requests on the database throw an exception
        if(requests.isEmpty()){
            throw new NotFoundException("There are no requests");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = requests.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the requests list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(requests, User::getId, descending);
                break;
            case "joindate":
                SortUtils.heapSort(requests, User::getJoinDate, descending);
                break;
            case "fullname":
                SortUtils.heapSort(requests, User::getFullName, descending);
                break;
            case "dateofbirth":
                SortUtils.heapSort(requests, User::getDateOfBirth, descending);
                break;
            case "lastactivedate":
                SortUtils.heapSort(requests, User::getLastActiveDate, descending);
                break;
            case "numberofborrowings":
                SortUtils.heapSort(requests, User::getNumberOfBorrowings, descending);
                break;
            case "birthwilaya":
                SortUtils.heapSort(requests, User::getBirthWilaya, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<UserResponse> requestsPage = new PageResponse<>();
        requestsPage.setTotalElements(totalElements);
        requestsPage.setTotalPages(totalPages);
        requestsPage.setPageSize(size);
        requestsPage.setPageNumber(page);
        requestsPage.setFirst(page == 0);
        requestsPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<UserResponse> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, requests.size());
        //
        for (int i = start; i < end; i++) {
                UserResponse response = new UserResponse();
                response.setId(requests.get(i).getId());
                response.setUsername(requests.get(i).getUsername());
                response.setEmail(requests.get(i).getEmail());
                response.setFullName(requests.get(i).getFullName());
                response.setRole(requests.get(i).getRole());
                response.setIdentifier(requests.get(i).getIdentifier());
                response.setJoinDate(requests.get(i).getJoinDate());
                response.setDateOfBirth(requests.get(i).getDateOfBirth());
                response.setAccountStatus(requests.get(i).getAccountStatus());
                response.setLastActiveDate(requests.get(i).getLastActiveDate());
                response.setPhoneNumber(requests.get(i).getPhoneNumber());
                response.setBirthWilaya(requests.get(i).getBirthWilaya());
                response.setDepartment(requests.get(i).getDepartment());
                //Convert image bytes to Base64 string
                if (requests.get(i).getStudentCard() != null) {
                    response.setCardBase64(Base64.getEncoder().encodeToString(requests.get(i).getStudentCard()));
                } else {
                    response.setCardBase64(null); //or a default value like empty string or placeholder image URL
                }
            content.add(response);
        }
        requestsPage.setContent(content);
        return requestsPage;
    }

    public PageResponse<UserResponse> searchAccountRequests(String keyword, String status, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<User> requests;
        //check if the status is not null nor empty, if it is get only the requests with that status else get all the requests
        if (status != null && !status.isEmpty()){
            requests = userRepository.searchUsersAndRoleAndAccountStatus(keyword, "ROLE_MEMBER", status.toUpperCase());
        }else{
            requests = userRepository.searchUsersAndRole(keyword, "ROLE_MEMBER");
        }
        //check if the there are no requests on the database throw an exception
        if(requests.isEmpty()){
            throw new NotFoundException("There are no requests");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = requests.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the requests list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(requests, User::getId, descending);
                break;
            case "joindate":
                SortUtils.heapSort(requests, User::getJoinDate, descending);
                break;
            case "fullname":
                SortUtils.heapSort(requests, User::getFullName, descending);
                break;
            case "dateofbirth":
                SortUtils.heapSort(requests, User::getDateOfBirth, descending);
                break;
            case "lastactivedate":
                SortUtils.heapSort(requests, User::getLastActiveDate, descending);
                break;
            case "numberofborrowings":
                SortUtils.heapSort(requests, User::getNumberOfBorrowings, descending);
                break;
            case "birthwilaya":
                SortUtils.heapSort(requests, User::getBirthWilaya, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<UserResponse> requestsPage = new PageResponse<>();
        requestsPage.setTotalElements(totalElements);
        requestsPage.setTotalPages(totalPages);
        requestsPage.setPageSize(size);
        requestsPage.setPageNumber(page);
        requestsPage.setFirst(page == 0);
        requestsPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<UserResponse> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, requests.size());
        //
        for (int i = start; i < end; i++) {
            UserResponse response = new UserResponse();
            response.setId(requests.get(i).getId());
            response.setUsername(requests.get(i).getUsername());
            response.setEmail(requests.get(i).getEmail());
            response.setFullName(requests.get(i).getFullName());
            response.setRole(requests.get(i).getRole());
            response.setIdentifier(requests.get(i).getIdentifier());
            response.setJoinDate(requests.get(i).getJoinDate());
            response.setDateOfBirth(requests.get(i).getDateOfBirth());
            response.setAccountStatus(requests.get(i).getAccountStatus());
            response.setLastActiveDate(requests.get(i).getLastActiveDate());
            response.setPhoneNumber(requests.get(i).getPhoneNumber());
            response.setBirthWilaya(requests.get(i).getBirthWilaya());
            //Convert image bytes to Base64 string
            if (requests.get(i).getStudentCard() != null) {
                response.setCardBase64(Base64.getEncoder().encodeToString(requests.get(i).getStudentCard()));
            } else {
                response.setCardBase64(null); //or a default value like empty string or placeholder image URL
            }
            content.add(response);
        }
        requestsPage.setContent(content);
        return requestsPage;
    }

    public User getInfo(Long staffId) throws NotFoundException {
        //get all users from database
        List<User> allUsers = userRepository.findAll();
        //check if the users list is empty, if it is throw an exception
        if(allUsers.isEmpty()){
            throw new NotFoundException("There are no users.");
        }
        //sort the list and then search for the member with the provided id
        SortUtils.heapSort(allUsers, User::getId);
        User user = SearchUtils.binarySearch(allUsers, staffId, User::getId);
        //if the member not found throw an exception
        if(user == null){
            throw new NotFoundException("The user not found");
        }
        return user;
    }

    public void deleteUser(Long userId) throws NotFoundException {
        //get all users from database
        List<User> allUsers = userRepository.findAll();
        //check if the users list is empty, if it is throw an exception
        if(allUsers.isEmpty()){
            throw new NotFoundException("There are no users.");
        }
        //sort the list and then search for the member with the provided id
        SortUtils.heapSort(allUsers, User::getId);
        User user = SearchUtils.binarySearch(allUsers, userId, User::getId);
        //if the user not found throw an exception
        if(user == null){
            throw new NotFoundException("The user not found");
        }
        if(!user.getRole().equals("ROLE_MEMBER")){
            throw new NotFoundException("You don not have the rights to delete a staff");
        }
        //check if the user has unreturned borrowings, if it is throw an exception
        if(!borrowingRepository.findByMemberAndStatus(user, "PICKED_UP").isEmpty()){
           throw new NotFoundException("The user has not returned borrowings");
        }
        //delete the user
        userRepository.deleteById(userId);
    }

    public void reviewAccount(Long userId, String status) throws NotFoundException {
        //make sure the status is not empty or null
        if (status == null || status.isEmpty() ||
                (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED"))) {
            throw new NotFoundException("Invalid status. Must be 'APPROVED' or 'REJECTED'.");
        }
        //get all users from database
        List<User> allUsers = userRepository.findAll();
        //check if the users list is empty, if it is throw an exception
        if(allUsers.isEmpty()){
            throw new NotFoundException("There are no users.");
        }
        //sort the list and then search for the member with the provided id
        SortUtils.heapSort(allUsers, User::getId);
        User user = SearchUtils.binarySearch(allUsers, userId, User::getId);
        //if the user not found throw an exception
        if(user == null){
            throw new NotFoundException("The user not found");
        }
        //change the status if the user
        user.setAccountStatus(status.toUpperCase());
        userRepository.save(user);
    }


}
