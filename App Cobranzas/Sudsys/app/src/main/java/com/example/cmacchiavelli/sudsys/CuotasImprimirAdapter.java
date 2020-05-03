package com.example.cmacchiavelli.sudsys;

import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class CuotasImprimirAdapter extends RecyclerView.Adapter<CuotasImprimirAdapter.CuotasViewHolder>{

    private ArrayList<CuotasImprimir> data;
    private Context context;
    private String[] mList;

    //para el monto literal


    private int flag;
    public int numero;
    public String importe_parcial;
    public String num;
    public String num_letra;
    public String num_letras;
    public String num_letram;
    public String num_letradm;
    public String num_letracm;
    public String num_letramm;
    public String num_letradmm;


    String[] escrito;

    TextView total_recibo;

    //variables a recibir ws saveRecipent
    String codigo_mac_impresora, respuesta_recibido, mensaje_recibido, id_recibo_recibido, numero_recibo_recibido,anio_recibo_recibido;
    //variables para imprimir
    String resultado, parametro_usuario_recibido, id_empleado_usuario, todo_pagado;
    SoapObject SoapArray;

    //para el webservice
    SoapObject resultString;
    SoapObject SoapArrayNivel1;
    SoapObject SoapArrayNivel2;
    SoapObject SoapArrayNivel3;

    private ZebraPrinter zebraPrinter;
    private Connection connection;

    String id_recibo_app;
    String no_liquidacion_aux="nada";
    String string_cuotas;
    String string_cuotas_detalle;
    int cantidad_cuotas=0;
    int cantidad_cuotas_aux=0;

    double parametro_monto_bs;
    ProgressDialog dialog;

    public CuotasImprimirAdapter(ArrayList<CuotasImprimir> data) {
        this.data = data;
        this.context = context;



    }

    @Override
    public CuotasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        escrito = new String[data.size()];
        return new CuotasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuotas_imprimir, parent, false));
    }

    @Override
    public void onBindViewHolder(CuotasViewHolder holder, int position) {

        final CuotasImprimir musica = data.get(position);

        if(no_liquidacion_aux.equals(musica.getNoLiquidacion().toString())){
            holder.imgMusica.setVisibility(View.GONE);
            holder.imgImprime.setVisibility(View.GONE);
            holder.tvNombre.setVisibility(View.GONE);
            holder.tvArtista.setVisibility(View.GONE);
            holder.tvCia.setVisibility(View.GONE);
            holder.tvNoPoliza.setVisibility(View.GONE);
            holder.tvRamo.setVisibility(View.GONE);
            holder.tvIdRecibo.setVisibility(View.GONE);

            holder.tvNoPol.setVisibility(View.GONE);
            holder.tvNoLiquida.setVisibility(View.GONE);
            holder.tvCliente.setVisibility(View.GONE);

            holder.tvAseguradora.setVisibility(View.GONE);
            holder.tvNoRecibo.setVisibility(View.GONE);
            holder.tvNoLiquidacion.setVisibility(View.GONE);

        }else{
            holder.imgMusica.setVisibility(View.VISIBLE);
            holder.imgImprime.setVisibility(View.VISIBLE);
            holder.tvNombre.setVisibility(View.GONE);
            holder.tvArtista.setVisibility(View.GONE);
            holder.tvCia.setVisibility(View.GONE);
            holder.tvNoPoliza.setVisibility(View.VISIBLE);
            holder.tvRamo.setVisibility(View.VISIBLE);
            holder.tvIdRecibo.setVisibility(View.GONE);

            holder.tvNoPol.setVisibility(View.VISIBLE);
            holder.tvNoLiquida.setVisibility(View.VISIBLE);
            holder.tvCliente.setVisibility(View.VISIBLE);

            holder.tvAseguradora.setVisibility(View.VISIBLE);
            holder.tvNoRecibo.setVisibility(View.VISIBLE);
            holder.tvNoLiquidacion.setVisibility(View.VISIBLE);

            no_liquidacion_aux=musica.getNoLiquidacion();
        }


            holder.imgMusica.setImageResource(musica.getImagen());
            holder.tvNombre.setText(musica.getIdReciboApp());
            holder.tvArtista.setText(musica.getIdCuotaAmortizacion());
            holder.tvNoPoliza.setText(musica.getNoPoliza());
            holder.tvNoLiquidacion.setText(musica.getNoLiquidacion());

            holder.tvCia.setText(musica.getTotalSaldo());
            holder.tvMonto.setText(musica.getMonto());

            parametro_monto_bs = (Double.valueOf(musica.getMonto()).doubleValue()) * (6.96);
            holder.tvMonto_bs.setText(String.valueOf(String.format("%.2f", parametro_monto_bs)));



            holder.tvNoCuota.setText(musica.getNoCuota());

            holder.tvIdCliente.setText(musica.getIdCliente());
            holder.tvIdRecibo.setText(musica.getIdRecibo());
            holder.tvCliente.setText(musica.getCliente());
            holder.tvAseguradora.setText(musica.getAseguradora());
            holder.tvNoRecibo.setText("AMORTIZADO CON EL RECIBO: " + musica.getNoRecibo());
            holder.tvRamo.setText(musica.getRiesgo().replace(" ",""));

            holder.etCantidad.setText("0");
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

    class CuotasViewHolder extends RecyclerView.ViewHolder{

        ImageView imgMusica;
        ImageView imgImprime;
        TextView tvNombre;
        TextView tvArtista;
        TextView tvNoPoliza;
        TextView tvNoLiquidacion;
        TextView tvCia;
        TextView tvMonto;
        TextView tvMonto_bs;
        EditText etCantidad;
        TextView tvNoCuota;

        TextView tvNoPol;
        TextView tvNoLiquida;

        TextView tvIdCliente;
        TextView tvIdRecibo, tvCliente, tvAseguradora, tvNoRecibo, tvRamo;

        Button pagar_cuota;





        public CuotasViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            imgMusica = (ImageView) itemView.findViewById(R.id.img_musica);
            imgImprime = (ImageView) itemView.findViewById(R.id.img_imprimir);
            tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
            tvArtista = (TextView) itemView.findViewById(R.id.tv_artista);
            tvNoPoliza = (TextView) itemView.findViewById(R.id.tv_no_poliza);
            tvNoLiquidacion = (TextView) itemView.findViewById(R.id.tv_no_liquidacion);
            tvCia = (TextView) itemView.findViewById(R.id.tv_cia);
            tvMonto = (TextView) itemView.findViewById(R.id.tv_monto);
            tvMonto_bs = (TextView) itemView.findViewById(R.id.tv_monto_bs);
            //pagar_cuota=(Button) itemView.findViewById(R.id.pagar_cuota);
            etCantidad = (EditText) itemView.findViewById(R.id.monto_a_pagar);
            tvNoCuota = (TextView) itemView.findViewById(R.id.tv_no_cuota);
            tvIdCliente = (TextView) itemView.findViewById(R.id.tv_id_cliente);
            tvIdRecibo = (TextView) itemView.findViewById(R.id.tv_id_recibo);
            tvCliente = (TextView) itemView.findViewById(R.id.tv_cliente);
            tvAseguradora = (TextView) itemView.findViewById(R.id.tv_aseguradora);
            tvNoRecibo = (TextView) itemView.findViewById(R.id.tv_no_recibo);
            tvRamo = (TextView) itemView.findViewById(R.id.tv_ramo);

            tvNoPol = (TextView) itemView.findViewById(R.id.textViewNoPol);
            tvNoLiquida = (TextView) itemView.findViewById(R.id.textViewNoLiquida);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(final View view) {
                    if (!compruebaConexion(view.getContext())) {
                        Toast.makeText(view.getContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
                        //finish();
                    } else {
                        CuotasImprimir musica2 = data.get(getLayoutPosition());
                        //Toast.makeText(context.getApplicationContext(), "posicion:" +getLayoutPosition()+ "  "+ musica2.getIdCuotaAmortizacion()+ musica2.getIdReciboApp(), Toast.LENGTH_SHORT).show();


                        id_recibo_app = tvNombre.getText().toString();


                        // you can add here

                        //Intent i=new Intent(context.getApplicationContext(), listado_imprime_cuota.class);
                        //guardamos el parametro usuario para recuperarlo en otra actividad
                        //i.putExtra("parametro_id_recibo_app", id_recibo_app);

                        //context.startActivity(i);
                        CuotasImprimirAdapter.CuartoPlanoImpresion tarea = new CuotasImprimirAdapter.CuartoPlanoImpresion();
                        tarea.execute();


                        // Toast.makeText(view.getContext(), "Recibo mandado a imprimir..." +id_recibo_app, Toast.LENGTH_LONG).show();
                        //receipPrint(id_recibo_app);


                    }
                }
            });

            etCantidad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    escrito[getAdapterPosition()]=s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    escrito[getAdapterPosition()]=s.toString();
                    //Toast.makeText(itemView.getContext(), "posicion:", Toast.LENGTH_SHORT).show();

                    CuotasImprimir musica2 = data.get(getLayoutPosition());
                    musica2.getIdCuotaAmortizacion();



                }

                @Override
                public void afterTextChanged(Editable s) {
                    escrito[getAdapterPosition()]=s.toString();
                }
            });


        }
    }






    private class CuartoPlanoImpresion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            //imgImpresora.setVisibility(View.GONE);
            //Toast.makeText(context.getApplicationContext(), "id recibo app ultimo: "+id_recibo_app, Toast.LENGTH_LONG).show();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Imprimiendo...");
            dialog.setCancelable (false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params){

            String id_recibo_app_enviar = id_recibo_app;

            String id_recibo = id_recibo_app;
            String id_empleado = "404";
            String imei_dispositivo = "357136081571342";
            String usuario_ws = "SUdMOv1l3";
            String password_ws = "AXr53.o1";

            receipPrint(id_recibo_app, id_empleado, imei_dispositivo, usuario_ws, password_ws);



            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();

            //imgImpresora.setVisibility(View.VISIBLE);
            //Button boton = (Button) findViewById(R.id.imprime);
            //boton.setVisibility(View.VISIBLE);
            dialog.setMessage(resultado);
            dialog.hide();
        }
    }

    //metodo que imprime
    private void receipPrint(String id_recibo_app, String id_empleado, String imei_dispositivo, String usuario_ws, String password_ws) {

        String SOAP_ACTION = "http://ws.sudseguros.com/PrintCuotaAmortizada";
        String METHOD_NAME = "PrintCuotaAmortizada";
        String NAMESPACE = "http://ws.sudseguros.com/";
        String URL = "http://ws.sudseguros.com/sudsys_ws_soap_app_movil/ServiceSud.asmx";

        String codigo = "";
        String mensaje = "";
        String id_sucursal = "";
        String numero_recibo = "";
        String fecha_emision = "";
        String hora_emision = "";
        String ci_nit = "";
        String cliente = "";
        String recibido_de = "";
        String moneda = "";
        String importe = "";
        String importe_bs = "";
        String monto_literal = "";
        String concepto = "";
        String tipo_pago = "";
        String cheque_banco = "";
        String cheque_numero = "";
        String cobrador = "";
        String compania="";

        String sucursal = "";
        String sucursal_abreviatura = "";

        String datos_cabecera = "";

        String numero_cuota="";
        String numero_poliza="";

        String importe_final;



        try {

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            Request.addProperty("id_recibo_app", id_recibo_app);
            Request.addProperty("imei_dispositivo", "357136081571342");
            Request.addProperty("usuario_ws", "SUdMOv1l3");
            Request.addProperty("password_ws", "AXr53.o1");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true; //tipo de servicio .net
            soapEnvelope.setOutputSoapObject(Request);

            //Invoca al web service
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION, soapEnvelope);

            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
            //Agarratodo el Objeto
            SoapArray = (SoapObject) soapEnvelope.getResponse(); //SoapPrimitive resultado simple, SoapObjet mas complejo


            codigo = SoapArray.getProperty(0).toString();
            mensaje = SoapArray.getProperty(1).toString();
            id_sucursal = SoapArray.getProperty(18).toString();
            id_recibo_app = SoapArray.getProperty(1).toString();
            numero_recibo = SoapArray.getProperty(17).toString();
            fecha_emision = SoapArray.getProperty(11).toString();
            hora_emision = SoapArray.getProperty(7).toString();
            numero_cuota = SoapArray.getProperty(5).toString();
            numero_poliza = SoapArray.getProperty(13).toString();
            ci_nit = "";
            cliente = SoapArray.getProperty(14).toString();
            recibido_de = SoapArray.getProperty(14).toString();
            //moneda = SoapArray.getProperty(18).toString();
            moneda = "$us";
            importe = SoapArray.getProperty(6).toString();
            importe_bs = "";
            double importe_verdadero_double=Double.parseDouble(importe);
            int importe_verdadero_int = (int) importe_verdadero_double;

            concepto = "PAGO CUOTAS POLIZAS DE SEGURO ";
            tipo_pago = "";
            cheque_banco = "";
            cheque_numero = "";
            cobrador = SoapArray.getProperty(16).toString();
            compania = SoapArray.getProperty(15).toString();
            id_empleado_usuario = SoapArray.getProperty(20).toString();

            todo_pagado = SoapArray.getProperty(2).toString();
            string_cuotas = todo_pagado.toString();

            sucursal = "";
            sucursal_abreviatura = "";

            //TEXTO CHEQUE DESAPARECE SI ESTA VACIO
            if(cheque_numero.toString().trim().isEmpty() || cheque_numero.toString().equals("anyType{}")) {
                cheque_numero="------";
            }

            resultado = "Respuesta: " + codigo;

        } catch (Exception ex) {
            resultado = "ERROR 12: " + ex.getMessage() + " " + ex.getLocalizedMessage();
            //Toast.makeText(context.getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
        }

        connectPrint(); //Conexion con la Impresora

        if (codigo.equals("1") && zebraPrinter != null) {

            try {

                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                PrinterStatus printerStatus = printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {

                    resultado = "Listo para imprimir ";


                    if(moneda.equals("$us")){
                        importe_final = importe;
                    }else{
                        importe_final = importe_bs;
                    }

                    if(id_sucursal.equals("1")) {
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }
                    else if(id_sucursal.equals("2")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 3-3416055 Fax: (591) 3-3416055^FS\n" +
                                "^FT144,104^A0N,17,16^FH\\^FDCalle La Plata # 25 - Equipetrol, Santa Cruz^FS\n" +
                                "^FT228,77^A0N,17,16^FH\\^FDSucursal Santa Cruz^FS";
                    }
                    else if(id_sucursal.equals("3")) {
                        datos_cabecera = "^FT140,133^A0N,17,16^FH\\^FDTelf.: (591) 4-4374220 ^FS\n" +
                                "^FT124,104^A0N,17,16^FH\\^FDEdificio Torre Empresarial Torre 42 \\A2n Piso 7 Calle Papa Paulo esquina Ramón Rivero # 604^FS\n" +
                                "^FT220,77^A0N,17,16^FH\\^FDSucursal Cochabamba^FS";
                    }
                    else if(id_sucursal.equals("5")) {
                        datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                "^FT244,77^A0N,17,16^FH\\^FDSucursal Tarija^FS";
                    }
                    else if(id_sucursal.equals("6")) {
                        datos_cabecera = "^FT213,133^A0N,17,16^FH\\^FDTelf.: (591) 4 - 6672014^FS\n" +
                                "^FT19,104^A0N,17,16^FH\\^FDAvenida La Paz N\\A7 249 Edif Concordia 1ra planta, entre calles Ciro Trigo y Abaroa^FS\n" +
                                "^FT244,77^A0N,17,16^FH\\^FDSucursal Oruro^FS";
                    }
                    else{
                        datos_cabecera = "^FT135,133^A0N,17,16^FH\\^FDTelf.: (591) 2-433500 Fax: (591) - 2-2128329^FS\n" +
                                "^FT155,104^A0N,17,16^FH\\^FDProlongaci\\A2n Cordero N\\F8 163 - San Jorge^FS\n" +
                                "^FT242,77^A0N,17,16^FH\\^FDSucursal La Paz^FS\n";
                    }


                    String texto = "\u0010CT~~CD,~CC^~CT~\n" +
                            "^XA~TA000~JSN^LT0^MNN^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA\n" +
                            "^MMT\n" +
                            "^PW591\n";

                        int tamano_vertical=730;
                        String[] partsaux = string_cuotas.split("CUOTA");
                        cantidad_cuotas_aux = partsaux.length - 1;
                        for (int i = 1; i <= cantidad_cuotas_aux; i++) {
                            tamano_vertical=tamano_vertical+20;
                            texto += "^LL0"+tamano_vertical+"\n";
                        }


                    texto+= "^LS0\n" +
                            "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                            "^FT397,237^A0N,21,21^FH\\^FD"+numero_recibo+sucursal_abreviatura+"^FS\n" +
                            "^FT193,406^A0N,21,21^FH\\^FDDETALLE AMORTIZACION^FS\n" +
                            "^FT131,237^A0N,21,21^FH\\^FDCancelado con el Recibo Nro.: ^FS\n" +
                            "^FT185,197^A0N,25,24^FH\\^FDAMORTIZACION CUOTA^FS\n" +
                            "^FT190,48^A0N,25,24^FH\\^FDSUDAMERICANA S.R.L^FS\n" +
                            "^FO9,251^GB574,0,1^FS\n" +
                            
                            "^FO7,377^GB572,0,0^FS\n" +
                            "^FO9,208^GB574,0,1^FS\n" +
                            //"^FT458,277^A0N,17,16^FH\\^FD"+hora_emision+"^FS\n" +
                            //"^FT415,277^A0N,17,16^FH\\^FDHora:^FS\n" +




                            "^FT15,320^A0N,21,21^FH\\^FDHe recibido de:^FS\n" +
                            "^FT158,321^A0N,17,16^FH\\^FD"+recibido_de+"^FS\n" +

                            "^FT15,278^A0N,21,21^FH\\^FDCompania:^FS\n" +
                            "^FT109,278^A0N,17,16^FH\\^FD"+compania+"^FS\n" +

                            "^FT16,361^A0N,21,21^FH\\^FDPor Concepto de:^FS\n" +
                            "^FT168,362^A0N,17,16^FH\\^FD "+concepto+"^FS\n";



                            //CUOTAS PRE AMORTIZDAS
                            texto +=  "^FT354,442^A0N,21,21^FH\\^FDCuota^FS\n" +
                            "^FT464,441^A0N,21,21^FH\\^FDMonto^FS\n" +
                            "^FT18,443^A0N,21,21^FH\\^FDNro Poliza^FS\n";
                            //"^FT18,442^A0N,21,21^FH\\^FDNro^FS\n";
                    int espacio_vertical_col1=442;
                    int espacio_vertical_col2=441;
                    int espacio_vertical_col3=443;
                    int espacio_vertical_col4=442;

                    String[] parts = string_cuotas.split("CUOTA");
                    cantidad_cuotas=parts.length-1;

                    double total_amortizacion=0.0;

                    for (int i = 1; i <= cantidad_cuotas; i++) {
                        espacio_vertical_col1 = espacio_vertical_col1 + 20;
                        espacio_vertical_col2 = espacio_vertical_col2 + 20;
                        espacio_vertical_col3 = espacio_vertical_col3 + 20;
                        espacio_vertical_col4 = espacio_vertical_col4 + 20;

                        String[] parts2 = parts[i].toString().split("\\*");
                        String no_poliza_detalle=parts2[11];
                        String no_cuota_detalle=parts2[3];
                        String monto_pagar_detalle=parts2[4];
                        cantidad_cuotas=parts.length-1;

                        total_amortizacion=total_amortizacion+Double.parseDouble(monto_pagar_detalle);
                        int total_amortizacion_int = (int) total_amortizacion;

                        monto_literal = convertirLetras(total_amortizacion_int)+" 00/100.- Dolares Americanos";


                        texto += "^FT354," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+no_cuota_detalle+"^FS\n" +
                                "^FT464," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+monto_pagar_detalle+" "+moneda+ "^FS\n" +
                                "^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+no_poliza_detalle+"^FS\n";
                                //"^FT18," + espacio_vertical_col3 + "^A0N,21,21^FH\\^FD"+i+"^FS\n";
                        if (i == cantidad_cuotas) {
                            texto += "^FT26," + (espacio_vertical_col3 + 67) + "^A0N,13,12^FH\\^FDCobrador:^FS\n" +
                                    "^FT79," + (espacio_vertical_col3 + 67) + "^A0N,13,12^FH\\^FD"+cobrador+"^FS\n" +

                                    "^FT26," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FDFecha:^FS\n" +
                                    "^FT79," + (espacio_vertical_col3 + 34) + "^A0N,13,12^FH\\^FD"+fecha_emision+"^FS\n" +

                                    "^FT53," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FD" + monto_literal + "^FS\n" +
                                    "^FT405," + (espacio_vertical_col3 + 60) + "^A0N,21,21^FH\\^FDTOTAL: " + total_amortizacion + " "+moneda+ "^FS\n" +
                                    "^FT26," + (espacio_vertical_col3 + 50) + "^A0N,13,12^FH\\^FDSon:^FS\n" +
                                    "^FT18," + (espacio_vertical_col3 + 271) + "^A0N,17,16^FH\\^FDSeguros^FS\n" +
                                    "^FT57," + (espacio_vertical_col3 + 256) + "^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                                    "^FT18," + (espacio_vertical_col3 + 243) + "^BQN,2,5\n" +
                                    "^FDMA,"+ci_nit+"|"+importe_final+"|"+fecha_emision+"|"+id_recibo_app+"^FS\n" +//qr
                                    "^FT17," + (espacio_vertical_col3 + 255) + "^A0N,17,16^FH\\^FDNota:^FS\n" +
                                    "^FT200,162^A0N,17,16^FH\\^FDCall Center: 800-10-3070^FS\n" + datos_cabecera +
                                    "^FT382," + (espacio_vertical_col3 + 188) + "^A0N,17,16^FH\\^FDFirma Cobrador^FS\n" +
                                    "^PQ1,0,1,Y^XZ\n";
                        }
                    }

/*
                            //"^FT19,474^A0N,17,16^FH\\^FD"+tipo_pago+"^FS\n" +
                            //"^FT129,473^A0N,17,16^FH\\^FD"+cheque_numero+"^FS\n" +
                    texto +=  "^FT352,474^A0N,17,16^FH\\^FD"+moneda+"^FS\n" +
                            "^FT129,473^A0N,17,16^FH\\^FD"+importe_final+"^FS\n" +
                            "^FT354,442^A0N,21,21^FH\\^FDMoneda^FS\n" +
                            "^FT129,443^A0N,21,21^FH\\^FDMonto^FS\n" +
                            //"^FT129,443^A0N,21,21^FH\\^FDCheque Nro^FS\n" +
                            //"^FT18,442^A0N,21,21^FH\\^FDTipo^FS\n" +
                            "^FT16,537^A0N,13,12^FH\\^FDCobrador:^FS\n" +


                            "^FT43,514^A0N,13,12^FH\\^FD"+monto_literal+"^FS\n" +
                            "^FT16,513^A0N,13,12^FH\\^FDSon:^FS\n" +
                            "^FT18,751^A0N,17,16^FH\\^FDSeguros^FS\n" +
                            "^FT57,726^A0N,17,16^FH\\^FDConserve este recibo, posteriormente se le entregara la factura de la Compa\\A4ia de^FS\n" +
                            "^FT18,713^BQN,2,5\n" +
                            "^FDMA,"+ci_nit+"|"+importe_final+"|"+fecha_emision+"|"+id_recibo_app+"^FS\n" +//qr
                            "^FT17,725^A0N,17,16^FH\\^FDNota:^FS\n" +

                            "^FT382,668^A0N,17,16^FH\\^FDFirma Cobrador^FS\n" +
                            "^PQ1,0,1,Y^XZ\n";
*/

                    byte[] rcpt = texto.getBytes();
                    try {
                        connection.write(rcpt);
                        resultado = "Documento Impreso ";
                    } catch (ConnectionException e) {
                        resultado = "Error al Imprimir "+ e.getMessage();
                    }

                } else if (printerStatus.isPaused) {
                    resultado = "No se puede imprimir por que el dipositivo detenido.";
                } else if (printerStatus.isHeadOpen) {
                    resultado = "No se puede imprimir por que la cabecera esta abierta.";
                } else if (printerStatus.isPaperOut) {
                    resultado = "No se puede imprimir por que el papel esta afuera.";
                } else {
                    resultado = "No se puede imprimir.";
                }

            } catch (ConnectionException e) {
                e.printStackTrace();
            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }


        }
    }


    public void connectPrint() {

        //OBTENEMOS MAC DE LA IMPRESORA
        //importamos la clase BB_DD_HELPER
        final BBDD_Helper helper = new BBDD_Helper(context.getApplicationContext());


        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                //que columnas queremos que saque nuestra consulta
                Estructura_BBDD.NOMBRE_COLUMNA9,//1 id

        };

        //COLUMNA QUE COMPARAREMOS (WHERE)
        String selection = Estructura_BBDD.NOMBRE_COLUMNA1 + " = ? ";
        ////////String[] selectionArgs = { textoId.getText().toString() };
        String[] selectionArgs = { id_empleado_usuario.toString() };

        try {

            Cursor c = db.query(
                    Estructura_BBDD.TABLE_NAME,
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
            codigo_mac_impresora = c.getString(0);
        }
        catch (Exception ex2){
            //Toast.makeText(getApplicationContext(), "No se encontro registro de MAC", Toast.LENGTH_LONG).show();
            resultado = "No es Posible conectarse con la Impresora, MAC no valida " + ex2.getMessage();
        }

        //CODIGO MAC DE LA IMPRESORA, OBLIGATORIO PARA RECONOCIMIENTO
        connection = new BluetoothConnection(codigo_mac_impresora); //c:3f:a4:a1:0e:ad


        try
        {
            connection.open();
        }

        catch (ConnectionException ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
            closeConnection();
        }

        catch(Exception ex)
        {
            resultado = "No es Posible conectarse con la Impresora " + ex.getMessage();
        }

        try
        {
            zebraPrinter = ZebraPrinterFactory.getInstance(connection);
            PrinterLanguage pl = zebraPrinter.getPrinterControlLanguage();

            resultado = "Conectado a la Impresora " + pl;
        }

        catch (ConnectionException ex)
        {
            resultado = "No pudo conectarse a la Impresora 1 " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();

        } catch(Exception ex)
        {
            resultado = "No pudo conectarse a la Impresora2  " + ex.getMessage();
            zebraPrinter = null;
            closeConnection();
        }
    }

    private void closeConnection(){
        if(connection != null){
            try
            {
                connection.close();
            }
            catch (ConnectionException exx)
            {
                resultado = "Conexion con impresora Cerrada " + exx.getMessage();
            }
        }
    }





    private String unidad(int numero){

        switch (numero){
            case 9:
                num = "nueve";
                break;
            case 8:
                num = "ocho";
                break;
            case 7:
                num = "siete";
                break;
            case 6:
                num = "seis";
                break;
            case 5:
                num = "cinco";
                break;
            case 4:
                num = "cuatro";
                break;
            case 3:
                num = "tres";
                break;
            case 2:
                num = "dos";
                break;
            case 1:
                if (flag == 0)
                    num = "uno";
                else
                    num = "un";
                break;
            case 0:
                num = "";
                break;
        }
        return num;
    }

    private String decena(int numero){

        if (numero >= 90 && numero <= 99)
        {
            num_letra = "noventa ";
            if (numero > 90)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 90));
        }
        else if (numero >= 80 && numero <= 89)
        {
            num_letra = "ochenta ";
            if (numero > 80)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 80));
        }
        else if (numero >= 70 && numero <= 79)
        {
            num_letra = "setenta ";
            if (numero > 70)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 70));
        }
        else if (numero >= 60 && numero <= 69)
        {
            num_letra = "sesenta ";
            if (numero > 60)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 60));
        }
        else if (numero >= 50 && numero <= 59)
        {
            num_letra = "cincuenta ";
            if (numero > 50)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 50));
        }
        else if (numero >= 40 && numero <= 49)
        {
            num_letra = "cuarenta ";
            if (numero > 40)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 40));
        }
        else if (numero >= 30 && numero <= 39)
        {
            num_letra = "treinta ";
            if (numero > 30)
                num_letra = num_letra.concat("y ").concat(unidad(numero - 30));
        }
        else if (numero >= 20 && numero <= 29)
        {
            if (numero == 20)
                num_letra = "veinte ";
            else
                num_letra = "veinti".concat(unidad(numero - 20));
        }
        else if (numero >= 10 && numero <= 19)
        {
            switch (numero){
                case 10:

                    num_letra = "diez ";
                    break;

                case 11:

                    num_letra = "once ";
                    break;

                case 12:

                    num_letra = "doce ";
                    break;

                case 13:

                    num_letra = "trece ";
                    break;

                case 14:

                    num_letra = "catorce ";
                    break;

                case 15:

                    num_letra = "quince ";
                    break;

                case 16:

                    num_letra = "dieciseis ";
                    break;

                case 17:

                    num_letra = "diecisiete ";
                    break;

                case 18:

                    num_letra = "dieciocho ";
                    break;

                case 19:

                    num_letra = "diecinueve ";
                    break;

            }
        }
        else
            num_letra = unidad(numero);

        return num_letra;
    }

    private String centena(int numero){
        if (numero >= 100)
        {
            if (numero >= 900 && numero <= 999)
            {
                num_letra = "novecientos ";
                if (numero > 900)
                    num_letra = num_letra.concat(decena(numero - 900));
            }
            else if (numero >= 800 && numero <= 899)
            {
                num_letra = "ochocientos ";
                if (numero > 800)
                    num_letra = num_letra.concat(decena(numero - 800));
            }
            else if (numero >= 700 && numero <= 799)
            {
                num_letra = "setecientos ";
                if (numero > 700)
                    num_letra = num_letra.concat(decena(numero - 700));
            }
            else if (numero >= 600 && numero <= 699)
            {
                num_letra = "seiscientos ";
                if (numero > 600)
                    num_letra = num_letra.concat(decena(numero - 600));
            }
            else if (numero >= 500 && numero <= 599)
            {
                num_letra = "quinientos ";
                if (numero > 500)
                    num_letra = num_letra.concat(decena(numero - 500));
            }
            else if (numero >= 400 && numero <= 499)
            {
                num_letra = "cuatrocientos ";
                if (numero > 400)
                    num_letra = num_letra.concat(decena(numero - 400));
            }
            else if (numero >= 300 && numero <= 399)
            {
                num_letra = "trescientos ";
                if (numero > 300)
                    num_letra = num_letra.concat(decena(numero - 300));
            }
            else if (numero >= 200 && numero <= 299)
            {
                num_letra = "doscientos ";
                if (numero > 200)
                    num_letra = num_letra.concat(decena(numero - 200));
            }
            else if (numero >= 100 && numero <= 199)
            {
                if (numero == 100)
                    num_letra = "cien ";
                else
                    num_letra = "ciento ".concat(decena(numero - 100));
            }
        }
        else
            num_letra = decena(numero);

        return num_letra;
    }

    private String miles(int numero){
        if (numero >= 1000 && numero <2000){
            num_letram = ("mil ").concat(centena(numero%1000));
        }
        if (numero >= 2000 && numero <10000){
            flag=1;
            num_letram = unidad(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 1000)
            num_letram = centena(numero);

        return num_letram;
    }

    private String decmiles(int numero){
        if (numero == 10000)
            num_letradm = "diez mil";
        if (numero > 10000 && numero <20000){
            flag=1;
            num_letradm = decena(numero/1000).concat("mil ").concat(centena(numero%1000));
        }
        if (numero >= 20000 && numero <100000){
            flag=1;
            num_letradm = decena(numero/1000).concat(" mil ").concat(miles(numero%1000));
        }


        if (numero < 10000)
            num_letradm = miles(numero);

        return num_letradm;
    }

    private String cienmiles(int numero){
        if (numero == 100000)
            num_letracm = "cien mil";
        if (numero >= 100000 && numero <1000000){
            flag=1;
            num_letracm = centena(numero/1000).concat(" mil ").concat(centena(numero%1000));
        }
        if (numero < 100000)
            num_letracm = decmiles(numero);
        return num_letracm;
    }

    private String millon(int numero){
        if (numero >= 1000000 && numero <2000000){
            flag=1;
            num_letramm = ("Un millon ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 2000000 && numero <10000000){
            flag=1;
            num_letramm = unidad(numero/1000000).concat(" millones ").concat(cienmiles(numero%1000000));
        }
        if (numero < 1000000)
            num_letramm = cienmiles(numero);

        return num_letramm;
    }

    private String decmillon(int numero){
        if (numero == 10000000)
            num_letradmm = "diez millones";
        if (numero > 10000000 && numero <20000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat("millones ").concat(cienmiles(numero%1000000));
        }
        if (numero >= 20000000 && numero <100000000){
            flag=1;
            num_letradmm = decena(numero/1000000).concat(" milllones ").concat(millon(numero%1000000));
        }


        if (numero < 10000000)
            num_letradmm = millon(numero);

        return num_letradmm;
    }


    public String convertirLetras(int numero){
        num_letras = decmillon(numero);
        return num_letras;
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
