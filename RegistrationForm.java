package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RegistrationForm {
    private static Connection connection;

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    public UserAccount registerNewUserAccount() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Registration Form:");
        System.out.println();

        String username = "";
        String password = "";

        boolean isUsernameValid = false;
        boolean isPasswordValid = false;

        while (!isUsernameValid) {
            System.out.print("Enter username: ");
            username = scan.nextLine();

            if (username.length() < 4) {
                System.out.println("Username too short");
                continue;
            }

            connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT username from user_account" +
                    " WHERE username like '" + username + "'");

            if (rs.isBeforeFirst()){
                System.out.println("Username already exists");
            } else {
                isUsernameValid = true;
            }
        }

        while (!isPasswordValid) {
            System.out.print("Enter password: ");
            password = scan.nextLine();

            if (password.length() < 6) {
                System.out.println("Invalid Password");
            } else {
                isPasswordValid = true;
            }
        }

        String fullname = insertFullname(scan);
        String personalNumber = insertPersonalNumber(scan);
        String email = insertEmail(scan);
        double wallet = insertMoney(scan);

        System.out.println("Account registered! Welcome, " + fullname + " !");
        return new UserAccount(username,password,fullname,personalNumber,email,wallet);
    }

    private static String insertFullname(Scanner scan){
        boolean isFullnameValid = false;
        String fullname = "";

        while (!isFullnameValid){
            System.out.print("Enter fullname: ");
            fullname = scan.nextLine();

            if(fullname.length() > 6){
                isFullnameValid = true;
            }else{
                System.out.println("Fullname too short!");
            }
        }

        return fullname;
    }

    private static String insertPersonalNumber(Scanner scan) throws SQLException {
        boolean isPersonalNumberValid = false;
        String personalNumber = "";

        while (!isPersonalNumberValid){
            System.out.print("Enter personal number: ");
            personalNumber = scan.nextLine();

            connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT personal_number FROM user " +
                    "Where personal_number like '" + personalNumber + "'");

            if(rs.isBeforeFirst()){
                System.out.println("Personal number already exists!");
                continue;
            }else if(personalNumber.length() != 10){
                System.out.println("You've entered invalid personal number!");
                continue;
            }
            isPersonalNumberValid = true;
        }

        return personalNumber;
    }

    private static String insertEmail(Scanner scan) throws SQLException {
        boolean isEmailValid = false;
        String email = "";

        while (!isEmailValid){
            System.out.print("Enter email adress: ");
            email = scan.nextLine();

            if(email.contains("@") && email.contains(".")){
                connectToDatabase();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT email FROM user " +
                        "WHERE email like '" + email + "'");

                if(rs.isBeforeFirst()){
                    System.out.println("This email already exists!");
                }else{
                    isEmailValid = true;
                }
            }else{
                System.out.println("Invalid email!");
            }
        }

        return email;
    }

    private static double insertMoney(Scanner scan){
        System.out.print("Enter amount of money: ");
        return Double.parseDouble(scan.nextLine());
    }
}
