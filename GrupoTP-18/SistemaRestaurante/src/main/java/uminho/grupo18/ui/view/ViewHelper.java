package uminho.grupo18.ui.view;

import uminho.grupo18.ui.*;
import java.util.List;
import java.util.function.Consumer;

public class ViewHelper
{

    public static void limparEcra()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void mostrarMensagem(String mensagem)
    {
        Opcao[] opcoes = new Opcao[]{new Opcao(mensagem, () -> true)};
        Ecra ecra = new Ecra(1, opcoes, false, null);
        ecra.draw();
        aguardarEnter();
    }

    public static void aguardarEnter()
    {
        EcraInput input = new EcraInput(
                1,
                new Opcao[]{new Opcao("\nPressione Enter para continuar...", () -> true)},
                false,
                null,
                s -> true,
                s -> {}
        );
        input.draw();
    }

    public static boolean confirmarSimNao(String pergunta)
    {
        boolean[] resultado = {false};

        EcraInput input = new EcraInput
        (
                1,
                new Opcao[]{new Opcao(pergunta + " (S/N):", () -> true)},
                false,
                null,
                s -> s.toUpperCase().matches("[SN]"),
                s -> resultado[0] = s.toUpperCase().equals("S")
        );
        input.draw();

        return resultado[0];
    }

    public static String lerTexto(String prompt)
    {
        String[] resultado = {""};

        EcraInput input = new EcraInput
        (
                1,
                new Opcao[]{new Opcao(prompt, () -> true)},
                false,
                null,
                s -> true,
                s -> resultado[0] = s
        );
        input.draw();

        return resultado[0];
    }

    public static int lerInteiro(String prompt, int min, int max)
    {
        int[] resultado = {min};

        EcraInput input = new EcraInput(
                1,
                new Opcao[]{new Opcao(prompt, () -> true)},
                false,
                null,
                s ->
                {
                    try{
                        int val = Integer.parseInt(s);
                        return val >= min && val <= max;
                    }catch (NumberFormatException e){
                        return false;
                    }
                },
                s -> resultado[0] = Integer.parseInt(s)
        );
        input.draw();

        return resultado[0];
    }

    public static void mostrarLista(String titulo, List<String> itens)
    {
        Opcao[] opcoes = new Opcao[itens.size() + 1];
        opcoes[0] = new Opcao(titulo, () -> true);

        for(int i = 0; i < itens.size(); i++)
            opcoes[i + 1] = new Opcao(itens.get(i), () -> true);

        Ecra ecra = new Ecra(opcoes.length, opcoes, true, null);
        ecra.draw();
    }

    public static void mostrarTitulo(String titulo)
    {
        Opcao[] opcoes = new Opcao[3];
        opcoes[0] = new Opcao("================================================================================", () -> true);
        opcoes[1] = new Opcao(centrarTexto(titulo, 80), () -> true);
        opcoes[2] = new Opcao("================================================================================", () -> true);

        Ecra ecra = new Ecra(3, opcoes, false, null);
        ecra.draw();
    }

    private static String centrarTexto(String texto, int largura)
    {
        int padding = (largura - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < padding; i++)
            sb.append(" ");

        sb.append(texto);
        return sb.toString();
    }
}