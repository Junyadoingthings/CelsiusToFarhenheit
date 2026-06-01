

import java.sql.*;


public class DatabaseHelper {

    public static final String URL = "jdbc:sqlite:temperature.db";

    public static void createTable() {

        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();

            stmt.execute( "CREATE TABLE IF NOT EXISTS TemperatureRecords " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "temperature TEXT NOT NULL)"
            );


            System.out.println("Database Ready");
            conn.close();


        } catch (SQLException e) {
   System.out.println("Error creating table :"+ e.getMessage());

        }
    }

    public static void saveTemperature (String conversion){
        try { Connection conn = DriverManager.getConnection(URL);
Statement stmt = conn.createStatement();
stmt.execute( "INSERT INTO TemperatureRecords (temperature) "+ "VALUES('"+ conversion +"')");
conn.close();


        } catch (SQLException e){
            System.out.println("Error saving to Database:"+ e.getMessage());


        }

    }
    public static void readTemperatures() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TemperatureRecords");
            System.out.println("---Database Record---");
            while (rs.next()) {
                System.out.println(rs.getString("temperature"));
            }
            System.out.println("-----------------");
            conn.close();

        }catch (SQLException e){
            System.out.println("Error Reading Database "+ e.getMessage());


        }
    }
}


