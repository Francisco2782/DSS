package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.autenticacao.Utilizador;

import java.sql.*;
import java.util.*;

public class UtilizadorDAO implements Map<String, Utilizador>
{
    private static UtilizadorDAO singleton = null;

    private UtilizadorDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = """
                CREATE TABLE IF NOT EXISTS utilizadores (
                    Nome VARCHAR(60) PRIMARY KEY,
                    Password VARCHAR(255) NOT NULL,
                    Restaurante_Id INT,
                    Coo TINYINT(1) DEFAULT 0,
                    FOREIGN KEY (Restaurante_Id) REFERENCES restaurantes(Id)
                )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static UtilizadorDAO getInstance()
    {
        if(UtilizadorDAO.singleton == null)
            UtilizadorDAO.singleton = new UtilizadorDAO();

        return UtilizadorDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM utilizadores"))
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
        String nome = o.toString();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            PreparedStatement pstm = conn.prepareStatement("SELECT 1 FROM utilizadores WHERE Nome = ?"))
        {
            pstm.setString(1, nome);
            try(ResultSet rs = pstm.executeQuery())
            {
                return rs.next();
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public boolean containsValue(Object o)
    {
        Utilizador u = (Utilizador) o;
        return this.containsKey(u.getNome());
    }

    @Override
    public Utilizador get(Object key)
    {
        String nome = key.toString();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM utilizadores WHERE Nome=?");
            ps.setString(1, nome);

            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return null;

            int restauranteId = rs.getInt("Restaurante_Id");
            String restaurante = rs.wasNull() ? null : String.valueOf(restauranteId);

            return new Utilizador(
                    rs.getString("Nome"),
                    rs.getString("Password"),
                    restaurante,
                    rs.getBoolean("Coo")
            );
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Utilizador put(String key, Utilizador u)
    {
        Utilizador old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("REPLACE INTO utilizadores (Nome, Password, Restaurante_Id, Coo) VALUES (?, ?, ?, ?)");
            ps.setString(1, u.getNome());
            ps.setString(2, u.getPassword());

            if(u.getRestaurante() == null)
                ps.setNull(3, Types.INTEGER);
            else
                ps.setInt(3, Integer.parseInt(u.getRestaurante()));

            ps.setBoolean(4, u.isCoo());
            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public Utilizador remove(Object key)
    {
        String nome = key.toString();
        Utilizador old = get(nome);
        if(old == null) return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM utilizadores WHERE Nome=?");
            ps.setString(1, nome);
            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Utilizador> map)
    {
        map.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM utilizadores");
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
            ResultSet rs = st.executeQuery("SELECT Nome FROM utilizadores"))
        {
            while(rs.next())
                keys.add(rs.getString("Nome"));

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    @Override
    public Collection<Utilizador> values()
    {
        List<Utilizador> res = new ArrayList<>();
        for(String nome : keySet())
            res.add(get(nome));
        return res;
    }

    @Override
    public Set<Entry<String, Utilizador>> entrySet()
    {
        Set<Entry<String, Utilizador>> res = new HashSet<>();
        for(String nome : keySet())
            res.add(Map.entry(nome, get(nome)));
        return res;
    }

    // Helper methods

    /**
     * Authenticate a user by checking username and password
     * @param nome Username
     * @param password Password
     * @return Utilizador if credentials are valid, null otherwise
     */
    public Utilizador authenticate(String nome, String password)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM utilizadores WHERE Nome=? AND Password=?");
            ps.setString(1, nome);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return null;

            int restauranteId = rs.getInt("Restaurante_Id");
            String restaurante = rs.wasNull() ? null : String.valueOf(restauranteId);

            return new Utilizador(
                    rs.getString("Nome"),
                    rs.getString("Password"),
                    restaurante,
                    rs.getBoolean("Coo")
            );

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean updateCooStatus(String nome, boolean isCoo)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("UPDATE utilizadores SET Coo=? WHERE Nome=?");
            ps.setBoolean(1, isCoo);
            ps.setString(2, nome);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}