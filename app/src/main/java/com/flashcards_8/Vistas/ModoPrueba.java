package com.flashcards_8.Vistas;

import android.annotation.SuppressLint;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModoPrueba extends AppCompatActivity {

    DbHelper conn;
    DbManager dbManager;
    int idMaestro, idAlumno, aciertos = 0, fallos = 0, idSeleccionado = -1, idPalabraCorrecta = -1, cantidadPalabras, contador = 1;
    String nivel, FechaI;
    int palabra1 = -1, palabra2 = -1, palabra3 = -1, palabra4 = -1;
    ArrayList<Palabra> listaPalabras;
    ImageView imagenes;
    String musica, imagen;
    TextView txtNivel;

    private MediaPlayer player = null;
    boolean seleccionado = false;
    Button B1, B2, B3, B4;  // Opciones

    // En todos los Map el primer Integer -> ID de la palabra
    Map<Integer, Boolean> respuestasSesion; // Boolean -> Si selecciono la opcion correcta
    Map<Integer, Integer> tiemposVistaPalabra;  // Integer -> Tiempo en pantalla de la palabra preguntada
    Map<Integer, Integer> vecesEscuchadaPalabra;    // Integer -> Reproducciones de la palabra preguntada
    Map<Integer, Integer> vecesVistaPalabra;    // Integer -> Cantidad de visualizaciones de la palabra preguntada
    long tiempoInicio;
    long tiempoPalabraInicio;
    int idSesion;

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
        conn = new DbHelper(this);
        dbManager = new DbManager(this);

        respuestasSesion = new HashMap<>();
        tiemposVistaPalabra = new HashMap<>();
        vecesEscuchadaPalabra = new HashMap<>();
        vecesVistaPalabra = new HashMap<>();
        tiempoInicio = System.currentTimeMillis();
        tiempoPalabraInicio = tiempoInicio;

        // Reciben datos del intent ("Menu")
        Bundle extras = getIntent().getExtras();

        // Se prepara la sesion, registrandola con datos iniciales (nivel, IDs de usuarios involucrados, etc)
        if (extras != null) {
            idMaestro = extras.getInt("idMaestro");
            idAlumno = extras.getInt("idAlumno");
            nivel = extras.getString("nivel");
            cantidadPalabras = extras.getInt("cantidadPalabras");
            idSesion = (int) dbManager.registrarSesionPrueba(idAlumno, idMaestro, FechaI, 0, Integer.parseInt(nivel), 0, 0, "N/A");
            Log.d("ModoPrueba", "Sesion registrada con ID: " + idSesion);
        } else {
            Log.d("Debug", "Intent was null");
        }
        txtNivel.setText(nivel);
        try {
            obtenerPalabras(nivel);
            ciclo1(contador);
        } catch (Exception e) {
            Toast.makeText(this, "error al iniciar: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    // Construye la lista de palabras a preguntar
    private void obtenerPalabras(String nivel) {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaPalabras = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + Utilidades.CAMPO_ID_PALABRA + ", " + Utilidades.CAMPO_TEXTO_PALABRA + ", " + Utilidades.CAMPO_AUDIO + ", " + Utilidades.CAMPO_IMAGEN + " FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_NIVEL_PALABRA + " = ?", new String[]{nivel});

        while (cursor.moveToNext()) {
            Palabra palabra = new Palabra();
            palabra.setIdPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)));
            palabra.setTextoPalabra(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA)));
            String audioUri = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_AUDIO));
            if (audioUri != null) {
                palabra.setAudioPalabra(audioUri);
            } else {
                Log.d("ModoPrueba", "Audio no encontrado para la palabra: " + palabra.getTextoPalabra());
            }
            String imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_IMAGEN));
            if (imagenUri != null) {
                palabra.setImagenPalabra(imagenUri);
            } else {
                Log.d("ModoPrueba", "Imagen no encontrada para la palabra: " + palabra.getTextoPalabra());
            }

            listaPalabras.add(palabra);
        }
        cursor.close();
        Collections.shuffle(listaPalabras);

        // Si la lista creada es mayor?? (checar) a cantidadPalabras, se creara otra lista con la diferencia para completar
        if (listaPalabras.size() > cantidadPalabras) {
            listaPalabras = new ArrayList<>(listaPalabras.subList(0, cantidadPalabras));
        }
    }

    public void ciclo1(int c) {
        Log.d("ModoPrueba", "ciclo1: contador=" + contador + ", cantidadPalabras=" + cantidadPalabras + ", nivel=" + nivel);
        try {
            // Si todavia no se llega a la cantidad de palabras seleecionadas en "Menu", continuar
            if (c <= cantidadPalabras) {
                asignarBoton();
            } else {    // De lo contrario cerrar sesion
                cerrarSesion();
                Toast.makeText(this, "Nivel Completado", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(() -> finish(), 500);
            }
        } catch (Exception e) {
            Toast.makeText(this, "error en ciclo: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void asignarBoton() {
        if (contador <= listaPalabras.size()) {
            Palabra palabraCorrecta = listaPalabras.get(contador - 1);
            musica = palabraCorrecta.getAudioPalabra();
            imagen = palabraCorrecta.getImagenPalabra();

            if (musica != null && !musica.isEmpty()) {
                reproducirAudio(musica);
                vecesEscuchadaPalabra.put(palabraCorrecta.getIdPalabra(), 1);
            } else {
                Log.d("ModoPrueba", "No hay audio para reproducir.");
            }

            if (imagen != null && !imagen.isEmpty()) {
                Uri imagenUri = Uri.parse(imagen);
                imagenes.setImageURI(imagenUri);
            } else {
                Log.e("ModoPractica", "Imagen es nula o vacía");
            }

            // Palabras a mostrar como opciones
            ArrayList<Palabra> opciones = new ArrayList<>();
            opciones.add(palabraCorrecta);
            opciones.addAll(obtenerOpcionesIncorrectas(palabraCorrecta.getIdPalabra(), 3));

            Collections.shuffle(opciones);

            B1.setText(opciones.get(0).getTextoPalabra());
            palabra1 = opciones.get(0).getIdPalabra();

            B2.setText(opciones.get(1).getTextoPalabra());
            palabra2 = opciones.get(1).getIdPalabra();

            B3.setText(opciones.get(2).getTextoPalabra());
            palabra3 = opciones.get(2).getIdPalabra();

            B4.setText(opciones.get(3).getTextoPalabra());
            palabra4 = opciones.get(3).getIdPalabra();

            idPalabraCorrecta = palabraCorrecta.getIdPalabra();

            Log.d("ModoPrueba", "Palabra asignada: " + palabraCorrecta.getTextoPalabra() + ", idPalabraCorrecta: " + idPalabraCorrecta);
            tiempoPalabraInicio = System.currentTimeMillis();   // Registra el tiempo en que la palabra fue mostrada

            vecesVistaPalabra.put(idPalabraCorrecta, vecesVistaPalabra.getOrDefault(idPalabraCorrecta, 0) + 1); // Actualiza el Map de visualizaciones
        }
    }

    // Botones con opciones
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
            vecesEscuchadaPalabra.put(idPalabraCorrecta, vecesEscuchadaPalabra.getOrDefault(idPalabraCorrecta, 0) + 1); // Actualizacion del Map de reproducciones
        } else if (viewId == R.id.btnconfirmar) {
            confirmarSeleccion();
        }
    }

    // Pinta la seleccion actual del usuario
    public void seleccionarOpcion(Button boton, int idPalabra) {
        resetBotones();
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f48b43")));
        idSeleccionado = idPalabra; // Asigna idSeleccionado con la palabra del boton para su verificacion si decide confirmar
        seleccionado = true;
        Log.d("PruebaPalabras", "Opción seleccionada: " + idPalabra);
    }

    // Manda idSeleccionado para su verificacion
    private void confirmarSeleccion() {
        if (seleccionado) {
            verificarOpcion(idSeleccionado);
            seleccionado = false;
        } else {
            Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show();
        }
    }

    // Verificacion de respuesta con idPalabraCorrecta
    public void verificarOpcion(int idSeleccionado) {
        Log.d("PruebaPalabras", "idSeleccionado: " + idSeleccionado);
        Log.d("PruebaPalabras", "idPalabraCorrecta: " + idPalabraCorrecta);

        boolean esCorrecto = (idSeleccionado == idPalabraCorrecta);

        if (esCorrecto) {   // Acierto
            if (idSeleccionado == palabra1) {
                B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSeleccionado == palabra2) {
                B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSeleccionado == palabra3) {
                B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idSeleccionado == palabra4) {
                B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            }
            aciertos += 1;
        } else {    // Fallo
            if (idSeleccionado == palabra1) {
                B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSeleccionado == palabra2) {
                B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSeleccionado == palabra3) {
                B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            } else if (idSeleccionado == palabra4) {
                B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F83A3A")));
            }
            fallos += 1;

            if (idPalabraCorrecta == palabra1) {
                B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idPalabraCorrecta == palabra2) {
                B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idPalabraCorrecta == palabra3) {
                B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            } else if (idPalabraCorrecta == palabra4) {
                B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43FA3E")));
            }
        }
        Log.d("PruebaPalabras", "Acierto: " + aciertos + " | Fallos: " + fallos);

        respuestasSesion.put(idPalabraCorrecta, esCorrecto);    // Actualizacion del Map correspondiente a aciertos y fallos de cada palabra

        int tiempoVista = obtenerTiempoVista();
        int vecesVista = vecesVistaPalabra.getOrDefault(idPalabraCorrecta, 0);
        int vecesEscuchada = vecesEscuchadaPalabra.getOrDefault(idPalabraCorrecta, 0);

        // Actualizacion de los datos de las palabras de la sesion
        dbManager.insertarOActualizarDatosPalabraSesion(idSesion, idPalabraCorrecta, vecesVista, tiempoVista, vecesEscuchada, esCorrecto ? 1 : 0, esCorrecto ? 0 : 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                resetBotones();
                ciclo1(++contador);
            }
        }, 1000);
    }

    // Devuelve los botones a sus colores neutros (no seleccionados)
    private void resetBotones() {
        B1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        B4.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
    }

    // Reproduce el audio de la palabra
    private void reproducirAudio(String audioUri) {
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
        } catch (IOException e) {
            Toast.makeText(this, "Audio fallido", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtiene cuatro palabras para distribuir en los botones, incluyendo la respuesta correcta
    private ArrayList<Palabra> obtenerOpcionesIncorrectas(int idPalabraCorrecta, int numOpciones) {
        ArrayList<Palabra> opcionesIncorrectas = new ArrayList<>();
        SQLiteDatabase db = conn.getReadableDatabase();
        // Obtiene palabras que no sean "idPalabraCorrecta"
        Cursor cursor = db.rawQuery("SELECT " + Utilidades.CAMPO_ID_PALABRA + ", " + Utilidades.CAMPO_TEXTO_PALABRA + " FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_ID_PALABRA + " != ? AND " + Utilidades.CAMPO_NIVEL_PALABRA + " = ? ORDER BY RANDOM() LIMIT ?", new String[]{String.valueOf(idPalabraCorrecta), nivel, String.valueOf(numOpciones)});

        while (cursor.moveToNext()) {
            Palabra palabra = new Palabra();
            palabra.setIdPalabra(cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA)));
            palabra.setTextoPalabra(cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA)));
            opcionesIncorrectas.add(palabra);
        }
        cursor.close();
        return opcionesIncorrectas;
    }

    private String obtenerFecha() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private List<EstadisticasPalabra> obtenerEstadisticasPalabras(int idAlumno) {
        DbManager dbManager = new DbManager(this);
        return dbManager.obtenerEstadisticasPalabras(idAlumno);
    }

    private List<Sesion> obtenerSesionesPractica(int idAlumno) {
        DbManager dbManager = new DbManager(this);
        return dbManager.obtenerSesionesPractica(idAlumno);
    }

    // Cierre de sesion:
    // + Se cumple con la cantidad seleccionada de palabras en "Menu"
    // + Regresa al Menu pulsando el boton de Volver
    private void cerrarSesion() {
        long endTime = System.currentTimeMillis();
        int duracion = (int) ((endTime - tiempoInicio) / 1000); // duración en segundos
        int calificacion = (int) (((double) aciertos / (aciertos + fallos)) * 100);

        dbManager.actualizarSesionPrueba(idSesion, obtenerFecha(), duracion, aciertos, fallos, calificacion);

        StringBuilder sb = new StringBuilder();
        sb.append("Palabras\tVeces Vista\tVeces Escuchada\tTiempo Vista\tCalificacion\n");

        // Se toman los datos de los Map y junto con todos los demas datos se mandan a sus respectivos metodos
        // para su registro o actualizacion
        for (Map.Entry<Integer, Boolean> entry : respuestasSesion.entrySet()) {
            int idPalabra = entry.getKey();
            boolean acierto = entry.getValue();
            int tiempoVista = tiemposVistaPalabra.getOrDefault(idPalabra, 0);
            int vecesVista = vecesVistaPalabra.getOrDefault(idPalabra, 0);
            int vecesEscuchada = vecesEscuchadaPalabra.getOrDefault(idPalabra, 0);

            dbManager.insertarOActualizarEstadisticasPalabra(idAlumno, idPalabra, vecesVista, acierto ? 1 : 0, acierto ? 0 : 1, tiempoVista, vecesEscuchada);

            String textoPalabra = obtenerTextoPalabraPorId(idPalabra);
            sb.append(textoPalabra).append("\t\t\t").append(vecesVista).append("\t\t\t").append(vecesEscuchada).append("\t\t\t").append(tiempoVista).append("\t\t\t").append(acierto ? "Acierto" : "Fallo").append("\n");
        }

        // Para actualizar estadisticas de alumnos
        List<EstadisticasPalabra> estadisticasPalabras = obtenerEstadisticasPalabras(idAlumno);
        List<Sesion> sesionesPractica = obtenerSesionesPractica(idAlumno);

        EstadisticasAlumno estadisticasAlumno = new EstadisticasAlumno(idAlumno);
        dbManager.insertarOActualizarEstadisticasAlumno(estadisticasAlumno, estadisticasPalabras, sesionesPractica);

        sb.append("Calificación: ").append(calificacion).append("%\n");

        Log.d("ModoPrueba", "Sesión finalizada con ID: " + idSesion);
        Log.d("ModoPrueba", "Duración: " + duracion + " segundos");
        Log.d("ModoPrueba", "Aciertos: " + aciertos);
        Log.d("ModoPrueba", "Fallos: " + fallos);
        Log.d("ModoPrueba", "Detalles de la sesión:\n" + sb.toString());

        Toast.makeText(this, "Sesión guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    // Calcula el tiempo en pantalla de cada palabra de la sesion
    private int obtenerTiempoVista() {
        long currentTime = System.currentTimeMillis();
        int tiempoVista = (int) ((currentTime - tiempoPalabraInicio) / 1000); // tiempo en segundos
        tiempoPalabraInicio = currentTime;
        tiemposVistaPalabra.put(idPalabraCorrecta, tiempoVista);
        return tiempoVista;
    }

    // Obtiene el texto de la palabra por su ID
    private String obtenerTextoPalabraPorId(int idPalabra) {
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + Utilidades.CAMPO_TEXTO_PALABRA + " FROM " + Utilidades.TABLE_PALABRAS + " WHERE " + Utilidades.CAMPO_ID_PALABRA + " = ?", new String[]{String.valueOf(idPalabra)});
        String textoPalabra = "";
        if (cursor.moveToFirst()) {
            textoPalabra = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA));
        }
        cursor.close();
        return textoPalabra;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cerrarSesion();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
