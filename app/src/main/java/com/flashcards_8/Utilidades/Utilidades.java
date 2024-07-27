package com.flashcards_8.Utilidades;

import android.graphics.Color;

public class Utilidades {
    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "FlashCards.db";

    // Campos de los individuos
    public static final String TABLE_MAESTRO = "tabla_maestro";
    public static final String TABLE_ALUMNOS = "tabla_alumnos";
    public static final String CAMPO_ID_MAESTRO = "id_maestro";
    public static final String CAMPO_ID_ALUMNO = "id_alumno";
    public static final String CAMPO_CONTRASEÑA = "contraseña";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_APELLIDOS_ALUMNO = "apellidos_alumno";
    public static final String CAMPO_EDAD_ALUMNO = "edad_alumno";
    public static final String CAMPO_EDAD_MENTAL = "edad_mental";
    public static final String CAMPO_SEXO_ALUMNO = "sexo_alumno";
    public static final String CAMPO_COMENTARIOS = "comentarios";

    // Campos de las tablas de las palabras
    public static final String TABLE_PALABRAS = "tabla_palabras";
    public static final String CAMPO_ID_PALABRA = "id_palabra";
    public static final String CAMPO_TEXTO_PALABRA = "texto_palabra";
    public static final String CAMPO_IMAGEN = "imagen_palabra";
    public static final String CAMPO_AUDIO = "audio_palabra";
    public static final String CAMPO_NIVEL_PALABRA = "nivel_palabra";
    public static final String CAMPO_VISTA_PRACTICA = "vista_practica_palabra";
    public static final String CAMPO_VISTA_PRUEBA = "vista_prueba_palabra";

    // Creación de tabla para IDs de sesiones
    public static final String TABLE_SESION_IDS = "tabla_sesion_ids";
    public static final String CAMPO_ID_SESION_GLOBAL = "id_sesion_global";
    public static final String CAMPO_TIPO_SESION = "tipo_sesion";

    // Campos comunes para las tablas de sesiones
    public static final String CAMPO_ID_SESION = "id_sesion";
    public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_DURACION_SESION = "duracion_sesion";
    public static final String CAMPO_NIVEL_SESION = "nivel_sesion";

    // Creacion de datos comunes para las palabras de una sesion
    public static final String TABLE_DATOS_PALABRA_SESION = "tabla_datos_palabra_sesion";
    public static final String CAMPO_VECES_VISTA_PALABRA_SESION = "veces_vista_palabra_sesion";
    public static final String CAMPO_TIEMPO_VISTA_PALABRA_SESION = "tiempo_vista_palabra_sesion";
    public static final String CAMPO_VECES_ESCUCHADA_PALABRA_SESION = "veces_escuchada_palabra_sesion";
    public static final String CAMPO_ACIERTOS_PALABRA_SESION = "aciertos_palabra_sesion";
    public static final String CAMPO_FALLOS_PALABRA_SESION = "fallos_palabra_sesion";

    // Creación de tabla de sesiones prácticas
    public static final String TABLE_SESION_PRACTICA = "tabla_sesion_practica";
    public static final String CAMPO_INTERVALO_SESION = "intervalo_sesion";

    // Creación de tabla de sesiones de prueba
    public static final String TABLE_SESION_PRUEBA = "tabla_sesion_prueba";
    public static final String CAMPO_PUNTAJE_SESION = "calificacion_sesion";

    // Definición de la tabla de estadísticas de palabras
    public static final String TABLE_ESTADISTICAS_PALABRA = "tabla_estadisticas";
    public static final String CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA = "veces_vista_total_estadisticas";
    public static final String CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA = "aciertos_total_estadisticas";
    public static final String CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA = "fallos_total_estadisticas";
    public static final String CAMPO_TIEMPO_TOTAL_VISTA_PALABRA = "tiempo_total_vista";
    public static final String CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA = "veces_escuchada_total";

    // Definición de la tabla de estadísticas del alumno (no implementada, obsoleta)
    public static final String TABLE_ESTADISTICAS_ALUMNO = "tabla_estadisticas_alumno";
    public static final String CAMPO_ID_ESTADISTICAS_ALUMNO = "id_estadisticas_alumno";
    public static final String CAMPO_PALABRA_MAS_VISTA = "palabra_mas_vista";
    public static final String CAMPO_PALABRA_MAS_ACERTADA = "palabra_mas_acertada";
    public static final String CAMPO_PALABRA_MENOS_ACERTADA = "palabra_menos_acertada";
    public static final String CAMPO_INTERVALO_PREFERIDO = "intervalo_preferido";
    public static final String CAMPO_TIEMPO_PROMEDIO_POR_PALABRA = "tiempo_promedio_por_palabra";
    public static final String CAMPO_SESIONES_PRACTICA_JUGADAS = "sesiones_practica_jugadas";
    public static final String CAMPO_SESIONES_PRUEBA_JUGADAS = "sesiones_prueba_jugadas";
    public static final String CAMPO_PALABRAS_VISTAS_TOTALES = "palabras_vistas_totales";

    // Sentencias SQL para crear las tablas

    // Tabla de maestros
    public static final String CREAR_TABLA_MAESTRO = "CREATE TABLE IF NOT EXISTS " + TABLE_MAESTRO
            + " (" + CAMPO_ID_MAESTRO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAMPO_NOMBRE + " TEXT, "
            + CAMPO_CONTRASEÑA + " TEXT) ";

    // Tabla de alumnos
    public static final String CREAR_TABLA_ALUMNOS = "CREATE TABLE IF NOT EXISTS " + TABLE_ALUMNOS
            + " (" + CAMPO_ID_ALUMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAMPO_NOMBRE + " TEXT, "
            + CAMPO_APELLIDOS_ALUMNO + " TEXT, "
            + CAMPO_EDAD_ALUMNO + " INTEGER, "
            + CAMPO_EDAD_MENTAL + " INTEGER, "
            + CAMPO_SEXO_ALUMNO + " TEXT, "
            + CAMPO_COMENTARIOS + " TEXT) ";

    // Tabla de palabras
    public static final String CREAR_TABLA_PALABRAS = "CREATE TABLE IF NOT EXISTS " + TABLE_PALABRAS
            + " (" + CAMPO_ID_PALABRA + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAMPO_TEXTO_PALABRA + " TEXT, "
            + CAMPO_IMAGEN + " BLOB, "
            + CAMPO_AUDIO + " BLOB, "
            + CAMPO_NIVEL_PALABRA + " INTEGER, "
            // Campos para tener un control de que palabras se han visto ya en cada modo (se trata como boolean),
            // esto con el proposito de equilibrar las apariciones de cada palabra.

            // En modo practica (o Flashcards), el proposito de este campo es que cambie este campo de cada palabra
            // vista con "1". Una vez que se terminen los "0" (es decir, ya que se "dio la vuelta a la lista"), se devuelven
            // a "0" todos los campos sin terminar la sesion (mas info en "ModoPractica")
            + CAMPO_VISTA_PRACTICA + " INTEGER DEFAULT 0, "

            // En modo prueba, el proposito de este campo es que antes de construir la lista de palabras a preguntar compare
            // las palabras disponibles (es decir, las palabras que aun no ha visto en "esta vuelta" o en "0"). Si no hay
            // suficientes palabras, se "liberaran" la cantidad necesaria para completar la cantidad de la prueba (mas info en "ModoPrueba")
            + CAMPO_VISTA_PRUEBA + " INTEGER DEFAULT 0)";

    // Tabla de estadisticas del alumno (considerada obsoleta)
    public static final String CREAR_TABLA_ESTADISTICAS_ALUMNO = "CREATE TABLE IF NOT EXISTS " + TABLE_ESTADISTICAS_ALUMNO + " ("
            + CAMPO_ID_ESTADISTICAS_ALUMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAMPO_ID_ALUMNO + " INTEGER, "
            + CAMPO_PALABRAS_VISTAS_TOTALES + " INTEGER DEFAULT 0, "
            + CAMPO_PALABRA_MAS_VISTA + " INTEGER, "
            + CAMPO_PALABRA_MAS_ACERTADA + " INTEGER, "
            + CAMPO_PALABRA_MENOS_ACERTADA + " INTEGER, "
            + CAMPO_INTERVALO_PREFERIDO + " INTEGER, "
            + CAMPO_TIEMPO_PROMEDIO_POR_PALABRA + " REAL, "
            + CAMPO_SESIONES_PRACTICA_JUGADAS + " INTEGER DEFAULT 0, "
            + CAMPO_SESIONES_PRUEBA_JUGADAS + " INTEGER DEFAULT 0, "
            + "FOREIGN KEY (" + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ALUMNOS + "(" + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_PALABRA_MAS_VISTA + ", " + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ESTADISTICAS_PALABRA + "(" + CAMPO_ID_PALABRA + ", " + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_PALABRA_MAS_ACERTADA + ", " + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ESTADISTICAS_PALABRA + "(" + CAMPO_ID_PALABRA + ", " + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_PALABRA_MENOS_ACERTADA + ", " + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ESTADISTICAS_PALABRA + "(" + CAMPO_ID_PALABRA + ", " + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_INTERVALO_PREFERIDO + ") REFERENCES " + TABLE_SESION_PRACTICA + "(" + CAMPO_INTERVALO_SESION + "), "
            + "FOREIGN KEY (" + CAMPO_SESIONES_PRACTICA_JUGADAS + ") REFERENCES " + TABLE_SESION_PRACTICA + "(" + CAMPO_ID_SESION + "), "
            + "FOREIGN KEY (" + CAMPO_SESIONES_PRUEBA_JUGADAS + ") REFERENCES " + TABLE_SESION_PRUEBA + "(" + CAMPO_ID_SESION + "))";

    // Tabla de estadisticas de palabra por alumno
    public static final String CREAR_TABLA_ESTADISTICAS_PALABRA = "CREATE TABLE IF NOT EXISTS " + TABLE_ESTADISTICAS_PALABRA + " ("
            + CAMPO_ID_ALUMNO + " INTEGER, "
            + CAMPO_ID_PALABRA + " INTEGER, "
            + CAMPO_VECES_VISTA_TOTAL_ESTADISTICAS_PALABRA + " INTEGER, "
            + CAMPO_ACIERTOS_TOTAL_ESTADISTICAS_PALABRA + " INTEGER, "
            + CAMPO_FALLOS_TOTAL_ESTADISTICAS_PALABRA + " INTEGER, "
            + CAMPO_TIEMPO_TOTAL_VISTA_PALABRA + " INTEGER DEFAULT 0, "
            + CAMPO_VECES_ESCUCHADA_TOTAL_PALABRA + " INTEGER DEFAULT 0, "
            + "PRIMARY KEY (" + CAMPO_ID_ALUMNO + ", " + CAMPO_ID_PALABRA + "), "
            + "FOREIGN KEY (" + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ALUMNOS + "(" + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_ID_PALABRA + ") REFERENCES " + TABLE_PALABRAS + "(" + CAMPO_ID_PALABRA + "))";

    // Tabla global de sesiones, utilizada principalmente para evitar duplicados de IDs entre ambas tablas de sesiones (Prueba y Practica)
    public static final String CREAR_TABLA_SESION_IDS = "CREATE TABLE IF NOT EXISTS " + TABLE_SESION_IDS + " ("
            + CAMPO_ID_SESION_GLOBAL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CAMPO_TIPO_SESION + " TEXT)";

    // Esta tabla guarda los datos de las palabras de una sesion en especifico. A diferencia de TABLA_ESTADISTICAS_PALABRAS, esta tabla
    // solo se preocupa por los datos de la sesion en especifico. Posteriormente estos mismos datos se actualizan (o suman) a la tabla
    // de ESTADISTICAS_PALABRAS
    public static final String CREAR_TABLA_DATOS_PALABRA_SESION = "CREATE TABLE IF NOT EXISTS " + TABLE_DATOS_PALABRA_SESION + " ("
            + CAMPO_ID_SESION + " INTEGER, "
            + CAMPO_ID_PALABRA + " INTEGER, "
            + CAMPO_VECES_VISTA_PALABRA_SESION + " INTEGER DEFAULT 0, "
            + CAMPO_TIEMPO_VISTA_PALABRA_SESION + " INTEGER DEFAULT 0, "
            + CAMPO_VECES_ESCUCHADA_PALABRA_SESION + " INTEGER DEFAULT 0, "
            + CAMPO_ACIERTOS_PALABRA_SESION + " INTEGER DEFAULT 0, "
            + CAMPO_FALLOS_PALABRA_SESION + " INTEGER DEFAULT 0, "
            + "PRIMARY KEY (" + CAMPO_ID_SESION + ", " + CAMPO_ID_PALABRA + "), "
            + "FOREIGN KEY (" + CAMPO_ID_SESION + ") REFERENCES " + TABLE_SESION_IDS + "(" + CAMPO_ID_SESION_GLOBAL + "), "
            + "FOREIGN KEY (" + CAMPO_ID_PALABRA + ") REFERENCES " + TABLE_PALABRAS + "(" + CAMPO_ID_PALABRA + "))";

    // Tabla dedicada a las sesiones de practica (o Flashcards)
    public static final String CREAR_TABLA_SESION_PRACTICA = "CREATE TABLE IF NOT EXISTS " + TABLE_SESION_PRACTICA + " ("
            + CAMPO_ID_SESION + " INTEGER PRIMARY KEY, "
            + CAMPO_ID_ALUMNO + " INTEGER, "
            + CAMPO_ID_MAESTRO + " INTEGER, "
            + CAMPO_FECHA + " TEXT, "
            + CAMPO_DURACION_SESION + " INTEGER, "
            + CAMPO_NIVEL_SESION + " INTEGER, "
            + CAMPO_INTERVALO_SESION + " INTEGER, "
            + "FOREIGN KEY (" + CAMPO_ID_SESION + ") REFERENCES " + TABLE_SESION_IDS + "(" + CAMPO_ID_SESION_GLOBAL + "), "
            + "FOREIGN KEY (" + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ALUMNOS + "(" + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_ID_MAESTRO + ") REFERENCES " + TABLE_MAESTRO + "(" + CAMPO_ID_MAESTRO + "))";

    // Tabla dedicada a las sesiones prueba
    public static final String CREAR_TABLA_SESION_PRUEBA = "CREATE TABLE IF NOT EXISTS " + TABLE_SESION_PRUEBA + " ("
            + CAMPO_ID_SESION + " INTEGER PRIMARY KEY, "
            + CAMPO_ID_ALUMNO + " INTEGER, "
            + CAMPO_ID_MAESTRO + " INTEGER, "
            + CAMPO_FECHA + " TEXT, "
            + CAMPO_DURACION_SESION + " INTEGER, "
            + CAMPO_NIVEL_SESION + " INTEGER, "
            + CAMPO_ACIERTOS_PALABRA_SESION + " INTEGER, "
            + CAMPO_FALLOS_PALABRA_SESION + " INTEGER, "
            + CAMPO_PUNTAJE_SESION + " TEXT, "
            + "FOREIGN KEY (" + CAMPO_ID_SESION + ") REFERENCES " + TABLE_SESION_IDS + "(" + CAMPO_ID_SESION_GLOBAL + "), "
            + "FOREIGN KEY (" + CAMPO_ID_ALUMNO + ") REFERENCES " + TABLE_ALUMNOS + "(" + CAMPO_ID_ALUMNO + "), "
            + "FOREIGN KEY (" + CAMPO_ID_MAESTRO + ") REFERENCES " + TABLE_MAESTRO + "(" + CAMPO_ID_MAESTRO + "))";

    // Colores personalizados
    public static final int[] PERSONALIZADO = {
            Color.rgb(62, 78, 252),
            Color.rgb(218, 92, 250),
            Color.rgb(112, 124, 252),
            Color.rgb(225, 135, 248),
            Color.rgb(140, 249, 255)
    };
}
