package com.cloudia.repository;

import com.cloudia.model.Organization;
import com.cloudia.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository("ticketRepository")
public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    @Override
    @Transactional(timeout = 10)
    List<Ticket> findAll();

    @Override
    Ticket getOne(Integer id);

    @Override
    void deleteById(Integer id);

    List<Ticket> findAllTicketsByOrganizationId(Organization id);

}
