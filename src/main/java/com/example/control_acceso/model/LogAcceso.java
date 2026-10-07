package com.example.control_acceso.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_acceso")
public class LogAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uid;

    @Column(nullable = false)
    private String resultado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public LogAcceso() {
    }

    public LogAcceso(String uid, String resultado) {
        this.uid = uid;
        this.resultado = resultado;
        this.fechaHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}