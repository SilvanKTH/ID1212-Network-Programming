package kth.chara.server.model;

import kth.chara.server.integration.FileSystemDAO;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All the file system handling.
 */

public class FilesSystem {
    private FileSystemDAO fileSystemDAO;
    private final List<StatusInfo> statusInfos = new ArrayList<>();

    public FilesSystem(){fileSystemDAO = FileSystemDAO.getThis();}

    public boolean upload(String filename, long size, ClientHandler clientHandler, String access, String permissions) throws IOException {
        FilesEntity filesEntity = fileSystemDAO.findFileByName(filename, false);
        //case the file is not in the database
        if (filesEntity == null){
          fileSystemDAO.writeFile(new FilesEntity(filename, size, clientHandler, access, permissions));
          return true;
        }
        //case the file is in the database and we are the owners that want to update the file
        else if (filesEntity.getOwner().getUsername().equals(clientHandler.getUsername())){
            try {
                File file = new File("src/ServerDirectory/" + filename);
                boolean deleted = file.delete();
            } catch (Exception e){
                System.out.println("Something went wrong while trying to delete and update the file");
            }

            filesEntity.setSize(size);
            filesEntity.setAccess(access);
            filesEntity.setPermissions(permissions);
            fileSystemDAO.updateDB();
            return true;
        }
        //case the file is in the database but it's not ours, but it's public with write permissions.
        else if (filesEntity.getAccess().equals("public") && filesEntity.getPermissions().equals("write")){
            try {
                File file = new File("src/ServerDirectory/" + filename);
                boolean deleted = file.delete();
            } catch (Exception e){
                System.out.println("Something went wrong while trying to delete and update the file");
            }

            filesEntity.setSize(size);
            //avoid user mistyping trying to change the access and permissions
            filesEntity.setAccess("public");
            filesEntity.setPermissions("write");
            fileSystemDAO.updateDB();
            assignStatusList(filename, filesEntity.getOwner().getUsername(), clientHandler.getUsername(), "update");
            return true;
        }
        //case we are not allowed to change the file
        else {
            return false;
        }
    }

    public boolean download(String filename, ClientHandler clientHandler) throws IOException {
        FilesEntity filesEntity = fileSystemDAO.findFileByName(filename, true); //we won't change something else in DB, so commit
        //case file not in the database
        if (filesEntity == null){return false;}
        //case file is public and we are not the owners
        //increase the retrieve list of this specific file
        if (filesEntity.getAccess().equals("public") &&
                !filesEntity.getOwner().getUsername().equals(clientHandler.getUsername())){
            assignStatusList(filename,filesEntity.getOwner().getUsername(), clientHandler.getUsername(), "retrieve");
        }
        //return true when file is private and we are the owners or file is public
        return !filesEntity.getAccess().equals("private") ||
                filesEntity.getOwner().getUsername().equals(clientHandler.getUsername());
    }

    public boolean delete(String filename, ClientHandler clientHandler) throws FileException {
        FilesEntity filesEntity = fileSystemDAO.findFileByName(filename, false);
        //case not found in DB
        if (filesEntity == null) {return false;}
        //case it is private and we are not the owners
        if (filesEntity.getAccess().equals("private") &&
                !filesEntity.getOwner().getUsername().equals(clientHandler.getUsername())){
            return false;
        }
        //case it is public with read permissions and we are not the owners
        if (filesEntity.getAccess().equals("public") &&
                filesEntity.getPermissions().equals("read") &&
                !filesEntity.getOwner().getUsername().equals(clientHandler.getUsername())){
            return false;
        }
        //if the previous are false then we can delete the file from server directory and DB
        try {
            assignStatusList(filename, filesEntity.getOwner().getUsername(), clientHandler.getUsername(), "delete");
            File file = new File("src/ServerDirectory/" + filename);
            boolean deleted = file.delete();
            fileSystemDAO.deleteFileByName(filesEntity);
            return true;
        } catch (Exception e){
            System.out.println("Something went wrong while trying to delete the file");
        }
        return false;
    }

    public String listFiles(ClientHandler clientHandler){
        List<FilesEntity> filesEntities = fileSystemDAO.getClientFiles(clientHandler.getUsername());
        if (filesEntities.isEmpty()) {
            return "No files to show";
        } else {
            StringBuilder visibleFiles = new StringBuilder();
            for (FilesEntity filesEntity : filesEntities){
                visibleFiles.append("File: ");
                visibleFiles.append(filesEntity.getFilename());
                visibleFiles.append(", size: ");
                visibleFiles.append(filesEntity.getSize());
                visibleFiles.append(" bytes, owner: ");
                visibleFiles.append(filesEntity.getOwner().getUsername());
                visibleFiles.append(", access: ");
                visibleFiles.append(filesEntity.getAccess());
                visibleFiles.append(", permission: ");
                visibleFiles.append(filesEntity.getPermissions());
                visibleFiles.append("\n");
            }
            return visibleFiles.toString();
        }
    }

    public String fileInfo(String filename, ClientHandler clientHandler) {
        boolean found = false;
        StringBuilder fileInfos = new StringBuilder();
        for (StatusInfo statusInfo: statusInfos){
            if (statusInfo.getOwner().equals(clientHandler.getUsername()) && statusInfo.getFilename().equals(filename)){
                found = true;
                fileInfos.append("Retrieved by: ");
                if (!statusInfo.getRetrieveList().isEmpty()) {
                    fileInfos.append(statusInfo.getRetrieveList());
                } else {fileInfos.append("none");}
                fileInfos.append(", updated by: ");
                if (!statusInfo.getUpdateList().isEmpty()) {
                    fileInfos.append(statusInfo.getUpdateList());
                } else {fileInfos.append("none");}
                fileInfos.append(", deleted by: ");
                if (!statusInfo.getDeleteList().isEmpty()) {
                    fileInfos.append(statusInfo.getDeleteList());
                } else {fileInfos.append("none");}
                break;
            }
        }
        if (found){
            return fileInfos.toString();
        } else {
            return "Nothing to show!";
        }
    }

    private void assignStatusList(String filename, String owner, String client, String action){
        boolean found = false;
        for (StatusInfo statusInfo : statusInfos){
            if (statusInfo.getFilename().equals(filename)){
                switch (action){
                    case "update": statusInfo.setUpdateList(client); found=true; break;
                    case "delete": statusInfo.setDeleteList(client); found=true; break;
                    case "retrieve": statusInfo.setRetrieveList(client); found=true; break;
                }
            }
        }
        if (!found){
            StatusInfo statusInfo = new StatusInfo(filename, owner);
            switch (action){
                case "update": statusInfo.setUpdateList(client); break;
                case "delete": statusInfo.setDeleteList(client); break;
                case "retrieve": statusInfo.setRetrieveList(client); break;
            }
            statusInfos.add(statusInfo);
        }
    }


}
