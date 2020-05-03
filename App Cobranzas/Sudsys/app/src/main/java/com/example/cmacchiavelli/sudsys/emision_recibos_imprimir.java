package com.example.cmacchiavelli.sudsys;

import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class emision_recibos_imprimir extends AppCompatActivity {




    //incluimos el tool_bar
    private Toolbar toolbar;

    //para el webservice
    SoapObject resultString;
    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    String parametro_id_sucursal,id_sucursal_usuario, id_empleado_usuario, parametro_id_llave_unica_recibo, mensaje;
    String id_cliente,nombre_cliente,apellido_paterno_cliente,apellido_materno_cliente,ci_nit_cliente, id_recibo, el_concepto, forma_pago, banco, moneda, monto, numero_cheque, recibido_de, banco_completo, sucursal_recibo, id_sucursal_recibo, email_cliente;
    String parametro_tipo_emision,id_llave_unica_recibo, parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente,parametro_ci_nit_cliente, parametro_sucursal_recibo, parametro_email_cliente;

    //variables a recibir ws saveRecipent
    String respuesta_recibido, mensaje_recibido, id_recibo_recibido, numero_recibo_recibido,anio_recibo_recibido, parametro_tipo_cliente;
    //variables para imprimir
    String resultado, parametro_usuario_recibido, codigo_mac_impresora, mensaje_bancos;
    String numero_poliza_pre_amortizada, numero_cuota_pre_amortizada, monto_pre_amortizada, todo_pagado;

    String string_cuotas, parametro_numero_liquidacion;
    String string_cuotas_detalle;

    int cantidad_cuotas=0;
    int cantidad_cuotas_aux=0;

    SoapObject SoapArray;
    LinearLayout contenedor;

    private ZebraPrinter zebraPrinter;
    private Connection connection;
    ProgressDialog dialog, dialog2;


    //para el monto literal


    private int flag;
    public int numero;
    public String importe_parcial;
    public String num;
    public String num_letra;
    public String num_letras;
    public String num_letram;
    public String num_letradm;
    public String num_letracm;
    public String num_letramm;
    public String num_letradmm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision_recibos_imprimir);
        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Imprimir Recibo");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);


        //recuperamos variables
        Bundle datos=getIntent().getExtras();
        parametro_tipo_emision=datos.getString("parametro_tipo_emision");
        id_empleado_usuario=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_id_llave_unica_recibo=datos.getString("parametro_id_llave_unica_recibo");
        parametro_id_cliente =datos.getString("parametro_id_cliente");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        //Toast.makeText(getApplicationContext(), ""+parametro_tipo_cliente, Toast.LENGTH_LONG).show();
        parametro_email_cliente=datos.getString("parametro_email_cliente");
        id_sucursal_usuario=datos.getString("parametro_id_sucursal");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");
        //obtenemos la fecha actual
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date2 = new Date();
        String fecha2 = dateFormat2.format(date2);

        TextView txtCambiado4 = (TextView)findViewById(R.id.fecha);
        txtCambiado4.setText(fecha2.toString());

        TextView txtCambiado5 = (TextView)findViewById(R.id.email_cliente);
        txtCambiado5.setText(parametro_email_cliente);

        TextView txtCambiado6 = (TextView)findViewById(R.id.numero_liquidacion);
        txtCambiado6.setText(parametro_numero_liquidacion);

        contenedor =(LinearLayout) findViewById(R.id.contenedor_lista);

        if(parametro_tipo_emision.equals("Manual")) {
            parametro_usuario_recibido=datos.getString("parametro_usuario_recibido");
            toolbar.setBackgroundColor(Color.parseColor("#EE3F24"));
        }



            //llamamos al WebService  SaveRecipent
            emision_recibos_imprimir.TercerPlano tarea3 = new emision_recibos_imprimir.TercerPlano();
            tarea3.execute();

        //llamamos a la tarea tercero plano
        emision_recibos_imprimir.SeptimoPlano tarea_antes = new emision_recibos_imprimir.SeptimoPlano();
        tarea_antes.execute();


    }


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


    public void ejecutar_listado_cuotas_polizas(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_llave_unica_recibo", parametro_id_llave_unica_recibo);
        i.putExtra("parametro_id_sucursal", parametro_id_sucursal.toString());
        i.putExtra("parametro_id_empleado", id_empleado_usuario.toString());
        i.putExtra("parametro_tipo_emision", parametro_tipo_emision);
        i.putExtra("parametro_id_cliente", parametro_id_cliente);
        i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
        i.putExtra("parametro_id_recibo", id_recibo);
        i.putExtra("parametro_total_recibo", monto);
        i.putExtra("parametro_id_recibo_sudsys", id_recibo_recibido);
        i.putExtra("parametro_moneda", moneda);
        i.putExtra("parametro_numero_recibo_sudsys", numero_recibo_recibido);
        i.putExtra("parametro_nombre_cliente", parametro_nombre_cliente);
        i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);
        //i.putExtra("parametro_apellido_paterno_cliente", parametro_apellido_paterno_cliente);
        //i.putExtra("parametro_apellido_materno_cliente", parametro_apellido_materno_cliente);

        startActivity(i);
        finish();
    }


    //llamamos a la actividad inicio
    public void pantalla_inicio(View view){
        super.finish();
    }



    //para el metodo SaveRecipent del webservice
    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params){

            mostrar_recibo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);

            TextView txtIdReciboSudsys = (TextView) findViewById(R.id.id_recibo_sudsys);
            txtIdReciboSudsys.setText(id_recibo_recibido);

            TextView txtNumeroReciboSudsys = (TextView) findViewById(R.id.numero_recibo_sudsys);
            txtNumeroReciboSudsys.setText(numero_recibo_recibido);

            if(respuesta_recibido.toString().equals("1")) {
                //BOTON IMPRIMIR SE HABILITA
                Button boton = (Button) findViewById(R.id.imprime);
                boton.setVisibility(View.VISIBLE);
                if(parametro_tipo_emision.equals("Manual")) {
                    Toast.makeText(getApplicationContext(), "Recibo manual procesado", Toast.LENGTH_LONG).show();
                }else {
                    //Toast.makeText(getApplicationContext(), "" + mensaje, Toast.LENGTH_LONG).show();
                }
            }else {
                Button boton = (Button) findViewById(R.id.imprime);
                boton.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), mensaje+"Error no se puede habilitar el boton imprimir.", Toast.LENGTH_LONG).show();
            }

            //habilitar para ver errores del webService
            //TextView txtMonto = (TextView)findViewById(R.id.txtmonto);
            //txtMonto.setText(mensaje);



        }


    }

    private void mostrar_recibo() {
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA1,//1 id
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA2,//2 recibido_de
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA3,//3 concepto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA4,//4 tipo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA5,//5 moneda
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA6,//6 banco
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA7,//7 cheque
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA8,//8 monto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA9,//9 id_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA10,//10 ci_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA11,//11 id_recibo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA12,//12 sucursal_recibo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA13//13 email_cliente

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD_Recibos.NOMBRE_COLUMNA11 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { parametro_id_llave_unica_recibo.toString() };

        try {

            Cursor c = db.query(
                    Estructura_BBDD_Recibos.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //se mueve el cursor al primer registro

            c.moveToFirst();

            //mostramos los valores recuperados de la base de datos de recibos

            TextView txtNombreCliente = (TextView) findViewById(R.id.nombre_cliente);
            txtNombreCliente.setText(c.getString(1));
            TextView txtConcepto = (TextView) findViewById(R.id.tvconcepto);
            txtConcepto.setText(c.getString(2));
            TextView txtTipoPago = (TextView) findViewById(R.id.txttipo_pago);
            txtTipoPago.setText(c.getString(3));

            parametro_nombre_cliente=c.getString(1);

            //sacamos id_banco
            banco_completo = c.getString(5);
            String[] parts2 = banco_completo.split("-");
            banco = parts2[0];
            //
            TextView txtBanco = (TextView) findViewById(R.id.txtbanco);
            txtBanco.setText(banco_completo.toString());


            TextView txtCheque = (TextView) findViewById(R.id.txtnumero_cheque);
            txtCheque.setText(c.getString(6));
            TextView txtMonto = (TextView) findViewById(R.id.txtmonto);
            txtMonto.setText(c.getString(7) + " " + c.getString(4));
            TextView txtSucursal = (TextView) findViewById(R.id.txtSucursal);
            txtSucursal.setText(c.getString(11));

            parametro_sucursal_recibo=c.getString(11);



            //otras variables agarradas de la base de datos
            id_cliente = c.getString(8);
            id_recibo = ci_nit_cliente = c.getString(10).toString();
            el_concepto = c.getString(2);
            forma_pago = c.getString(3);
            moneda = c.getString(4);
            //sacamos id_banco
            banco_completo = c.getString(5);
            String[] parts = banco_completo.split("-");
            banco = parts[0].toString();
            //
            numero_cheque = c.getString(6);
            monto = c.getString(7);
            recibido_de = c.getString(1);
            email_cliente = c.getString(12);
            sucursal_recibo = c.getString(11);
            //recibimos asi porque el la base de datos el nombre del cliente esta junto
            nombre_cliente = parametro_nombre_cliente;
            apellido_paterno_cliente = parametro_apellido_paterno_cliente;
            apellido_materno_cliente = parametro_apellido_materno_cliente;
            ci_nit_cliente = parametro_ci_nit_cliente;
            //
            parametro_id_cliente=id_cliente;

            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(numero_cheque.toString().trim().isEmpty() || numero_cheque.toString().equals("anyType{}")) {
                TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                textcargando2.setVisibility(View.GONE);
                TextView textcargando3 = (TextView) findViewById(R.id.txtnumero_cheque);
                textcargando3.setVisibility(View.GONE);

                TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                textcargando4.setVisibility(View.GONE);
                TextView textcargando5 = (TextView) findViewById(R.id.txtbanco);
                textcargando5.setVisibility(View.GONE);
            }


            //almacenamos variables para la impresion del recibo manual
            if (parametro_tipo_emision.equals("Manual")) {
                //almacenamos variables recibidas del ws
                respuesta_recibido = "1";
                mensaje_recibido = "Recibo manual";
                id_recibo_recibido = "" + parametro_id_llave_unica_recibo;
                //creamos llave unica para recibos
                //obtenemos la fecha actual
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-ss", Locale.getDefault());
                Date date = new Date();
                String fecha = dateFormat.format(date);
                String[] parts4 = fecha.split("-");
                numero_recibo_recibido = parametro_usuario_recibido+"-"+parts4[3]+id_empleado_usuario+parametro_id_sucursal+"-OFF";

                anio_recibo_recibido = "2018";
            }

            //almacenamos variables para la impresion del recibo automatica
            if (parametro_tipo_emision.equals("Automatica")){

            String SOAP_ACTION = "http://ws.sudseguros.com/SaveReceipt";
            String METHOD_NAME = "SaveReceipt";
            String NAMESPACE = "http://ws.sudseguros.com/";
            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

            //enviamos parametros al ws SaveReceipt

                try {

                    SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

                    RequestWs.addProperty("id_cliente", id_cliente);
                    RequestWs.addProperty("id_sucursal", "6");
                    RequestWs.addProperty("recibido_de", recibido_de);
                    RequestWs.addProperty("concepto", el_concepto);
                    RequestWs.addProperty("tipo_pago", forma_pago);
                    RequestWs.addProperty("moneda", moneda);
                    RequestWs.addProperty("id_entidad_financiera", banco);
                    RequestWs.addProperty("cheque_numero", numero_cheque);
                    RequestWs.addProperty("importe", monto);
                    RequestWs.addProperty("id_empleado", id_empleado_usuario);
                    RequestWs.addProperty("imei_dispositivo", "357136081571342");
                    RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
                    RequestWs.addProperty("password_ws", "AXr53.o1");
                    RequestWs.addProperty("sucursal_recibo", sucursal_recibo);

                    SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    soapEnvelope.dotNet = true; //tipo de servicio .net
                    soapEnvelope.setOutputSoapObject(RequestWs);


                    //Invoca al web service
                    HttpTransportSE transport = new HttpTransportSE(URL);
                    transport.call(SOAP_ACTION, soapEnvelope);

                    resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

                    //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
                    mensaje = "" + resultString.getProperty(1);
                    //almacenamos variables recibidas del ws
                    respuesta_recibido = "" + resultString.getProperty(0);
                    mensaje_recibido = "" + resultString.getProperty(1);
                    id_recibo_recibido = "" + resultString.getProperty(2);
                    numero_recibo_recibido = "" + resultString.getProperty(3);
                    anio_recibo_recibido = "" + resultString.getProperty(4);
                } catch (Exception ex) {
                    mensaje = "ERROR 2: " + ex.getMessage();
                }

        }



        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "No se encontro registro", Toast.LENGTH_LONG).show();
        }

    }



    private void enviar_email() {
        //WS MTODO ENVIAR CORREO SendEmail
        String SOAP_ACTION = "http://ws.sudseguros.com/SendEmail";
        String METHOD_NAME = "SendEmail";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //enviamos parametros al ws SaveReceipt

        try {

            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

            RequestWs.addProperty("id_cliente", id_cliente);
            RequestWs.addProperty("id_sucursal", parametro_id_sucursal);
            RequestWs.addProperty("id_sucursal_recibo", id_sucursal_recibo);
            RequestWs.addProperty("recibido_de", recibido_de);
            RequestWs.addProperty("concepto", el_concepto);
            RequestWs.addProperty("tipo_pago", forma_pago);
            RequestWs.addProperty("moneda", moneda);
            RequestWs.addProperty("id_entidad_financiera", banco);
            RequestWs.addProperty("cheque_numero", numero_cheque);
            RequestWs.addProperty("importe", monto);
            RequestWs.addProperty("id_empleado", id_empleado_usuario);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");
            RequestWs.addProperty("sucursal_recibo", sucursal_recibo);
            RequestWs.addProperty("numero_recibo", numero_recibo_recibido);
            RequestWs.addProperty("nombre_cliente", nombre_cliente);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
        } catch (Exception ex) {
            mensaje = "ERROR 2: " + ex.getMessage();
        }
    }



    private void enviar_email_cliente() {
        //WS MTODO ENVIAR CORREO SendEmailClient
        String SOAP_ACTION = "http://ws.sudseguros.com/SendEmailClient";
        String METHOD_NAME = "SendEmailClient";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //enviamos parametros al ws SaveReceipt

        try {

            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

            RequestWs.addProperty("id_cliente", id_cliente);
            RequestWs.addProperty("id_sucursal", parametro_id_sucursal);
            RequestWs.addProperty("id_sucursal_recibo", id_sucursal_recibo);
            RequestWs.addProperty("recibido_de", recibido_de);
            RequestWs.addProperty("concepto", el_concepto);
            RequestWs.addProperty("tipo_pago", forma_pago);
            RequestWs.addProperty("moneda", moneda);
            RequestWs.addProperty("id_entidad_financiera", banco);
            RequestWs.addProperty("cheque_numero", numero_cheque);
            RequestWs.addProperty("importe", monto);
            RequestWs.addProperty("id_empleado", id_empleado_usuario);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");
            RequestWs.addProperty("sucursal_recibo", sucursal_recibo);
            RequestWs.addProperty("numero_recibo", numero_recibo_recibido);
            RequestWs.addProperty("nombre_cliente", nombre_cliente);
            RequestWs.addProperty("email_cliente", parametro_email_cliente);



            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
        } catch (Exception ex) {
            mensaje = "ERROR 3: " + ex.getMessage();
        }
    }








    public void ejecutar_impresion(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Recibo enviado a imprimir...", Toast.LENGTH_LONG).show();
            CuartoPlanoImpresion tarea = new CuartoPlanoImpresion();
            tarea.execute();
        }
    }

    private class CuartoPlanoImpresion extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);*/
            dialog2 = new ProgressDialog(emision_recibos_imprimir.this);
            dialog2.setMessage("Imprimiendo...");
            dialog2.setCancelable (false);
            dialog2.show();

            Button boton = (Button) findViewById(R.id.imprime);
            boton.setVisibility(View.GONE);

            //VERIFICAMOS SUCURSAL DEL RECIBO
            if(sucursal_recibo.equals("Santa Cruz")){
                id_sucursal_recibo="2";
            }
            if(sucursal_recibo.equals("La Paz")){
                id_sucursal_recibo="1";
            }
            if(sucursal_recibo.equals("Cochabamba")){
                id_sucursal_recibo="3";
            }
            if(sucursal_recibo.equals("Tarija")){
                id_sucursal_recibo="5";
            }

            //VERIFICAMOS SI LA SUCURSAL DEL RECIBO ES LA MISMA QUE DEL USUARIO
            if(id_sucursal_recibo.equals(parametro_id_sucursal)) {
                if (parametro_tipo_cliente.equals("NATURAL") || parametro_tipo_cliente.equals("Natural")) {
                    Button boton2 = (Button) findViewById(R.id.amortizar);
                    boton2.setVisibility(View.GONE);
                } else {
                    //ImageView img_info2 = (ImageView) findViewById(R.id.info);
                    //img_info2.setVisibility(View.GONE);

                    TextView tv_alerta2 = (TextView) findViewById(R.id.tv_alerta);
                    tv_alerta2.setVisibility(View.GONE);
                }
            }else {
                TextView tv_alerta3 = (TextView) findViewById(R.id.tv_alerta_sucursal);
                tv_alerta3.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... params){

            String id_recibo = id_recibo_recibido;
            String id_empleado = id_empleado_usuario;
            String imei_dispositivo = "357136081571342";
            String usuario_ws = "SUdMOv1l3";
            String password_ws = "AXr53.o1";

            if(parametro_tipo_emision.equals("Manual")){
                receipPrint_manual(id_recibo, id_empleado, imei_dispositivo, usuario_ws, password_ws);
            }
            if(parametro_tipo_emision.equals("Automatica")){
                //preguntamos si hay conexion a internet, sino no pasa a la siguiente ventana con finsh()
                if (!compruebaConexion(getApplicationContext())) {
                    Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else {
                    receipPrint(id_recibo, id_empleado, imei_dispositivo, usuario_ws, password_ws);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);*/
            dialog2.hide();
            Button boton = (Button) findViewById(R.id.imprime);
            boton.setVisibility(View.VISIBLE);

            //VERIFICAMOS SUCURSAL DEL RECIBO
            if(sucursal_recibo.equals("Santa Cruz")){
                id_sucursal_recibo="2";
            }
            if(sucursal_recibo.equals("La Paz")){
                id_sucursal_recibo="1";
            }
            if(sucursal_recibo.equals("Cochabamba")){
                id_sucursal_recibo="3";
            }
            if(sucursal_recibo.equals("Tarija")){
                id_sucursal_recibo="5";
            }

            //VERIFICAMOS SI LA SUCURSAL DEL RECIBO ES LA MISMA QUE DEL USUARIO
            if(id_sucursal_recibo.equals(parametro_id_sucursal)) {
                if (parametro_tipo_cliente.equals("NATURAL") || parametro_tipo_cliente.equals("Natural")) {
                    Button boton2 = (Button) findViewById(R.id.amortizar);
                    boton2.setVisibility(View.VISIBLE);
                } else {
                    //ImageView img_info2 = (ImageView) findViewById(R.id.info);
                    //img_info2.setVisibility(View.GONE);

                    TextView tv_alerta2 = (TextView) findViewById(R.id.tv_alerta);
                    tv_alerta2.setVisibility(View.VISIBLE);
                }
            }else {
                QuintoPlanoEmail tarea = new QuintoPlanoEmail();
                tarea.execute();
                TextView tv_alerta3 = (TextView) findViewById(R.id.tv_alerta_sucursal);
                tv_alerta3.setVisibility(View.VISIBLE);
                //tv_alerta3.setText(mensaje);
            }
            SextoPlanoEmail tarea = new SextoPlanoEmail();
            tarea.execute();
        }
    }



    //para el metodo SaveRecipent del webservice
    private class QuintoPlanoEmail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);*/
            dialog = new ProgressDialog(emision_recibos_imprimir.this);
            dialog.setMessage("Enviando correo sucursal...");
            dialog.setCancelable (false);
            //dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

            enviar_email();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            Toast.makeText(getApplicationContext(), "Estado envio notificacion sucursales: "+mensaje, Toast.LENGTH_LONG).show();
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);*/
            dialog.hide();

        }


    }

    //para el metodo SaveRecipent del webservice
    private class SextoPlanoEmail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);*/
            dialog = new ProgressDialog(emision_recibos_imprimir.this);
            dialog.setMessage("Enviando correo cliente...");
            dialog.setCancelable (false);
            //dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

            enviar_email_cliente();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //Toast.makeText(getApplicationContext(), "email cliente: " + email_cliente, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Notificacion: "+mensaje, Toast.LENGTH_LONG).show();
            /*ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.GONE);*/
            dialog.hide();

        }


    }

    //llamamos al metodo que lista preamortizaciones
    class SeptimoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

        }

        @Override
        protected Void doInBackground(Void... params){
            lista_cuotas_pre_amortizadas();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), mensaje_bancos, Toast.LENGTH_LONG).show();
        }
    }

    private void lista_cuotas_pre_amortizadas(){

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintCuotaPreAmortizada";
        String METHOD_NAME = "PrintCuotaPreAmortizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_cliente", id_cliente);
            Request.addProperty("id_sucursal", parametro_id_sucursal.toString());
            Request.addProperty("id_empleado", id_empleado_usuario.toString());
            Request.addProperty("id_recibo", id_recibo_recibido);
            Request.addProperty("tipo", "terminado");
            Request.addProperty("imei_dispositivo", "357136081571342" );
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
            //mensaje habilitar para ver respuesta del ws
            //mensaje = "OK: " + resultString.getProperty(0) + " " + resultString.getProperty(1);
            //mensaje normal
            int k=0;
            int numero_columnas = 9;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];



            for (int i = 0; i < SoapArrayNivel2.getPropertyCount(); i++) {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k = j;
                }

                final String  numero_poliza_pre_amortizada = ClientesArray[i][1];
                final String numero_cuota_pre_amortizada = ClientesArray[i][2];
                final String monto_pre_amortizada = ClientesArray[i][4];

                //LISTAMOS CUOTAS PRE AMORTIZADAS, DIBUJANDO MANUALMENTE ELEMENTOS CON  runOnUiThread new Runnable()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView miTextView2 = new TextView(getApplicationContext());
                        miTextView2.setText("Cancelar: ");
                        miTextView2.setBackgroundColor(Color.RED);
                        miTextView2.setTextColor(Color.WHITE);
                        miTextView2.setTextSize(13);
                        miTextView2.setGravity(Gravity.CENTER);

                        //Agrega vistas al contenedor.
                        //contenedor.addView(miImageView);
                        contenedor.addView(miTextView2);

                        TextView miTextView = new TextView(getApplicationContext());
                        miTextView.setText("No. Póliza: "+numero_poliza_pre_amortizada+" Cuota: "+numero_cuota_pre_amortizada+" Pagar: "+monto_pre_amortizada.toString());
                        miTextView.setBackgroundColor(Color.BLUE);
                        miTextView.setTextColor(Color.WHITE);
                        miTextView.setTextSize(13);
                        miTextView.setGravity(Gravity.CENTER);



                        contenedor.addView(miTextView);

                    }
                });




                //Mensaje si guardo los datos del acceso exitosamente
                mensaje_bancos = "Registro de Banco Exitoso ";

            }
            mensaje_bancos = "Almacenamiento bancos exitososss" +SoapArrayNivel2.getPropertyCount();

        }
        catch (Exception ex) {
            mensaje_bancos = "ERROR: " + ex.getMessage();
        }
    }

    //metodo que imprime
    private void receipPrint(String id_recibo, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintReceipt";
        String METHOD_NAME = "PrintReceipt";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        String codigo = "";
        String mensaje = "";
        String id_sucursal = "";
        String numero_recibo = "";
        String fecha_emision = "";
        String hora_emision = "";
        String ci_nit = "";
        String cliente = "";
        String recibido_de = "";
        String moneda = "";
        String importe = "";
        String importe_bs = "";
        String monto_literal = "";
        String concepto = "";
        String tipo_pago = "";
        String cheque_banco = "";
        String cheque_numero = "";
        String cobrador = "";

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String importe_final;
        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo", id_recibo);
            Request.addProperty("id_empleado", id_empleado);
            Request.addProperty("imei_dispositivo", imei_dispositivo);
            Request.addProperty("usuario_ws", usuario_ws);
            Request.addProperty("password_ws", password_ws);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Agarra todo el Objeto
            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


            codigo = SoapArray.getProperty(0).toString();
            mensaje = SoapArray.getProperty(1).toString();
            id_sucursal = SoapArray.getProperty(2).toString();
            id_recibo = SoapArray.getProperty(3).toString();
            numero_recibo = SoapArray.getProperty(4).toString();
            fecha_emision = SoapArray.getProperty(5).toString();
            hora_emision = SoapArray.getProperty(6).toString();
            ci_nit = SoapArray.getProperty(7).toString();
            cliente = SoapArray.getProperty(8).toString();
            recibido_de = SoapArray.getProperty(9).toString();
            moneda = SoapArray.getProperty(10).toString();
            importe = SoapArray.getProperty(11).toString();
            importe_bs = SoapArray.getProperty(12).toString();
            monto_literal = SoapArray.getProperty(13).toString();
            concepto = SoapArray.getProperty(14).toString();
            tipo_pago = SoapArray.getProperty(15).toString();
            cheque_banco = SoapArray.getProperty(16).toString();
            cheque_numero = SoapArray.getProperty(17).toString();
            cobrador = SoapArray.getProperty(18).toString();

            sucursal = SoapArray.getProperty(19).toString();
            sucursal_abreviatura = SoapArray.getProperty(20).toString();

            todo_pagado = SoapArray.getProperty(22).toString();
            string_cuotas = todo_pagado.toString();


            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }

            resultado = "Respuesta: " + codigo;

        } catch (Exception ex) {
            resultado = "ERROR 1: " + ex.getMessage() + " " + ex.getLocalizedMessage();
        }

        connectPrint(); //Conexion con la Impresora


        if (codigo.equals("1") && zebraPrinter != null) {

            try {

                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                PrinterStatus printerStatus = printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {

                    resultado = "Listo para imprimir ";


                    if(moneda.equals("$us")){
                        importe_final = importe;
                    }else{
                        importe_final = importe_bs;
                    }

                    if(id_sucursal.equals("1")) {
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }
                    else if(id_sucursal.equals("2")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 3-3416055 Fax: (591) 3-3416055^FS\n" +
                                "^FT144,104^A0N,17,16^FH\\^FDCalle La Plata # 25 - Equipetrol, Santa Cruz^FS\n" +
                                "^FT228,77^A0N,17,16^FH\\^FDSucursal Santa Cruz^FS";
                    }
                    else if(id_sucursal.equals("3")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 4-4374220 ^FS\n" +
                                "^FT124,104^A0N,17,16^FH\\^FDEdificio Torre Empresarial Torre 42 \\A2n Piso 7 Calle Papa Paulo esquina Ramón Rivero # 604^FS\n" +
                                "^FT220,77^A0N,17,16^FH\\^FDSucursal Cochabamba^FS";;
                    }
                    else if(id_sucursal.equals("5")) {
                        datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                "^FT244,77^A0N,17,16^FH\\^FDSucursal Tarija^FS";
                    }else{
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }


                    String texto = "\u0010CT~~CD,~CC^~CT~\n" +
                            "^XA~TA000~JSN^LT0^MNN^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA\n" +
                            "^MMT\n" +
                            "^PW591\n";
                    if(id_sucursal.equals(id_sucursal_usuario)) {
                        int tamano_vertical=830;
                        String[] partsaux = string_cuotas.split("CUOTA");
                        cantidad_cuotas_aux = partsaux.length - 1;
                        for (int i = 1; i <= cantidad_cuotas_aux; i++) {
                            tamano_vertical=tamano_vertical+20;
                            texto += "^LL0"+tamano_vertical+"\n";
                        }
                    }else {
                        texto+= "^LL0830\n";
                    }

                    texto+= "^LS0\n" +
                            "^FT292,237^A0N,21,21^FH\\^FD"+numero_recibo+"/"+sucursal_abreviatura+"^FS\n" +
                            "^FT221,406^A0N,21,21^FH\\^FDFORMA DE PAGO^FS\n" +
                            "^FT193,237^A0N,21,21^FH\\^FDN\\F8 Recibo^FS\n" +
                            "^FT217,197^A0N,25,24^FH\\^FDRECIBO OFICIAL^FS\n" +
                            "^FT190,48^A0N,25,24^FH\\^FDSUDAMERICANA S.R.L^FS\n" +
                            "^FO9,251^GB574,0,1^FS\n" +
                            "^FO5,494^GB575,0,1^FS\n" +
                            "^FO7,377^GB572,0,0^FS\n" +
                            "^FO9,208^GB574,0,1^FS\n" +
                            "^FT458,277^A0N,17,16^FH\\^FD"+hora_emision+"^FS\n" +
                            "^FT415,277^A0N,17,16^FH\\^FDHora:^FS\n" +
                            "^FT65,278^A0N,17,16^FH\\^FD"+fecha_emision+"^FS\n" +
                            "^FT15,278^A0N,17,16^FH\\^FDFecha:^FS\n" +



                            "^FT15,321^A0N,21,21^FH\\^FDHe recibido de:^FS\n" +
                            "^FT158,321^A0N,21,21^FH\\^FD"+recibido_de+"^FS\n" +

                            "^FT16,361^A0N,21,21^FH\\^FDPor Concepto de:^FS\n" +
                            "^FT170,361^A0N,21,21^FH\\^FD"+concepto+"^FS\n" +

                            "^FT19,474^A0N,17,16^FH\\^FD"+tipo_pago+"^FS\n" +
                            "^FT129,473^A0N,17,16^FH\\^FD"+cheque_numero+"^FS\n" +
                            "^FT352,474^A0N,17,16^FH\\^FD"+moneda+"^FS\n" +
                            "^FT464,475^A0N,17,16^FH\\^FD"+importe_final+"^FS\n" +
                            "^FT354,442^A0N,21,21^FH\\^FDMoneda^FS\n" +
                            "^FT464,441^A0N,21,21^FH\\^FDMonto^FS\n" +
                            "^FT129,443^A0N,21,21^FH\\^FDCheque Nro^FS\n" +
                            "^FT18,442^A0N,21,21^FH\\^FDTipo^FS\n";

                    //CUOTAS PRE AMORTIZDAS
                    if(id_sucursal.equals(id_sucursal_usuario)) {

                        texto +=     "^FT18,543^A0N,17,17^FH\\^FDNro Poliza^FS\n" +
                                "^FT274,543^A0N,17,17^FH\\^FDRamo^FS\n" +
                                "^FT334,543^A0N,17,17^FH\\^FDCuota^FS\n" +
                                "^FT394,543^A0N,17,17^FH\\^FDFecha Cuota^FS\n" +
                                "^FT494,543^A0N,17,17^FH\\^FDMonto^FS\n";
                        //"^FT18,542^A0N,21,21^FH\\^FDNro^FS\n";
                        int espacio_vertical_col1 = 542;
                        int espacio_vertical_col2 = 541;
                        int espacio_vertical_col3 = 543;
                        int espacio_vertical_col4 = 542;

                        String[] parts = string_cuotas.split("CUOTA");
                        cantidad_cuotas = parts.length - 1;

                        for (int i = 1; i <= cantidad_cuotas; i++) {
                            espacio_vertical_col1 = espacio_vertical_col1 + 20;
                            espacio_vertical_col2 = espacio_vertical_col2 + 20;
                            espacio_vertical_col3 = espacio_vertical_col3 + 20;
                            espacio_vertical_col4 = espacio_vertical_col4 + 20;

                            String[] parts2 = parts[i].toString().split("\\*");
                            String no_poliza_detalle = parts2[2];
                            String no_cuota_detalle = parts2[3];
                            String monto_pagar_detalle = parts2[5];

                            String ramo = parts2[12];
                            //FECHA
                            String fecha_pago_cuota = parts2[11];
                            String[] parts3 = fecha_pago_cuota.toString().split(" ");
                            String fecha_pago_cuota_sin_hora=parts3[0];

                            cantidad_cuotas = parts.length - 1;

                            texto +=
                                    "^FT18," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + no_poliza_detalle + "^FS\n" +
                                            "^FT274," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + ramo + "^FS\n" +
                                            "^FT350," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD"+no_cuota_detalle+"^FS\n" +
                                            "^FT394," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD"+fecha_pago_cuota_sin_hora+"^FS\n" +
                                            "^FT494," + espacio_vertical_col3 + "^A0N,15,15^FH\\^FD" + monto_pagar_detalle + " " + moneda + "^FS\n";
                            //"^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+i+"^FS\n";
                            if (i == cantidad_cuotas) {
                                texto += "^FT16," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                        "^FT69," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +


                                        "^FT43," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                                        "^FT16," + (espacio_vertical_col3 + 33) + "^A0N,13,12^FH\\^FDSon:^FS\n" +
                                        "^FT18," + (espacio_vertical_col3 + 271) + "^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                        "^FT57," + (espacio_vertical_col3 + 256) + "^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                        "^FT18," + (espacio_vertical_col3 + 243) + "^BQN,2,5\n" +
                                        "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                                        "^FT17," + (espacio_vertical_col3 + 255) + "^A0N,17,16^FH\\^FDNota:^FS\n" +
                                        "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                        "^FT382," + (espacio_vertical_col3 + 188) + "^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                                        "^PQ1,0,1,Y^XZ\n";
                            }
                        }
                    }else{
                        int espacio_vertical_col1 = 542;
                        int espacio_vertical_col2 = 541;
                        int espacio_vertical_col3 = 543;
                        int espacio_vertical_col4 = 542;

                        texto += "^FT354," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n" +
                                "^FT464," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n" +
                                "^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD-^FS\n";

                        texto += "^FT16," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                "^FT69," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +


                                "^FT43," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                                "^FT16," + (espacio_vertical_col3 + 33) + "^A0N,13,12^FH\\^FDSon:^FS\n" +
                                "^FT18," + (espacio_vertical_col3 + 271) + "^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                "^FT57," + (espacio_vertical_col3 + 256) + "^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                "^FT18," + (espacio_vertical_col3 + 243) + "^BQN,2,5\n" +
                                "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                                "^FT17," + (espacio_vertical_col3 + 255) + "^A0N,17,16^FH\\^FDNota:^FS\n" +
                                "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                "^FT382," + (espacio_vertical_col3 + 188) + "^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                                "^PQ1,0,1,Y^XZ\n";
                    }
                    ///////////////////////



                    byte[] rcpt = texto.getBytes();
                    try {
                        connection.write(rcpt);
                        resultado = "Documento Impreso ";
                    } catch (ConnectionException e) {
                        resultado = "Error al Imprimir "+ e.getMessage();
                    }

                } else if (printerStatus.isPaused) {
                    resultado = "No se puede imprimir por que el dipositivo detenido.";
                } else if (printerStatus.isHeadOpen) {
                    resultado = "No se puede imprimir por que la cabecera esta abierta.";
                } else if (printerStatus.isPaperOut) {
                    resultado = "No se puede imprimir por que el papel esta afuera.";
                } else {
                    resultado = "No se puede imprimir.";
                }

            } catch (ConnectionException e) {
                e.printStackTrace();

            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }


        }
        else {
            //resultado = "No se puede imprimir, error de Impresora 5";
        }
    }




    //metodo que imprime emisiones manuales
    //metodo que imprime
    private void receipPrint_manual(String id_recibo, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {

        String codigo = "";
        String mensaje = "";
        String id_sucursal = "";
        String numero_recibo = "";
        String fecha_emision = "";
        String hora_emision = "";
        String ci_nit = "";
        String cliente = "";
        String recibido_de = "";
        String moneda = "";
        String importe = "";
        String importe_bs = "";
        String monto_literal = "";
        String concepto = "";
        String tipo_pago = "";
        String cheque_banco = "";
        String cheque_numero = "";
        String cobrador = "";

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String importe_final;

        connectPrint(); //Conexion con la Impresora

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA1,//1 id
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA2,//2 recibido_de
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA3,//3 concepto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA4,//4 tipo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA5,//5 moneda
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA6,//6 banco
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA7,//7 cheque
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA8,//8 monto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA9,//9 id_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA10,//10 ci_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA11//11 id_recibo

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD_Recibos.NOMBRE_COLUMNA11 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = {parametro_id_llave_unica_recibo.toString()};

             /*   String sortOrder =
                        Estructura_BBDD.NOMBRE_COLUMNA1 + " DESC";*/

        try {

            Cursor c = db.query(
                    Estructura_BBDD_Recibos.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //se mueve el cursor al primer registro

            c.moveToFirst();

           //almacenamos variables que se imprimiran
            codigo = "";
            mensaje = "Impresion manual";
            id_sucursal = parametro_id_sucursal;
            id_recibo = c.getString(10).toString();
            //creamos llave unica para recibos
            //obtenemos la fecha actual
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-ss", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            String[] parts = fecha.split("-");
            numero_recibo = parametro_usuario_recibido+"-"+parts[3]+id_empleado+id_sucursal+"-OFF";

            //fecha actual
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());;
            Date date2 = new Date();
            String fecha2 = dateFormat2.format(date2);
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());;
            Date date3 = new Date();
            String fecha3 = dateFormat3.format(date3);


            fecha_emision = fecha2;
            hora_emision = fecha3;


            ci_nit = c.getString(9).toString();
            cliente = c.getString(1).toString();
            recibido_de = c.getString(1).toString();
            moneda = c.getString(4);
            importe = c.getString(7);
            importe_bs = c.getString(7);

            String literal_moneda="";
            if(c.getString(4).equals("Bs")){
                literal_moneda="Bolivianos";
            }else {
                literal_moneda="Dolares";
            }
            monto_literal = convertirLetras(Integer.parseInt(c.getString(7)))+" 00/100.- "+literal_moneda;
            concepto = c.getString(2);
            tipo_pago = c.getString(3);
            cheque_banco = c.getString(5);
            cheque_numero =  numero_cheque = c.getString(6);
            cobrador = parametro_usuario_recibido;

            sucursal = parametro_id_sucursal;
            sucursal_abreviatura = parametro_id_sucursal;

            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }




        if (zebraPrinter != null) {

            try {

                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                PrinterStatus printerStatus = printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {

                    resultado = "Listo para imprimir ";


                    if (moneda.equals("$us")) {
                        importe_final = importe;
                    } else {
                        importe_final = importe_bs;
                    }

                    if (id_sucursal.equals("1")) {
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    } else if (id_sucursal.equals("2")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 3-3416055 Fax: (591) 3-3416055^FS\n" +
                                "^FT144,104^A0N,17,16^FH\\^FDCalle La Plata # 25 - Equipetrol, Santa Cruz^FS\n" +
                                "^FT228,77^A0N,17,16^FH\\^FDSucursal Santa Cruz^FS";
                    } else if (id_sucursal.equals("3")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 4-4374220 ^FS\n" +
                                "^FT124,104^A0N,17,16^FH\\^FDEdificio Torre Empresarial Torre 42 \\A2n Piso 7 Calle Papa Paulo esquina Ramón Rivero # 604^FS\n" +
                                "^FT220,77^A0N,17,16^FH\\^FDSucursal Cochabamba^FS";;
                    } else if (id_sucursal.equals("5")) {
                        datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                "^FT244,77^A0N,17,16^FH\\^FDSucursal Tarija^FS";
                    } else {
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }


                    String texto = "\u0010CT~~CD,~CC^~CT~\n" +
                            "^XA~TA000~JSN^LT0^MNN^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA\n" +
                            "^MMT\n" +
                            "^PW591\n" +
                            "^LL0768\n" +
                            "^LS0\n" +
                            "^FT222,237^A0N,21,21^FH\\^FD" + numero_recibo + "^FS\n" +
                            "^FT221,406^A0N,21,21^FH\\^FDFORMA DE PAGO^FS\n" +
                            "^FT123,237^A0N,21,21^FH\\^FDN\\F8 Recibo^FS\n" +
                            "^FT217,197^A0N,25,24^FH\\^FDRECIBO OFICIAL^FS\n" +
                            "^FT190,48^A0N,25,24^FH\\^FDSUDAMERICANA S.R.L^FS\n" +
                            "^FO9,251^GB574,0,1^FS\n" +
                            "^FO5,494^GB575,0,1^FS\n" +
                            "^FO7,377^GB572,0,0^FS\n" +
                            "^FO9,208^GB574,0,1^FS\n" +
                            "^FT458,277^A0N,17,16^FH\\^FD" + hora_emision + "^FS\n" +
                            "^FT415,277^A0N,17,16^FH\\^FDHora:^FS\n" +
                            "^FT65,278^A0N,17,16^FH\\^FD" + fecha_emision + "^FS\n" +
                            "^FT15,278^A0N,17,16^FH\\^FDFecha:^FS\n" +
                            "^FT170,361^A0N,21,21^FH\\^FD" + concepto + "^FS\n" +
                            "^FT69,538^A0N,13,12^FH\\^FD" + cobrador + "^FS\n" +
                            "^FT158,321^A0N,21,21^FH\\^FD" + recibido_de + "^FS\n" +
                            "^FT19,474^A0N,17,16^FH\\^FD" + tipo_pago + "^FS\n" +
                            "^FT129,473^A0N,17,16^FH\\^FD" + cheque_numero + "^FS\n" +
                            "^FT352,474^A0N,17,16^FH\\^FD" + moneda + "^FS\n" +
                            "^FT464,475^A0N,17,16^FH\\^FD" + importe_final + "^FS\n" +
                            "^FT354,442^A0N,21,21^FH\\^FDMoneda^FS\n" +
                            "^FT464,441^A0N,21,21^FH\\^FDMonto^FS\n" +
                            "^FT129,443^A0N,21,21^FH\\^FDCheque Nro^FS\n" +
                            "^FT18,442^A0N,21,21^FH\\^FDTipo^FS\n" +
                            "^FT16,537^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                            "^FT16,361^A0N,21,21^FH\\^FDPor Concepto de:^FS\n" +
                            "^FT15,320^A0N,21,21^FH\\^FDHe recibido de:^FS\n" +
                            "^FT43,514^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                            "^FT16,513^A0N,13,12^FH\\^FDSon:^FS\n" +
                            "^FT18,751^A0N,17,16^FH\\^FDSeguros^FS\n" +
                            "^FT57,726^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                            "^FT18,713^BQN,2,5\n" +
                            "^FDMA," + ci_nit + "|" + importe_final + "|" + fecha_emision + "|" + id_recibo + "^FS\n" +//qr
                            "^FT17,725^A0N,17,16^FH\\^FDNota:^FS\n" +
                            "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                            "^FT382,668^A0N,17,16^FH\\^FDFirma Cliente^FS\n" +
                            "^PQ1,0,1,Y^XZ\n";


                    byte[] rcpt = texto.getBytes();
                    try {
                        connection.write(rcpt);
                        resultado = "Documento Impreso ";
                    } catch (ConnectionException e) {
                        resultado = "Error al Imprimir " + e.getMessage();
                    }

                } else if (printerStatus.isPaused) {
                    resultado = "No se puede imprimir por que el dipositivo detenido.";
                } else if (printerStatus.isHeadOpen) {
                    resultado = "No se puede imprimir por que la cabecera esta abierta.";
                } else if (printerStatus.isPaperOut) {
                    resultado = "No se puede imprimir por que el papel esta afuera.";
                } else {
                    resultado = "No se puede imprimir.";
                }

                resultado = "Documento impreso ";
            } catch (ConnectionException e) {
                e.printStackTrace();
            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }


        }
        else {
            resultado = "No se puede imprimir, error de Impresora 4";
        }
    }catch (Exception e){
        Toast.makeText(getApplicationContext(), "no se encontro registro", Toast.LENGTH_LONG).show();
            resultado = "Error al Imprimir "+ e.getMessage();
    }
    }


    public void connectPrint() {
        //OBTENEMOS MAC DE LA IMPRESORA
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD.NOMBRE_COLUMNA9,//1 id

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD.NOMBRE_COLUMNA1 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { id_empleado_usuario.toString() };

        try {

            Cursor c = db.query(
                    Estructura_BBDD.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //se mueve el cursor al primer registro

            c.moveToFirst();

            //mostramos los valores recuperados de la base de datos de recibos
            codigo_mac_impresora = c.getString(0);
        }
        catch (Exception ex2){
            //Toast.makeText(getApplicationContext(), "No se encontro registro de MAC", Toast.LENGTH_LONG).show();
            resultado = "No es Posible conectarse con la Impresora, MAC no valida " + ex2.getMessage();
        }

        //CODIGO MAC DE LA IMPRESORA, OBLIGATORIO PARA RECONOCIMIENTO
        connection = new BluetoothConnection(codigo_mac_impresora); //c:3f:a4:a1:0e:ad

        try
        {
            connection.open();
        }

        catch (ConnectionException ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
            closeConnection();
        }

        catch(Exception ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
        }

        try
        {
            zebraPrinter = ZebraPrinterFactory.getInstance(connection);
            PrinterLanguage pl = zebraPrinter.getPrinterControlLanguage();

            resultado = "Conectado a la Impresora bt " + pl;
        }

        catch (ConnectionException ex)
        {
            resultado = "No pudo conectarse a la Impresora1 " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();

        } catch(Exception ex)
        {
            resultado = "No pudo conectarse a la Impresora2  " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();
        }
    }

    private void closeConnection(){
        if(connection != null){
            try
            {
                connection.close();
            }
            catch (ConnectionException exx)
            {
                resultado = "Conexion con impresora Cerrada " + exx.getMessage();
            }
        }
    }

    //obtener Emai, modelo telefono
    private String getIMEI() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";

        try{
            imei =tm.getDeviceId();
        }
        catch (SecurityException ex) {
            mensaje = "ERROR 3: " + ex.getMessage();
        }

        return imei;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "No se puede volver atras",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }



    private String unidad(int numero){

        switch (numero){
            case 9:
                num = "nueve";
                break;
            case 8:
                num = "ocho";
                break;
            case 7:
                num = "siete";
                break;
            case 6:
                num = "seis";
                break;
            case 5:
                num = "cinco";
                break;
            case 4:
                num = "cuatro";
                break;
            case 3:
                num = "tres";
                break;
            case 2:
                num = "dos";
                break;
            case 1:
                if (flag == 0)
                    num = "uno";
                else
                    num = "un";
                break;
            case 0:
                num = "";
                break;
        }
        return num;
    }

    private String decena(int numero){

        if (numero >= 90 && numero <= 99)
        {
            num_letra = "noventa ";
            if (numero > 90)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 90));
        }
        else if (numero >= 80 && numero <= 89)
        {
            num_letra = "ochenta ";
            if (numero > 80)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 80));
        }
        else if (numero >= 70 && numero <= 79)
        {
            num_letra = "setenta ";
            if (numero > 70)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 70));
        }
        else if (numero >= 60 && numero <= 69)
        {
            num_letra = "sesenta ";
            if (numero > 60)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 60));
        }
        else if (numero >= 50 && numero <= 59)
        {
            num_letra = "cincuenta ";
            if (numero > 50)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 50));
        }
        else if (numero >= 40 && numero <= 49)
        {
            num_letra = "cuarenta ";
            if (numero > 40)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 40));
        }
        else if (numero >= 30 && numero <= 39)
        {
            num_letra = "treinta ";
            if (numero > 30)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 30));
        }
        else if (numero >= 20 && numero <= 29)
        {
            if (numero == 20)
                num_letra = "veinte ";
            else
                num_letra = "veinti".concat(unidad(numero - 20));
        }
        else if (numero >= 10 && numero <= 19)
        {
            switch (numero){
                case 10:

                    num_letra = "diez ";
                    break;

                case 11:

                    num_letra = "once ";
                    break;

                case 12:

                    num_letra = "doce ";
                    break;

                case 13:

                    num_letra = "trece ";
                    break;

                case 14:

                    num_letra = "catorce ";
                    break;

                case 15:

                    num_letra = "quince ";
                    break;

                case 16:

                    num_letra = "dieciseis ";
                    break;

                case 17:

                    num_letra = "diecisiete ";
                    break;

                case 18:

                    num_letra = "dieciocho ";
                    break;

                case 19:

                    num_letra = "diecinueve ";
                    break;

            }
        }
        else
            num_letra = unidad(numero);

        return num_letra;
    }

    private String centena(int numero){
        if (numero >= 100)
        {
            if (numero >= 900 && numero <= 999)
            {
                num_letra = "novecientos ";
                if (numero > 900)
                    num_letra = num_letra.concat(decena(numero - 900));
            }
            else if (numero >= 800 && numero <= 899)
            {
                num_letra = "ochocientos ";
                if (numero > 800)
                    num_letra = num_letra.concat(decena(numero - 800));
            }
            else if (numero >= 700 && numero <= 799)
            {
                num_letra = "setecientos ";
                if (numero > 700)
                    num_letra = num_letra.concat(decena(numero - 700));
            }
            else if (numero >= 600 && numero <= 699)
            {
                num_letra = "seiscientos ";
                if (numero > 600)
                    num_letra = num_letra.concat(decena(numero - 600));
            }
            else if (numero >= 500 && numero <= 599)
            {
                num_letra = "quinientos ";
                if (numero > 500)
                    num_letra = num_letra.concat(decena(numero - 500));
            }
            else if (numero >= 400 && numero <= 499)
            {
                num_letra = "cuatrocientos ";
                if (numero > 400)
                    num_letra = num_letra.concat(decena(numero - 400));
            }
            else if (numero >= 300 && numero <= 399)
            {
                num_letra = "trescientos ";
                if (numero > 300)
                    num_letra = num_letra.concat(decena(numero - 300));
            }
            else if (numero >= 200 && numero <= 299)
            {
                num_letra = "doscientos ";
                if (numero > 200)
                    num_letra = num_letra.concat(decena(numero - 200));
            }
            else if (numero >= 100 && numero <= 199)
            {
                if (numero == 100)
                    num_letra = "cien ";
                else
                    num_letra = "ciento ".concat(decena(numero - 100));
            }
        }
        else
            num_letra = decena(numero);

        return num_letra;
    }

    private String miles(int numero){
        if (numero >= 1000 && numero <2000){
            num_letram = ("mil ").concat(centena(numero%1000));
        }
        if (numero >= 2000 && numero <10000){
            flag=1;
            num_letram = unidad(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 1000)
            num_letram = centena(numero);

        return num_letram;
    }

    private String decmiles(int numero){
        if (numero == 10000)
            num_letradm = "diez mil";
        if (numero > 10000 && numero <20000){
            flag=1;
            num_letradm = decena(numero/1000).concat("mil ").concat(centena(numero%1000));
        }
        if (numero >= 20000 && numero <100000){
            flag=1;
            num_letradm = decena(numero/1000).concat(" mil ").concat(miles(numero%1000));
        }


        if (numero < 10000)
            num_letradm = miles(numero);

        return num_letradm;
    }

    private String cienmiles(int numero){
        if (numero == 100000)
            num_letracm = "cien mil";
        if (numero >= 100000 && numero <1000000){
            flag=1;
            num_letracm = centena(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 100000)
            num_letracm = decmiles(numero);
        return num_letracm;
    }

    private String millon(int numero){
        if (numero >= 1000000 && numero <2000000){
            flag=1;
            num_letramm = ("Un millon ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 2000000 && numero <10000000){
            flag=1;
            num_letramm = unidad(numero/1000000).concat(" millones ").concat(cienmiles(numero%1000000));
        }
        if (numero < 1000000)
            num_letramm = cienmiles(numero);

        return num_letramm;
    }

    private String decmillon(int numero){
        if (numero == 10000000)
            num_letradmm = "diez millones";
        if (numero > 10000000 && numero <20000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat("millones ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 20000000 && numero <100000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat(" milllones ").concat(millon(numero%1000000));
        }


        if (numero < 10000000)
            num_letradmm = millon(numero);

        return num_letradmm;
    }


    public String convertirLetras(int numero){
        num_letras = decmillon(numero);
        return num_letras;
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
