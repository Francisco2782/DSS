package uminho.grupo18.logicanegocio.pedidos;

public interface IPedido {

	/**
	 * 
	 * @param numero
	 * @param artigos
	 * @param takeaway
	 */
	void criarPedido(int numero, List<Proposta> artigos, boolean takeaway);

	/**
	 * 
	 * @param pedido
	 */
	String emitirTalaoBalcao(Pedido pedido);

	/**
	 * 
	 * @param pedido
	 */
	String emitirFatura(Pedido pedido);

	/**
	 * 
	 * @param pedido
	 */
	String emitirQRCode(Pedido pedido);

	/**
	 * 
	 * @param pedido
	 */
	int atribuirBalcao(Pedido pedido);

	/**
	 * 
	 * @param nome
	 */
	List<Proposta> filtrarAlergenio(String nome);

	List<Proposta> mostrarPropostas();

	List<Alergenios> mostrarAlergenios();

}