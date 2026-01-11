package uminho.grupo18.logicanegocio.cozinha;

import java.util.LinkedList;
import java.util.Queue;

public class Posto {

    private int id;
    private Funcao.TipoFuncao funcao;
    private Posto nextPosto;

    private Queue<Pedido> pedidos = new LinkedList<>();

    public Posto(int id, Funcao.TipoFuncao funcao) {
        this.id = id;
        this.funcao = funcao;
        this.nextPosto = null;
    }

    public void setNextPosto(Posto next) {
        this.nextPosto = next;
    }

    public void adicionarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public Pedido getProximoPedido() {
        return pedidos.peek();
    }

    public void completarPasso() {
        Pedido pedido = pedidos.poll();
        if (pedido == null) return;

        // comportamento depende da função
        if (funcao == Funcao.TipoFuncao.PASSO_CONFECAO) {
            // completar passo de confecção, talvez marcar ingredientes como preparados
            // por exemplo, para cada ingrediente no pedido, setPassoConcluido
            // mas aqui simplificado
        } else { // empratamento/entrega
            // pedido pronto para o próximo posto ou entrega
            if (nextPosto != null) {
                nextPosto.adicionarPedido(pedido);
            } else {
                // marcar como pronto para entrega
                // pedido.setEsperaEntrega(true); // se houvesse setter
            }
        }
    }
}
