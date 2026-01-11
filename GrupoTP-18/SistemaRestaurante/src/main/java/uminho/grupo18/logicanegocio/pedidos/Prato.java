package uminho.grupo18.logicanegocio.pedidos;

import uminho.grupo18.data.IngredienteDAO;
import uminho.grupo18.data.PratoDAO;
import uminho.grupo18.logicanegocio.cozinha.Ingrediente;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Prato extends Proposta
{
    List<Ingrediente> ingredientesExtra = new ArrayList<>();
    List<Ingrediente> ingredientesARemover = new ArrayList<>();

    public Prato(String nome, float preco, float custo, LocalTime tempoMedioPreparacao, int tamanho)
    {
        super(nome, preco, custo, tempoMedioPreparacao, tamanho);
    }

    public List<Ingrediente> getIngredientes() {
        return PratoDAO.getInstance().getIngredientes(this.getNome());
    }

    @Override
    public String showProposta() {
        return getNome() + " - " + getPreco() + " EUR";
    }
}
