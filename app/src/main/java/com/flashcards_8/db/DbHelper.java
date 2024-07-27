package com.flashcards_8.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.flashcards_8.Utilidades.Utilidades;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context) {
        super(context, Utilidades.DATABASE_NAME, null, Utilidades.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_MAESTRO);
        db.execSQL(Utilidades.CREAR_TABLA_ALUMNOS);
        db.execSQL(Utilidades.CREAR_TABLA_PALABRAS);
        db.execSQL(Utilidades.CREAR_TABLA_ESTADISTICAS_ALUMNO);
        db.execSQL(Utilidades.CREAR_TABLA_ESTADISTICAS_PALABRA);
        db.execSQL(Utilidades.CREAR_TABLA_SESION_IDS);
        db.execSQL(Utilidades.CREAR_TABLA_DATOS_PALABRA_SESION);
        db.execSQL(Utilidades.CREAR_TABLA_SESION_PRACTICA);
        db.execSQL(Utilidades.CREAR_TABLA_SESION_PRUEBA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 11) {
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_ALUMNOS);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_MAESTRO);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_PALABRAS);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_ESTADISTICAS_ALUMNO);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_ESTADISTICAS_PALABRA);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_SESION_IDS);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_DATOS_PALABRA_SESION);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_SESION_PRACTICA);
            db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLE_SESION_PRUEBA);
            onCreate(db);
        }
    }
}
