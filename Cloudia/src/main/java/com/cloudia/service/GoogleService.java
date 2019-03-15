package com.cloudia.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.io.InputStream;

@Service
@Qualifier("googledrive")
public class GoogleService implements FileService
{
    private static final String APPLICATION_NAME = "Projektron FIW Project";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials/googleCredentials.json";

    private Drive service;

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the googleCredentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, int orgId) throws IOException {
        // Load client secrets
        InputStream in = GoogleService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH+"/"+orgId)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receier).authorize("user");
    }


    public GoogleService()throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    }

    public GoogleService(int orgId)throws IOException, GeneralSecurityException
    {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, orgId))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void createTicketFolder(String ticketId, String ticketTitle) throws IOException
    {
        if(!this.getTicketExists(ticketId)) //diese Abfrage ist bei google nötig, da sonst beim ändern eines tickets ein neuer ordner mit dem gleichen namen angelegt wird. dieser ist dann leer. und somit werden die dateien aus dem ticket nicht mehr angezeigt.
        {
            File fileMetadata = new File();
            fileMetadata.setName(ticketId+" "+ticketTitle);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            File file = this.service.files().create(fileMetadata).setFields("id, name").execute();
            System.out.printf("%s Folder ID(%s)\n", file.getName(), file.getId());
            this.makeWriter("projektron2018@gmail.com",file.getId());
        }

    }
    public void printFiles(List<File> files)
    {
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }
    public List<File> getAllFiles() throws IOException
    {
        FileList result = this.service.files().list().setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        this.printFiles(files);

        return files;
    }
    public List<File> getFilesOfFolder(String folderId) throws IOException
    {
        String search = "'"+folderId+"'"+ " in parents";
        FileList result = this.service.files().list().setQ(search).setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        return files;
    }
    public String getFolderId(String ticketId) throws IOException
    {
        String ticketSearch = "name contains " + "'" +ticketId+ "'" + " and mimeType = 'application/vnd.google-apps.folder'";
        FileList tickets = this.service.files().list().setQ(ticketSearch).setFields("nextPageToken, files(id, name)").execute();
        List<File> ticketFiles = tickets.getFiles();
        if(ticketFiles == null || ticketFiles.isEmpty()) return null;
        String folderId = ticketFiles.get(0).getId();

        return folderId;
    }
    public List<String> getFilesOfTicket(String ticketId) throws IOException
    {
        String folderId = this.getFolderId(ticketId);

        List<File> files = this.getFilesOfFolder(folderId);
        this.printFiles(files);
        List<String> fileNames = new ArrayList<String>();
        for (File file : files)
        {
            fileNames.add(file.getName());
        }
        return fileNames;
    }
    public void uploadFile(String pathname, String fileName) throws IOException
    {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        java.io.File filePath = new java.io.File(pathname);
        String mimeType = "";
        FileContent mediaContent = new FileContent(mimeType, filePath);
        File file = this.service.files().create(fileMetadata, mediaContent).setFields("id, name").execute();
        System.out.printf("%s File ID(%s)\n", file.getName(), file.getId());
        //mediaContent.getFile();
    }


    public void uploadFileToTicket(String ticketId, java.io.File uploadedFile, String fileName) throws IOException
    {
        if(this.getFileExists(ticketId,fileName))
        {
            this.updateFile(ticketId,fileName,uploadedFile);
        }
        else
        {
            String folderId = this.getFolderId(ticketId);

            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(Collections.singletonList(folderId));
            //java.io.File uploadedFile = new java.io.File(pathname);
            String mimeType = "";
            FileContent mediaContent = new FileContent(mimeType, uploadedFile);
            File file = this.service.files().create(fileMetadata,mediaContent).setFields("id, parents, name").execute();
            System.out.printf("%s File ID(%s)\n", file.getName(), file.getId());
            //mediaContent.getFile();
        }
    }
    public boolean getFileExists(String ticketId, String fileName) throws IOException
    {
        String fileId = this.getFileId(this.getFolderId(ticketId), fileName);
        if(fileId == null) return false;
        else return true;
    }
    public String getFileId(String folderId, String fileName) throws IOException
    {
        String fileSearch ="'"+folderId+"'"+" in parents and name = "+"'"+fileName+"'"+
                " and mimeType != 'application/vnd.google-apps.folder'";
        FileList files = this.service.files().list().setQ(fileSearch).setFields("nextPageToken, files(id, name)").execute();
        List<File> fileList = files.getFiles();
        if(fileList == null || fileList.isEmpty()) return null;
        String fileId = fileList.get(0).getId();

        return fileId;
    }

    public void downloadFileFromTicket(String ticketId, String fileName, OutputStream outputStream) throws IOException
    {
        String fileId = this.getFileId(this.getFolderId(ticketId), fileName);

        File file = service.files().get(fileId).setFields("mimeType").execute();

        String mimeType = file.getMimeType();

        String compare = "application/vnd.google-apps"; //testet ob das file ein google docs file ist

        int length;
        if(mimeType.length()<compare.length()) length = mimeType.length();
        else length = compare.length();

        if(mimeType.substring(0,length).equals(compare))
        {
            this.service.files().export(fileId,"application/pdf").executeMediaAndDownloadTo(outputStream);
            //exportieren von google docs als pdf
        }
        else
        {
            this.service.files().get(fileId).executeMediaAndDownloadTo(outputStream); //funktoniert nicht für google docs
        }
    }

    public void updateFile(String ticketId, String fileName, java.io.File uploadedFile)throws IOException
    {
        String fileId = this.getFileId(this.getFolderId(ticketId), fileName);
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        //java.io.File filePath = new java.io.File(pathname);
        String mimeType = "";
        FileContent mediaContent = new FileContent(mimeType, uploadedFile);
        File file = this.service.files().update(fileId, fileMetadata, mediaContent).execute();
        System.out.printf("%s File ID(%s)\n", file.getName(), file.getId());
        //mediaContent.getFile();
    }
    public void renameFile(String ticketId, String fileName, String newFileName) throws IOException
    {
        String fileId = this.getFileId(this.getFolderId(ticketId), fileName);
        File fileMetadata = new File();
        fileMetadata.setName(newFileName);
        String mimeType = "";
        File file = this.service.files().update(fileId, fileMetadata).execute();
        System.out.printf("%s File ID(%s)\n", file.getName(), file.getId());
        //mediaContent.getFile();
    }
    public void deleteFile(String ticketId, String fileName)
    {
        try {
            String fileId = this.getFileId(this.getFolderId(ticketId),fileName);
            service.files().delete(fileId).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }

    }
    public void deleteTicketFolder(String ticketId)
    {
        try
        {
            String folderId = this.getFolderId(ticketId);
            service.files().delete(folderId).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }


    public boolean getTicketExists(String ticketId) throws IOException
    {
        String id = this.getFolderId(ticketId);
        if(id == null) return false;
        else return true;
    }

    public List searchFilesByName(String name) throws IOException
    {
        String search = "name contains "+"'"+name+"'" + " and mimeType != 'application/vnd.google-apps.folder'";
        FileList result = this.service.files().list().setQ(search).setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        this.printFiles(files);
        List<String> fileNamesAndFolder = new ArrayList<String>();

        for (File file : files)
        {
            String fileId = file.getId();
            File foundFile = service.files().get(fileId)
                    .setFields("parents, name")
                    .execute();

            String folderId = foundFile.getParents().get(0);

            File folder = service.files().get(folderId)
                    .setFields("id, name")
                    .execute();

            System.out.println(folder.getName());

            fileNamesAndFolder.add(foundFile.getName());
            fileNamesAndFolder.add(folder.getName());
        }
        return fileNamesAndFolder;
    }

    public void makeWriter(String email, String fileId) throws IOException {
        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission, HttpHeaders responseHeaders)     throws IOException {
                System.out.println("Permission ID: " + permission.getId());
            }
        };
        BatchRequest batch = this.service.batch();
        Permission userPermission = new Permission().setType("user").setRole("writer").setEmailAddress(email);
        this.service.permissions().create(fileId, userPermission).setFields("id").queue(batch, callback);

        batch.execute();
    }

    public static void main(String... args) throws IOException, GeneralSecurityException
    {
        // Build a new authorized API client service.
           GoogleService test = new GoogleService(15);
           test.getAllFiles();
         // test.downloadFileFromTicket("172","test.png");

        //uplaoding file
        // String fileId     = test.uploadFile("files/photo1.jpg","photo.jpg");
        //  String fileId2  = test.uploadFile("files/text.txt","");

        //creating a new Folder
        //String fileId3    = test.createFolder("Invoices");

        //inserting file into folder
        //String fileId4    = test.uploadTo("10RHBE_nnL3ZMMJqBhX2_hFbnjkgPHsFw","files/text.txt","text.txt","text/plain");

        // Print the names and IDs for up to 10 files.
        // test.getAllFiles();
        //test.getFilesOfTicket("Bilder");
        // test.downloadFileFromTicket("Bilder","photoInOrdner.jpg","files/DownladPhoto.jpg");
        //test.updateFile("1X_IC0FlLlYU8QwD3T2EkrdAK8pum9JHL","files/plain.txt","plain.txt","text/plain");
        //downloading file


        // String fileId5  = test.updateFile("1X_IC0FlLlYU8QwD3T2EkrdAK8pum9JHL","files/text.txt","text.txt","text/plain");


        //  test.deleteFile(fileId2);
        //test.printFiles();

        //test.printFiles();
        /*String ticketId = "Ticket001";

        test.createTicketFolder(ticketId);

        test.getAllFiles();

        test.uploadFileToTicket(ticketId,"files/test.txt","test.txt");
        test.updateFile(ticketId,"test.txt","files/test2.txt");
        test.renameFile(ticketId,"test.txt","test2.txt");
        test.downloadFileFromTicket(ticketId,"test2.txt","files/test.txt");
        test.getFilesOfTicket(ticketId);
        test.deleteFile(ticketId,"test2.txt");
        test.getFilesOfTicket(ticketId);
        test.deleteTicketFolder(ticketId);*/
        //  test.getFilesOfTicket("50");
        //test.deleteTicketFolder("Unterordner");
        //test.deleteFile("Unterordner","Kopie von photoInOrdner.jpg");
        //test.searchFilesByName("photoInOrdner");
        // test.downloadFileFromTicket("0","photoInOrdner.jpg","files/photo2");
        //test.getAllFiles();
    }
}

