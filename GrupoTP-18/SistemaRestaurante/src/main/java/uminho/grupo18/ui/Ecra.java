package uminho.grupo18.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.valueOf;

public class Ecra
{

	private List<Integer> opcoesValidas = new ArrayList<>();
    private final Opcao[] opcoes;
	private boolean opcoesNumeradas;
    private Ecra proximoEcra;

	/**
	 * 
	 * @param numOpcoes
	 * @param opcoes
	 * @param opNumeradas
	 * @param proximoEcra
	 */
	public Ecra(int numOpcoes, Opcao[] opcoes, boolean opNumeradas, Ecra proximoEcra)
    {
        this.opcoes = new Opcao[numOpcoes];
        for(int i=0; i<numOpcoes; i++)
        {
            this.opcoes[i] = opcoes[i].clone();
        }
        this.opcoesNumeradas = opNumeradas;
        this.proximoEcra = proximoEcra;
	}

	/**
	 * 
	 * @param original
	 */
	public Ecra(Ecra original)
    {
        this.opcoes = new Opcao[original.opcoes.length];
        for(int i=0; i < original.opcoes.length; i++)
        {
            this.opcoes[i] = original.opcoes[i].clone();
        }
        this.opcoesNumeradas = original.opcoesNumeradas;
        this.proximoEcra = original.proximoEcra;
	}

	public Ecra clone()
    {
        return new Ecra(this);
	}

    public void verificaOpcoesValidas()
    {
        this.opcoesValidas = IntStream.range(0, opcoes.length)
                                    .filter(indice -> opcoes[indice].isValid())
                                    .boxed()
                                    .collect(Collectors.toList());
    }

	public void draw()
    {
        verificaOpcoesValidas();
        List<String> opcoesAMostrar = opcoesValidas.stream().map(indice -> opcoes[indice].getTexto())
                                                            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        int acumulador = 1;
        for(String opcao : opcoesAMostrar)
        {
            if(opcoesNumeradas)
                sb.append(acumulador).append(": ");

            sb.append(opcao).append("\n");
        }

        System.out.println(sb.toString());
	}

	public Ecra getProximoEcra()
    {
        return this.proximoEcra.clone();
	}

	/**
	 * 
	 * @param idOpcao
	 */
	public void updateOpcaoAt(int idOpcao, String texto)
    {
        opcoes[idOpcao].setTexto(texto);
	}

}