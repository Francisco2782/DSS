package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.pedidos.Elemento;
import uminho.grupo18.logicanegocio.pedidos.Menu;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


import uminho.grupo18.logicanegocio.pedidos.Elemento;
import uminho.grupo18.logicanegocio.pedidos.Prato;

import java.sql.*;
import java.util.*;

public class ElementoDAO implements Map<Integer, Elemento>
{
    private static ElementoDAO singleton = null;

    private ElementoDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = """
                CREATE TABLE IF NOT EXISTS elementos (
                    Id INT PRIMARY KEY AUTO_INCREMENT,
                    Nome_Menu VARCHAR(60) NOT NULL,
                    FOREIGN KEY (Nome_Menu) REFERENCES menus(Nome)
                )""";
            stm.executeUpdate(sql);

            sql = """
                CREATE TABLE IF NOT EXISTS possibilidades (
                    Elemento_Id INT NOT NULL,
                    Nome_Prato VARCHAR(60) NOT NULL,
                    PRIMARY KEY (Elemento_Id, Nome_Prato),
                    FOREIGN KEY (Elemento_Id) REFERENCES elementos(Id),
                    FOREIGN KEY (Nome_Prato) REFERENCES pratos(Nome)
                )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static ElementoDAO getInstance()
    {
        if(ElementoDAO.singleton == null)
            ElementoDAO.singleton = new ElementoDAO();

        return ElementoDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM elementos"))
        {
            if(rs.next())
                i = rs.getInt(1);
        }catch (Exception e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
        return i;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object o)
    {
        int id = (Integer) o;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM elementos WHERE Id=?"))
        {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsValue(Object o)
    {
        Elemento elem = (Elemento) o;
        return this.containsKey(elem.getId());
    }

    @Override
    public Elemento get(Object o)
    {
        int id = (Integer) o;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM elementos WHERE Id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            int escolhidoIndex = rs.getInt("Escolhido_Index");
            List<Prato> possibilidades = getPossibilidades(id);

            Elemento elem = new Elemento(id, possibilidades);
            if(escolhidoIndex >= 0)
                elem.setEscolhido(escolhidoIndex);

            return elem;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Elemento put(Integer key, Elemento elemento)
    {
        Elemento old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps;
            if(old == null){
                ps = conn.prepareStatement("INSERT INTO elementos (Id, Nome_Menu, Escolhido_Index) VALUES (?, ?, ?)");
            }else{
                ps = conn.prepareStatement("UPDATE elementos SET Nome_Menu=?, Escolhido_Index=? WHERE Id=?");
            }

            if(old == null) {
                ps.setInt(1, elemento.getId());
                ps.setString(2, elemento.getNomeMenu());
                ps.setInt(3, elemento.getEscolhidoIndex());
            } else {
                ps.setString(1, elemento.getNomeMenu());
                ps.setInt(2, elemento.getEscolhidoIndex());
                ps.setInt(3, elemento.getId());
            }
            ps.executeUpdate();

            PreparedStatement del = conn.prepareStatement("DELETE FROM possibilidades WHERE Elemento_Id=?");
            del.setInt(1, elemento.getId());
            del.executeUpdate();

            PreparedStatement ins = conn.prepareStatement("INSERT INTO possibilidades VALUES (?,?)");
            for(Prato p : elemento.getPossibilidades()) {
                ins.setInt(1, elemento.getId());
                ins.setString(2, p.getNome());
                ins.executeUpdate();
            }

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public Elemento remove(Object o)
    {
        int id = (Integer) o;
        Elemento old = get(id);
        if(old == null)
            return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement p1 = conn.prepareStatement("DELETE FROM possibilidades WHERE Elemento_Id=?");
            p1.setInt(1, id);
            p1.executeUpdate();

            PreparedStatement p2 = conn.prepareStatement("DELETE FROM elementos WHERE Id=?");
            p2.setInt(1, id);
            p2.executeUpdate();

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Elemento> map)
    {
        map.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM possibilidades");
            st.executeUpdate("DELETE FROM elementos");
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Integer> keySet()
    {
        Set<Integer> keys = new HashSet<>();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Id FROM elementos"))
        {
            while(rs.next())
                keys.add(rs.getInt("Id"));

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    @Override
    public Collection<Elemento> values()
    {
        List<Elemento> res = new ArrayList<>();
        for(Integer id : keySet())
            res.add(get(id));
        return res;
    }

    @Override
    public Set<Entry<Integer, Elemento>> entrySet()
    {
        Set<Entry<Integer, Elemento>> res = new HashSet<>();
        for(Integer id : keySet())
            res.add(Map.entry(id, get(id)));
        return res;
    }

    public List<Prato> getPossibilidades(int elementoId)
    {
        List<Prato> res = new ArrayList<>();
        PratoDAO pratoDAO = PratoDAO.getInstance();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Nome_Prato FROM possibilidades WHERE Elemento_Id=?");
            ps.setInt(1, elementoId);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                String nomePrato = rs.getString("Nome_Prato");
                res.add(pratoDAO.get(nomePrato));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return res;
    }

    public List<Elemento> getElementosByMenu(String nomeMenu)
    {
        List<Elemento> res = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Id FROM elementos WHERE Nome_Menu=?");
            ps.setString(1, nomeMenu);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                int id = rs.getInt("Id");
                res.add(get(id));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return res;
    }

    public void updateEscolhido(int elementoId, int escolhidoIndex)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("UPDATE elementos SET Escolhido_Index=? WHERE Id=?");
            ps.setInt(1, escolhidoIndex);
            ps.setInt(2, elementoId);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}