package com.example.cmacchiavelli.sudsys;

public class CuotasImprimir {

    private String id_recibo_app;
    private String id_cuota_amortizacion;
    private int imagen;
    private String no_poliza;
    private String total_saldo;
    private String monto;
    private String no_cuota;
    private String id_cliente;
    private String id_recibo;
    private String cliente;
    private String aseguradora;
    private String numero_recibo;
    private String riesgo;
    private String no_liquidacion;

    public CuotasImprimir(){

    }

    public CuotasImprimir(String id_recibo_app, String id_cuota_amortizacion, int imagen, String no_poliza, String total_saldo, String monto, String no_cuota, String id_cliente, String id_recibo, String cliente, String aseguradora, String numero_recibo, String riesgo, String no_liquidacion) {
        this.id_recibo_app = id_recibo_app;
        this.id_cuota_amortizacion = id_cuota_amortizacion;
        this.imagen = imagen;
        this.no_poliza= no_poliza;
        this.total_saldo= total_saldo;
        this.monto= monto;
        this.no_cuota= no_cuota;
        this.id_cliente= id_cliente;
        this.id_recibo= id_recibo;
        this.cliente= cliente;
        this.aseguradora= aseguradora;
        this.numero_recibo= numero_recibo;
        this.riesgo= riesgo;
        this.no_liquidacion= no_liquidacion;
    }

    public String getIdReciboApp() {
        return id_recibo_app;
    }

    public void setIdReciboApp(String id_recibo_app) {
        this.id_recibo_app = id_recibo_app;
    }

    public String getIdCuotaAmortizacion() {
        return id_cuota_amortizacion;
    }

    public void setIdCuotaAmortizacion(String id_cuota_amortizacion) {
        this.id_cuota_amortizacion = id_cuota_amortizacion;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNoPoliza() {
        return no_poliza;
    }

    public void setNoPoliza(String no_poliza) {
        this.no_poliza = no_poliza;
    }

    public String getTotalSaldo() {
        return total_saldo;
    }

    public void setTotalSaldo(String total_saldo) {
        this.total_saldo = total_saldo;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getNoCuota() {
        return no_cuota;
    }

    public void setNoCuota(String no_cuota) {
        this.no_cuota = no_cuota;
    }

    public String getIdCliente() {
        return id_cliente;
    }

    public void setIdCliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getIdRecibo() {
        return id_recibo;
    }

    public void setIdRecibo(String id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
    }

    public String getNoRecibo() {
        return numero_recibo;
    }

    public void setNoRecibo(String numero_recibo) {
        this.numero_recibo = numero_recibo;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public String getNoLiquidacion() {
        return no_liquidacion;
    }

    public void setNoLiquidacion(String no_liquidacion) {
        this.no_liquidacion = no_liquidacion;
    }
}
