package com.example.cmacchiavelli.sudsys;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class listado_cuotas_polizas_imprimir extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    //incluimos el tool_bar
    private Toolbar toolbar;

    //lectura ws clientes objetos dentro de objeto
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    private RecyclerView rvMusicas;
    private GridLayoutManager glm;
    private CuotasImprimirAdapter adapter;
    String usuario, parametro_numero_liquidacion;
    String mensaje, mensaje2, parametro_id_sucursal, parametro_id_empleado, parametro_id_cliente, parametro_id_recibo, parametro_id_recibo_sudsys,parametro_numero_recibo_sudsys,  parametro_total_recibo, consumo_recibo;;

    ArrayList<CuotasImprimir> data2 = new ArrayList<>();

    TextView tv_id_recibo_sudsys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_cuotas_polizas_imprimir);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Listado Cuotas Amortizadas");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

        //parametro_id_recibo_sudsys="222";
        Bundle datos=getIntent().getExtras();
        parametro_id_recibo_sudsys=datos.getString("parametro_id_recibo");
        parametro_id_recibo=datos.getString("parametro_id_recibo");
        parametro_numero_recibo_sudsys=datos.getString("parametro_numero_recibo");


        parametro_id_empleado=datos.getString("parametro_id_empleado");
        parametro_id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");

        tv_id_recibo_sudsys=(TextView) findViewById(R.id.tv_id_recibo_sudsys);
        tv_id_recibo_sudsys.setText(parametro_id_recibo);



        //llamamos a la tarea en segundo plano
        listado_cuotas_polizas_imprimir.SegundoPlano tarea = new listado_cuotas_polizas_imprimir.SegundoPlano();
        tarea.execute();

        rvMusicas = (RecyclerView) findViewById(R.id.rv_musicas);

        rvMusicas.setHasFixedSize(true);
        rvMusicas.setItemViewCacheSize(200);
        rvMusicas.setDrawingCacheEnabled(true);
        rvMusicas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        glm = new GridLayoutManager(this, 1);
        rvMusicas.setLayoutManager(glm);
        adapter = new CuotasImprimirAdapter(dataSet());
        rvMusicas.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        listado_cuotas_polizas_imprimir.TercerPlano tarea2 = new listado_cuotas_polizas_imprimir.TercerPlano();
        tarea2.execute();

        //actulaizamos al hacer Swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //llamamos a la tarea en segundo plano
                listado_cuotas_polizas_imprimir.SegundoPlano tarea = new listado_cuotas_polizas_imprimir.SegundoPlano();
                tarea.execute();
                swipeRefreshLayout.setRefreshing(false);

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
            IrPantallaPrincipal();
            finish();
            return true;
        }
        if(id==R.id.cerrar_sesion){
                    Toast.makeText(getApplicationContext(), "Cerrar sesion desde Pantalla Principal.", Toast.LENGTH_LONG).show();
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

    private ArrayList<CuotasImprimir> dataSet() {

        ArrayList<CuotasImprimir> data = data2;

        return data;
    }

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

            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Amortizaciones Encontradas..." +mensaje + "sss"+parametro_id_recibo, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Amortizaciones Encontradas.", Toast.LENGTH_LONG).show();


        }
    }

    //llamamos al metodo login del webservice
    private void convertir(){
        //WEB SERVICE CUPTAS POLIZAS CLIENTE
        String SOAP_ACTION = "http://ws.sudseguros.com/AmortizacionesPagadas";
        String METHOD_NAME = "AmortizacionesPagadas";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";



        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo", parametro_id_recibo_sudsys);
            Request.addProperty("numero_recibo", parametro_numero_recibo_sudsys);
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
            int k2=0;



            for (k2 = 0; k2< SoapArrayNivel2.getPropertyCount(); k2++)
            {

            }
            int numero_columnas = 15;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];

            for (int i = 0; i< SoapArrayNivel2.getPropertyCount(); i++)
            {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);

                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k=j;
                }
                if (Double.valueOf(ClientesArray[i][3]).doubleValue() > 0) {
                    data2.add(new CuotasImprimir(ClientesArray[i][0], ClientesArray[i][1], R.mipmap.ic_launcher_round, ClientesArray[i][10], ClientesArray[i][4], ClientesArray[i][3], ClientesArray[i][2], ClientesArray[i][6], ClientesArray[i][5], ClientesArray[i][11], ClientesArray[i][12], ClientesArray[i][13], ClientesArray[i][14], ClientesArray[i][9]));
                    mensaje = ClientesArray[i][1];
                }
            }

        }
        catch (Exception ex) {

            mensaje = "ERROR 11: " + ex.getMessage();
            // data2.add(new Cuotas(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        }
    }


    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            data2.add(new CuotasImprimir(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200", "Cuota: 1 (100 Bs)","id_cliente","id_recibo","cliente", "aseguradora", "no_recibo", "riesgo", "no_liquidacion"));

        }

        @Override
        protected Void doInBackground(Void... params){

            eliminar_primer_elemento_rv();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            ///ELIMINA EL PRIMER ELEMNTO DE LA LISTA
            //Toast.makeText(getApplicationContext(), "Eliminado primer elemento..." +parametro_id_cliente , Toast.LENGTH_LONG).show();
            data2.remove(0);
            adapter.notifyDataSetChanged();

        }
    }

    private void eliminar_primer_elemento_rv(){
        //data2.remove(0);
        //actualizamos vista de la lista
        //adapter.notifyDataSetChanged();





        // data2.add(new Cuotas(mensaje, "Cuota: 1 (100 Bs)", R.mipmap.ic_launcher_round, "No. Póliza: 570", "Compañia: Alianza", "200 Bs."));

        if(rvMusicas.getAdapter() != null){
            //De esta manera sabes si tu RecyclerView está vacío
            if(rvMusicas.getAdapter().getItemCount() == 0) {
                Toast.makeText(getApplicationContext(), "No existen cuotas pendientes a cancelar por el cliente...", Toast.LENGTH_LONG).show();
                //Aquí muestras el mensaje
            }
        }

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

    public void IrPantallaPrincipal() {
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
        String selection = Estructura_BBDD.NOMBRE_COLUMNA1 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { parametro_id_empleado };

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

            c.moveToFirst();

            //almacenamos tb id_sucursal, id_empleado para pasar a la otra actividad
            usuario=c.getString(1);



        }catch (Exception e){
//                Toast.makeText(getApplicationContext(), "no se encontro registro", Toast.LENGTH_LONG).show();
        }

        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(this, pantalla_principal.class);
        //guardamos el parametro usuario para recuperarlo en otra actividad
        i.putExtra("parametro_usuario", usuario.toString());
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "No se puede volver atras",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }
}
