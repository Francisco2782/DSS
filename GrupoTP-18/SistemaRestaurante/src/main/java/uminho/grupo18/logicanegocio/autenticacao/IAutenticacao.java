package uminho.grupo18.logicanegocio.autenticacao;

public interface IAutenticacao {

	/**
	 * 
	 * @param userName
	 * @param pass
	 */
	boolean login(String userName, String pass);

	/**
	 * 
	 * @param utilizador
	 */
	boolean isCOO(Utilizador utilizador);

	/**
	 * 
	 * @param nome
	 * @param password
	 * @param restaurante
	 * @param coo
	 */
	boolean registarGestor(String nome, String password, String restaurante, bool coo);

}