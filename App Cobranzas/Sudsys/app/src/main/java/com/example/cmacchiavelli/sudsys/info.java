package com.example.cmacchiavelli.sudsys;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

//Poner la linea extends Activity, significa que herede de actividad
public class info extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //info es el nombre del xml activity
        setContentView(R.layout.activity_info);
    }

}