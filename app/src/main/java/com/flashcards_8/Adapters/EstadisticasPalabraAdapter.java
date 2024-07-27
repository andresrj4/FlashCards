package com.flashcards_8.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flashcards_8.Entidades.EstadisticasPalabra;
import com.flashcards_8.R;

import java.util.List;

public class EstadisticasPalabraAdapter extends RecyclerView.Adapter<EstadisticasPalabraAdapter.ViewHolder> {

    private List<EstadisticasPalabra> estadisticasPalabraList;

    // Se reciben de "DetallesPorPalabra" los datos de todas las palabras del alumno iniciado en sesion
    public EstadisticasPalabraAdapter(List<EstadisticasPalabra> estadisticasPalabraList) {
        this.estadisticasPalabraList = estadisticasPalabraList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estadisticas_palabra, parent, false);
        return new ViewHolder(view);
    }

    // Por medio de getters y setters (en "EstadisticasPalabra") se recupera la informacion
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EstadisticasPalabra estadistica = estadisticasPalabraList.get(position);
        holder.idPalabraTextView.setText(String.valueOf(estadistica.getIdPalabra()));
        holder.palabraTextView.setText(estadistica.getTextoPalabra());
        holder.nivelTextView.setText(String.valueOf(estadistica.getNivelPalabra()));
        holder.vecesVistaTextView.setText(String.valueOf(estadistica.getTotalVecesVista()));
        holder.vecesEscuchadaTextView.setText(String.valueOf(estadistica.getTotalVecesEscuchada()));
        holder.tiempoVistaTextView.setText(formatTime(estadistica.getTotalTiempoVista()));
        holder.aciertosTextView.setText(String.valueOf(estadistica.getTotalAciertos()));
        holder.fallosTextView.setText(String.valueOf(estadistica.getTotalFallos()));
        holder.promedioTextView.setText(String.format("%.2f%%", estadistica.getPromedioAciertos() * 100));
    }

    @Override
    public int getItemCount() {
        return estadisticasPalabraList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView idPalabraTextView, palabraTextView, nivelTextView, vecesVistaTextView, vecesEscuchadaTextView, tiempoVistaTextView, aciertosTextView, fallosTextView, promedioTextView;

        // Se asigna la informacion a elementos del layout
        public ViewHolder(View itemView) {
            super(itemView);
            idPalabraTextView = itemView.findViewById(R.id.tvIdPalabra);
            palabraTextView = itemView.findViewById(R.id.tvPalabra);
            nivelTextView = itemView.findViewById(R.id.tvNivel);
            vecesVistaTextView = itemView.findViewById(R.id.tvVecesVista);
            vecesEscuchadaTextView = itemView.findViewById(R.id.tvVecesEscuchada);
            tiempoVistaTextView = itemView.findViewById(R.id.tvTiempoVista);
            aciertosTextView = itemView.findViewById(R.id.tvAciertos);
            fallosTextView = itemView.findViewById(R.id.tvFallos);
            promedioTextView = itemView.findViewById(R.id.tvPromedio);
        }
    }

    // Se formatea el tiempo de visualizacion a MM:SS
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}