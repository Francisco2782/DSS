package uminho.grupo18.logicanegocio.pedidos;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.restaurante.Balcao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;

public class Pedido {

	private static int contadorGlobal = 0;
	private int numero;
	private boolean esperaEntrega = false;
	private Balcao balcaoEntrega = null;

	private List<Proposta> propostas = new ArrayList<>();
	private Map<Ingrediente, Integer> ingredientesEmPreparacao = null;

	private String nota;

	public Pedido(){
		contadorGlobal++;
		if (contadorGlobal > 9999) {
			contadorGlobal = 1;
		}	
		this.numero = contadorGlobal;
	}

	public addProposta(Proposta proposta) {
		this.propostas.add(proposta);
	}

	public void atribuirBalcao(Balcao balcao) {
		this.balcaoEntrega = balcao;
	}

	public adicionarNota(String nota) {
		this.nota = nota;
	}

	public void setPassoConcluido(Ingrediente ingrediente) {
		if (this.ingredientesEmPreparacao != null && this.ingredientesEmPreparacao.containsKey(ingrediente)) {
			this.ingredientesEmPreparacao.remove(ingrediente);
		}
	}

	public double calcularPrecoTotal() {
		double total = 0.0;
		for (Proposta proposta : this.propostas) {
			total += proposta.getPreco();
		}
		return total;
	}

	public LocalTime calcularTempoPreparacaoEstimado() {
		LocalTime maxTempo = LocalTime.MIN;
		for (Proposta proposta : this.propostas) {
			if (proposta.getTempoMedioPreparacao().isAfter(maxTempo)) {
				maxTempo = proposta.getTempoMedioPreparacao();
			}
		}
		return maxTempo;
	}

	public String getDadosFatura(){
		StringBuilder dados = new StringBuilder();
		dados.append("Pedido Nº: ").append(this.numero).append("\n");
		dados.append("Propostas:\n");
		for (Proposta proposta : this.propostas) {
			dados.append(proposta.showProposta()).append("\n");
		}
		if (this.nota != null) {
			dados.append("Nota: ").append(this.nota).append("\n");
		}
		return dados.toString();
	}

	public String getDadosTalao() {
		StringBuilder dados = new StringBuilder();
		dados.append("Pedido Nº: ").append(this.numero).append("\n");
		dados.append("Balcão de Entrega: ").append(this.balcaoEntrega != null ? this.balcaoEntrega.getIdentificacao() : "Não atribuído").append("\n");
		dados.append("Propostas:\n");
		for (Proposta proposta : this.propostas) {
			dados.append(proposta.showProposta()).append("\n");
		}
		dados.append("Total: " + calcularPrecoTotal() + " Eur\n");
		if (this.nota != null) {
			dados.append("Nota: ").append(this.nota).append("\n");
		}
		return dados.toString();
	}

	public String getDadosQRCode() {
		StringBuilder sb = new StringBuilder();

		int hash = numero;

		sb.append("  +-------------------+\n");
		for (int i = 0; i < 7; i++) {
			sb.append("  | ");
			for (int j = 0; j < 17; j++) {
				int bit = (hash >> ((i * 17 + j) % 16)) & 1;
				sb.append(bit == 1 ? "##" : "  ");
			}
			sb.append(" |\n");
		}
		sb.append("  +-------------------+");

		return sb.toString();
	}

}