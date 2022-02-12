package com.henry.criminalintent;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.henry.criminalintent.CrimeDbSchema.CrimeTable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //編寫SQL 創建初始Table代碼
        db.execSQL("create table "+ CrimeTable.NAME+"("+
                "_id integer primary key autoincrement, "+
                CrimeTable.Cols.UUID+","+
                CrimeTable.Cols.TITLE+","+
                CrimeTable.Cols.DATE+","+
                CrimeTable.Cols.SOLVED+","+
                CrimeTable.Cols.SUSPECT+
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
