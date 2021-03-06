package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.Nullable;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    // Use CommonUserHelper
//    public static Task<Void> createUser(String uid, Boolean isSignedInUser, String email, String username, @Nullable String urlPicture) {
//        User userToCreate = new User(uid, username, isSignedInUser, email, urlPicture);
//        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
//    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }


    public static Query getUserByMail(String email){
        return UserHelper.getUsersCollection()
                .whereEqualTo("email", email);
    }

    public static Query getUserByUsername(String username){
        return UserHelper.getUsersCollection()
                .whereEqualTo("username", username);
    }



    // --- UPDATE ---

    // Use CommonUserHelper
//    public static Task<Void> updateUsername(String username, String uid) {
//        return UserHelper.getUsersCollection().document(uid).update("username", username);
//    }

    public static Task<Void> updateIsSignedInUser(String uid, Boolean isSignedInUser) {
        return UserHelper.getUsersCollection().document(uid).update("isSignedInUser", isSignedInUser);
    }

    // Use CommonUserHelper
//    public static Task<Void> updateUserPicture(String uid, String urlPicture) {
//        return UserHelper.getUsersCollection().document(uid).update("urlPicture", urlPicture);
//    }
//
//    public static Task<Void> updatePublicState(String uid, Boolean isAPublicUser){
//        return UserHelper.getUsersCollection().document(uid).update("isAPublicUser", isAPublicUser);
//    }

    // --- DELETE ---

    // Use CommonUserHelper
//    public static Task<Void> deleteUser(String uid) {
//        return UserHelper.getUsersCollection().document(uid).delete();
//    }

}
