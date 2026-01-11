package uminho.grupo18.logicanegocio.gestao;

import uminho.grupo18.data.ArmazemDAO;
import uminho.grupo18.logicanegocio.cozinha.Ingrediente;

import java.util.HashMap;
import java.util.Map;

public class Armazem {

	private int id;
	private String localizacao;
	private Map<Ingrediente, Integer> stock;

    public Armazem(int id, String localizacao)
    {
        this.id = id;
        this.localizacao = localizacao;
        this.stock = new HashMap<>();
    }

    public int getId()
    {
        return this.id;
    }

    public String getLocalizacao()
    {
        return this.localizacao;
    }

	/**
	 * 
	 * @param ingrediente
	 */
	public int getStockIngrediente(Ingrediente ingrediente)
    {
		ArmazemDAO.getInstance().getStockForArmazem(this.id).get()
	}

	/**
	 * 
	 * @param ingrediente
	 * @param quantidade
	 */
	public void addStockIngrediente(Ingrediente ingrediente, int quantidade)
    {
        ArmazemDAO.getInstance().setStock(ingrediente, );
	}

	/**
	 * 
	 * @param ingrediente
	 * @param quantidade
	 */
	public void removerStockIngrediente(Ingrediente ingrediente, int quantidade)
    {
		int current = stock.getOrDefault(ingrediente, 0);
		if(current >= quantidade) {
			stock.put(ingrediente, current - quantidade);
		} else {
			throw new IllegalArgumentException("Stock insuficiente para o ingrediente: " + ingrediente.getNome());
		}
	}

}