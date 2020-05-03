package com.example.cmacchiavelli.sudsys;

public class Estructura_BBDD {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Estructura_BBDD() {}

    //definimos la estructura de la tabla
    //public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "datosUsuario";//nombre de la tabla
    //public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA1 = "id_empleado";
    public static final String NOMBRE_COLUMNA2 = "id_sucursal";
    public static final String NOMBRE_COLUMNA3 = "usuario";
    public static final String NOMBRE_COLUMNA4 = "nombre";
    public static final String NOMBRE_COLUMNA5 = "apellido_paterno";
    public static final String NOMBRE_COLUMNA6 = "apellido_materno";
    public static final String NOMBRE_COLUMNA7 = "app_movil_cobranza";
    public static final String NOMBRE_COLUMNA8 = "app_movil_reclamos";
    public static final String NOMBRE_COLUMNA9 = "codigo_mac_impresora";

    //}

    //creamos la tabla con sql
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " , ";
    public static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + Estructura_BBDD.TABLE_NAME + " (" +
                    Estructura_BBDD.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    Estructura_BBDD.NOMBRE_COLUMNA2 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA3 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA4 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA5 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA6 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA7 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA8 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD.NOMBRE_COLUMNA9 + TEXT_TYPE + ") ";

    //elimina la tabla si ya existe, bien para actualizarla o crearla por primera vez
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Estructura_BBDD.TABLE_NAME;


}
