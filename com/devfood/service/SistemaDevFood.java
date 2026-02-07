package com.devfood.service;

import com.devfood.exception.*;
import com.devfood.model.*;
import com.devfood.model.enums.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



public class SistemaDevFood implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final SistemaDevFood INSTANCE = new SistemaDevFood();

    public static SistemaDevFood getInstance() { return INSTANCE; }

    private final AtomicInteger idRest = new AtomicInteger(1);
    private final AtomicInteger idItem = new AtomicInteger(1);
    private final AtomicInteger idCliente = new AtomicInteger(1);
    private final AtomicInteger idEntregador = new AtomicInteger(1);
    private final AtomicInteger idPedido = new AtomicInteger(1);

    private final Map<Integer, Restaurante> restaurantes = new ConcurrentHashMap<>();
    private final Map<Integer, Cliente> clientes = new ConcurrentHashMap<>();
    private final Map<Integer, Entregador> entregadores = new ConcurrentHashMap<>();
    private final Map<Integer, Pedido> pedidos = new ConcurrentHashMap<>();

    private final Map<String, Integer> clienteByCpf = new ConcurrentHashMap<>();
    private final Map<String, Integer> restauranteByCnpj = new ConcurrentHashMap<>();
    private final Map<String, Integer> entregadorByCpf = new ConcurrentHashMap<>();

    private final List<PedidoListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private final String dataDir = "data";
    private final String logPath = dataDir + File.separator + "devfood.log";

    private SistemaDevFood() {
       
        Restaurante r1 = new Restaurante(idRest.getAndIncrement(),
                "Burguerão", "00.000.000/0001-00", "Rua A, 123",
                LocalTime.of(10, 0), LocalTime.of(23, 0));
        ItemCardapio i1 = new ItemCardapio(idItem.getAndIncrement(), "X-Burguer", "Hambúrguer com queijo", 18.50);
        ItemCardapio i2 = new ItemCardapio(idItem.getAndIncrement(), "Batata Frita", "Porção média", 9.00);
        r1.adicionarItem(i1); r1.adicionarItem(i2);
        restaurantes.put(r1.getId(), r1);
        restauranteByCnpj.put(r1.getCnpj(), r1.getId());
    }

    // ---------------- Persistence ----------------
    public void salvarDados() {
        try {
            new File(dataDir).mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "restaurantes.ser"))) {
                oos.writeObject(restaurantes);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "clientes.ser"))) {
                oos.writeObject(clientes);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "entregadores.ser"))) {
                oos.writeObject(entregadores);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "pedidos.ser"))) {
                oos.writeObject(pedidos);
            }
           
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "lookups.ser"))) {
                Map<String, Map> look = new HashMap<>();
                look.put("clienteByCpf", (Map) clienteByCpf);
                look.put("restauranteByCnpj", (Map) restauranteByCnpj);
                look.put("entregadorByCpf", (Map) entregadorByCpf);
                oos.writeObject(look);
            }
          
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataDir + File.separator + "counters.ser"))) {
                Map<String, Integer> cnt = new HashMap<>();
                cnt.put("idRest", idRest.get());
                cnt.put("idItem", idItem.get());
                cnt.put("idCliente", idCliente.get());
                cnt.put("idEntregador", idEntregador.get());
                cnt.put("idPedido", idPedido.get());
                oos.writeObject(cnt);
            }
            log("Dados salvos com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarDados() {
        try {
            File f = new File(dataDir + File.separator + "restaurantes.ser");
            if (!f.exists()) { log("Nenhum dado persistido encontrado."); return; }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "restaurantes.ser"))) {
                Map<Integer, Restaurante> r = (Map<Integer, Restaurante>) ois.readObject();
                restaurantes.clear(); restaurantes.putAll(r);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "clientes.ser"))) {
                Map<Integer, Cliente> c = (Map<Integer, Cliente>) ois.readObject();
                clientes.clear(); clientes.putAll(c);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "entregadores.ser"))) {
                Map<Integer, Entregador> e = (Map<Integer, Entregador>) ois.readObject();
                entregadores.clear(); entregadores.putAll(e);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "pedidos.ser"))) {
                Map<Integer, Pedido> p = (Map<Integer, Pedido>) ois.readObject();
                pedidos.clear(); pedidos.putAll(p);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "lookups.ser"))) {
                Map<String, Map> look = (Map<String, Map>) ois.readObject();
                clienteByCpf.clear(); clienteByCpf.putAll((Map<String,Integer>) look.get("clienteByCpf"));
                restauranteByCnpj.clear(); restauranteByCnpj.putAll((Map<String,Integer>) look.get("restauranteByCnpj"));
                entregadorByCpf.clear(); entregadorByCpf.putAll((Map<String,Integer>) look.get("entregadorByCpf"));
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataDir + File.separator + "counters.ser"))) {
                Map<String, Integer> cnt = (Map<String,Integer>) ois.readObject();
                idRest.set(cnt.getOrDefault("idRest", idRest.get()));
                idItem.set(cnt.getOrDefault("idItem", idItem.get()));
                idCliente.set(cnt.getOrDefault("idCliente", idCliente.get()));
                idEntregador.set(cnt.getOrDefault("idEntregador", idEntregador.get()));
                idPedido.set(cnt.getOrDefault("idPedido", idPedido.get()));
            }
            log("Dados carregados com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // ---------------- cadastro / login ----------------
    public Cliente cadastrarCliente(String nome, String cpf, String endereco) {
        if (clienteByCpf.containsKey(cpf)) throw new InvalidOperationException("CPF já cadastrado");
        int id = idCliente.getAndIncrement();
        Cliente c = new Cliente(id, nome, cpf, endereco);
        clientes.put(id, c); clienteByCpf.put(cpf, id);
        log("Cliente cadastrado: " + c);
        return c;
    }

    public Cliente loginCliente(String cpf) {
        Integer id = clienteByCpf.get(cpf);
        if (id == null) return null;
        return clientes.get(id);
    }

    public Restaurante cadastrarRestaurante(String nome, String cnpj, String endereco, LocalTime abre, LocalTime fecha) {
        if (restauranteByCnpj.containsKey(cnpj)) throw new InvalidOperationException("CNPJ já cadastrado");
        int id = idRest.getAndIncrement();
        Restaurante r = new Restaurante(id, nome, cnpj, endereco, abre, fecha);
        restaurantes.put(id, r); restauranteByCnpj.put(cnpj, id);
        log("Restaurante cadastrado: " + r);
        return r;
    }

    public Restaurante loginRestaurante(String cnpj) {
        Integer id = restauranteByCnpj.get(cnpj);
        if (id == null) return null;
        return restaurantes.get(id);
    }

    public Entregador cadastrarEntregador(String nome, String cpf, String tipoVeiculo) {
        if (entregadorByCpf.containsKey(cpf)) throw new InvalidOperationException("Entregador já cadastrado");
        int id = idEntregador.getAndIncrement();
        Entregador e = new Entregador(id, nome, cpf, tipoVeiculo);
        entregadores.put(id, e); entregadorByCpf.put(cpf, id);
        log("Entregador cadastrado: " + e);
        return e;
    }

    
    public Entregador loginEntregador(String cpf) {
        Integer id = entregadorByCpf.get(cpf);
        if (id == null) return null;
        return entregadores.get(id);
    }


    // ---------------- consultas ----------------
    public Collection<Restaurante> listarRestaurantes() { return restaurantes.values(); }
    public Collection<Cliente> listarClientes() { return clientes.values(); }
    public Collection<Entregador> listarEntregadores() { return entregadores.values(); }
    public Collection<Pedido> listarPedidos() { return pedidos.values(); }

    // ---------------- cardapio ----------------
    public ItemCardapio criarItemCardapio(String nome, String desc, double preco) {
        return new ItemCardapio(idItem.getAndIncrement(), nome, desc, preco);
    }

    public void adicionarItemAoRestaurante(int idRestaurante, ItemCardapio item) {
        Restaurante r = restaurantes.get(idRestaurante);
        if (r == null) throw new InvalidOperationException("Restaurante não encontrado");
        r.adicionarItem(item);
        log("Item adicionado ao restaurante " + idRestaurante + ": " + item);
    }

    public void removerItemDoRestaurante(int idRestaurante, int idItem) {
        Restaurante r = restaurantes.get(idRestaurante);
        if (r == null) throw new InvalidOperationException("Restaurante não encontrado");
        r.removerItem(idItem);
        log("Item removido do restaurante " + idRestaurante + ": " + idItem);
    }

    // ---------------- pedidos ----------------
    
   
    public Pedido criarPedido(int idCliente, int idRestaurante, List<Integer> idsItens, LocalTime horaDoPedido) throws RestauranteFechadoException {
        Cliente c = clientes.get(idCliente);
        Restaurante r = restaurantes.get(idRestaurante);
        if (c == null || r == null) throw new InvalidOperationException("Cliente ou restaurante inválido");


        LocalTime horaVerificar = (horaDoPedido != null) ? horaDoPedido : LocalTime.now();

        if (!r.estaAberto(horaVerificar)) {
            String msg = "O restaurante está fechado no momento.";
            if(horaDoPedido != null) msg += " (Simulando: " + horaVerificar + ")";
            throw new RestauranteFechadoException(msg);
        }

        int pid = idPedido.getAndIncrement();
        Pedido p = new Pedido(pid, c, r);
        for (int iid : idsItens) {
            ItemCardapio item = r.getCardapio().get(iid);
            if (item != null) p.adicionarItem(item);
        }
        pedidos.put(pid, p);
        
      
        String logMsg = "Pedido criado: " + p;
        if(horaDoPedido != null) logMsg += " (Simulando H: " + horaVerificar + ")";
        log(logMsg);
        
        notificar(p, "Seu pedido foi criado e está aguardando aprovação.");
        return p;
    }

    public void atualizarStatusPedido(int idPedido, StatusPedido novoStatus) throws PedidoNotFoundException {
        Pedido p = pedidos.get(idPedido);
        if (p == null) throw new PedidoNotFoundException("Pedido não encontrado.");
        synchronized (p) {
            p.setStatus(novoStatus);
            log("Pedido " + idPedido + " status: " + novoStatus);
            notificar(p, "Status atualizado para: " + novoStatus);
            if (novoStatus == StatusPedido.PRONTO_PARA_ENTREGA) tentarAtribuirEntregador(p);
        }
    }

 
    private void tentarAtribuirEntregador(Pedido p) {
        synchronized (entregadores) {
            for (Entregador e : entregadores.values()) {
                if (e.getStatus() == StatusEntregador.DISPONIVEL) {
                    e.setStatus(StatusEntregador.EM_ENTREGA);
                    p.setEntregador(e);
                    p.setStatus(StatusPedido.EM_TRANSITO);
                    log("Pedido " + p.getId() + " atribuído automaticamente ao entregador " + e.getId());
                    notificar(p, "Seu pedido saiu para entrega com " + e.getNome());
                    

                    
                    return; 
                }
            }
        }
        notificar(p, "Nenhum entregador disponível no momento.");
    }

    // ---------------- observer ----------------
    public void registrarListener(PedidoListener listener) { listeners.add(listener); }
    public void removerListener(PedidoListener listener) { listeners.remove(listener); }

    private void notificar(Pedido p, String mensagem) {
        synchronized (listeners) {
            for (PedidoListener l : listeners) {
                try { l.onPedidoAtualizado(p, mensagem); } catch (Exception ex) {}
            }
        }
    }

    // ---------------- logging ----------------
    private synchronized void log(String linha) {
        try {
            new File(dataDir).mkdirs();
            try (FileWriter fw = new FileWriter(logPath, true)) {
                fw.append(LocalDateTime.now().toString()).append(" - ").append(linha).append(System.lineSeparator());
            }
        } catch (Exception e) {
        }
    }

    // ---------------- statistics ----------------
    public double faturamentoRestaurante(int idRestaurante) {
        return listarPedidos().stream()
                .filter(p -> p.getRestaurante().getId() == idRestaurante && p.getStatus() == StatusPedido.ENTREGUE)
                .mapToDouble(Pedido::calcularTotal).sum();
    }

    public long pedidosEntreguesRestaurante(int idRestaurante) {
        return listarPedidos().stream()
                .filter(p -> p.getRestaurante().getId() == idRestaurante && p.getStatus() == StatusPedido.ENTREGUE)
                .count();
    }

    public double notaMediaRestaurante(int idRestaurante) {
        return listarPedidos().stream()
                .filter(p -> p.getRestaurante().getId() == idRestaurante && p.getAvaliacao() > 0)
                .mapToInt(Pedido::getAvaliacao).average().orElse(0.0);
    }

    // ---------------- CLI ----------------
    
    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        registrarListener((pedido, msg) -> System.out.println("[Notificação] " + msg + " (" + pedido + ")"));
        System.out.println("=== DEVFOOD (v3) CLI ===");

        while (true) {
            // Menu principal 
            System.out.println("\nÁrea:\n1 - Cliente\n2 - Restaurante\n3 - Entregador\n4 - Estatísticas\n5 - Ver log\n0 - Sair");
            String area = sc.nextLine();
            try {
                switch (area) {
                    case "1": areaCliente(sc); break;
                    case "2": areaRestaurante(sc); break;
                    case "3": areaEntregador(sc); break; 
                    case "4": areaEstatisticas(sc); break; 
                    case "5": mostrarLog(); break; 
                    case "0": System.out.println("Saindo..."); return;
                    default: System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void mostrarLog() {
        try {
            File f = new File(logPath);
            if (!f.exists()) { System.out.println("Nenhum log encontrado."); return; }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                br.lines().forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler log: " + e.getMessage());
        }
    }

   
    private void areaCliente(Scanner sc) {
        while (true) {
            System.out.println("\nCLIENTE:\n1 - Cadastrar\n2 - Login\n3 - Voltar");
            String opc = sc.nextLine();
            if (opc.equals("1")) {
                System.out.print("Nome: "); String nome = sc.nextLine();
                System.out.print("CPF: "); String cpf = sc.nextLine();
                System.out.print("Endereço: "); String end = sc.nextLine();
                try { 
                    Cliente c = cadastrarCliente(nome, cpf, end); 
                    System.out.println("Cadastrado: " + c);
                    
            
                    System.out.print("Simular horário (HH:MM) ou [Enter] para usar o real: ");
                    String horaStr = sc.nextLine();
                    LocalTime horaSimulada = null;
                    if (!horaStr.trim().isEmpty()) {
                        try { horaSimulada = LocalTime.parse(horaStr); } catch (Exception e) { System.out.println("Horário inválido, usando hora real."); }
                    }
                    
                    clienteMenu(sc, c, horaSimulada); 
                    
                } catch (Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
            } else if (opc.equals("2")) {
                System.out.print("CPF: "); String cpf = sc.nextLine();
                Cliente c = loginCliente(cpf);
                if (c == null) System.out.println("Cliente não encontrado"); 
                else {
                    
                    System.out.print("Simular horário (HH:MM) ou [Enter] para usar o real: ");
                    String horaStr = sc.nextLine();
                    LocalTime horaSimulada = null;
                    if (!horaStr.trim().isEmpty()) {
                        try { horaSimulada = LocalTime.parse(horaStr); } catch (Exception e) { System.out.println("Horário inválido, usando hora real."); }
                    }
                    
                    clienteMenu(sc, c, horaSimulada);
                }
            } else if (opc.equals("3")) return; else System.out.println("Opção inválida");
        }
    }

  
    private void clienteMenu(Scanner sc, Cliente cliente, LocalTime horaSimulada) {
    
        if (horaSimulada != null) {
            System.out.println("\n[Você está simulando o horário: " + horaSimulada + "]");
        } else {
            System.out.println("\n[Usando horário real do sistema]");
        }
        
        while (true) {
            System.out.println("\nCLIENTE [" + cliente.getNome() + "]:\n1 - Fazer pedido\n2 - Ver meus pedidos\n3 - Avaliar pedido\n4 - Logout");
            String opc = sc.nextLine();
            try {
                switch (opc) {
                    case "1":
                        System.out.println("Restaurantes:"); listarRestaurantes().forEach(System.out::println);
                        System.out.print("ID Restaurante: "); int idR = Integer.parseInt(sc.nextLine());
                        Restaurante r = restaurantes.get(idR);
                        if (r == null) { System.out.println("Restaurante inválido"); break; }
                        System.out.println("Cardápio:"); r.getCardapio().forEach((k,it) -> System.out.println(k + " - " + it));
                        
                        System.out.print("IDs dos itens (vírgula): "); 
                        String s = sc.nextLine();
                        
                        List<Integer> ids = new ArrayList<>();
                        String[] idTokens = s.split(",");
                        
                        for (String token : idTokens) {
                            try {
                                ids.add(Integer.parseInt(token.trim()));
                            } catch (NumberFormatException e) {
                                
                            }
                        }
                        
                        
                        try { 
                            
                            Pedido p = criarPedido(cliente.getId(), idR, ids, horaSimulada); 
                            System.out.println("Pedido criado: " + p); 
                        } catch(RestauranteFechadoException ex) { 
                            System.out.println(ex.getMessage()); 
                        }
                        break;
                    case "2":
                        listarPedidos().stream().filter(p -> p.getCliente().getId() == cliente.getId()).forEach(System.out::println);
                        break;
                    case "3":
                        System.out.print("ID do pedido a avaliar: "); int idAval = Integer.parseInt(sc.nextLine());
                        Pedido ped = pedidos.get(idAval);
                        if (ped == null || ped.getCliente().getId() != cliente.getId()) { System.out.println("Pedido inválido"); break; }
                        if (ped.getStatus() != StatusPedido.ENTREGUE) { System.out.println("Só é possível avaliar pedidos entregues"); break; }
                        System.out.print("Nota (1-5): "); int nota = Integer.parseInt(sc.nextLine());
                        System.out.print("Comentário (opcional): "); String cm = sc.nextLine();
                        ped.setAvaliacao(nota); ped.setComentario(cm);
                        log("Pedido " + ped.getId() + " avaliado com nota " + nota);
                        System.out.println("Obrigado pela avaliação!");
                        break;
                    case "4" : return;
                    default: System.out.println("Opção inválida");
                }
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
    }

    private void areaRestaurante(Scanner sc) {
        while (true) {
            System.out.println("\nRESTAURANTE:\n1 - Cadastrar\n2 - Login\n3 - Voltar");
            String opc = sc.nextLine();
            if (opc.equals("1")) {
                System.out.print("Nome: "); String nome = sc.nextLine();
                System.out.print("CNPJ: "); String cnpj = sc.nextLine();
                System.out.print("Endereço: "); String end = sc.nextLine();
                System.out.print("Abre (HH:MM): "); LocalTime abre = LocalTime.parse(sc.nextLine());
                System.out.print("Fecha (HH:MM): "); LocalTime fecha = LocalTime.parse(sc.nextLine());
                try { Restaurante r = cadastrarRestaurante(nome, cnpj, end, abre, fecha); System.out.println("Cadastrado: " + r); } catch(Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
            } else if (opc.equals("2")) {
                System.out.print("CNPJ: "); String cnpj = sc.nextLine();
                Restaurante r = loginRestaurante(cnpj);
                if (r == null) System.out.println("Restaurante não encontrado"); else restauranteMenu(sc, r);
            } else if (opc.equals("3")) return; else System.out.println("Opção inválida");
        }
    }

    private void restauranteMenu(Scanner sc, Restaurante restaurante) {
        while (true) {
            System.out.println("\nRESTAURANTE [" + restaurante.getNome() + "]:\n1 - Cadastrar entregador\n2 - Adicionar item ao cardápio\n3 - Remover item\n4 - Ver pedidos recebidos\n5 - Aprovar pedido\n6 - Atribuir entregador\n7 - Ver estatísticas\n8 - Logout");
            String opc = sc.nextLine();
            try {
                switch (opc) {
                    case "1":
                        System.out.print("Nome entregador: "); String ne = sc.nextLine();
                        System.out.print("CPF: "); String cpfe = sc.nextLine();
                        System.out.print("Veículo: "); String ve = sc.nextLine();
                        try { Entregador ent = cadastrarEntregador(ne, cpfe, ve); System.out.println("Entregador cadastrado: " + ent); } catch(Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
                        break;
                    case "2":
                        System.out.print("Nome item: "); String ni = sc.nextLine();
                        System.out.print("Descrição: "); String desc = sc.nextLine();
                        System.out.print("Preço: "); double preco = Double.parseDouble(sc.nextLine());
                        ItemCardapio it = criarItemCardapio(ni, desc, preco); adicionarItemAoRestaurante(restaurante.getId(), it); System.out.println("Item adicionado: " + it);
                        break;
                    case "3":
                        System.out.println("Cardápio:"); restaurante.getCardapio().forEach((k,it2) -> System.out.println(k + " - " + it2));
                        System.out.print("ID item a remover: "); int idIt = Integer.parseInt(sc.nextLine()); removerItemDoRestaurante(restaurante.getId(), idIt); System.out.println("Removido.");
                        break;
                    case "4":
                        listarPedidos().stream().filter(p -> p.getRestaurante().getId() == restaurante.getId()).forEach(System.out::println);
                        break;
                    case "5":
                        System.out.print("ID pedido a aprovar: "); int idA = Integer.parseInt(sc.nextLine()); try { atualizarStatusPedido(idA, StatusPedido.EM_PREPARO); System.out.println("Pedido agora EM_PREPARO"); } catch(Exception ex) { System.out.println("Erro: " + ex.getMessage()); }
                        break;
                    case "6":
                        System.out.print("ID pedido a atribuir: "); int idAtt = Integer.parseInt(sc.nextLine());
                        System.out.println("Entregadores disponíveis:"); listarEntregadores().stream().filter(e -> e.getStatus() == StatusEntregador.DISPONIVEL).forEach(System.out::println);
                        System.out.print("ID entregador: "); int idEnt = Integer.parseInt(sc.nextLine()); Entregador entSel = entregadores.get(idEnt); Pedido pedSel = pedidos.get(idAtt);
                        if (entSel == null || pedSel == null) { System.out.println("Entregador ou pedido inválido"); break; }
                        entSel.setStatus(StatusEntregador.EM_ENTREGA); pedSel.setEntregador(entSel); pedSel.setStatus(StatusPedido.EM_TRANSITO); notificar(pedSel, "Seu pedido saiu para entrega com " + entSel.getNome()); System.out.println("Atribuído.");
                        break;
                    case "7":
                        System.out.println("Faturamento: R$" + String.format("%.2f", faturamentoRestaurante(restaurante.getId())));
                        System.out.println("Pedidos entregues: " + pedidosEntreguesRestaurante(restaurante.getId()));
                        System.out.println("Nota média: " + String.format("%.2f", notaMediaRestaurante(restaurante.getId())));
                        break;
                    case "8": return;
                    default: System.out.println("Opção inválida");
                }
            } catch(Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
    }

    /* A área de login do entregador. */
    private void areaEntregador(Scanner sc) {
        while (true) {
            System.out.println("\nÁREA DO ENTREGADOR:\n1 - Login\n2 - Voltar");
            String opc = sc.nextLine();
            if (opc.equals("1")) {
                System.out.print("Seu CPF: "); String cpf = sc.nextLine();
                Entregador e = loginEntregador(cpf);
                if (e == null) System.out.println("Entregador não encontrado");
                else {
                    entregadorMenu(sc, e); 
                }
            } else if (opc.equals("2")) return;
            else System.out.println("Opção inválida");
        }
    }

    /* O menu de ações do entregador. */
    private void entregadorMenu(Scanner sc, Entregador entregador) {
        System.out.println("\nBEM-VINDO, " + entregador.getNome() + " | Status: [" + entregador.getStatus() + "]");
        while (true) {
            System.out.println("\nMENU ENTREGADOR:\n1 - Ver minhas entregas (em trânsito)\n2 - Marcar pedido como 'Entregue'\n3 - Logout");
            String opc = sc.nextLine();
            try {
                switch (opc) {
                    case "1":
                        System.out.println("Meus pedidos em trânsito:");
                        long count = listarPedidos().stream()
                            .filter(p -> p.getEntregador() != null && p.getEntregador().getId() == entregador.getId() && p.getStatus() == StatusPedido.EM_TRANSITO)
                            .peek(System.out::println) 
                            .count();
                        if (count == 0) System.out.println("Nenhuma entrega em trânsito no momento.");
                        break;
                    case "2":
                        System.out.print("ID do pedido entregue: ");
                        int idPedido = Integer.parseInt(sc.nextLine());
                        Pedido p = pedidos.get(idPedido);
                        
                        
                        if (p == null || p.getEntregador() == null || p.getEntregador().getId() != entregador.getId()) {
                            System.out.println("Pedido inválido ou não pertence a você.");
                            break;
                        }
                        if (p.getStatus() != StatusPedido.EM_TRANSITO) {
                            System.out.println("Este pedido não está em trânsito. Status atual: " + p.getStatus());
                            break;
                        }
                        
                        
                        atualizarStatusPedido(idPedido, StatusPedido.ENTREGUE);
                        entregador.setStatus(StatusEntregador.DISPONIVEL); // Libera o entregador
                        System.out.println("Pedido #" + idPedido + " marcado como ENTREGUE. Você está DISPONÍVEL.");
                        notificar(p, "Seu pedido foi entregue por " + entregador.getNome() + "!");
                        break;
                    case "3":
                        return; 
                    default:
                        System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }


    private void areaEstatisticas(Scanner sc) {
        System.out.println("Estatísticas gerais:");
        System.out.println("Total restaurantes: " + listarRestaurantes().size());
        System.out.println("Total clientes: " + listarClientes().size());
        System.out.println("Total entregadores: " + listarEntregadores().size());
        System.out.println("Total pedidos: " + listarPedidos().size());
    }
}