package uminho.grupo18.ui.view;

import uminho.grupo18.ui.*;
import ModelacaoEstrutural.facade.PedidoFacade;

public class DisplayBalcaoView {
    private PedidoFacade pedidoFacade;
    private int balcaoID;
    private boolean continuar;

    public DisplayBalcaoView(PedidoFacade facade, int balcaoID) {
        this.pedidoFacade = facade;
        this.balcaoID = balcaoID;
        this.continuar = true;
    }

    public void iniciar() {
        while (continuar) {
            ViewHelper.limparEcra();
            construirEcraBalcao();
        }
    }

    private void construirEcraBalcao() {
        Opcao[] opcoes = new Opcao[4];
        opcoes[0] = new Opcao("DISPLAY BALCAO #" + balcaoID, () -> true);
        opcoes[1] = new Opcao("Ver pedidos prontos", () -> true);
        opcoes[2] = new Opcao("Atualizar display", () -> true);
        opcoes[3] = new Opcao("Voltar", () -> true);

        EcraInput ecra = new EcraInput(
                4,
                opcoes,
                true,
                null,
                s -> s.matches("[123]"),
                this::processarOpcao
        );
        ecra.draw();
    }

    private void processarOpcao(String input) {
        int opcao = Integer.parseInt(input);

        switch (opcao) {
            case 1:
                mostrarPedidosProntos();
                break;
            case 2:
                // Refresh - voltará ao loop
                break;
            case 3:
                continuar = false;
                break;
        }
    }

    private void mostrarPedidosProntos() {
        java.util.List<String> pedidos = pedidoFacade.getPedidosProntosPorBalcao(balcaoID);

        if (pedidos.isEmpty()) {
            ViewHelper.mostrarMensagem("Nenhum pedido pronto no momento.");
        } else {
            ViewHelper.mostrarLista("=== PEDIDOS PRONTOS - BALCAO #" + balcaoID + " ===", pedidos);
            ViewHelper.aguardarEnter();
        }
    }
}