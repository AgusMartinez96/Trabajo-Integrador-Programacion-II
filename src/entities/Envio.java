package entities;

import java.time.LocalDate;

public class Envio extends GenericEntity {
    private String tracking; // UNIQUE, máx. 40
    private Empresa empresa; // Enum: ANDREANI, OCA, CORREO_ARG
    private TipoEnvio tipo; // Enum: ESTANDAR, EXPRES
    private double costo; // (10,2)
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private EstadoEnvio estado; // Enum: EN_PREPARACION, EN_TRANSITO, ENTREGADO

    public enum Empresa {
        ANDREANI, OCA, CORREO_ARG
    }

    public enum TipoEnvio {
        ESTANDAR, EXPRES
    }

    public enum EstadoEnvio {
        EN_PREPARACION, EN_TRANSITO, ENTREGADO
    }

    // Constructor vacío
    public Envio() {}

    // Constructor completo
    public Envio(Long id, String tracking, Empresa empresa, TipoEnvio tipo,
                 double costo, LocalDate fechaDespacho, LocalDate fechaEstimada, EstadoEnvio estado) {
        super(id, false);
        this.tracking = tracking;
        this.empresa = empresa;
        this.tipo = tipo;
        this.costo = costo;
        this.fechaDespacho = fechaDespacho;
        this.fechaEstimada = fechaEstimada;
        this.estado = estado;
    }

    // Getters y setters
    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public TipoEnvio getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnvio tipo) {
        this.tipo = tipo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDate getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDate fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
        this.estado = estado;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

     @Override
    public String toString() {
        return "Envio{" +
                "id=" + getId() +
                ", eliminado=" + isEliminado() +
                ", tracking='" + tracking + '\'' +
                ", empresa=" + empresa +
                ", tipo=" + tipo +
                ", costo=" + costo +
                ", fechaDespacho=" + fechaDespacho +
                ", fechaEstimada=" + fechaEstimada +
                ", estado=" + estado +
                '}';
    }
}