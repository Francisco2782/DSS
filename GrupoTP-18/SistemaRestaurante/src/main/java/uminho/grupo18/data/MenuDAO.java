package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.pedidos.Menu;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class MenuDAO implements Map<String, Menu>
{
    private static MenuDAO singleton = null;

    private MenuDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = """
                CREATE TABLE IF NOT EXISTS menus (
                    Nome VARCHAR(60) NOT NULL PRIMARY KEY,
                    Preco FLOAT NOT NULL,
                    Custo FLOAT NOT NULL,
                    Tempo_Medio_Preparacao TIME NOT NULL,
                    Tamanho INT NOT NULL
                )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static MenuDAO getInstance()
    {
        if(MenuDAO.singleton == null)
            MenuDAO.singleton = new MenuDAO();

        return MenuDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM menus"))
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
        boolean res;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM menus WHERE Nome = ?"))
        {
            pstm.setString(1, o.toString());
            try(ResultSet rs = pstm.executeQuery())
            {
                res = rs.next();
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }

    @Override
    public boolean containsValue(Object o)
    {
        Menu menu = (Menu) o;
        return this.containsKey(menu.getNome());
    }

    @Override
    public Menu get(Object key)
    {
        String nome = key.toString();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM menus WHERE Nome = ?");
            pstm.setString(1, nome);

            ResultSet rs = pstm.executeQuery();

            if(rs.next())
            {
                return new Menu(
                        rs.getString("Nome"),
                        rs.getFloat("Preco"),
                        rs.getFloat("Custo"),
                        rs.getTime("Tempo_Medio_Preparacao").toLocalTime(),
                        rs.getInt("Tamanho")
                );
            }
            else
            {
                return null;
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Menu put(String key, Menu menu)
    {
        Menu old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("REPLACE INTO menus VALUES (?,?,?,?,?)");
            ps.setString(1, menu.getNome());
            ps.setFloat(2, menu.getPreco());
            ps.setFloat(3, menu.getCusto());
            ps.setTime(4, Time.valueOf(menu.getTempoMedioPreparacao()));
            ps.setInt(5, menu.getTamanho());
            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public Menu remove(Object key)
    {
        String nome = key.toString();
        Menu old = get(nome);
        if(old == null)
            return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM possibilidades WHERE Elemento_Id IN (SELECT Id FROM elementos WHERE Nome_Menu=?)");
            ps1.setString(1, nome);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM elementos WHERE Nome_Menu=?");
            ps2.setString(1, nome);
            ps2.executeUpdate();

            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM menus WHERE Nome=?");
            ps3.setString(1, nome);
            ps3.executeUpdate();

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Menu> map)
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
            st.executeUpdate("DELETE FROM menus");
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> keySet()
    {
        Set<String> keys = new HashSet<>();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Nome FROM menus"))
        {
            while(rs.next())
                keys.add(rs.getString("Nome"));

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    @Override
    public Collection<Menu> values()
    {
        List<Menu> res = new ArrayList<>();
        for(String nome : keySet())
            res.add(get(nome));
        return res;
    }

    @Override
    public Set<Entry<String, Menu>> entrySet()
    {
        Set<Entry<String, Menu>> res = new HashSet<>();
        for(String nome : keySet())
            res.add(Map.entry(nome, get(nome)));
        return res;
    }
}