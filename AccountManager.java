package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Scanner;

public class AccountManager {
    private static Connection connection;

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    private static void accountMenu() {
        System.out.println("1. Login");
        System.out.println("2. Register account");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
    }

    public UserAccount accountOperator() throws SQLException {
        Scanner scan = new Scanner(System.in);
        String selector = "";
        accountMenu();
        selector = scan.nextLine();

        UserAccount account = null;

        boolean isLoggedin = false;

        while (!isLoggedin) {
            if (selector.equals("1")) {
                account = login(scan);
                isLoggedin = true;
            } else if (selector.equals("2")) {
                RegistrationForm registration = new RegistrationForm();
                account = registration.registerNewUserAccount();
                isLoggedin = true;
            } else if (selector.equals("3")) {
               break;
            } else {
                System.out.println("Wrong command\n");
                accountMenu();
                selector = scan.nextLine();
            }
        }
        return account;
    }

    private UserAccount login(Scanner scan) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();

        String username = "";
        String password = "";
        boolean isUsernameCorrect = false;
        boolean isPasswordCorrect = false;

        while (!isUsernameCorrect) {
            System.out.print("Enter username: ");
            username = scan.nextLine();

            ResultSet rs = statement.executeQuery("SELECT username FROM user_account " +
                    "WHERE username like '" + username + "'");

            if (rs.isBeforeFirst()) {
                isUsernameCorrect = true;
            } else {
                System.out.println("Username doesn't exist.");
            }
        }

        ResultSet rs = statement.executeQuery("SELECT password FROM user_account " +
                "WHERE username like '" + username + "'");
        rs.next();

        while (!isPasswordCorrect) {
            System.out.print("Enter password: ");
            password = scan.nextLine();

            Decoder decoder = Base64.getDecoder();
            byte[] decodedPassBytes = decoder.decode(rs.getString("password").getBytes());
            String decodedPassword = new String(decodedPassBytes);

            if (password.equals(decodedPassword)) {
                isPasswordCorrect = true;
            } else {
                System.out.println("Incorrect password.");
            }
        }

        UserAccount user = UserAccount.getUserAccountObject(username);
        System.out.println("Welcome, " + user.getFullName() + "!");
        return user;
    }
}