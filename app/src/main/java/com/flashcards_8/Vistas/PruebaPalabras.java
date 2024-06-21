package com.flashcards_8.Vistas;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flashcards_8.Entidades.Alumno;
import com.flashcards_8.Entidades.Docente;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.R;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class PruebaPalabras extends AppCompatActivity {

    DbHelper conn;
    int idMaestro, idAlumno, aciertos = 0, fallos = 0, idSeleccionado;
    String nivel, nombreA, nombreM, FechaI, FechaF;
    int palabra1 = 1, palabra2 = 1, palabra3 = 1, palabra4 = 1, c = 1, c2 = 1;
    ArrayList<Palabra> listaPalabras;
    ImageView imagenes;
    String musica, imagen, tipoPrueba;
    TextView txtNivel;

    private MediaPlayer player = null;
    boolean seleccionado = false;
    Button B1, B2, B3, B4;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_palabras);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        B1 = findViewById(R.id.Opcion1);
        B2 = findViewById(R.id.Opcion2);
        B3 = findViewById(R.id.Opcion3);
        B4 = findViewById(R.id.Opcion4);

        txtNivel = findViewById(R.id.txtNivelP);
        imagenes = findViewById(R.id.imgPractica);
        FechaI = obtenerFecha();
        conn = new DbHelper(this, Utilidades.DATABASE_NAME, null, Utilidades.DATABASE_VERSION);
        SQLiteDatabase dbR = conn.getReadableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMaestro = extras.getInt("idMaestro");
            idAlumno = extras.getInt("idAlumno");
            nivel = extras.getString("nivel");
            tipoPrueba = extras.getString("prueba");
        } else {
            Log.d("Debug", "Intent was null");
        }
        txtNivel.setText(nivel);
        try {
            ciclo1(c);
        } catch (Exception e) {
            Toast.makeText(this, "error al iniciar: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private int obtenerCantidad(String Nivel) {
        SQLiteDatabase dbCan = conn.getReadableDatabase();
        Palabra palabra = null;
        listaPalabras = new ArrayList<>();
        Cursor cursor = dbCan.rawQuery("SELECT " + Utilidades.CAMPO_ID + " , " + Utilidades.CAMPO_PALABRA + " FROM " + Nivel, null);
        while (cursor.moveToNext()) {
            palabra = new Palabra();
            palabra.setId(cursor.getInt(0));
            palabra.setPalabra(cursor.getString(1));
            listaPalabras.add(palabra);
        }
        cursor.close();
        return listaPalabras.size();
    }

    public void ciclo1(int c) {
        try {
            int cantidadPalabras = obtenerCantidad(nivel);
            if (c <= cantidadPalabras) {
                asignarBoton();
            } else {
                cerrarSesion();
                Toast.makeText(this, "Nivel Completado", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        } catch (Exception e) {
            Toast.makeText(this, "error en ciclo: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void asignarBoton() {
        conn = new DbHelper(getApplicationContext(), Utilidades.DATABASE_NAME, null, Utilidades.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();
        try {
            Palabra word = null;
            Cursor cursorB = db.rawQuery("SELECT * FROM " + nivel + " ORDER BY RANDOM() LIMIT 1", null);
            if (cursorB.moveToFirst()) {
                word = new Palabra();
                word.setId(cursorB.getInt(0));
                word.setPalabra(cursorB.getString(1));
                word.setImagen(cursorB.getString(2));
                word.setAudio(cursorB.getString(3));
                imagen = word.getImagen().trim();
                musica = word.getAudio().trim();
                imagenes.setImageURI(Uri.parse(imagen));
                c = word.getId(); // Actualiza 'c' con el ID de la palabra correcta seleccionada
            }

            ArrayList<Palabra> opcionesIncorrectas = new ArrayList<>();
            Cursor cursorIncorrectas = db.rawQuery("SELECT * FROM " + nivel + " WHERE " + Utilidades.CAMPO_ID + " != " + c + " ORDER BY RANDOM() LIMIT 3", null);
            while (cursorIncorrectas.moveToNext()) {
                word = new Palabra();
                word.setId(cursorIncorrectas.getInt(0));
                word.setPalabra(cursorIncorrectas.getString(1));
                word.setImagen(cursorIncorrectas.getString(2));
                word.setAudio(cursorIncorrectas.getString(3));
                opcionesIncorrectas.add(word);
            }

            opcionesIncorrectas.add(new Palabra(c, cursorB.getString(1), cursorB.getString(2), cursorB.getString(3)));

            Collections.shuffle(opcionesIncorrectas);

            B1.setText(opcionesIncorrectas.get(0).getPalabra());
            palabra1 = opcionesIncorrectas.get(0).getId();

            B2.setText(opcionesIncorrectas.get(1).getPalabra());
            palabra2 = opcionesIncorrectas.get(1).getId();

            B3.setText(opcionesIncorrectas.get(2).getPalabra());
            palabra3 = opcionesIncorrectas.get(2).getId();

            B4.setText(opcionesIncorrectas.get(3).getPalabra());
            palabra4 = opcionesIncorrectas.get(3).getId();

        } catch (Exception e) {
            Toast.makeText(this, "Error en asignar palabra a botón: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void verificarOpcion(int idSelec) {
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (idSelec == c) {
            if (idSelec == palabra1) {
                B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSelec == palabra2) {
                B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSelec == palabra3) {
                B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSelec == palabra4) {
                B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            }
            guardarRespuesta(db, c, true, obtenerFecha());
            aciertos += 1;
        } else {
            guardarRespuesta(db, c, false, obtenerFecha());
            fallos += 1;
            if (idSelec == palabra1) {
                B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSelec == palabra2) {
                B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSelec == palabra3) {
                B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSelec == palabra4) {
                B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            }
        }
    }

    private void guardarRespuesta(SQLiteDatabase db, int idPalabra, boolean resultado, String fecha) {
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_ID_NINO, idAlumno);
        values.put(Utilidades.CAMPO_ID_PALABRA, idPalabra);
        values.put(Utilidades.CAMPO_RESULTADO, resultado ? 1 : 0);
        values.put(Utilidades.CAMPO_DIFICULTAD, nivel);
        values.put(Utilidades.CAMPO_FECHA, fecha);

        long registro = db.insert(Utilidades.TABLE_REGISTRO, null, values);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            cerrarSesion();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void OnClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.Bpracticaregresar) {
            cerrarSesion();
            finish();
        } else if (viewId == R.id.Opcion1) {
            seleccionarOpcion(B1, palabra1);
        } else if (viewId == R.id.Opcion2) {
            seleccionarOpcion(B2, palabra2);
        } else if (viewId == R.id.Opcion3) {
            seleccionarOpcion(B3, palabra3);
        } else if (viewId == R.id.Opcion4) {
            seleccionarOpcion(B4, palabra4);
        } else if (viewId == R.id.btnReproducir) {
            reproducirAudio(musica);
        } else if (viewId == R.id.btnconfirmar) {
            confirmarSeleccion();
        }
    }

    private void seleccionarOpcion(Button boton, int idPalabra) {
        B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));

        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f48b43")));
        idSeleccionado = idPalabra;
        seleccionado = true;
    }

    private void reproducirAudio(String audioUri) {
        player = new MediaPlayer();
        try {
            Uri uri = Uri.parse(audioUri);
            player.setDataSource(this, uri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Toast.makeText(this, "Audio fallido", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarSeleccion() {
        if (seleccionado) {
            verificarOpcion(idSeleccionado);
            seleccionado = false;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
                    B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
                    B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
                    B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));

                    ciclo1(c += 1);
                }
            }, 1000);
            if (c2 == 4) {
                c2 = 1;
            } else {
                c2 += 1;
            }
        } else {
            Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show();
        }
    }

    private void cerrarSesion() {
        try {
            FechaF = obtenerFecha();
            SQLiteDatabase dbR = conn.getReadableDatabase();
            SQLiteDatabase dbW = conn.getWritableDatabase();
            SQLiteDatabase db = conn.getWritableDatabase();
            Alumno alumno = null;
            Docente docente = null;
            Cursor cursorA = dbR.rawQuery("SELECT * FROM " + Utilidades.TABLE_ALUMNOS + " WHERE " + Utilidades.CAMPO_ID + " = " + idAlumno, null);
            while (cursorA.moveToNext()) {
                alumno = new Alumno();
                alumno.setId(cursorA.getInt(0));
                alumno.setNombre(cursorA.getString(1));
                nombreA = alumno.getNombre();
            }
            Cursor cursorM = dbR.rawQuery("SELECT * FROM " + Utilidades.TABLE_DOCENTE + " WHERE " + Utilidades.CAMPO_ID + " = " + idMaestro, null);
            while (cursorM.moveToNext()) {
                docente = new Docente();
                docente.setId(cursorM.getInt(0));
                docente.setNombre(cursorM.getString(1));
                nombreM = docente.getNombre();
            }
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_ID_NINO, idAlumno);
            values.put(Utilidades.CAMPO_NOMBREM, nombreM);
            values.put(Utilidades.CAMPO_NOMBRE, nombreA);
            values.put(Utilidades.CAMPO_DIFICULTAD, nivel);
            values.put(Utilidades.CAMPO_TIPO_PRUEBA, tipoPrueba);
            values.put(Utilidades.CAMPO_FECHAI, FechaI);
            values.put(Utilidades.CAMPO_FECHAF, FechaF);
            values.put(Utilidades.CAMPO_ACIERTOS, aciertos);
            values.put(Utilidades.CAMPO_FALLOS, fallos);
            values.put(Utilidades.CAMPO_CALIFICACION, obtenerPuntaje(aciertos, fallos) + "%");
            long idResultado = dbW.insert(Utilidades.TABLE_SESION_NINO, Utilidades.CAMPO_ID, values);
        } catch (Exception e) {
            Toast.makeText(PruebaPalabras.this, "error en cerrar sesion por: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerFecha() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private float obtenerPuntaje(int aciertos, int fallos) {
        float promedio = 0;
        try {
            promedio = (100 / (aciertos + fallos)) * aciertos;
        } catch (Exception e) {
            Toast.makeText(this, "error en score: " + e, Toast.LENGTH_SHORT).show();
        }
        return promedio;
    }
}
