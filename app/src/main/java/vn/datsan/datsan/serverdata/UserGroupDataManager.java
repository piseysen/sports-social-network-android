package vn.datsan.datsan.serverdata;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.datsan.datsan.models.Group;
import vn.datsan.datsan.utils.listeners.FirebaseChildEventListener;

/**
 * Created by xuanpham on 7/29/16.
 */
public class UserGroupDataManager {
    private static final String TAG = FieldDataManager.class.getName();
    private static UserGroupDataManager instance;
    private DatabaseReference groupDatabaseRef = FirebaseDatabase.getInstance().getReference("app/usergroups");
    private List<Group> userGroups;

    public UserGroupDataManager() {
        groupDatabaseRef.addChildEventListener(new FirebaseChildEventListener(Group.class));
    }

    public static UserGroupDataManager getInstance() {
        if (instance == null) {
            instance = new UserGroupDataManager();
        }
        return instance;
    }

    public void addGroup(Group group) {
        String key = groupDatabaseRef.push().getKey();
        group.setId(key);
        groupDatabaseRef.child(key).setValue(group);
    }

    public List<Group> getUserGroups(final CallBack.OnResultReceivedListener callBack) {

        if (userGroups != null && !userGroups.isEmpty()) {
            if (callBack != null)
                callBack.onResultReceived(userGroups);
            return userGroups;
        }

        groupDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userGroups = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Group field = postSnapshot.getValue(Group.class);
                    if (field != null)
                        userGroups.add(field);
                }
                if (callBack != null)
                    callBack.onResultReceived(userGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return userGroups;
    }
}