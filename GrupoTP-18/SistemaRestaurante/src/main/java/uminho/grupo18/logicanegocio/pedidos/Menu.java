package uminho.grupo18.logicanegocio.pedidos;

import uminho.grupo18.data.ElementoDAO;
import uminho.grupo18.logicanegocio.cozinha.Ingrediente;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Menu extends Proposta
{
    private List<Elemento> selecionados;

    public Menu(String nome, float preco, float custo, LocalTime tempoMedioPreparacao, int tamanho)
    {
        super(nome, preco, custo, tempoMedioPreparacao, tamanho);
        this.selecionados = null;
    }

    public List<Elemento> getElementosSelecionados()
    {
        if(selecionados == null) {
            ElementoDAO elementoDAO = ElementoDAO.getInstance();
            selecionados = elementoDAO.getElementosByMenu(this.getNome());
        }
        return selecionados;
    }

    public void selecionaElemento(Elemento elemento)
    {
        if(selecionados == null)
            selecionados = new ArrayList<>();

        selecionados.add(elemento);
    }

    public Prato getPratoEscolhido(int elemento)
    {
        ElementoDAO elementoDAO = ElementoDAO.getInstance();

        if(elemento < 0 || elemento >= selecionados.size()) {
            throw new IllegalArgumentException("Elemento inválido");
        }
        return selecionados.get(elemento).getEscolhido();
    }

    @Override
    public List<Ingrediente> getIngredientes()
    {
        List<Ingrediente> ingredientes = new ArrayList<>();

        for (Elemento e : selecionados)
        {
            Prato p = e.getEscolhido();
            if (p != null) {
                ingredientes.addAll(p.getIngredientes());
            }
        }
        return ingredientes;
    }

    @Override
    public String showProposta() {
        StringBuilder sb = new StringBuilder();
		sb.append("Menu:\n");
		for (Elemento elem : selecionados){
			sb.append("   - " + elem.getEscolhido().getNome() + " -> " + elem.getEscolhido().getPreco() + "\n");
		}
		sb.append("Total: " + this.getPreco() + " EUR");
		return sb.toString();
    }
}
