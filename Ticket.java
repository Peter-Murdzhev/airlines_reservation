package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String departurePoint;
    private String destinationName;
    private double ticketPrice;
    private LocalDateTime dateAndTimeOfFlight;
    private int availableTickets;

    public static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static Connection connection;

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    public Ticket(String departurePoint, String destinationName, double ticketPrice,
                  String dateAndTimeOFFlight, int availableTickets) throws SQLException {
        this.departurePoint = departurePoint;
        this.destinationName = destinationName;
        this.ticketPrice = ticketPrice;
        this.dateAndTimeOfFlight = LocalDateTime.parse(dateAndTimeOFFlight, DT_FORMAT);
        this.availableTickets = availableTickets;

        connectToDatabase();
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT ticket_id FROM ticket " +
                "WHERE destination_name like '" + destinationName + "' AND " +
                "date_and_time_of_flight like '" + dateAndTimeOFFlight + "'");

        if (!rs.isBeforeFirst()) {
            statement.execute("INSERT INTO ticket VALUES (null, '" + departurePoint +
                    "', '" + destinationName + "', " + ticketPrice + ", '" +
                    dateAndTimeOFFlight + "', " + availableTickets + ")");
            statement.close();
        }
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String newDeparturePoint) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET departure_point = '" + newDeparturePoint +
                "' WHERE destination_name like '" + destinationName + "' AND '" +
                "date_and_time_of_flight like " + dateAndTimeOfFlight.format(DT_FORMAT));

        this.departurePoint = newDeparturePoint;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String newDestinationName) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET destination_name = '" + newDestinationName +
                "' WHERE destination_name like '" + destinationName + "' AND '" +
                "date_and_time_of_flight like '" + dateAndTimeOfFlight.format(DT_FORMAT) + "'");

        this.destinationName = newDestinationName;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double newPrice) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET ticket_price = " + newPrice +
                " WHERE destination_name like '" + destinationName + "' AND date_and_time_of_flight like '"
                + dateAndTimeOfFlight.format(DT_FORMAT) + "'");

        this.ticketPrice = newPrice;
    }

    public String getDateAndTimeOfFlight() {
        return dateAndTimeOfFlight.format(DT_FORMAT);
    }

    public void setDateAndTimeOfFlight(String newDateAndTimeOfFlight) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET date_and_time_of_flight = '" + newDateAndTimeOfFlight +
                "' WHERE destination_name like '" + destinationName + "' AND date_and_time_of_flight like '" +
                dateAndTimeOfFlight.format(DT_FORMAT) + "'");

        this.dateAndTimeOfFlight = LocalDateTime.parse(newDateAndTimeOfFlight);
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        statement.execute("UPDATE ticket SET available_tickets = " + availableTickets +
                " WHERE destination_name like '" + destinationName + "' AND " +
                "date_and_time_of_flight like '" + dateAndTimeOfFlight.format(DT_FORMAT) + "'");

        this.availableTickets = availableTickets;
    }

    @Override
    public String toString() {
        String view = "Departure point: " + departurePoint + "\n" +
                "Destination: " + destinationName + "\n" +
                "Date and time of flight: " + dateAndTimeOfFlight.format(DT_FORMAT) + "\n" +
                "Ticket price: " + ticketPrice + "\n\n";
        return view;
    }
}
