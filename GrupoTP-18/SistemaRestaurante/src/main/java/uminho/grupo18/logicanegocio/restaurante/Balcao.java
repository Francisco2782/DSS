package uminho.grupo18.logicanegocio.restaurante;

public class Balcao {

    private int id;
    private int numeroPedidosEntregar = 0;

    public Balcao(int id) {
        this.id = id;
    }

    public int getNumeroPedidosEntregar() {
        return numeroPedidosEntregar;
    }

    public void setNumeroPedidosEntregar(int numeroPedidosEntregar) {
        this.numeroPedidosEntregar = numeroPedidosEntregar;
    }
}
