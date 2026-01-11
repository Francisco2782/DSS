package uminho.grupo18.data;

import uminho.grupo18.logicanegocio.cozinha.Ingrediente;
import uminho.grupo18.logicanegocio.pedidos.Alergenio;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class IngredienteDAO implements Map<String, Ingrediente>
{
    private static IngredienteDAO singleton = null;

    private IngredienteDAO()
    {
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
            Statement stm = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS ingredientes (" +
                    "Nome varchar(60) NOT NULL PRIMARY KEY," +
                    "Tempo_Espera time(60) NOT NULL)";
            stm.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS ingredientesAlergenios (" +
                    "Nome_Ingrediente varchar(60) NOT NULL," +
                    "Nome_Alergenio varchar(60) NOT NULL," +
                    "FOREIGN KEY (Nome_Ingrediente) REFERENCES ingredientes(Nome)," +
                    "FOREIGN KEY (Nome_Alergenio) REFERENCES alergenios(Nome)," +
                    "PRIMARY KEY (Nome_Ingrediente, Nome_Alergenio))";
            stm.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }
    }

    public static IngredienteDAO getInstance()
    {
        if(IngredienteDAO.singleton == null)
            IngredienteDAO.singleton = new IngredienteDAO();

        return IngredienteDAO.singleton;
    }

    @Override
    public int size()
    {
        int i = 0;
        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery("SELECT count(*) FROM ingredientes")) 
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
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM ingredientes WHERE Nome = ?"))
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
        Ingrediente id = (Ingrediente)o;
        return this.containsKey(id.getNome());
    }

    @Override
    public Ingrediente get(Object key)
    {
        String nome = key.toString();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ingredientes WHERE Nome=?");
            ps.setString(1, nome);

            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                return null;

            LocalTime tempo = rs.getTime("Tempo_Espera").toLocalTime();

            PreparedStatement ps2 = conn.prepareStatement("SELECT Nome_Alergenio FROM ingredientesAlergenios WHERE Nome_Ingrediente=?");
            ps2.setString(1, nome);

            ResultSet rs2 = ps2.executeQuery();

            List<Alergenio> alergenios = new ArrayList<>();
            AlergenioDAO alergDAO = AlergenioDAO.getInstance();

            while(rs2.next())
            {
                String alergNome = rs2.getString("Nome_Alergenio");
                alergenios.add(alergDAO.get(alergNome));
            }

            return new Ingrediente(nome, tempo, alergenios);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Ingrediente put(String key, Ingrediente ing)
    {
        Ingrediente old = get(key);

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement("REPLACE INTO ingredientes (Nome, Tempo_Espera) VALUES (?, ?)");
            ps.setString(1, ing.getNome());
            ps.setTime(2, Time.valueOf(ing.getTempoDeEspera()));
            ps.executeUpdate();

            PreparedStatement del = conn.prepareStatement("DELETE FROM ingredientesAlergenios WHERE Nome_Ingrediente=?");
            del.setString(1, ing.getNome());
            del.executeUpdate();

            PreparedStatement ins = conn.prepareStatement("INSERT INTO ingredientesAlergenios VALUES (?, ?)");

            for(String a : ing.getAlergenios())
            {
                ins.setString(1, ing.getNome());
                ins.setString(2, a);
                ins.executeUpdate();
            }

            if(old == null) //Cria stock a 0 em todos os armazéns
            {
                PreparedStatement armazens = conn.prepareStatement("SELECT Id FROM armazens");

                ResultSet rs = armazens.executeQuery();

                PreparedStatement stock = conn.prepareStatement("INSERT INTO armazemIngredientes (Armazem_Id, Nome_Ingrediente, Quantidade) VALUES (?, ?, 0)");

                while(rs.next())
                {
                    stock.setInt(1, rs.getInt("Id"));
                    stock.setString(2, ing.getNome());
                    stock.executeUpdate();
                }
            }

            conn.commit();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }



    @Override
    public Ingrediente remove(Object key)
    {
        String nome = key.toString();
        Ingrediente old = get(nome);
        if (old == null) return null;

        try(Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD))
        {
            conn.setAutoCommit(false); //transação

            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ingredientesAlergenios WHERE Nome_Ingrediente=?");
            ps1.setString(1, nome);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM armazemIngredientes WHERE Nome_Ingrediente=?");
            ps2.setString(1, nome);
            ps2.executeUpdate();

            PreparedStatement ps3 = conn.prepareStatement("DELETE FROM armazemIngredientes WHERE Nome_Ingrediente=?");
            ps3.setString(1, nome);
            ps3.executeUpdate();


            PreparedStatement ps4 = conn.prepareStatement("DELETE FROM pratoIngredientes WHERE Nome_Ingrediente=?");
            ps4.setString(1, nome);
            ps4.executeUpdate();

            conn.commit();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return old;
    }


    @Override
    public void putAll(Map<? extends String, ? extends Ingrediente> m)
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
    public Collection<Ingrediente> values()
    {
        List<Ingrediente> res = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Nome FROM ingredientes"))
        {
            while(rs.next())
            {
                String nome = rs.getString("Nome");
                res.add(get(nome));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }




    @Override
    public Set<Entry<String, Ingrediente>> entrySet()
    {
        Set<Entry<String, Ingrediente>> res = new HashSet<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Nome FROM ingredientes"))
        {
            while (rs.next())
            {
                String nome = rs.getString("Nome");
                res.add(Map.entry(nome, get(nome)));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return res;
    }

    @Override
    public Set<String> keySet()
    {
        Set<String> keys = new HashSet<>();

        try (Connection conn = DriverManager.getConnection(DAOconfig.URL, DAOconfig.USERNAME, DAOconfig.PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Nome FROM ingredientes"))
        {
            while (rs.next())
                keys.add(rs.getString("Nome"));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return keys;
    }
}