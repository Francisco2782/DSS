package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.pedidos.Alergenio;

import java.sql.*;
import java.util.*;

public class AlergenioDAO implements Map<String, Alergenio>
{
    private static AlergenioDAO singleton = null;

    private AlergenioDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS alergenios (" +
                    "Nome varchar(60) NOT NULL PRIMARY KEY)";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static AlergenioDAO getInstance()
    {
        if(AlergenioDAO.singleton == null)
            AlergenioDAO.singleton = new AlergenioDAO();

        return AlergenioDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM alergenios")) 
        {
            if(rs.next()) {
                i = rs.getInt(1);
            }
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
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM alergenios WHERE Nome = ?"))
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
        Alergenio id = (Alergenio)o;
        return this.containsKey(id.getNome());
    }

    @Override
    public Alergenio get(Object key)
    {
        Alergenio res;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM alergenios WHERE Nome = ?"))
        {
            pstm.setString(1, key.toString());
            try(ResultSet rs = pstm.executeQuery())
            {
                if(rs.next())
                    res = new Alergenio(rs.getString("Nome"));
                else
                    res = null;
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }

    @Override
    public Alergenio put(String key, Alergenio value)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO alergenios VALUES(?) " +
                    "ON DUPLICATE KEY UPDATE Nome=Values(Nome))")
            )
        {
            pstm.setString(1, key.toString());

            pstm.executeUpdate();
            return value;

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Alergenio remove(Object key)
    {
        Alergenio res;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("DELETE FROM alergenios WHERE Nome = ?"))
        {
            pstm.setString(1, key.toString());
            res = this.get(key);
            pstm.executeUpdate();
            return res;

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends Alergenio> m)
    {
        m.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("TRUNCATE alergenios"))
        {
            pstm.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public Set<String> keySet()
    {
        Set<String> res = new HashSet<>();
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT Nome FROM alergenios"))
        {
            try(ResultSet rs = pstm.executeQuery())
            {
                while(rs.next())
                {
                    String numAtual = rs.getString("Nome");
                    res.add(numAtual);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }

    @Override
    public Collection<Alergenio> values()
    {
        Collection<Alergenio> res = new HashSet<>();
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT Nome FROM alergenios"))
        {
            try(ResultSet rs = pstm.executeQuery())
            {
                while(rs.next())
                {
                    String nome = rs.getString("Nome");

                    Alergenio alergenioAtual = new Alergenio(nome);
                    res.add(alergenioAtual);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }

    @Override
    public Set<Entry<String, Alergenio>> entrySet()
    {
        Set<Entry<String, Alergenio>> res = new HashSet<>();
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT Nome FROM alergenios")
           )
        {
            try(ResultSet rs = pstm.executeQuery())
            {
                while(rs.next())
                {
                    String nomeAtual = rs.getString("Nome");

                    Alergenio alergenioAtual = new Alergenio(nomeAtual);
                    res.add(new AbstractMap.SimpleEntry<>(nomeAtual, alergenioAtual));
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

        return res;
    }
}
