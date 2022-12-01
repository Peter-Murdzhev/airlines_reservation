package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class UserAccount extends User {
    private String username;
    private String password;
    private static Connection connection;

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    public UserAccount(String username, String password, String fullname, String personalNumber,
                       String email, double moneyBalance) throws SQLException {
        super(fullname, personalNumber, email, moneyBalance);
        this.username = username;
        this.password = password;

        connectToDatabase();
        Encoder encoder = Base64.getEncoder();
        String encryptedPassword = encoder.encodeToString(this.password.getBytes());

        Statement statement = connection.createStatement();
        statement.execute("INSERT IGNORE INTO user_account (`username`,`password`)" +
                " VALUES ('" + username + "', '" + encryptedPassword + "')");

        statement = connection.createStatement();
        statement.execute("UPDATE user_account acc\n" +
                "JOIN user u\n" +
                "SET acc.user_id = u.user_id\n" +
                "WHERE u.personal_number like '" +
                personalNumber + "' AND acc.username like '"
                + username + "'");
        statement.close();
    }

    public static UserAccount getUserAccountObject(String username) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM user_account WHERE "
                + "username = '" + username + "'");
        rs.next();

        username = rs.getString("username");

        Decoder decoder = Base64.getDecoder();
        byte[] decodedPasswordBytes = decoder.decode(rs.getString("password"));
        String password = new String(decodedPasswordBytes);

        int userID = rs.getInt("user_id");

        rs = statement.executeQuery("SELECT * FROM user WHERE user_id = " + userID);
        rs.next();

        String fullname = rs.getString("fullname");
        String personalNumber = rs.getString("personal_number");
        String email = rs.getString("email");
        double wallet = rs.getDouble("wallet");

        return new UserAccount(username, password, fullname, personalNumber, email, wallet);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String newUserName) throws SQLException {
        if (username.length() >= 4) {
            connectToDatabase();
            Statement statement = connection.createStatement();
            statement.execute("UPDATE user_account SET username = '" + newUserName +
                    "' WHERE username like '" + username + "'");
            statement.close();

            username = newUserName;
            System.out.println("Username changed!");
        } else {
            System.out.println("Incorrect username");
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String newPassword) throws SQLException {
        if (newPassword.length() >= 6) {
            connectToDatabase();

            Statement statement = connection.createStatement();
            Encoder encoder = Base64.getEncoder();
            String encryptedPassword = encoder.encodeToString(newPassword.getBytes());

            statement.execute("UPDATE user_account SET password = '" + encryptedPassword
                    + "' WHERE username like '" + username + "'");
            statement.close();

            password = newPassword;
            System.out.println("Password changed!");
        } else {
            System.out.println("Password is too short");
        }
    }
}
