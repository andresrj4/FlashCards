package com.flashcards_8.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.flashcards_8.Entidades.EstadisticasAlumno;
import com.flashcards_8.Entidades.EstadisticasPalabra;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.Entidades.Sesion;
import com.flashcards_8.Utilidades.Utilidades;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public DbManager(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public SQLiteDatabase getReadableDatabaseInstance() {
        return dbHelper.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabaseInstance() {
        return dbHelper.getWritableDatabase();
    }

    // Agrega una palabra nueva (funcionalidad pendiente)
    public long insertarPalabra(String textoPalabra, byte[] imagenPalabra, byte[] audioPalabra, int nivelPalabra) {
        open();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_TEXTO_PALABRA, textoPalabra);
            values.put(Utilidades.CAMPO_IMAGEN, imagenPalabra);
            values.put(Utilidades.CAMPO_AUDIO, audioPalabra);
            values.put(Utilidades.CAMPO_NIVEL_PALABRA, nivelPalabra);
            result = db.insert(Utilidades.TABLE_PALABRAS, null, values);
        } finally {
            close();
        }
        return result;
    }

    // Inserta palabras del "GeneradorDePalabras" a una lista
    public void insertarMultiplesPalabras(List<Palabra> palabras) {
        open();
        try {
            db.beginTransaction();
            for (Palabra palabra : palabras) {
                ContentValues values = new ContentValues();
                values.put(Utilidades.CAMPO_TEXTO_PALABRA, palabra.getTextoPalabra());
                values.put(Utilidades.CAMPO_IMAGEN, palabra.getImagenPalabra());
                values.put(Utilidades.CAMPO_AUDIO, palabra.getAudioPalabra());
                values.put(Utilidades.CAMPO_NIVEL_PALABRA, palabra.getNivelPalabra());
                db.insert(Utilidades.TABLE_PALABRAS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        close();
    }

    // Registra una sesion modo practica (o Flashcards)
    public long registrarSesionPractica(int idAlumno, int idMaestro, String fecha, int duracion, int nivel, int intervalo) {
        open();
        long sessionId = -1;
        try {
            db.beginTransaction();
            ContentValues sessionValues = new ContentValues();
            sessionValues.put(Utilidades.CAMPO_TIPO_SESION, "Practica");
            sessionId = db.insert(Utilidades.TABLE_SESION_IDS, null, sessionValues);

            if (sessionId != -1) {
                ContentValues values = new ContentValues();
                values.put(Utilidades.CAMPO_ID_SESION, sessionId);
                values.put(Utilidades.CAMPO_ID_ALUMNO, idAlumno);
                values.put(Utilidades.CAMPO_ID_MAESTRO, idMaestro);
                values.put(Utilidades.CAMPO_FECHA, fecha);
                values.put(Utilidades.CAMPO_DURACION_SESION, duracion);
                values.put(Utilidades.CAMPO_NIVEL_SESION, nivel);
                values.put(Utilidades.CAMPO_INTERVALO_SESION, intervalo);
                db.insert(Utilidades.TABLE_SESION_PRACTICA, null, values);
                db.setTransactionSuccessful();
            } else {
                Log.d("DbManager", "Error inserting session ID in TABLE_SESION_IDS.");
            }
        } finally {
            db.endTransaction();
        }
        close();
        return sessionId;
    }

    // Registra una sesion modo prueba
    public long registrarSesionPrueba(int idAlumno, int idMaestro, String fecha, int duracion, int nivel, int aciertos, int fallos, String calificacion) {
        open();
        long sessionId = -1;
        try {
            db.beginTransaction();
            ContentValues sessionValues = new ContentValues();
            sessionValues.put(Utilidades.CAMPO_TIPO_SESION, "Prueba");
            sessionId = db.insert(Utilidades.TABLE_SESION_IDS, null, sessionValues);

            if (sessionId != -1) {
                ContentValues values = new ContentValues();
                values.put(Utilidades.CAMPO_ID_SESION, sessionId);
                values.put(Utilidades.CAMPO_ID_ALUMNO, idAlumno);
                values.put(Utilidades.CAMPO_ID_MAESTRO, idMaestro);
                values.put(Utilidades.CAMPO_FECHA, fecha);
                values.put(Utilidades.CAMPO_DURACION_SESION, duracion);
                values.put(Utilidades.CAMPO_NIVEL_SESION, nivel);
                values.put(Utilidades.CAMPO_ACIERTOS_PALABRA_SESION, aciertos);
                values.put(Utilidades.CAMPO_FALLOS_PALABRA_SESION, fallos);
                values.put(Utilidades.CAMPO_PUNTAJE_SESION, calificacion);
                db.insert(Utilidades.TABLE_SESION_PRUEBA, null, values);
                db.setTransactionSuccessful();
            } else {
                Log.d("DbManager", "Error inserting session ID in TABLE_SESION_IDS.");
            }
        } finally {
            db.endTransaction();
        }
        close();
        return sessionId;
    }

    // Actualiza (o inserta si no existe la entrada de dicha palabra) los datos de las palabras tratadas de una sesion.
    public long insertarOActualizarDatosPalabraSesion(int idSesion, int idPalabra, int vecesVista, int tiempoVista, int vecesEscuchada, int aciertos, int fallos) {
        open();
        long result = -1;
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_DATOS_PALABRA_SESION + " WHERE " + Utilidades.CAMPO_ID_SESION + "=? AND " + Utilidades.CAMPO_ID_PALABRA + "=?", new String[]{String.valueOf(idSesion), String.valueOf(idPalabra)})) {
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_VECES_VISTA_PALABRA_SESION, vecesVista);
            values.put(Utilidades.CAMPO_TIEMPO_VISTA_PALABRA_SESION, tiempoVista);
            values.put(Utilidades.CAMPO_VECES_ESCUCHADA_PALABRA_SESION, vecesEscuchada);
            values.put(Utilidades.CAMPO_ACIERTOS_PALABRA_SESION, aciertos);
            values.put(Utilidades.CAMPO_FALLOS_PALABRA_SESION, fallos);

            if (cursor.moveToFirst()) {
                String whereClause = Utilidades.CAMPO_ID_SESION + "=? AND " + Utilidades.CAMPO_ID_PALABRA + "=?";
                String[] whereArgs = {String.valueOf(idSesion), String.valueOf(idPalabra)};
                result = db.update(Utilidades.TABLE_DATOS_PALABRA_SESION, values, whereClause, whereArgs);
            } else {
                values.put(Utilidades.CAMPO_ID_SESION, idSesion);
                values.put(Utilidades.CAMPO_ID_PALABRA, idPalabra);
                result = db.insert(Utilidades.TABLE_DATOS_PALABRA_SESION, null, values);
            }
        }
        close();
        return result;
    }

    // Recupera los campos de la tabla ESTADISTICAS_PALABRA para su actualizacion en el cierre de sesion
    public List<EstadisticasPalabra> obtenerEstadisticasPalabras(int idAlumno) {
        List<EstadisticasPalabra> estadisticasPalabras = new ArrayList<>();
        open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_ESTADISTICAS_PALABRA + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + "=?", new String[]{String.valueOf(idAlumno)});

        while (cursor.moveToNext()) {
            EstadisticasPalabra estadistica = new EstadisticasPalabra(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_TOTAL_VISTA_PALABRA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA))
            );
            estadisticasPalabras.add(estadistica);
        }
        cursor.close();
        close();
        return estadisticasPalabras;
    }

    // Recupera los datos generales de todas las sesiones practica (o Flashcards) registradas bajo la ID del alumno
    public List<Sesion> obtenerSesionesPractica(int idAlumno) {
        List<Sesion> listaSesiones = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_SESION_PRACTICA + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + " = ?", new String[]{String.valueOf(idAlumno)});

        while (cursor.moveToNext()) {
            Sesion sesion = new Sesion();
            sesion.setIdSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_SESION)));
            sesion.setFechaSesion(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FECHA)));
            sesion.setNivelSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_NIVEL_SESION)));
            sesion.setTipoSesion("Practica");
            sesion.setDuracionSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_DURACION_SESION)));
            sesion.setIntervaloSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_INTERVALO_SESION)));
            listaSesiones.add(sesion);
        }
        cursor.close();
        return listaSesiones;
    }

    // Recupera los datos generales de todas las sesiones prueba registradas bajo la ID del alumno
    public List<Sesion> obtenerSesionesPrueba(int idAlumno) {
        List<Sesion> listaSesiones = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabaseInstance();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_SESION_PRUEBA + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + " = ?", new String[]{String.valueOf(idAlumno)});

        while (cursor.moveToNext()) {
            Sesion sesion = new Sesion();
            sesion.setIdSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_SESION)));
            sesion.setFechaSesion(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FECHA)));
            sesion.setNivelSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_NIVEL_SESION)));
            sesion.setTipoSesion("Prueba");
            sesion.setDuracionSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_DURACION_SESION)));
            sesion.setAciertosSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ACIERTOS_PALABRA_SESION)));
            sesion.setFallosSesion(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FALLOS_PALABRA_SESION)));
            sesion.setCalificacionSesion(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_PUNTAJE_SESION)));
            listaSesiones.add(sesion);
        }
        cursor.close();
        return listaSesiones;
    }

    // Regresa los datos de las palabras vistas en una sesion modo practica (o Flashcards) en especifico
    public List<Palabra> obtenerPalabrasVistas(int idSesion) {
        List<Palabra> palabras = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabaseInstance();
        Cursor cursor = db.rawQuery(
                "SELECT dps.*, p." + Utilidades.CAMPO_TEXTO_PALABRA +
                        " FROM " + Utilidades.TABLE_DATOS_PALABRA_SESION + " dps" +
                        " INNER JOIN " + Utilidades.TABLE_PALABRAS + " p ON dps." + Utilidades.CAMPO_ID_PALABRA + " = p." + Utilidades.CAMPO_ID_PALABRA +
                        " WHERE dps." + Utilidades.CAMPO_ID_SESION + " = ?",
                new String[]{String.valueOf(idSesion)}
        );
        while (cursor.moveToNext()) {
            Palabra palabra = new Palabra();
            palabra.setIdPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)));
            palabra.setTextoPalabra(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA)));
            palabra.setVecesVistaPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_VISTA_PALABRA_SESION)));
            palabra.setTiempoVista(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_VISTA_PALABRA_SESION)));
            palabra.setVecesEscuchada(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_ESCUCHADA_PALABRA_SESION)));
            palabras.add(palabra);
        }
        cursor.close();
        return palabras;
    }

    // Regresa los datos de las palabras evaluadas en una sesion modo prueba en especifico
    public List<Palabra> obtenerPalabrasEvaluadas(int idSesion) {
        List<Palabra> palabras = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabaseInstance();
        Cursor cursor = db.rawQuery(
                "SELECT dps.*, p." + Utilidades.CAMPO_TEXTO_PALABRA +
                        " FROM " + Utilidades.TABLE_DATOS_PALABRA_SESION + " dps" +
                        " INNER JOIN " + Utilidades.TABLE_PALABRAS + " p ON dps." + Utilidades.CAMPO_ID_PALABRA + " = p." + Utilidades.CAMPO_ID_PALABRA +
                        " WHERE dps." + Utilidades.CAMPO_ID_SESION + " = ?",
                new String[]{String.valueOf(idSesion)}
        );
        while (cursor.moveToNext()) {
            Palabra palabra = new Palabra();
            palabra.setIdPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)));
            palabra.setTextoPalabra(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA)));
            palabra.setVecesVistaPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_VISTA_PALABRA_SESION)));
            palabra.setTiempoVista(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_VISTA_PALABRA_SESION))); // Tiempo en milisegundos
            palabra.setVecesEscuchada(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_ESCUCHADA_PALABRA_SESION)));
            palabra.setResultado(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ACIERTOS_PALABRA_SESION)) > 0);
            palabras.add(palabra);
        }
        cursor.close();
        return palabras;
    }

    // Actualiza datos generales de una sesion modo practica (o Flashcards)
    public void actualizarSesionPractica(int idSesion, String fecha, int duracion) {
        open();
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_FECHA, fecha);
        values.put(Utilidades.CAMPO_DURACION_SESION, duracion);

        String whereClause = Utilidades.CAMPO_ID_SESION + "=?";
        String[] whereArgs = {String.valueOf(idSesion)};
        db.update(Utilidades.TABLE_SESION_PRACTICA, values, whereClause, whereArgs);
        close();
    }

    // Actualiza datos generales de una sesion modo prueba
    public void actualizarSesionPrueba(int idSesion, String fecha, int duracion, int aciertos, int fallos, int calificacion) {
        open();
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_FECHA, fecha);
        values.put(Utilidades.CAMPO_DURACION_SESION, duracion);
        values.put(Utilidades.CAMPO_ACIERTOS_PALABRA_SESION, aciertos);
        values.put(Utilidades.CAMPO_FALLOS_PALABRA_SESION, fallos);
        values.put(Utilidades.CAMPO_PUNTAJE_SESION, calificacion);

        String whereClause = Utilidades.CAMPO_ID_SESION + "=?";
        String[] whereArgs = {String.valueOf(idSesion)};
        db.update(Utilidades.TABLE_SESION_PRUEBA, values, whereClause, whereArgs);
        close();
    }

    // Ver metodo "insertarOActualizarEstadisticasPalabra"
    public void actualizarEstadisticasPalabra(int idAlumno, int idPalabra, ContentValues values) {
        String whereClause = Utilidades.CAMPO_ID_ALUMNO + "=? AND " + Utilidades.CAMPO_ID_PALABRA + "=?";
        String[] whereArgs = {String.valueOf(idAlumno), String.valueOf(idPalabra)};
        db.update(Utilidades.TABLE_ESTADISTICAS_PALABRA, values, whereClause, whereArgs);
    }

    // Actualiza (o crea la entrada si es que no existe) los datos de la palabra de la tabla ESTADISTICAS_PALABRA
    public void insertarOActualizarEstadisticasPalabra(int idAlumno, int idPalabra, int vecesVista, int aciertos, int fallos, int tiempoVista, int vecesEscuchada) {
        open();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_ESTADISTICAS_PALABRA + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + "=? AND " + Utilidades.CAMPO_ID_PALABRA + "=?", new String[]{String.valueOf(idAlumno), String.valueOf(idPalabra)})) {
            ContentValues values = new ContentValues();
            if (cursor.moveToFirst()) {
                // Obtener los valores actuales
                int currentVecesVista = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA));
                int currentAciertos = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA));
                int currentFallos = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA));
                int currentTiempoVista = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_TOTAL_VISTA_PALABRA));
                int currentVecesEscuchada = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA));

                // Sumar los nuevos valores a los actuales usando métodos de EstadisticasPalabra
                values.put(Utilidades.CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA, currentVecesVista + vecesVista);
                values.put(Utilidades.CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA, currentAciertos + aciertos);
                values.put(Utilidades.CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA, currentFallos + fallos);
                values.put(Utilidades.CAMPO_TIEMPO_TOTAL_VISTA_PALABRA, currentTiempoVista + tiempoVista);
                values.put(Utilidades.CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA, currentVecesEscuchada + vecesEscuchada);

                // Actualizar los valores
                actualizarEstadisticasPalabra(idAlumno, idPalabra, values);
            } else {
                values.put(Utilidades.CAMPO_ID_ALUMNO, idAlumno);
                values.put(Utilidades.CAMPO_ID_PALABRA, idPalabra);
                values.put(Utilidades.CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA, vecesVista);
                values.put(Utilidades.CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA, aciertos);
                values.put(Utilidades.CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA, fallos);
                values.put(Utilidades.CAMPO_TIEMPO_TOTAL_VISTA_PALABRA, tiempoVista);
                values.put(Utilidades.CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA, vecesEscuchada);
                db.insert(Utilidades.TABLE_ESTADISTICAS_PALABRA, null, values);
            }
        }
        close();
    }

    // Recupera todos los datos de todas las palabras de la tabla ESTADISTICAS_PALABRA
    public List<EstadisticasPalabra> obtenerTodasLasEstadisticasDePalabras() {
        List<EstadisticasPalabra> estadisticasPalabraList = new ArrayList<>();
        open();
        Cursor cursor = db.rawQuery("SELECT ep.*, p." + Utilidades.CAMPO_TEXTO_PALABRA + ", p." + Utilidades.CAMPO_NIVEL_PALABRA +
                " FROM " + Utilidades.TABLE_ESTADISTICAS_PALABRA + " ep" +
                " JOIN " + Utilidades.TABLE_PALABRAS + " p ON ep." + Utilidades.CAMPO_ID_PALABRA + " = p." + Utilidades.CAMPO_ID_PALABRA, null);

        while (cursor.moveToNext()) {
            int idPalabra = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA));
            int vecesVista = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA));
            int aciertos = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA));
            int fallos = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA));
            int tiempoVista = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_TOTAL_VISTA_PALABRA));
            int vecesEscuchada = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA));
            String textoPalabra = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA));
            int nivelPalabra = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_NIVEL_PALABRA));

            EstadisticasPalabra estadistica = new EstadisticasPalabra(idPalabra, vecesVista, aciertos, fallos, tiempoVista, vecesEscuchada);
            estadistica.setTextoPalabra(textoPalabra);
            estadistica.setNivelPalabra(nivelPalabra);

            estadisticasPalabraList.add(estadistica);
        }
        cursor.close();
        close();
        return estadisticasPalabraList;
    }

    // Ver metodo "insertarOActualizarEstadisticasAlumno"
    private void actualizarEstadisticasAlumno(int idAlumno, ContentValues values) {
        String whereClause = Utilidades.CAMPO_ID_ALUMNO + "=?";
        String[] whereArgs = {String.valueOf(idAlumno)};
        db.update(Utilidades.TABLE_ESTADISTICAS_ALUMNO, values, whereClause, whereArgs);
    }

    // Actualiza (o crea la entrada si es que no existe) los datos de un alumno en la tabla ESTADISTICAS_ALUMNO
    public void insertarOActualizarEstadisticasAlumno(EstadisticasAlumno estadisticas, List<EstadisticasPalabra> estadisticasPalabras, List<Sesion> sesionesPractica) {
        open();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_ESTADISTICAS_ALUMNO + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + "=?", new String[]{String.valueOf(estadisticas.getIdAlumno())})) {
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_SESIONES_PRACTICA_JUGADAS, estadisticas.getTotalSesionesPractica());
            values.put(Utilidades.CAMPO_SESIONES_PRUEBA_JUGADAS, estadisticas.getTotalSesionesPrueba());
            values.put(Utilidades.CAMPO_PALABRAS_VISTAS_TOTALES, estadisticas.getPalabrasVistasTotales());
            values.put(Utilidades.CAMPO_PALABRA_MAS_VISTA, estadisticas.obtenerPalabraMasVista(estadisticasPalabras));
            values.put(Utilidades.CAMPO_PALABRA_MAS_ACERTADA, estadisticas.obtenerPalabraMasAcertada(estadisticasPalabras));
            values.put(Utilidades.CAMPO_PALABRA_MENOS_ACERTADA, estadisticas.obtenerPalabraMenosAcertada(estadisticasPalabras));
            values.put(Utilidades.CAMPO_INTERVALO_PREFERIDO, estadisticas.obtenerIntervaloPreferido(sesionesPractica));
            values.put(Utilidades.CAMPO_TIEMPO_PROMEDIO_POR_PALABRA, estadisticas.getTiempoPromedioPorPalabra());

            if (cursor.moveToFirst()) {
                actualizarEstadisticasAlumno(estadisticas.getIdAlumno(), values);
            } else {
                values.put(Utilidades.CAMPO_ID_ALUMNO, estadisticas.getIdAlumno());
                db.insert(Utilidades.TABLE_ESTADISTICAS_ALUMNO, null, values);
            }
        }
        close();
    }

    // Recupera los datos del alumno en la tabla ESTADISTICAS_ALUMNO
    public EstadisticasAlumno obtenerEstadisticasAlumno(int idAlumno) {
        EstadisticasAlumno estadisticasAlumno = new EstadisticasAlumno(idAlumno);
        open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_ESTADISTICAS_ALUMNO + " WHERE " + Utilidades.CAMPO_ID_ALUMNO + " = ?", new String[]{String.valueOf(idAlumno)});

        if (cursor.moveToFirst()) {
            estadisticasAlumno.incrementarPalabrasVistasTotales(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_PALABRAS_VISTAS_TOTALES)));
            estadisticasAlumno.setTotalSesionesPractica(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_SESIONES_PRACTICA_JUGADAS)));
            estadisticasAlumno.setTotalSesionesPrueba(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_SESIONES_PRUEBA_JUGADAS)));
            estadisticasAlumno.actualizarTiempoPromedioPorPalabra(cursor.getFloat(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TIEMPO_PROMEDIO_POR_PALABRA)));

            int palabraMasVista = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_PALABRA_MAS_VISTA));
            int palabraMasAcertada = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_PALABRA_MAS_ACERTADA));
            int palabraMenosAcertada = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_PALABRA_MENOS_ACERTADA));
            int intervaloPreferido = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_INTERVALO_PREFERIDO));

            cursor.close();

            estadisticasAlumno.setPalabraMasVista(palabraMasVista);
            estadisticasAlumno.setPalabraMasAcertada(palabraMasAcertada);
            estadisticasAlumno.setPalabraMenosAcertada(palabraMenosAcertada);
            estadisticasAlumno.setIntervaloPreferido(intervaloPreferido);
        }
        close();
        return estadisticasAlumno;
    }

}
