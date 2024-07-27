package com.flashcards_8.Vistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.flashcards_8.Adapters.SesionAdapter;
import com.flashcards_8.R;
import com.flashcards_8.db.DbManager;
import com.flashcards_8.Entidades.Sesion;
import java.util.ArrayList;
import java.util.List;

public class Sesiones extends AppCompatActivity {

    public int idAlumno;
    TextView txtNombre;
    RecyclerView rvSesionesPractica, rvSesionesPrueba;
    List<Sesion> listaSesionesPractica, listaSesionesPrueba;
    DbManager dbManager;

    // Se preparan los elementos del layout
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesiones);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtNombre = findViewById(R.id.txtAlumnoSesion);
        dbManager = new DbManager(this);
        rvSesionesPractica = findViewById(R.id.rvSesionesPractica);
        rvSesionesPrueba = findViewById(R.id.rvSesionesPrueba);

        rvSesionesPractica.setLayoutManager(new LinearLayoutManager(this));
        rvSesionesPrueba.setLayoutManager(new LinearLayoutManager(this));

        // Se reciben los extras de la pantalla anterior ("Menu")
        Bundle extras = getIntent().getExtras();
        // Debug para verificar que se reciben los extras
        if (extras != null) {
            idAlumno = extras.getInt("idAlumno", -1);
            if (idAlumno == -1) {
                Log.d("Sesiones", "idAlumno no se pasó correctamente en el Intent");
            }
        } else {
            Log.d("Debug", "Intent was null");
            idAlumno = -1; // Manejo de errores
        }

        // Con los extras (ID del alumno) se recuperan las sesiones con la ID del alumno
        if (idAlumno != -1) {
            consultarListaSesiones();
        } else {
            Log.d("Sesiones", "No se pudo obtener el idAlumno.");
        }
    }

    private void consultarListaSesiones() {
        listaSesionesPractica = dbManager.obtenerSesionesPractica(idAlumno);
        listaSesionesPrueba = dbManager.obtenerSesionesPrueba(idAlumno);

        // Log para confirmar si se recuperan sesiones con la ID del alumno
        Log.d("Sesiones", "IdAlumno: " + idAlumno);
        Log.d("Sesiones", "Número de sesiones de práctica obtenidas: " + listaSesionesPractica.size());
        Log.d("Sesiones", "Número de sesiones de prueba obtenidas: " + listaSesionesPrueba.size());

        // Establecer adaptadores (SesionAdapter) después de obtener los datos
        SesionAdapter adaptadorPractica = new SesionAdapter(this, (ArrayList<Sesion>) listaSesionesPractica);
        rvSesionesPractica.setAdapter(adaptadorPractica);

        SesionAdapter adaptadorPrueba = new SesionAdapter(this, (ArrayList<Sesion>) listaSesionesPrueba);
        rvSesionesPrueba.setAdapter(adaptadorPrueba);
        // Se continua en "SesionAdapter"
    }

    public void onclick(View view) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
