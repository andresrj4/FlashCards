package com.flashcards_8.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flashcards_8.Entidades.Sesion;
import com.flashcards_8.R;
import com.flashcards_8.Vistas.DetallesSesion;

import java.util.ArrayList;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Sesion> listaSesiones;

    public SesionAdapter(Context context, ArrayList<Sesion> listaSesiones) {
        this.context = context;
        this.listaSesiones = listaSesiones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sesion, parent, false);
        return new ViewHolder(view);
    }

    // Datos que muestra cada item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sesion sesion = listaSesiones.get(position);

        holder.txtSesionId.setText("ID: " + sesion.getIdSesion());
        holder.txtFechaSesion.setText("Fecha: " + sesion.getFechaSesion());
        holder.txtSesionNivel.setText("Nivel: " + sesion.getNivelSesion());
    }

    @Override
    public int getItemCount() {
        return listaSesiones.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSesionId, txtFechaSesion, txtSesionNivel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSesionId = itemView.findViewById(R.id.txtSesionId);
            txtFechaSesion = itemView.findViewById(R.id.txtFechaSesion);
            txtSesionNivel = itemView.findViewById(R.id.txtSesionNivel);

            // Se carga el intent de Extras con datos generales de la sesion, asi como
            // unos especificos dependiendo su tipo
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Sesion sesion = listaSesiones.get(position);
                        Intent intent = new Intent(context, DetallesSesion.class);
                        intent.putExtra("sesionId", sesion.getIdSesion());
                        intent.putExtra("fechaSesion", sesion.getFechaSesion());
                        intent.putExtra("nivelSesion", sesion.getNivelSesion());
                        intent.putExtra("tipoSesion", sesion.getTipoSesion());
                        intent.putExtra("duracionSesion", sesion.getDuracionSesion());

                        if (sesion.getTipoSesion().equals("Practica")) {
                            intent.putExtra("intervaloSesion", sesion.getIntervaloSesion());
                        } else if (sesion.getTipoSesion().equals("Prueba")) {
                            intent.putExtra("aciertosSesion", sesion.getAciertosSesion());
                            intent.putExtra("fallosSesion", sesion.getFallosSesion());
                            intent.putExtra("calificacionSesion", sesion.getCalificacionSesion());
                        }

                        context.startActivity(intent);  // "DetallesSesion"
                    }
                }
            });
        }
    }
}
