package uminho.grupo18.logicanegocio.cozinha;

import uminho.grupo18.data.IngredienteDAO;
import uminho.grupo18.logicanegocio.pedidos.Pedido;

public class CozinhaFacade implements ICozinha {

	/**
	 * 
	 * @param pedido
	 */
	public void realizarPedido(Pedido pedido) {
		// adicionar pedido ao primeiro posto da cozinha
		// por exemplo, posto inicial
	}

	/**
	 * 
	 * @param pedido
	 * @param novaPosicao
	 * @param novoTempo
	 */
	public void atrasarPedido(Pedido pedido, int novaPosicao, int novoTempo) {
		// atrasar pedido, talvez reordenar fila
	}

	/**
	 * 
	 * @param ingrediente
	 */
	public void pedirIngrediente(Ingrediente ingrediente) {
		// pedir ingrediente, talvez adicionar ao stock via DAO
		IngredienteDAO dao = IngredienteDAO.getInstance();
		dao.put(ingrediente.getNome(), ingrediente);
	}

	/**
	 * 
	 * @param numeroEtapa
	 */
	public void registarEtapaCompleta(int numeroEtapa) {
		// registar que uma etapa foi completada
	}

	/**
	 * 
	 * @param pedido
	 */
	public void fazerEntrega(Pedido pedido) {
		// fazer entrega do pedido
	}

	/**
	 * 
	 * @param pedido
	 */
	public void registarPedidoCompleto(Pedido pedido) {
		// registar pedido como completo
	}

}