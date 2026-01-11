package uminho.grupo18.logicanegocio.autenticacao;

import uminho.grupo18.data.UtilizadorDAO;

public class AutenticacaoFacade implements IAutenticacao {

	/**
	 * 
	 * @param nome
	 * @param password
	 * @param restaurante
	 * @param coo
	 */
	public boolean registarGestor(String nome, String password, String restaurante, boolean coo) {
		Utilizador u = new Utilizador(nome, password, restaurante, coo);
		UtilizadorDAO dao = UtilizadorDAO.getInstance();
		dao.put(nome, u);
		return true;
	}

	/**
	 * 
	 * @param userName
	 * @param pass
	 */
	public boolean login(String userName, String pass) {
		UtilizadorDAO dao = UtilizadorDAO.getInstance();
		Utilizador u = dao.get(userName);
		if (u != null && u.getPassword().equals(pass)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param utilizador
	 */
	public boolean isCOO(Utilizador utilizador) {
		return utilizador.isCoo();
	}

}