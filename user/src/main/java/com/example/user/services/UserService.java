package com.example.user.services;

import com.example.user.customExceptions.CustomUserException;
import com.example.user.customExceptions.UserNotFoundException;
import com.example.user.models.User;
import com.example.user.models.bin.PutUserCurrencyBin;

/**
 * The UserService interface provides methods for managing user-related operations.
 */
public interface UserService {

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the User object representing the user with the specified ID
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws CustomUserException if there is an error while retrieving the user
     */
    User getUser(String id) throws UserNotFoundException, CustomUserException;

    /**
     * Adds a new user with the specified ID.
     *
     * @param id the ID of the user to add
     * @return the User object representing the newly added user
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws CustomUserException if there is an error while adding the user
     */
    User addUser(String id) throws UserNotFoundException, CustomUserException;

    /**
     * Updates the currency of a user.
     *
     * @param currencyBin the PutUserCurrencyBin object containing the updated currency information
     * @return the User object representing the user with the updated currency
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws CustomUserException if there is an error while updating the currency
     */
    User updateCurrency(PutUserCurrencyBin currencyBin) throws UserNotFoundException, CustomUserException;

}
