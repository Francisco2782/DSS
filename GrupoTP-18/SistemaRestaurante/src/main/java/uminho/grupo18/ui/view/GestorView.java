package uminho.grupo18.ui.view;
import uminho.grupo18.logicanegocio.*;
import uminho.grupo18.logicanegocio.pedidos.*;

import java.time.LocalTime;


import uminho.grupo18.ui.*;
import uminho.grupo18.controller.GestaoCadeiaController;
import java.time.LocalTime;

/**
 * View do Gestor/COO - comunica apenas com GestaoCadeiaController
 */
public class GestorView {
    private GestaoCadeiaController controller;
    private boolean continuar;
    private boolean isCOO;

    public GestorView(GestaoCadeiaController controller, boolean isCOO)
    {
        this.controller = controller;
        this.continuar = true;
    }

    public void iniciar()
    {
        if(!fazerLogin())
        {
            ViewHelper.mostrarMensagem("Login falhou!");
            return;
        }

        while(continuar)
        {
            ViewHelper.limparEcra();
            construirMenuPrincipal();
        }
    }

    private boolean fazerLogin() {
        ViewHelper.mostrarTitulo("LOGIN " + (isCOO ? "COO" : "GESTOR"));

        String username = ViewHelper.lerTexto("Username:");
        String password = ViewHelper.lerTexto("Password:");

        boolean sucesso = controller.login(username, password);

        if (sucesso) {
            ViewHelper.mostrarMensagem("Login bem sucedido! Bem-vindo, " + username);
        }

        return sucesso;
    }

    private void construirMenuPrincipal()
    {
        ViewHelper.mostrarTitulo("MENU " + (isCOO ? "COO" : "GESTOR"));

        Opcao[] opcoes;

        if(isCOO) // Menu COO - todas as funcionalidades
        {
            opcoes = new Opcao[9];
            opcoes[0] = new Opcao("Registar gestor", () -> true);
            opcoes[1] = new Opcao("Adicionar restaurante", () -> true);
            opcoes[2] = new Opcao("Fechar restaurante", () -> true);
            opcoes[3] = new Opcao("Adicionar armazém", () -> true);
            opcoes[4] = new Opcao("Obter indicadores globais", () -> true);
            opcoes[5] = new Opcao("Adicionar prato", () -> true);
            opcoes[6] = new Opcao("Adicionar menu", () -> true);
            opcoes[7] = new Opcao("Encomendar ingrediente", () -> true);
            opcoes[8] = new Opcao("Sair", () -> true);
        }else{
            opcoes = new Opcao[7];
            opcoes[0] = new Opcao("Obter indicadores do restaurante", () -> true);
            opcoes[1] = new Opcao("Adicionar prato", () -> true);
            opcoes[2] = new Opcao("Adicionar menu", () -> true);
            opcoes[3] = new Opcao("Emitir notificação", () -> true);
            opcoes[4] = new Opcao("Adicionar ingrediente ao armazém", () -> true);
            opcoes[5] = new Opcao("Remover armazém", () -> true);
            opcoes[6] = new Opcao("Sair", () -> true);
        }

        EcraInput ecra = new EcraInput
        (
                opcoes.length,
                opcoes,
                true,
                null,
                s -> s.matches("\\d+") && Integer.parseInt(s) >= 0 && Integer.parseInt(s) < opcoes.length,
                this::processarOpcao
        );
        ecra.draw();
    }

    private void processarOpcao(String input) {
        int opcao = Integer.parseInt(input);

        if (isCOO) {
            processarOpcaoCOO(opcao);
        } else {
            processarOpcaoGestor(opcao);
        }
    }

    private void processarOpcaoCOO(int opcao)
    {
        switch (opcao) {
            case 1:
                registarGestor();
                break;
            case 2:
                adicionarRestaurante();
                break;
            case 3:
                fecharRestaurante();
                break;
            case 4:
                adicionarArmazem();
                break;
            case 5:
                obterIndicadoresGlobais();
                break;
            case 6:
                adicionarPrato();
                break;
            case 7:
                adicionarMenu();
                break;
            case 8:
                encomendar();
                break;
            case 0:
                continuar = false;
                break;
        }
    }

    private void processarOpcaoGestor(int opcao) {
        switch (opcao) {
            case 1:
                obterIndicadoresRestaurante();
                break;
            case 2:
                adicionarPrato();
                break;
            case 3:
                adicionarMenu();
                break;
            case 4:
                emitirNotificacao();
                break;
            case 5:
                adicionarIngredienteArmazem();
                break;
            case 6:
                removerArmazem();
                break;
            case 0:
                continuar = false;
                break;
        }
    }

    private void registarGestor() {
        ViewHelper.mostrarHeader("REGISTAR GESTOR");

        String nome = ViewHelper.lerTexto("Nome:");
        String password = ViewHelper.lerTexto("Password:");
        String restaurante = ViewHelper.lerTexto("Restaurante ID:");
        boolean coo = ViewHelper.confirmarSimNao("É COO?");

        boolean sucesso = controller.registarGestor(nome, password, restaurante, coo);

        if (sucesso) {
            ViewHelper.mostrarMensagem("Gestor registado com sucesso!");
        } else {
            ViewHelper.mostrarMensagem("Erro ao registar gestor!");
        }
    }

    private void adicionarRestaurante() {
        ViewHelper.mostrarHeader("ADICIONAR RESTAURANTE");

        String restauranteId = ViewHelper.lerTexto("ID do restaurante:");
        controller.addRestaurante(restauranteId);
        ViewHelper.mostrarMensagem("Restaurante adicionado com sucesso!");
    }

    private void fecharRestaurante() {
        ViewHelper.mostrarHeader("FECHAR RESTAURANTE");

        int restauranteId = ViewHelper.lerInteiro("ID do restaurante:", 1, 9999);

        if (ViewHelper.confirmarSimNao("Confirma o encerramento do restaurante?")) {
            controller.fecharRestaurante(restauranteId);
            ViewHelper.mostrarMensagem("Restaurante fechado!");
        }
    }

    private void adicionarArmazem() {
        ViewHelper.mostrarHeader("ADICIONAR ARMAZÉM");

        int armazemId = ViewHelper.lerInteiro("ID do armazém:", 1, 9999);
        controller.adicionarArmazem(armazemId);
        ViewHelper.mostrarMensagem("Armazém adicionado com sucesso!");
    }

    private void obterIndicadoresGlobais() {
        ViewHelper.mostrarHeader("INDICADORES GLOBAIS");

        String indicadores = controller.getIndicadoresGlobal();

        Opcao[] opcoes = new Opcao[]{
                new Opcao(indicadores, () -> true)
        };
        Ecra ecra = new Ecra(1, opcoes, false, null);
        ecra.draw();

        ViewHelper.aguardarEnter();
    }

    private void obterIndicadoresRestaurante() {
        ViewHelper.mostrarHeader("INDICADORES DO RESTAURANTE");

        int restauranteId = ViewHelper.lerInteiro("ID do restaurante:", 1, 9999);
        String indicadores = controller.getIndicadoresRestaurante(restauranteId);

        Opcao[] opcoes = new Opcao[]{
                new Opcao(indicadores, () -> true)
        };
        Ecra ecra = new Ecra(1, opcoes, false, null);
        ecra.draw();

        ViewHelper.aguardarEnter();
    }

    private void adicionarPrato() {
        ViewHelper.mostrarHeader("ADICIONAR PRATO");

        String nome = ViewHelper.lerTexto("Nome do prato:");
        float preco = Float.parseFloat(ViewHelper.lerTexto("Preço (EUR):"));
        float custo = Float.parseFloat(ViewHelper.lerTexto("Custo (EUR):"));

        int horas = ViewHelper.lerInteiro("Tempo de preparo - Horas:", 0, 23);
        int minutos = ViewHelper.lerInteiro("Tempo de preparo - Minutos:", 0, 59);
        LocalTime tempoPreparo = LocalTime.of(horas, minutos);

        int tamanho = ViewHelper.lerInteiro("Tamanho (porções):", 1, 999);

        // Lista de ingredientes
        java.util.List<String> ingredientes = new java.util.ArrayList<>();
        boolean adicionarMais = true;

        while (adicionarMais) {
            String ingrediente = ViewHelper.lerTexto("Ingrediente (Enter para terminar):");
            if (ingrediente.isEmpty()) {
                adicionarMais = false;
            } else {
                ingredientes.add(ingrediente);
            }
        }

        controller.adicionarPrato(nome, preco, custo, tempoPreparo, tamanho, ingredientes);
        ViewHelper.mostrarMensagem("Prato adicionado com sucesso!");
    }

    private void adicionarMenu() {
        ViewHelper.mostrarHeader("ADICIONAR MENU");

        String nome = ViewHelper.lerTexto("Nome do menu:");
        float preco = Float.parseFloat(ViewHelper.lerTexto("Preço (EUR):"));
        float custo = Float.parseFloat(ViewHelper.lerTexto("Custo (EUR):"));

        int horas = ViewHelper.lerInteiro("Tempo de preparo - Horas:", 0, 23);
        int minutos = ViewHelper.lerInteiro("Tempo de preparo - Minutos:", 0, 59);
        LocalTime tempoPreparo = LocalTime.of(horas, minutos);

        int tamanho = ViewHelper.lerInteiro("Tamanho (porções):", 1, 999);

        // Lista de elementos do menu
        java.util.List<String> elementos = new java.util.ArrayList<>();
        boolean adicionarMais = true;

        while (adicionarMais) {
            String elemento = ViewHelper.lerTexto("Elemento do menu (Enter para terminar):");
            if (elemento.isEmpty()) {
                adicionarMais = false;
            } else {
                elementos.add(elemento);
            }
        }

        controller.adicionarMenu(nome, preco, custo, tempoPreparo, tamanho, elementos);
        ViewHelper.mostrarMensagem("Menu adicionado com sucesso!");
    }

    private void emitirNotificacao() {
        ViewHelper.mostrarHeader("EMITIR NOTIFICAÇÃO");

        int restauranteId = ViewHelper.lerInteiro("ID do restaurante:", 1, 9999);
        String texto = ViewHelper.lerTexto("Texto da notificação:");

        controller.emitirNotificacao(restauranteId, texto);
        ViewHelper.mostrarMensagem("Notificação emitida com sucesso!");
    }

    private void adicionarIngredienteArmazem() {
        ViewHelper.mostrarHeader("ADICIONAR INGREDIENTE AO ARMAZÉM");

        String nome = ViewHelper.lerTexto("Nome do ingrediente:");

        int horas = ViewHelper.lerInteiro("Tempo de espera - Horas:", 0, 23);
        int minutos = ViewHelper.lerInteiro("Tempo de espera - Minutos:", 0, 59);
        LocalTime tempoEspera = LocalTime.of(horas, minutos);

        controller.adicionarIngrediente(nome, tempoEspera);
        ViewHelper.mostrarMensagem("Ingrediente adicionado ao armazém!");
    }

    private void removerArmazem() {
        ViewHelper.mostrarHeader("REMOVER ARMAZÉM");

        int idArmazem = ViewHelper.lerInteiro("ID do armazém:", 1, 9999);

        if (ViewHelper.confirmarSimNao("Confirma remoção do armazém?")) {
            controller.removeArmazem(idArmazem);
            ViewHelper.mostrarMensagem("Armazém removido!");
        }
    }

    private void encomendar() {
        ViewHelper.mostrarHeader("ENCOMENDAR INGREDIENTE");

        String nome = ViewHelper.lerTexto("Nome do ingrediente:");
        int idArmazem = ViewHelper.lerInteiro("ID do armazém:", 1, 9999);

        controller.encomendar(nome, idArmazem);
        ViewHelper.mostrarMensagem("Encomenda efetuada com sucesso!");
    }
}