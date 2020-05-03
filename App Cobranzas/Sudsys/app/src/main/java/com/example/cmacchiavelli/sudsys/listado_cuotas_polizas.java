package com.example.cmacchiavelli.sudsys;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class listado_cuotas_polizas extends AppCompatActivity {

    private RecyclerView rvMusicas;
    private GridLayoutManager glm;
    private CuotasAdapter adapter;

    Spinner spinner;

    //incluimos el tool_bar
    private Toolbar toolbar;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    double saldo_recibo=0;
    double resultado = 0;
    //Elementos que se mostraran en el listview
    String[] lenguajeProgramacion = new String[] {"", "", "", "", "", "", "", "", "", "", ""};

    String autentificado,parametro_numero_liquidacion, mensaje, mensaje2, parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente, parametro_id_recibo, parametro_total_recibo, parametro_id_empleado, parametro_id_sucursal, parametro_id_recibo_sudsys, consumo_recibo, parametro_moneda, parametro_numero_recibo_sudsys;

    TextView tv_total_recibo, tv_consumo_recibo, tv_saldo_recibo, tv_id_cliente, tv_id_recibo_sudsys, tv_numero_recibo_sudsys, tv_total_recibo_bs, tv_nombre_cliente, tv_id_sucursal_usuario;
    double parametro_total_recibo_bs, parametro_total_recibo_sus;

    ArrayList<Cuotas> data2 = new ArrayList<>();

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_cuotas_polizas);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Amortizacion Cuotas");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();

        BorrarBasesDatos();

        //desbloquearrrrr
        parametro_total_recibo=datos.getString("parametro_total_recibo");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_id_recibo=datos.getString("parametro_id_recibo");
        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_id_recibo_sudsys=datos.getString("parametro_id_recibo_sudsys");

        //MOSTRAMOS EL ID DEL RECIBO EN EL SUDSYS
        tv_id_recibo_sudsys=(TextView) findViewById(R.id.tv_id_recibo_sudsys);
        tv_id_recibo_sudsys.setText(parametro_id_recibo_sudsys);

        parametro_moneda=datos.getString("parametro_moneda");
        parametro_numero_recibo_sudsys=datos.getString("parametro_numero_recibo_sudsys");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");

        //bloquearrrr
        //parametro_total_recibo="5000";
        //parametro_id_cliente="111";
        //parametro_id_recibo="222";

        //combo mora
        //combobox
        spinner = (Spinner) findViewById(R.id.tipo_mora);
        String[] letra = {"15 Días","30 Días","50 Días", "70 Días", "100 Días", "180 Días", "365 Días"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        //
        //llamamos a la tarea en segundo plano
        listado_cuotas_polizas.SegundoPlano tarea = new listado_cuotas_polizas.SegundoPlano();
        tarea.execute();

        rvMusicas = (RecyclerView) findViewById(R.id.rv_musicas);

        rvMusicas.setHasFixedSize(true);
        rvMusicas.setItemViewCacheSize(200);
        rvMusicas.setDrawingCacheEnabled(true);
        rvMusicas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //define el tamaño del item en el adaptador, como celda
        glm = new GridLayoutManager(this, 1);
        rvMusicas.setLayoutManager(glm);
        adapter = new CuotasAdapter(dataSet());
        rvMusicas.setAdapter(adapter);

        listado_cuotas_polizas.TercerPlano tarea2 = new listado_cuotas_polizas.TercerPlano();
        tarea2.execute();

        if(parametro_moneda.equals("$us")) {
            //MOSTRAMOS EL TOTAL DEL RECIBO EN $US
            tv_total_recibo = (TextView) findViewById(R.id.tv_total_recibo_sus);
            tv_total_recibo.setText(parametro_total_recibo);
            //MOSTRAMOS EL TOTAL DEL RECIBO CONVERTIDO EN BS
            parametro_total_recibo_bs = (Double.valueOf(parametro_total_recibo).doubleValue()) * (6.96);
            tv_total_recibo_bs = (TextView) findViewById(R.id.tv_total_recibo_bs);
            tv_total_recibo_bs.setText(String.valueOf(String.format("%.2f", parametro_total_recibo_bs)));
            //GUARDAMOS EL TOTAL DEL RECIBO PARA SUS CALCULOS POSTERIORES
            parametro_total_recibo=parametro_total_recibo;
            parametro_total_recibo_sus = (Double.valueOf(parametro_total_recibo).doubleValue());
        }else{
            //MOSTRAMOS EL TOTAL DEL RECIBO CONVERTIDO EN $US
            parametro_total_recibo_sus = (Double.valueOf(parametro_total_recibo).doubleValue()) / (6.96);
            tv_total_recibo = (TextView) findViewById(R.id.tv_total_recibo_sus);
            tv_total_recibo.setText(String.valueOf(String.format("%.2f",parametro_total_recibo_sus)));
            //MOSTRAMOS EL TOTAL DEL RECIBOEN BS
            tv_total_recibo_bs = (TextView) findViewById(R.id.tv_total_recibo_bs);
            tv_total_recibo_bs.setText(parametro_total_recibo);
            //GUARDAMOS EL TOTAL DEL RECIBO PARA SUS CALCULOS POSTERIORES
            parametro_total_recibo=String.valueOf(parametro_total_recibo_sus);
            parametro_total_recibo_bs = (Double.valueOf(parametro_total_recibo).doubleValue()) * (6.96);
        }
        tv_id_cliente=(TextView) findViewById(R.id.tv_id_cliente);
        //tv_id_cliente.setText("s2");


        //MOSTRAMOS EL NUMERO DEL RECIBO EN EL SUDSYS
        tv_numero_recibo_sudsys=(TextView) findViewById(R.id.tv_numero_recibo_sudsys);
        tv_numero_recibo_sudsys.setText(parametro_numero_recibo_sudsys);

        tv_nombre_cliente=(TextView) findViewById(R.id.tv_nombre_cliente);
        tv_nombre_cliente.setText(parametro_nombre_cliente);


        final Button btnCalcular = (Button) findViewById(R.id.btn_paga);
        final Button btn_guardar = (Button) findViewById((R.id.btn_guardar));

        btnCalcular.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resultado=0;
                saldo_recibo=0;

                String[] escrito = adapter.getEscrito();

                String[] estado_cuota = adapter.getEstado_cuota();


                double monto_total_recibo=Double.valueOf(parametro_total_recibo).doubleValue();
                String resultado2 = "";

                //Toast.makeText(getApplicationContext(), "primer vector: "+escrito[0]+" tamaño vector :" + escrito.length + " total a pagar: " + resultado + escrito[0] + "en strin :" + resultado2, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "primer vector: "+escrito[0]+"segundo vector: "+escrito[1]+"tercer vector: "+escrito[2]+"cuarto vector: "+escrito[3], Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "primer vector: "+escrito[0], Toast.LENGTH_LONG).show();
                //QUE TIENEN LOS VECTORES
                //Toast.makeText(getApplicationContext(), "primer vector: "+escrito[0]+"estado cuota: "+estado_cuota[0], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "segundo vector: " + escrito[1]+"estado cuota: "+estado_cuota[1], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "tercer vector: "+escrito[2]+"estado cuota: "+estado_cuota[2], Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "cuarto vector: "+escrito[3]+"estado cuota: "+estado_cuota[3], Toast.LENGTH_SHORT).show();

                /*
                for (int i = 0; i < escrito.length; i++) {

                    if (escrito[i].toString().isEmpty()) {
                        escrito[i] = "0";
                    }
                    //si no esta como cancelado (imagen) no suma
                    if (estado_cuota[i].toString().isEmpty()) {
                        estado_cuota[i]="1";
                    }
                    if (estado_cuota[i].toString().equals("1")) {
                        escrito[i] = "0";
                    }
                    Toast.makeText(getApplicationContext(), "estado cuota: "+estado_cuota[i], Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Calculo Exitoso... ", Toast.LENGTH_SHORT).show();

                    //escrito[i]="0";
                    if(estado_cuota[i].equals("0")) {
                        resultado += Double.parseDouble(escrito[i]);
                        resultado2 = resultado2 + escrito[i];
                    }

                }
                */

                ///////////////////////////////BUSCAMOS SI YA EXISTE UN PAGO ANTERIOR DE LA CUOTA
                try {
                    //importamos la clase BB_DD_HELPER
                    final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());
                    SQLiteDatabase db = helper.getReadableDatabase();

                    String[] projection = {
                            //que columnas queremos que saque nuestra consulta
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA3,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA4,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA5,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA6,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA7,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA9,
                            Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA10

                    };

                    //COLUMNA QUE COMPARAREMOS (WHERE)
                    String selection = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8 + " = ? ";
                    ////////String[] selectionArgs = { textoId.getText().toString() };
                    String[] selectionArgs = {parametro_id_recibo.toString()};

                    Cursor c = db.query(
                            Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    //se mueve el cursor al primer registro

                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            //Toast.makeText(getApplicationContext(), "...."+c.getString(5).toString(), Toast.LENGTH_SHORT).show();
                            resultado += Double.parseDouble(c.getString(5).toString());
                            resultado2 = resultado2 + c.getString(5).toString();
                        }
                    }

                   //String numero_id_poliza_amortizacion = c.getString(1).toString();
                } catch (Exception ex2) {
                    String mensaje2 = "ERROR 12: " + ex2;
                    //Toast.makeText(view.getContext(), mensaje2, Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(getApplicationContext(), "primer vector: "+escrito[0]+" tamaño vector :" + escrito.length + " total a pagar: " + resultado + escrito[0] + "en strin :" + resultado2, Toast.LENGTH_LONG).show();


                saldo_recibo=Double.valueOf(parametro_total_recibo).doubleValue()-resultado;

                if(resultado<=0) {
                    btn_guardar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "El consumo no puede ser menor o igual a 0, consumo: " + resultado, Toast.LENGTH_SHORT).show();
                }else{
                    if (resultado > monto_total_recibo) {
                        btn_guardar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "El consumo no puede ser mayor al monto del recibo, consumo: " + resultado, Toast.LENGTH_SHORT).show();
                    } else {
                        btn_guardar.setVisibility(View.VISIBLE);
                        //MOSTRAMOS EL CONSUMO DEL RECIBO
                        tv_consumo_recibo = (TextView) findViewById(R.id.tv_consumo_recibo);
                        tv_consumo_recibo.setText(String.valueOf(resultado));
                        //MOSTRAMOS EL CONSUMO DEL RECIBO
                        tv_saldo_recibo = (TextView) findViewById(R.id.tv_saldo_recibo);
                        //"%.2f" hace que se redonde a 2 decimales
                        tv_saldo_recibo.setText(String.format("%.2f", saldo_recibo));

                    }
                }
        }
        });


        //ponemos a la escucha
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //llamamos a la tarea antes del segundo plano
                listado_cuotas_polizas.AntesSegundoPlano tarea_antes = new listado_cuotas_polizas.AntesSegundoPlano();
                tarea_antes.execute();
            }
        });

    }


    //para el metodo login del webservice
    class AntesSegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
        }

        @Override
        protected Void doInBackground(Void... params){
            guarda_pago_recibo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas_confirmar.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", parametro_id_cliente);
            i.putExtra("parametro_id_recibo", parametro_id_recibo);
            i.putExtra("parametro_id_empleado", parametro_id_empleado);
            i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
            i.putExtra("parametro_id_recibo_sudsys", parametro_id_recibo_sudsys);
            i.putExtra("parametro_moneda", parametro_moneda);
            i.putExtra("parametro_numero_recibo_sudsys", parametro_numero_recibo_sudsys);
            i.putExtra("parametro_total_recibo_sus", String.valueOf(String.format("%.2f",parametro_total_recibo_sus)));
            i.putExtra("parametro_total_recibo_bs", String.valueOf(String.format("%.2f",parametro_total_recibo_bs)));
            i.putExtra("parametro_consumo_recibo",String.valueOf(resultado).toString());
            i.putExtra("parametro_saldo_recibo", String.format("%.2f",saldo_recibo));
            i.putExtra("parametro_nombre_cliente", parametro_nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", parametro_apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", parametro_apellido_materno_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

            startActivity(i);

        }
    }

    private void guarda_pago_recibo(){
        ///////////////////////////////GUARDAMOS EN LA BASE DE DATOS
        try {
            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());
            SQLiteDatabase db = helper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();

            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA2, parametro_id_recibo);
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA3, parametro_id_cliente);
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA4, "");
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA5, parametro_total_recibo.toString());
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA6, String.valueOf(resultado).toString());
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA7, String.valueOf(saldo_recibo).toString());
            values.put(Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA8, "");

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(Estructura_BBDD_Recibos_Pagados.TABLE_NAME, null, values);
            mensaje="Consumo Guardado";
        }
        catch (Exception ex) {
            mensaje="ERROR: " +ex;
        }
    }


    //@Override sirve para sobrescribir un método
    @Override public  boolean onCreateOptionsMenu(Menu mimenu){
        getMenuInflater().inflate(R.menu.menu_superior, mimenu);
        return true;
    }


    //click en algun elemento del menu
    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu){
        //capturamos el id del menu presionado
        int id=opcion_menu.getItemId();
        //si es el mismo id recibido del item presionado con el de configuracion almacenado en el archivo R
        if(id==R.id.configuracion){
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            pantalla_inicio(null);
            return true;
        }
        if(id==R.id.cerrar_sesion){
            if(id==R.id.cerrar_sesion){
                Toast.makeText(getApplicationContext(), "Cerrar sesion desde Pantalla Principal.", Toast.LENGTH_LONG).show();
            }
        }
        if(id==R.id.info){
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            ejecutar_info(null);
            return true;
        }
        //si no se cumple ninguna condicion, entonces debe devolver una opcion de menu por defecto
        return super.onOptionsItemSelected(opcion_menu);
    }


    //mostrar_actividad_info al hacer clieck en el item del menu
    //llamamos a la actividad info
    public void ejecutar_info(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(this, info.class);
        startActivity(i);
    }
    //llamamos a la actividad inicio
    public void pantalla_inicio(View view){
        super.finish();
    }



    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            data2.add(new Cuotas(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs.", "Cuota: 1 (100 Bs)","id_cliente","id_recibo","id_poliza_movimiento", "ramo", "monto_pagado", "resta", "sucursal", "id_sucursal", parametro_id_sucursal, "fecha_cuota", "no_liquidacion"));

        }

        @Override
        protected Void doInBackground(Void... params){

            eliminar_primer_elemento_rv();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            ///ELIMINA EL PRIMER ELEMNTO DE LA LISTA
           // Toast.makeText(getApplicationContext(), "Eliminado primer elemento..." +parametro_id_cliente , Toast.LENGTH_LONG).show();
            data2.remove(0);
            adapter.notifyDataSetChanged();

        }
    }

    private void eliminar_primer_elemento_rv(){
        //data2.remove(0);
        //actualizamos vista de la lista
        //adapter.notifyDataSetChanged();





       // data2.add(new Cuotas(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        if(rvMusicas.getAdapter() != null){
            //De esta manera sabes si tu RecyclerView está vacío
            if(rvMusicas.getAdapter().getItemCount() == 0) {
                Toast.makeText(getApplicationContext(), "No existen cuotas pendientes a cancelar por el cliente...", Toast.LENGTH_LONG).show();
                //Aquí muestras el mensaje
            }
        }

    }



    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            //ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            //textcargando.setVisibility(View.VISIBLE);
            dialog = new ProgressDialog(listado_cuotas_polizas.this);
            dialog.setMessage("Buscando pólizas...");
            dialog.setCancelable (false);
            dialog.show();

            LinearLayout contenedor = (LinearLayout) findViewById(R.id.ly_contenedor);
            contenedor.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params){

            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //TEXTO DESAPARECE AL CARGAR
            //ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            //textcargando.setVisibility(View.GONE);
            dialog.hide();

            LinearLayout contenedor = (LinearLayout) findViewById(R.id.ly_contenedor);
            contenedor.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Cuotas para Amortizar Encontradas." , Toast.LENGTH_SHORT).show();


        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){

        //WEB SERVICE CUPTAS POLIZAS CLIENTE
        String SOAP_ACTION = "http://ws.sudseguros.com/PolicyShare";
        String METHOD_NAME = "PolicyShare";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";



        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_cliente", parametro_id_cliente);
            Request.addProperty("numero_liquidacion", parametro_numero_liquidacion);
            //Request.addProperty("id_cliente", "17856");
            Request.addProperty("fecha_mora", "");
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Agarratodo el Objeto
            SoapArrayNivel1 = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //Obtiene el listado de clientes
            SoapArrayNivel2 = (SoapObject) SoapArrayNivel1.getProperty(2);


            String resultado = "";
            mensaje ="";
            int k=0;
            int k2=0;



            for (k2 = 0; k2< SoapArrayNivel2.getPropertyCount(); k2++)
            {

            }
            int numero_columnas = 22;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];

            for (int i = 0; i< SoapArrayNivel2.getPropertyCount(); i++)
            {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k=j;
                }

                data2.add(new Cuotas(ClientesArray[i][0],  ClientesArray[i][3], R.mipmap.ic_launcher_round, ClientesArray[i][17], ClientesArray[i][15], ClientesArray[i][3], ClientesArray[i][2], parametro_id_cliente, parametro_id_recibo, ClientesArray[i][1], ClientesArray[i][16].replace(" ",""),  ClientesArray[i][5],  ClientesArray[i][18],  ClientesArray[i][19],  ClientesArray[i][20], parametro_id_sucursal,  ClientesArray[i][9],  ClientesArray[i][21]));
                mensaje=ClientesArray[i][16];
            }

        }
        catch (Exception ex) {

            mensaje = "ERROR: " + ex.getMessage();
           // data2.add(new Cuotas(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "No se puede volver atras",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }

    private ArrayList<Cuotas> dataSet() {

        ArrayList<Cuotas> data = data2;

        return data;
    }

    public void refrescar_actividad(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            //Intent es como evento, debe mejecutar el java infoClase
            finish();
            startActivity(getIntent());
        }
    }

    public void BorrarBasesDatos() {
        String comparador_master="12345678";
        final BBDD_Helper helper_master = new BBDD_Helper(getApplicationContext());
        SQLiteDatabase db_master = helper_master.getWritableDatabase();

        String selection_master_3 = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_3 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME, selection_master_3, selectionArgs_master_3);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

        String selection_master_4 = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_4 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME, selection_master_4, selectionArgs_master_4);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Pre_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

        String selection_master_5 = Estructura_BBDD_Recibos.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_5 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Recibos.TABLE_NAME, selection_master_5, selectionArgs_master_5);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Recibos", Toast.LENGTH_SHORT).show();

        String selection_master_6 = Estructura_BBDD_Recibos_Pagados.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_6 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Recibos_Pagados.TABLE_NAME, selection_master_6, selectionArgs_master_6);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Recibos_Pagados", Toast.LENGTH_SHORT).show();

    }

    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

}
