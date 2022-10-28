package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserAccount extends User {
    private String username;
    private String password;
    private static Connection connection;

    public static void connectToDatabase(){
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }
    
    public UserAccount(String username, String password, String fullname, String personalNumber,
                       String email, double moneyBalance) throws SQLException {
        super(fullname,personalNumber,email,moneyBalance);
        this.username = username;
        this.password = password;

        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO user_account (`username`,`password`)" +
                " VALUES ("+username +", " + password + ")");
        statement.close();

        statement = connection.createStatement();
        statement.execute("UPDATE user_account acc\n" +
                "JOIN user u\n" +
                "ON acc.user_id = u.user_id\n" +
                "SET acc.user_id = u.user_id\n" +
                "WHERE persnal_number like"+
                personalNumber);
        statement.close();

    }

    public UserAccount getUserAccountObject(String username) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM user_account WHERE "
        + "username = " + username);
        rs.next();

        username = rs.getString("username");
        String password = rs.getString("password");
        int userID = rs.getInt("user_id");

        rs = statement.executeQuery("SELECT * FROM user WHERE user_id = " + userID);
        rs.next();

        String fullname = rs.getString("fullname");
        String personalNumber = rs.getString("personal_number");
        String email = rs.getString("email");
        double wallet =  rs.getDouble("wallet");

        return new UserAccount(username,password,fullname,personalNumber,email,wallet);
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username, String newUserName) throws SQLException {
        if(username.length() >= 4){
           connectToDatabase();
           Statement statement = connection.createStatement();
           statement.execute("UPDATE user_account SET username = "+ newUserName +
                   " WHERE username like " + username);
           statement.close();
        }else{
            System.out.println("Incorrect username");
        }
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String userName, String oldPassword, String newPassword) throws SQLException {
        if(newPassword.length() >= 6){
           connectToDatabase();

           Statement statement = connection.createStatement();
           statement.execute("UPDATE user_account SET password = "+ newPassword
                   + " WHERE username like " + userName + " AND password like " + oldPassword);
           statement.close();
        }else{
            System.out.println("Password is too short");
        }
    }

}
