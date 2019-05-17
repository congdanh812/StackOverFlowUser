package dc.danh.sofuser.model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = SOFUser.class, version = 1, exportSchema = false)
public abstract class SOFUserDatabase extends RoomDatabase {
    public abstract SOFUserDAO sofUserDAO();
}
