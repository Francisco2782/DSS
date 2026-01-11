package uminho.grupo18.ui;

import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EcraInput extends Ecra
{
    private Predicate<String> validacaoInput;
    private Consumer<String> acaoInput;

    public EcraInput(int numOpcoes, Opcao[] opcoes, boolean opNumeradas, Ecra proximoEcra, Predicate<String> validacao, Consumer<String> acao)
    {
        super(numOpcoes, opcoes, opNumeradas, proximoEcra);
        this.validacaoInput = validacao;
        this.acaoInput = acao;
    }

    public EcraInput(EcraInput original)
    {
        super(original);
        this.validacaoInput = original.validacaoInput;
        this.acaoInput = original.acaoInput;
    }

    public void draw()
    {
        super.draw();
        Scanner scanner = new Scanner(System.in);
        processarInput(scanner);
    }

    private void processarInput(Scanner scanner)
    {
        String input = scanner.nextLine();
        if(!validacaoInput.test(input))
        {
            System.err.println("INPUT INVÁLIDO");
            processarInput(scanner);
        }else{
            if(acaoInput != null)
                acaoInput.accept(input);
        }
    }

    public EcraInput clone()
    {
        return new EcraInput(this);
    }
}