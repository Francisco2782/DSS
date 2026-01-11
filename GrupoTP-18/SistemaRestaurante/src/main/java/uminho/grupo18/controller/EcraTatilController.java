package uminho.grupo18.controller;

import java.util.List;

import uminho.grupo18.logicanegocio.pedidos.Pedido;
import uminho.grupo18.logicanegocio.pedidos.Proposta;
import uminho.grupo18.logicanegocio.restaurante.Restaurante;

public class EcraTatilController extends Controller {
    private Restaurante restaurante;
    private Pedido pedido;
    private List<Proposta> propostasDisponiveis;

    public EcraTatilController(CadeiaLNFacade cadeiaLN, int restauranteId) {
        super(cadeiaLN);
        this.restaurante = this.cadeiaLN.getRestaurantePorId(restauranteId);
        //this.cadeiaLN = cadeiaLN;
        this.restauranteId = restauranteId;
    }

    public void criarPedidoAtual(boolean takeaway) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void mostrarPropostasDisponiveis() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<String> mostrarAlergenios() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void adicionarPropostaPedido(Pedido pedido, Proposta proposta) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void filtrarAlergenio(String nome) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void adicionarNotaPedido(String texto) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public String pagarFisico() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public String pagarMBWay() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public String confirmarPagamento() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void cancelarPedido() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }
}
