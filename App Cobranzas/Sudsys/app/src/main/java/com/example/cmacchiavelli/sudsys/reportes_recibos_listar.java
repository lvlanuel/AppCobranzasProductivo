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
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
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

public class reportes_recibos_listar extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    //variables globales
    String id_sucursal, id_empleado;

    String numero_recibo, mensaje, mensaje2;
    String desde, hasta;

    //variables recibidas ws
    String id_recibo_recibido, numero_recibo_recibido,fecha_emision_recibido, recibido_de_recibido, moneda_recibido, importe_recibido,
    importe_bs_recibido,
    concepto_recibido,
    tipo_pago_recibido,
    cheque_banco_recibido,
    cheque_numero_recibido,
    cobrador_recibido;

    private TextView tv1;
    Button button2;

    //para el webservice
    SoapObject resultString;
    SoapObject resultString2;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    //variables para imprimir
    String resultado, datosReciboCompleto;
    SoapObject SoapArray;

    private ZebraPrinter zebraPrinter;
    private Connection connection;


    //Definimos el ListView para clientes
    private ListView listView;
    //Elementos que se mostraran en el listview
    String[] lenguajeProgramacion = new String[] {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    String[] lenguajeProgramacionCompleto = new String[] {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    //String[] array1= new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_recibos_listar);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Lista de Recibos");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();
        numero_recibo=datos.getString("parametro_numero_recibo");
        desde=datos.getString("parametro_desde");
        hasta=datos.getString("parametro_hasta");
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");

        //Toast.makeText(getApplicationContext(), "Busqueda " + desde , Toast.LENGTH_LONG).show();
        button2=(Button)findViewById(R.id.button2);
        tv1=(TextView) findViewById(R.id.tv1);

        //llamamos a la tarea en segundo plano
        reportes_recibos_listar.SegundoPlano tarea = new reportes_recibos_listar.SegundoPlano();
        tarea.execute();

        reportes_recibos_listar.TercerPlano tarea3 = new reportes_recibos_listar.TercerPlano();
        tarea3.execute();

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


    //para el metodo searchCustomer del webservice
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

            tv1.setText("Clientes Encontrados");
            //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Recibos encontados...", Toast.LENGTH_LONG).show();
        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){

        //Se obtiene de la definicion de wsdl http://localhost:55006/ServiceSud.asmx?wsdl
        /*
        String SOAP_ACTION = "http://ws.sudseguros.com/Login";
        String METHOD_NAME = "Login";re
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";
*/
        String SOAP_ACTION = "http://ws.sudseguros.com/SearchReceipt";
        String METHOD_NAME = "SearchReceipt";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";


        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("numero_recibo", numero_recibo.toString());
            Request.addProperty("fecha_inicio", "01/06/2018");
            Request.addProperty("fecha_fin", "10/10/2030");
            Request.addProperty("id_empleado", id_empleado.toString());
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

            int numero_columnas = 12;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];




            for (int i = 0; i< SoapArrayNivel2.getPropertyCount(); i++)
            {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k=j;
                }
                mensaje = mensaje + "OK: id_cliente: " +  ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                ////lenguajeProgramacion[i]=ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                //lenguajeProgramacion[i]=ClientesArray[i][0] + "-"+ClientesArray[i][1] + "-" + ClientesArray[i][2] + "-" + ClientesArray[i][3] + "-" + ClientesArray[i][4]+ "-" + ClientesArray[i][5]+ "-" + ClientesArray[i][6]+ "-" + ClientesArray[i][7]+ "-" + ClientesArray[i][8]+ "-" + ClientesArray[i][9]+ "-" + ClientesArray[i][10]+ "-" + ClientesArray[i][11];
                //completo lenguajeProgramacion[i]=ClientesArray[i][0] + "-"+ClientesArray[i][1] + "-" + ClientesArray[i][2] + "-" + ClientesArray[i][3] + "-" + ClientesArray[i][6] + "-" + ClientesArray[i][7] + "-" + ClientesArray[i][8] + "-" + ClientesArray[i][9] + "-" + ClientesArray[i][10];
                lenguajeProgramacion[i]=ClientesArray[i][0]+":"+ "\n" + "Fecha: " + ClientesArray[i][2]+ "\n" + "Nombre: " + ClientesArray[i][3]+ "\n" + "Bs: " + ClientesArray[i][6];
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

            //TEXTO DESAPARECE AL CARGAR
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCliente);
            textcargando.setVisibility(View.GONE);

            //Conectamos miLista a mi ListView
            listView = (ListView) findViewById(R.id.clientes);


            //Declaramos el Array Adactes,le pasamos el contexto, le indicamos para que tenga
            // una simple_expandable_list_item_1(estilo lista) y le damos nuestro Array de String
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, lenguajeProgramacion);

            //Le asignamos el adacter al listView
            listView.setAdapter(adapter);
            //listView.setAdapter(null);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /// Obtiene el valor de la casilla elegida
                    String itemSeleccionado = parent.getItemAtPosition(position).toString();
                    //Preguntamos si es un item seleccionado no vacio
                    if(itemSeleccionado.equals("")){
                        Toast.makeText(getBaseContext(),"No existen datos para mostrar", Toast.LENGTH_SHORT).show();
                    }else{
                    //sacamos el id cliente
                    String string = itemSeleccionado;
                    String[] parts = string.split(":");

                    id_recibo_recibido = parts[0];


                    String imei_dispositivo = getIMEI();
                    String usuario_ws = "SUdMOv1l3";
                    String password_ws = "AXr53.o1";

                    // muestra un mensaje
                    //Toast.makeText(getApplicationContext(), mensaje + id_recibo_recibido, Toast.LENGTH_SHORT).show();
                    //imprime si hace click en el elemento


                    //preguntamos si hay conexion a internet, sino no pasa a la siguiente ventana con finsh()
                    if (!compruebaConexion(getApplicationContext())) {
                        Toast.makeText(getBaseContext(),"Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        // muestra un mensaje
                        Toast.makeText(getApplicationContext(),
                                "Numero Recibo a Imprimir: " + id_recibo_recibido, Toast.LENGTH_SHORT).show();
                        ejecutar_emision_recibos_imprimir(null);
                    }
                    }
                }
            });


        }
        public void ejecutar_emision_recibos_imprimir(View view){
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), reportes_recibos_imprimir.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_recibo_recibido", id_recibo_recibido.toString());
            //i.putExtra("parametro_recibido_de_recibido", recibido_de_recibido.toString());
            //i.putExtra("parametro_importe_bs_recibido", importe_bs_recibido.toString());
            //i.putExtra("parametro_concepto_recibido", concepto_recibido.toString());
            //i.putExtra("parametro_tipo_pago_recibido", tipo_pago_recibido.toString());
            //i.putExtra("parametro_cheque_banco_recibido", cheque_banco_recibido.toString());
            //i.putExtra("parametro_cheque_numero_recibido", cheque_numero_recibido.toString());
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            i.putExtra("parametro_tipo_emision", "Automatica");

            startActivity(i);
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
