package uminho.grupo18.logicanegocio.autenticacao;

public class Utilizador {

	private String nome;
	private String password;
	private String restaurante;
	private boolean coo;

	/**
	 * 
	 * @param nome
	 * @param password
	 * @param restaurante
	 * @param coo
	 */
	public Utilizador(String nome, String password, String restaurante, boolean coo) {
		this.nome = nome;
		this.password = password;
		this.restaurante = restaurante;
		this.coo = coo;
	}

	public String getRestaurante() {
		return this.restaurante;
	}

	public String getNome() {
		return this.nome;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isCoo() {return this.coo;}

}