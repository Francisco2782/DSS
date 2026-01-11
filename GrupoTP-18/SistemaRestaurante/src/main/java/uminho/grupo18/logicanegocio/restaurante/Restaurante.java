package uminho.grupo18.logicanegocio.restaurante;

public class Restaurante
{
    private final int id;
    private String localizacao;
    private int idPedidoAtual;
    private int idPostoAtual;

    public Restaurante(int id, String localizacao)
    {
        this.id = id;
        this.localizacao = localizacao;
        this.idPedidoAtual = 0;
        this.idPostoAtual = 0;
    }

    public Restaurante(int id, String localizacao, int idPedidoAtual, int idPostoAtual)
    {
        this.id = id;
        this.localizacao = localizacao;
        this.idPedidoAtual = idPedidoAtual;
        this.idPostoAtual = idPostoAtual;
    }

    public int getId()
    {
        return id;
    }

    public String getLocalizacao()
    {
        return localizacao;
    }

    public void setLocalizacao(String localizacao)
    {
        this.localizacao = localizacao;
    }

    public int getIdPedidoAtual()
    {
        return idPedidoAtual;
    }

    public void setIdPedidoAtual(int idPedidoAtual)
    {
        this.idPedidoAtual = idPedidoAtual;
    }

    public int getIdPostoAtual()
    {
        return idPostoAtual;
    }

    public void setIdPostoAtual(int idPostoAtual)
    {
        this.idPostoAtual = idPostoAtual;
    }
}