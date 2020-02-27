package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, Boolean isSignedInUser, String email) {
        User userToCreate = new User(uid, isSignedInUser, email);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsSignedInUser(String uid, Boolean isSignedInUser) {
        return UserHelper.getUsersCollection().document(uid).update("isSignedInUser", isSignedInUser);
    }

    public static Task<Void> updateUserPicture(String uid, String urlPicture) {
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", urlPicture);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
