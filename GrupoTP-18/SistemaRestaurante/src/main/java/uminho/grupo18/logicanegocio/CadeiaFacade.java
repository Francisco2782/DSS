package uminho.grupo18.logicanegocio;

import uminho.grupo18.logicanegocio.autenticacao.AutenticacaoFacade;
import uminho.grupo18.logicanegocio.pedidos.Pedido;
import uminho.grupo18.logicanegocio.pedidos.Proposta;

import java.util.List;

public class CadeiaFacade {

	private AutenticacaoFacade auth = new AutenticacaoFacade();

	/**
	 * 
	 * @param userName
	 * @param pass
	 */
	public boolean login(String userName, String pass) {
		return auth.login(userName, pass);
	}

	/**
	 * 
	 * @param nome
	 * @param password
	 * @param restaurante
	 * @param coo
	 */
	public boolean registarGestor(String nome, String password, String restaurante, boolean coo) {
		return auth.registarGestor(nome, password, restaurante, coo);
	}

	/**
	 * 
	 * @param numero
	 * @param artigos
	 * @param takeaway
	 * @param restauranteID
	 */
	public void criarPedido(int numero, List<Proposta> artigos, boolean takeaway, int restauranteID) {
		Pedido pedido = new Pedido();
		// definir numero se necessario, mas Pedido ja tem contador
		for (Proposta proposta : artigos) {
			pedido.addProposta(proposta);
		}
		// talvez definir takeaway e restaurante
		// depois delegar para cozinha ou algo
	}

}