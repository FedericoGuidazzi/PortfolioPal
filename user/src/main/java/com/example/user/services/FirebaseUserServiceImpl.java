package com.example.user.services;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.user.customExceptions.CustomUserException;
import com.example.user.customExceptions.DeleteUserException;
import com.example.user.customExceptions.UserNotFoundException;
import com.example.user.models.User;
import com.example.user.models.bin.PutUserCurrencyBin;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseUserServiceImpl implements UserService {
    private static final String COLLECTION_NAME = "users";

    private DocumentReference getFirestoreUserDocument(String id) {
        return FirestoreClient.getFirestore()
                .collection(COLLECTION_NAME)
                .document(id);
    }

    private static User mapFirestoreUserToUser(Map<String, Object> data) {
        // check if the data is empty
        if (Optional.ofNullable(data).isEmpty()) {
            return null;
        }

        // map firestore data to user object
        String name = (String) data.getOrDefault("name", "");
        boolean sharePortfolio = (boolean) data.getOrDefault("sharePortfolio", true);
        String currency = (String) data.getOrDefault("favouriteCurrency", "USD");

        return User.builder()
                .name(name)
                .sharePortfolio(sharePortfolio)
                .favouriteCurrency(currency)
                .build();
    }

    private String getFirebaseAuthUserEmail(String userID) {
        try {
            return FirebaseAuth.getInstance().getUser(userID).getEmail();
        } catch (FirebaseAuthException e) {
            return null;
        }
    }

    private boolean userExistInFirestore(String id) {
        try {
            return this.getFirestoreUserDocument(id)
                    .get().get().exists();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    private User getFirestoreUserData(String id) {
        try {
            DocumentSnapshot documentSnapshot = this.getFirestoreUserDocument(id).get().get();
            return documentSnapshot.exists() ? mapFirestoreUserToUser(documentSnapshot.getData()) : null;
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private void deleteFirestoreUser(String id) throws DeleteUserException {
        try {
            this.getFirestoreUserDocument(id)
                    .delete()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DeleteUserException(e);
        }
    }

    private void setFirestoreUser(String id, User user) throws CustomUserException {
        try {
            if (Optional.ofNullable(user).isPresent()) {
                this.getFirestoreUserDocument(id)
                        .set(user)
                        .get();
            } else {
                throw new CustomUserException("User is null");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new CustomUserException(e);
        }
    }

    protected User setUser(String id, User newUser)
            throws DeleteUserException, CustomUserException {

        // get user email from firebase auth
        Optional<String> userAuthEmail = Optional.ofNullable(this.getFirebaseAuthUserEmail(id));
        if (userAuthEmail.isPresent()) {
            // create new user if it doesn't exist or update the existing one
            Optional<User> optionalUser = Optional.ofNullable(newUser);
            User user = this.userExistInFirestore(id) && optionalUser.isPresent() ? optionalUser.get()
                    : User.builder()
                            .name(userAuthEmail.get().replaceAll("@.*$", ""))
                            .sharePortfolio(true)
                            .favouriteCurrency("EUR")
                            .build();

            // set user data in firestore
            this.setFirestoreUser(id, user);
            return user;

        } else { // User doesn't exist in the auth db
            if (userExistInFirestore(id)) {
                // if the user isn't in firebase auth database then he'll be deleted
                deleteFirestoreUser(id);
            }
            return null;
        }
    }

    @Override
    public User getUser(String id) throws UserNotFoundException, CustomUserException {
        try {
            Optional<String> userAuthEmail = Optional.ofNullable(this.getFirebaseAuthUserEmail(id));
            if (userAuthEmail.isPresent()) {
                Optional<User> data = Optional.ofNullable(this.getFirestoreUserData(id));
                if (data.isPresent()) {
                    return data.get();
                } else {
                    // try to restore firestore user info
                    // if he doesn't exist in firebase auth database he'll be deleted
                    Optional<User> user = Optional.ofNullable(setUser(id, null));
                    if (user.isPresent()) {
                        return user.get();
                    }
                }
            } else {
                if (userExistInFirestore(id)) {
                    // if the user isn't in firebase auth database then he'll be deleted
                    deleteFirestoreUser(id);
                }
            }
            throw new UserNotFoundException("User not found with id: " + id);
        } catch (DeleteUserException e) {
            throw new CustomUserException("Failed to reset user");
        }
    }

    @Override
    public User addUser(String id) throws UserNotFoundException, CustomUserException {
        if (!userExistInFirestore(id)) {
            try {
                return Optional.ofNullable(this.setUser(id, null))
                        .orElseThrow(() -> new UserNotFoundException("User not found in the authentication system"));
            } catch (DeleteUserException e) {
                throw new CustomUserException("Failed to delete unidentified user");
            } catch (CustomUserException e) {
                throw new CustomUserException("Failed to create new user. Please try again later");
            }
        }
        throw new CustomUserException("User already exists in the database");
    }

    @Override
    public User updateCurrency(PutUserCurrencyBin currencyBin) throws UserNotFoundException, CustomUserException {
        Optional<User> user = Optional.ofNullable(this.getUser(currencyBin.getUserID()));
        if (user.isPresent()) {
            user.get().setFavouriteCurrency(currencyBin.getCurrency());
            try {
                return this.setUser(currencyBin.getUserID(), user.get());
            } catch (CustomUserException | DeleteUserException e) {
                throw new CustomUserException("Unable to update user due to: " + e.getMessage());
            }
        }
        throw new UserNotFoundException("User not found with id: " + currencyBin.getUserID());
    }

}
