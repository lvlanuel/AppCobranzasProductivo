package com.example.cmacchiavelli.sudsys;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class listado_pre_polizas_clientes extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView rvMusicas;
    private GridLayoutManager glm;
    private CuotasPreClienteAdapter adapter;
    ArrayList<CuotasPreCliente> data3 = new ArrayList<>();
    //incluimos el tool_bar
    private Toolbar toolbar;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    double saldo_recibo=0;
    double resultado = 0;

    String parametro_id_empleado, parametro_id_sucursal, parametro_tipo, parametro_id_cliente, parametro_id_recibo, mensaje,
            parametro_tipo_actividad,parametro_nombre_cliente, parametro_apellido_paterno_cliente, parametro_apellido_materno_cliente,
            parametro_ci_nit_cliente, parametro_tipo_cliente, parametro_email_cliente, parametro_numero_liquidacion, encontrado, mensaje2;

    String parametro_total_recibo;

    TextView tv_consumo_recibo, tv_saldo_recibo;

    TextView tv_total_recibo, tv_nombre_cliente, tv_id_sucursal_usuario;


    String mensaje_amortizacion, respuesta_amortizacion;
    //para el webservice
    SoapObject resultString;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_pre_polizas_clientes);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Pre Seleccion Polizas Cliente");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        //buscamos a los clientes, PERO PRIMERO RECUPERAMOS LA VARIABLES ENVIADAS HACIA ESTA ACTIVIDAD
        Bundle datos=getIntent().getExtras();
        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo=datos.getString("parametro_tipo");

        parametro_tipo_actividad = datos.getString("parametro_tipo_actividad");
        parametro_id_cliente = datos.getString("parametro_id_cliente");
        parametro_nombre_cliente = datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente = datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente = datos.getString("parametro_apellido_materno_cliente");
        parametro_ci_nit_cliente = datos.getString("parametro_ci_nit_cliente");
        parametro_tipo_cliente = datos.getString("parametro_tipo_cliente");
        parametro_email_cliente = datos.getString("parametro_email_cliente");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");
        parametro_id_recibo="";

        //Toast.makeText(getApplicationContext(), parametro_id_cliente, Toast.LENGTH_LONG).show();

        BorrarBasesDatos();

        listado_pre_polizas_clientes.SegundoPlano tarea = new listado_pre_polizas_clientes.SegundoPlano();
        tarea.execute();

        rvMusicas = (RecyclerView) findViewById(R.id.rv_musicas);

        rvMusicas.setHasFixedSize(true);
        rvMusicas.setItemViewCacheSize(200);
        rvMusicas.setDrawingCacheEnabled(true);
        rvMusicas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //define el tamaño del item en el adaptador, como celda
        glm = new GridLayoutManager(this, 1);
        rvMusicas.setLayoutManager(glm);
        adapter = new CuotasPreClienteAdapter(dataSet());
        rvMusicas.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //llamamos a la tarea en segundo plano




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //la primera linea vacia mi lista que esta en la variable data3 y la segunda linea actualiza mi Adapter
                data3.clear();
                adapter.notifyDataSetChanged();

                // Esto se ejecuta cada vez que se realiza el gesto
                Bundle datos=getIntent().getExtras();
                parametro_id_empleado=datos.getString("parametro_id_empleado");
                parametro_id_sucursal=datos.getString("parametro_id_sucursal");
                parametro_tipo=datos.getString("parametro_tipo");

                parametro_tipo_actividad = datos.getString("parametro_tipo_actividad");
                parametro_id_cliente = datos.getString("parametro_id_cliente");
                parametro_nombre_cliente = datos.getString("parametro_nombre_cliente");
                parametro_apellido_paterno_cliente = datos.getString("parametro_apellido_paterno_cliente");
                parametro_apellido_materno_cliente = datos.getString("parametro_apellido_materno_cliente");
                parametro_ci_nit_cliente = datos.getString("parametro_ci_nit_cliente");
                parametro_tipo_cliente = datos.getString("parametro_tipo_cliente");
                parametro_email_cliente = datos.getString("parametro_email_cliente");
                parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");
                parametro_id_recibo="";
                listado_pre_polizas_clientes.SegundoPlano tarea = new listado_pre_polizas_clientes.SegundoPlano();
                tarea.execute();

                rvMusicas = (RecyclerView) findViewById(R.id.rv_musicas);

                rvMusicas.setHasFixedSize(true);
                rvMusicas.setItemViewCacheSize(200);
                rvMusicas.setDrawingCacheEnabled(true);
                rvMusicas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                //define el tamaño del item en el adaptador, como celda
                glm = new GridLayoutManager(getApplicationContext(), 1);
                rvMusicas.setLayoutManager(glm);
                adapter = new CuotasPreClienteAdapter(dataSet());
                rvMusicas.setAdapter(adapter);

                //Activamos si queremos llenar el recycler con un primer elemento de ejemplo
                listado_pre_polizas_clientes.TercerPlano tarea2 = new listado_pre_polizas_clientes.TercerPlano();
                tarea2.execute();

                swipeRefreshLayout.setRefreshing(false);

            }
        });




        if(!parametro_numero_liquidacion.equals("")) {
            //llamamos a la tarea que cargara los datos del cliente
            listado_pre_polizas_clientes.TareaDatosCliente tarea_datos_cliente = new listado_pre_polizas_clientes.TareaDatosCliente();
            tarea_datos_cliente.execute();
        }else{
            tv_nombre_cliente=(TextView) findViewById(R.id.tv_nombre_cliente);
            tv_nombre_cliente.setText(parametro_nombre_cliente);
        }

        //Activamos si queremos llenar el recycler con un primer elemento de ejemplo
        listado_pre_polizas_clientes.TercerPlano tarea2 = new listado_pre_polizas_clientes.TercerPlano();
        tarea2.execute();




        final Button btnCalcular = (Button) findViewById(R.id.btn_paga);
        final Button btn_guardar = (Button) findViewById((R.id.btn_guardar));

        //funcion click boton calcular
        btnCalcular.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resultado=0;
                saldo_recibo=0;

                String[] escrito = adapter.getEscrito();

                String[] estado_cuota = adapter.getEstado_cuota();


                //double monto_total_recibo=Double.valueOf(parametro_total_recibo).doubleValue();
                String resultado2 = "";

                ///////////////////////////////BUSCAMOS SI YA EXISTE UN PAGO ANTERIOR DE LA CUOTA
                try {
                    //importamos la clase BB_DD_HELPER
                    final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());
                    SQLiteDatabase db = helper.getReadableDatabase();

                    String[] projection = {
                            //que columnas queremos que saque nuestra consulta
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA1,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA2,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA3,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA4,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA5,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA6,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA7,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA8,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA9,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA10

                    };

                    //COLUMNA QUE COMPARAREMOS (WHERE)
                    String selection = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA8 + " = ? ";
                    ////////String[] selectionArgs = { textoId.getText().toString() };
                    String[] selectionArgs = {parametro_id_recibo.toString()};

                    Cursor c = db.query(
                            Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    //se mueve el cursor al primer registro

                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            //Toast.makeText(getApplicationContext(), "...."+c.getString(5).toString(), Toast.LENGTH_SHORT).show();
                            resultado += Double.parseDouble(c.getString(5).toString());
                            resultado2 = resultado2 + c.getString(5).toString();
                        }
                    }

                    //String numero_id_poliza_amortizacion = c.getString(1).toString();
                } catch (Exception ex2) {
                    String mensaje2 = "ERROR 12: " + ex2;
                    //Toast.makeText(view.getContext(), mensaje2, Toast.LENGTH_SHORT).show();
                }

                //saldo_recibo=Double.valueOf(parametro_total_recibo).doubleValue()-resultado;
                saldo_recibo=resultado;

                if(resultado<=0) {
                    tv_consumo_recibo = (TextView) findViewById(R.id.tv_consumo_recibo);
                    tv_consumo_recibo.setText(String.valueOf(resultado));
                    //MOSTRAMOS EL CONSUMO DEL RECIBO
                    tv_saldo_recibo = (TextView) findViewById(R.id.tv_saldo_recibo);
                    //"%.2f" hace que se redonde a 2 decimales
                    tv_saldo_recibo.setText(String.format("%.2f", saldo_recibo));
                    btn_guardar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "El consumo no puede ser menor o igual a 0, consumo: " + resultado, Toast.LENGTH_SHORT).show();
                }else{
                    btn_guardar.setVisibility(View.VISIBLE);
                    //MOSTRAMOS EL CONSUMO DEL RECIBO
                    tv_consumo_recibo = (TextView) findViewById(R.id.tv_consumo_recibo);
                    tv_consumo_recibo.setText(String.valueOf(resultado));
                    //MOSTRAMOS EL CONSUMO DEL RECIBO
                    tv_saldo_recibo = (TextView) findViewById(R.id.tv_saldo_recibo);
                    //"%.2f" hace que se redonde a 2 decimales
                    tv_saldo_recibo.setText(String.format("%.2f", saldo_recibo));
                }
            }
        });


        //ponemos a la escucha
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultado=0;
                saldo_recibo=0;

                String[] escrito = adapter.getEscrito();

                String[] estado_cuota = adapter.getEstado_cuota();


                //double monto_total_recibo=Double.valueOf(parametro_total_recibo).doubleValue();
                String resultado2 = "";

                ///////////////////////////////BUSCAMOS SI YA EXISTE UN PAGO ANTERIOR DE LA CUOTA
                try {
                    //importamos la clase BB_DD_HELPER
                    final BBDD_Helper helper = new BBDD_Helper(getApplicationContext());
                    SQLiteDatabase db = helper.getReadableDatabase();

                    String[] projection = {
                            //que columnas queremos que saque nuestra consulta
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA1,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA2,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA3,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA4,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA5,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA6,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA7,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA8,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA9,
                            Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA10

                    };

                    //COLUMNA QUE COMPARAREMOS (WHERE)
                    String selection = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA8 + " = ? ";
                    ////////String[] selectionArgs = { textoId.getText().toString() };
                    String[] selectionArgs = {parametro_id_recibo.toString()};

                    Cursor c = db.query(
                            Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );

                    //se mueve el cursor al primer registro

                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            //Toast.makeText(getApplicationContext(), "...."+c.getString(5).toString(), Toast.LENGTH_SHORT).show();
                            resultado += Double.parseDouble(c.getString(5).toString());
                            resultado2 = resultado2 + c.getString(5).toString();
                        }
                    }

                    //String numero_id_poliza_amortizacion = c.getString(1).toString();
                } catch (Exception ex2) {
                    String mensaje2 = "ERROR 12: " + ex2;
                    //Toast.makeText(view.getContext(), mensaje2, Toast.LENGTH_SHORT).show();
                }

                //saldo_recibo=Double.valueOf(parametro_total_recibo).doubleValue()-resultado;
                saldo_recibo=resultado;

                if(resultado<=0) {
                    tv_consumo_recibo = (TextView) findViewById(R.id.tv_consumo_recibo);
                    tv_consumo_recibo.setText(String.valueOf(resultado));
                    //MOSTRAMOS EL CONSUMO DEL RECIBO
                    tv_saldo_recibo = (TextView) findViewById(R.id.tv_saldo_recibo);
                    //"%.2f" hace que se redonde a 2 decimales
                    tv_saldo_recibo.setText(String.format("%.2f", saldo_recibo));
                    btn_guardar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "El consumo no puede ser menor o igual a 0, consumo: " + resultado, Toast.LENGTH_SHORT).show();
                }else{
                    listado_pre_polizas_clientes.AntesSegundoPlano tarea_antes = new listado_pre_polizas_clientes.AntesSegundoPlano();
                    tarea_antes.execute();
                }

            }
        });



    }


    //para el metodo login del webservice
    class AntesSegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
        }

        @Override
        protected Void doInBackground(Void... params){
            //guarda_pago_recibo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();

            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), emision_recibo.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad

            i.putExtra("parametro_id_cliente", parametro_id_cliente);
            i.putExtra("parametro_nombre_cliente", parametro_nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", parametro_apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", parametro_apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", parametro_ci_nit_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente",parametro_email_cliente);
            i.putExtra("parametro_numero_liquidacion",parametro_numero_liquidacion);

            i.putExtra("parametro_tipo_actividad", "Nuevo");
            i.putExtra("parametro_id_sucursal", parametro_id_sucursal.toString());
            i.putExtra("parametro_id_empleado", parametro_id_empleado.toString());
            i.putExtra("parametro_total_a_pagar", String.valueOf(resultado));

            startActivity(i);
            finish();

        }
    }


    //para el metodo login del webservice
    class TareaDatosCliente extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
        }

        @Override
        protected Void doInBackground(Void... params){
            datos_cliente();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            tv_nombre_cliente=(TextView) findViewById(R.id.tv_nombre_cliente);
            tv_nombre_cliente.setText(parametro_nombre_cliente);
            //Toast.makeText(getApplicationContext(), "Cliente: "+mensaje2+parametro_nombre_cliente , Toast.LENGTH_SHORT).show();

        }
    }

    //funcion nos retorna datos del cliente ws
    private void datos_cliente(){
        String SOAP_ACTION = "http://ws.sudseguros.com/SearchOneCustomer";
        String METHOD_NAME = "SearchOneCustomer";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("numero_liquidacion", parametro_numero_liquidacion.toString());
            Request.addProperty("imei_dispositivo", "357136081571342" );
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");


            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            resultString = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo

            if(resultString.getProperty(0).toString().equals("1")) {
                //GUARDAMOS VARIABLES
                parametro_id_cliente = resultString.getProperty(2).toString();
                parametro_nombre_cliente = resultString.getProperty(4).toString();
                parametro_apellido_paterno_cliente = resultString.getProperty(5).toString();
                parametro_apellido_materno_cliente = resultString.getProperty(6).toString();
                parametro_ci_nit_cliente = resultString.getProperty(3).toString();
                parametro_tipo_cliente = resultString.getProperty(7).toString();
                parametro_email_cliente = resultString.getProperty(8).toString();

                mensaje2 = "Cliente encontrado ";
                encontrado="SI";
            }else {
                mensaje2 = "No se encontro cliente ";
                encontrado="NO";
            }


        }
        catch (Exception ex) {
            mensaje2 = "ERROR 1: " + ex.getMessage();
        }
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
            //muestra texto mientras carga
            //ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            //textcargando.setVisibility(View.VISIBLE);

            dialog = new ProgressDialog(listado_pre_polizas_clientes.this);
            dialog.setMessage("Buscando pólizas...");
            dialog.setCancelable (false);
            dialog.show();

            LinearLayout contenedor = (LinearLayout) findViewById(R.id.ly_contenedor);
            contenedor.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params){

            convertir();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR
            //ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarCargando);
            //textcargando.setVisibility(View.GONE);
            dialog.hide();
            LinearLayout contenedor = (LinearLayout) findViewById(R.id.ly_contenedor);
            contenedor.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), mensaje , Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Cuotas para Amortizar Encontradas." , Toast.LENGTH_SHORT).show();
            //Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();

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


            //String resultado = "";
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

                data3.add(new CuotasPreCliente(ClientesArray[i][0],  ClientesArray[i][3], R.mipmap.ic_launcher_round, ClientesArray[i][17], ClientesArray[i][15], ClientesArray[i][3], ClientesArray[i][2], parametro_id_cliente, parametro_id_recibo, ClientesArray[i][1], ClientesArray[i][16].replace(" ",""),  ClientesArray[i][5],  ClientesArray[i][18],  ClientesArray[i][19],  ClientesArray[i][20], parametro_id_sucursal,   ClientesArray[i][9], parametro_id_empleado, ClientesArray[i][21]));

                mensaje=ClientesArray[i][17];
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
            data3.add(new CuotasPreCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs.", "Cuota: 1 (100 Bs)","id_cliente","id_recibo","id_poliza_movimiento", "ramo", "monto_pagado", "resta", "sucursal", "id_sucursal", parametro_id_sucursal, "fecha_cuota", "440", "no_liquidacion"));

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

    //SE HABILITA ESTA OPCION PARA BUSCAR POR NOMBRE APTERNO O APMATERNO
    private void eliminar_primer_elemento_rv(){

        //data3.remove(0);
        //actualizamos vista de la lista
        //adapter.notifyDataSetChanged();
        //Toast.makeText(getApplicationContext(), "lista", Toast.LENGTH_LONG).show();
        //Aquí muestras el mensaje
        // data3.add(new CuotasPreCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));
        //data3.add(new CuotasPreCliente(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs.", "Cuota: 1 (100 Bs)","id_cliente","id_recibo","id_poliza_movimiento", "ramo", "monto_pagado", "resta", "sucursal", "id_sucursal", parametro_id_sucursal, "fecha_cuota", "440", "no_liquidacion"));

        if(rvMusicas.getAdapter() != null){
            //De esta manera sabes si tu RecyclerView está vacío
            if(rvMusicas.getAdapter().getItemCount() == 0) {
                Toast.makeText(getApplicationContext(), "No existen cuotas pendientes a cancelar por el cliente.", Toast.LENGTH_LONG).show();
                //Aquí muestras el mensaje
            }
        }
    }


    private ArrayList<CuotasPreCliente> dataSet() {

        ArrayList<CuotasPreCliente> data = data3;

        return data;
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
        listado_pre_polizas_clientes.TareaEliminarPreCuotas tarea3 = new listado_pre_polizas_clientes.TareaEliminarPreCuotas();
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
            RequestWs.addProperty("id_empleado", parametro_id_empleado);
            RequestWs.addProperty("id_sucursal", parametro_id_sucursal);
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
}
