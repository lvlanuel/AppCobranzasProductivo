package com.example.cmacchiavelli.sudsys;

public class Estructura_BBDD_Cuotas_Canceladas {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Estructura_BBDD_Cuotas_Canceladas() {}

    //definimos la estructura de la tabla
    //public static class FeedEntry implements BaseColumns {
    public static final String TABLE_NAME = "datosCuotasCanceladas";//nombre de la tabla
    //public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA1 = "id";
    public static final String NOMBRE_COLUMNA2 = "id_poliza_amortizacion";
    public static final String NOMBRE_COLUMNA3 = "no_poliza";
    public static final String NOMBRE_COLUMNA4 = "numero_cuota";
    public static final String NOMBRE_COLUMNA5 = "total_cuota";
    public static final String NOMBRE_COLUMNA6 = "total_pagado";
    public static final String NOMBRE_COLUMNA7 = "total_saldo";
    public static final String NOMBRE_COLUMNA8 = "id_recibo";
    public static final String NOMBRE_COLUMNA9 = "id_cliente";
    public static final String NOMBRE_COLUMNA10 = "id_poliza_movimiento";


    //}

    //creamos la tabla con sql
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = " , ";
    public static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME + " (" +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA3 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA4 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA5 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA6 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA7 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA9 + TEXT_TYPE + COMMA_SEP +
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA10 + TEXT_TYPE + ") ";

    //elimina la tabla si ya existe, bien para actualizarla o crearla por primera vez
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME;


}
