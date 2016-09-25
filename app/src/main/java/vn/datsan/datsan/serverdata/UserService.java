package vn.datsan.datsan.serverdata;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.datsan.datsan.models.BaseDto;
import vn.datsan.datsan.models.User;
import vn.datsan.datsan.utils.AppConstants;
import vn.datsan.datsan.utils.AppLog;

/**
 * Created by xuanpham on 6/20/16.
 */
public class UserService {
    private static final String TAG = UserService.class.getName();
    private static UserService instance;

    private DatabaseReference userDatabaseRef;
    private User userInfo;
    private User currentUser;

    private UserService() {
        userDatabaseRef = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_USERS);
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void addUser(User user, DatabaseReference.CompletionListener listener) {
        userDatabaseRef.child(user.getId()).setValue(user, listener);
    }

    public void updateUser(User user, DatabaseReference.CompletionListener listener) {
        userDatabaseRef.child(user.getId()).setValue(user, listener);
    }

    public void addUserToGroup(BaseDto group, DatabaseReference.CompletionListener listener) {
        if (userInfo.getGroups() == null) {
            userInfo.setGroups(new ArrayList<BaseDto>());
        }
        userInfo.getGroups().add(group);
        userDatabaseRef.child(userInfo.getId()).setValue(userInfo, listener);
    }

    public void getCurrentUserInfo(CallBack.OnResultReceivedListener callBack) {
        getUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(), callBack);
    }

    public void getUserInfo(String id, final CallBack.OnResultReceivedListener callBack) {
        AppLog.log(AppLog.LogType.LOG_ERROR, TAG, "Get for " + id);
        userDatabaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (callBack != null)
                    callBack.onResultReceived(user);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                AppLog.log(AppLog.LogType.LOG_DEBUG, TAG, firebaseError.getMessage());
                if (callBack != null)
                    callBack.onResultReceived(null);
            }
        });
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Get current logged user
     *
     * @return
     */
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get Chat history of the current user
     * @return
     */
    public DatabaseReference getCurrentUserChatDatabaseRef() {
        return userDatabaseRef.child(currentUser.getId()).child("chats");
    }

    /**
     * Get Chat list of a user
     * @return
     */
    public DatabaseReference getUserChatDatabaseRef(String userId) {
        return userDatabaseRef.child(userId).child("chats");
    }

    public DatabaseReference getUserDatabaseRef() {
        return userDatabaseRef;
    }

    public DatabaseReference getCurrentUserChatDatabaseRef(String chatId) {
        return userDatabaseRef.child(getCurrentUser().getId()).child("chats").child(chatId);
    }
}