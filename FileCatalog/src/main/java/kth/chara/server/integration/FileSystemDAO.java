package kth.chara.server.integration;

import kth.chara.server.model.clientHandling.ClientException;
import kth.chara.server.model.clientHandling.ClientHandler;
import kth.chara.server.model.fileHandling.FileException;
import kth.chara.server.model.fileHandling.FilesEntity;

import javax.persistence.*;
import java.util.List;

/**
 * This data access object (DAO) encapsulates all database calls in the file catalog application.
 * No code outside this class shall have any knowledge about the database.
 */

public class FileSystemDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();
    private static FileSystemDAO fileSystemDAO;

    /**
     * Constructs a new DAO object connected to the specified database.
     */

    private FileSystemDAO(){
        entityManagerFactory = Persistence.createEntityManagerFactory("persistenceUnitFilesystem");
    }

    /**
     * The specific instance of the DAO.
     * @return The instance.
     */

    public static FileSystemDAO getThis(){
        if (fileSystemDAO == null) {fileSystemDAO = new FileSystemDAO();}
        return fileSystemDAO;
    }

    /**
     * Searches for a client by the given username
     * @param username The username to search in DB
     * @return The ClientHandler object.
     */

    public ClientHandler findCLientByName(String username){
        if (username == null) {return null;}
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findClientByName", ClientHandler.class).
                        setParameter("username", username).getSingleResult();
            } catch (NoResultException noSuchClient) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }

    public void register(ClientHandler clientHandler) {
        try {
            EntityManager em = beginTransaction();
            em.persist(clientHandler);
        }
        finally{
            commitTransaction();
        }
    }

    public void unregister(String username) throws ClientException {
        try{
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteClientByName",ClientHandler.class).
                    setParameter("username",username).executeUpdate();
        } finally{
            commitTransaction();
        }
    }

    private EntityManager beginTransaction() {
        EntityManager em = entityManagerFactory.createEntityManager();
        threadLocal.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }

    private void commitTransaction() {
        threadLocal.get().getTransaction().commit();
    }

    /**
     * Searches the DB for a specific file by the given filename.
     * @param filename The name of the file to search.
     * @param finishTransaction Whether the transaction should commit after searching for
     *                          a file. Set to <code>true</code> if no further action is taken on the
     *                          file. Set to <code>false</code> if more operations,
     *                          are to be performed on the found file.
     * @return The file entity with the given filename.
     */

    public FilesEntity findFileByName(String filename, boolean finishTransaction) {
        if (filename == null){return null;}
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByName", FilesEntity.class).
                        setParameter("filename", filename).getSingleResult();
            } catch (NoResultException fileNotFound){
                return null;
            }
        } finally {
            if (finishTransaction){
                commitTransaction();
            }
        }
    }

    /**
     * Deletes a file from DB.
     * @param filesEntity The file entity to delete
     * @throws FileException In case of file exception.
     */

    public void deleteFileByName(FilesEntity filesEntity) throws FileException {
        String filename = filesEntity.getFilename();
        try {
            EntityManager em = beginTransaction();
            int f = em.createNamedQuery("deleteFileByName", FilesEntity.class).setParameter("filename", filename).executeUpdate();
            if (f != 1){
                throw new FileException("File " + filename + " was not deleted!");
            }
        } finally {
            commitTransaction();
        }
    }

    /**
     * Get all appropriate files from DB according to the username.
     * @param username The username of the client.
     * @return A list with file entities for the corresponding client.
     */

    public List<FilesEntity> getClientFiles(String username){
        try {
            EntityManager em = beginTransaction();
            return em.createNamedQuery("getClientFiles", FilesEntity.class).setParameter("username", username).getResultList();
        } finally {
            commitTransaction();
        }
    }

    public void writeFile(FilesEntity filesEntity) {
        try {
            EntityManager em = beginTransaction();
            em.persist(filesEntity);
        } finally {
            commitTransaction();
        }
    }

    public void updateDB(){
        commitTransaction();
    }
}
