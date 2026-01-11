package uminho.grupo18.logicanegocio.pedidos;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Proposta {

    private String nome;
    private float preco; // preço de venda
    private float custo; // custo de produção
	private int tamanho; // 0 nao tem, 1 medio, 2 grande
    private LocalTime tempoMedioPreparacao;
    private List<Ingrediente> ingredientes;

    public Proposta(String nome,
                    float preco,
                    float custo,
                    LocalTime tempoMedioPreparacao,
                    int tamanho) {

        this.nome = nome;
        this.preco = preco;
        this.custo = custo;
        this.tempoMedioPreparacao = tempoMedioPreparacao;
        this.tamanho = tamanho;
        this.ingredientes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public float getCusto() {
        return custo;
    }

    public void setCusto(float custo) {
        this.custo = custo;
    }

    public LocalTime getTempoMedioPreparacao() {
        return tempoMedioPreparacao;
    }

    public void setTempoMedioPreparacao(LocalTime tempoMedioPreparacao) {
        this.tempoMedioPreparacao = tempoMedioPreparacao;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void addIngrediente(Ingrediente ingrediente) {
        this.ingredientes.add(ingrediente);
    }

    public void removeIngrediente(Ingrediente ingrediente) {
        this.ingredientes.remove(ingrediente);
    }

    public String showProposta() {
		return nome + " - " + preco + " EUR";
	}

}
