package uminho.grupo18.ui.view;

import uminho.grupo18.ui.*;
import uminho.grupo18.controller.EcraTatilController;
import java.util.*;

public class ClienteView
{
    private EcraTatilController controller;
    private View pai;
    private boolean continuar = true;

    public ClienteView(EcraTatilController controller, int restauranteId, View central)
    {
        this.controller = controller;
        this.pai = central;
    }

    public void iniciar()
    {
        construirEcraInicial();
    }

    private void construirEcraInicial()
    {
        ViewHelper.limparEcra();
        ViewHelper.mostrarTitulo("CLIENTE");

        Opcao[] opcoes = {
                new Opcao("Criar pedido no restaurante", () -> true),
                new Opcao("Criar pedido takeaway", () -> true),
                new Opcao("Voltar ao menu", () -> true)
        };

        new EcraInput(3, opcoes, true, null, s -> s.matches("[012]"),
                this::processarEcraInicial).draw();
    }

    private void processarEcraInicial(String input)
    {
        switch(input) {
            case "0":
                controller.criarPedido(false);
                loopMenu();
                break;
            case "1":
                controller.criarPedido(true);
                loopMenu();
                break;
            case "2":
                pai.run();
                break;
        }
    }

    private void loopMenu()
    {
        while(continuar) {
            ViewHelper.limparEcra();
            construirMenuPrincipal();
        }
    }

    private void construirMenuPrincipal()
    {
        ViewHelper.mostrarTitulo("FAZER PEDIDO");
        controller.mostrarPropostasDisponiveis();

        Opcao[] opcoes = {
                new Opcao("Adicionar proposta", () -> true),
                new Opcao("Filtrar alergénio", () -> true),
                new Opcao("Adicionar nota ao pedido", () -> true),
                new Opcao("Finalizar pedido", () -> true),
                new Opcao("Cancelar pedido", () -> true)
        };

        new EcraInput(5, opcoes, true, null, s -> s.matches("[0-4]"),
                this::processarOpcaoPrincipal).draw();
    }

    private void processarOpcaoPrincipal(String input)
    {
        switch(input) {
            case "0": adicionarProposta(); break;
            case "1": filtrarAlergenio(); break;
            case "2": adicionarNota(); break;
            case "3": finalizarPedido(); break;
            case "4": cancelarPedido(); break;
        }
    }

    private void adicionarProposta()
    {
        String nome = ViewHelper.lerTexto("Nome da proposta:");

        if(controller.propostaIsMenu(nome)) {
            escolherElementosMenu(nome);
        }

        personalizarPrato(nome);
        controller.adicionarPropostaPedido(nome);
        ViewHelper.mostrarMensagem("Proposta adicionada!");
    }

    private void escolherElementosMenu(String nomeMenu)
    {
        List<String> elementos = controller.getNomesElementosMenu(nomeMenu);

        for(int i = 0; i < elementos.size(); i++) {
            ViewHelper.limparEcra();
            ViewHelper.mostrarTitulo("Menu: " + nomeMenu);
            ViewHelper.mostrarTitulo("Escolha para: " + elementos.get(i));

            List<String> pratos = controller.getPossibilidadesElemento(nomeMenu, i);

            for(int p = 0; p < pratos.size(); p++)
                System.out.println(p + " - " + pratos.get(p));

            int esc = ViewHelper.lerInteiro("Escolha:", 0, pratos.size() - 1);
            controller.escolherElementoMenu(nomeMenu, i, esc);
        }
    }

    private void personalizarPrato(String nome)
    {
        boolean sair = false;

        while(!sair) {
            ViewHelper.limparEcra();
            ViewHelper.mostrarTitulo("Personalizar " + nome);

            for(String i : controller.getIngredientesPrato(nome))
                System.out.println(" - " + i);

            Opcao[] ops = {
                    new Opcao("Adicionar ingrediente", () -> true),
                    new Opcao("Remover ingrediente", () -> true),
                    new Opcao("Concluir", () -> true)
            };

            new EcraInput(3, ops, true, null, s -> s.matches("[012]"), op -> {
                if(op.equals("0")) adicionarIngrediente(nome);
                else if(op.equals("1")) removerIngrediente(nome);
                else sair = true;
            }).draw();
        }
    }

    private void adicionarIngrediente(String nome)
    {
        List<String> disp = controller.getIngredientesDisponiveis();

        for(int i = 0; i < disp.size(); i++)
            System.out.println(i + " - " + disp.get(i));

        int esc = ViewHelper.lerInteiro("Ingrediente:", 0, disp.size() - 1);
        controller.adicionarIngrediente(nome, disp.get(esc));
    }

    private void removerIngrediente(String nome)
    {
        List<String> atual = controller.getIngredientesPrato(nome);

        for(int i = 0; i < atual.size(); i++)
            System.out.println(i + " - " + atual.get(i));

        int esc = ViewHelper.lerInteiro("Ingrediente:", 0, atual.size() - 1);
        controller.removerIngrediente(nome, atual.get(esc));
    }

    private void adicionarNota()
    {
        String nota = ViewHelper.lerTexto("Nota para o pedido:");
        controller.adicionarNotaPedido(nota);
        ViewHelper.mostrarMensagem("Nota adicionada!");
    }

    private void filtrarAlergenio()
    {
        String nome = ViewHelper.lerTexto("Alergénio:");
        controller.filtrarAlergenio(nome);
    }

    private void finalizarPedido()
    {
        if(controller.finalizarPedido())
            continuar = false;
    }

    private void cancelarPedido()
    {
        if(ViewHelper.confirmarSimNao("Cancelar pedido?")) {
            controller.cancelarPedido();
            continuar = false;
        }
    }
}
