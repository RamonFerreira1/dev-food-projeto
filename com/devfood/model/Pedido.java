package com.devfood.model;

import com.devfood.model.enums.StatusPedido;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final Cliente cliente;
    private final Restaurante restaurante;
    private final List<ItemCardapio> itens = new ArrayList<>();
    private StatusPedido status = StatusPedido.AGUARDANDO_APROVACAO;
    private Entregador entregador;
    private final LocalDateTime criadoEm = LocalDateTime.now();
    private int avaliacao = 0; // 0 = sem avaliar
    private String comentario = null;

    public Pedido(int id, Cliente cliente, Restaurante restaurante) {
        this.id = id;
        this.cliente = cliente;
        this.restaurante = restaurante;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Restaurante getRestaurante() { return restaurante; }
    public List<ItemCardapio> getItens() { return itens; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public Entregador getEntregador() { return entregador; }
    public void setEntregador(Entregador entregador) { this.entregador = entregador; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public double calcularTotal() {
        return itens.stream().mapToDouble(ItemCardapio::getPreco).sum();
    }

    public void adicionarItem(ItemCardapio item) { itens.add(item); }

    public int getAvaliacao() { return avaliacao; }
    public void setAvaliacao(int avaliacao) { this.avaliacao = avaliacao; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    @Override
    public String toString() {
        return "Pedido#" + id + " - Cliente: " + cliente.getNome() + " - Restaurante: " + restaurante.getNome() + " - Status: " + status;
    }
}
