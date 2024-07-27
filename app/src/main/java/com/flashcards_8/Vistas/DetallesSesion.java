package com.flashcards_8.Vistas;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flashcards_8.Adapters.PalabraAdapter;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.Entidades.Sesion;
import com.flashcards_8.R;
import com.flashcards_8.db.DbManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

public class DetallesSesion extends AppCompatActivity {

    TextView txtSesionId, txtTipoSesion, txtNivelSesion, txtFechaSesion, txtDuracionSesion, txtIntervaloSesion, txtNumeroPalabras, txtCalificacion, txtTituloGrafico;
    RecyclerView recyclerViewPalabras;
    PieChart pieChart;
    DbManager dbManager;
    Sesion sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_sesion);

        txtSesionId = findViewById(R.id.txtSesionId);
        txtTipoSesion = findViewById(R.id.txtTipoSesion);
        txtNivelSesion = findViewById(R.id.txtNivelSesionDetalles);
        txtFechaSesion = findViewById(R.id.txtFechaSesion);
        txtDuracionSesion = findViewById(R.id.txtDuracionSesion);
        txtIntervaloSesion = findViewById(R.id.txtIntervaloSesion);
        txtNumeroPalabras = findViewById(R.id.txtNumeroPalabras);
        txtCalificacion = findViewById(R.id.txtCalificacion);
        recyclerViewPalabras = findViewById(R.id.recyclerViewPalabras);
        txtTituloGrafico = findViewById(R.id.tituloGrafico);
        pieChart = findViewById(R.id.graficoPastel);

        dbManager = new DbManager(this);

        int sesionId = getIntent().getIntExtra("sesionId", -1);
        String fechaSesion = getIntent().getStringExtra("fechaSesion");
        int nivelSesion = getIntent().getIntExtra("nivelSesion", -1);
        int duracionSesion = getIntent().getIntExtra("duracionSesion", -1);
        String tipoSesion = getIntent().getStringExtra("tipoSesion");
        int intervaloSesion = getIntent().getIntExtra("intervaloSesion", -1);
        int aciertosSesion = getIntent().getIntExtra("aciertosSesion", -1);
        int fallosSesion = getIntent().getIntExtra("fallosSesion", -1);
        String calificacionSesion = getIntent().getStringExtra("calificacionSesion");

        Log.d("DetallesSesion", "Sesión ID recibida: " + sesionId);

        sesion = new Sesion(sesionId, fechaSesion, nivelSesion, tipoSesion);
        sesion.setDuracionSesion(duracionSesion);
        sesion.setIntervaloSesion(intervaloSesion);

        txtSesionId.setText("ID de Sesión: " + sesionId);
        txtTipoSesion.setText("Tipo de Sesión: " + tipoSesion);

        Log.d("DetallesSesion", "Fecha de Sesión: " + fechaSesion);
        Log.d("DetallesSesion", "Nivel de Sesión: " + nivelSesion);
        Log.d("DetallesSesion", "Duración de Sesión: " + duracionSesion);
        Log.d("DetallesSesion", "Intervalo de Sesión: " + intervaloSesion);

        txtFechaSesion.setText("Fecha de Sesión: " + (fechaSesion != null ? fechaSesion : "N/A"));
        txtNivelSesion.setText("Nivel: " + nivelSesion);
        txtDuracionSesion.setText("Duración: " + duracionSesion + " segundos");

        // Dependiendo el tipo de sesion (Practica o Flashcards | Prueba) habilita elementos del layout
        if (tipoSesion.equals("Practica")) {
            txtIntervaloSesion.setText("Intervalo de Sesión: " + (intervaloSesion / 1000) + " segundos");
            txtIntervaloSesion.setVisibility(View.VISIBLE);
            mostrarPalabrasVistas(sesionId);
            txtTituloGrafico.setText("Distribución de reproducciones de sesión");
        } else if (tipoSesion.equals("Prueba")) {
            sesion.setAciertosSesion(aciertosSesion);
            sesion.setFallosSesion(fallosSesion);
            sesion.setCalificacionSesion(calificacionSesion);

            txtNumeroPalabras.setText("Aciertos: " + aciertosSesion + " | Fallos: " + fallosSesion);
            txtNumeroPalabras.setVisibility(View.VISIBLE);
            txtCalificacion.setText("Calificación: " + calificacionSesion);
            txtCalificacion.setVisibility(View.VISIBLE);
            txtTituloGrafico.setText("Distribución de tiempo por palabra de sesión");
            mostrarResultadosPalabras(sesionId);
        }
    }

    // Recupera el listado de las palabras vistas en dicha sesion y configura
    // el RecyclerView y el PieChart
    private void mostrarPalabrasVistas(int sesionId) {
        List<Palabra> palabrasVistas = dbManager.obtenerPalabrasVistas(sesionId);
        Log.d("DetallesSesion", "Palabras vistas: " + palabrasVistas.size());
        configurarRecyclerView(palabrasVistas, "Practica");
        configurarPieChart(palabrasVistas, "Practica");
    }

    // Recupera el listado de las palabras evaluadas en dicha sesion y configura
    // el RecyclerView y el PieChart
    private void mostrarResultadosPalabras(int sesionId) {
        List<Palabra> palabrasEvaluadas = dbManager.obtenerPalabrasEvaluadas(sesionId);
        Log.d("DetallesSesion", "Palabras evaluadas: " + palabrasEvaluadas.size());
        configurarRecyclerView(palabrasEvaluadas, "Prueba");
        configurarPieChart(palabrasEvaluadas, "Prueba");
    }

    // Muestra o esconde elementos del RecyclerView dependiendo del tipo de sesion
    private void configurarRecyclerView(List<Palabra> palabras, String tipoSesion) {
        TextView columnaResultado = findViewById(R.id.columnaResultado);
        TextView columnaVisualizaciones = findViewById(R.id.columnaVisualizaciones);
        TextView columnaTiempoEnPantalla = findViewById(R.id.columnaTiempoEnPantalla);
        if (tipoSesion.equals("Prueba")) {
            columnaResultado.setVisibility(View.VISIBLE);
            columnaTiempoEnPantalla.setVisibility(View.VISIBLE);
            columnaVisualizaciones.setVisibility(View.GONE);
        } else { // Para sesiones practica (o Flashcards)
            columnaResultado.setVisibility(View.GONE);
            columnaTiempoEnPantalla.setVisibility(View.GONE);
            columnaVisualizaciones.setVisibility(View.VISIBLE);
        }

        recyclerViewPalabras.setLayoutManager(new LinearLayoutManager(this));
        // Manda el listado de palabras a "PalabraAdapter" (Adapters)
        PalabraAdapter adapter = new PalabraAdapter(this, palabras, tipoSesion);
        recyclerViewPalabras.setAdapter(adapter);
    }

    // Prepara los datos a usar en el grafico dependiendo del tipo de sesion
    private void configurarPieChart(List<Palabra> palabras, String tipoSesion) {
        List<PieEntry> entries = new ArrayList<>();
        if (tipoSesion.equals("Practica")) {
            for (Palabra palabra : palabras) {
                entries.add(new PieEntry(palabra.getVecesEscuchada(), palabra.getTextoPalabra()));
            }
        } else if (tipoSesion.equals("Prueba")) {
            int duracionTotal = sesion.getDuracionSesion();
            for (Palabra palabra : palabras) {
                float porcentaje = (float) palabra.getTiempoVista() / duracionTotal;
                entries.add(new PieEntry(porcentaje, palabra.getTextoPalabra()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Palabras");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false);
        data.setValueTextSize(16f);
        Legend legend = pieChart.getLegend();
        legend.setTextSize(16f);
        pieChart.invalidate(); // refrescar gráfico
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
