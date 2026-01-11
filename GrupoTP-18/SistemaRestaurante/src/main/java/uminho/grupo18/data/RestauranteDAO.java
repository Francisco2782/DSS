package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.restaurante.Restaurante;

import java.sql.*;
import java.util.*;

public class RestauranteDAO implements Map<Integer, Restaurante>
{
    private static RestauranteDAO singleton = null;

    private RestauranteDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = """
                CREATE TABLE IF NOT EXISTS restaurantes (
                    Id INT PRIMARY KEY,
                    Localizacao VARCHAR(100) NOT NULL,
                    Id_Pedido_Atual INT DEFAULT 0,
                    Id_Posto_Atual INT DEFAULT 0
                )""";
            stm.executeUpdate(sql);

            sql = """
                CREATE TABLE IF NOT EXISTS restauranteIngredientes (
                    Restaurante_Id INT NOT NULL,
                    Nome_Ingrediente VARCHAR(60) NOT NULL,
                    Quantidade INT NOT NULL,
                    PRIMARY KEY (Restaurante_Id, Nome_Ingrediente),
                    FOREIGN KEY (Restaurante_Id) REFERENCES restaurantes(Id),
                    FOREIGN KEY (Nome_Ingrediente) REFERENCES ingredientes(Nome)
                )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static RestauranteDAO getInstance()
    {
        if(RestauranteDAO.singleton == null)
            RestauranteDAO.singleton = new RestauranteDAO();

        return RestauranteDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM restaurantes"))
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
            PreparedStatement pstm = conn.prepareStatement("SELECT 1 FROM restaurantes WHERE Id = ?"))
        {
            pstm.setInt(1, id);
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
        Restaurante r = (Restaurante) o;
        return this.containsKey(r.getId());
    }

    @Override
    public Restaurante get(Object key)
    {
        int id = (Integer) key;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM restaurantes WHERE Id=?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return null;

            return new Restaurante(
                    rs.getInt("Id"),
                    rs.getString("Localizacao"),
                    rs.getInt("Id_Pedido_Atual"),
                    rs.getInt("Id_Posto_Atual")
            );
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Restaurante put(Integer key, Restaurante r)
    {
        Restaurante old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement("REPLACE INTO restaurantes (Id, Localizacao, Id_Pedido_Atual, Id_Posto_Atual) VALUES (?, ?, ?, ?)");
            ps.setInt(1, r.getId());
            ps.setString(2, r.getLocalizacao());
            ps.setInt(3, r.getIdPedidoAtual());
            ps.setInt(4, r.getIdPostoAtual());
            ps.executeUpdate();

            if(old == null)
            {
                PreparedStatement ingredientes = conn.prepareStatement("SELECT Nome FROM ingredientes");
                ResultSet rs = ingredientes.executeQuery();

                PreparedStatement stock = conn.prepareStatement("INSERT INTO restauranteIngredientes (Restaurante_Id, Nome_Ingrediente, Quantidade) VALUES (?, ?, 0)");

                while(rs.next())
                {
                    stock.setInt(1, r.getId());
                    stock.setString(2, rs.getString("Nome"));
                    stock.executeUpdate();
                }

                PreparedStatement estatisticas = conn.prepareStatement("INSERT INTO estatisticas (Restaurante_Id, Faturacao, Tempo_Medio_Resposta) VALUES (?, 0, '00:00:00')");
                estatisticas.setInt(1, r.getId());
                estatisticas.executeUpdate();

                EstatisticasDAO estatisticasDAO = EstatisticasDAO.getInstance();

                PreparedStatement pratos = conn.prepareStatement("SELECT Nome FROM pratos");
                ResultSet rsPratos = pratos.executeQuery();
                while(rsPratos.next())
                {
                    estatisticasDAO.adicionarItemARestaurantes(rsPratos.getString("Nome"), "PRATO");
                }

                PreparedStatement menus = conn.prepareStatement("SELECT Nome FROM menus");
                ResultSet rsMenus = menus.executeQuery();
                while(rsMenus.next())
                {
                    estatisticasDAO.adicionarItemARestaurantes(rsMenus.getString("Nome"), "MENU");
                }
            }

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public Restaurante remove(Object key)
    {
        int id = (Integer) key;
        Restaurante old = get(id);
        if(old == null) return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM restauranteIngredientes WHERE Restaurante_Id=?");
            ps1.setInt(1, id);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM popularidade WHERE Restaurante_Id=?");
            ps2.setInt(1, id);
            ps2.executeUpdate();

            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM estatisticas WHERE Restaurante_Id=?");
            ps3.setInt(1, id);
            ps3.executeUpdate();

            PreparedStatement ps4 = conn.prepareStatement("DELETE FROM restaurantes WHERE Id=?");
            ps4.setInt(1, id);
            ps4.executeUpdate();

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Restaurante> m)
    {
        m.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM restauranteIngredientes");
            st.executeUpdate("DELETE FROM popularidade");
            st.executeUpdate("DELETE FROM estatisticas");
            st.executeUpdate("DELETE FROM restaurantes");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Restaurante> values()
    {
        List<Restaurante> res = new ArrayList<>();
        for (int id : keySet())
            res.add(get(id));
        return res;
    }

    @Override
    public Set<Entry<Integer, Restaurante>> entrySet()
    {
        Set<Entry<Integer, Restaurante>> res = new HashSet<>();
        for(int id : keySet())
            res.add(Map.entry(id, get(id)));
        return res;
    }

    @Override
    public Set<Integer> keySet()
    {
        Set<Integer> keys = new HashSet<>();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Id FROM restaurantes"))
        {
            while(rs.next())
                keys.add(rs.getInt("Id"));
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    public Map<String, Integer> getStockForRestaurante(int restauranteId)
    {
        Map<String, Integer> stock = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Nome_Ingrediente, Quantidade FROM restauranteIngredientes WHERE Restaurante_Id=?");
            ps.setInt(1, restauranteId);

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

    public void setStock(int restauranteId, String ingrediente, int quantidade)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("REPLACE INTO restauranteIngredientes (Restaurante_Id, Nome_Ingrediente, Quantidade) VALUES (?, ?, ?)");
            ps.setInt(1, restauranteId);
            ps.setString(2, ingrediente);
            ps.setInt(3, quantidade);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void removeIngrediente(int restauranteId, String ingrediente)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM restauranteIngredientes WHERE Restaurante_Id=? AND Nome_Ingrediente=?");
            ps.setInt(1, restauranteId);
            ps.setString(2, ingrediente);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int getNextPedidoId(int restauranteId)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("UPDATE restaurantes SET Id_Pedido_Atual = Id_Pedido_Atual + 1 WHERE Id=?");
            ps.setInt(1, restauranteId);
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("SELECT Id_Pedido_Atual FROM restaurantes WHERE Id=?");
            ps2.setInt(1, restauranteId);
            ResultSet rs = ps2.executeQuery();

            if(rs.next())
                return rs.getInt("Id_Pedido_Atual");
            return 0;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int getNextPostoId(int restauranteId)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("UPDATE restaurantes SET Id_Posto_Atual = Id_Posto_Atual + 1 WHERE Id=?");
            ps.setInt(1, restauranteId);
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("SELECT Id_Posto_Atual FROM restaurantes WHERE Id=?");
            ps2.setInt(1, restauranteId);
            ResultSet rs = ps2.executeQuery();

            if(rs.next())
                return rs.getInt("Id_Posto_Atual");

            return 0;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}