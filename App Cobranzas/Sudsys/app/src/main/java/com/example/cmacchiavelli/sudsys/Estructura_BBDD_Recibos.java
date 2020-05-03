package com.example.cmacchiavelli.sudsys;

public class Estructura_BBDD_Recibos {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Estructura_BBDD_Recibos() {}

    //definimos la estructura de la tabla
    //public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "datosRecibos";//nombre de la tabla
    //public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA2 = "recibido_de";
    public static final String NOMBRE_COLUMNA3 = "concepto";
    public static final String NOMBRE_COLUMNA4 = "tipo";
    public static final String NOMBRE_COLUMNA5 = "moneda";
    public static final String NOMBRE_COLUMNA6 = "banco";
    public static final String NOMBRE_COLUMNA7 = "cheque";
    public static final String NOMBRE_COLUMNA8 = "monto";
    public static final String NOMBRE_COLUMNA9 = "id_cliente";
    public static final String NOMBRE_COLUMNA10 = "ci_cliente";
    public static final String NOMBRE_COLUMNA11 = "id_recibo";
    public static final String NOMBRE_COLUMNA12 = "id_recibo_sucursal";
    public static final String NOMBRE_COLUMNA13 = "email_cliente";


    //}

    //creamos la tabla con sql
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " , ";
    public static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + Estructura_BBDD_Recibos.TABLE_NAME + " (" +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA2 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA3 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA4 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA5 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA6 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA7 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA8 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA9 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA10 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA11 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA12 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos.NOMBRE_COLUMNA13 + TEXT_TYPE + ") ";

    //elimina la tabla si ya existe, bien para actualizarla o crearla por primera vez
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Estructura_BBDD_Recibos.TABLE_NAME;


}
