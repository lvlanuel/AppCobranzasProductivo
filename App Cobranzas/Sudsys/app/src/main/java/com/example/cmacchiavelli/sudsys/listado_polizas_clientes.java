package com.example.cmacchiavelli.sudsys;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class listado_polizas_clientes extends AppCompatActivity {

    private RecyclerView rvMusicas;
    private GridLayoutManager glm;
    private CuotasClienteAdapter adapter;
    ArrayList<CuotasCliente> data3 = new ArrayList<>();
    //incluimos el tool_bar
    private Toolbar toolbar;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    String parametro_id_empleado, parametro_id_sucursal, parametro_tipo, parametro_id_cliente, parametro_id_recibo, mensaje,
    parametro_tipo_actividad,parametro_nombre_cliente, parametro_apellido_paterno_cliente, parametro_apellido_materno_cliente,
    parametro_ci_nit_cliente, parametro_tipo_cliente, parametro_numero_liquidacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_polizas_clientes);



        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Polizas Cliente");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();
        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo=datos.getString("parametro_tipo");

        parametro_tipo_actividad=datos.getString("parametro_tipo_actividad");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_ci_nit_cliente=datos.getString("parametro_ci_nit_cliente");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        parametro_id_recibo="";
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");


        //llamamos a la tarea en segundo plano
        listado_polizas_clientes.SegundoPlano tarea = new listado_polizas_clientes.SegundoPlano();
        tarea.execute();

        rvMusicas = (RecyclerView) findViewById(R.id.rv_musicas);

        rvMusicas.setHasFixedSize(true);
        rvMusicas.setItemViewCacheSize(200);
        rvMusicas.setDrawingCacheEnabled(true);
        rvMusicas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //define el tamaño del item en el adaptador, como celda
        glm = new GridLayoutManager(this, 1);
        rvMusicas.setLayoutManager(glm);
        adapter = new CuotasClienteAdapter(dataSet());
        rvMusicas.setAdapter(adapter);
        listado_polizas_clientes.TercerPlano tarea2 = new listado_polizas_clientes.TercerPlano();
        tarea2.execute();

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


    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params){

            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Cuotas para Amortizar Encontradas..." +mensaje, Toast.LENGTH_SHORT).show();


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

                data3.add(new CuotasCliente(ClientesArray[i][0],  ClientesArray[i][3], R.mipmap.ic_launcher_round, ClientesArray[i][17], ClientesArray[i][15], ClientesArray[i][3], ClientesArray[i][2], parametro_id_cliente, parametro_id_recibo, ClientesArray[i][1], ClientesArray[i][16].replace(" ",""),  ClientesArray[i][5],  ClientesArray[i][18],  ClientesArray[i][19],  ClientesArray[i][20], parametro_id_sucursal,   ClientesArray[i][9],  ClientesArray[i][21]));
                mensaje=ClientesArray[i][16];
            }

        }
        catch (Exception ex) {

            mensaje = "ERROR: " + ex.getMessage();
            // data3.add(new CuotasCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        }
    }


    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            data3.add(new CuotasCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs.", "Cuota: 1 (100 Bs)","id_cliente","id_recibo","id_poliza_movimiento", "ramo", "monto_pagado", "resta", "sucursal", "id_sucursal", parametro_id_sucursal, "fecha_cuota", "no_liquidacion"));

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
            data3.remove(0);
            adapter.notifyDataSetChanged();

        }
    }

    private void eliminar_primer_elemento_rv(){
        //data3.remove(0);
        //actualizamos vista de la lista
        //adapter.notifyDataSetChanged();





        // data3.add(new CuotasCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        if(rvMusicas.getAdapter() != null){
            //De esta manera sabes si tu RecyclerView está vacío
            if(rvMusicas.getAdapter().getItemCount() == 0) {
                Toast.makeText(getApplicationContext(), "No existen cuotas pendientes a cancelar por el cliente...", Toast.LENGTH_LONG).show();
                //Aquí muestras el mensaje
            }
        }

    }

    private ArrayList<CuotasCliente> dataSet() {

        ArrayList<CuotasCliente> data = data3;

        return data;
    }

}
