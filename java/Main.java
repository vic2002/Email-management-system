import java.sql.*;
import java.util.ArrayList;

public class Main {
    public ArrayList<String> names = new ArrayList<>();

    public void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading driver: " + e);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.loadDriver();
        String host = "bronto.ewi.utwente.nl";
        String dbName = "dab_di20212b_22";
        String url = "jdbc:postgresql://" + host + ":5432/" + dbName +"?currentSchema=kickin";
        String username = dbName;
        String password = "59VBwBTb41wjAdOo";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM kickin.email";
            ResultSet resultSet = statement.executeQuery(query);
            connection.close();
            while (true) {
                resultSet.next();
                if (resultSet.getString(1) != null) {
                    main.names.add(resultSet.getString(1));
                } else {
                    break;
                }
            }
        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
        for (String s : main.names) {
            System.out.println(s);
        }
    }
}
