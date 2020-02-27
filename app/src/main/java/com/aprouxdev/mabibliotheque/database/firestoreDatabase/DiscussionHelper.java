package com.aprouxdev.mabibliotheque.database.firestoreDatabase;

import com.aprouxdev.mabibliotheque.models.Discussion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class DiscussionHelper {
    
    public static final String DISCUSSIONS_COLLECTION_NAME = "discussions";
    public static final String MESSAGES_COLLECTION_NAME = "messages";


    // --- COLLECTION REFERENCE ---

    public static CollectionReference getDiscussionsCollection(){
        return FirebaseFirestore.getInstance().collection(DISCUSSIONS_COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createDiscussion(String uid, String name, List<String> usersUid) {
        Discussion discussionToCreate = new Discussion(uid, name, usersUid);
        return DiscussionHelper.getDiscussionsCollection().document(uid).set(discussionToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getDiscussion(String uid){
        return DiscussionHelper.getDiscussionsCollection().document(uid).get();
    }

    public static Query getAllMessagesForDiscussion(String discussionUid){
        return DiscussionHelper.getDiscussionsCollection()
                .document(discussionUid)
                .collection(MESSAGES_COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- UPDATE ---

    public static Task<Void> updateDiscussionName(String DiscussionName, String uid) {
        return DiscussionHelper.getDiscussionsCollection().document(uid).update("name", DiscussionName);
    }

    public static Task<Void> updateUsers(String uid, List<String> users) {
        return DiscussionHelper.getDiscussionsCollection().document(uid).update("usersUid", users);
    }

    // --- DELETE ---

    public static Task<Void> deleteDiscussion(String uid) {
        return DiscussionHelper.getDiscussionsCollection().document(uid).delete();
    }
    
}
