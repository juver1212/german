package com.itp.trackinn.Utils;

/**
 * Created by desarrollo on 18/08/2017.
 */
public class ListaGuiaCampos {
    public String guia;
    public String cliente;
    public String destino;
    public String contacto;
    public String numero_contacto;
    public String tipo_documento;
    public String latitud;
    public String longitud;
    public String fechaCompromiso;
    public String horaCompromiso;
    public String flete;
    public String direccionReferencia;
    public String coordinador;
    public String telefonoCoordinador;
    public String ruccliente;

    public ListaGuiaCampos(){
        super();
    }

    public ListaGuiaCampos(String _guia,String _cliente,String _destino, String _contacto, String _numero_contacto, String _tipo_documento,
                           String _latitud, String _longitud, String fechaCompromiso_, String horaCompromiso_, String flete_,
                           String _direccionReferencia, String coordinador_, String telefonoCoordinador_, String _ruccliente) {
        super();
        this.guia = _guia;
        this.cliente = _cliente;
        this.ruccliente = _ruccliente;
        this.destino = _destino;
        this.contacto = _contacto;
        this.numero_contacto = _numero_contacto;
        this.tipo_documento = _tipo_documento;
        this.latitud = _latitud;
        this.longitud = _longitud;
        this.fechaCompromiso = fechaCompromiso_;
        this.horaCompromiso = horaCompromiso_;
        this.flete = flete_;
        this.direccionReferencia = _direccionReferencia;
        this.coordinador = coordinador_;
        this.telefonoCoordinador = telefonoCoordinador_;
    }
}