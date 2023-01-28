import db.ReadConfigFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PreparedStatementNow {
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
        ReadConfigFile resources = new ReadConfigFile();
        String host = "bronto.ewi.utwente.nl";
        String dbName = resources.getUsername();
        String url = "jdbc:postgresql://" + host + ":5432/" + dbName +"?currentSchema=movies";
        String username = dbName;
        String password = resources.getPassword();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT DISTINCT p1.name " +
                    "FROM person p1, person p2, movie m, writes w, acts a " +
                    "WHERE m.mid = w.mid AND w.pid = p1.pid " +
                    "AND m.mid = a.mid AND a.pid = p2.pid " +
                    "AND p2.name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            Scanner scnr = new Scanner(System.in);
            System.out.println("What actor do you want to search.");
            String response = scnr.nextLine();
            statement.setString(1, response);
            ResultSet resultSet = statement.executeQuery();
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
            System.err.println("Printing results:");
        }
        int i = 0;
        for (String s : main.names) {
            i++;
            System.out.println(i + ": " + s);
        }
    }
}