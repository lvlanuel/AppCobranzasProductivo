package com.example.cmacchiavelli.sudsys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class CuotasPreClienteAdapter extends RecyclerView.Adapter<CuotasPreClienteAdapter.CuotasViewHolder>{

    private ArrayList<CuotasPreCliente> data;
    private Context context;
    private String[] mList;


    String[] escrito;
    String[] estado_cuota;

    TextView total_recibo;

    ProgressDialog dialog;

    //VARIABLES A GUARDAR EN LA BD
    String id_poliza_amortizacion;
    String no_poliza;
    String no_cuota;
    String id_cliente;
    String id_recibo;
    String id_poliza_movimiento;
    String ramo;
    String no_poliza_aux="";
    String tv_id_sucursal_poliza;
    String tv_id_sucursal_usuario;
    String mensaje, id_poliza_amortizacion_recibida, mensaje_recibido, respuesta_recibido;
    String monto_total_del_recibo = "0";
    String monto_pagar = "0";
    double saldo =0;
    String id_empleado;
    String no_liquidacion;
    String no_liquidacion_aux="nada";

    //para el webservice
    SoapObject resultString;

    private SwipeRefreshLayout swipeContainer;

    String numero_id_poliza_amortizacion="";
    //Button btnGuardar;

    private boolean mHorizontal;

    int tamano_vector=0;

    //para poder utilizar otros elementos de layouts que no estan en item_cuotas
    View vista_otra_actividad;

    public CuotasPreClienteAdapter(ArrayList<CuotasPreCliente> data) {
        this.data = data;
        this.context = context;


    }

    @Override
    public CuotasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //para poder utilizar otros elementos de layouts que no estan en item_cuotas
        vista_otra_actividad = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listado_cuotas_polizas, parent, false);
        escrito = new String[data.size()];
        estado_cuota = new String[data.size()];
        tamano_vector = data.size();

        //Iniciamos los valores de las cuotas en 0
        //for (int i = 0; i < data.size(); i++) {
          //  escrito[i] = "";
          //  estado_cuota[i] = "";
        //}

        return new CuotasPreClienteAdapter.CuotasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pre_cuotas_cliente, parent, false), vista_otra_actividad);
    }

    @Override
    public void onBindViewHolder(CuotasViewHolder holder, int position) {

        final CuotasPreCliente musica = data.get(position);


        //Toast.makeText(context, "ssss", Toast.LENGTH_LONG).show();



            if (no_liquidacion_aux.equals(musica.getNoLiquidacion().toString())) {

                holder.imgMusica.setVisibility(View.GONE);
                holder.tvNombre.setVisibility(View.GONE);
                holder.tvArtista.setVisibility(View.GONE);
                holder.tvCia.setVisibility(View.GONE);
                holder.tvNoPoliza.setVisibility(View.GONE);
                holder.tvNoLiquidacion.setVisibility(View.GONE);
                holder.tvRamo.setVisibility(View.GONE);
                holder.tvSucursal.setVisibility(View.GONE);
                holder.tvIdRecibo.setVisibility(View.VISIBLE);
                holder.tvIdPolizaMovimiento.setVisibility(View.GONE);
                if(position!=0) {
                    no_liquidacion_aux = musica.getNoLiquidacion();
                }
            } else {

                holder.imgMusica.setVisibility(View.VISIBLE);
                holder.tvNombre.setVisibility(View.GONE);
                holder.tvArtista.setVisibility(View.GONE);
                holder.tvCia.setVisibility(View.VISIBLE);
                holder.tvNoPoliza.setVisibility(View.VISIBLE);
                holder.tvNoLiquidacion.setVisibility(View.VISIBLE);
                holder.tvRamo.setVisibility(View.VISIBLE);
                holder.tvSucursal.setVisibility(View.VISIBLE);
                holder.tvIdRecibo.setVisibility(View.VISIBLE);
                holder.tvIdPolizaMovimiento.setVisibility(View.GONE);

                no_poliza_aux = musica.getNoPoliza();
                if(position!=0) {
                    no_liquidacion_aux = musica.getNoLiquidacion();
                }
            }


            //CABECERAS
            holder.imgMusica.setImageResource(musica.getImagen());
            holder.tvNombre.setText(musica.getNombre());
            holder.tvArtista.setText(musica.getArtista());
            holder.tvCia.setText(musica.getCia());
            holder.tvNoPoliza.setText(musica.getNoPoliza());
            holder.tvNoLiquidacion.setText(musica.getNoLiquidacion());
            no_poliza_aux = musica.getNoPoliza();
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

            holder.tvIdEmpleado.setText(musica.getIdEmpleado());

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
        TextView tvIdEmpleado;
        TextView tvIdRecibo;
        TextView tvIdPolizaMovimiento;

        TextView tvRamo;
        TextView tvSucursal;
        TextView tvIdSucursal;
        TextView tvIdSucursalUsuario;
        TextView tvFechaCuota;

        Button pagar_cuota;
        Button btnGuardar;

        ImageView bloquea;
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
            tvIdEmpleado = (TextView) itemView.findViewById(R.id.tv_id_empleado);
            tvIdRecibo = (TextView) itemView.findViewById(R.id.tv_id_recibo);
            tvIdPolizaMovimiento = (TextView) itemView.findViewById(R.id.tv_id_poliza_movimiento);
            tvRamo = (TextView) itemView.findViewById(R.id.tv_ramo);
            tvSucursal = (TextView) itemView.findViewById(R.id.tv_sucursal);
            tvIdSucursal = (TextView) itemView.findViewById(R.id.tv_id_sucursal);
            tvIdSucursalUsuario = (TextView) itemView.findViewById(R.id.tv_id_sucursal_usuario);
            tvFechaCuota = (TextView) itemView.findViewById(R.id.tv_fecha_cuota);
            bloquea =(ImageView) itemView.findViewById(R.id.bloquea);
            img_imprimir = (ImageView) itemView.findViewById(R.id.img_imprimir);
            tvEditable = (EditText) itemView.findViewById(R.id.tv_editable);
            btnGuardar = (Button) vista_otra_actividad2.findViewById(R.id.btn_paga);

            //Toast.makeText(itemView.getContext(), "value: " + btnGuardar.toString(), Toast.LENGTH_LONG).show();
            ///btnGuardar.setVisibility(itemView.INVISIBLE);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(final View view) {
                    if (!compruebaConexion(view.getContext())) {
                        Toast.makeText(view.getContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                        //finish();
                    } else {
                    /*
                    TextView valor_monetario;
                    valor_monetario = (TextView) vista_otra_actividad2.findViewById(R.id.tv_numero_recibo_sudsys);
                    valor_monetario.setVisibility(View.GONE);
                    String monto_pagar_string2 =  valor_monetario.getText().toString();
                    Toast.makeText(vista_otra_actividad2.getContext(), "selce2:" +monto_pagar_string2, Toast.LENGTH_LONG).show();
                    valor_monetario.setText("prueba2");
                    */

                        tv_id_sucursal_poliza = tvIdSucursal.getText().toString();
                        tv_id_sucursal_usuario = tvIdSucursalUsuario.getText().toString();

                        //if (tv_id_sucursal_poliza.equals(tv_id_sucursal_usuario)) {

                        String monto_pagar_string = etCantidad.getText().toString();
                        double monto_pagar_double = Double.valueOf(monto_pagar_string).doubleValue();

                        if (monto_pagar_double <= 0) {
                            Toast.makeText(view.getContext(), "El monto a pagar debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                        } else {
                            CuotasPreCliente musica2 = data.get(getLayoutPosition());
                            //Toast.makeText(view.getContext(), "posicion:" +getLayoutPosition()+ "  "+ musica2.getArtista() + musica2.getNombre(), Toast.LENGTH_SHORT).show();
                            //elimina item en la posicion seleccionada
                            //data.remove(data.get(getLayoutPosition()));
                            //notifyItemRemoved(0);
                            //notifyItemRangeChanged(0, data.size());
                            btnGuardar.setVisibility(View.GONE);
                            //Toast.makeText(view.getContext(), "posicion: prueba:", Toast.LENGTH_SHORT).show();
                            if (tvEditable.getText().toString().equals("0")) {
                                //ocultamos layout de cancelado
                                id_poliza_amortizacion = tvNombre.getText().toString();
                                no_poliza = tvNoPoliza.getText().toString();
                                no_liquidacion = tvNoLiquidacion.getText().toString();
                                no_cuota = tvNoCuota.getText().toString();
                                id_cliente = tvIdCliente.getText().toString();
                                id_empleado = tvIdEmpleado.getText().toString();
                                id_recibo = tvIdRecibo.getText().toString();
                                id_poliza_movimiento = tvIdPolizaMovimiento.getText().toString();
                                ramo = tvRamo.getText().toString();

                                bloquea.setVisibility(View.GONE);
                                img_imprimir.setVisibility(View.VISIBLE);
                                tvEditable.setText("1");
                                etCantidad.setEnabled(false);
                                //Toast.makeText(view.getContext(), "presiono4", Toast.LENGTH_SHORT).show();

                                //ELIMINAMOS REGISTRO PARA INSERTAR NUEVO
                                //importamos la clase BB_DD_HELPER
                                final BBDD_Helper helper = new BBDD_Helper(view.getContext());
                                SQLiteDatabase db = helper.getWritableDatabase();
                                String selection = Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA2 + " LIKE ?";
                                String[] selectionArgs = {id_poliza_amortizacion.toString()};
                                db.delete(Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME, selection, selectionArgs);
                                btnGuardar.setVisibility(View.GONE);

                                tvMontoPagado.setText("0");
                                tvMonto.setText(String.valueOf(tvMontoAux.getText().toString()));
                                //Toast.makeText(view.getContext(), "Se borro el pre pago el con clave: " + id_poliza_amortizacion.toString() + "-- numero_id_poliza_amortizacion:" + numero_id_poliza_amortizacion, Toast.LENGTH_SHORT).show();


                                //llamamos al WebService  SavePreCuotaAmortiza
                                CuotasPreClienteAdapter.CuartoPlano tarea3 = new CuotasPreClienteAdapter.CuartoPlano();
                                tarea3.execute();

                            } else {

                                //calculos de cada elemento


                                tvEditable.setText("1");
                                dialog = new ProgressDialog(view.getContext());
                                dialog.setMessage("Guardando Pago...");
                                dialog.show();

                                monto_total_del_recibo = "0";
                                monto_pagar = "0";
                                String monto_recibo = "0";
                                String saldo_cuota = "0";
                                String monto_pagado = "0";

                                monto_total_del_recibo = tvArtista.getText().toString();
                                monto_pagar = etCantidad.getText().toString();
                                monto_recibo = tvMonto.getText().toString();
                                saldo_cuota = tvMontoAux.getText().toString();
                                monto_pagado = tvMontoPagado.getText().toString();

                                saldo = Double.valueOf(saldo_cuota).doubleValue() - Double.valueOf(monto_pagar).doubleValue();

                                double monto_pagado_final = Double.valueOf(monto_pagado).doubleValue() + Double.valueOf(monto_pagar).doubleValue();

                                if (Double.valueOf(monto_pagar).doubleValue() > Double.valueOf(saldo_cuota).doubleValue()) {
                                    Toast.makeText(view.getContext(), "El monto a cancelar no puede ser mayor al saldo de la cuota", Toast.LENGTH_SHORT).show();
                                } else {

                                    //MOSTRAMOS EL SALDO DE LA CUOTA
                                    tvMonto.setText(String.valueOf(saldo));
                                    tvMontoPagado.setText(String.valueOf(monto_pagado_final));
                                    //VARIABLES A GUARDAR EN LA BD
                                    id_poliza_amortizacion = tvNombre.getText().toString();
                                    no_poliza = tvNoPoliza.getText().toString();
                                    no_liquidacion = tvNoLiquidacion.getText().toString();
                                    no_cuota = tvNoCuota.getText().toString();
                                    id_cliente = tvIdCliente.getText().toString();
                                    id_empleado = tvIdEmpleado.getText().toString();
                                    id_recibo = tvIdRecibo.getText().toString();
                                    id_poliza_movimiento = tvIdPolizaMovimiento.getText().toString();
                                    ramo = tvRamo.getText().toString();


                                    ///////////////////////////////BUSCAMOS SI YA EXISTE UN PAGO ANTERIOR DE LA CUOTA
                                    try {
                                        //importamos la clase BB_DD_HELPER
                                        final BBDD_Helper helper = new BBDD_Helper(view.getContext());
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
                                        String selection = Estructura_BBDD_Cuotas_Canceladas.NOMBRE_COLUMNA2 + " = ? ";
                                        ////////String[] selectionArgs = { textoId.getText().toString() };
                                        String[] selectionArgs = {id_poliza_amortizacion.toString()};

                                        Cursor c = db.query(
                                                Estructura_BBDD_Cuotas_Canceladas.TABLE_NAME,
                                                projection,
                                                selection,
                                                selectionArgs,
                                                null,
                                                null,
                                                null
                                        );

                                        //se mueve el cursor al primer registro

                                        c.moveToFirst();

                                        numero_id_poliza_amortizacion = c.getString(1).toString();
                                        Toast.makeText(view.getContext(), "El pago ya existe en la Base de datos de la aplicacion. Id poliza amortizacion: " + numero_id_poliza_amortizacion, Toast.LENGTH_SHORT).show();
                                    } catch (Exception ex2) {
                                        String mensaje2 = "ERROR 12: " + ex2;
                                        //Toast.makeText(view.getContext(), " no se encontro registros"+mensaje2, Toast.LENGTH_SHORT).show();
                                    }

                                    //Toast.makeText(view.getContext(), "numero_id_poliza_amortizacion : " + numero_id_poliza_amortizacion, Toast.LENGTH_SHORT).show();

                                    if (Double.valueOf(monto_pagar).doubleValue() <= 0) {
                                        Toast.makeText(view.getContext(), "El monto a pagar no puede ser menor o igual a 0, monto a pagar : " + monto_pagar, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (numero_id_poliza_amortizacion.isEmpty()) {
                                            /////////////////////////////// SI NO ENCUENTRA EL ID_POLIZA_AMORTIZACION GUARDAMOS EN LA BASE DE DATOS
                                            try {

                                                //importamos la clase BB_DD_HELPER
                                                final BBDD_Helper helper = new BBDD_Helper(view.getContext());
                                                SQLiteDatabase db = helper.getWritableDatabase();

                                                // Create a new map of values, where column names are the keys
                                                ContentValues values = new ContentValues();

                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA2, id_poliza_amortizacion);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA3, no_poliza);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA4, no_cuota);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA5, monto_total_del_recibo);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA6, monto_pagar);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA7, saldo);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA8, id_recibo);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA9, id_cliente);
                                                values.put(Estructura_BBDD_Pre_Cuotas_Canceladas.NOMBRE_COLUMNA10, id_poliza_movimiento);

                                                // Insert the new row, returning the primary key value of the new row
                                                long newRowId = db.insert(Estructura_BBDD_Pre_Cuotas_Canceladas.TABLE_NAME, null, values);
                                                //Mensaje si guardo los datos del acceso exitosamente
                                                //Toast.makeText(view.getContext(), "Monto insertado correctamente " + id_recibo, Toast.LENGTH_SHORT).show();
                                                //mostramos layout de cancelado
                                                bloquea.setVisibility(View.VISIBLE);
                                                img_imprimir.setVisibility(View.GONE);
                                                etCantidad.setEnabled(false);
                                                tvEditable.setText("0");

                                                btnGuardar.setVisibility(View.GONE);

                                                //GUARDAMOS PRE AMORTIZACION EN EL SUDSYS
                                                //enviamos parametros al ws SaveReceipt
                                                //llamamos al WebService  SavePreCuotaAmortiza
                                                CuotasPreClienteAdapter.TercerPlano tarea3 = new CuotasPreClienteAdapter.TercerPlano();
                                                tarea3.execute();

                                                abrirDialogo();

                                            } catch (Exception ex) {
                                                mensaje = "ERROR: 10" + ex;
                                                Toast.makeText(view.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            //SI ENCUENTRA CUOTA YA PAGADA, ACTUALIZAMOS MONTO
                                            try {
                                                //ELIMINAMOS REGISTRO PARA INSERTAR NUEVO
                                                //importamos la clase BB_DD_HELPER

                                            /*
                                            final BBDD_Helper helper = new BBDD_Helper(view.getContext());
                                            SQLiteDatabase db = helper.getWritableDatabase();

                                            String selection = Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA2 + " LIKE ?";

                                            String[] selectionArgs = { id_poliza_amortizacion.toString() };

                                            db.delete(Estructura_BBDD_Cuotas_Pre_Canceladas.TABLE_NAME, selection, selectionArgs);

                                            Toast.makeText(view.getContext(), "Se borro el con clave: " + id_poliza_amortizacion.toString()+"-- numero_id_poliza_amortizacion:"+numero_id_poliza_amortizacion, Toast.LENGTH_SHORT).show();
                                            */
                                            /*
                                            //ACTUALIZAMOS DATOS A LA BASE DE DATOS
                                            //importamos la clase BB_DD_HELPER
                                            final BBDD_Helper helper = new BBDD_Helper(view.getContext());
                                            SQLiteDatabase db = helper.getWritableDatabase();

                                            // Create a new map of values, where column names are the keys
                                            ContentValues values = new ContentValues();


                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA2, id_poliza_amortizacion.toString());
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA3, no_poliza.toString());
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA4, no_cuota.toString());
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA5, monto_total_del_recibo);
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA6, monto_pagar);
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA7, saldo);
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA8, id_recibo.toString());
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA9, id_cliente.toString());
                                            values.put(Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA10, id_poliza_movimiento.toString());

                                            String selection2 = Estructura_BBDD_Cuotas_Pre_Canceladas.NOMBRE_COLUMNA2 + " LIKE ? ";
                                            String[] selectionArgs2 = {id_poliza_amortizacion.toString()};

                                            int count = db.update(

                                                    Estructura_BBDD_Cuotas_Pre_Canceladas.TABLE_NAME,
                                                    values,
                                                    selection2,
                                                    selectionArgs2
                                            );
                                            //Mensaje si guardo los datos del acceso exitosamente
                                            //Toast.makeText(view.getContext(), "Monto actualizado correctamente " + id_poliza_amortizacion, Toast.LENGTH_SHORT).show();


                                            //mostramos layout de cancelado
                                            bloquea.setVisibility(View.VISIBLE);
                                            img_imprimir.setVisibility(View.GONE);
                                            tvEditable.setText("0");
                                            etCantidad.setEnabled(false);
                                            abrirDialogo();
                                    */
                                            } catch (Exception ex3) {
                                                String mensaje3 = "ERROR 13: " + ex3;
                                                Toast.makeText(view.getContext(), mensaje3, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                }
                                dialog.hide();
                            }
                        }
                        //}else {

                        //  abrirDialogoPermisoSucursal();
                        //}
                    }
                }

            });


        }
    }

    //para el metodo SaveRecipent del webservice
    private class TercerPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

        }

        @Override
        protected Void doInBackground(Void... params){

            save_cuota_pre_amortizada();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR


            if(respuesta_recibido.toString().equals("1")) {
                //Toast.makeText(context, mensaje+"Se registro la Pre Amortizacion en el sistema sudsys, sucursal: " +id_recibo.toString(), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context, mensaje+"Error no se puede registrar la Pre Amortizacion en el sistema sudsys ", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void save_cuota_pre_amortizada() {
        String SOAP_ACTION = "http://ws.sudseguros.com/SavePreAmortizacion";
        String METHOD_NAME = "SavePreAmortizacion";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        try {
            if(id_cliente.equals("")){
                id_cliente="0";
            }
            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);
            RequestWs.addProperty("id_poliza_amortizacion", id_poliza_amortizacion);
            RequestWs.addProperty("no_poliza",no_poliza);
            RequestWs.addProperty("no_cuota",no_cuota);
            RequestWs.addProperty("monto_total_del_recibo", monto_total_del_recibo.toString());
            RequestWs.addProperty("monto_pagar", monto_pagar.toString());
            RequestWs.addProperty("saldo", String.valueOf(saldo));
            RequestWs.addProperty("id_recibo", "0");
            RequestWs.addProperty("id_cliente", id_cliente);
            RequestWs.addProperty("id_poliza_movimiento", id_poliza_movimiento);
            RequestWs.addProperty("id_sucursal", tv_id_sucursal_usuario);
            RequestWs.addProperty("id_empleado", id_empleado);
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

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);

            if (mensaje.equals("Error: Problema al Conectarse")){
                //    bloquea.setVisibility(View.GONE);

                Intent x =new Intent(context, pantalla_principal.class);
                context.startActivity(x);
            }

            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
            id_poliza_amortizacion_recibida = "" + resultString.getProperty(2);
        } catch (Exception ex) {
            mensaje = "ERROR 13: " + ex.getMessage() + " " +mensaje_recibido ;
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    //para el metodo SaveRecipent del webservice
    private class CuartoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            //muestra texto mientras carga

        }

        @Override
        protected Void doInBackground(Void... params){

            eliminar_cuota_pre_amortizada();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //TEXTO DESAPARECE AL CARGAR


            if(respuesta_recibido.toString().equals("1")) {
                //Toast.makeText(context, mensaje+"Se elimino la Pre Amortizacion en el sistema sudsys, sucursal: " +id_recibo.toString(), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context, mensaje+"Error no se puede eliminar la Pre Amortizacion en el sistema sudsys.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void eliminar_cuota_pre_amortizada() {
        String SOAP_ACTION = "http://ws.sudseguros.com/EliminarPreAmortizacion";
        String METHOD_NAME = "EliminarPreAmortizacion";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        try {


            if(id_cliente.equals("")){
                id_cliente="0";
            }
            SoapObject RequestWs = new SoapObject(NAMESPACE, METHOD_NAME);
            RequestWs.addProperty("id_poliza_amortizacion", id_poliza_amortizacion);
            RequestWs.addProperty("no_poliza",no_poliza);
            RequestWs.addProperty("no_cuota",no_cuota);
            RequestWs.addProperty("id_cliente", id_cliente);
            RequestWs.addProperty("id_poliza_movimiento", id_poliza_movimiento);
            RequestWs.addProperty("id_sucursal", tv_id_sucursal_usuario);
            RequestWs.addProperty("id_empleado", id_empleado);
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

            //mensaje = "" + resultString.getProperty(0) + " " + resultString.getProperty(1);
            mensaje = "" + resultString.getProperty(1);
            //almacenamos variables recibidas del ws
            respuesta_recibido = "" + resultString.getProperty(0);
            mensaje_recibido = "" + resultString.getProperty(1);
            id_poliza_amortizacion_recibida = "" + resultString.getProperty(2);
        } catch (Exception ex) {
            mensaje = "ERROR 13: " + ex.getMessage() + " " +mensaje_recibido ;
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
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

    public void abrirDialogoPermisoSucursal(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
        dialogo1.setTitle("Aviso");
        dialogo1.setMessage("No se puede amortizar la cuota debido a que la póliza pertenece a otra sucursal, gracias.");
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
