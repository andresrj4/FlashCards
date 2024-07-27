package com.flashcards_8.Vistas;

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

import com.flashcards_8.Entidades.EstadisticasAlumno;
import com.flashcards_8.Entidades.EstadisticasPalabra;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.Entidades.Sesion;
import com.flashcards_8.R;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbHelper;
import com.flashcards_8.db.DbManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModoPractica extends AppCompatActivity {

    TextView txtpalabra, txtNivel;
    ImageView imagenes;
    DbHelper conn;
    DbManager dbManager;
    ArrayList<Palabra> listaPalabras;
    public boolean continuar = true, pregunta = false;
    String imagen, musica;
    public MediaPlayer player;
    public String nivel;
    public int ciclo = 1;
    int idMaestro, idAlumno, tiempo;
    Handler handler;

    // Map<ID de palabra, Arreglo para los datos de palabras de sesion:
    // [0] -> Visualizaciones de la sesion (veces vistas en sesion)
    // [1] -> Reproducciones de la sesion (veces escuchada en sesion)
    // [2] -> Tiempo en pantalla de la sesion (tiempo vista en sesion)>
    // Ver metodo "actualizarEstadisticas" para mejor comprension
    Map<Integer, Integer[]> palabrasEstadisticas;
    long tiempoInicio;
    int idSesion;
    int idPalabraActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_practica);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtpalabra = findViewById(R.id.txtPalabraLibre);
        txtNivel = findViewById(R.id.txtPracticaN);
        imagenes = findViewById(R.id.imagenModoLibre);
        conn = new DbHelper(this);
        dbManager = new DbManager(this);

        handler = new Handler();
        palabrasEstadisticas = new HashMap<>();
        tiempoInicio = System.currentTimeMillis();

        // Reciben datos del intent ("Menu")
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMaestro = extras.getInt("idMaestro");
            idAlumno = extras.getInt("idAlumno");
            nivel = extras.getString("nivel");
            tiempo = extras.getInt("tiempo");

            // Se prepara la sesion, registrandola con datos iniciales (nivel, IDs de usuarios involucrados, etc)
            if (nivel != null) {
                txtNivel.setText(nivel);
                obtenerCantidad(nivel.trim());
                idSesion = (int) dbManager.registrarSesionPractica(idAlumno, idMaestro, obtenerFecha(), 0, Integer.parseInt(nivel), tiempo);
                Log.d("ModoPractica", "Sesion registrada con ID: " + idSesion);
                Log.d("ModoPractica", "Intervalo de Sesion: " + tiempo);
                consultarListaPalabras(nivel.trim(), ciclo);
            } else {
                Log.e("ModoPractica", "Nivel es null");
            }
            Log.d("ModoPractica", "Extras received: idMaestro=" + idMaestro + ", idAlumno=" + idAlumno + ", nivel=" + nivel + ", tiempo=" + tiempo);
        } else {
            Log.e("ModoPractica", "Intent extras are null");
        }

    }

    // Se construye el listado de palabras a mostrar, retorna su tamaño
    private int obtenerCantidad(String nivel) {
        SQLiteDatabase dbCan = conn.getReadableDatabase();
        listaPalabras = new ArrayList<>();
        Cursor cursor = dbCan.rawQuery("SELECT " + Utilidades.CAMPO_ID_PALABRA + " , " + Utilidades.CAMPO_TEXTO_PALABRA + " FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_NIVEL_PALABRA + " = ?", new String[]{nivel});
        while (cursor.moveToNext()) {
            Palabra palabra = new Palabra();
            palabra.setIdPalabra(cursor.getInt(0));
            palabra.setTextoPalabra(cursor.getString(1));
            listaPalabras.add(palabra);
        }
        cursor.close();
        return listaPalabras.size();
    }

    private void consultarListaPalabras(String nivel, int ciclo) {
        if (continuar) {
            player = new MediaPlayer();
            if (ciclo <= listaPalabras.size()) {
                try {
                    SQLiteDatabase db = conn.getReadableDatabase();
                    Palabra palabra = null;
                    // Se obtiene una palabra del nivel y que en el CAMPO_VISTA_PRACTICA = 0
                    Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_NIVEL_PALABRA + " = ? AND " + Utilidades.CAMPO_VISTA_PRACTICA + " = 0 ORDER BY RANDOM() LIMIT 1", new String[]{nivel});
                    // Recuperala informacion de la palabra
                    if (cursor.moveToFirst()) {
                        palabra = new Palabra();
                        palabra.setIdPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)));
                        palabra.setTextoPalabra(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA)));
                        byte[] imagenBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_IMAGEN));
                        byte[] audioBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_AUDIO));
                        palabra.setImagenPalabra(new String(imagenBlob));
                        palabra.setAudioPalabra(new String(audioBlob));

                        musica = palabra.getAudioPalabra().trim();
                        imagen = palabra.getImagenPalabra().trim();

                        Log.d("ModoPractica", "Palabra recuperada: ID=" + palabra.getIdPalabra() + ", Texto=" + palabra.getTextoPalabra());

                        // Metodos para visualizacion, reproduccion de palabra
                        mostrarImagen(imagen);
                        reproducirAudio(musica, palabra.getIdPalabra(), palabra.getTextoPalabra());
                        mostrarPalabraEnPantalla(palabra.getIdPalabra(), palabra.getTextoPalabra());

                        // Cambio del campo a "0" (palabra ya vista)
                        marcarPalabraVista(palabra.getIdPalabra());

                        // En caso de no contar con palabras no vistas, reinicia CAMPO_VISTA_PRACTICA a "0" y entra al ciclo de nuevo
                    } else {
                        Log.d("ModoPractica", "No se encontró una palabra, reiniciando palabras vistas");
                        reiniciarPalabrasVistas();
                        consultarListaPalabras(nivel, 1);
                        return;
                    }
                    cursor.close();

                    reproducir(nivel, ciclo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                reiniciarPalabrasVistas();
                consultarListaPalabras(nivel, 1);
            }
        } else {
            if (pregunta) {
                cerrarSesion();
                handler = null;
                finish();
            }
        }
    }

    // Muestra la imagen de la palabra
    private void mostrarImagen(String imagenUri) {
        if (imagenUri != null && !imagenUri.isEmpty()) {
            Uri uri = Uri.parse(imagenUri);
            imagenes.setImageURI(uri);
        } else {
            Log.e("ModoPractica", "Imagen es nula o vacía");
        }
    }

    // Reproduce el audio de la palabra
    private void reproducirAudio(String audioUri, int idPalabra, String textoPalabra) {
        if (audioUri == null || audioUri.isEmpty()) {
            Toast.makeText(this, "URI del audio es nulo o vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        player = new MediaPlayer();
        try {
            Uri uri = Uri.parse(audioUri);
            player.setDataSource(this, uri);
            player.prepare();
            player.start();
            // Actualiza el Map de la palabra en pantalla (escuchada -> true)
            actualizarEstadisticas(idPalabra, textoPalabra, false, true);
        } catch (IOException e) {
            Toast.makeText(this, "Audio fallido", Toast.LENGTH_SHORT).show();
        }
    }

    // Muestra la palabra en pantalla
    private void mostrarPalabraEnPantalla(int idPalabra, String textoPalabra) {
        txtpalabra.setText(textoPalabra);
        // Actualiza el Map de la palabra en pantalla (vista -> true)
        actualizarEstadisticas(idPalabra, textoPalabra, true, false);
    }

    // Cambia el valor de CAMPO_VISTA_PRACTICA a "1", evitando que se vuelva a mostrar hasta
    // que todas las demas palabras del nivel esten en "1"
    private void marcarPalabraVista(int idPalabra) {
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_VISTA_PRACTICA, 1);
        db.update(Utilidades.TABLE_PALABRAS, values, Utilidades.CAMPO_ID_PALABRA + " = ?", new String[]{String.valueOf(idPalabra)});
    }

    // Actualiza (o crea una entrada nueva si la palabra ya existe) los datos de las palabras de la sesion
    private void actualizarEstadisticas(int idPalabra, String textoPalabra, boolean vista, boolean escuchada) {
        Log.d("ModoPractica", "Actualizando estadísticas para idPalabra: " + idPalabra + " - Vista: " + vista + " - Escuchada: " + escuchada);

        Integer[] estadisticas;
        // Si la palabra ya se registro en el Map (es decir, ya se vio mas de una vez en esta sesion), busca su posicion
        // y actualiza sus columnas, evitando duplicado de palabras
        if (palabrasEstadisticas.containsKey(idPalabra)) {
            estadisticas = palabrasEstadisticas.get(idPalabra);
            Log.d("ModoPractica", "Palabra existente encontrada en el array: " + idPalabra);
        } else {    // De lo contrario, crea una nueva entrada
            estadisticas = new Integer[]{0, 0, 0};
            Log.d("ModoPractica", "Palabra nueva agregada al array: " + idPalabra);
        }

        // Actualiza visualizaciones y tiempo en pantalla de la palabra
        if (vista) {
            estadisticas[0]++;
            estadisticas[2] += tiempo / 1000;   // tiempo -> intervalo seleccionado en "Menu"
            Log.d("ModoPractica", "Veces Vista incrementadas para " + textoPalabra + " a " + estadisticas[0]);
        }
        // Actualiza las reproducciones de la palabra
        if (escuchada) {
            estadisticas[1]++;
            Log.d("ModoPractica", "Veces Escuchada incrementadas para " + textoPalabra + " a " + estadisticas[1]);
        }

        // Agrega los valores alterados al Map
        palabrasEstadisticas.put(idPalabra, estadisticas);
        Log.d("ModoPractica", "Palabras estadísticas actualizadas: " + palabrasEstadisticas);
    }

    // Regresa CAMPO_VISTA_PRACTICA a "0"
    private void reiniciarPalabrasVistas() {
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_VISTA_PRACTICA, 0);
        db.update(Utilidades.TABLE_PALABRAS, values, Utilidades.CAMPO_NIVEL_PALABRA + " = ?", new String[]{nivel});
    }

    private void reproducir(String nivel, int ciclo) {
        handler.postDelayed(() -> consultarListaPalabras(nivel, ciclo + 1), tiempo);
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
            volverEscuchar(view);
        }
    }

    // Metodo se llama cuando el usuario manualmente pulsa el boton de "Play", reproduciendo
    // el audio nuevamente
    public void volverEscuchar(View view) {
        if (musica != null && !musica.isEmpty()) {
            Palabra palabraActual = obtenerPalabraActual();
            if (palabraActual != null) {
                reproducirAudio(musica, palabraActual.getIdPalabra(), palabraActual.getTextoPalabra());
            } else {
                Toast.makeText(this, "No se pudo obtener la palabra actual", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    // Se obtiene la palabra actualmente mostrada en pantalla, para poder compararla en diferentes metodos
    private Palabra obtenerPalabraActual() {
        String textoActual = txtpalabra.getText().toString().trim();
        for (Map.Entry<Integer, Integer[]> entry : palabrasEstadisticas.entrySet()) {
            int idPalabra = entry.getKey();
            String textoPalabra = obtenerTextoPalabra(idPalabra);
            if (textoPalabra.equals(textoActual)) {
                idPalabraActual = idPalabra;
                break;
            }
        }
        Log.d("ModoPractica", "Palabra actual obtenida: " + idPalabraActual + " - " + textoActual);
        return idPalabraActual != -1 ? new Palabra(idPalabraActual, textoActual) : null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            detenerReproduccion();
            continuar = false;
            pregunta = true;
            cerrarSesion();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<EstadisticasPalabra> obtenerEstadisticasPalabras(int idAlumno) {
        DbManager dbManager = new DbManager(this);
        return dbManager.obtenerEstadisticasPalabras(idAlumno);
    }

    private List<Sesion> obtenerSesionesPractica(int idAlumno) {
        DbManager dbManager = new DbManager(this);
        return dbManager.obtenerSesionesPractica(idAlumno);
    }

    // Cierre de sesion ocurre cuando el usuario decide volver al "Menu" via boton Volver
    private void cerrarSesion() {
        long endTime = System.currentTimeMillis();
        int duracion = (int) ((endTime - tiempoInicio) / 1000); // duración en segundos

        dbManager.actualizarSesionPractica(idSesion, obtenerFecha(), duracion);

        StringBuilder sb = new StringBuilder();
        sb.append("Palabras\tVeces Vista\tVeces Escuchada\tTiempo Vista\n");

        // Se toman los datos del Map y junto con todos los demas datos se mandan a sus respectivos metodos
        // para su registro o actualizacion
        for (Map.Entry<Integer, Integer[]> entry : palabrasEstadisticas.entrySet()) {
            int idPalabra = entry.getKey();
            Integer[] stats = entry.getValue();
            int vecesVista = stats[0];
            int vecesEscuchada = stats[1];
            int tiempoVista = stats[2];

            dbManager.insertarOActualizarDatosPalabraSesion(idSesion, idPalabra, vecesVista, tiempoVista, vecesEscuchada, 0, 0);
            dbManager.insertarOActualizarEstadisticasPalabra(idAlumno, idPalabra, vecesVista, 0, 0, tiempoVista, vecesEscuchada);
            sb.append(obtenerTextoPalabra(idPalabra)).append("\t").append(vecesVista).append("\t").append(vecesEscuchada).append("\t").append(tiempoVista).append("\n");
        }

        // Para actualizar estadisticas de alumnos
        List<EstadisticasPalabra> estadisticasPalabras = obtenerEstadisticasPalabras(idAlumno);
        List<Sesion> sesionesPractica = obtenerSesionesPractica(idAlumno);

        EstadisticasAlumno estadisticasAlumno = new EstadisticasAlumno(idAlumno);
        dbManager.insertarOActualizarEstadisticasAlumno(estadisticasAlumno, estadisticasPalabras, sesionesPractica);

        Log.d("ModoPractica", "Sesión finalizada con ID: " + idSesion);
        Log.d("ModoPractica", "Duración: " + duracion + " segundos");
        Log.d("ModoPractica", "Intervalo: " + (tiempo / 1000) + " segundos");
        Log.d("ModoPractica", "Detalles de la sesión:\n" + sb.toString());

        Toast.makeText(this, "Sesión guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    // Obtencion del texto de la palabra
    private String obtenerTextoPalabra(int idPalabra) {
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Utilidades.CAMPO_TEXTO_PALABRA + " FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_ID_PALABRA + " = ?", new String[]{String.valueOf(idPalabra)});
        String textoPalabra = "";
        if (cursor.moveToFirst()) {
            textoPalabra = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA));
        }
        cursor.close();
        return textoPalabra;
    }

    private String obtenerFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date());
    }
}
