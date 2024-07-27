package com.flashcards_8.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.R;
import java.util.List;

public class PalabraAdapter extends RecyclerView.Adapter<PalabraAdapter.PalabraViewHolder> {

    private Context context;
    private List<Palabra> palabras;
    private String tipoSesion;

    public PalabraAdapter(Context context, List<Palabra> palabras, String tipoSesion) {
        this.context = context;
        this.palabras = palabras;
        this.tipoSesion = tipoSesion;
    }

    @NonNull
    @Override
    public PalabraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_palabra_detalles_sesion, parent, false);
        return new PalabraViewHolder(view);
    }

    // Se muestran u ocultan elementos del layout dependiendo del tipo de sesion
    @Override
    public void onBindViewHolder(@NonNull PalabraViewHolder holder, int position) {
        Palabra palabra = palabras.get(position);
        holder.txtPalabra.setText(palabra.getTextoPalabra());

        if (tipoSesion.equals("Practica")) {
            holder.txtVecesVista.setVisibility(View.VISIBLE);
            holder.txtVecesEscuchada.setVisibility(View.VISIBLE);

            holder.txtVecesVista.setText(String.valueOf(palabra.getVecesVistaPalabra()));
            holder.txtVecesEscuchada.setText(String.valueOf(palabra.getVecesEscuchada()));
            holder.txtTiempoVista.setVisibility(View.GONE); // Ocultar el tiempo vista
            holder.txtResultado.setVisibility(View.GONE);
        } else if (tipoSesion.equals("Prueba")) {
            holder.txtVecesVista.setVisibility(View.GONE);
            holder.txtTiempoVista.setVisibility(View.VISIBLE);
            holder.txtVecesEscuchada.setVisibility(View.VISIBLE);
            holder.txtResultado.setVisibility(View.VISIBLE);

            holder.txtTiempoVista.setText(String.valueOf(palabra.getTiempoVista()));
            holder.txtVecesEscuchada.setText(String.valueOf(palabra.getVecesEscuchada()));
            holder.txtResultado.setText(palabra.getResultado() ? "Correcto" : "Incorrecto");
        }
    }

    @Override
    public int getItemCount() {
        return palabras.size();
    }

    public static class PalabraViewHolder extends RecyclerView.ViewHolder {
        TextView txtPalabra, txtVecesVista, txtTiempoVista, txtVecesEscuchada, txtResultado;

        public PalabraViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPalabra = itemView.findViewById(R.id.txtPalabra);
            txtVecesVista = itemView.findViewById(R.id.txtVisualizaciones);
            txtTiempoVista = itemView.findViewById(R.id.txtTiempoEnPantalla);
            txtVecesEscuchada = itemView.findViewById(R.id.txtReproducciones);
            txtResultado = itemView.findViewById(R.id.txtResultado);
        }
    }
}
