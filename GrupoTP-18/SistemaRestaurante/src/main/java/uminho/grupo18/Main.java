package uminho.grupo18;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ModelacaoEstrutural.facade.*;
import ModelacaoEstrutural.model.*;
import ModelacaoEstrutural.view.*;
import uminho.grupo18.logicanegocio.*;
import uminho.grupo18.logicanegocio.autenticacao.AutenticacaoFacade;
import uminho.grupo18.logicanegocio.cozinha.CozinhaFacade;
import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.pedidos.*;
import uminho.grupo18.logicanegocio.restaurante.Restaurante;
import uminho.grupo18.ui.view.View;

public class Main {
    
    public static void main(String[] args) {
        // Inicializar facades
        AutenticacaoFacade autenticacaoFacade = AutenticacaoFacade.getInstance();
        GestaoFacade gestaoFacade = GestaoFacade.getInstance();
        PedidoFacade pedidoFacade = PedidoFacade.getInstance();
        CozinhaFacade cozinhaFacade = CozinhaFacade.getInstance();
        CadeiaFacade cadeiaFacade = CadeiaFacade.getInstance();
        
        // Inicializar dados exemplo
        inicializarDadosExemplo(pedidoFacade, gestaoFacade, autenticacaoFacade);
        
        // Criar view geral
        View view = new View(autenticacaoFacade, cadeiaFacade);
        view.run();
    }
    
    // TODO: mandar isto de pica
    private static void inicializarDadosExemplo(PedidoFacade pedidoFacade, GestaoFacade gestaoFacade, AutenticacaoFacade autenticacaoFacade) {
        // Criar restaurantes
        Restaurante rest1 = new Restaurante(1, "Porto Centro", 5);
        Restaurante rest2 = new Restaurante(2, "Lisboa Baixa", 8);
        gestaoFacade.adicionarRestaurante(rest1);
        gestaoFacade.adicionarRestaurante(rest2);
        
        // Criar alguns pratos
        Prato prato1 = new Prato("Francesinha", 12.50f, 5.00f);
        Prato prato2 = new Prato("Bacalhau à Brás", 15.00f, 6.50f);
        Prato prato3 = new Prato("Arroz de Pato", 13.00f, 5.50f);
        Menu menu1 = new Menu("Menu Executivo", 10.00f, 4.00f, Menu.TamanhoMenu.MEDIO);
        
        // Adicionar ingredientes
        prato1.adicionarIngrediente(new Ingrediente("Queijo", LocalTime.of(0, 5)));
        prato1.adicionarIngrediente(new Ingrediente("Bife", LocalTime.of(0, 10)));
        prato2.adicionarIngrediente(new Ingrediente("Bacalhau", LocalTime.of(0, 15)));
        prato2.adicionarIngrediente(new Ingrediente("Batata", LocalTime.of(0, 10)));
        prato3.adicionarIngrediente(new Ingrediente("Pato", LocalTime.of(0, 20)));
        prato3.adicionarIngrediente(new Ingrediente("Arroz", LocalTime.of(0, 15)));
        
        // Adicionar alergénios
        Alergenio gluten = new Alergenio("Glúten");
        Alergenio lactose = new Alergenio("Lactose");
        prato1.adicionarAlergenio(gluten);
        prato1.adicionarAlergenio(lactose);
        
        pedidoFacade.adicionarProposta(prato1);
        pedidoFacade.adicionarProposta(prato2);
        pedidoFacade.adicionarProposta(prato3);
        pedidoFacade.adicionarProposta(menu1);
        pedidoFacade.adicionarAlergenio(gluten);
        pedidoFacade.adicionarAlergenio(lactose);
        
        // Criar um gestor padrão
        autenticacaoFacade.registarGestor("admin", "admin123", "Porto Centro", true);
    }
}
    
    private static void menuAutenticacao() {
        System.out.println("\n========== AUTENTICAÇÃO ==========");
        System.out.println("1. Login");
        System.out.println("2. Registar novo gestor");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        
        switch (opcao) {
            case 1:
                fazerLogin();
                break;
            case 2:
                registarGestor();
                break;
            case 0:
                System.out.println("A sair...");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
    private static void fazerLogin() {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (autenticacaoFacade.login(username, password)) {
            utilizadorLogado = autenticacaoFacade.getUtilizador(username);
            System.out.println("\n✓ Login bem sucedido! Bem-vindo, " + utilizadorLogado.getNome());
        } else {
            System.out.println("\n✗ Credenciais inválidas!");
        }
    }
    
    private static void registarGestor() {
        System.out.print("\nNome: ");
        String nome = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Restaurante: ");
        String restaurante = scanner.nextLine();
        System.out.print("É COO? (s/n): ");
        boolean coo = scanner.nextLine().equalsIgnoreCase("s");
        
        if (autenticacaoFacade.registarGestor(nome, password, restaurante, coo)) {
            System.out.println("\n✓ Gestor registado com sucesso!");
        } else {
            System.out.println("\n✗ Erro: utilizador já existe!");
        }
    }
    
    private static void menuPrincipal() {
        System.out.println("\n========== MENU PRINCIPAL ==========");
        System.out.println("Utilizador: " + utilizadorLogado.getNome() + 
                         (utilizadorLogado.isCoo() ? " (COO)" : " (Gestor)"));
        System.out.println("\n1. Sistema de Pedidos");
        System.out.println("2. Sistema de Gestão");
        System.out.println("3. Sistema de Cozinha");
        System.out.println("4. Sair (Logout)");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        
        switch (opcao) {
            case 1:
                menuPedidos();
                break;
            case 2:
                menuGestao();
                break;
            case 3:
                menuCozinha();
                break;
            case 4:
                utilizadorLogado = null;
                System.out.println("\n✓ Logout realizado com sucesso!");
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
    private static void menuPedidos() {
        System.out.println("\n========== SISTEMA DE PEDIDOS ==========");
        System.out.println("1. Criar novo pedido");
        System.out.println("2. Mostrar propostas disponíveis");
        System.out.println("3. Filtrar por alergénio");
        System.out.println("4. Mostrar alergénios");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        
        switch (opcao) {
            case 1:
                criarPedido();
                break;
            case 2:
                mostrarPropostas();
                break;
            case 3:
                filtrarAlergenio();
                break;
            case 4:
                mostrarAlergenios();
                break;
            case 0:
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
    private static void criarPedido() {
        System.out.println("\n--- Criar Novo Pedido ---");
        
        List<Proposta> propostas = pedidoFacade.mostrarPropostas();
        if (propostas.isEmpty()) {
            System.out.println("Não há propostas disponíveis!");
            return;
        }
        
        System.out.println("\nPropostas disponíveis:");
        for (int i = 0; i < propostas.size(); i++) {
            Proposta p = propostas.get(i);
            System.out.println((i + 1) + ". " + p.getNome() + " - " + p.getPreco() + "€");
        }
        
        List<Proposta> artigosSelecionados = new ArrayList<>();
        boolean continuar = true;
        
        while (continuar) {
            System.out.print("\nEscolha um artigo (0 para terminar): ");
            int escolha = lerInteiro();
            
            if (escolha == 0) {
                continuar = false;
            } else if (escolha > 0 && escolha <= propostas.size()) {
                artigosSelecionados.add(propostas.get(escolha - 1));
                System.out.println("✓ Artigo adicionado!");
            } else {
                System.out.println("✗ Opção inválida!");
            }
        }
        
        if (artigosSelecionados.isEmpty()) {
            System.out.println("Pedido cancelado - nenhum artigo selecionado.");
            return;
        }
        
        System.out.print("\nÉ takeaway? (s/n): ");
        boolean takeaway = scanner.nextLine().equalsIgnoreCase("s");
        
        // Criar pedido
        int numeroPedido = (int) (Math.random() * 10000);
        Pedido pedido = new Pedido(numeroPedido, artigosSelecionados, takeaway);
        
        System.out.println("\n--- Pedido Criado ---");
        System.out.println(pedido);
        
        // Atribuir balcão
        int balcao = pedidoFacade.atribuirBalcao(pedido);
        pedido.setBalcaoAtribuido(balcao);
        
        // Emitir documentos
        System.out.print("\nEmitir talão? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            Talao talao = pedidoFacade.emitirTalaoBalcao(pedido);
            talao.imprimir();
        }
        
        System.out.print("\nEmitir fatura? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            Fatura fatura = pedidoFacade.emitirFatura(pedido);
            fatura.imprimir();
        }
        
        System.out.print("\nGerar QR Code? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            String qrCode = pedidoFacade.emitirQRCode(pedido);
            System.out.println("QR Code: " + qrCode);
        }
    }
    
    private static void mostrarPropostas() {
        System.out.println("\n--- Propostas Disponíveis ---");
        List<Proposta> propostas = pedidoFacade.mostrarPropostas();
        
        if (propostas.isEmpty()) {
            System.out.println("Não há propostas disponíveis!");
            return;
        }
        
        for (Proposta p : propostas) {
            System.out.println("\n" + p);
            System.out.println("  Custo: " + p.getCusto() + "€");
            System.out.println("  Margem: " + p.calcularMargem() + "€");
            
            if (p instanceof Prato) {
                Prato prato = (Prato) p;
                if (!prato.getIngredientes().isEmpty()) {
                    System.out.println("  Ingredientes: ");
                    for (Ingrediente ing : prato.getIngredientes()) {
                        System.out.println("    - " + ing.getNome());
                    }
                }
            }
            
            if (!p.getAlergenios().isEmpty()) {
                System.out.print("  Alergénios: ");
                for (Alergenio a : p.getAlergenios()) {
                    System.out.print(a.getNome() + " ");
                }
                System.out.println();
            }
        }
    }
    
    private static void filtrarAlergenio() {
        System.out.print("\nNome do alergénio a filtrar: ");
        String alergenio = scanner.nextLine();
        
        List<Proposta> filtradas = pedidoFacade.filtrarAlergenio(alergenio);
        
        if (filtradas.isEmpty()) {
            System.out.println("Nenhuma proposta encontrada sem esse alergénio!");
        } else {
            System.out.println("\nPropostas sem " + alergenio + ":");
            for (Proposta p : filtradas) {
                System.out.println("  - " + p);
            }
        }
    }
    
    private static void mostrarAlergenios() {
        System.out.println("\n--- Alergénios Registados ---");
        List<Alergenio> alergenios = pedidoFacade.mostrarAlergenios();
        
        if (alergenios.isEmpty()) {
            System.out.println("Nenhum alergénio registado!");
        } else {
            for (Alergenio a : alergenios) {
                System.out.println("  - " + a.getNome());
            }
        }
    }
    
    private static void menuGestao() {
        System.out.println("\n========== SISTEMA DE GESTÃO ==========");
        System.out.println("1. Ver indicadores globais");
        System.out.println("2. Ver indicadores de restaurante");
        System.out.println("3. Registar funcionário");
        System.out.println("4. Emitir notificação");
        System.out.println("5. Encomendar ingrediente");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        
        switch (opcao) {
            case 1:
                gestaoFacade.getIndicadoresGlobal();
                break;
            case 2:
                verIndicadoresRestaurante();
                break;
            case 3:
                registarFuncionario();
                break;
            case 4:
                emitirNotificacao();
                break;
            case 5:
                encomendarIngrediente();
                break;
            case 0:
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
    private static void verIndicadoresRestaurante() {
        System.out.print("\nID do restaurante: ");
        int id = lerInteiro();
        gestaoFacade.getIndicadoresRestaurante(id);
    }
    
    private static void registarFuncionario() {
        System.out.print("\nID do restaurante: ");
        int id = lerInteiro();
        System.out.print("Nome do funcionário: ");
        String nome = scanner.nextLine();
        System.out.print("NIF: ");
        int nif = lerInteiro();
        
        if (gestaoFacade.registarFuncionario(id, nome, nif)) {
            System.out.println("✓ Funcionário registado com sucesso!");
        } else {
            System.out.println("✗ Erro ao registar funcionário!");
        }
    }
    
    private static void emitirNotificacao() {
        System.out.print("\nID do restaurante: ");
        int id = lerInteiro();
        gestaoFacade.emitirNotificacao(id);
    }
    
    private static void encomendarIngrediente() {
        System.out.print("\nID do restaurante: ");
        int id = lerInteiro();
        System.out.print("Nome do ingrediente: ");
        String nome = scanner.nextLine();
        gestaoFacade.encomendarIngrediente(nome, id);
    }
    
    private static void menuCozinha() {
        System.out.println("\n========== SISTEMA DE COZINHA ==========");
        System.out.println("1. Realizar pedido");
        System.out.println("2. Atrasar pedido");
        System.out.println("3. Pedir ingrediente");
        System.out.println("4. Registar etapa completa");
        System.out.println("5. Fazer entrega");
        System.out.println("6. Registar pedido completo");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        
        int opcao = lerInteiro();
        
        switch (opcao) {
            case 1:
                realizarPedido();
                break;
            case 2:
                atrasarPedido();
                break;
            case 3:
                pedirIngrediente();
                break;
            case 4:
                registarEtapa();
                break;
            case 5:
                fazerEntrega();
                break;
            case 6:
                registarPedidoCompleto();
                break;
            case 0:
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
    private static void realizarPedido() {
        System.out.print("\nNúmero do pedido: ");
        int numero = lerInteiro();
        Pedido pedido = new Pedido(numero, new ArrayList<>(), false);
        cozinhaFacade.realizarPedido(pedido);
    }
    
    private static void atrasarPedido() {
        System.out.print("\nNúmero do pedido: ");
        int numero = lerInteiro();
        System.out.print("Nova posição: ");
        int posicao = lerInteiro();
        System.out.print("Novo tempo (minutos): ");
        int tempo = lerInteiro();
        
        Pedido pedido = new Pedido(numero, new ArrayList<>(), false);
        cozinhaFacade.atrasarPedido(pedido, posicao, tempo);
    }
    
    private static void pedirIngrediente() {
        System.out.print("\nNome do ingrediente: ");
        String nome = scanner.nextLine();
        Ingrediente ingrediente = new Ingrediente(nome, LocalTime.of(0, 5));
        cozinhaFacade.pedirIngrediente(ingrediente);
    }
    
    private static void registarEtapa() {
        System.out.print("\nNúmero da etapa: ");
        int etapa = lerInteiro();
        cozinhaFacade.registarEtapaCompleta(etapa);
    }
    
    private static void fazerEntrega() {
        System.out.print("\nNúmero do pedido: ");
        int numero = lerInteiro();
        Pedido pedido = new Pedido(numero, new ArrayList<>(), false);
        cozinhaFacade.fazerEntrega(pedido);
    }
    
    private static void registarPedidoCompleto() {
        System.out.print("\nNúmero do pedido: ");
        int numero = lerInteiro();
        Pedido pedido = new Pedido(numero, new ArrayList<>(), false);
        cozinhaFacade.registarPedidoCompleto(pedido);
    }
    
    private static int lerInteiro() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Por favor, insira um número válido: ");
            }
        }
    }
}