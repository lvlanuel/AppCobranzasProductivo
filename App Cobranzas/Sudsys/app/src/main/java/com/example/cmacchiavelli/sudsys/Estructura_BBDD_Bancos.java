package com.example.cmacchiavelli.sudsys;

public class Estructura_BBDD_Bancos {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Estructura_BBDD_Bancos() {}

    //definimos la estructura de la tabla
    //public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "datosBancos";//nombre de la tabla
    //public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA2 = "id_entidad_financiera";
    public static final String NOMBRE_COLUMNA3 = "entidad_financiera";


    //}

    //creamos la tabla con sql
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " , ";
    public static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + Estructura_BBDD_Bancos.TABLE_NAME + " (" +
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA2 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA3 + TEXT_TYPE + ") ";

    //elimina la tabla si ya existe, bien para actualizarla o crearla por primera vez
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Estructura_BBDD_Bancos.TABLE_NAME;


}
