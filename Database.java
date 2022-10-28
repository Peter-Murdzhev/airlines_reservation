package airlines_reservation_system;

import java.sql.*;

public class Database {
    private Connection connection;

    public void createConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline_ticket_reservation",
                    "root", "pass");
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
