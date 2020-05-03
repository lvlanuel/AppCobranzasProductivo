package com.example.cmacchiavelli.sudsys;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class reportes_recibos extends AppCompatActivity {


    //incluimos el tool_bar
    private Toolbar toolbar;

    //definimos el txt que estara a la escucha, para almacenar en base de datos
    EditText txt_numero_recibo, txt_desde_oculto, txt_hasta_oculto;
    CalendarView txt_desde, txt_hasta;

    //variables globales
    String id_sucursal, id_empleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_recibos);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Reportes de Recibos");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

        Bundle datos=getIntent().getExtras();
        id_empleado=datos.getString("parametro_id_empleado");
        id_sucursal=datos.getString("parametro_id_sucursal");


        txt_desde = (CalendarView)findViewById(R.id.desde);
        //fecha desde
        txt_desde.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                Toast.makeText(getApplicationContext(), "" + dayOfMonth +"/" + (month+1) + "/" + year , Toast.LENGTH_LONG).show();
                txt_desde_oculto = (EditText) findViewById(R.id.tdesde);
                txt_desde_oculto.setText("" + dayOfMonth +"/" + (month+1) + "/" + year);

            }
        });


        txt_hasta = (CalendarView)findViewById(R.id.hasta);
        //fecha
        txt_hasta.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                Toast.makeText(getApplicationContext(), "" + dayOfMonth +"/" + (month+1) + "/" + year , Toast.LENGTH_LONG).show();
                txt_hasta_oculto = (EditText) findViewById(R.id.thasta);
                txt_hasta_oculto.setText("" + dayOfMonth +"/" + (month+1) + "/" + year);

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


    //llamamos a la actividad lista_recibos
    public void lista_recibos(View view){
        txt_numero_recibo=(EditText)findViewById(R.id.numero_recibo);
        txt_desde_oculto=(EditText)findViewById(R.id.tdesde);
        txt_hasta_oculto=(EditText)findViewById(R.id.thasta);

       // long selectedDateDesde = txt_desde.getDate();
       // long selectedDateHasta = txt_hasta.getDate();
        //Intent es como evento, debe mejecutar el java infoClase
        Intent i=new Intent(this, reportes_recibos_listar.class);
        //guardamos  para recuperarlo en otra actividad
        i.putExtra("parametro_id_sucursal", id_sucursal.toString());
        i.putExtra("parametro_id_empleado", id_empleado.toString());

        i.putExtra("parametro_numero_recibo", txt_numero_recibo.getText().toString());
        i.putExtra("parametro_desde", txt_desde_oculto.getText().toString());
        i.putExtra("parametro_hasta", txt_hasta_oculto.getText().toString());
        startActivity(i);

    }

    public void mostrar_calendario_desde(View view){
        //Intent es como evento, debe mejecutar el java infoClase
        //TEXTO DESAPARECE AL CARGAR
        Button textcargando = (Button) findViewById(R.id.desde);
        textcargando.setVisibility(View.VISIBLE);
    }

}
