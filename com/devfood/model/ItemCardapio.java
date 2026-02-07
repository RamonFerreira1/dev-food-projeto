package com.devfood.model;

import java.io.Serializable;

public class ItemCardapio implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String nome;
    private final String descricao;
    private final double preco;

    public ItemCardapio(int id, String nome, String descricao, double preco) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return id + ": " + nome + " - R$" + String.format("%.2f", preco) + " (" + descricao + ")";
    }
}
