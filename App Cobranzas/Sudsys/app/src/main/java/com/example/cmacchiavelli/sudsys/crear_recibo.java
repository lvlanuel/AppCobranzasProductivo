package com.example.cmacchiavelli.sudsys;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class crear_recibo extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    //definimos el txt que estara a la escucha, para almacenar en base de datos
    EditText txt_nombre, txt_apellido_paterno, txt_apellido_materno, txt_ci_nit, txt_numero_liquidacion;
    Spinner tipo_cliente;
    Spinner spinner;

    //variables globales

    String id_sucursal, id_empleado, spTipoCliente, parametro_tipo, mensaje_amortizacion, respuesta_amortizacion, mensaje, estado_habilitado, combo_tipo_busqueda;
    //para el webservice
    SoapObject resultString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recibo);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Búsqueda de Cliente");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);


        //combobox
        spinner = (Spinner) findViewById(R.id.tipo_cliente);
        String[] letra = {"Liquidacion","Natural","Juridico"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        BorrarBasesDatos();

        //recibimos variables
        Bundle datos=getIntent().getExtras();
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo=datos.getString("parametro_tipo");

        //vemos si el usuario esta habilitado en el sudsys
        crear_recibo.VerificaUsuarioPlano tarea3 = new crear_recibo.VerificaUsuarioPlano();
        tarea3.execute();

        //si cambiamos valor del combo(spinner)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                //sacamos valores de los spinners
                spTipoCliente = spinner.getSelectedItem().toString();
                if(spTipoCliente.equals("Liquidacion")) {

                    TextView textcargando8 = (TextView) findViewById(R.id.textViewLiquidacion);
                    textcargando8.setVisibility(View.VISIBLE);
                    EditText textcargando9 = (EditText) findViewById(R.id.txt_liquidacion);
                    textcargando9.setVisibility(View.VISIBLE);

                    TextView textcargando6 = (TextView) findViewById(R.id.textViewNombre);
                    textcargando6.setVisibility(View.GONE);
                    EditText textcargando11 = (EditText) findViewById(R.id.txt_nombre);
                    textcargando11.setVisibility(View.GONE);

                    TextView textcargando7 = (TextView) findViewById(R.id.textViewCiNit);
                    textcargando7.setVisibility(View.GONE);
                    EditText textcargando10 = (EditText) findViewById(R.id.txt_ci_nit);
                    textcargando10.setVisibility(View.GONE);

                    TextView textcargando2 = (TextView) findViewById(R.id.textViewAp);
                    textcargando2.setVisibility(View.GONE);
                    EditText textcargando3 = (EditText) findViewById(R.id.txt_apellido_paterno);
                    textcargando3.setVisibility(View.GONE);

                    TextView textcargando4 = (TextView) findViewById(R.id.textViewAm);
                    textcargando4.setVisibility(View.GONE);
                    EditText textcargando5 = (EditText) findViewById(R.id.txt_apellido_materno);
                    textcargando5.setVisibility(View.GONE);
                }else{
                if (spTipoCliente.equals("Natural")) {


                    TextView textcargando8 = (TextView) findViewById(R.id.textViewLiquidacion);
                    textcargando8.setVisibility(View.GONE);
                    EditText textcargando9 = (EditText) findViewById(R.id.txt_liquidacion);
                    textcargando9.setVisibility(View.GONE);

                    TextView textcargando6 = (TextView) findViewById(R.id.textViewNombre);
                    textcargando6.setVisibility(View.VISIBLE);
                    EditText textcargando11 = (EditText) findViewById(R.id.txt_nombre);
                    textcargando11.setVisibility(View.VISIBLE);

                    TextView textcargando7 = (TextView) findViewById(R.id.textViewCiNit);
                    textcargando7.setVisibility(View.VISIBLE);
                    EditText textcargando10 = (EditText) findViewById(R.id.txt_ci_nit);
                    textcargando10.setVisibility(View.VISIBLE);

                    TextView textcargando2 = (TextView) findViewById(R.id.textViewAp);
                    textcargando2.setVisibility(View.VISIBLE);
                    EditText textcargando3 = (EditText) findViewById(R.id.txt_apellido_paterno);
                    textcargando3.setVisibility(View.VISIBLE);

                    TextView textcargando4 = (TextView) findViewById(R.id.textViewAm);
                    textcargando4.setVisibility(View.VISIBLE);
                    EditText textcargando5 = (EditText) findViewById(R.id.txt_apellido_materno);
                    textcargando5.setVisibility(View.VISIBLE);
                } else {
                    TextView textcargando8 = (TextView) findViewById(R.id.textViewLiquidacion);
                    textcargando8.setVisibility(View.GONE);
                    EditText textcargando9 = (EditText) findViewById(R.id.txt_liquidacion);
                    textcargando9.setVisibility(View.GONE);

                    TextView textcargando2 = (TextView) findViewById(R.id.textViewAp);
                    textcargando2.setVisibility(View.GONE);
                    EditText textcargando3 = (EditText) findViewById(R.id.txt_apellido_paterno);
                    textcargando3.setVisibility(View.GONE);

                    TextView textcargando4 = (TextView) findViewById(R.id.textViewAm);
                    textcargando4.setVisibility(View.GONE);
                    EditText textcargando5 = (EditText) findViewById(R.id.txt_apellido_materno);
                    textcargando5.setVisibility(View.GONE);

                    TextView textcargando6 = (TextView) findViewById(R.id.textViewNombre);
                    textcargando6.setVisibility(View.VISIBLE);
                    EditText textcargando11 = (EditText) findViewById(R.id.txt_nombre);
                    textcargando11.setVisibility(View.VISIBLE);

                    TextView textcargando7 = (TextView) findViewById(R.id.textViewCiNit);
                    textcargando7.setVisibility(View.VISIBLE);
                    EditText textcargando10 = (EditText) findViewById(R.id.txt_ci_nit);
                    textcargando10.setVisibility(View.VISIBLE);
                }
            }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


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
            //llamamos a la funcion ejecutar info que es de tipo vista, y si no hay vista poner null
            cerrarSesion();
            //salimos de la aplicacion
            System.exit(0);
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

    //llamamos a la actividad lista_clientes
    public void lista_clientes(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            txt_nombre = (EditText) findViewById(R.id.txt_nombre);
            txt_apellido_paterno = (EditText) findViewById(R.id.txt_apellido_paterno);
            txt_apellido_materno = (EditText) findViewById(R.id.txt_apellido_materno);
            txt_ci_nit = (EditText) findViewById(R.id.txt_ci_nit);
            tipo_cliente = (Spinner) findViewById(R.id.tipo_cliente);
            txt_numero_liquidacion = (EditText) findViewById(R.id.txt_liquidacion);


            //PREGUNTAMOS QUE TIPO DE BUISQUEDA SERA, SI CLIENTE O POR LIQUIDACION
            if (spTipoCliente.equals("Liquidacion")) {
                ejecutar_polizas_clientes(null);
            } else {
                //Intent es como evento, debe mejecutar el java infoClase
                Intent i = new Intent(this, lista_clientes.class);
                //guardamos  para recuperarlo en otra actividad
                i.putExtra("parametro_nombre", txt_nombre.getText().toString());
                i.putExtra("parametro_apellido_paterno", txt_apellido_paterno.getText().toString());
                i.putExtra("parametro_apellido_materno", txt_apellido_materno.getText().toString());
                i.putExtra("parametro_ci_nit", txt_ci_nit.getText().toString());
                i.putExtra("parametro_tipo_cliente", tipo_cliente.getSelectedItem().toString());
                i.putExtra("parametro_numero_liquidacion", txt_numero_liquidacion.getText().toString());

                i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                i.putExtra("parametro_id_empleado", id_empleado.toString());
                i.putExtra("parametro_tipo", parametro_tipo.toString());
                startActivity(i);
            }
        }
    }

    //llamamos a la actividad pantalla_principal
    public void ejecutar_polizas_clientes(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_pre_polizas_clientes.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_cliente", "");
        i.putExtra("parametro_nombre_cliente", "");
        i.putExtra("parametro_apellido_paterno_cliente", "");
        i.putExtra("parametro_apellido_materno_cliente", "");
        i.putExtra("parametro_ci_nit_cliente", "");
        i.putExtra("parametro_tipo_cliente", "");
        i.putExtra("parametro_email_cliente", "");
        i.putExtra("parametro_numero_liquidacion", txt_numero_liquidacion.getText().toString());

        i.putExtra("parametro_tipo_actividad", "Nuevo");
        i.putExtra("parametro_id_sucursal", id_sucursal.toString());
        i.putExtra("parametro_id_empleado", id_empleado.toString());

        startActivity(i);
        finish();
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

        //llamamos al WebService  EliminaPreCuotaAmortizaTemporales
        crear_recibo.TareaEliminarPreCuotas tarea3 = new crear_recibo.TareaEliminarPreCuotas();
        tarea3.execute();

    }


    //para el metodo SaveRecipent del webservice
    private class TareaEliminarPreCuotas extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

        }

        @Override
        protected Void doInBackground(Void... params){

            eliminar_cuota_pre_amortizada_no_utilizada();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR


            if(respuesta_amortizacion.toString().equals("1")) {
                //Toast.makeText(getApplicationContext(), mensaje_amortizacion+"Se elimino la Pre Amortizacion temporales en el sistema sudsys, sucursal: ", Toast.LENGTH_LONG).show();
            }else {
                //Toast.makeText(getApplicationContext(), mensaje_amortizacion+"Error no se puede eliminar la Pre Amortizacion temporal en el sistema sudsys.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Info 1: Continuando...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void eliminar_cuota_pre_amortizada_no_utilizada() {
        String SOAP_ACTION = "http://ws.sudseguros.com/EliminarPreAmortizacionNoUtilizada";
        String METHOD_NAME = "EliminarPreAmortizacionNoUtilizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        try {
            if ((id_empleado != null) && (!id_empleado.equals("")) ) {

            }else {
                id_empleado = "0";
            }
            if ((id_sucursal != null) && (!id_empleado.equals("")) ) {

            }else {
                id_sucursal = "0";
            }
            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);
            RequestWs.addProperty("id_empleado", id_empleado);
            RequestWs.addProperty("id_sucursal", id_sucursal);
            RequestWs.addProperty("imei_dispositivo", "357136081571342");
            RequestWs.addProperty("usuario_ws", "SUdMOv1l3");
            RequestWs.addProperty("password_ws", "AXr53.o1");


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(RequestWs);


            //Invoca al web service
            try {
                //HttpTransportSE transport = new HttpTransportSE(URL);
                HttpTransportSE transport = new HttpTransportSE(URL, 30000);
                transport.call(SOAP_ACTION, soapEnvelope);
            }catch (IOException ioex) {
                ioex.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                //TODO: handle exception
            }


            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            respuesta_amortizacion = "" + resultString.getProperty(0);
            mensaje_amortizacion = "" + resultString.getProperty(1);

        } catch (Exception ex) {
            mensaje_amortizacion = "ERROR 13: " + ex.getMessage();
        }
    }



    private class VerificaUsuarioPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
            textcargando.setVisibility(View.VISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params){
            //leemos la BB_DD, PERO PRIMERO RECUPERAMOS LA VARIABLE PARAMETRO_USUARIO Y LA MOSTRAMOS EN EL TOOLBAR, A LADO DEL TITULO
            Bundle datos=getIntent().getExtras();
            String parametro_usuario_recibido=datos.getString("parametro_usuario");


            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());


            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {
                    //que columnas queremos que saque nuestra consulta
                    Estructura_BBDD.NOMBRE_COLUMNA1,//1 id_empleado
                    Estructura_BBDD.NOMBRE_COLUMNA3,//2 usuario
                    Estructura_BBDD.NOMBRE_COLUMNA2,//3 id_sucursal
                    Estructura_BBDD.NOMBRE_COLUMNA4,//4 nombre
                    Estructura_BBDD.NOMBRE_COLUMNA6//5 apellido_paterno

            };

            //COLUMNA QUE COMPARAREMOS (WHERE)
            String selection = Estructura_BBDD.NOMBRE_COLUMNA3 + " = ? ";
            ////////String[] selectionArgs = { textoId.getText().toString() };
            String[] selectionArgs = { parametro_usuario_recibido };

             /*   String sortOrder =
                        Estructura_BBDD.NOMBRE_COLUMNA1 + " DESC";*/

            try{

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

                String nombre_sucursal="SIN ASIGNAR";
                if(c.getString(2).toString().equals("1")){
                    nombre_sucursal="LA PAZ";
                }
                if(c.getString(2).toString().equals("2")){
                    nombre_sucursal="SANTA CRUZ";
                }
                if(c.getString(2).toString().equals("3")){
                    nombre_sucursal="COCHABAMBA";
                }
                if(c.getString(2).toString().equals("5")){
                    nombre_sucursal="TARIJA";
                }

                TextView valor_resultado=(TextView)findViewById(R.id.text_usuario);
                valor_resultado.setText("HOLA "+c.getString(3) + " "+c.getString(4)+ " - SUCURSAL " +nombre_sucursal );

                //almacenamos tb id_sucursal, id_empleado para pasar a la otra actividad
                id_empleado=c.getString(0);
                id_sucursal=c.getString(2);

                //textoNombre.setText(c.getString(0));
                //textoApellido.setText(c.getString(1));

            }catch (Exception e){
//                Toast.makeText(getApplicationContext(), "no se encontro registro", Toast.LENGTH_LONG).show();
            }


            String SOAP_ACTION = "http://ws.sudseguros.com/Login";
            String METHOD_NAME = "Login";
            String NAMESPACE = "http://ws.sudseguros.com/";
            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

            try {

                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                Request.addProperty("usuario", parametro_usuario_recibido);
                Request.addProperty("password", "");
                Request.addProperty("imei_dispositivo", "357136081571342");
                Request.addProperty("descripcion_dispositivo", "nada");
                Request.addProperty("usuario_ws", "SUdMOv1l3");
                Request.addProperty("password_ws", "AXr53.o1");
                Request.addProperty("id_empleado", id_empleado);

                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true; //tipo de servicio .net
                soapEnvelope.setOutputSoapObject(Request);

                //Invoca al web service
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);

                resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

                //mensaje habilitar para ver respuesta del ws
                //mensaje = "OK: " + resultString.getProperty(0) + " " + resultString.getProperty(1);
                //mensaje normal
                mensaje = "Bienvenido";
            }
            catch (Exception ex) {
                mensaje = "ERROR: " + ex.getMessage();
            }

            if(resultString.getProperty(0).toString().equals("1") && resultString.getProperty(8).toString().equals("true")) {
                estado_habilitado="1";
            }else {
                estado_habilitado="0";
                //pantalla_inicio(null);
                //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            if(estado_habilitado.equals("1")) {
                //muestra texto mientras carga
                ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
                textcargando.setVisibility(View.GONE);
            }else{
                Toast.makeText(getApplicationContext(), "Usuario no habilitado.", Toast.LENGTH_LONG).show();
                pantalla_inicio(null);
                cerrarSesion();
                finish();
            }
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


    public void cerrarSesion() {
        String comparador_master="12345678";
        final BBDD_Helper helper_master = new BBDD_Helper(getApplicationContext());
        SQLiteDatabase db_master = helper_master.getWritableDatabase();

        String selection_master = Estructura_BBDD.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD.TABLE_NAME, selection_master, selectionArgs_master);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD", Toast.LENGTH_SHORT).show();

        String selection_master_2 = Estructura_BBDD_Bancos.NOMBRE_COLUMNA1 + " != ? ";
        String[] selectionArgs_master_2 = {comparador_master.toString()};
        db_master.delete(Estructura_BBDD_Bancos.TABLE_NAME, selection_master_2, selectionArgs_master_2);
        //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Bancos", Toast.LENGTH_SHORT).show();

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

}
