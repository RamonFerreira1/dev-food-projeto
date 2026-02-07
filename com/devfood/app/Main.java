package com.devfood.app;

import com.devfood.service.SistemaDevFood;

public class Main {
    public static void main(String[] args) {
        SistemaDevFood sistema = SistemaDevFood.getInstance();
        sistema.carregarDados(); // load persisted data if any
        sistema.iniciar();
        sistema.salvarDados(); // save on exit
    }
}
