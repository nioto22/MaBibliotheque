package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.SocialUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.Nullable;

public class SocialUserHelper {

    private static final String COLLECTION_NAME = "socialUsers";
    private static final String FRIENDS_COLLECTION_NAME = "friends";
    private static final String POSTS_COLLECTION_NAME = "posts";
    private static final String COMMENTS_COLLECTION_NAME = "comments";
    private static final String REQUESTS_TO_COLLECTION_NAME = "requestsTo";
    private static final String REQUESTS_FROM_COLLECTION_NAME = "requestsFrom";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getSocialUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    // Use CommonUserHelper
//    public static Task<Void> createSocialUser(String uid, String username, @Nullable String urlPicture) {
//        SocialUser socialUserToCreate = new SocialUser(uid, username, urlPicture);
//        return SocialUserHelper.getSocialUsersCollection().document(uid).set(socialUserToCreate);
//    }

    // --- GET ---

    public static Task<DocumentSnapshot> getSocialUser(String uid){
        return SocialUserHelper.getSocialUsersCollection().document(uid).get();
    }

    public static CollectionReference getAllFriendsForUser(String userUid){
        return SocialUserHelper.getSocialUsersCollection()
                .document(userUid)
                .collection(FRIENDS_COLLECTION_NAME);
    }

    public static CollectionReference getAllPostsForUser(String userUid){
        return SocialUserHelper.getSocialUsersCollection()
                .document(userUid)
                .collection(POSTS_COLLECTION_NAME);
    }
    public static CollectionReference getAllCommentsForUser(String userUid){
        return SocialUserHelper.getSocialUsersCollection()
                .document(userUid)
                .collection(COMMENTS_COLLECTION_NAME);
    }
    public static CollectionReference getAllRequestToForUser(String userUid){
        return SocialUserHelper.getSocialUsersCollection()
                .document(userUid)
                .collection(REQUESTS_TO_COLLECTION_NAME);
    }
    public static CollectionReference getAllRequestFromForUser(String userUid){
        return SocialUserHelper.getSocialUsersCollection()
                .document(userUid)
                .collection(REQUESTS_FROM_COLLECTION_NAME);
    }

    // --- UPDATE ---

    // Use CommonUserHelper
//    public static Task<Void> updateSocialUsername(String username, String uid) {
//        return SocialUserHelper.getSocialUsersCollection()
//                .document(uid)
//                .update("username", username);
//    }
    // Use CommonUserHelper
//    public static Task<Void> updatePublicState(String uid, Boolean isAPublicUser) {
//        return SocialUserHelper.getSocialUsersCollection()
//                .document(uid)
//                .update("isAPublicUser", isAPublicUser);
//    }
    // Use CommonUserHelper
//    public static Task<Void> updateSocialUserPicture(String uid, String urlPicture) {
//        return SocialUserHelper.getSocialUsersCollection()
//                .document(uid)
//                .update("urlPicture", urlPicture);
//    }

    public static Task<Void> updateAdviserLevel(String uid, int level) {
        return SocialUserHelper.getSocialUsersCollection()
                .document(uid)
                .update("adviserLevel", level);
    }

    // --- DELETE ---

    // Use CommonUserHelper
//    public static Task<Void> deleteSocialUser(String uid) {
//        return SocialUserHelper.getSocialUsersCollection()
//                .document(uid)
//                .delete();
//    }

}

