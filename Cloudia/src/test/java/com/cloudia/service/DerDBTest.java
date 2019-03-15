package com.cloudia.service;

import com.cloudia.service.DropboxService;
import com.cloudia.service.FileService;
import com.dropbox.core.DbxException;
import org.junit.Assert;
import org.junit.Test;
import java.io.*;
import java.security.GeneralSecurityException;

/**
 *  Dieser Test Funktioniert nicht komplett. Die Dateien muessen manuell entfernt werden.
 *  Die Datei wird erfolgreich in den erstellten Ticket Ordner hochgeladen,
 *  jedoch wird sie nicht mehr aus dem Ticket heraus runtergeladen.
 */

public class DerDBTest
{
    // parameter fuer den test

    // die id vom ticket (ordner)
    private String ticketId = "00";

    // file path fuer ticket ordner in cloud
    private String dropboxFile = "00/ersteTextdatei.txt";

    // ticket path fuer dropbox
    private String dropboxTicketPath = "home/00";

    // der titel des tickets
    private String ticketTitle = "TestVonVanessa";

    // der name der ersten datei, welche zum testen erstellt wird
    private String firstFileName = "ersteTestdatei.txt";

    // der pfad der ersten datei, welche zum testen uploaded wird
    private String uploadFilePath = "src/test/java/com/projektron/service/toUpload/ersteTestdatei.txt";

    // der pfad der ersten datei, welche zum testen downloaded wird
    private String firstFilePath = "src/test/java/com/projektron/service/ersteTestdatei.txt";

    // der pfad der zweiten datei, welche zum testen verwendet wird
    private String secondFilePath = "src/test/java/com/projektron/service/zweiteTestdatei.txt";

    // inhalt der textdatei
    private String textinhalt = "hallo das ist vanessas test :)";

    private FileService fileService;

    private void test(FileService fileService) throws IOException, GeneralSecurityException, DbxException
    {
        // DROPBOX: erste text datei zum testen im extra ordner erstellen
        PrintWriter firstName = new PrintWriter(uploadFilePath);
            firstName.println(textinhalt);
            firstName.close();

        // zweite text datei erstellen
        PrintWriter secondName = new PrintWriter(secondFilePath);
            secondName.println(textinhalt);
            secondName.close();

        // neues ticket erstellen
        fileService.createTicketFolder(ticketId, ticketTitle);

        // neue uploadedFile fuer parameter
        java.io.File firstUploadedFile = new java.io.File(uploadFilePath);

        // erste test datei in das ticket hochladen
        fileService.uploadFileToTicket(ticketId, firstUploadedFile, dropboxFile);

        // neuer outputstream fuer parameter
        FileOutputStream einOutputstream = new FileOutputStream(firstFilePath);

        // erste datei runterladen
        fileService.downloadFileFromTicket(ticketId, firstFileName, einOutputstream);

        // string aus runtergeladener datei nehmen
        BufferedReader firstReader = new BufferedReader(new FileReader(firstFilePath));
        String firstFileinhalt = firstReader.readLine();

        // string aus anderer datei mit selbem inhalt nehmen
        BufferedReader secondReader = new BufferedReader(new FileReader(secondFilePath));
        String newFileinhalt = secondReader.readLine();

        // runtergeladene datei mit erster originalen vergleichen
        Assert.assertEquals(firstFileinhalt, newFileinhalt);
    }

    // eigentlicher test der obigen funktionen


    @Test
    public void dropboxTest() throws IOException, DbxException, GeneralSecurityException, java.lang.NullPointerException
    {
        fileService = new DropboxService();
        test(fileService);
        test(new DropboxService());
    }

    /** cleanup nach test
    @After
    public void cleanup() throws IOException
    {
        // ticket ordner loeschen (wenn funktion verfuegbar)
        //fileService.deleteTicketFolder(ticketId);

        // lokale dateien loeschen
        Files.delete(Paths.get(uploadFilePath));
        Files.delete(Paths.get(firstFilePath));
        Files.delete(Paths.get(secondFilePath));
    }
    **/
}