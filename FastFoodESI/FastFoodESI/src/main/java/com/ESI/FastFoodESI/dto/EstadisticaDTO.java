package com.ESI.FastFoodESI.dto;

public class EstadisticaDTO {

    private long nPedidosTotal;
    private long nPedidosDia;
    private long nPedidosMes;
    private long nPedidosAnno;

    // --- CONSTRUCTORES ---

    public EstadisticaDTO() {
    }

    public EstadisticaDTO(long nPedidosTotal, long nPedidosDia, long nPedidosMes, long nPedidosAnno) {
        this.nPedidosTotal = nPedidosTotal;
        this.nPedidosDia = nPedidosDia;
        this.nPedidosMes = nPedidosMes;
        this.nPedidosAnno = nPedidosAnno;
    }

    // --- GETTERS Y SETTERS ---

    public long getnPedidosTotal() {
        return nPedidosTotal;
    }

    public void setnPedidosTotal(long nPedidosTotal) {
        this.nPedidosTotal = nPedidosTotal;
    }

    public long getnPedidosDia() {
        return nPedidosDia;
    }

    public void setnPedidosDia(long nPedidosDia) {
        this.nPedidosDia = nPedidosDia;
    }

    public long getnPedidosMes() {
        return nPedidosMes;
    }

    public void setnPedidosMes(long nPedidosMes) {
        this.nPedidosMes = nPedidosMes;
    }

    public long getnPedidosAnno() {
        return nPedidosAnno;
    }

    public void setnPedidosAnno(long nPedidosAnno) {
        this.nPedidosAnno = nPedidosAnno;
    }
}