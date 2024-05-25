package com.example.user.services;

import com.example.user.customExceptions.CustomUserException;
import com.example.user.customExceptions.UserNotFoundException;
import com.example.user.models.User;
import com.example.user.models.bin.PutUserCurrencyBin;

public interface UserService {

    User getUser(String id) throws UserNotFoundException, CustomUserException;

    User addUser(String id) throws UserNotFoundException, CustomUserException;

    User updateCurrency(PutUserCurrencyBin currencyBin) throws UserNotFoundException, CustomUserException;

}
