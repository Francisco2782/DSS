package uminho.grupo18.ui.view;

import uminho.grupo18.logicanegocio.cozinha.CozinhaFacade;
import uminho.grupo18.ui.Ecra;
import uminho.grupo18.ui.EcraInput;
import uminho.grupo18.ui.Opcao;

public class DisplayFuncionarioView
{
    private CozinhaFacade cozinhaFacade;
    private boolean continuar;

    public DisplayFuncionarioView(CozinhaFacade facade)
    {
        this.cozinhaFacade = facade;
        this.continuar = true;
    }

    public void iniciar()
    {
        while(continuar)
        {
            ViewHelper.limparEcra();
            construirEcraFuncionario();
        }
    }

    private void construirEcraFuncionario()
    {
        Opcao[] opcoes = new Opcao[6];
        opcoes[0] = new Opcao("POSTO DE TRABALHO - COZINHA", () -> true);
        opcoes[1] = new Opcao("Ver fila de pedidos", () -> true);
        opcoes[2] = new Opcao("Marcar pedido como pronto", () -> true);
        opcoes[3] = new Opcao("Registar atraso", () -> true);
        opcoes[4] = new Opcao("Atualizar display", () -> true);
        opcoes[5] = new Opcao("Voltar", () -> true);

        EcraInput ecra = new EcraInput(
                6,
                opcoes,
                true,
                null,
                s -> s.matches("[12345]"),
                this::processarOpcao
        );
        ecra.draw();
    }

    private void processarOpcao(String input)
    {
        int opcao = Integer.parseInt(input);

        switch (opcao)
        {
            case 1:
                verFilaPedidos();
                break;
            case 2:
                marcarPedidoPronto();
                break;
            case 3:
                registarAtraso();
                break;
            case 4:
                // Refresh
                break;
            case 5:
                continuar = false;
                break;
        }
    }

    private void verFilaPedidos()
    {
        java.util.List<String> fila = cozinhaFacade.getFilaPedidos();

        if(fila.isEmpty())
        {
            ViewHelper.mostrarMensagem("Nenhum pedido em espera.");
        } else {
            ViewHelper.mostrarLista("=== FILA DE PEDIDOS ===", fila);
            ViewHelper.aguardarEnter();
        }
    }

    private void marcarPedidoPronto()
    {
        int numero = ViewHelper.lerInteiro("Numero do pedido:", 1, 99999);

        if (cozinhaFacade.marcarPedidoPronto(numero)) {
            ViewHelper.mostrarMensagem("Pedido marcado como pronto!");
        } else {
            ViewHelper.mostrarMensagem("Erro ao marcar pedido!");
        }
    }

    private void registarAtraso() {
        int numero = ViewHelper.lerInteiro("Numero do pedido:", 1, 99999);
        int minutos = ViewHelper.lerInteiro("Minutos de atraso:", 1, 999);

        cozinhaFacade.registarAtraso(numero, minutos);
        ViewHelper.mostrarMensagem("Atraso registado!");
    }
}