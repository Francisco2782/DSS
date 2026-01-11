package uminho.grupo18.ui.view;

import uminho.grupo18.ui.*;
import uminho.grupo18.controller.*;


public class View
{
    private BalcaoController balcaoController = new BalcaoController();
    private EcraTatilController ecraTatilController = new EcraTatilController();
    private GestaoCadeiaController gestaoCadeiaController = new GestaoCadeiaController();
    private PostoTrabalhoController postoTrabalhoController = new PostoTrabalhoController();

    public View() {}

    public void run()
    {
        ViewHelper.limparEcra();
        construirMenuInicial();
    }

    private void construirMenuInicial()
    {
        ViewHelper.mostrarTitulo("SISTEMA DE GESTAO DE RESTAURANTES");

        Opcao[] opcoes = new Opcao[4];
        opcoes[0] = new Opcao("Selecionar restaurante", () -> true);
        opcoes[1] = new Opcao("Login COO", () -> true);
        opcoes[2] = new Opcao("Login Gestor", () -> true);
        opcoes[3] = new Opcao("Sair", () -> true);

        EcraInput ecra = new EcraInput
                (
                    4,
                    opcoes,
                    true,
                    null,
                    s -> s.matches("[0123]"),
                    this::processarMenuInicial
                );
        ecra.draw();
    }

    private void processarMenuInicial(String input)
    {
        int opcao = Integer.parseInt(input);

        switch (opcao)
        {
            case 1:
                selecionarRestaurante();
                break;
            case 2:
                loginCOO();
                break;
            case 3:
                loginGestor();
                break;
            case 0:
                ViewHelper.mostrarMensagem("A sair do programa...");
                break;
        }
    }

    private void selecionarRestaurante()
    {
        int restauranteId = ViewHelper.lerInteiro("ID do restaurante:", 1, 9999);
        menuRestaurante(restauranteId);
    }

    private void menuRestaurante(int restauranteId)
    {
        boolean voltarMenu = false;

        while (!voltarMenu)
        {
            ViewHelper.limparEcra();
            ViewHelper.mostrarTitulo("MENU RESTAURANTE #" + restauranteId);

            Opcao[] opcoes = new Opcao[4];
            opcoes[0] = new Opcao("Efetuar pedido (Cliente)", () -> true);
            opcoes[1] = new Opcao("Display de Balcão", () -> true);
            opcoes[2] = new Opcao("Posto de Trabalho", () -> true);
            opcoes[3] = new Opcao("Voltar", () -> true);

            boolean[] continuar = {true};

            EcraInput ecra = new EcraInput(
                    4,
                    opcoes,
                    true,
                    null,
                    s -> s.matches("[0123]"),
                    input ->
                    {
                        int escolha = Integer.parseInt(input);
                        switch (escolha)
                        {
                            case 1:
                                ClienteView clienteView = new ClienteView(ecraTatilController, restauranteId);
                                clienteView.iniciar();
                                break;
                            case 2:
                                DisplayBalcaoView balcaoView = new DisplayBalcaoView(balcaoController, restauranteId);
                                balcaoView.iniciar();
                                break;
                            case 3:
                                int postoId = ViewHelper.lerInteiro("ID do posto:", 1, 999);
                                PostoTrabalhoController postoController =
                                        (PostoTrabalhoController) this.balcaoController; // Cast apropriado
                                PostoTrabalhoView postoView = new PostoTrabalhoView(postoController, restauranteId, postoId);
                                postoView.iniciar();
                                break;
                            case 0:
                                continuar[0] = false;
                                break;
                        }
                    }
            );
            ecra.draw();

            voltarMenu = !continuar[0];
        }
    }

    private void loginCOO()
    {
        GestorView gestorView = new GestorView(gestaoCadeiaController, true);
        gestorView.iniciar();
    }

    private void loginGestor()
    {
        GestorView gestorView = new GestorView(gestaoCadeiaController, false);
        gestorView.iniciar();
    }
}