package uminho.grupo18.logicanegocio.pedidos;

import java.util.ArrayList;
import java.util.List;

import uminho.grupo18.data.ElementoDAO;
import uminho.grupo18.data.PratoDAO;

import java.util.ArrayList;
import java.util.List;

public class Elemento
{

    private final int id;
    private int escolhidoIndex = -1;
    private String nomeMenu;

    public Elemento(int id, List<Prato> possibilidades)
    {
        this.id = id;
    }

    public Elemento(int id, String nomeMenu)
    {
        this.id = id;
        this.nomeMenu = nomeMenu;
    }

    public int getId()
    {
        return id;
    }

    public String getNomeMenu()
    {
        return nomeMenu;
    }

    public void setNomeMenu(String nomeMenu)
    {
        this.nomeMenu = nomeMenu;
    }

    public int getEscolhidoIndex()
    {
        return escolhidoIndex;
    }

    public List<Prato> getPossibilidades()
    {
        ElementoDAO dao = ElementoDAO.getInstance();
        return dao.getPossibilidades(this.id);
    }

    public Prato getEscolhido()
    {
        List<Prato> possibilidades = getPossibilidades();
        if(escolhidoIndex < 0 || escolhidoIndex >= possibilidades.size())
            return null;

        return possibilidades.get(escolhidoIndex);
    }

    public void setEscolhido(int indexPrato)
    {
        List<Prato> possibilidades = getPossibilidades();
        if (indexPrato < 0 || indexPrato >= possibilidades.size()) {
            throw new IllegalArgumentException("Índice inválido");
        }
        this.escolhidoIndex = indexPrato;
    }

    public List<String> showPossibilidades()
    {
        List<Prato> possibilidades = getPossibilidades();
        List<String> res = new ArrayList<>();
        for (int i = 0; i < possibilidades.size(); i++) {
            res.add(i + " - " + possibilidades.get(i).showProposta());
        }
        return res;
    }
}