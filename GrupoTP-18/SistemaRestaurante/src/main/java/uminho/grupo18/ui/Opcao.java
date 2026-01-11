package uminho.grupo18.ui;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Opcao
{

	private String texto;
	private BooleanSupplier condition;

	/**
	 * 
	 * @param textoBase
	 * @param cond
	 */
	public Opcao(String textoBase, BooleanSupplier cond)
    {
		this.texto = textoBase;
        this.condition = cond;
	}

    public Opcao(Opcao original)
    {
        this.texto = original.texto;
        this.condition = original.condition;
    }

    public Opcao clone()
    {
        return new Opcao(this);
    }

	public boolean isValid()
    {
		return condition.getAsBoolean();
	}

	public String getTexto() {
		return this.texto;
	}

	/**
	 * 
	 * @param texto
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

}