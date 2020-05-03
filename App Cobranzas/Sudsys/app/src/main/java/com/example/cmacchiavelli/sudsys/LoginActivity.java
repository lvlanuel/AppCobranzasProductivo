package com.example.cmacchiavelli.sudsys;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LoginActivity extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    //definimos el boton que estara a la escucha, para almacenar en base de datos
    Button botonIngresar;

    //definimos el txt que estara a la escucha, para almacenar en base de datos
    EditText textoUsuario, textoContrasenia;

    //para el webservice
    SoapObject resultString;
    String autentificado="", autentificado2, mensaje, mensaje2, mensaje_bancos, mensaje_bancos2, parametro_usuario_recibido, id_empleado, id_sucursal;
    private TextView tv1;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //ponemos las opciones del menu
        //setSupportActionBar(toolbar);

        //boton ingresar(insertar) a la base de datos
        botonIngresar=(Button)findViewById(R.id.ingresar);
        textoUsuario=(EditText)findViewById(R.id.usuario);
        SharedPreferences preferences = getSharedPreferences("datosusu",Context.MODE_PRIVATE);
        textoUsuario.setText(preferences.getString("usuario",""));
        textoContrasenia=(EditText)findViewById(R.id.contrasenia);
        textoContrasenia.setText(preferences.getString("pasw",""));
        parametro_usuario_recibido="";


        //preguntamos si ya hay un logueo
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
        String selection = Estructura_BBDD.NOMBRE_COLUMNA3 + " != ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { "" };

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

            //almacenamos tb id_sucursal, id_empleado para pasar a la otra actividad
            id_empleado=c.getString(0);
            id_sucursal=c.getString(2);
            //textoNombre.setText(c.getString(0));
            //textoApellido.setText(c.getString(1));

            //si encuentra algo en la bd, pasa directamente a la otra actividad

            //OJO DESBLOQUEARRRRRRRRRRRRRRRRR
            ejecutar_pantalla_principal(null);

            //ejecutar_listado_cuotas_polizas(null);
            //ejecutar_listado_cuotas_polizas_imprimir(null);

            //BORRAMOS TODODELATABLAEstructura_BBDD_Cuotas_Canceladas
            //ELIMINAMOS REGISTRO PARA INSERTAR NUEVO
            //importamos la clase BB_DD_HELPER

        }catch (Exception e){
            //Toast.makeText(getApplicationContext(), e.getMessage()+" no se encontro usuario logueado", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "No se encontro usuario logueado.", Toast.LENGTH_LONG).show();
        }




        //ponemos a la escucha
        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compruebaConexion(getApplicationContext())) {
                    Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else {
                    //llamamos a la tarea antes del segundo plano

                    SharedPreferences preferences = getSharedPreferences("datosusu",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("usuario",textoUsuario.getText().toString());
                    editor.putString("pasw",textoContrasenia.getText().toString());

                     editor.commit();
                    AntesSegundoPlano tarea_antes = new AntesSegundoPlano();
                    tarea_antes.execute();
                }
            }
        });
    }


    //@Override sirve para sobrescribir un método
    @Override public  boolean onCreateOptionsMenu(Menu mimenu){
       getMenuInflater().inflate(R.menu.menu_superior, mimenu);
        return true;
    }


    //mostrar_actividad_info
    //llamamos a la actividad info
    public void ejecutar_info(View view){
        //Intent es como evento, debe mejecutar el jafimeva infoClase
        Intent i=new Intent(this, info.class);
        startActivity(i);
    }



//click en algun elemento del menu
    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu){
        //capturamos el id del menu presionado
        int id=opcion_menu.getItemId();
        //si es el mismo id recibido del item presionado con el de configuracion almacenado en el archivo R
        if(id==R.id.configuracion){
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



    //llamamos a la actividad pantalla_principal
    public void ejecutar_pantalla_principal(View view){

        if(textoUsuario.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Debe ingresar el usuario por favor.", Toast.LENGTH_LONG).show();
        }else {
            if(textoUsuario.getText().toString().trim().isEmpty()){
                Toast.makeText(getApplicationContext(), "Debe ingresar la contraseña por favor.", Toast.LENGTH_LONG).show();
            }else {
                //Intent es como evento, debe mejecutar el java infoClase
                Intent i=new Intent(this, pantalla_principal.class);
                //guardamos el parametro usuario para recuperarlo en otra actividad
                i.putExtra("parametro_usuario", textoUsuario.getText().toString());
                startActivity(i);
                //Elimina de la pila de Actividades para que no vuelvan a esta actividad haciendo click en atras
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
            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            if(autentificado.toString().equals("SI")){
                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                ejecutar_pantalla_principal(null);
            }else {
                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Intenta nuevamente por favor" +mensaje, Toast.LENGTH_LONG).show();
                //muestra texto mientras carga
                Button textcargando2 = (Button) findViewById(R.id.ingresar);
                textcargando2.setVisibility(View.VISIBLE);
                ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
                textcargando.setVisibility(View.GONE);
            }
        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){


        String SOAP_ACTION = "http://ws.sudseguros.com/Login";
        String METHOD_NAME = "Login";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("usuario", textoUsuario.getText().toString());
            Request.addProperty("password", textoContrasenia.getText().toString());
            Request.addProperty("imei_dispositivo", "357136081571342" );
            Request.addProperty("descripcion_dispositivo", getDescriptionPhone());
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");
            Request.addProperty("id_empleado", "");

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

            if(resultString.getProperty(0).toString().equals("1") && resultString.getProperty(8).toString().equals("true")) {
                //INSERTAMOS DATOS A LA BASE DE DATOS
                SQLiteDatabase db = helper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                values.put(Estructura_BBDD.NOMBRE_COLUMNA1, resultString.getProperty(2).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA2, resultString.getProperty(3).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA3, resultString.getProperty(4).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA4, resultString.getProperty(5).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA5, resultString.getProperty(6).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA6, resultString.getProperty(7).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA7, resultString.getProperty(8).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA8, resultString.getProperty(9).toString());
                values.put(Estructura_BBDD.NOMBRE_COLUMNA9, resultString.getProperty(10).toString());

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(Estructura_BBDD.TABLE_NAME, null, values);
                //Mensaje si guardo los datos del acceso exitosamente
                mensaje2 = "Acceso Exitoso " + newRowId;

                autentificado="SI";
            }else {
                if(resultString.getProperty(8).toString().equals("false")){
                    mensaje = "El usuario no se encuentra habilitado";
                }else {
                    mensaje = "Usuario o contraseña incorrecta";
                }
                //mensaje2 = resultString.getProperty(0)+" "+resultString.getProperty(1)+" No se encontro al usuario en WebService ";
                mensaje2 = "No se encontro al usuario logueado ";
                autentificado="NO";
            }


        }
        catch (Exception ex) {
            mensaje = "ERROR 1: " + ex.getMessage();
        }
    }


    //para el metodo login del webservice
    class AntesSegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            Button textcargando2 = (Button) findViewById(R.id.ingresar);
            textcargando2.setVisibility(View.GONE);
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
            textcargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params){
            lista_bancos();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), mensaje_bancos, Toast.LENGTH_LONG).show();
            if(autentificado2.toString().equals("SI")){
                //llamamos a la tarea en segundo plano
                SegundoPlano tarea = new SegundoPlano();
                tarea.execute();
            }else {
                Toast.makeText(getApplicationContext(), "Intenta nuevamente por favor, bancos no almacenados", Toast.LENGTH_LONG).show();
                //muestra texto mientras carga
                Button textcargando2 = (Button) findViewById(R.id.ingresar);
                textcargando2.setVisibility(View.VISIBLE);
                ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
                textcargando.setVisibility(View.GONE);
            }
        }
    }


    //llamamos al metodo login del webservice
    private void lista_bancos(){

        String SOAP_ACTION = "http://ws.sudseguros.com/SearchFinancialEntity";
        String METHOD_NAME = "SearchFinancialEntity";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

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
            int numero_columnas = 2;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];


                for (int i = 0; i < SoapArrayNivel2.getPropertyCount(); i++) {
                    SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                    for (int j = 0; j < numero_columnas; j++) {
                        ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                        k = j;
                    }
                        //INSERTAMOS DATOS A LA BASE DE DATOS
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();

                        values.put(Estructura_BBDD_Bancos.NOMBRE_COLUMNA1, ClientesArray[i][0]);
                        values.put(Estructura_BBDD_Bancos.NOMBRE_COLUMNA2, ClientesArray[i][0]);
                        values.put(Estructura_BBDD_Bancos.NOMBRE_COLUMNA3, ClientesArray[i][1]);

                        db.insert(Estructura_BBDD_Bancos.TABLE_NAME, null, values);


                        //Mensaje si guardo los datos del acceso exitosamente
                        mensaje2 = "Registro de Banco Exitoso ";

                }
            autentificado2="SI";
            mensaje_bancos = "Almacenamiento bancos exitoso, total: " +SoapArrayNivel2.getPropertyCount();

        }
        catch (Exception ex) {
            autentificado2="NO";
            mensaje_bancos = "ERROR: " + ex.getMessage();
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
            mensaje = "ERROR: " + ex.getMessage();
        }

        return imei;

    }

    private String getDescriptionPhone() {

        String descripcion = "";

        try{
            descripcion = Build.MODEL + " " + Build.MANUFACTURER + " " + Build.VERSION.RELEASE;
        }
        catch (SecurityException ex) {
            descripcion = ex.getMessage();
        }

        return descripcion;

    }

    public void ejecutar_listado_cuotas_polizas(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_llave_unica_recibo", "");
        i.putExtra("parametro_id_sucursal", "");
        i.putExtra("parametro_id_empleado", "");
        i.putExtra("parametro_tipo_emision", "");
        i.putExtra("parametro_id_cliente", "");
        i.putExtra("parametro_id_recibo", "");

        startActivity(i);
        finish();
    }

    public void ejecutar_listado_cuotas_polizas_imprimir(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas_imprimir.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_recibo", "20180725364401");

        startActivity(i);
        finish();
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
