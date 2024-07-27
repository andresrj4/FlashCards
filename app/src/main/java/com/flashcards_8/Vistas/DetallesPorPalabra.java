package com.flashcards_8.Vistas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcards_8.Adapters.EstadisticasPalabraAdapter;
import com.flashcards_8.Entidades.EstadisticasPalabra;
import com.flashcards_8.R;
import com.flashcards_8.db.DbManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetallesPorPalabra extends AppCompatActivity {

    private RecyclerView recyclerViewPalabrasEstadisticas;
    private EstadisticasPalabraAdapter adapter;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_por_palabra);

        recyclerViewPalabrasEstadisticas = findViewById(R.id.recyclerViewPalabrasEstadisticas);
        recyclerViewPalabrasEstadisticas.setLayoutManager(new LinearLayoutManager(this));

        dbManager = new DbManager(this);

        // Se obtienen los datos de todas las palabras de TABLE_ESTADISTICAS_PALABRAS
        List<EstadisticasPalabra> estadisticasPalabraList = dbManager.obtenerTodasLasEstadisticasDePalabras();

        // Se ordenan por ID
        Collections.sort(estadisticasPalabraList, new Comparator<EstadisticasPalabra>() {
            @Override
            public int compare(EstadisticasPalabra o1, EstadisticasPalabra o2) {
                return Integer.compare(o1.getIdPalabra(), o2.getIdPalabra());
            }
        });

        // Se mandan al adaptador "EstadisticasPalabraAdapter"
        adapter = new EstadisticasPalabraAdapter(estadisticasPalabraList);
        recyclerViewPalabrasEstadisticas.setAdapter(adapter);

        findViewById(R.id.btnVolverDetalles).setOnClickListener(view -> finish());
    }
}
