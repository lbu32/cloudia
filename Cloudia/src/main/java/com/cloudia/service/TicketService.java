package com.cloudia.service;

/**
 * Created by Lena on 27.10.2019
 */
import com.cloudia.model.Organization;
import com.cloudia.model.Ticket;
import com.cloudia.repository.TicketRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service("ticketService")
public class TicketService {

    private TicketRepository ticketRepository;
    private UserDetails authuser;

    /**public Ticket getTicketById(Integer ticketId){
     return ticketRepository.findById(getTicketById(ticketId));
     }

    */
    @Autowired
    public TicketService(TicketRepository ticketRepository){

        this.ticketRepository = ticketRepository;
    }

    public Ticket getTicketById(Integer id) {
        return ticketRepository.getOne(id);
    }

    public List<Ticket> findAllTickets() {

        return ticketRepository.findAll();
    }

    public void deleteById(Integer id) {
        ticketRepository.deleteById(id);
    }

    public Ticket save(Ticket ticket){return ticketRepository.save(ticket);}

    public Ticket saveTicket(Ticket ticket, Organization owner) {
        ticket.setTicketTitle(ticket.getTicketTitle());
        ticket.setCreated(ticket.getCreated());
        ticket.setCreatorId(ticket.getCreator());
        ticket.setTicketDescription(ticket.getTicketDescription());

        //Hier muss Ticketorg =  org des users sein
        ticket.setOrganizationId(owner);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> findAllTicketsByOrganizationId(Organization id) {
        //Alle Tickets mit der Organization_id des Users
        return  ticketRepository.findAllTicketsByOrganizationId(id);
    }
}