package com.example.cmacchiavelli.sudsys;

public class Estructura_BBDD_Recibos_Pagados {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Estructura_BBDD_Recibos_Pagados() {}

    //definimos la estructura de la tabla
    //public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "datosRecibosPagados";//nombre de la tabla
    //public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA2 = "id_recibo";
    public static final String NOMBRE_COLUMNA3 = "id_cliente";
    public static final String NOMBRE_COLUMNA4 = "id_empleado";
    public static final String NOMBRE_COLUMNA5 = "monto_total_recibo";
    public static final String NOMBRE_COLUMNA6 = "monto_pagado_recibo";
    public static final String NOMBRE_COLUMNA7 = "saldo_recibo";
    public static final String NOMBRE_COLUMNA8 = "fecha";


    //}

    //creamos la tabla con sql
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " , ";
    public static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + Estructura_BBDD_Recibos_Pagados.TABLE_NAME + " (" +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA2 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA3 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA4 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA5 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA6 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA7 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA8 + TEXT_TYPE + ") ";

    //elimina la tabla si ya existe, bien para actualizarla o crearla por primera vez
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Estructura_BBDD_Recibos_Pagados.TABLE_NAME;


}
