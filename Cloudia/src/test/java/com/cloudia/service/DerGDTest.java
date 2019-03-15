package com.cloudia.service;

import com.cloudia.service.FileService;
import com.cloudia.service.GoogleService;
import com.dropbox.core.DbxException;
import org.junit.Assert;
import org.junit.Test;
import java.io.*;
import java.security.GeneralSecurityException;

/**
 *  Dieser Test Funktioniert nicht komplett. Die Dateien muessen manuell entfernt werden.
 *  Die Datei wird erfolgreich in den erstellten Ticket Ordner hochgeladen, jedoch doppelt.
 *  Des weiteren muss man manchmal die Berechtigungen neu hinzufuegen.
 *  Ansonsten funktioniert der Test.
 */

public class DerGDTest
{
    // parameter fuer den test

    // die id vom ticket (ordner)
    private String ticketId = "00";

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
        // GDRIVE: erste text datei zum testen im extra ordner erstellen
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
        fileService.uploadFileToTicket(ticketId, firstUploadedFile, firstFileName);

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
    public void googleTest() throws IOException, GeneralSecurityException, DbxException, java.lang.NullPointerException
    {
        fileService = new GoogleService();
        test(fileService);
        test(new GoogleService());
    }


    /** // cleanup nach test
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