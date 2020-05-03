package com.example.cmacchiavelli.sudsys;

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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class listado_cuotas_polizas_confirmar extends AppCompatActivity {


    //incluimos el tool_bar
    private Toolbar toolbar;

    String mensaje, mensaje2, parametro_id_recibo,  parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente, estado_amortizacion_sudsys, estado_amortizacion_temporal;

    //para el webservice
    SoapObject resultString;

    //variables a recibir ws saveRecipent
    String respuesta_recibido, mensaje_recibido,
            parametro_id_empleado, id_recibo_recibido,
            numero_recibo_recibido,anio_recibo_recibido,
            parametro_id_sucursal, parametro_id_recibo_sudsys,
            parametro_moneda, parametro_numero_recibo_sudsys,
            parametro_total_recibo_sus, parametro_total_recibo_bs,
            parametro_consumo_recibo, parametro_saldo_recibo, sw_existe_consumo="0", parametro_numero_liquidacion;

    TextView tv_id_recibo_sudsys;
    TextView tv_total_recibo, tv_consumo_recibo, tv_saldo_recibo, tv_id_cliente, tv_numero_recibo_sudsys, tv_total_recibo_bs, tv_nombre_cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_cuotas_polizas_confirmar);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Confirmacion Amortizacion Cuotas");
        //ponemos las opciones del menu
        /////setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();
        parametro_id_recibo=datos.getString("parametro_id_recibo");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        //parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        //parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_id_recibo_sudsys=datos.getString("parametro_id_recibo_sudsys");
        parametro_moneda=datos.getString("parametro_moneda");
        parametro_numero_recibo_sudsys=datos.getString("parametro_numero_recibo_sudsys");
        parametro_total_recibo_sus=datos.getString("parametro_total_recibo_sus");
        parametro_total_recibo_bs=datos.getString("parametro_total_recibo_bs");
        parametro_consumo_recibo=datos.getString("parametro_consumo_recibo");
        parametro_saldo_recibo=datos.getString("parametro_saldo_recibo");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");

        //MOSTRAMOS EL ID DEL RECIBO EN EL SUDSYS
        tv_id_recibo_sudsys=(TextView) findViewById(R.id.tv_id_recibo_sudsys);
      //  tv_id_recibo_sudsys.setText(parametro_id_recibo_sudsys + "parametro_id_recibo: " + parametro_id_recibo);
        tv_id_recibo_sudsys.setText(parametro_id_recibo_sudsys);

        tv_consumo_recibo = (TextView) findViewById(R.id.tv_consumo_recibo);
        tv_consumo_recibo.setText(parametro_consumo_recibo);

        tv_saldo_recibo = (TextView) findViewById(R.id.tv_saldo_recibo);
        tv_saldo_recibo.setText(parametro_saldo_recibo);

        tv_numero_recibo_sudsys=(TextView) findViewById(R.id.tv_numero_recibo_sudsys);
        tv_numero_recibo_sudsys.setText(parametro_numero_recibo_sudsys);

        tv_total_recibo = (TextView) findViewById(R.id.tv_total_recibo_sus);
        tv_total_recibo.setText(parametro_total_recibo_sus);

        tv_total_recibo_bs = (TextView) findViewById(R.id.tv_total_recibo_bs);
        tv_total_recibo_bs.setText(parametro_total_recibo_bs);

        tv_nombre_cliente=(TextView) findViewById(R.id.tv_nombre_cliente);
        tv_nombre_cliente.setText(parametro_nombre_cliente);
        Button boton_amortizar = (Button) findViewById(R.id.amortizar);


        ///////////////////////////////LISTAMOS LO DE LA BASE DE DATOS
        LinearLayout contenedor =(LinearLayout) findViewById(R.id.contenedor_lista);
        try {
            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(this);


            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {
                    //que columnas queremos que saque nuestra consulta
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1,//1 id
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2,//2
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA3,//3
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA4,//4
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA5,//5
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA6,//6
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA7,//7
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8,//8
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA9,//9
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA10,//10

            };

            //COLUMNA QUE COMPARAREMOS (WHERE)
            String selection = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8 + " = ? ";

            String[] selectionArgs = { parametro_id_recibo };


            try {

                Cursor c = db.query(
                        Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                // c.moveToFirst();
                if (c.getCount() > 0) {
                    sw_existe_consumo="1";
                    while (c.moveToNext()) {
                        ImageView miImageView = new ImageView(getApplicationContext());
                        TextView miTextView = new TextView(getApplicationContext());
                        miTextView.setText("No. Póliza: "+c.getString(2).toString()+" Cuota: "+c.getString(3).toString()+" Pagar: "+c.getString(5).toString());
                        miTextView.setBackgroundColor(Color.BLUE);
                        miTextView.setTextColor(Color.WHITE);
                        miTextView.setTextSize(13);
                        miTextView.setGravity(Gravity.CENTER);

                        TextView miTextView2 = new TextView(getApplicationContext());
                        miTextView2.setText("Cancelar: ");
                        miTextView2.setBackgroundColor(Color.RED);
                        miTextView2.setTextColor(Color.WHITE);
                        miTextView2.setTextSize(13);
                        miTextView2.setGravity(Gravity.CENTER);
                        //Agrega imagen al ImageView.
                        miImageView.setImageResource(R.drawable.cancelar_pago);
                        //Agrega vistas al contenedor.
                        //contenedor.addView(miImageView);
                        contenedor.addView(miTextView2);
                        contenedor.addView(miTextView);
                           // Toast.makeText(getApplicationContext(), c.getString(2).toString(), Toast.LENGTH_LONG).show();
                    }
                }


            }
            catch (Exception ex) {
                mensaje = "ERROR 5: " + ex.getMessage();
            }
        }
        catch (Exception ex) {
            mensaje="ERROR 6: " +ex;
        }

    //SI EXISTE ALGUN CONSUMO MOSTRAMOS EL BOTON AMORTIZAR
        if(sw_existe_consumo.equals("1")){
            boton_amortizar.setVisibility(View.VISIBLE);
        }else {
            boton_amortizar.setVisibility(View.GONE);
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

    public void ejecutar_listado_polizas_cuotas(View view){
        super.finish();
    }

    public void ejecutar_amortizaciones(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            //llamamos a la tarea antes del segundo plano
            listado_cuotas_polizas_confirmar.AntesSegundoPlano tarea_antes = new listado_cuotas_polizas_confirmar.AntesSegundoPlano();
            tarea_antes.execute();
        }
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

            //Toast.makeText(getApplicationContext(), estado_amortizacion_temporal+": "+mensaje, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), estado_amortizacion_sudsys +": "+mensaje2, Toast.LENGTH_LONG).show();

            if(estado_amortizacion_sudsys.equals("1") && estado_amortizacion_temporal.equals("1")) {
                //Intent es como evento, debe mejecutar el java infoClase
                Intent i = new Intent(getApplicationContext(), listado_cuotas_polizas_imprimir.class);
                //i.putExtra("parametro_id_cliente", parametro_id_cliente);
                i.putExtra("parametro_id_recibo", parametro_id_recibo_sudsys);
                i.putExtra("parametro_numero_recibo", parametro_numero_recibo_sudsys);
                i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
                i.putExtra("parametro_id_empleado", parametro_id_empleado);
                i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

                startActivity(i);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Error al amortizar", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void guarda_pago_recibo(){
        ///////////////////////////////GUARDAMOS EN LA BASE DE DATOS
        try {
            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(this);


            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {
                    //que columnas queremos que saque nuestra consulta
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1,//1 id
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2,//2
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA3,//3
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA4,//4
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA5,//5
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA6,//6
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA7,//7
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8,//8
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA9,//9
                    Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA10,//10

            };

            //COLUMNA QUE COMPARAREMOS (WHERE)
            String selection = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA8 + " = ? ";

            String[] selectionArgs = { parametro_id_recibo };


            try {

                Cursor c = db.query(
                        Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

              // c.moveToFirst();
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        if(c.getString(1).toString().equals("null")){

                        }else {
                            //mensaje = mensaje+"-"+c.getString(1);

                            //llamamos al ws para amortizar, y le enviamos estos parametros(id_cliente, id_recibo, id_cuota_amortizacion, id_poliza_movimiento,numero_cuota, numero_factura_recibo,
                            //      observaciones, monto_cancelar, id_empleado, saldo_final,
                            //    imei_dispositivo, usuario_ws, password_ws)

                            //ALMACENA AMORTIZACION
                        try {
                            if (Double.valueOf(c.getString(5)).doubleValue() > 0){
                                String SOAP_ACTION = "http://ws.sudseguros.com/SaveAmortizacion";
                            String METHOD_NAME = "SaveAmortizacion";
                            String NAMESPACE = "http://ws.sudseguros.com/";
                            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

                            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

                            RequestWs.addProperty("id_cliente", c.getString(8));
                            RequestWs.addProperty("id_recibo", parametro_id_recibo_sudsys);
                            RequestWs.addProperty("id_cuota_amortizacion", c.getString(1));
                            RequestWs.addProperty("id_poliza_movimiento", c.getString(9));
                            RequestWs.addProperty("numero_cuota", c.getString(3));
                            RequestWs.addProperty("numero_factura_recibo", parametro_numero_recibo_sudsys);
                            RequestWs.addProperty("observaciones", "sin obs");
                            RequestWs.addProperty("monto_cancelar", c.getString(5));
                            RequestWs.addProperty("id_empleado", parametro_id_empleado);
                            RequestWs.addProperty("id_sucursal", "6");
                            RequestWs.addProperty("saldo_final", c.getString(6));
                            RequestWs.addProperty("imei_dispositivo", "357136081571342");
                            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
                            RequestWs.addProperty("password_ws", "AXr53.o1");

                            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            soapEnvelope.dotNet = true; //tipo de servicio .net
                            soapEnvelope.setOutputSoapObject(RequestWs);


                            //Invoca al web service
                            HttpTransportSE transport = new HttpTransportSE(URL);
                            transport.call(SOAP_ACTION, soapEnvelope);

                            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


                            mensaje2 = "MONTO CANCELAR: " + c.getString(5) + " " + resultString.getProperty(0) + " " + resultString.getProperty(1);
                            //almacenamos variables recibidas del ws
                            respuesta_recibido = "" + resultString.getProperty(0);
                            mensaje_recibido = "" + resultString.getProperty(1);
                            id_recibo_recibido = "" + resultString.getProperty(2);
                            numero_recibo_recibido = "" + resultString.getProperty(3);
                            anio_recibo_recibido = "" + resultString.getProperty(4);

                            estado_amortizacion_sudsys = respuesta_recibido;

                                //ELIMINAMOS DE LA BD DE App
                                String id_poliza_amortizacion = c.getString(1).toString();
                                //importamos la clase BB_DD_HELPER
                                SQLiteDatabase db2 = helper.getWritableDatabase();
                                String selection2 = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2 + " LIKE ?";
                                String[] selectionArgs2 = { id_poliza_amortizacion.toString() };
                                db2.delete(Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME, selection2, selectionArgs2);
                                //Toast.makeText(getApplicationContext(), "Se borro la amortizacion con id_poliza_amortizacion: " + id_poliza_amortizacion.toString(), Toast.LENGTH_SHORT).show();




                            }else {
                                respuesta_recibido = "Ok";
                                mensaje_recibido = "" ;
                                id_recibo_recibido = "";
                                numero_recibo_recibido = "";
                                anio_recibo_recibido = "";
                                estado_amortizacion_sudsys = "1";

                            }
                        } catch (Exception ex) {
                            mensaje2 = "ERROR 4: " + ex.getMessage();
                            estado_amortizacion_sudsys=respuesta_recibido;
                        }

                        //ALMACENA EN TABLA TEMPORAL
                        try {

                            String SOAP_ACTION = "http://ws.sudseguros.com/SaveAmortizacionTemporal";
                            String METHOD_NAME = "SaveAmortizacionTemporal";
                            String NAMESPACE = "http://ws.sudseguros.com/";
                            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

                            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);

                            RequestWs.addProperty("id_cliente", c.getString(8));
                            RequestWs.addProperty("id_recibo", parametro_id_recibo_sudsys);
                            RequestWs.addProperty("id_cuota_amortizacion", c.getString(1));
                            RequestWs.addProperty("id_poliza_movimiento", c.getString(9));
                            RequestWs.addProperty("numero_cuota", c.getString(3));
                            RequestWs.addProperty("numero_factura_recibo", "123");
                            RequestWs.addProperty("observaciones", "AMORTIZADO DESDE APP");
                            RequestWs.addProperty("monto_cancelar", c.getString(5));
                            RequestWs.addProperty("id_empleado", parametro_id_empleado);
                            RequestWs.addProperty("id_sucursal", parametro_id_sucursal);
                            RequestWs.addProperty("imei_dispositivo", "357136081571342");
                            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
                            RequestWs.addProperty("password_ws", "AXr53.o1");
                            RequestWs.addProperty("moneda", parametro_moneda);

                            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            soapEnvelope.dotNet = true; //tipo de servicio .net
                            soapEnvelope.setOutputSoapObject(RequestWs);


                            //Invoca al web service
                            HttpTransportSE transport = new HttpTransportSE(URL);
                            transport.call(SOAP_ACTION, soapEnvelope);

                            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

                            mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
                            //almacenamos variables recibidas del ws
                            respuesta_recibido = "" + resultString.getProperty(0);
                            mensaje_recibido = "" + resultString.getProperty(1);

                            estado_amortizacion_temporal=respuesta_recibido;
                            //id_recibo_recibido = "" + resultString.getProperty(2);
                            //numero_recibo_recibido = "" + resultString.getProperty(3);
                            //anio_recibo_recibido = "" + resultString.getProperty(4);
                        } catch (Exception ex) {
                            mensaje = "ERROR 7: " + ex.getMessage();
                            estado_amortizacion_temporal=respuesta_recibido;
                        }


                        }
                    }
                }


             }
            catch (Exception ex) {
            mensaje = "ERROR 5: " + ex.getMessage();
            }
        }
        catch (Exception ex) {
            mensaje="ERROR 6: " +ex;
        }
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
