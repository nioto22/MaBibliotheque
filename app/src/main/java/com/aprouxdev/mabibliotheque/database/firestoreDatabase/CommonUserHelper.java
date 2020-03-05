package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.SocialUser;
import com.aprouxdev.mabibliotheque.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import androidx.annotation.Nullable;

public class CommonUserHelper {

    // --- COLLECTION REFERENCE ---
    public static DocumentReference getUserDocumentReference(String uid){
      return  UserHelper.getUsersCollection().document(uid);
    }
    public static DocumentReference getSocialUserDocumentReference(String uid){
        return  SocialUserHelper.getSocialUsersCollection().document(uid);
    }

    // --- CREATE ---
    public static WriteBatch createCommonUser(String uid, Boolean isSignedInUser, String email, String username, @Nullable String urlPicture){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        User userToCreate = new User(uid, username, isSignedInUser, email, urlPicture);
        DocumentReference userDocument = getUserDocumentReference(uid);
        batch.set(userDocument, userToCreate);

        SocialUser socialUserToCreate = new SocialUser(uid, username, urlPicture);
        DocumentReference socialUserDocument = getSocialUserDocumentReference(uid);
        batch.set(socialUserDocument, socialUserToCreate);

        return batch; // To use .commit.addOncComplete
    }

    // --- UPDATE ---
    public static WriteBatch updateCommonUserUsername(String uid, String username){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference userDocument = getUserDocumentReference(uid);
        DocumentReference socialUserDocument = getSocialUserDocumentReference(uid);

        batch.update(userDocument, "username", username);
        batch.update(socialUserDocument, "username", username);

        return batch;
    }

    public static WriteBatch updateCommonUserPicture(String uid, String pictureUrl){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference userDocument = getUserDocumentReference(uid);
        DocumentReference socialUserDocument = getSocialUserDocumentReference(uid);

        batch.update(userDocument, "urlPicture", pictureUrl);
        batch.update(socialUserDocument, "urlPicture", pictureUrl);

        return batch;
    }

    public static WriteBatch updateCommonUserPublicState(String uid, Boolean isAPublicUser){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference userDocument = getUserDocumentReference(uid);
        DocumentReference socialUserDocument = getSocialUserDocumentReference(uid);

        batch.update(userDocument, "isAPublicUser", isAPublicUser);
        batch.update(socialUserDocument, "isAPublicUser", isAPublicUser);

        return batch;
    }

    // --- DELETE ---
    public static WriteBatch deleteCommonUser(String uid){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference userDocument = getUserDocumentReference(uid);
        DocumentReference socialUserDocument = getSocialUserDocumentReference(uid);

        batch.delete(userDocument);
        batch.delete(socialUserDocument);

        return batch;
    }

}
