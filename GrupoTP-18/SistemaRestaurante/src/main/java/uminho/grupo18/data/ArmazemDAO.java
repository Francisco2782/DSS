package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.gestao.Armazem;
import uminho.grupo18.logicanegocio.pedidos.Alergenio;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class ArmazemDAO implements Map<Integer, Armazem>

{
    private static ArmazemDAO singleton = null;

    private ArmazemDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS armazens(" +
                "Id INT PRIMARY KEY," +
                "Localizacao VARCHAR(100) NOT NULL)";
            stm.executeUpdate(sql);
            sql ="""
            CREATE TABLE IF NOT EXISTS armazemIngredientes(
                Armazem_Id INT NOT NULL,
                Nome_Ingrediente VARCHAR(60) NOT NULL,
                Quantidade INT NOT NULL,
                PRIMARY KEY (Armazem_Id, Nome_Ingrediente),
                FOREIGN KEY (Armazem_Id) REFERENCES armazens(Id),
                FOREIGN KEY (Nome_Ingrediente) REFERENCES ingredientes(Nome)
            )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public static ArmazemDAO getInstance()
    {
        if(ArmazemDAO.singleton == null)
            ArmazemDAO.singleton = new ArmazemDAO();

        return ArmazemDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM armazens"))
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
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM armazens WHERE Nome = ?"))
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
        Armazem id = (Armazem) o;
        return this.containsKey(id.getId());
    }

    @Override
    public Armazem get(Object key)
    {
        int id = (Integer) key;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM armazens WHERE Id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new Armazem(
                    rs.getInt("Id"),
                    rs.getString("Localizacao")
            );
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Armazem put(Integer key, Armazem a)
    {
        Armazem old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("REPLACE INTO armazens (Id, Localizacao) VALUES (?, ?)");
            ps.setInt(1, a.getId());
            ps.setString(2, a.getLocalizacao());
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }



    @Override
    public Armazem remove(Object key)
    {
        int id = (Integer) key;
        Armazem old = get(id);
        if(old == null)
            return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM armazemIngredientes WHERE Armazem_Id=?");
            ps1.setInt(1, id);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM armazens WHERE Id=?");
            ps2.setInt(1, id);
            ps2.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }



    @Override
    public void putAll(Map<? extends Integer, ? extends Armazem> m)
    {
        m.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM ingredientes");
            st.executeUpdate("DELETE FROM ingredientesAlergenios");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Armazem> values()
    {
        List<Armazem> res = new ArrayList<>();
        for (int id : keySet())
            res.add(get(id));
        return res;
    }


    @Override
    public Set<Map.Entry<Integer, Armazem>> entrySet()
    {
        Set<Entry<Integer, Armazem>> res = new HashSet<>();
        for(int id : keySet())
            res.add(Map.entry(id, get(id)));
        return res;
    }


    @Override
    public Set<Integer> keySet()
    {
        Set<Integer> keys = new HashSet<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Id FROM armazens"))
        {
            while (rs.next())
                keys.add(rs.getInt("Id"));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return keys;
    }


    public Map<String,Integer> getStockForArmazem(int armazemId)
    {
        Map<String,Integer> stock = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Nome_Ingrediente, Quantidade FROM armazemIngredientes WHERE Armazem_Id=?");
            ps.setInt(1, armazemId);

            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                stock.put(rs.getString("Nome_Ingrediente"), rs.getInt("Quantidade"));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return stock;
    }

    public void setStock(int armazemId, String ingrediente, int quantidade)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("REPLACE INTO armazemIngredientes VALUES (?, ?, ?)");
            ps.setInt(1, armazemId);
            ps.setString(2, ingrediente);
            ps.setInt(3, quantidade);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void removeIngrediente(int armazemId, String ingrediente)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM armazemIngredientes WHERE Armazem_Id=? AND Nome_Ingrediente=?");
            ps.setInt(1, armazemId);
            ps.setString(2, ingrediente);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}