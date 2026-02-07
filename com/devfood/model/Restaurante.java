package com.devfood.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Restaurante implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private String nome;
    private String cnpj;
    private String endereco;
    private LocalTime abre;
    private LocalTime fecha;
    private final Map<Integer, ItemCardapio> cardapio = new ConcurrentHashMap<>();

    public Restaurante(int id, String nome, String cnpj, String endereco, LocalTime abre, LocalTime fecha) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.abre = abre;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getEndereco() { return endereco; }
    public LocalTime getAbre() { return abre; }
    public LocalTime getFecha() { return fecha; }

    public void adicionarItem(ItemCardapio item) { cardapio.put(item.getId(), item); }
    public void removerItem(int itemId) { cardapio.remove(itemId); }
    public Map<Integer, ItemCardapio> getCardapio() { return cardapio; }

    public boolean estaAberto(java.time.LocalTime hora) {
        if (abre.isBefore(fecha) || abre.equals(fecha)) {
            return !hora.isBefore(abre) && hora.isBefore(fecha);
        } else {
            return !hora.isBefore(abre) || hora.isBefore(fecha);
        }
    }

    @Override
    public String toString() {
        return id + ": " + nome + " (CNPJ:" + cnpj + ") - " + endereco + " [" + abre + " - " + fecha + "]";
    }
}
