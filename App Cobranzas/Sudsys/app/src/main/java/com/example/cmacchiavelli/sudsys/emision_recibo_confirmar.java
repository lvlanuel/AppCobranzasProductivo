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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class emision_recibo_confirmar extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;
    //variables globales
    String id_sucursal, id_empleado, parametro_id_llave_unica_recibo, parametro_usuario_recibido;
    //variables para pasar a la otra actividad
    String parametro_tipo_emision, parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente,parametro_ci_nit_cliente, parametro_email_cliente;
    String id_cliente,nombre_cliente, parametro_tipo_cliente, apellido_paterno_cliente,apellido_materno_cliente,ci_nit_cliente, id_recibo, el_concepto, forma_pago, banco, moneda, monto, numero_cheque, sucursal_recibo, email_cliente;
    //variables para emisiones manuales
    String ci_nit_cliente_para_manual, nombre_cliente_para_manual, mensaje_bancos, parametro_numero_liquidacion;
    String numero_poliza_pre_amortizada, numero_cuota_pre_amortizada, monto_pre_amortizada;


    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;
    LinearLayout contenedor;

    int contador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision_recibo_confirmar);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Confirmar Recibo");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);
        //recuperamos variables
        Bundle datos=getIntent().getExtras();
        parametro_id_llave_unica_recibo=datos.getString("parametro_id_llave_unica_recibo");
        TextView txtCi = (TextView)findViewById(R.id.textView_ci);
        txtCi.setText("Id recibo: "+parametro_id_llave_unica_recibo);

        //obtenemos la fecha actual
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date2 = new Date();
        String fecha2 = dateFormat2.format(date2);

        TextView txtCambiado4 = (TextView)findViewById(R.id.fecha);
        txtCambiado4.setText(fecha2.toString());

        parametro_tipo_emision=datos.getString("parametro_tipo_emision");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_ci_nit_cliente=datos.getString("parametro_ci_nit_cliente");
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        parametro_email_cliente=datos.getString("parametro_email_cliente");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");

        contenedor =(LinearLayout) findViewById(R.id.contenedor_lista);

        if(parametro_tipo_emision.equals("Manual")) {
            parametro_usuario_recibido=datos.getString("parametro_usuario_recibido");
            toolbar.setBackgroundColor(Color.parseColor("#EE3F24"));
        }

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA1,//1 id
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA2,//2 recibido_de
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA3,//3 concepto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA4,//4 tipo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA5,//5 moneda
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA6,//6 banco
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA7,//7 cheque
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA8,//8 monto
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA9,//9 id_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA10,//10 ci_cliente
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA11,//11 id_recibo
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA12,//12 id_recibo_sucursal
                Estructura_BBDD_Recibos.NOMBRE_COLUMNA13//13 email_cliente

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD_Recibos.NOMBRE_COLUMNA11 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { parametro_id_llave_unica_recibo.toString() };

             /*   String sortOrder =
                        Estructura_BBDD.NOMBRE_COLUMNA1 + " DESC";*/

        try{

            Cursor c = db.query(
                    Estructura_BBDD_Recibos.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            //se mueve el cursor al primer registro

            c.moveToFirst();

            //mostramos los valores recuperados de la base de datos de recibos

            TextView txtNombreCliente = (TextView)findViewById(R.id.nombre_cliente);
            txtNombreCliente.setText(c.getString(1));
            TextView txtConcepto = (TextView)findViewById(R.id.tvconcepto);
            txtConcepto.setText(c.getString(2));
            TextView txtTipoPago = (TextView)findViewById(R.id.txttipo_pago);
            txtTipoPago.setText(c.getString(3));
            TextView txtBanco = (TextView)findViewById(R.id.txtbanco);
            txtBanco.setText(c.getString(5));
            TextView txtCheque = (TextView)findViewById(R.id.txtnumero_cheque);
            txtCheque.setText(c.getString(6));
            TextView txtMonto = (TextView)findViewById(R.id.txtmonto);
            txtMonto.setText(c.getString(7) + " " +c.getString(4));
            TextView txtSucursalRecibo = (TextView)findViewById(R.id.txtSucursal);
            txtSucursalRecibo.setText(c.getString(11));
            TextView txtEmailCliente = (TextView)findViewById(R.id.email_cliente);
            txtEmailCliente.setText(c.getString(12));

            //otras variables agarradas de la base de datos
            id_cliente=c.getString(8);
            id_recibo= c.getString(10).toString();
            ci_nit_cliente_para_manual = c.getString(9).toString();
            nombre_cliente_para_manual = c.getString(1).toString();
            el_concepto=c.getString(2);
            forma_pago=c.getString(3);
            moneda=c.getString(4);
            banco=c.getString(5);
            numero_cheque=c.getString(6);
            monto=c.getString(7);
            //recibimos asi porque el la base de datos el nombre del cliente esta junto
            nombre_cliente = parametro_nombre_cliente;
            apellido_paterno_cliente = parametro_apellido_paterno_cliente;
            apellido_materno_cliente = parametro_apellido_materno_cliente;
            ci_nit_cliente = parametro_ci_nit_cliente;
            email_cliente = parametro_email_cliente.toString();
            sucursal_recibo = c.getString(11);


            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(numero_cheque.toString().trim().isEmpty() || numero_cheque.toString().equals("anyType{}")) {
                TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                textcargando2.setVisibility(View.GONE);
                TextView textcargando3 = (TextView) findViewById(R.id.txtnumero_cheque);
                textcargando3.setVisibility(View.GONE);

                TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                textcargando4.setVisibility(View.GONE);
                TextView textcargando5 = (TextView) findViewById(R.id.txtbanco);
                textcargando5.setVisibility(View.GONE);
            }
            //Toast.makeText(getApplicationContext(), "sssss"+parametro_email_cliente.toString(), Toast.LENGTH_LONG).show();


        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "no se encontro registro", Toast.LENGTH_LONG).show();
        }

        //llamamos a la tarea tercero plano
        emision_recibo_confirmar.TercerPlano tarea_antes = new emision_recibo_confirmar.TercerPlano();
        tarea_antes.execute();

    }

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

    public void pantalla_inicio(View view){
        finish();
    }

    public void ejecutar_emision_recibo(View view){
        if(parametro_tipo_emision.equals("Automatica")) {
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(getApplicationContext(), emision_recibo.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_nombre_cliente", nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", ci_nit_cliente);
            i.putExtra("parametro_email_cliente", email_cliente);
            i.putExtra("parametro_id_recibo", id_recibo);
            i.putExtra("parametro_concepto", el_concepto);
            i.putExtra("parametro_forma_pago", forma_pago);
            i.putExtra("parametro_moneda", moneda);
            i.putExtra("parametro_banco", banco);
            i.putExtra("parametro_numero_cheque", numero_cheque);
            i.putExtra("parametro_monto", monto);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

            i.putExtra("parametro_tipo_actividad", "Editar");
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            startActivity(i);
        }
        if(parametro_tipo_emision.equals("Manual")) {
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(getApplicationContext(), crear_recibo_manual.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_nombre_cliente", nombre_cliente_para_manual);
            i.putExtra("parametro_apellido_paterno_cliente", "");
            i.putExtra("parametro_apellido_materno_cliente", "");
            i.putExtra("parametro_ci_nit_cliente", ci_nit_cliente_para_manual);
            i.putExtra("parametro_id_recibo", id_recibo);
            i.putExtra("parametro_concepto", el_concepto);
            i.putExtra("parametro_forma_pago", forma_pago);
            i.putExtra("parametro_moneda", moneda);
            i.putExtra("parametro_banco", banco);
            i.putExtra("parametro_numero_cheque", numero_cheque);
            i.putExtra("parametro_monto", monto);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);

            i.putExtra("parametro_tipo_actividad", "Editar");
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            i.putExtra("parametro_usuario_recibido", parametro_usuario_recibido);


            startActivity(i);
        }
    }

    public void ejecutar_emision_recibos_imprimir(View view){
        if (!compruebaConexion(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            //finish();
        }
        else {
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i = new Intent(getApplicationContext(), emision_recibos_imprimir.class);
            //guardamos el parametro usuario para recuperarlo en otra actividad
            i.putExtra("parametro_id_llave_unica_recibo", parametro_id_llave_unica_recibo);
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            i.putExtra("parametro_tipo_emision", parametro_tipo_emision);
            i.putExtra("parametro_id_cliente", id_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente", parametro_email_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);
            if (parametro_tipo_emision.equals("Manual")) {
                i.putExtra("parametro_usuario_recibido", parametro_usuario_recibido);
            }

            startActivity(i);
            finish();
        }
    }

    //llamamos al metodo que lista preamortizaciones
    class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

        }

        @Override
        protected Void doInBackground(Void... params){
            lista_cuotas_pre_amortizadas();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //tv1.setText("Response: "  + mensaje);
            //Toast.makeText(getApplicationContext(), mensaje + mensaje2, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), mensaje_bancos, Toast.LENGTH_LONG).show();
        }
    }

    private void lista_cuotas_pre_amortizadas(){

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintCuotaPreAmortizada";
        String METHOD_NAME = "PrintCuotaPreAmortizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);

        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_cliente", id_cliente.toString());
            Request.addProperty("id_sucursal", id_sucursal.toString());
            Request.addProperty("id_empleado", id_empleado.toString());
            Request.addProperty("id_recibo", "0");
            Request.addProperty("tipo", "pendiente");
            Request.addProperty("imei_dispositivo", "357136081571342" );
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
            int numero_columnas = 9;
            String[][] ClientesArray = new String [SoapArrayNivel2.getPropertyCount()][numero_columnas];



            for (int i = 0; i < SoapArrayNivel2.getPropertyCount(); i++) {
                SoapArrayNivel3 = (SoapObject) SoapArrayNivel2.getProperty(i);
                final int contador = i;
                for (int j = 0; j < numero_columnas; j++) {
                    ClientesArray[i][j] = SoapArrayNivel3.getProperty(j).toString();
                    k = j;
                }

                final String numero_poliza_pre_amortizada = ClientesArray[i][1];
                final String numero_cuota_pre_amortizada = ClientesArray[i][2];
                final String monto_pre_amortizada = ClientesArray[i][4];

                //LISTAMOS CUOTAS PRE AMORTIZADAS, DIBUJANDO MANUALMENTE ELEMENTOS CON  runOnUiThread new Runnable()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView miTextView2 = new TextView(getApplicationContext());
                        miTextView2.setText("Cancelar: ");
                        miTextView2.setBackgroundColor(Color.RED);
                        miTextView2.setTextColor(Color.WHITE);
                        miTextView2.setTextSize(13);
                        miTextView2.setGravity(Gravity.CENTER);

                        //Agrega vistas al contenedor.
                        //contenedor.addView(miImageView);
                        contenedor.addView(miTextView2);

                        TextView miTextView = new TextView(getApplicationContext());
                        miTextView.setText("No. Póliza: "+numero_poliza_pre_amortizada+" Cuota: "+numero_cuota_pre_amortizada+" Pagar: "+monto_pre_amortizada.toString());
                        miTextView.setBackgroundColor(Color.BLUE);
                        miTextView.setTextColor(Color.WHITE);
                        miTextView.setTextSize(13);
                        miTextView.setGravity(Gravity.CENTER);



                        contenedor.addView(miTextView);

                    }
                });




                //Mensaje si guardo los datos del acceso exitosamente
                mensaje_bancos = "Registro de Banco Exitoso ";

            }
            mensaje_bancos = "Almacenamiento bancos exitososss" +SoapArrayNivel2.getPropertyCount();

        }
        catch (Exception ex) {
            mensaje_bancos = "ERROR: " + ex.getMessage();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "No se puede volver atras",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
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
