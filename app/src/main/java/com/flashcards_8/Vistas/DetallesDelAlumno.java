package com.flashcards_8.Vistas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flashcards_8.Entidades.EstadisticasAlumno;
import com.flashcards_8.R;
import com.flashcards_8.db.DbManager;

// Asigna datos de la tabla de ESTADISTICAS_ALUMNOS (utilizando metodos en DbManager) a elementos
// para su visualizacion
public class DetallesDelAlumno extends AppCompatActivity {

    private TextView tvPalabrasVistasTotales, tvPalabraMasVista, tvPalabraMasAcertada, tvPalabraMenosAcertada, tvIntervaloPreferido, tvTiempoPromedioPorPalabra, tvSesionesPracticaJugadas, tvSesionesPruebaJugadas;
    private DbManager dbManager;
    private int idAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_alumno);

        tvPalabrasVistasTotales = findViewById(R.id.tvPalabrasVistasTotales);
        tvPalabraMasVista = findViewById(R.id.tvPalabraMasVista);
        tvPalabraMasAcertada = findViewById(R.id.tvPalabraMasAcertada);
        tvPalabraMenosAcertada = findViewById(R.id.tvPalabraMenosAcertada);
        tvIntervaloPreferido = findViewById(R.id.tvIntervaloPreferido);
        tvTiempoPromedioPorPalabra = findViewById(R.id.tvTiempoPromedioPorPalabra);
        tvSesionesPracticaJugadas = findViewById(R.id.tvSesionesPracticaJugadas);
        tvSesionesPruebaJugadas = findViewById(R.id.tvSesionesPruebaJugadas);

        dbManager = new DbManager(this);
        idAlumno = getIntent().getIntExtra("idAlumno", -1);

        EstadisticasAlumno estadisticasAlumno = dbManager.obtenerEstadisticasAlumno(idAlumno);

        tvPalabrasVistasTotales.setText(String.valueOf(estadisticasAlumno.getPalabrasVistasTotales()));
        tvPalabraMasVista.setText(String.valueOf(estadisticasAlumno.getPalabraMasVista()));
        tvPalabraMasAcertada.setText(String.valueOf(estadisticasAlumno.getPalabraMasAcertada()));
        tvPalabraMenosAcertada.setText(String.valueOf(estadisticasAlumno.getPalabraMenosAcertada()));
        tvIntervaloPreferido.setText(String.valueOf(estadisticasAlumno.getIntervaloPreferido()));
        tvTiempoPromedioPorPalabra.setText(String.valueOf(estadisticasAlumno.getTiempoPromedioPorPalabra()));
        tvSesionesPracticaJugadas.setText(String.valueOf(estadisticasAlumno.getTotalSesionesPractica()));
        tvSesionesPruebaJugadas.setText(String.valueOf(estadisticasAlumno.getTotalSesionesPrueba()));

        findViewById(R.id.btnVolverDetallesAlumno).setOnClickListener(view -> finish());
    }
}
