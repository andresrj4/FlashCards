package com.flashcards_8.Vistas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flashcards_8.R;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbHelper;

public class Login_maestro extends AppCompatActivity {

    private EditText txtUsuario, txtContrasena;
    private Button btnIniciarSesion, btnAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_maestro);

        txtUsuario = findViewById(R.id.txt_usuario);
        txtContrasena = findViewById(R.id.txt_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_siguiente);
        btnAgregar = findViewById(R.id.btn_agregar);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_maestro.this, Agregar_maestro.class);
                startActivity(intent);
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = txtUsuario.getText().toString();
                String contrasena = txtContrasena.getText().toString();

                if (usuario.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(Login_maestro.this, "Por favor, ingresa tu nombre de usuario y contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    int idMaestro = validarCredenciales(usuario, contrasena);
                    if (idMaestro != -1) {
                        guardarIdMaestroEnSharedPreferences(idMaestro);
                        Intent intent = new Intent(Login_maestro.this, Login_alumno.class);
                        intent.putExtra("idMaestro", idMaestro);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login_maestro.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private int validarCredenciales(String usuario, String contrasena) {
        DbHelper dbHelper = new DbHelper(Login_maestro.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {Utilidades.CAMPO_ID_MAESTRO};
        String selection = Utilidades.CAMPO_NOMBRE + " = ? AND " + Utilidades.CAMPO_CONTRASEÑA + " = ?";
        String[] selectionArgs = {usuario, contrasena};

        Cursor cursor = db.query(
                Utilidades.TABLE_MAESTRO,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int idMaestro = -1;
        if (cursor.moveToFirst()) {
            idMaestro = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_MAESTRO));
        }

        cursor.close();
        db.close();

        return idMaestro;
    }

    private void guardarIdMaestroEnSharedPreferences(int idMaestro) {
        SharedPreferences sharedPref = getSharedPreferences("com.flashcards_8.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("ID_MAESTRO", idMaestro);
        editor.apply();
    }
}
