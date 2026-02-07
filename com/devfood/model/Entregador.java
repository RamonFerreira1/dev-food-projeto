package com.devfood.model;

import com.devfood.model.enums.StatusEntregador;
import java.io.Serializable;

public class Entregador implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final String cpf;
    private final String tipoVeiculo;
    private volatile StatusEntregador status = StatusEntregador.DISPONIVEL;

    public Entregador(int id, String nome, String cpf, String tipoVeiculo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.tipoVeiculo = tipoVeiculo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTipoVeiculo() { return tipoVeiculo; }
    public StatusEntregador getStatus() { return status; }
    public void setStatus(StatusEntregador status) { this.status = status; }

    @Override
    public String toString() {
        return id + ": " + nome + " - " + tipoVeiculo + " [" + status + "]";
    }
}
