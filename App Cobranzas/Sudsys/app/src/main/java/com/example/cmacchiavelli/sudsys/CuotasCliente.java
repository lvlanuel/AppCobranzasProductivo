package com.example.cmacchiavelli.sudsys;

public class CuotasCliente {

    private String nombre;
    private String artista;
    private int imagen;
    private String no_poliza;
    private String no_liquidacion;
    private String cia;
    private String monto;
    private String no_cuota;
    private String id_cliente;
    private String id_recibo;
    private String id_poliza_movimiento;
    private String ramo;
    private String monto_pagado;
    private String saldo_cuota;
    private String sucursal;
    private String id_sucursal;
    private String id_sucursal_usuario;
    private  String fecha_cuota;

    public CuotasCliente(){

    }

    public CuotasCliente(String nombre, String artista, int imagen, String no_poliza, String cia, String monto, String no_cuota, String id_cliente, String id_recibo, String id_poliza_movimiento, String ramo, String monto_pagado, String saldo_cuota, String sucursal, String id_sucursal, String id_sucursal_usuario, String fecha_cuota, String no_liquidacion ) {
        this.nombre = nombre;
        this.artista = artista;
        this.imagen = imagen;
        this.no_poliza= no_poliza;
        this.cia= cia;
        this.monto= monto;
        this.no_cuota= no_cuota;
        this.id_cliente= id_cliente;
        this.id_recibo= id_recibo;
        this.id_poliza_movimiento= id_poliza_movimiento;
        this.ramo= ramo;
        this.monto_pagado= monto_pagado;
        this.saldo_cuota= saldo_cuota;
        this.sucursal= sucursal;
        this.id_sucursal= id_sucursal;
        this.id_sucursal_usuario= id_sucursal_usuario;
        this.fecha_cuota= fecha_cuota;
        this.no_liquidacion= no_liquidacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
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

    public String getNoLiquidacion() {
        return no_liquidacion;
    }

    public void setNoLiquidacion(String no_liquidacion) {
        this.no_liquidacion = no_liquidacion;
    }

    public String getCia() {
        return cia;
    }

    public void setCia(String cia) {
        this.cia = cia;
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

    public String getIdPolizaMovimiento() {
        return id_poliza_movimiento;
    }

    public void setIdPolizaMovimiento(String id_poliza_movimiento) {
        this.id_poliza_movimiento = id_poliza_movimiento;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public String getMontoPagado() {
        return monto_pagado;
    }

    public void setMontoPagado(String monto_pagado) {
        this.monto_pagado = monto_pagado;
    }

    public String getSaldoCuota() {
        return saldo_cuota;
    }

    public void setSaldoCuota(String saldo_cuota) {
        this.saldo_cuota = saldo_cuota;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getIdSucursal() {
        return id_sucursal;
    }

    public void setIdSucursal(String id_sucursal) {
        this.id_sucursal = id_sucursal;
    }

    public String getIdSucursalUsuario() {
        return id_sucursal_usuario;
    }

    public void setIdSucursalUsuario(String id_sucursal_usuario) {
        this.id_sucursal_usuario = id_sucursal_usuario;
    }

    public String getFechaCuota() {
        return fecha_cuota;
    }

    public void setFechaCuota(String fecha_cuota) {
        this.fecha_cuota = fecha_cuota;
    }

}
