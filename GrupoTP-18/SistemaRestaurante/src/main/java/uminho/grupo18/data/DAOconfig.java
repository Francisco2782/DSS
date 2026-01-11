package uminho.grupo18.data;

public class DAOconfig
{
    static final String USERNAME = "me";
    static final String PASSWORD = "@Mypass123";
    private static final String DATABASE = "SistemaRestaurante";
    private static final String DRIVER = "jdbc:mysql";
    static final String URL = DRIVER+"://localhost:3306/"+DATABASE+ "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Europe/Lisbon";
}
