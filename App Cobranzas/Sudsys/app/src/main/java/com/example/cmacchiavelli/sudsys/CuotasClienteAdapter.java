package com.example.cmacchiavelli.sudsys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CuotasClienteAdapter extends RecyclerView.Adapter<CuotasClienteAdapter.CuotasViewHolder>{

    private ArrayList<CuotasCliente> data;
    private Context context;
    private String[] mList;


    String[] escrito;
    String[] estado_cuota;

    TextView total_recibo;

    ProgressDialog dialog;

    //VARIABLES A GUARDAR EN LA BD
    String id_poliza_amortizacion;
    String no_poliza;
    String no_liquidacion;
    String no_cuota;
    String id_cliente;
    String id_recibo;
    String id_poliza_movimiento;
    String ramo;
    String no_poliza_aux="";
    String no_liquidacion_aux="";

    private SwipeRefreshLayout swipeContainer;

    String numero_id_poliza_amortizacion="";
    //Button btnGuardar;

    private boolean mHorizontal;

    int tamano_vector=0;

    //para poder utilizar otros elementos de layouts que no estan en item_cuotas_cliente
    View vista_otra_actividad;

    public CuotasClienteAdapter(ArrayList<CuotasCliente> data) {
        this.data = data;
        this.context = context;


    }

    @Override
    public CuotasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //para poder utilizar otros elementos de layouts que no estan en item_cuotas_cliente
        vista_otra_actividad = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listado_cuotas_polizas, parent, false);
        escrito = new String[data.size()];
        estado_cuota = new String[data.size()];
        tamano_vector = data.size();
        //Iniciamos los valores de las cuotas en 0
        //for (int i = 0; i < data.size(); i++) {
          //  escrito[i] = "";
          //  estado_cuota[i] = "";
        //}

        return new CuotasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuotas_cliente, parent, false), vista_otra_actividad);
    }

    @Override
    public void onBindViewHolder(CuotasViewHolder holder, int position) {

        final CuotasCliente musica = data.get(position);




            if(no_liquidacion_aux.equals(musica.getNoLiquidacion().toString())){
                holder.imgMusica.setVisibility(View.GONE);
                holder.tvNombre.setVisibility(View.GONE);
                holder.tvArtista.setVisibility(View.GONE);
                holder.tvCia.setVisibility(View.GONE);
                holder.tvNoPoliza.setVisibility(View.GONE);
                holder.tvNoLiquidacion.setVisibility(View.GONE);
                holder.tvRamo.setVisibility(View.GONE);
                holder.tvSucursal.setVisibility(View.GONE);
                holder.tvIdRecibo.setVisibility(View.GONE);
                holder.tvIdPolizaMovimiento.setVisibility(View.GONE);
            }else{
                holder.imgMusica.setVisibility(View.VISIBLE);
                holder.tvNombre.setVisibility(View.GONE);
                holder.tvArtista.setVisibility(View.GONE);
                holder.tvCia.setVisibility(View.VISIBLE);
                holder.tvNoPoliza.setVisibility(View.VISIBLE);
                holder.tvNoLiquidacion.setVisibility(View.VISIBLE);
                holder.tvRamo.setVisibility(View.VISIBLE);
                holder.tvSucursal.setVisibility(View.VISIBLE);
                holder.tvIdRecibo.setVisibility(View.GONE);
                holder.tvIdPolizaMovimiento.setVisibility(View.GONE);

                no_poliza_aux=musica.getNoPoliza();
            }


            //CABECERAS
                holder.imgMusica.setImageResource(musica.getImagen());
                holder.tvNombre.setText(musica.getNombre());
                holder.tvArtista.setText(musica.getArtista());
                holder.tvCia.setText(musica.getCia());
                holder.tvNoPoliza.setText(musica.getNoPoliza());
                holder.tvNoLiquidacion.setText(musica.getNoLiquidacion());
                no_poliza_aux=musica.getNoPoliza();
                no_liquidacion_aux=musica.getNoLiquidacion();
                holder.tvRamo.setText(musica.getRamo());
                holder.tvSucursal.setText(musica.getSucursal());

            //monto pagado total ojo
            //holder.tvMonto.setText(musica.getMonto());
            //double saldo_a_mostrar = Double.valueOf(musica.getMonto().toString()).doubleValue() - Double.valueOf(musica.getMontoPagado().toString()).doubleValue();
            //MOSTRAMOS EL SALDO DE LA CUOTA


            holder.tvMonto.setText(musica.getSaldoCuota());
            holder.tvMontoAux.setText(musica.getSaldoCuota());

            holder.tvMontoPagado.setText(musica.getMontoPagado());
            holder.tvMontoTotal.setText(musica.getMonto());
            holder.tvNoCuota.setText(musica.getNoCuota());

            holder.tvIdCliente.setText(musica.getIdCliente());
            holder.tvIdRecibo.setText(musica.getIdRecibo());
            holder.tvIdPolizaMovimiento.setText(musica.getIdPolizaMovimiento());

            holder.tvIdSucursal.setText(musica.getIdSucursal());
            holder.tvIdSucursalUsuario.setText(musica.getIdSucursalUsuario());
            //iniciamos los valores donde se utiliza el evento change de los EditText
            holder.etCantidad.setText(musica.getSaldoCuota());
            holder.tvEditable.setText("1");

            String hallostring = musica.getFechaCuota();
            String asubstring = hallostring.substring(0, 10);
            holder.tvFechaCuota.setText(asubstring);

        for (int i = 0; i < tamano_vector; i++) {
              escrito[i] = musica.getSaldoCuota();
              estado_cuota[i] = "1";
            }

            //escrito[0]="2";
            //escrito[1]="5";
            //


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public String[] getEscrito() {
        return escrito;
    }

    public String[] getEstado_cuota() {
        return estado_cuota;
    }

    class CuotasViewHolder extends RecyclerView.ViewHolder{

        ImageView imgMusica;
        TextView tvNombre;
        TextView tvArtista;
        TextView tvNoPoliza;
        TextView tvNoLiquidacion;
        TextView tvCia;
        TextView tvMonto;
        TextView tvMontoAux;
        TextView tvMontoPagado;
        TextView tvMontoTotal;
        EditText etCantidad;
        TextView tvNoCuota;

        TextView tvIdCliente;
        TextView tvIdRecibo;
        TextView tvIdPolizaMovimiento;

        TextView tvRamo;
        TextView tvSucursal;
        TextView tvIdSucursal;
        TextView tvIdSucursalUsuario;
        TextView tvFechaCuota;

        Button pagar_cuota;
        Button btnGuardar;

        LinearLayout bloquea;
        ImageView img_imprimir;
        EditText tvEditable;

        public CuotasViewHolder(final View itemView, final View vista_otra_actividad2) {
            super(itemView);
            context = itemView.getContext();
            imgMusica = (ImageView) itemView.findViewById(R.id.img_musica);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
            tvArtista = (TextView) itemView.findViewById(R.id.tv_artista);
            tvNoPoliza = (TextView) itemView.findViewById(R.id.tv_no_poliza);
            tvNoLiquidacion = (TextView) itemView.findViewById(R.id.tv_no_liquidacion);
            tvCia = (TextView) itemView.findViewById(R.id.tv_cia);
            tvMonto = (TextView) itemView.findViewById(R.id.tv_monto);
            tvMontoAux = (TextView) itemView.findViewById(R.id.tv_monto_aux);
            tvMontoPagado = (TextView) itemView.findViewById(R.id.tv_monto_pagado);
            tvMontoTotal = (TextView) itemView.findViewById(R.id.tv_monto_total);
            //pagar_cuota=(Button) itemView.findViewById(R.id.pagar_cuota);
            etCantidad = (EditText) itemView.findViewById(R.id.monto_a_pagar);
            tvNoCuota = (TextView) itemView.findViewById(R.id.tv_no_cuota);
            tvIdCliente = (TextView) itemView.findViewById(R.id.tv_id_cliente);
            tvIdRecibo = (TextView) itemView.findViewById(R.id.tv_id_recibo);
            tvIdPolizaMovimiento = (TextView) itemView.findViewById(R.id.tv_id_poliza_movimiento);
            tvRamo = (TextView) itemView.findViewById(R.id.tv_ramo);
            tvSucursal = (TextView) itemView.findViewById(R.id.tv_sucursal);
            tvIdSucursal = (TextView) itemView.findViewById(R.id.tv_id_sucursal);
            tvIdSucursalUsuario = (TextView) itemView.findViewById(R.id.tv_id_sucursal_usuario);
            tvFechaCuota = (TextView) itemView.findViewById(R.id.tv_fecha_cuota);
            bloquea =(LinearLayout) itemView.findViewById(R.id.bloquea);
            img_imprimir = (ImageView) itemView.findViewById(R.id.img_imprimir);
            tvEditable = (EditText) itemView.findViewById(R.id.tv_editable);
            btnGuardar = (Button) vista_otra_actividad2.findViewById(R.id.btn_paga);

            //Toast.makeText(itemView.getContext(), "value: " + btnGuardar.toString(), Toast.LENGTH_LONG).show();
            ///btnGuardar.setVisibility(itemView.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(final View view) {
                    abrirDialogoAmortizar();
                }

            });


        }
    }


    public void abrirDialogo(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
        dialogo1.setTitle("Aviso");
        dialogo1.setMessage("Para editar el pago haz click nuevamente en el elemento.");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //Toast.makeText(context.getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            }
        });
        //dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
         //  public void onClick(DialogInterface dialogo1, int id) {
         //      Toast.makeText(context.getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
         // }
         //});
        dialogo1.show();
    }

    public void abrirDialogoAmortizar(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
        dialogo1.setTitle("Aviso");
        dialogo1.setMessage("Para amortizar la cuota debe ir a la opcion 'crear un recibo', gracias.");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //Toast.makeText(context.getApplicationContext(), "ok", Toast.LENGTH_LONG).show();
            }
        });
        //dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        //  public void onClick(DialogInterface dialogo1, int id) {
        //      Toast.makeText(context.getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
        // }
        //});
        dialogo1.show();
    }


}
