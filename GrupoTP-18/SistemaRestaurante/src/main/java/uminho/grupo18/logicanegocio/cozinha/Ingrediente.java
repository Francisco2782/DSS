package uminho.grupo18.logicanegocio.cozinha;

import uminho.grupo18.logicanegocio.pedidos.Alergenio;

import java.time.LocalTime;
import java.util.List;

public class Ingrediente {

	private String nome;
	private LocalTime tempoDeEspera;
	private List<Alergenio> alergenios = null;

	public Ingrediente(String nome, LocalTime tempoDeEspera, List<Alergenio> alergenios) {
		this.nome = nome;
		this.tempoDeEspera = tempoDeEspera;
		this.alergenios = alergenios;
	}

	public String getNome() {
		return this.nome;
	}

	/**
	 * 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalTime getTempoDeEspera() {
		return this.tempoDeEspera;
	}

	/**
	 * 
	 * @param tempoDeEspera
	 */
	public void setTempoDeEspera(LocalTime tempoDeEspera) {
		this.tempoDeEspera = tempoDeEspera;
	}

	public List<String> getAlergenios() {
		return this.alergenios.stream().map(Alergenio::getNome).collect(Collectors.toList());
	}

}