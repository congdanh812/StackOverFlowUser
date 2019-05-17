package dc.danh.sofuser.model.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SOFUserDAO {
    @Query("SELECT * FROM SOFUser")
    List<SOFUser> getAllSOFUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SOFUser... sofUser);

    @Query("DELETE from SOFUser where userID = :userID")
    void deleteSOFUser(int userID);
}
