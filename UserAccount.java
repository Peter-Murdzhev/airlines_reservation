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

    //constructor for registering new user account
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

        //probably it's not going to work
        statement = connection.createStatement();
        statement.execute("UPDATE user_account SET user_id = user_account_id WHERE username = "+
                this.username);
        statement.close();
        //
    }

    //constructor for taking user account that exists from DB
    public UserAccount(String username, String password){
        super();
        this.username = username;
        this.password = password;
    }

    public UserAccount getUserAccountObject(String username) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM user_account WHERE "
        + "username = " + username);
        rs.next();

        return new UserAccount(rs.getString("username"),rs.getString("password"));
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
