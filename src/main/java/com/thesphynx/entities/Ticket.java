package com.thesphynx.entities;

public class Ticket {
    private int TicketID; //
    private int EventID;
    private int ClientID;
    private int NbPerson;
    private String Type;

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
}
