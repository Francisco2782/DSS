package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.gestao.Armazem;
import uminho.grupo18.logicanegocio.pedidos.Prato;

import java.sql.*;
import java.util.*;

public class PratoDAO implements Map<String, Prato>
{
    private static PratoDAO singleton = null;

    public static PratoDAO getInstance()
    {
        if(singleton == null)
            singleton = new PratoDAO();
        return singleton;
    }

    private PratoDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement())
        {
            String sql = """
                        CREATE TABLE IF NOT EXISTS pratos(
                            Nome varchar(60) PRIMARY KEY,
                            Preco float,
                            Custo float,
                            Tempo_Medio time,
                            Tamanho int)
                    """;
            st.executeUpdate(sql);

            sql = """ 
                        CREATE TABLE IF NOT EXISTS pratoIngredientes(
                            Nome_Prato varchar(60),
                            Nome_Ingrediente varchar(60),
                            PRIMARY KEY (Nome_Prato, Nome_Ingrediente),
                            FOREIGN KEY (Nome_Prato) REFERENCES pratos(Nome),
                            FOREIGN KEY (Nome_Ingrediente) REFERENCES ingredientes(Nome))
                    """;
            st.executeUpdate(sql);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT count(*) FROM pratos"))
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
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM pratos WHERE Nome = ?"))
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
        Prato id = (Prato)o;
        return this.containsKey(id.getNome());
    }

    @Override
    public Prato get(Object key)
    {
        String nome = key.toString();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM pratos WHERE Nome=?");
            ps.setString(1, nome);

            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return null;

            return new Prato(
                    nome,
                    rs.getFloat("Preco"),
                    rs.getFloat("Custo"),
                    rs.getTime("Tempo_Medio").toLocalTime(),
                    rs.getInt("Tamanho")
            );
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public Prato put(String key, Prato p, List<Ingrediente> ingredientes)
    {
        Prato old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false); //transação

            PreparedStatement ps = conn.prepareStatement("REPLACE INTO pratos VALUES (?,?,?,?,?)");
            ps.setString(1, p.getNome());
            ps.setFloat(2, p.getPreco());
            ps.setFloat(3, p.getCusto());
            ps.setTime(4, Time.valueOf(p.getTempoMedioPreparacao()));
            ps.setInt(5, p.getTamanho());
            ps.executeUpdate();

            PreparedStatement del = conn.prepareStatement("DELETE FROM pratoIngredientes WHERE Nome_Prato=?");
            del.setString(1, p.getNome());
            del.executeUpdate();

            PreparedStatement ins = conn.prepareStatement("INSERT INTO pratoIngredientes VALUES (?,?)");

            for(Ingrediente e : ingredientes)
            {
                ins.setString(1, p.getNome());
                ins.setString(2, e.getNome());
                ins.executeUpdate();
            }

            conn.commit();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }


    @Override
    public Prato remove(Object key)
    {
        String nome = key.toString();
        Prato old = get(nome);
        if(old == null)
            return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement p1 = conn.prepareStatement("DELETE FROM pratoIngredientes WHERE Nome_Prato=?");
            p1.setString(1, nome);
            p1.executeUpdate();

            PreparedStatement p2 = conn.prepareStatement("DELETE FROM pratos WHERE Nome=?");
            p2.setString(1, nome);
            p2.executeUpdate();

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }


    @Override
    public void putAll(Map<? extends String, ? extends Prato> map)
    {
        map.forEach(this::put);
    }

    @Override
    public void clear()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement st = conn.createStatement())
        {
            st.executeUpdate("DELETE FROM pratos");
            st.executeUpdate("DELETE FROM pratoIngredientes");
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Prato> values()
    {
        List<Prato> res = new ArrayList<>();
        for(String id : keySet())
            res.add(get(id));
        return res;
    }


    @Override
    public Set<Map.Entry<String, Prato>> entrySet()
    {
        Set<Entry<String, Prato>> res = new HashSet<>();
        for(String id : keySet())
            res.add(Map.entry(id, get(id)));
        return res;
    }

    @Override
    public Prato put(String key, Prato value)
    {
        throw new UnsupportedOperationException(
                "Utilize put(String, Prato, List<Ingrediente>) para adicionar os ingredientes");
    }


    @Override
    public Set<String> keySet()
    {
        Set<String> keys = new HashSet<>();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Nome FROM pratos"))
        {
            while(rs.next())
                keys.add(rs.getString("Nome"));

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return keys;
    }

    public List<Ingrediente> getIngredientes(String prato)
    {
        List<Ingrediente> res = new ArrayList<>();
        IngredienteDAO ingDAO = IngredienteDAO.getInstance();

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            PreparedStatement ps = conn.prepareStatement("SELECT Nome_Ingrediente FROM pratoIngredientes WHERE Nome_Prato=?");
            ps.setString(1, prato);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                String nomeIng = rs.getString("Nome_Ingrediente");

                res.add(ingDAO.get(nomeIng));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return res;
    }

}

