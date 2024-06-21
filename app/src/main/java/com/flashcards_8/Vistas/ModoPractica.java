package com.flashcards_8.Vistas;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.R;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ModoPractica extends AppCompatActivity {

    TextView txtpalabra, txtNivel;
    ImageView imagenes;
    DbHelper conn;

    ArrayList<Palabra> listaPalabras;
    public boolean continuar = true, pregunta = false;
    String imagen, musica, tipoPrueba;
    public MediaPlayer player;
    public String nivel;
    public int ciclo = 1;
    int idMaestro, idAlumno, tiempo;
    Handler handler;
    HashMap<String, Integer> contadores = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_practica);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtpalabra = findViewById(R.id.txtPalabraLibre);
        txtNivel = findViewById(R.id.txtPracticaN);
        imagenes = findViewById(R.id.imagenModoLibre);
        conn = new DbHelper(getApplicationContext(), Utilidades.DATABASE_NAME, null, Utilidades.DATABASE_VERSION);
        SQLiteDatabase db = conn.getReadableDatabase();

        handler = new Handler();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMaestro = extras.getInt("idMaestro");
            idAlumno = extras.getInt("idAlumno");
            nivel = extras.getString("nivel");
            tipoPrueba = extras.getString("prueba");
            tiempo = extras.getInt("tiempo");
        } else {
            Log.d("Debug", "Intent was null");
        }
        txtNivel.setText(nivel);
        obtenerCantidad(nivel.trim());
        consultarListaPalabras(nivel.trim(), ciclo);
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
        return listaPalabras.size();
    }

    private void consultarListaPalabras(String tabla, int ciclo) {
        if (continuar) {
            player = new MediaPlayer();
            if (ciclo < obtenerCantidad(tabla) + 1) {
                try {
                    SQLiteDatabase db = conn.getReadableDatabase();
                    Palabra palabra = null;
                    Cursor cursor = db.rawQuery("SELECT * FROM " + tabla + " WHERE " + Utilidades.CAMPO_ID + " = " + ciclo, null);
                    while (cursor.moveToNext()) {
                        palabra = new Palabra();
                        palabra.setId(cursor.getInt(0));
                        palabra.setPalabra(cursor.getString(1));
                        palabra.setImagen(cursor.getString(2));
                        palabra.setAudio(cursor.getString(3));
                        musica = palabra.getAudio().trim();
                        imagen = palabra.getImagen().trim();
                        imagenes.setImageURI(Uri.parse(imagen));
                        txtpalabra.setText(palabra.getPalabra().trim());
                        actualizarContador(db, palabra);
                    }
                    cursor.close();

                    if (musica != null && !musica.isEmpty()) {
                        Uri uri = Uri.parse(musica);
                        try {
                            player.setDataSource(ModoPractica.this, uri);
                            player.prepare();
                            player.start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "El audio es nulo o vacío", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                reproducir(tabla, ciclo);
            } else {
                consultarListaPalabras(tabla, 1);
            }
        } else {
            if (pregunta) {
                handler = null;
                finish();
            }
        }
    }


    private void reproducir(String tabla, int ciclo) {
        handler.postDelayed(new Runnable() {
            public void run() {
                consultarListaPalabras(tabla, ciclo + 1);
            }
        }, tiempo);
    }

    private void actualizarContador(SQLiteDatabase db, Palabra palabra) {
        String query = "SELECT * FROM " + Utilidades.TABLE_CONTADOR_PALABRAS +
                " WHERE " + Utilidades.CAMPO_ID + " = " + idAlumno +
                " AND " + Utilidades.CAMPO_PALABRA + " = '" + palabra.getId() + "'" +
                " AND " + Utilidades.CAMPO_DIFICULTAD + " = '" + nivel + "'";
        Cursor palabraCursor = db.rawQuery(query, null);

        int contadorIndex = palabraCursor.getColumnIndex(Utilidades.CAMPO_CONTADOR);

        if (palabraCursor.moveToFirst()) {
            if (contadorIndex != -1) {
                int contadorActual = palabraCursor.getInt(contadorIndex);
                ContentValues values = new ContentValues();
                values.put(Utilidades.CAMPO_CONTADOR, contadorActual + 1);
                String updateQuery = Utilidades.CAMPO_ID + " = " + idAlumno + " AND " + Utilidades.CAMPO_PALABRA + " = '" + palabra.getId() + "'" + " AND " + Utilidades.CAMPO_DIFICULTAD + " = '" + nivel + "'";
                db.update(Utilidades.TABLE_CONTADOR_PALABRAS, values, updateQuery, null);
            } else {
                // Manejar el caso donde el índice es -1 si la columna no existe
                // Esto no debería ocurrir si la columna existe en la tabla
                Log.e("actualizarContador", "La columna " + Utilidades.CAMPO_CONTADOR + " no existe.");
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(Utilidades.CAMPO_ID, idAlumno);
            values.put(Utilidades.CAMPO_PALABRA, palabra.getId());
            values.put(Utilidades.CAMPO_CONTADOR, 1);
            values.put(Utilidades.CAMPO_DIFICULTAD, nivel);
            db.insert(Utilidades.TABLE_CONTADOR_PALABRAS, null, values);
        }
        palabraCursor.close();
    }

    private void detenerReproduccion() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public void onclick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnVolverModoLibre) {
            detenerReproduccion();
            continuar = false;
            pregunta = true;
        } else if (viewId == R.id.imgPreguntas) {
            reproducirAudio(musica);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            detenerReproduccion();
            continuar = false;
            pregunta = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void reproducirAudio(String audioUri) {
        if (audioUri == null || audioUri.isEmpty()) {
            Toast.makeText(this, "URI del audio es nulo o vacío", Toast.LENGTH_SHORT).show();
            return;
        }

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

}
