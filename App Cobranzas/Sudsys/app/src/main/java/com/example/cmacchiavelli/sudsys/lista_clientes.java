package com.example.cmacchiavelli.sudsys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class lista_clientes extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    //para el webservice
    SoapObject resultString;
    String respuesta_amortizacion, mensaje_amortizacion, autentificado, mensaje, mensaje2, parametro_nombre, parametro_apellido_paterno, parametro_apellido_materno, parametro_ci_nit, parametro_tipo_cliente, parametro_email_cliente, parametro_tipo;
    private TextView tv1;
    Button button2;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;


    //Definimos el ListView para clientes
    private ListView listView;
    //Elementos que se mostraran en el listview
    String[] lenguajeProgramacion = new String[] {"", "", "", "", "", "", "", "", "", "", ""};
    String[] lenguajeProgramacion_aux = new String[] {"", "", "", "", "", "", "", "", "", "", ""};
    //String[] array1= new String[]{};

    //variables para pasar a la otra actividad
    String id_cliente, nombre_cliente, apellido_paterno_cliente, apellido_materno_cliente, ci_nit_cliente, email_cliente;


    //variables globales
    String id_sucursal, id_empleado, sw_clientes_encontrados="0", parametro_numero_liquidacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);


        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Selección de Cliente");
        //ponemos las opciones del menu
        ////setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();
        parametro_nombre=datos.getString("parametro_nombre");
        parametro_apellido_paterno=datos.getString("parametro_apellido_paterno");
        parametro_apellido_materno=datos.getString("parametro_apellido_materno");
        parametro_ci_nit=datos.getString("parametro_ci_nit");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        parametro_email_cliente=datos.getString("parametro_email_cliente");

        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo=datos.getString("parametro_tipo");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");

        //preguntamos si hay conexion a internet, sino no pasa a la siguiente ventana con finsh()
        if (!compruebaConexion(this)) {
            Toast.makeText(getBaseContext(),"Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Toast.makeText(getApplicationContext(), "Busqueda para: " + parametro_nombre +"-"+ parametro_apellido_paterno+"-"+parametro_apellido_materno+"-"+parametro_ci_nit+"-"+parametro_tipo_cliente , Toast.LENGTH_LONG).show();
        button2=(Button)findViewById(R.id.button2);
        tv1=(TextView) findViewById(R.id.tv1);





        //ponemos a la escucha
        //button2.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //public void onClick(View v) {

        //ponemos a la escucha
        //llamamos a la tarea en segundo plano
        lista_clientes.SegundoPlano tarea = new lista_clientes.SegundoPlano();
        tarea.execute();

        lista_clientes.TercerPlano tarea3 = new lista_clientes.TercerPlano();
        tarea3.execute();

        //}
        //});






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
            Intent i = new Intent(getApplicationContext(), listado_cuotas_polizas_confirmar.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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



    //llamamos a la actividad ejecutar_emision_recibo
    public void ejecutar_emision_recibo(View view){
        //Intent es como evento, debe mejecutar el java ejecutar_emision_recibo
        Intent i=new Intent(this, emision_recibo.class);
        startActivity(i);
        finish();
    }


    //para el metodo searchCustomer del webservice
    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params){
            if (!compruebaConexion(getApplicationContext())) {
                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                //finish();
            }
            else {
                convertir();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            if(sw_clientes_encontrados.equals("1")) {
                tv1.setText("Clientes Encontrados" + mensaje);
                //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Clientes Encontrados...", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "No existen Clientes", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){

        //Se obtiene de la definicion de wsdl http://localhost:55006/ServiceSud.asmx?wsdl
        /*
        String SOAP_ACTION = "http://ws.sudseguros.com/Login";
        String METHOD_NAME = "Login";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";
*/
        String SOAP_ACTION = "http://ws.sudseguros.com/SearchCustomer";
        String METHOD_NAME = "SearchCustomer";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";


        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("nombre", parametro_nombre);
            Request.addProperty("apellido_paterno", parametro_apellido_paterno);
            Request.addProperty("apellido_materno", parametro_apellido_materno );
            Request.addProperty("ci_nit", parametro_ci_nit);
            Request.addProperty("tipo_cliente", parametro_tipo_cliente);
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");

            /*Request.addProperty("nombre", "");
            Request.addProperty("apellido_paterno", "jordan");
            Request.addProperty("apellido_materno", "");
            Request.addProperty("ci_nit", "" );
            Request.addProperty("tipo_cliente", "");
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");*/

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
            int numero_columnas = 7;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];




            for (int i = 0; i< SoapArrayNivel2.getPropertyCount(); i++)
            {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k=j;
                }
                //mensaje = mensaje + "OK: id_cliente: " +  ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                ////lenguajeProgramacion[i]=ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                if(ClientesArray[i][0].equals("anyType{}")){
                    ClientesArray[i][0]="";
                }
                if(ClientesArray[i][1].equals("anyType{}")){
                    ClientesArray[i][1]="";
                }
                if(ClientesArray[i][2].equals("anyType{}")){
                    ClientesArray[i][2]="";
                }
                if(ClientesArray[i][3].equals("anyType{}")){
                    ClientesArray[i][3]="";
                }
                if(ClientesArray[i][4].equals("anyType{}") ){
                    ClientesArray[i][4]="";
                }
                lenguajeProgramacion[i]="NOMBRE: "+ClientesArray[i][2] + " " + ClientesArray[i][3] + "-" + ClientesArray[i][4] + "\n\nCI/NIT: " + ClientesArray[i][1]+ "\n\nCORREO: " + ClientesArray[i][6];
                lenguajeProgramacion_aux[i]=ClientesArray[i][0] + ":"+ClientesArray[i][2] + ":" + ClientesArray[i][3] + ":" + ClientesArray[i][4] + ":" + ClientesArray[i][1] + ":" + ClientesArray[i][6];

                sw_clientes_encontrados="1";
            }

            //lenguajeProgramacion=new String[]{"ss"};



            //mensaje = "OK: id_cliente: " +  ClientesArray[0][0] + " ci_nit: " + ClientesArray[0][1] + " nombre: " + ClientesArray[0][2] + " apellido paterno: " + ClientesArray[0][3] + " apellido materno: " + ClientesArray[0][4];
            //mensaje = "OK: id_cliente: " +  ClientesArray[3][3] + " ci_nit: " + ClientesArray[3][1] + " nombre: " + ClientesArray[3][2] + " apellido paterno: " + ClientesArray[3][3] + " apellido materno: " + ClientesArray[3][4] + " ";
            //mensaje = mensaje + "OK: id_cliente: " +  ClientesArray[0][0] + " ci_nit: " + ClientesArray[0][1] + " nombre: " + ClientesArray[0][2] + " apellido paterno: " + ClientesArray[0][3] + " apellido materno: " + ClientesArray[0][4] + " ";

        }
        catch (Exception ex) {
            mensaje = "ERROR: " + ex.getMessage();
        }
    }


    //para el metodo searchCustomer del webservice
    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
            textcargando.setVisibility(View.VISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params){

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //Conectamos miLista a mi ListView
            listView = (ListView) findViewById(R.id.clientes);


            //Declaramos el Array Adactes,le pasamos el contexto, le indicamos para que tenga
            // una simple_expandable_list_item_1 y le damos nuestro Array de String
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, lenguajeProgramacion);




            //Le asignamos el adacter al listView
            listView.setAdapter(adapter);
            //listView.setAdapter(null);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!compruebaConexion(getApplicationContext())) {
                        Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                    else {
                        BorrarBasesDatos();

                        /// Obtiene el valor de la casilla elegida
                        String itemSeleccionado = parent.getItemAtPosition(position).toString();
                        //Preguntamos si es un item seleccionado no vacio
                        if (itemSeleccionado.equals("")) {
                            Toast.makeText(getBaseContext(), "No existen datos para mostrar", Toast.LENGTH_SHORT).show();
                        } else {
                            //sacamos el id cliente
                            String string = lenguajeProgramacion_aux[position].toString();
                            String[] parts = string.split(":");

                            id_cliente = parts[0];
                            nombre_cliente = parts[1];
                            apellido_paterno_cliente = parts[2];
                            apellido_materno_cliente = parts[3];
                            ci_nit_cliente = parts[4];
                            email_cliente = parts[5].replace(';', ' ');
                            ;
                            //preguntamos si hay conexion a internet, sino no pasa a la siguiente ventana con finsh()
                            if (!compruebaConexion(getApplicationContext())) {
                                Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (parametro_tipo.equals("crear_recibo")) {
                                    // muestra un mensaje
                                    //Toast.makeText(getApplicationContext(),"Emitir recibo para: " + nombre_cliente, Toast.LENGTH_SHORT).show();

                                    //deshabilitamos esto si queremos que no haya pre seleccion de cuotas
                                    //ejecutar_emision_recibo(null);

                                    //habilitamos esto si queremos que si haya pre seleccion de cuotas
                                    ejecutar_pre_amortizacion_listado(null);
                                }
                                if (parametro_tipo.equals("polizas_cliente")) {
                                    // muestra un mensaje
                                    Toast.makeText(getApplicationContext(),
                                            "Buscando cuotas vencidas: " + nombre_cliente, Toast.LENGTH_SHORT).show();
                                    ejecutar_polizas_clientes(null);
                                }
                            }

                        }
                    }
                }
            });

            //TEXTO DESAPARECE AL CARGAR
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
            textcargando.setVisibility(View.GONE);
        }

        //llamamos a la actividad pantalla_principal
        public void ejecutar_emision_recibo(View view){
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), emision_recibo.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_nombre_cliente", nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", ci_nit_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente", email_cliente);
            i.putExtra("parametro_total_a_pagar", "0");

            i.putExtra("parametro_tipo_actividad", "Nuevo");
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
            finish();
        }

        //llamamos a la actividad pantalla_principal
        public void ejecutar_polizas_clientes(View view){
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), listado_polizas_clientes.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_nombre_cliente", nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", ci_nit_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente", email_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

            i.putExtra("parametro_tipo_actividad", "Nuevo");
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
            finish();
        }

        //llamamos a la actividad pantalla_principal
        public void ejecutar_pre_amortizacion_listado(View view){
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), listado_pre_polizas_clientes.class);

            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_nombre_cliente", nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", ci_nit_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente", email_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

            i.putExtra("parametro_tipo_actividad", "Nuevo");
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
            finish();
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
        lista_clientes.TareaEliminarPreCuotas tarea3 = new lista_clientes.TareaEliminarPreCuotas();
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
                Toast.makeText(getApplicationContext(), mensaje_amortizacion+"Error no se puede eliminar la Pre Amortizacion temporal en el sistema sudsys.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void eliminar_cuota_pre_amortizada_no_utilizada() {
        String SOAP_ACTION = "http://ws.sudseguros.com/EliminarPreAmortizacionNoUtilizada";
        String METHOD_NAME = "EliminarPreAmortizacionNoUtilizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        try {

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
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            respuesta_amortizacion = "" + resultString.getProperty(0);
            mensaje_amortizacion = "" + resultString.getProperty(1);

        } catch (Exception ex) {
            mensaje_amortizacion = "ERROR 13: " + ex.getMessage();
        }
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