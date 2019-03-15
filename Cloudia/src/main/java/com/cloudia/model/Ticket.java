package com.cloudia.model;

/**
 * Created by Lena on 22.10.2018.
 */

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ticket_id;
    private String ticketTitle;
    private String ticketDescription;

    @ManyToOne
    @JoinColumn(name = "organizationId")
    private Organization organizationId;

    @CreationTimestamp
    private Date created;

    @ManyToOne
    @JoinColumn(name = "creatorId")
    private User creator;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getTicketId(){
        return ticket_id;
    }

    public void setTicketId(int ticket_id){
        this.ticket_id = ticket_id;
    }

    public String getTicketTitle(){
        return ticketTitle;
    }

    public void setTicketTitle(String ticketTitle){
        this.ticketTitle = ticketTitle;
    }

    public String getTicketDescription(){
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription){
        this.ticketDescription = ticketDescription;
    }
//user ist hier der creator des tickets

    public User getCreator(){
        return creator;
    }

    public void setCreatorId(User creator){
        this.creator = creator;
    }

    public Organization getOrganizationId(){ return organizationId; }

    public void setOrganizationId(Organization owner) {this.organizationId = owner; }


}
