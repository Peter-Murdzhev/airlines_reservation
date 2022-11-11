package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ticket{
    private String departurePoint;
    private String destinationName;
    private double ticketPrice;
    private LocalDateTime dateAndTimeOfFlight;
    private int availableTickets;


    public static final DateTimeFormatter DTFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static Connection connection;

    public static void connectToDatabase(){
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    public Ticket(String departurePoint, String destinationName, double ticketPrice,
                  String dateAndTimeOFFlight, int availableTickets)throws SQLException {
        this.departurePoint = departurePoint;
        this.destinationName = destinationName;
        this.ticketPrice = ticketPrice;
        this.dateAndTimeOfFlight = LocalDateTime.parse(dateAndTimeOFFlight,DTFormat);
        this.availableTickets = availableTickets;

        connectToDatabase();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT ticket_id FROM ticket " +
                "WHERE destination_name like '"+ destinationName + "' AND " +
                "date_and_time_of_flight like '" + dateAndTimeOFFlight + "'");

        if(!rs.isBeforeFirst()){
            statement.execute("INSERT INTO ticket VALUES (null, '" + departurePoint +
                    "', '" + destinationName + "', " + ticketPrice + ", '" +
                    dateAndTimeOFFlight + "', " + availableTickets + ")");
            statement.close();
        }
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String destinationName, String dateAndTime, String newDeparturePoint)throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET departure_point = '" + newDeparturePoint +
                "' WHERE destination_name like '" +destinationName+ "' AND '" +
                "date_and_time_of_flight like " + dateAndTime);

        ResultSet rs = statement.executeQuery("SELECT departure_point from ticket " +
                "WHERE destination_name like '" + destinationName + "' AND date_and_time_of_flight like '"+
                dateAndTime + "'");
        rs.next();

        String departurePoint = rs.getString("departure_point");

        if(departurePoint.equals(newDeparturePoint)){
            this.departurePoint = newDeparturePoint;
        }
    }

    public String getDestinationName(){
        return destinationName;
    }

    public void setDestinationName(String destinationName, String dateAndTime,
                                   String newDestinationName) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET destination_name = '" + newDestinationName +
                "' WHERE destination_name like '" + destinationName + "' AND '" +
                "date_and_time_of_flight like '" + dateAndTime + "'");

        ResultSet rs = statement.executeQuery("SELECT destination_name from ticket " +
                "WHERE destination_name like '" + newDestinationName + "' AND date_and_time_of_flight like '"+
                dateAndTime + "'");

        if(rs.isBeforeFirst()){
            this.destinationName = rs.getString("destination_name");
        }
    }

    public double getTicketPrice(){
        return ticketPrice;
    }

    public void setTicketPrice(String destinationName, String dateAndTime, double newPrice) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET ticket_price = " + newPrice +
                "WHERE destination_name like '" + destinationName + "' AND date_and_time_of_flight like '"
        +dateAndTime + "'");

        ResultSet rs = statement.executeQuery("SELECT ticket_price from ticket WHERE " +
                "destination_name like '"+ destinationName + "' AND date_and_time_of_flight like '"+
                dateAndTime + "'");
        rs.next();
        
        double currentPrice = rs.getDouble("ticket_price");

        if(currentPrice == newPrice){
            this.ticketPrice = newPrice;
        }
    }

    public String getDateAndTimeOfFlight() {
        return dateAndTimeOfFlight.format(DTFormat);
    }

    public void setDateAndTimeOfFlight(String destinationName,String dateAndTime,
                                       String newDateAndTimeOfFlight) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET date_and_time_of_flight = '" + newDateAndTimeOfFlight +
                "' WHERE destination_name like '" + destinationName + "' AND date_and_time_of_flight like '"+
                dateAndTime + "'");

        ResultSet rs = statement.executeQuery("SELECT date_and_time_of_flight FROM ticket " +
                "WHERE destination_name like '" + destinationName + "' AND '"+
                "date_and_time_of_flight like '" + newDateAndTimeOfFlight + "'");

        if(rs.isBeforeFirst()){
            this.dateAndTimeOfFlight = LocalDateTime.parse(newDateAndTimeOfFlight);
        }
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(String destinationName, String dateAndTime,
                                    int availableTickets) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET available_tickets = " + availableTickets +
                "WHERE destination_name like '" + destinationName + "' AND '" +
                "date_and_time_of_flight like '" + dateAndTime + "'");

        ResultSet rs = statement.executeQuery("SELECT available_tickets FROM ticket " +
                "WHERE destination_name like '" + destinationName + "' AND '" +
                "' date_and_time_of_flight like '" + dateAndTime +"'");
        rs.next();

        if(this.availableTickets != rs.getInt("available_tickets")){
            this.availableTickets = availableTickets;
        }

    }

    public List<User> getPeopleWhoBookedTicket() throws SQLException {
        List<User> peopleList = new ArrayList<>();
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rsTicketID = statement.executeQuery("SELECT ticket_id FROM ticket " +
                "WHERE destination_name like '" + this.getDestinationName() +
                "' AND date_and_time_of_flight like '" + this.getDateAndTimeOfFlight() + "'");
        rsTicketID.next();

        int ticketID = rsTicketID.getInt("ticket_id");

        ResultSet rsUserID = statement.executeQuery("SELECT user_id FROM reservation " +
                "WHERE booked_ticket_id = " + ticketID);

        List<Integer> wantedUsers = new ArrayList<>();

        while (rsUserID.next()){
            wantedUsers.add(rsUserID.getInt("user_id"));
        }

       for(int i = 0; i < wantedUsers.size(); i++){
            int userID = wantedUsers.get(i);

            ResultSet rsUser = statement.executeQuery("SELECT * FROM user WHERE " +
                    "user_id = " + userID);
            rsUser.next();

            peopleList.add(new User(rsUser.getString("fullname"),
                    rsUser.getString("personal_number"),rsUser.getString("email"),
                    rsUser.getDouble("wallet")));
        }

        return peopleList;
    }

    @Override
    public String toString(){
        String view = "Departure point: " + departurePoint + "\n" +
                "Destination: " + destinationName + "\n" +
                "Date and time of flight: " + dateAndTimeOfFlight.format(DTFormat) + "\n" +
                "Available tickets: " + availableTickets + "\n\n";
        return view;
    }

}
