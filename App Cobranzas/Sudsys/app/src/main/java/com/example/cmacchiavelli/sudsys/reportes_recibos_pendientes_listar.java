package com.example.cmacchiavelli.sudsys;

import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class reportes_recibos_pendientes_listar extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;
    //Definimos el ListView para clientes
    private ListView listView;
    //Elementos que se mostraran en el listview
    String[] lenguajeProgramacion = new String[] {"", "", "", "", "", "", "", "", "", "", ""};
    String[] lenguajeProgramacion_aux = new String[] {"", "", "", "", "", "", "", "", "", "", ""};

    String mensaje,id_empleado, id_recibo_recibido, id_cliente, tipo_cliente;
    String moneda, numero_recibo, nombre_cliente, monto_total;
    String id_sucursal;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_recibos_pendientes_listar);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Lista de Recibos");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

        Bundle datos=getIntent().getExtras();
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");


        //llamamos a la tarea en segundo plano
        reportes_recibos_pendientes_listar.SegundoPlano tarea = new reportes_recibos_pendientes_listar.SegundoPlano();
        tarea.execute();
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

    public void pantalla_inicio(View view){
        super.finish();
    }

    //mostrar_actividad_info al hacer clieck en el item del menu
    //llamamos a la actividad info
    public void ejecutar_info(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(this, info.class);
        startActivity(i);
    }





    //para el metodo searchCustomer del webservice
    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
//muestra texto mientras carga
            //ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            //textcargando.setVisibility(View.VISIBLE);
            dialog = new ProgressDialog(reportes_recibos_pendientes_listar.this);
            dialog.setMessage("Buscarndo recibos...");
            dialog.setCancelable (false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //tv1.setText("Clientes Encontrados"  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Pendientes Encontrados...", Toast.LENGTH_LONG).show();

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
                    if (itemSeleccionado.equals("")) {
                        Toast.makeText(getBaseContext(), "No existen datos para mostrar", Toast.LENGTH_SHORT).show();
                    } else {
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
                            if(tipo_cliente.equals("Juridico") || tipo_cliente.equals("JURIDICO")){
                                // muestra un mensaje
                                Toast.makeText(getApplicationContext(),
                                        "El cliente es de tipo " + tipo_cliente +", realizar la amortización desde el sistema Sudsys.", Toast.LENGTH_SHORT).show();
                            }else {
                                // muestra un mensaje
                                Toast.makeText(getApplicationContext(),
                                        "Numero Recibo Seleccionado: " + numero_recibo, Toast.LENGTH_SHORT).show();


                                ejecutar_amortizaciones(null);
                            }
                        }
                    }
                }
            });
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            textcargando.setVisibility(View.GONE);
            dialog.hide();

        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){

        String SOAP_ACTION = "http://ws.sudseguros.com/SearchReceiptPendiente";
        String METHOD_NAME = "SearchReceiptPendiente";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";


        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("numero_recibo", "");
            Request.addProperty("fecha_inicio", "");
            Request.addProperty("fecha_fin", "");
            Request.addProperty("id_empleado", id_empleado);
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

            //tener cuidado con esta linea poner la cantidad de vectores enviados
            int numero_columnas = 15;
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
                lenguajeProgramacion[i]="NO. RECIBO: " + ClientesArray[i][1]+ "\n\n" +"FECHA: " + ClientesArray[i][2]+ "\n\n" + "NOMBRE: " + ClientesArray[i][3]+ "\n\n" + "Bs: " + ClientesArray[i][6];
                lenguajeProgramacion_aux[i]=ClientesArray[i][0]+"\n"+ClientesArray[i][12]+"\n"+ClientesArray[i][13]+"\n"+ClientesArray[i][14]+"\n"+ClientesArray[i][1]+"\n"+ClientesArray[i][3]+ "\n" + "FECHA: " + ClientesArray[i][2]+ ".\n" + "NOMBRE: " + ClientesArray[i][3]+ "\n" + "Bs: " + ClientesArray[i][6];
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


    public void ejecutar_amortizaciones(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(getApplicationContext(), listado_cuotas_polizas.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_id_llave_unica_recibo", "");
        i.putExtra("parametro_id_sucursal", id_sucursal.toString());
        i.putExtra("parametro_id_empleado", id_empleado.toString());
        i.putExtra("parametro_tipo_emision", "Automatica");
        i.putExtra("parametro_id_cliente", id_cliente.toString());
        i.putExtra("parametro_id_sucursal", id_sucursal);
        i.putExtra("parametro_id_recibo", id_recibo_recibido.toString());
        i.putExtra("parametro_total_recibo", monto_total.toString());
        i.putExtra("parametro_id_recibo_sudsys",id_recibo_recibido.toString());
        i.putExtra("parametro_moneda", "$us");
        i.putExtra("parametro_numero_recibo_sudsys", numero_recibo.toString());
        i.putExtra("parametro_nombre_cliente", nombre_cliente.toString());
        startActivity(i);

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
