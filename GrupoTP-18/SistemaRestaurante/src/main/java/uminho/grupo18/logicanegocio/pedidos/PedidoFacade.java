package uminho.grupo18.logicanegocio.pedidos;

import java.util.List;

import uminho.grupo18.data.PratoDAO;
import uminho.grupo18.data.MenuDAO;
import uminho.grupo18.data.ElementoDAO;
import uminho.grupo18.data.AlergenioDAO;

public class PedidoFacade implements IPedido {

	private static PedidoFacade singleton = null;

	private ElementoDAO elementosDisponiveis = ElementoDAO.getInstance();
	private PratoDAO pratosDisponiveis = PratoDAO.getInstance();
	private MenuDAO menusDisponiveis = MenuDAO.getInstance();
	private AlergenioDAO alergeniosEncontrados = AlergenioDAO.getInstance();

	public static PedidoFacade getInstance()
    {
        if(singleton == null)
            singleton = new PedidoFacade();
        return singleton;
    }

	private PedidoFacade() {
	}

	/**
	 * 
	 * @param restaurante
	 * @param takeaway
	 */
	public Pedido criarPedido(Restaurante restaurante, boolean takeaway) {
		restaurante.criarPedido(takeaway);
		return restaurante.getPedidoAtual();
	}

	/**
	 * 
	 * @param pedido
	 */
	public String emitirTalaoBalcao(Pedido pedido) {
		return pedido.getDadosTalao();
	}

	/**
	 * 
	 * @param pedido
	 */
	public String emitirFatura(Pedido pedido) {
		return pedido.getDadosFatura();
	}

	/**
	 * 
	 * @param pedido
	 */
	public String emitirQRCode(Pedido pedido) {
		return pedido.getDadosQRCode();
	}

	/**
	 * 
	 * @param pedido
	 */
	public int atribuirBalcao(Pedido pedido) {
		pedido.atribuirBalcao(null);
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param nomesAlergenios
	 */
	public List<Proposta> filtrarAlergenios(Set<String> nomesAlergenios) {
		List<Proposta> propostasFiltradas = new ArrayList<>();
		propostasFiltradas.addAll(elementosDisponiveis.filtrarAlergenios(nomesAlergenios));
		propostasFiltradas.addAll(pratosDisponiveis.filtrarAlergenios(nomesAlergenios));
		propostasFiltradas.addAll(menusDisponiveis.filtrarAlergenios(nomesAlergenios));
		return propostasFiltradas;
	}

}