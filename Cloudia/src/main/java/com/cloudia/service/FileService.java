package com.cloudia.service;



import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public interface FileService
{
    /**
     * creating a new Folder
     * @param ticketId
     * @return the folder ID
     * @throws IOException
     */
    void createTicketFolder(String ticketId, String ticketTitle) throws IOException;

    // Returns a list of all files belonging to a folder
    List<String> getFilesOfTicket(String ticketId)throws IOException;

    //inserting file into folder
    void uploadFileToTicket(String ticketId, java.io.File uploadedFile, String fileName) throws IOException;

    //downloading file
    void downloadFileFromTicket(String ticketId, String fileName, OutputStream outputStream) throws IOException;

    //update file: replace
    void updateFile(String ticketId, String fileName, File uploadedFile)throws IOException;

    void renameFile(String ticketId, String fileName, String newFileName)throws IOException;

    //deleting file
    void deleteFile(String ticketId, String fileName)throws IOException;

    //deleting folder
    void deleteTicketFolder(String ticketId)throws IOException;


}