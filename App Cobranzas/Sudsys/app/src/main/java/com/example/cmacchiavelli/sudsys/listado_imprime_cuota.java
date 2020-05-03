package com.example.cmacchiavelli.sudsys;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class listado_imprime_cuota extends AppCompatActivity {

    //incluimos el tool_bar
    private Toolbar toolbar;

    String parametro_id_recibo_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_imprime_cuota);

        //incluimos el tool_bar
        toolbar=(Toolbar)findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setTitle("Reportes de Recibos");
        //ponemos las opciones del menu
        setSupportActionBar(toolbar);

        //recuperamos variables
        Bundle datos=getIntent().getExtras();
        parametro_id_recibo_app=datos.getString("parametro_id_recibo_app");
        TextView txtCi = (TextView)findViewById(R.id.textView_ci);
        txtCi.setText("Id cuota pagada : "+parametro_id_recibo_app);
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
}
