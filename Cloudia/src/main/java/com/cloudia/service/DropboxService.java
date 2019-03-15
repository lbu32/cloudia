package com.cloudia.service;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("dropbox")
public class DropboxService implements FileService {
    //private static final String ACCESS_TOKEN = "JtFf6471OMAAAAAAAAAAC4vFllkqF5R8S6MoJKHELIvNzGfWFCVNaBcAbqvrBJzT";
    DbxRequestConfig config;
    DbxClientV2 client;

    public DropboxService() {
        config = DbxRequestConfig.newBuilder("Projetron").build();
    }

    public DropboxService(String ACCESS_TOKEN)
    {
        config = DbxRequestConfig.newBuilder("Projetron").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public void createTicketFolder(String ticketId, String ticketTitle) throws IOException {
        try {
            CreateFolderResult folder = client.files().createFolderV2("/" + ticketId);
           // System.out.println(folder.getName());
        } catch (CreateFolderErrorException err) {
            if (err.errorValue.isPath() && err.errorValue.getPathValue().isConflict()) {
                System.out.println("Something already exists at the path.");
            } else {
                System.out.print("Some other CreateFolderErrorException occurred...");
                System.out.print(err.toString());
            }
        } catch (Exception err) {
            System.out.print("Some other Exception occurred...");
            System.out.print(err.toString());
        }

    }

    public List<String> getFilesOfTicket(String ticketId) throws IOException{
        List<String> files = new LinkedList<String>();
         try {

             ListFolderResult listFolderResult = client.files().listFolderBuilder("/"+ticketId).start();
             while (true) {
                 for (Metadata metadata : listFolderResult.getEntries()) {
                     if (metadata instanceof FileMetadata) {
                         files.add(metadata.getName());
                     }
                 }

                 if (!listFolderResult.getHasMore()) {
                     break;
                 }

                 listFolderResult = client.files().listFolderContinue(listFolderResult.getCursor());
             }
             System.out.println(files.toString());
         }
         catch(ListFolderErrorException err)
         {
             System.out.print("Some other ListFolderErrorException occurred...");
             System.out.print(err.toString());
         }
         catch(DbxException err)
         {
             System.out.print("Some other DbxException occurred...");
             System.out.print(err.toString());
         }
        return files;
    }

    public void uploadFileToTicket(String ticketId, java.io.File filePath, String fileName) throws IOException {
        try (InputStream in = new FileInputStream(filePath)) {
            FileMetadata metadata = client.files().uploadBuilder("/"+ticketId+"/" + fileName).uploadAndFinish(in);
        } catch (UploadErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }

    }

    public void downloadFileFromTicket(String ticketId, String fileName, OutputStream outputStream) throws IOException {
        try
        {
            //output file for download --> storage location on local system to download file

            try {
                client.files().downloadBuilder("/"+ticketId+"/"+fileName).download(outputStream);

            } finally
            {
                outputStream.close();
                System.out.println("Download erfolgreich!");
            }
        }
        catch (DbxException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void updateFile(String ticketId, String fileName, File uploadedFile) throws IOException {

    }

    public void renameFile(String ticketId, String fileName, String newFileName) throws IOException {

    }


    public void deleteFile(String ticketId, String fileName) throws IOException{
            try {
                DeleteResult deleted = client.files().deleteV2(ticketId + fileName);
                System.out.println("Datei " + fileName+  ticketId + " wurde gelöscht");
            } catch (DeleteErrorException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }


    public void deleteTicketFolder(String ticketId)throws IOException{
        try {
            DeleteResult deleted = client.files().deleteV2("/"+ ticketId);
            System.out.println("Ordner " + ticketId + " wurde gelöscht");
        } catch (DeleteErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
    public List<SearchMatch> searchFilesByName(String name) throws ListFolderErrorException, DbxException {
        SearchResult result = this.client.files().searchBuilder("", name).start();
        List<SearchMatch> files = result.getMatches();
        System.out.println(files);
        return files;
    }
}
