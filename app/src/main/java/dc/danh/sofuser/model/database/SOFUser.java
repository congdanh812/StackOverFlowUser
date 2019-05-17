package dc.danh.sofuser.model.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "SOFUser")
public class SOFUser {
    @PrimaryKey
    private int userID;

    @ColumnInfo(name = "userDetail")
    private String userDetail;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(String userDetail) {
        this.userDetail = userDetail;
    }
}
