package com.flashcards_8.Vistas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flashcards_8.Entidades.Alumno;
import com.flashcards_8.R;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbHelper;

import java.util.ArrayList;

public class Login_alumno extends AppCompatActivity {

    ListView lvAlumnos;
    ArrayList<String> listaInformacion;
    ArrayList<Alumno> listaAlumnos;
    DbHelper conn;
    public int idMaestro;
    public int idAlumno;
    public boolean Seleccion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_alumno);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        conn = new DbHelper(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences("com.flashcards_8.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        idMaestro = sharedPref.getInt("ID_MAESTRO", -1);
        if (idMaestro == -1) {
            Toast.makeText(this, "Error al obtener ID del maestro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lvAlumnos = findViewById(R.id.lvAlumnos);
        mostrarLista();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarLista();
    }

    private void consultarListaAlumnos() {
        SQLiteDatabase db = conn.getReadableDatabase();
        Alumno alumno = null;
        listaAlumnos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLE_ALUMNOS, null);

        while (cursor.moveToNext()) {
            alumno = new Alumno();
            alumno.setIdAlumno(cursor.getInt(0));
            alumno.setNombreAlumno(cursor.getString(1));
            alumno.setApellidosAlumno(cursor.getString(2));
            listaAlumnos.add(alumno);
        }
        cursor.close();
        db.close();
        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<>();
        for (int i = 0; i < listaAlumnos.size(); i++) {
            listaInformacion.add(listaAlumnos.get(i).getNombreAlumno() + " " + listaAlumnos.get(i).getApellidosAlumno());
        }
    }

    private void mostrarLista() {
        consultarListaAlumnos();

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaInformacion) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) convertView;
                if (textView == null) {
                    textView = new TextView(getContext());
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(18);
                }
                textView.setText(listaInformacion.get(position));
                return textView;
            }
        };

        lvAlumnos.setAdapter(adaptador);

        lvAlumnos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                idAlumno = listaAlumnos.get(pos).getIdAlumno();
                Seleccion = true;
            }
        });
    }

    public void agregarAlumnoOnClick(View view) {
        Intent intent = new Intent(Login_alumno.this, Agregar_alumno.class);
        startActivityForResult(intent, 1);
        Seleccion = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mostrarLista();
        }
    }

    public void iniciarAlumnoOnClick(View view) {
        if (Seleccion) {
            guardarIdAlumnoEnSharedPreferences(idAlumno);
            Intent intent = new Intent(Login_alumno.this, Menu.class);
            intent.putExtra("idMaestro", idMaestro);
            intent.putExtra("idAlumno", idAlumno);
            startActivity(intent);
            Seleccion = false;
        } else {
            Toast.makeText(this, "Debe Seleccionar un Alumno", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarIdAlumnoEnSharedPreferences(int idAlumno) {
        SharedPreferences sharedPref = getSharedPreferences("com.flashcards_8.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("ID_ALUMNO", idAlumno);
        editor.apply();
    }

    public void volverInicioOnClick(View view) {
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
