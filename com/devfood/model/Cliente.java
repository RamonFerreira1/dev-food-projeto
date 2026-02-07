package com.devfood.model;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final String cpf;
    private final String endereco;
    private String senha; // simple password for future extension

    public Cliente(int id, String nome, String cpf, String endereco) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.senha = null;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public String toString() {
        return id + ": " + nome + " (CPF:" + cpf + ") - " + endereco;
    }
}
