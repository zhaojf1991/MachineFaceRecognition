package com.minivision.machinefacerecognition.activity.dailydb;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.minivision.machinefacerecognition.activity.entity.PersonDaily;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PERSON_DAILY".
*/
public class PersonDailyDao extends AbstractDao<PersonDaily, Long> {

    public static final String TABLENAME = "PERSON_DAILY";

    /**
     * Properties of entity PersonDaily.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _dailyid = new Property(0, long.class, "_dailyid", true, "_id");
        public final static Property TakePicPath = new Property(1, String.class, "takePicPath", false, "TAKE_PIC_PATH");
        public final static Property Pic = new Property(2, String.class, "pic", false, "PIC");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Num = new Property(4, String.class, "num", false, "NUM");
        public final static Property TakeTime = new Property(5, String.class, "takeTime", false, "TAKE_TIME");
    }


    public PersonDailyDao(DaoConfig config) {
        super(config);
    }
    
    public PersonDailyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PERSON_DAILY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," + // 0: _dailyid
                "\"TAKE_PIC_PATH\" TEXT," + // 1: takePicPath
                "\"PIC\" TEXT," + // 2: pic
                "\"NAME\" TEXT," + // 3: name
                "\"NUM\" TEXT," + // 4: num
                "\"TAKE_TIME\" TEXT);"); // 5: takeTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PERSON_DAILY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PersonDaily entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.get_dailyid());
 
        String takePicPath = entity.getTakePicPath();
        if (takePicPath != null) {
            stmt.bindString(2, takePicPath);
        }
 
        String pic = entity.getPic();
        if (pic != null) {
            stmt.bindString(3, pic);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String num = entity.getNum();
        if (num != null) {
            stmt.bindString(5, num);
        }
 
        String takeTime = entity.getTakeTime();
        if (takeTime != null) {
            stmt.bindString(6, takeTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PersonDaily entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.get_dailyid());
 
        String takePicPath = entity.getTakePicPath();
        if (takePicPath != null) {
            stmt.bindString(2, takePicPath);
        }
 
        String pic = entity.getPic();
        if (pic != null) {
            stmt.bindString(3, pic);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String num = entity.getNum();
        if (num != null) {
            stmt.bindString(5, num);
        }
 
        String takeTime = entity.getTakeTime();
        if (takeTime != null) {
            stmt.bindString(6, takeTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public PersonDaily readEntity(Cursor cursor, int offset) {
        PersonDaily entity = new PersonDaily( //
            cursor.getLong(offset + 0), // _dailyid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // takePicPath
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // pic
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // num
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // takeTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PersonDaily entity, int offset) {
        entity.set_dailyid(cursor.getLong(offset + 0));
        entity.setTakePicPath(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPic(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNum(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTakeTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PersonDaily entity, long rowId) {
        entity.set_dailyid(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PersonDaily entity) {
        if(entity != null) {
            return entity.get_dailyid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PersonDaily entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
