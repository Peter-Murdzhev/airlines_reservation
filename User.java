package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String fullName;
    private String personalNumber;
    private String email;
    private double wallet;
    private List<Ticket> bookedTickets;

    private static Connection connection;

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    public User(String fullName, String personalNumber,
                String email, double moneyBalance) throws SQLException {
        this.fullName = fullName;
        this.personalNumber = personalNumber;
        this.email = email;
        this.wallet = moneyBalance;
        this.bookedTickets = new ArrayList<>();

        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("INSERT IGNORE INTO user (`fullname`,`personal_number`,`email`, `wallet`)" +
                " VALUES (" + fullName + ", " + personalNumber + ", " + email + ", " + moneyBalance + ")");
        statement.close();
    }

    public User(){};

    public User getUserObjectFromDB(String personalNumber) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE personal_number like "
        + personalNumber);
        rs.next();

        this.fullName = rs.getString("fullname");
        this.personalNumber = rs.getString("personal_number");
        this.email = rs.getString("email");
        this.wallet = rs.getDouble("wallet");

        return new User(this.fullName,this.personalNumber,this.email,this.wallet);
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String personalNumber, String newFullname) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET fullname = " + newFullname +
                "WHERE personal_number like " + personalNumber);

        //check if fullname is changed(it may not exist) and then assign newFullname to the variable
        ResultSet rs = statement.executeQuery("SELECT fullname FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();
        if(rs.getString("fullname").equals(newFullname)){
            this.fullName = newFullname;
        }
    }

    public String getPersonalNumber() {
        return this.personalNumber;
    }

    public void setPersonalNumber(String personalNumber, String newPersonalNumber) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET personal_number = " + newPersonalNumber +
                "WHERE personal_number like " + personalNumber);

        ResultSet rs = statement.executeQuery("SELECT personal_number FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();
        if(rs.wasNull()){
            this.personalNumber = newPersonalNumber;
        }
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail, String personalNumber) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET email = " + newEmail +
                "WHERE personal_number like " + personalNumber);

        ResultSet rs = statement.executeQuery("SELECT email FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();
        if(rs.getString("email").equals(newEmail)){
            this.email = newEmail;
        }
    }

    public double getWallet() {
        return this.wallet;
    }

    public void setWallet(double wallet, String personalNumber) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET wallet = " + wallet +
                "WHERE personal_number like " + personalNumber);

        ResultSet rs = statement.executeQuery("SELECT wallet FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();
        if(rs.getDouble("wallet") == wallet){
            this.wallet = wallet;
        }
    }

    public void withdrawMoney(double price, String personalNumber) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET wallet = " + (this.wallet - price) +
                "WHERE personal_number like " + personalNumber);

        ResultSet rs = statement.executeQuery("SELECT wallet FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();
        if(rs.getDouble("wallet") == this.wallet - price){
            this.wallet -= price;
        }
    }

    public void addBookedTicket(Ticket ticket, String personalNumber) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT user_id FROM user WHERE " +
                "personal_number like " + personalNumber);
        rs.next();

        int personID = rs.getInt("user_id");
        rs.close();

        rs = statement.executeQuery("SELECT ticket_id FROM ticket WHERE" +
                "destination_name like " + ticket.getDestinationName() + "AND date_and_time_of_flight like "
                + ticket.getDateAndTimeOfFlight());
        rs.next();

        int ticketID = rs.getInt("ticket_id");

        statement.execute("INSERT INTO reservation (user_id, booked_ticket_id)" +
                "VALUES (" + personID + ",  " + ticketID + ")");
        bookedTickets.add(ticket);
    }

    @Override
    public String toString(){
        String view = "Fullname: " + fullName + "\n" +
                        "Personal number: " + personalNumber + "\n" +
                        "Email: " + email + "\n" +
                        "Wallet: " + wallet;
        return view;
    }

    //to do
    public void removeBookedTicket(Ticket wantedTicked, String dateOfFlight) {
        bookedTickets.removeIf(ticket -> ticket.getDestinationName().
                equalsIgnoreCase(wantedTicked.getDestinationName())
                && ticket.getDateAndTimeOfFlight().equals(dateOfFlight));

    }

}
