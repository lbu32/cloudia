package com.cloudia.controller;
import com.cloudia.model.Organization;
import com.cloudia.model.Ticket;
import com.cloudia.model.User;
import com.cloudia.service.*;
import com.cloudia.repository.TicketRepository;
import com.projektron.service.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


/**
 * Created by Lena on 27.10.2018
 */
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private TicketRepository ticketrepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    public FileService getFileService(Organization organization) throws IOException, GeneralSecurityException {

        if(organization.getCloud().getCloudId()==1)
        {
            int orgId = organization.getOrganizationId();
            return new GoogleService(orgId);
        }

        else if(organization.getCloud().getCloudId()==2)
        {
            String token = organization.getOrganizationToken();
            return new DropboxService(token);
        }

        else
            return null;


    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    //Zeige Liste von Tickets
    @RequestMapping(value = "/admin/tickets", method = RequestMethod.GET)
    public String ticketsAll(Model model, User user){
        model.addAttribute("ticket", ticketService.findAllTickets());
        // model.addAttribute("ticket", ticketService.findAllTicketsOfMyOrganization());
        return "admin/tickets";
    }
    //Ticketdetails
    @RequestMapping("tickets/{id}")
    public String showTicket(@PathVariable Integer id,
                             Model model,
                             @AuthenticationPrincipal UserDetails activeUser) throws IOException, GeneralSecurityException
    {
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);
        //Erhalte die zum User gehörende Organization
        Organization orgId = user.organization();

        model.addAttribute("ticket", ticketService.getTicketById(id));

        model.addAttribute("files", getFileService(orgId).getFilesOfTicket(""+id));

        //model.addAttribute("downloadFile",getFileService().downloadFileFromTicket(id,"photo.jpg","files/"));

        model.addAttribute("fileService",getFileService(orgId));
        return "ticketdetails";
    }
    //Zeige alle Tickets des eingeloggten Users
    @RequestMapping(value="/user/ticketsmeinerorganisation", method = RequestMethod.GET)
    public String showTicketsByOrg(Model model, @AuthenticationPrincipal UserDetails activeUser)
    {
        //Erhalte den aktuell eingeloggten User
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);
        //Erhalte die zum User gehörende Organization
        Organization userOrg;
        userOrg = user.organization();


        List<Ticket> t;
        t = ticketService.findAllTicketsByOrganizationId(userOrg);

        model.addAttribute("ticketByOrg", t);
        return "/user/ticketsmeinerorganisation";
    }



    //Tickets löschen
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable Integer id){
        ticketService.deleteById(id);
        return "redirect:/admin/tickets";
    }
    /**
     // Edit Ticket
     @RequestMapping("ticketEdit/{id}")
     public String edit(@PathVariable Integer id, Model model){
     model.addAttribute("ticket", ticketService.getTicketById(id));
     return "formular";
     }
     **/
    // Edit Ticket - Lade Bearbeiten Seite
    @RequestMapping(value = "ticketEdit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable Integer id, Model model){
        model.addAttribute("ticket", ticketService.getTicketById(id));
        return "bearbeiten";
    }

    /**
     //Update Ticket - Ändere Ticket
     @RequestMapping(value = "ticket", method = RequestMethod.POST)
     public String update(Ticket ticket){
     ticketService.saveTicket(ticket);
     return "redirect:/user/tickets";

     }

     */
    //  !!!!!!!!!
    //Update Ticket - Ändere Ticket
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String update(Model model, Ticket ticket, @AuthenticationPrincipal UserDetails activeUser) {
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);

        //Erhalte die zum User gehörende Organization
        Organization userOrg;
        userOrg = user.organization();

        model.addAttribute("updatedticket", ticketService.saveTicket(ticket, userOrg));

        return "redirect:tickets/{id}";
    }

    //Datei suche
    @RequestMapping(value = "suche", method = RequestMethod.GET)
    public String suche(Ticket ticket) {
        return "suche";
    }

    //erstelle ein neues Ticket
    @RequestMapping(value = "/formular", method = RequestMethod.POST)
    public ModelAndView saveTicket(Ticket ticket, @AuthenticationPrincipal UserDetails activeUser) throws IOException, GeneralSecurityException {
        ModelAndView modelAndView = new ModelAndView();
        //Erhalte den aktuell eingeloggten User
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);

        //Erhalte die zum User gehörende Organization
        Organization userOrg;
        userOrg = user.organization();


        //Erhalte die zum User gehörende Rolle
        Integer userRoleId;
        userRoleId= user.getRole().getRoleId();

        ticketService.saveTicket(ticket, userOrg);
        modelAndView.addObject("successMessage", "Das neue Ticket wurde angelegt!");
        modelAndView.addObject("ticket", new Ticket());
        boolean folderExists = false;
        getFileService(userOrg).createTicketFolder(""+ticket.getTicketId(),ticket.getTicketTitle());

        if(userRoleId==1){
            modelAndView.setViewName("redirect:admin/tickets");
        }else if(userRoleId==2){
            modelAndView.setViewName("redirect:user/ticketsmeinerorganisation");
        }
        return modelAndView;
    }

    //Create
    // Lade das Ticketformular
    @RequestMapping(value="/formular", method = RequestMethod.GET)
    public ModelAndView createTickets(@AuthenticationPrincipal UserDetails activeUser)
    {
        ModelAndView modelAndView = new ModelAndView();
        //Erhalte den aktuell eingeloggten User
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);

        //Erhalte die zum User gehörende Organization
        Organization userOrg;
        userOrg = user.organization();

        if (userOrg != null) {

            Ticket ticket = new Ticket();
            modelAndView.addObject("ticket", ticket);
            modelAndView.setViewName("formular");
        }
        else{
            modelAndView.addObject("permissionDenied", "Sie können erst Tickets anlegen, wenn Sie einer Organisation angehören.");
            modelAndView.setViewName("home");
        }

        return modelAndView;
    }


    @RequestMapping(value="/downloadfile", method = RequestMethod.GET)
    public void performFileDownload(
            @RequestParam("ticketId") String ticketId,
            @RequestParam("fileName") String fileName,
            @AuthenticationPrincipal UserDetails activeUser,
            HttpServletResponse response) throws IOException, GeneralSecurityException {
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);
        //Erhalte die zum User gehörende Organization
        Organization orgId = user.organization();

        response.setHeader("Content-Disposition", "attachment; filename="+fileName);

        getFileService(orgId).downloadFileFromTicket(ticketId, fileName, response.getOutputStream());
    }

    @RequestMapping(value="/uploadfile/{id}", method = RequestMethod.POST)
    public String performFileUpload(
            Model model,
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails activeUser,
            @RequestParam("fileToUpload") MultipartFile multipart) throws IOException, GeneralSecurityException
    {
        String email= activeUser.getUsername();
        User user=userService.findUserByEmail(email);
        //Erhalte die zum User gehörende Organization
        Organization orgId = user.organization();
        model.addAttribute("ticket", ticketService.getTicketById(id));
        InputStream inputStream = multipart.getInputStream();
        File convFile = new File("files/"+multipart.getOriginalFilename());
        OutputStream outputStream = new FileOutputStream(convFile);
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
       /* InputStream inputStream = multipart.getInputStream();
        File convFile = new File(multipart.getOriginalFilename());
        multipart.transferTo(convFile);*/

        getFileService(orgId).uploadFileToTicket(""+id, convFile, convFile.getName());

        convFile.delete();
        return "redirect:../tickets/{id}";

    }


}
