package org.example.services;

import org.example.entities.Ticket;
import org.example.utils.MyDataBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketService implements ICrud<Ticket> {

    public Ticket ajouterEtRetourner(Ticket ticket) throws SQLException {
        String req = "INSERT INTO tickets (event_id, client_id, quantity, type, purchase_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = MyDataBase.getInstance().getConnection().prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getEventID());
            ps.setInt(2, ticket.getClientID());
            ps.setInt(3, ticket.getQuantity());
            ps.setString(4, ticket.getType());
            ps.setDate(5, ticket.getPurchaseDate());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setTicketID(rs.getInt(1));
                }
            }
            return ticket;
        }
    }

    @Override
    public void ajouter(Ticket ticket) throws SQLException {
        String req="insert into tickets (event_id, client_id, quantity, type, purchase_date)"+
                "values(?,?,?,?,?)";
        try (PreparedStatement preparedStatement = MyDataBase.getInstance().getConnection().prepareStatement(req)) {
            preparedStatement.setInt(1, ticket.getEventID());
            preparedStatement.setInt(2, ticket.getClientID());
            preparedStatement.setInt(3, ticket.getQuantity());
            preparedStatement.setString(4, ticket.getType());
            preparedStatement.setDate(5, ticket.getPurchaseDate());

            preparedStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println("Error in ticket prepared statement: " + e.getMessage());
        }
        System.out.println("Ticket Added successfully");
    }

    @Override
    public void modifier(Ticket ticket) throws SQLException {
        String req="update tickets set event_id = ?, client_id = ?, quantity = ?, type = ?, purchase_date = ?  where ticket_id=?";
        try (PreparedStatement preparedStatement = MyDataBase.getInstance().getConnection().prepareStatement(req)) {
            preparedStatement.setInt(1, ticket.getEventID());
            preparedStatement.setInt(2, ticket.getClientID());
            preparedStatement.setInt(3, ticket.getQuantity());
            preparedStatement.setString(4, ticket.getType());
            preparedStatement.setDate(5, ticket.getPurchaseDate());

            preparedStatement.setInt(6, ticket.getTicketID());

            preparedStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println("Error in ticket prepared statement: " + e.getMessage());
            return;
        }
        System.out.println("Ticket Modified successfully");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM tickets WHERE ticket_id = ?";
        try (PreparedStatement preparedStatement = MyDataBase.getInstance().getConnection().prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Ticket with ID " + id + " deleted successfully");
            } else {
                System.out.println("No ticket found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting ticket: " + e.getMessage());
            throw e; // Re-throw the exception for the caller to handle
        }
    }

    public List<Ticket> afficher() throws SQLException {
        List<Ticket> Tickets = new ArrayList<>();
        String req="select * from tickets";
        ResultSet rs;
        try (Statement statement = MyDataBase.getInstance().getConnection().createStatement()) {

            rs = statement.executeQuery(req);
            while (rs.next()){
                Ticket ticket= new Ticket(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4),
                        rs.getString(5));
                Tickets.add(ticket);
            }
        }


        return Tickets;
    }

    public List<Ticket> getAll() throws SQLException{
        return afficher();
    }
}
