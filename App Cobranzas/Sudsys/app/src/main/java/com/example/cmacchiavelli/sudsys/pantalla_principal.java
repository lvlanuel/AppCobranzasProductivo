package com.example.cmacchiavelli.sudsys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;

public class pantalla_principal extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    //incluimos el tool_bar
    private Toolbar toolbar;
    //variables globales
    String id_sucursal, id_empleado;
    int contador_internet=0;
    SoapObject resultString;
    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;
    String autentificado, autentificado2, mensaje, mensaje2;
    String estado_habilitado="0";


    TextView tv_amortizaciones_pendientes, tv_amortizaciones_pendientes_numero;
    LinearLayout contenedor_opciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);


        BorrarBasesDatos();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        //preguntamos si hay conexion a internet
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(),"Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
        }else {
            String comparador="12345678";
            final BBDD_Helper helper2 = new BBDD_Helper(getApplicationContext());
            SQLiteDatabase db2 = helper2.getWritableDatabase();
            String selection2 = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
            String[] selectionArgs2 = {comparador.toString()};
            db2.delete(Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME, selection2, selectionArgs2);
            //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

            SQLiteDatabase db3 = helper2.getWritableDatabase();
            String selection3 = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA1 + " != ? ";
            String[] selectionArgs3 = {comparador.toString()};
            db3.delete(Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME, selection3, selectionArgs3);
            //Toast.makeText(getApplicationContext(), "Se vacio la tabla : Estructura_BBDD_Pre_Cuotas_Canceladas", Toast.LENGTH_SHORT).show();

        }

        pantalla_principal.TercerPlano tarea3 = new pantalla_principal.TercerPlano();
        tarea3.execute();

        //llamamos a la tarea en segundo plano
        //tare muestra numero de pendientes de los recibos con saldo otra sucursal
        pantalla_principal.SegundoPlano tarea = new pantalla_principal.SegundoPlano();
        tarea.execute();

        //actulaizamos al hacer Swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!compruebaConexion(getApplicationContext())) {
                    //dibujamos circulo conectados
                    Bitmap bitMap = Bitmap.createBitmap(40, 50, Bitmap.Config.ARGB_8888);

                    bitMap = bitMap.copy(bitMap.getConfig(), true);
                    // Construct a canvas with the specified bitmap to draw into
                    Canvas canvas = new Canvas(bitMap);
                    // Create a new paint with default settings.
                    Paint paint = new Paint();
                    // smooths out the edges of what is being drawn
                    paint.setAntiAlias(true);

                    //conectado/desconectadao
                    TextView valor_estado=(TextView)findViewById(R.id.textViewEstado);
                    // set color
                    paint.setColor(Color.RED);
                    valor_estado.setText("No Conectado");
                    // set style
                    //paint.setStyle(Paint.Style.STROKE);
                    paint.setStyle(Paint.Style.FILL);
                    // set stroke
                    paint.setStrokeWidth(4.5f);
                    // draw circle with radius 30
                    canvas.drawCircle(25, 25, 10, paint);
                    // set on ImageView or any other view
                    ImageView imageViewCirculo = (ImageView) findViewById(R.id.imageView15);
                    imageViewCirculo.setImageBitmap(bitMap);

                    Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else {
                    BorrarBasesDatos();
                    pantalla_principal.TercerPlano tarea3 = new pantalla_principal.TercerPlano();
                    tarea3.execute();

                    //llamamos a la tarea en segundo plano
                    //tare muestra numero de pendientes de los recibos con saldo otra sucursal
                    pantalla_principal.SegundoPlano tarea = new pantalla_principal.SegundoPlano();
                    tarea.execute();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        tv_amortizaciones_pendientes = (TextView) findViewById(R.id.tv_amortizaciones_pendientes);

        tv_amortizaciones_pendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!compruebaConexion(getApplicationContext())) {
                    Toast.makeText(getBaseContext(), "Necesaria conexión a internet " , Toast.LENGTH_SHORT).show();
                }else {
                    //Intent es como evento, debe mejecutar el java infoClase
                    Intent i = new Intent(getApplicationContext(), reportes_amortizaciones.class);
                    //pasamos variables
                    i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                    i.putExtra("parametro_id_empleado", id_empleado.toString());
                    i.putExtra("parametro_tipo", "amortizar_cuotas_pendientes");
                    startActivity(i);
                }
            }
        });

        TextView tv_polizas_cliente = (TextView) findViewById(R.id.tv_polizas_cliente);


        tv_polizas_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!compruebaConexion(getApplicationContext())) {
                    contador_internet=contador_internet+1;
                    Toast.makeText(getBaseContext(),"Necesaria conexión a internet, intentos errados "+ contador_internet, Toast.LENGTH_SHORT).show();
                    if(contador_internet>=3){
                        Toast.makeText(getBaseContext(),"Necesaria conexión a internet, intentos errados "+ contador_internet + " es necesario un recibo manual ", Toast.LENGTH_SHORT).show();
                        //Intent es como evento, debe ejecutar la actividad recibo manual
                        Intent i = new Intent(getApplicationContext(), crear_recibo_manual.class);
                        //pasamos variables
                        i.putExtra("parametro_tipo_actividad", "Nuevo");
                        i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                        i.putExtra("parametro_id_empleado", id_empleado.toString());
                        Bundle datos=getIntent().getExtras();
                        String parametro_usuario_recibido=datos.getString("parametro_usuario");
                        i.putExtra("parametro_usuario_recibido", parametro_usuario_recibido.toString());

                        startActivity(i);
                    }
                }else {
                    BorrarBasesDatos();
                    //Intent es como evento, debe mejecutar el java infoClase
                    Intent i = new Intent(getApplicationContext(), crear_recibo.class);
                    //pasamos variables
                    i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                    i.putExtra("parametro_id_empleado", id_empleado.toString());
                    i.putExtra("parametro_tipo", "polizas_cliente");
                    startActivity(i);
                }
            }
        });


        TextView tv_crear_recibo = (TextView) findViewById(R.id.tv_crear_recibo);


        tv_crear_recibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!compruebaConexion(getApplicationContext())) {
                    contador_internet=contador_internet+1;
                    Toast.makeText(getBaseContext(),"Necesaria conexión a internet, intentos errados "+ contador_internet, Toast.LENGTH_SHORT).show();
                    if(contador_internet>=3){
                        Toast.makeText(getBaseContext(),"Necesaria conexión a internet, intentos errados "+ contador_internet + " es necesario un recibo manual ", Toast.LENGTH_SHORT).show();
                        //Intent es como evento, debe ejecutar la actividad recibo manual
                        Intent i = new Intent(getApplicationContext(), crear_recibo_manual.class);
                        //pasamos variables
                        i.putExtra("parametro_tipo_actividad", "Nuevo");
                        i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                        i.putExtra("parametro_id_empleado", id_empleado.toString());
                        Bundle datos=getIntent().getExtras();
                        String parametro_usuario_recibido=datos.getString("parametro_usuario");
                        i.putExtra("parametro_usuario_recibido", parametro_usuario_recibido.toString());

                        startActivity(i);
                    }
                }else {
                    BorrarBasesDatos();
                    //Intent es como evento, debe mejecutar el java infoClase
                    Intent i = new Intent(getApplicationContext(), crear_recibo.class);
                    //pasamos variables
                    i.putExtra("parametro_id_sucursal", id_sucursal.toString());
                    i.putExtra("parametro_id_empleado", id_empleado.toString());
                    i.putExtra("parametro_tipo", "crear_recibo");
                    startActivity(i);
                }
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
            BorrarBasesDatos();
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
        super.onRestart();
    }

    //llamamos a la actividad reportes
    public void ejecutar_reportes_recibos(View view){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet " , Toast.LENGTH_SHORT).show();
        }else {
            BorrarBasesDatos();
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(this, reportes_recibos.class);
            //pasamos variables
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
        }
    }
    //llamamos a la actividad reportes recibos pendientes
    public void ejecutar_reportes_recibos_pendientes(View view){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet " , Toast.LENGTH_SHORT).show();
        }else {
            BorrarBasesDatos();
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(this, reportes_recibos_pendientes_listar.class);
            //pasamos variables
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
        }
    }
    //llamamos a la actividad reportes recibos pendientes
    public void ejecutar_reportes_recibos_creados(View view){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet " , Toast.LENGTH_SHORT).show();
        }else {
            BorrarBasesDatos();
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(this, reportes_recibos_creados.class);
            //pasamos variables
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
        }
    }
    //llamamos a la actividad reportes amortizaciones
    public void ejecutar_reportes_amortizaciones(View view){
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet " , Toast.LENGTH_SHORT).show();
        }else {
            BorrarBasesDatos();
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(this, reportes_amortizaciones.class);
            //pasamos variables
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            i.putExtra("parametro_tipo", "reportes");
            startActivity(i);
        }
    }
    //llamamos a la actividad crear_recibo
    public void crear_recibo(View view){

    }

    private class TercerPlano extends AsyncTask<Void, Void, Void> {

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
            toolbar.setTitle("Sudsys Bienvenido "+ parametro_usuario_recibido);
            setSupportActionBar(toolbar);


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

                //dibujamos circulo conectados
                Bitmap bitMap = Bitmap.createBitmap(40, 50, Bitmap.Config.ARGB_8888);

                bitMap = bitMap.copy(bitMap.getConfig(), true);
                // Construct a canvas with the specified bitmap to draw into
                Canvas canvas = new Canvas(bitMap);
                // Create a new paint with default settings.
                Paint paint = new Paint();
                // smooths out the edges of what is being drawn
                paint.setAntiAlias(true);
                //conectado/desconectadao
                TextView valor_estado=(TextView)findViewById(R.id.textViewEstado);
                if(c.getString(0).toString()!="" && compruebaConexion(getApplicationContext())){
                    // set color
                    paint.setColor(Color.GREEN);
                    valor_estado.setText("Conectado");
                }else {
                    // set color
                    paint.setColor(Color.RED);
                    valor_estado.setText("No Conectado");

                }
                // set style
                //paint.setStyle(Paint.Style.STROKE);
                paint.setStyle(Paint.Style.FILL);
                // set stroke
                paint.setStrokeWidth(4.5f);
                // draw circle with radius 30
                canvas.drawCircle(25, 25, 10, paint);
                // set on ImageView or any other view
                ImageView imageViewCirculo = (ImageView) findViewById(R.id.imageView15);
                imageViewCirculo.setImageBitmap(bitMap);



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
                textcargando.setVisibility(View.INVISIBLE);
            }else{
                Toast.makeText(getApplicationContext(), "Usuario no habilitado.", Toast.LENGTH_LONG).show();
                pantalla_inicio(null);
                cerrarSesion();
                finish();
            }
        }

    }


    //para el metodo login del webservice
    class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params){
            cantidad_pendientes_sucursal();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tv_amortizaciones_pendientes_numero = (TextView) findViewById(R.id.tv_amortizaciones_pendientes_numero);
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), resultString.getProperty(2).toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "ERROR 2: "+mensaje.toString(), Toast.LENGTH_SHORT).show();
            tv_amortizaciones_pendientes_numero.setText(resultString.getProperty(2).toString());

        }
    }

    private void cantidad_pendientes_sucursal(){
            //OBTENEMOS INFROMACION
            String SOAP_ACTION = "http://ws.sudseguros.com/SearchAReceiptConSaldoCantidad";
            String METHOD_NAME = "SearchAReceiptConSaldoCantidad";
            String NAMESPACE = "http://ws.sudseguros.com/";
            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

            try {

                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                Request.addProperty("numero_recibo", "");
                Request.addProperty("id_sucursal", id_sucursal.toString());
                Request.addProperty("imei_dispositivo", "357136081571342");
                Request.addProperty("usuario_ws", "SUdMOv1l3");
                Request.addProperty("password_ws", "AXr53.o1");

                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true; //tipo de servicio .net
                soapEnvelope.setOutputSoapObject(Request);

                //Invoca al web service
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);

                resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo



            }
            catch (Exception ex) {
                mensaje = "ERROR 125: " + ex.getMessage();
            }

    }

    /**
     * Función para comprobar si hay conexión a Internet
     * @param context
     * @return boolean
     */

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

}

