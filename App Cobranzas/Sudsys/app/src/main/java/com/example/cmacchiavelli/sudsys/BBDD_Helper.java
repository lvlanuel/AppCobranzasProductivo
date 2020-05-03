package com.example.cmacchiavelli.sudsys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BBDD_Helper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1; //No confundir con el nro de seliarizacion
    public static final String DATABASE_NAME = "App_Datos_Recibos_cris.db";//nombre de la base de datos

    public BBDD_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Estructura_BBDD.SQL_CREATE_ENTRIES);
        db.execSQL(Estructura_BBDD_Recibos.SQL_CREATE_ENTRIES);
        db.execSQL(Estructura_BBDD_Bancos.SQL_CREATE_ENTRIES);
        db.execSQL(Estructura_BBDD_Cuotas_Canceladas.SQL_CREATE_ENTRIES);
        db.execSQL(Estructura_BBDD_Pre_Cuotas_Canceladas.SQL_CREATE_ENTRIES);
        db.execSQL(Estructura_BBDD_Recibos_Pagados.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Estructura_BBDD.SQL_DELETE_ENTRIES);
        db.execSQL(Estructura_BBDD_Recibos.SQL_DELETE_ENTRIES);
        db.execSQL(Estructura_BBDD_Bancos.SQL_DELETE_ENTRIES);
        db.execSQL(Estructura_BBDD_Cuotas_Canceladas.SQL_DELETE_ENTRIES);
        db.execSQL(Estructura_BBDD_Pre_Cuotas_Canceladas.SQL_DELETE_ENTRIES);
        db.execSQL(Estructura_BBDD_Recibos_Pagados.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
