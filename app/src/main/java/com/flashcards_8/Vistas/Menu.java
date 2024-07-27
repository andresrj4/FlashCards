package com.flashcards_8.Vistas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.flashcards_8.R;
import com.flashcards_8.Utilidades.GeneradorDePalabras;
import com.flashcards_8.Utilidades.infoTec;
import com.flashcards_8.db.DbHelper;
import com.flashcards_8.Entidades.Palabra;
import com.flashcards_8.Utilidades.Utilidades;
import com.flashcards_8.db.DbManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    public int idMaestro;
    public int idAlumno;
    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer);

        SharedPreferences sharedPref = getSharedPreferences("com.flashcards_8.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        idMaestro = sharedPref.getInt("ID_MAESTRO", -1);
        idAlumno = sharedPref.getInt("ID_ALUMNO", -1);

        if (idMaestro == -1 || idAlumno == -1) {
            Toast.makeText(this, "Error al obtener IDs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbManager = new DbManager(this);
        dbManager.open();

        // Inserción de palabras (si es necesario)
        List<Palabra> palabras = GeneradorDePalabras.generarListaDePalabras();
        dbManager.insertarMultiplesPalabras(palabras);

        dbManager.close();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);

        buttonDrawerToggle.setOnClickListener(v -> drawerLayout.open());
        // Items del "drawer" o menu lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navAlumnos) {
                Toast.makeText(Menu.this, "Lista de alumnos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Menu.this, Alumnos.class));
            }

            if (itemId == R.id.navMaestros) {
                Toast.makeText(Menu.this, "Lista de docentes", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Menu.this, Maestros.class));
            }

            if (itemId == R.id.navPalabras) {
                Toast.makeText(Menu.this, "Lista de palabras", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Menu.this, Palabras.class));
            }

            if (itemId == R.id.navSesion) {
                Toast.makeText(Menu.this, "Sesiones de los alumnos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Menu.this, Sesiones.class);
                intent.putExtra("idMaestro", idMaestro);
                intent.putExtra("idAlumno", idAlumno);
                startActivity(intent);
            }
            if (itemId == R.id.nav_resumen) {
                Intent intent = new Intent(this, DetallesDelAlumno.class);
                intent.putExtra("idAlumno", idAlumno);
                startActivity(intent);
            }

            if (itemId == R.id.InfoTEC) {
                Toast.makeText(Menu.this, "InfoTEC", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Menu.this, infoTec.class));
            }

            if (itemId == R.id.navSalir) {
                Toast.makeText(Menu.this, "Salir de la aplicacion", Toast.LENGTH_SHORT).show();
                opcionSalir();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Niveles
    public void OnClick2(View view) {
        if (view.getId() == R.id.IMGnvl1) {
            if (obtenerCantidad(1) != 0) {
                menuNivel(1);
            } else {
                Toast.makeText(this, "Este nivel no tiene palabras", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.IMGnvl2) {
            if (obtenerCantidad(2) != 0) {
                menuNivel(2);
            } else {
                Toast.makeText(this, "Este nivel no tiene palabras", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.IMGnvl3) {
            if (obtenerCantidad(3) != 0) {
                menuNivel(3);
            } else {
                Toast.makeText(this, "Este nivel no tiene palabras", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.IMGnvlEsp) {
            if (obtenerCantidad(4) != 0) {  // Si se tiene un nivel especial, lo consideramos como nivel 4
                menuNivel(4);
            } else {
                Toast.makeText(this, "Este nivel no tiene palabras", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Diferentes significados dependiendo el modo de sesion
    // Modo Practica o Flashcards -> intervalo de segundos entre palabras
    // Modo Prueba -> la cantidad de palabras a preguntar
    private int obtenerCantidad(int nivel) {
        SQLiteDatabase dbCan = new DbHelper(this).getReadableDatabase();
        ArrayList<Palabra> listaPalabras = new ArrayList<>();

        String[] columns = {Utilidades.CAMPO_ID_PALABRA, Utilidades.CAMPO_TEXTO_PALABRA};
        Cursor cursor = dbCan.query(Utilidades.TABLE_PALABRAS, columns, Utilidades.CAMPO_NIVEL_PALABRA + " = ?", new String[]{String.valueOf(nivel)}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_ID_PALABRA));
            String palabra = cursor.getString(cursor.getColumnIndexOrThrow(Utilidades.CAMPO_TEXTO_PALABRA));

            Palabra palabraObj = new Palabra(id, palabra, null, null, nivel);
            listaPalabras.add(palabraObj);
        }
        cursor.close();
        dbCan.close();

        return listaPalabras.size();
    }

    // Dependiendo el tipo de prueba se prepara el dato necesario:
    // Modo Practica o Flashcards -> intervalo de segundos entre palabras
    // Modo Prueba -> la cantidad de palabras a preguntar
    private void menuNivel(int nivel) {
        String[] options = {"Flashcards", "Prueba de Palabras"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar un modo");
        builder.setItems(options, (dialog, opcion) -> {
            Intent intent;  // Reemplazado por SharedPreferences
            if (opcion == 1) {
                menuCantidadPalabras(nivel);
            } else if (opcion == 0) {
                menuTiempo(nivel);
            }
        });
        builder.create().show();
    }

    // Pregunta la cantidad de palabras de la prueba e inicia la actividad
    private void menuCantidadPalabras(int nivel) {
        String[] options = {"5", "10", "15", "20"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Número de palabras para la prueba");
        builder.setItems(options, (dialog, opcion) -> {
            int cantidadPalabras = Integer.parseInt(options[opcion]);
            Intent intent = new Intent(Menu.this, ModoPrueba.class);
            intent.putExtra("idMaestro", idMaestro);
            intent.putExtra("idAlumno", idAlumno);
            intent.putExtra("nivel", String.valueOf(nivel));
            intent.putExtra("cantidadPalabras", cantidadPalabras);
            startActivity(intent);  // "ModoPrueba"
        });
        builder.create().show();
    }

    // Pregunta el intervalo de segundos entre palabras e inicia la actividad
    private void menuTiempo(int nivel) {
        String[] options = {"2", "4", "6", "8", "10"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Intervalos de tiempo (seg)");
        builder.setItems(options, (dialog, opcion) -> {
            Intent intent = new Intent(Menu.this, ModoPractica.class);
            intent.putExtra("idMaestro", idMaestro);
            intent.putExtra("idAlumno", idAlumno);
            intent.putExtra("nivel", String.valueOf(nivel));
            int tiempo;
            switch (opcion) {
                case 0:
                    tiempo = 2000;
                    break;
                case 1:
                    tiempo = 4000;
                    break;
                case 2:
                    tiempo = 6000;
                    break;
                case 3:
                    tiempo = 8000;
                    break;
                case 4:
                    tiempo = 10000;
                    break;
                default:
                    tiempo = 2000;
                    break;
            }
            intent.putExtra("tiempo", tiempo);
            startActivity(intent);  // "ModoPractica"
        });
        builder.create().show();
    }

    private void opcionSalir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea salir de la aplicacion?")
                .setPositiveButton("Si", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
