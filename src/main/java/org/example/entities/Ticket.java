package org.example.entities;

import java.sql.Date;
import java.time.LocalDate;

public class Ticket {
    private int TicketID; //
    private int EventID;
    private int ClientID;
    private int Quantity;
    private String Type;
    private Date PurchaseDate;

    public Ticket(int id, int eventId, int cliId, int quantity, String type, Date purchaseDate){
        TicketID = id;
        EventID = eventId;
        ClientID = cliId;
        Quantity = quantity;
        Type = type;
        PurchaseDate = purchaseDate;
    }
    
    public Ticket(int id, int eventID, int cliId, int quantity, String type){
        this(id,eventID,cliId,quantity,type, Date.valueOf(LocalDate.now()));
    }

    /// Getters and Setters.

    public int getTicketID() {
        return TicketID;
    }

    public void setTicketID(int ticketID) {
        TicketID = ticketID;
    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Date getPurchaseDate() {
        return PurchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        PurchaseDate = purchaseDate;
    }
}
