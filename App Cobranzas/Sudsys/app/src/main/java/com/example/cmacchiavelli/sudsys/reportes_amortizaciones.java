package com.example.cmacchiavelli.sudsys;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class reportes_amortizaciones extends AppCompatActivity {


    //incluimos el tool_bar
    private Toolbar toolbar;

    EditText txt_numero_recibo;

    //Definimos el ListView para clientes
    private ListView listView;
    //Elementos que se mostraran en el listview
    String[] lenguajeProgramacion = new String[] {"", "", "", "", "", "", "", "", "", "", ""};
    String[] lenguajeProgramacion_aux = new String[] {"", "", "", "", "", "", "", "", "", "", ""};

    String id_empleado, id_recibo_recibido, id_cliente, tipo_cliente;
    String moneda, numero_recibo, nombre_cliente, monto_total;
    String id_sucursal;

    String parametro_id_empleado,  parametro_id_sucursal, parametro_tipo, resultado, mensaje, sw="0";

    //para el webservice
    SoapObject resultString;
    SoapObject resultString2;
    SoapObject SoapArray;
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    SoapObject SoapArrayNivel4;
    SoapObject SoapArrayNivel5;
    SoapObject SoapArrayNivel6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_amortizaciones);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        Bundle datos=getIntent().getExtras();


        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo=datos.getString("parametro_tipo");
        //Toast.makeText(getApplicationContext(), parametro_tipo, Toast.LENGTH_LONG).show();
        txt_numero_recibo=(EditText)findViewById(R.id.numero_recibo);

        if (parametro_tipo.equals("amortizar_cuotas_pendientes")) {
            toolbar.setTitle("Recibos Pendientes Otra Sucursal");
        }else{
            toolbar.setTitle("Reimpresion cuotas recibos");
        }
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

        //llamamos a la tarea en cuarto plano
        reportes_amortizaciones.CuartoPlano tarea = new reportes_amortizaciones.CuartoPlano();
        tarea.execute();


    }

    //para el metodo searchCustomer del webservice
    private class CuartoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            textcargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params){

            listar_pendientes_otra_sucursal();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //tv1.setText("Clientes Encontrados"  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Polizas amortizadas encontradas...", Toast.LENGTH_LONG).show();

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
                    }else {
                        //sacamos el id cliente
                        String string = lenguajeProgramacion_aux[position].toString();
                        String[] parts = string.split("\n");
                    /*
                    int posicion_inicio_id_recibo = string.indexOf(":");
                    int posicion_final_id_recibo = string.indexOf(".");

                    id_recibo_recibido = (string.substring(posicion_inicio_id_recibo+1, posicion_final_id_recibo));

                    Toast.makeText(getApplicationContext(),
                            "caracter inicio: "+posicion_inicio_id_recibo+" " + posicion_final_id_recibo +" "+id_recibo_recibido, Toast.LENGTH_SHORT).show();
                    */


                        id_recibo_recibido = parts[0];
                        id_cliente = parts[1];
                        tipo_cliente = parts[2];
                        monto_total = parts[3];
                        numero_recibo = parts[4];
                        nombre_cliente = parts[5];

                        // muestra un mensaje
                        //Toast.makeText(getApplicationContext(), mensaje + id_recibo_recibido, Toast.LENGTH_SHORT).show();
                        //imprime si hace click en el elemento


                        //preguntamos si hay conexion a internet, sino no pasa a la siguiente ventana con finsh()
                        if (!compruebaConexion(getApplicationContext())) {
                            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // muestra un mensaje
                           // Toast.makeText(getApplicationContext(),"Numero Recibo a Imprimir: " + id_recibo_recibido, Toast.LENGTH_SHORT).show();

                            if (parametro_tipo.equals("amortizar_cuotas_pendientes")) {
                                ejecutar_amortizaciones2(null);
                            } else {
                                ejecutar_amortizaciones3(null);
                            }

                        }
                    }
                }
            });
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            textcargando.setVisibility(View.GONE);

        }
    }



    //llamamos al metodo login del webservice
    private void listar_pendientes_otra_sucursal(){
        String SOAP_ACTION ="";
        String METHOD_NAME ="";
        if (parametro_tipo.equals("amortizar_cuotas_pendientes")) {
            SOAP_ACTION = "http://ws.sudseguros.com/SearchReceiptPendienteRegional";
            METHOD_NAME = "SearchReceiptPendienteRegional";
        }else {
            SOAP_ACTION = "http://ws.sudseguros.com/SearchReceiptAmortizadosRegional";
            METHOD_NAME = "SearchReceiptAmortizadosRegional";
        }
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";


        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("numero_recibo", "");
            Request.addProperty("fecha_inicio", "");
            Request.addProperty("fecha_fin", "");
            Request.addProperty("id_empleado", parametro_id_empleado);
            Request.addProperty("id_sucursal", parametro_id_sucursal);
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
            SoapArrayNivel4 = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            //Obtiene el listado de clientes
            SoapArrayNivel5 = (SoapObject) SoapArrayNivel4.getProperty(2);


            String resultado = "";
            mensaje ="";
            int k=0;

            //tener cuidado con esta linea poner la cantidad de vectores enviados
            int numero_columnas = 15;
            String[][] ClientesArray = new String [SoapArrayNivel5.getPropertyCount()][numero_columnas];




            for (int i = 0; i< SoapArrayNivel5.getPropertyCount(); i++)
            {
                SoapArrayNivel6 = (SoapObject) SoapArrayNivel5.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel6.getProperty(j).toString();
                    k=j;
                }
                mensaje = mensaje + "OK: id_cliente: " +  ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                ////lenguajeProgramacion[i]=ClientesArray[i][0] + " ci_nit: " + ClientesArray[i][1]  + " nombre: " + ClientesArray[i][2]  + " apellido paterno: " + ClientesArray[i][3]  + " apellido materno: " + ClientesArray[i][4]  + " ";
                //lenguajeProgramacion[i]=ClientesArray[i][0] + "-"+ClientesArray[i][1] + "-" + ClientesArray[i][2] + "-" + ClientesArray[i][3] + "-" + ClientesArray[i][4]+ "-" + ClientesArray[i][5]+ "-" + ClientesArray[i][6]+ "-" + ClientesArray[i][7]+ "-" + ClientesArray[i][8]+ "-" + ClientesArray[i][9]+ "-" + ClientesArray[i][10]+ "-" + ClientesArray[i][11];
                //completo lenguajeProgramacion[i]=ClientesArray[i][0] + "-"+ClientesArray[i][1] + "-" + ClientesArray[i][2] + "-" + ClientesArray[i][3] + "-" + ClientesArray[i][6] + "-" + ClientesArray[i][7] + "-" + ClientesArray[i][8] + "-" + ClientesArray[i][9] + "-" + ClientesArray[i][10];
                lenguajeProgramacion[i]="NO. RECIBO: " + ClientesArray[i][1]+ "\n\n" +"FECHA: " + ClientesArray[i][2]+ "\n\n" + "NOMBRE: " + ClientesArray[i][3]+ "\n\n" + "Bs: " + ClientesArray[i][6];
                lenguajeProgramacion_aux[i]=ClientesArray[i][0]+"\n"+ClientesArray[i][12]+"\n"+ClientesArray[i][13]+"\n"+ClientesArray[i][14]+"\n"+ClientesArray[i][1]+"\n"+ClientesArray[i][3]+ "\n" + "FECHA: " + ClientesArray[i][2]+ ".\n" + "NOMBRE: " + ClientesArray[i][3]+ "\n" + "Bs: " + ClientesArray[i][6];
            }

        }
        catch (Exception ex) {
            mensaje = "ERROR: " + ex.getMessage();
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

    public  void  ejecutar_listado_cuotas_polizas_imprimir(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            //Toast.makeText(getApplicationContext(), "cuarto vector: "+parametro_tipo + txt_numero_recibo.getText().toString(), Toast.LENGTH_SHORT).show();
            //llamamos a la tarea en segundo plano
            reportes_amortizaciones.SegundoPlano tarea = new reportes_amortizaciones.SegundoPlano();
            tarea.execute();
        }

    }

    //para el metodo login del webservice
    class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            textcargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params){
            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), resultString.getProperty(2).toString(), Toast.LENGTH_LONG).show();
            if (parametro_tipo.equals("amortizar_cuotas_pendientes")) {
                if (resultString.getProperty(2).toString().equals("0")) {
                    Toast.makeText(getApplicationContext(), "No se encuentra el número de recibo", Toast.LENGTH_SHORT).show();
                } else {
                    ejecutar_amortizaciones(null);
                }
            }
            if (txt_numero_recibo.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "No se encuentra el número de recibo 2", Toast.LENGTH_SHORT).show();
            } else {
                if (parametro_tipo.equals("reportes")) {
                    ejecutar_reportes(null);
                }
            }
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            textcargando.setVisibility(View.GONE);
        }

    }





    private void convertir(){
        if(parametro_tipo.equals("amortizar_cuotas_pendientes")){
            //OBTENEMOS INFROMACION
            String SOAP_ACTION = "http://ws.sudseguros.com/SearchAReceiptConSaldo";
            String METHOD_NAME = "SearchAReceiptConSaldo";
            String NAMESPACE = "http://ws.sudseguros.com/";
            String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

            try {

                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

                Request.addProperty("numero_recibo", txt_numero_recibo.getText().toString());
                Request.addProperty("id_sucursal", parametro_id_sucursal.toString());
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

                sw="1";

            }
            catch (Exception ex) {
                sw="0";
                mensaje = "ERROR: " + ex.getMessage();
                Toast.makeText(getApplicationContext(), "ERROR 1: "+mensaje.toString(), Toast.LENGTH_SHORT).show();
            }


        }
        if(parametro_tipo.equals("reportes")){
            //Intent es como evento, debe mejecutar el java infoClase
            mensaje="Reportes";
        }
    }

    //llamamos a la actividad pantalla_principal
    public void ejecutar_reportes(View view){

        //Intent es como evento, debe mejecutar el java infoClase
        Intent i = new Intent(getApplicationContext(), listado_cuotas_polizas_imprimir.class);
        i.putExtra("parametro_id_recibo", "120186");
        i.putExtra("parametro_numero_recibo", txt_numero_recibo.getText().toString());

        i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
        i.putExtra("parametro_id_empleado", parametro_id_empleado);
        startActivity(i);
        finish();
    }

    public void ejecutar_amortizaciones(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_llave_unica_recibo", "");
            i.putExtra("parametro_id_sucursal", parametro_id_sucursal.toString());
            i.putExtra("parametro_id_empleado", parametro_id_empleado.toString());
            i.putExtra("parametro_tipo_emision", "Automatica");
            i.putExtra("parametro_id_cliente", resultString.getProperty(14).toString());
            i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
            i.putExtra("parametro_id_recibo", resultString.getProperty(2).toString());
            i.putExtra("parametro_total_recibo", resultString.getProperty(15).toString());
            i.putExtra("parametro_id_recibo_sudsys", resultString.getProperty(2).toString());
            i.putExtra("parametro_moneda", resultString.getProperty(6).toString());
            i.putExtra("parametro_numero_recibo_sudsys", txt_numero_recibo.getText().toString());
            i.putExtra("parametro_nombre_cliente", resultString.getProperty(16).toString());
            startActivity(i);
        finish();

    }

    public void ejecutar_amortizaciones2(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_llave_unica_recibo", "");
        i.putExtra("parametro_id_sucursal", parametro_id_sucursal.toString());
        i.putExtra("parametro_id_empleado", parametro_id_empleado.toString());
        i.putExtra("parametro_tipo_emision", "Automatica");
        i.putExtra("parametro_id_cliente", id_cliente.toString());
        i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
        i.putExtra("parametro_id_recibo", id_recibo_recibido.toString());
        i.putExtra("parametro_total_recibo", monto_total.toString());
        i.putExtra("parametro_id_recibo_sudsys",id_recibo_recibido.toString());
        i.putExtra("parametro_moneda", "$us");
        i.putExtra("parametro_numero_recibo_sudsys", numero_recibo.toString());
        i.putExtra("parametro_nombre_cliente", nombre_cliente.toString());
        startActivity(i);

    }

    public void ejecutar_amortizaciones3(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        //Toast.makeText(getApplicationContext(), "id_recibo"+id_recibo_recibido.toString(), Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), listado_cuotas_polizas_imprimir.class);
        i.putExtra("parametro_id_recibo", id_recibo_recibido.toString());
        i.putExtra("parametro_numero_recibo", txt_numero_recibo.getText().toString());

        i.putExtra("parametro_id_sucursal", parametro_id_sucursal);
        i.putExtra("parametro_id_empleado", parametro_id_empleado);
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
