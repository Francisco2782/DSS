package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.restaurante.Estatisticas;
import uminho.grupo18.logicanegocio.pedidos.Proposta;
import uminho.grupo18.logicanegocio.pedidos.Menu;
import uminho.grupo18.logicanegocio.pedidos.Prato;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class EstatisticasDAO implements Map<Integer, Estatisticas>
{
    private static EstatisticasDAO singleton = null;

    private EstatisticasDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = """
                CREATE TABLE IF NOT EXISTS estatisticas (
                    Restaurante_Id INT PRIMARY KEY,
                    Faturacao FLOAT DEFAULT 0,
                    Tempo_Medio_Resposta TIME DEFAULT '00:00:00',
                    FOREIGN KEY (Restaurante_Id) REFERENCES restaurantes(Id)
                )""";
            stm.executeUpdate(sql);

            sql = """
                CREATE TABLE IF NOT EXISTS popularidade (
                    Restaurante_Id INT NOT NULL,
                    Nome_Item VARCHAR(60) NOT NULL,
                    Tipo ENUM('PRATO', 'MENU') NOT NULL,
                    Quantidade INT DEFAULT 0,
                    PRIMARY KEY (Restaurante_Id, Nome_Item, Tipo),
                    FOREIGN KEY (Restaurante_Id) REFERENCES restaurantes(Id)
                )""";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static EstatisticasDAO getInstance()
    {
        if(EstatisticasDAO.singleton == null)
            EstatisticasDAO.singleton = new EstatisticasDAO();

        return EstatisticasDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM estatisticas"))
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
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM estatisticas WHERE Restaurante_Id=?"))
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
        // Not efficiently implementable for Estatisticas
        throw new UnsupportedOperationException();
    }

    @Override
    public Estatisticas get(Object key)
    {
        int restauranteId = (Integer) key;

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM estatisticas WHERE Restaurante_Id=?");
            ps.setInt(1, restauranteId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            float faturacao = rs.getFloat("Faturacao");
            LocalTime tempoMedio = rs.getTime("Tempo_Medio_Resposta").toLocalTime();

            Map<Proposta, Integer> popularidade = getPopularidade(restauranteId);

            RestauranteDAO restauranteDAO = RestauranteDAO.getInstance();
            Map<String, Integer> stock = restauranteDAO.getStockForRestaurante(restauranteId);

            Estatisticas estatisticas = new Estatisticas();
            estatisticas.setFaturacao(faturacao);
            estatisticas.setTempoMedioEspera(tempoMedio);

            for(Map.Entry<Proposta, Integer> entry : popularidade.entrySet())
            {
                for(int i = 0; i < entry.getValue(); i++)
                {
                    estatisticas.registarProposta(entry.getKey());
                }
            }

            return estatisticas;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Estatisticas put(Integer key, Estatisticas estatisticas)
    {
        Estatisticas old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement("REPLACE INTO estatisticas (Restaurante_Id, Faturacao, Tempo_Medio_Resposta) VALUES (?, ?, ?)");
            ps.setInt(1, key);
            ps.setFloat(2, estatisticas.getFaturacao());
            ps.setTime(3, Time.valueOf(estatisticas.getTempoMedioEspera()));
            ps.executeUpdate();

            updatePopularidade(key, estatisticas.getPopularidadeOrdenada(), conn);

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public Estatisticas remove(Object key)
    {
        int restauranteId = (Integer) key;
        Estatisticas old = get(restauranteId);
        if(old == null) return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM popularidade WHERE Restaurante_Id=?");
            ps1.setInt(1, restauranteId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM estatisticas WHERE Restaurante_Id=?");
            ps2.setInt(1, restauranteId);
            ps2.executeUpdate();

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Estatisticas> map)
    {
        map.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM popularidade");
            st.executeUpdate("DELETE FROM estatisticas");
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
            ResultSet rs = st.executeQuery("SELECT Restaurante_Id FROM estatisticas"))
        {
            while(rs.next())
                keys.add(rs.getInt("Restaurante_Id"));
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    @Override
    public Collection<Estatisticas> values()
    {
        List<Estatisticas> res = new ArrayList<>();
        for(Integer id : keySet())
            res.add(get(id));
        return res;
    }

    @Override
    public Set<Entry<Integer, Estatisticas>> entrySet()
    {
        Set<Entry<Integer, Estatisticas>> res = new HashSet<>();
        for(Integer id : keySet())
            res.add(Map.entry(id, get(id)));
        return res;
    }

    public Map<Proposta, Integer> getPopularidade(int restauranteId)
    {
        Map<Proposta, Integer> popularidade = new HashMap<>();
        PratoDAO pratoDAO = PratoDAO.getInstance();
        MenuDAO menuDAO = MenuDAO.getInstance();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Nome_Item, Tipo, Quantidade FROM popularidade WHERE Restaurante_Id=?");
            ps.setInt(1, restauranteId);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                String nome = rs.getString("Nome_Item");
                String tipo = rs.getString("Tipo");
                int quantidade = rs.getInt("Quantidade");

                Proposta proposta = null;
                if("PRATO".equals(tipo))
                    proposta = pratoDAO.get(nome);
                else if("MENU".equals(tipo))
                    proposta = menuDAO.get(nome);

                if(proposta != null)
                    popularidade.put(proposta, quantidade);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return popularidade;
    }

    private void updatePopularidade(int restauranteId, Map<Proposta, Integer> popularidade, Connection conn) throws SQLException
    {
        PreparedStatement del = conn.prepareStatement("DELETE FROM popularidade WHERE Restaurante_Id=?");
        del.setInt(1, restauranteId);
        del.executeUpdate();

        PreparedStatement ins = conn.prepareStatement("INSERT INTO popularidade (Restaurante_Id, Nome_Item, Tipo, Quantidade) VALUES (?, ?, ?, ?)");

        for(Map.Entry<Proposta, Integer> entry : popularidade.entrySet())
        {
            Proposta proposta = entry.getKey();
            String tipo = (proposta instanceof Menu) ? "MENU" : "PRATO";

            ins.setInt(1, restauranteId);
            ins.setString(2, proposta.getNome());
            ins.setString(3, tipo);
            ins.setInt(4, entry.getValue());
            ins.executeUpdate();
        }
    }

    public void incrementarPopularidade(int restauranteId, String nomeItem, String tipo)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO popularidade (Restaurante_Id, Nome_Item, Tipo, Quantidade) VALUES (?, ?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE Quantidade = Quantidade + 1");
            ps.setInt(1, restauranteId);
            ps.setString(2, nomeItem);
            ps.setString(3, tipo);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void adicionarItemARestaurantes(String nomeItem, String tipo)
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement restaurantes = conn.prepareStatement("SELECT Id FROM restaurantes");
            ResultSet rs = restaurantes.executeQuery();

            PreparedStatement ins = conn.prepareStatement("INSERT IGNORE INTO popularidade (Restaurante_Id, Nome_Item, Tipo, Quantidade) VALUES (?, ?, ?, 0)");

            while(rs.next())
            {
                ins.setInt(1, rs.getInt("Id"));
                ins.setString(2, nomeItem);
                ins.setString(3, tipo);
                ins.executeUpdate();
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}