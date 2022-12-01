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
                " VALUES ('" + fullName + "', '" + personalNumber + "', '" + email + "', " + moneyBalance + ")");
        statement.close();
    }

    public static User getUserObjectFromDB(String personalNumber) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE personal_number like '"
        + personalNumber + "'");
        rs.next();

        String fullName = rs.getString("fullname");
        personalNumber = rs.getString("personal_number");
        String email = rs.getString("email");
        double wallet = rs.getDouble("wallet");

        return new User(fullName,personalNumber,email,wallet);
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String newFullname) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET fullname = '" + newFullname +
                "' WHERE personal_number like '" + personalNumber + "'");

        this.fullName = newFullname;
    }

    public String getPersonalNumber() {
        return this.personalNumber;
    }

    public void setPersonalNumber(String newPersonalNumber) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET personal_number = '" + newPersonalNumber +
                "' WHERE personal_number like '" + personalNumber + "'");

        this.personalNumber = newPersonalNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET email = '" + newEmail +
                "' WHERE personal_number like '" + personalNumber + "'");

        this.email = newEmail;
    }

    public double getWallet() {
        return this.wallet;
    }

    public void setWallet(double wallet) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET wallet = " + wallet +
                " WHERE personal_number like '" + personalNumber + "'");

        this.wallet = wallet;
    }

    public void withdrawMoney(double price) throws SQLException {
        connectToDatabase();

        Statement statement = connection.createStatement();
        statement.execute("UPDATE user SET wallet = " + (wallet - price) +
                " WHERE personal_number like '" + personalNumber + "'");

        wallet -= price;
    }

    public void addBookedTicketToDB(Ticket wantedTicket) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT user_id FROM user WHERE " +
                "personal_number like '" + personalNumber + "'");
        rs.next();

        int personID = rs.getInt("user_id");

        rs = statement.executeQuery("SELECT ticket_id FROM ticket WHERE " +
                "destination_name like '" + wantedTicket.getDestinationName() + "' AND date_and_time_of_flight like '"
                + wantedTicket.getDateAndTimeOfFlight() + "'");
        rs.next();

        int ticketID = rs.getInt("ticket_id");

        statement.execute("INSERT INTO reservation (user_id, booked_ticket_id)" +
                "VALUES (" + personID + ", " + ticketID + ")");
    }

    public List<Ticket> getBookedTicketsFromUser() throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT user_id FROM user " +
                "WHERE personal_number like '" + this.personalNumber + "'");
        rs.next();

        int userID = rs.getInt("user_id");

        rs = statement.executeQuery("SELECT booked_ticket_id FROM reservation " +
                "WHERE user_id = " + userID);

        List<Integer> ticketIDs = new ArrayList<>();

        while (rs.next()){
            ticketIDs.add(rs.getInt("booked_ticket_id"));
        }

        for(int ticket_id : ticketIDs){
            rs = statement.executeQuery("SELECT * FROM ticket " +
                    "WHERE ticket_id = " + ticket_id);
            rs.next();

            this.bookedTickets.add(new Ticket(rs.getString("departure_point"),
                    rs.getString("destination_name"),rs.getDouble("ticket_price"),
                    rs.getString("date_and_time_of_flight"),
                    rs.getInt("available_tickets")));
        }
        return this.bookedTickets;
    }

    public void removeBookedTicket(Ticket wantedTicked) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT user_id FROM user " +
                "WHERE personal_number like '" + personalNumber + "'");
        rs.next();

        int userID = rs.getInt("user_id");

        rs = statement.executeQuery("SELECT ticket_id FROM ticket WHERE " +
                "destination_name like '" + wantedTicked.getDestinationName() +
                "' AND date_and_time_of_flight like '" +
                wantedTicked.getDateAndTimeOfFlight() + "'");
        rs.next();

        int ticketID = rs.getInt("ticket_id");

        statement.execute("DELETE FROM reservation WHERE " +
                "user_id = " + userID + " AND booked_ticket_id = " + ticketID);

        bookedTickets.removeIf(ticket -> ticket.getDestinationName().
                equalsIgnoreCase(wantedTicked.getDestinationName())
                && ticket.getDateAndTimeOfFlight().equals(wantedTicked.getDateAndTimeOfFlight()));

    }

    @Override
    public String toString(){
        String view = "Fullname: " + fullName + "\n" +
                "Personal number: " + personalNumber + "\n" +
                "Email: " + email + "\n" +
                "Wallet: " + wallet + "\n\n";
        return view;
    }

}
