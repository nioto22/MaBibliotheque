package com.aprouxdev.mabibliotheque.database.firestoreDatabase;


import com.aprouxdev.mabibliotheque.models.Message;
import com.aprouxdev.mabibliotheque.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class MessageHelper {

    public static final String MESSAGES_COLLECTION_NAME = "messages";

    public static Task<DocumentReference> createMessageForDiscussion(String uid, String textMessage, String discussionUid, User userSender){

        // 1 - Create the Message object
        Message message = new Message(uid, textMessage, userSender);

        // 2 - Store Message to Firestore
        return DiscussionHelper.getDiscussionsCollection()
                .document(discussionUid)
                .collection(MESSAGES_COLLECTION_NAME)
                .add(message);
    }

    public static Task<DocumentReference> createMessageForDiscussion(String uid, String textMessage, String bookId, String bookImage, String discussionUid, User userSender){

        // 1 - Create the Message object
        Message message = new Message(uid, textMessage, bookId, bookImage, userSender);

        // 2 - Store Message to Firestore
        return DiscussionHelper.getDiscussionsCollection()
                .document(discussionUid)
                .collection(MESSAGES_COLLECTION_NAME)
                .add(message);
    }


}
