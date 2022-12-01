package airlines_reservation_system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reservation {
    private static Connection connection;

    private static List<Ticket> ticketsToDestination = new ArrayList<>();

    public static void connectToDatabase() {
        Database db = new Database();
        db.createConnection();
        connection = db.getConnection();
    }

    private static void menu() {
        System.out.println();
        System.out.println("1.Check out your booked tickets.");
        System.out.println("2.Find tickets for destination.");
        System.out.println("3.Change profile information.");
        System.out.println("4.Check your money balance.");
        System.out.println("5.Log out.");
        System.out.println("6.End");
        System.out.print("Choose option: ");
    }

    public void reserveTicket() throws SQLException {
        Scanner scan = new Scanner(System.in);

        AccountManager accountManager = new AccountManager();
        //it triggers the login menu and returns UserAccount object.
        UserAccount user = accountManager.accountOperator();

        boolean hasFinished = false;

        if (user == null) {
            hasFinished = true;
        }

        while (!hasFinished) {
            menu();
            String selector = scan.nextLine();
            String command = "";
            switch (selector) {
                case "1":
                    List<Ticket> userTickets = user.getBookedTicketsFromUser();

                    System.out.println();
                    for (int i = 0; i < userTickets.size(); i++) {
                        System.out.print((i + 1) + ". ");
                        System.out.print(userTickets.get(i));
                    }

                    if(userTickets.isEmpty()){
                        System.out.println("You don't have any booked tickets yet!");
                        break;
                    }

                    System.out.print("Do you want to cancel any reservation (yes/no): ");
                    command = scan.nextLine();

                    if (command.equals("yes")) {
                        System.out.print("Choose number of ticket you want to remove: ");
                        int ticketNum = Integer.parseInt(scan.nextLine());

                        if (ticketNum <= userTickets.size()) {
                            Ticket chosenTicket = userTickets.get(ticketNum - 1);

                            user.removeBookedTicket(chosenTicket);
                            user.setWallet(user.getWallet() + chosenTicket.getTicketPrice());
                            chosenTicket.setAvailableTickets(chosenTicket.getAvailableTickets() + 1);

                            System.out.println("Ticket removed.");
                        } else {
                            System.out.println("You chose wrong number.");
                        }
                    }
                    userTickets.clear();
                    break;
                case "2":
                    System.out.print("Enter destination: ");
                    String chosenDestination = scan.nextLine();
                    getTicketsFromDBByDestination(chosenDestination);

                    System.out.println();
                    for (int i = 0; i < ticketsToDestination.size(); i++) {
                        System.out.print((i + 1) + ". ");
                        System.out.print(ticketsToDestination.get(i));
                    }

                    if(ticketsToDestination.isEmpty()){
                        System.out.println("There aren't available tickets for the" +
                                " required destination!");
                        break;
                    }

                    System.out.print("Do you want to make a reservation? (yes/no): ");
                    command = scan.nextLine();

                    if (command.equals("yes")) {
                        System.out.print("Choose ticket number: ");
                        int ticketNumber = Integer.parseInt(scan.nextLine());

                        if (ticketNumber <= ticketsToDestination.size()) {
                            Ticket chosenTicket = ticketsToDestination.get(ticketNumber - 1);

                            if (chosenTicket.getTicketPrice() <= user.getWallet() &&
                                chosenTicket.getAvailableTickets() > 0) {
                                user.withdrawMoney(chosenTicket.getTicketPrice());
                                user.addBookedTicketToDB(chosenTicket);
                                chosenTicket.setAvailableTickets(chosenTicket.getAvailableTickets() - 1);
                                System.out.println("The ticket is successfully booked.");
                            }else{
                                if(chosenTicket.getAvailableTickets() > 0){
                                    System.out.println("Your money balance isn't enough for" +
                                            " purchase! Current balance: " + user.getWallet());
                                }else{
                                    System.out.println("All tickets for this flight are sold!");
                                }
                            }
                        } else {
                            System.out.println("The chosen ticket doesn't exist!");
                        }
                    }
                    ticketsToDestination.clear();
                    break;
                case "3":
                    System.out.println("\n1.Change password.");
                    System.out.println("2.Change user information.");
                    System.out.println("3.Insert money");
                    System.out.println("4.Back.");
                    System.out.print("Choose option: ");
                    String option = scan.nextLine();

                    changeProfileInformation(option,user,scan);
                    break;
                case "4":
                    System.out.println("Your current balance is " + user.getWallet());
                    break;
                case "5":
                    System.out.println();
                    user = accountManager.accountOperator();

                    if (user == null) {
                        hasFinished = true;
                    }
                    break;
                case "6":
                    hasFinished = true;
                    break;
                default:
                    System.out.println("Wrong command!");
                    break;
            }
        }
    }

    public static void getTicketsFromDBByDestination(String destinationName) throws SQLException {
        connectToDatabase();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM ticket WHERE " +
                "destination_name like '" + destinationName + "'");

        while (rs.next()) {
            String departurePoint = rs.getString("departure_point");
            destinationName = rs.getString("destination_name");
            double ticketPrice = rs.getDouble("ticket_price");
            String dateAndTimeOfFlight = rs.getString("date_and_time_of_flight");
            int availableTickets = rs.getInt("available_tickets");

            if(availableTickets > 0){
                ticketsToDestination.add(new Ticket(departurePoint, destinationName,
                        ticketPrice, dateAndTimeOfFlight, availableTickets));
            }
        }
    }

    private static void changeProfileInformation(String option,UserAccount user, Scanner scan) throws SQLException {
        switch (option){
                //change password
            case "1":
                System.out.print("Enter old password: ");
                String oldPassword = scan.nextLine();

                if(oldPassword.equals(user.getPassword())){
                    System.out.print("Enter new password: ");
                    String newPassword = scan.nextLine();

                    System.out.print("Repeat new password: ");
                    String newPasswordRep = scan.nextLine();

                    if (newPassword.equals(newPasswordRep)){
                        user.setPassword(newPassword);
                    }else{
                        System.out.println("The two inputs doesn't match!");
                    }
                }else{
                    System.out.println("The password is wrong.");
                }
                break;
                //change user information
            case "2":
                try{
                    System.out.print("Enter fullname: ");
                    String fullname = scan.nextLine();
                    System.out.print("Enter personal number: ");
                    String personalNumber = scan.nextLine();
                    System.out.print("Enter email: ");
                    String email = scan.nextLine();

                    user.setFullName(fullname);
                    user.setPersonalNumber(personalNumber);
                    user.setEmail(email);
                    System.out.println("Information changed!");
                }catch (SQLException sql){
                    System.out.println("The information you entered is already" +
                            " occupied by another user.");
                }
                break;
                //add sum to user wallet
            case "3":
                System.out.print("Enter sum of money: ");
                double money = Double.parseDouble(scan.nextLine());
                user.setWallet(user.getWallet() + money);
                System.out.println("Your current balance is " + user.getWallet());
                break;
                //go back to previous menu
            case "4":
                break;
            default:
                System.out.println("Wrong option.");
        }
    }
}
