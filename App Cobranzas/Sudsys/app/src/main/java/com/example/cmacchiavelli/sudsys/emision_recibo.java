package com.example.cmacchiavelli.sudsys;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class emision_recibo extends AppCompatActivity {


    //incluimos el tool_bar
    private Toolbar toolbar;
    //para el webservice
    SoapObject resultString;
    //definimos el txt que estara a la escucha, para almacenar en base de datos
    EditText textoConcepto, textoNumeroCheque, textoMonto;
    Spinner spinner, spinner2, spinner3, spinner_sucursal;
    String spbanco, spformaPago, spmoneda, spsucursal;
    TextView ci_cliente;
    //variables globales
    String exito, mensaje2, parametro_tipo_actividad, parametro_id_cliente,parametro_nombre_cliente,parametro_apellido_paterno_cliente,parametro_apellido_materno_cliente,parametro_ci_nit_cliente, parametro_tipo_cliente;

    //variables globales
    String id_sucursal, id_empleado, id_llave_unica_recibo, parametro_numero_liquidacion;

    //variables para la edicion
    String parametro_id_recibo, parametro_concepto, parametro_monto, parametro_numero_cheque, parametro_forma_pago, parametro_moneda, parametro_banco, parametro_recibo_sucusal, parametro_email_cliente, parametro_total_a_pagar, contenedor_combo_bancos;

    List<String> list_bancos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision_recibo);


        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Guardar Recibo");
        //ponemos las opciones del menu
        ///setSupportActionBar(toolbar);

        //recuperamos variables de la interfaz
        textoConcepto=(EditText)findViewById(R.id.concepto);
        textoNumeroCheque=(EditText)findViewById(R.id.numero_cheque);
        textoMonto=(EditText)findViewById(R.id.monto);


        //recibimos variables
        Bundle datos=getIntent().getExtras();
        parametro_tipo_actividad=datos.getString("parametro_tipo_actividad");
        parametro_id_cliente=datos.getString("parametro_id_cliente");
        parametro_nombre_cliente=datos.getString("parametro_nombre_cliente");
        parametro_apellido_paterno_cliente=datos.getString("parametro_apellido_paterno_cliente");
        parametro_apellido_materno_cliente=datos.getString("parametro_apellido_materno_cliente");
        parametro_ci_nit_cliente=datos.getString("parametro_ci_nit_cliente");
        parametro_tipo_cliente=datos.getString("parametro_tipo_cliente");
        parametro_email_cliente=datos.getString("parametro_email_cliente");
        parametro_total_a_pagar=datos.getString("parametro_total_a_pagar");
        parametro_numero_liquidacion=datos.getString("parametro_numero_liquidacion");


        //recuperamos la variable ci_cliente
        ci_cliente=(TextView)findViewById(R.id.textView_ci);

        //ponemos las variables recibidas de la otra actividad en los campos de texto

        TextView txtCambiado = (TextView)findViewById(R.id.nombre_cliente);
        txtCambiado.setText(parametro_nombre_cliente+" "+parametro_apellido_paterno_cliente+" "+parametro_apellido_materno_cliente);
        TextView txtCambiado2 = (TextView)findViewById(R.id.textView_ci);
        txtCambiado2.setText(parametro_ci_nit_cliente);
        TextView txtCambiado3 = (TextView)findViewById(R.id.textView_nombre);
        txtCambiado3.setText(parametro_nombre_cliente+" "+parametro_apellido_paterno_cliente+" "+parametro_apellido_materno_cliente);
        EditText txtCambiadoEmail = (EditText)findViewById(R.id.email_cliente);
        txtCambiadoEmail.setText(parametro_email_cliente);


        //obtenemos la fecha actual
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date2 = new Date();
        String fecha2 = dateFormat2.format(date2);

        TextView txtCambiado4 = (TextView)findViewById(R.id.fecha);
        txtCambiado4.setText(fecha2.toString());

        //combobox tipo_pago
        spinner = (Spinner) findViewById(R.id.tipo_pago);
        String[] letra = {"Contado","Cheque"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));


        //combobox banco con bd

        try {
            //importamos la clase BB_DD_HELPER
            final BBDD_Helper helper = new BBDD_Helper(this);


            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {
                    //que columnas queremos que saque nuestra consulta
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA1,//1 id
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA2,//2
                    Estructura_BBDD_Bancos.NOMBRE_COLUMNA3,//3

            };

            //COLUMNA QUE COMPARAREMOS (WHERE)
            String selection = Estructura_BBDD_Bancos.NOMBRE_COLUMNA1 + " != ? ";

            String[] selectionArgs = { "" };


            try {

                Cursor c = db.query(
                        Estructura_BBDD_Bancos.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                // c.moveToFirst();
                if (c.getCount() > 0) {
                    list_bancos = new ArrayList<String>();
                    while (c.moveToNext()) {
                        list_bancos.add("0-SIN BANCO");
                        contenedor_combo_bancos = c.getString(1).toString()+"-"+c.getString(2).toString();
                        list_bancos.add(contenedor_combo_bancos);
                        //Toast.makeText(getApplicationContext(), c.getString(1).toString(), Toast.LENGTH_LONG).show();
                    }

                }


            }
            catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error al cargar los Bancos", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error base datos de Bancos", Toast.LENGTH_LONG).show();
        }

        spinner2 = (Spinner) findViewById(R.id.banco);
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_bancos));

        //combobox moneda
        spinner3 = (Spinner) findViewById(R.id.moneda);
        String[] letra3 = {"$us", "Bs"};
        spinner3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra3));

        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");

        //combobox recibo sucursal
        spinner_sucursal = (Spinner) findViewById(R.id.spinner_sucursal);
        if(id_sucursal.equals("1")){
            String[] letra4 = {"La Paz","Santa Cruz","Cochabamba", "Tarija"};
            //String[] letra4 = {"La Paz"};
            spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
        }else{
            if(id_sucursal.equals("2")){
                String[] letra4 = {"Santa Cruz", "La Paz", "Cochabamba", "Tarija"};
                spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
            }else {
                if(id_sucursal.equals("3")){
                    String[] letra4 = {"Cochabamba", "Santa Cruz", "La Paz", "Tarija"};
                    spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
                }else {
                    String[] letra4 = {"Tarija", "Cochabamba", "Santa Cruz", "La Paz"};
                    spinner_sucursal.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra4));
                    }
                }
        }



        //obtenemos la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        //creamos llave unica para recibos
        String[] parts = fecha.split("-");
        id_llave_unica_recibo = parts[0]+parts[1]+parts[2]+parts[3]+id_empleado+id_sucursal;

        EditText txtMonto = (EditText)findViewById(R.id.monto);
        txtMonto.setText(parametro_total_a_pagar);

        //preguntamos si es nuevo o editar
        if(parametro_tipo_actividad.equals("Editar")){
            parametro_concepto=datos.getString("parametro_concepto");
            parametro_monto=datos.getString("parametro_monto");
            parametro_numero_cheque=datos.getString("parametro_numero_cheque");
            parametro_forma_pago=datos.getString("parametro_forma_pago");
            parametro_moneda=datos.getString("parametro_moneda");
            parametro_banco=datos.getString("parametro_banco");
            parametro_recibo_sucusal=datos.getString("parametro_recibo_sucusal");
            parametro_id_recibo=datos.getString("parametro_id_recibo");
            Toast.makeText(getApplicationContext(), "Editara datos del recibo.", Toast.LENGTH_LONG).show();
            //ponemos a la caja de texto los valores recibidos
            EditText txtConcepto = (EditText)findViewById(R.id.concepto);
            txtConcepto.setText(parametro_concepto);

            txtMonto.setText(parametro_monto);

            EditText txtNumeroCheque = (EditText)findViewById(R.id.numero_cheque);
            txtNumeroCheque.setText(parametro_numero_cheque);
            //seleccionamos los spinners de acuerdo al valor recibido
            selectValue(spinner, parametro_forma_pago);
            selectValue(spinner3, parametro_moneda);
            selectValue(spinner2, parametro_banco);
            selectValue(spinner_sucursal, parametro_recibo_sucusal);
        }

        //si cambiamos valor del combo(spinner)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                //sacamos valores de los spinners
                spformaPago = spinner.getSelectedItem().toString();
                if(spformaPago.equals("Cheque")){
                    TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                    textcargando2.setVisibility(View.VISIBLE);
                    TextView textcargando3 = (TextView) findViewById(R.id.numero_cheque);
                    textcargando3.setVisibility(View.VISIBLE);

                    TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                    textcargando4.setVisibility(View.VISIBLE);
                    Spinner textcargando5 = (Spinner) findViewById(R.id.banco);
                    textcargando5.setVisibility(View.VISIBLE);
                }else{
                    TextView textcargando2 = (TextView) findViewById(R.id.text_view_cheque);
                    textcargando2.setVisibility(View.GONE);
                    EditText textcargando3 = (EditText) findViewById(R.id.numero_cheque);
                    textcargando3.setVisibility(View.GONE);

                    TextView textcargando4 = (TextView) findViewById(R.id.text_view_banco);
                    textcargando4.setVisibility(View.GONE);
                    Spinner textcargando5 = (Spinner) findViewById(R.id.banco);
                    textcargando5.setVisibility(View.GONE);
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
    public void pantalla_inicio(View view){
        super.finish();
    }


    //llamamos a la actividad pantalla_principal
    public void guardar_recibo(View view){
        if(textoConcepto.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Debe ingresar el concepto por favor.", Toast.LENGTH_LONG).show();
        }else {
            if(textoMonto.getText().toString().trim().isEmpty()){
                Toast.makeText(getApplicationContext(), "Debe ingresar el monto por favor.", Toast.LENGTH_LONG).show();
            }else {
                emision_recibo.TercerPlano tarea3 = new emision_recibo.TercerPlano();
                tarea3.execute();
            }
        }


    }






    //para el metodo SaveRecipent del webservice
    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.VISIBLE);


        }

        @Override
        protected Void doInBackground(Void... params){

            almacenar_recibo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR
            ProgressBar textcargando = (ProgressBar) findViewById(R.id.progressBarRecibo);
            textcargando.setVisibility(View.INVISIBLE);

            if(exito.toString().equals("1")){
                Toast.makeText(getApplicationContext(), mensaje2, Toast.LENGTH_LONG).show();
                ejecutar_emision_recibo_confirmar(null);
            }else {
                Toast.makeText(getApplicationContext(), mensaje2, Toast.LENGTH_LONG).show();
            }
        }

        //llamamos a la actividad pantalla_principal
        public void ejecutar_emision_recibo_confirmar(View view){
            //Intent es como evento, debe mejecutar el java infoClase
            Intent i=new Intent(getApplicationContext(), emision_recibo_confirmar.class);
            //guardamos parametros para recuperarlo en otra actividad
            i.putExtra("parametro_id_sucursal", id_sucursal.toString());
            i.putExtra("parametro_id_empleado", id_empleado.toString());
            i.putExtra("parametro_tipo_emision", "Automatica");
            //preguntamos si es nuevo o edita
            if(parametro_tipo_actividad.equals("Nuevo")) {
                i.putExtra("parametro_id_llave_unica_recibo", id_llave_unica_recibo.toString());
            }
            if(parametro_tipo_actividad.equals("Editar")){
                i.putExtra("parametro_id_llave_unica_recibo", parametro_id_recibo.toString());
            }

            i.putExtra("parametro_id_cliente", parametro_id_cliente);
            i.putExtra("parametro_nombre_cliente", parametro_nombre_cliente);
            i.putExtra("parametro_apellido_paterno_cliente", parametro_apellido_paterno_cliente);
            i.putExtra("parametro_apellido_materno_cliente", parametro_apellido_materno_cliente);
            i.putExtra("parametro_ci_nit_cliente", parametro_ci_nit_cliente);
            i.putExtra("parametro_tipo_cliente", parametro_tipo_cliente);
            i.putExtra("parametro_email_cliente", parametro_email_cliente);
            i.putExtra("parametro_numero_liquidacion", parametro_numero_liquidacion);

            startActivity(i);
            finish();
        }
    }


    private void almacenar_recibo() {

        //sacamos valores de los spinners
        spbanco = spinner2.getSelectedItem().toString();
        spformaPago = spinner.getSelectedItem().toString();
        spmoneda = spinner3.getSelectedItem().toString();
        spsucursal = spinner_sucursal.getSelectedItem().toString();
        EditText txtCambiadoEmail2 = (EditText)findViewById(R.id.email_cliente);
        parametro_email_cliente=txtCambiadoEmail2.getText().toString();
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(this);
        //preguntamos si es nuevo o editar
        if(parametro_tipo_actividad.equals("Nuevo")){
        try {
            //INSERTAMOS DATOS A LA BASE DE DATOS
            SQLiteDatabase db = helper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();


            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA2, parametro_nombre_cliente + " " + parametro_apellido_paterno_cliente + " " + parametro_apellido_materno_cliente.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA3, textoConcepto.getText().toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA4, spformaPago.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA5, spmoneda.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA6, spbanco.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA7, textoNumeroCheque.getText().toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA8, textoMonto.getText().toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA9, parametro_id_cliente.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA10, ci_cliente.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA11, id_llave_unica_recibo.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA12, spsucursal.toString());
            values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA13, parametro_email_cliente.toString());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(Estructura_BBDD_Recibos.TABLE_NAME, null, values);
            //Mensaje si guardo los datos del acceso exitosamente
            //mensaje2 = "Registro de recibo exitoso " + newRowId;
            mensaje2 = "Registro de recibo exitoso.";
            exito = "1";

        } catch (Exception ex) {
            mensaje2 = "ERROR: " + ex.getMessage();
            exito = "0";
        }
    }

        if(parametro_tipo_actividad.equals("Editar")){
            try {
                //INSERTAMOS DATOS A LA BASE DE DATOS
                SQLiteDatabase db = helper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();


                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA2, parametro_nombre_cliente + " " + parametro_apellido_paterno_cliente + " " + parametro_apellido_materno_cliente.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA3, textoConcepto.getText().toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA4, spformaPago.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA5, spmoneda.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA6, spbanco.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA7, textoNumeroCheque.getText().toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA8, textoMonto.getText().toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA9, parametro_id_cliente.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA10, ci_cliente.toString());
                //values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA11, id_llave_unica_recibo.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA12, spsucursal.toString());
                values.put(Estructura_BBDD_Recibos.NOMBRE_COLUMNA13, parametro_email_cliente.toString());

                String selection = Estructura_BBDD_Recibos.NOMBRE_COLUMNA11 + " LIKE ?";
                String[] selectionArgs = { parametro_id_recibo.toString() };

                int count = db.update(

                        Estructura_BBDD_Recibos.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                //Mensaje si guardo los datos del acceso exitosamente
                mensaje2 = "Se actualizo el registro ";
                exito = "1";

            } catch (Exception ex) {
                mensaje2 = "ERROR: " + ex.getMessage();
                exito = "0";
            }
        }
    }




    private void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
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
