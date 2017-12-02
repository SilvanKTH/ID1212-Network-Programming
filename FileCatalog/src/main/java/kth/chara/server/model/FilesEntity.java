package kth.chara.server.model;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(
                name = "findFileByName",
                query = "SELECT file FROM FilesEntity file WHERE file.filename LIKE :filename"
        ),
        @NamedQuery(
                name = "deleteFileByName",
                query = "DELETE FROM FilesEntity file WHERE file.filename LIKE :filename"
        ),
        @NamedQuery(
                name = "getClientFiles",
                query = "SELECT file FROM FilesEntity file WHERE file.owner.username = :username OR file.access = 'public'"
        )
})

/**
 * Handle one file entity.
 */

@Entity(name = "FilesEntity")
@Table(name = "FILES", schema = "APP")
public class FilesEntity {
    private String filename;
    private long size;
    private ClientHandler owner;
    private String access;
    private String permissions;

    public FilesEntity(){}

    public FilesEntity(String filename, long size, ClientHandler owner, String access, String permissions){
        this.filename = filename;
        this.size = size;
        this.owner = owner;
        this.access = access;
        this.permissions = permissions;
    }

    @Id
    @Column(name = "FILENAME", nullable = false)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "SIZE", nullable = false)
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Many files can correspond to one owner.
     * @return The owner of this specific file.
     */

    @ManyToOne
    @JoinColumn(name = "OWNER", nullable = false)
    public ClientHandler getOwner() {
        return owner;
    }

    public void setOwner(ClientHandler owner) {
        this.owner = owner;
    }

    @Basic
    @Column(name = "ACCESS", nullable = false)
    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @Basic
    @Column(name = "PERMISSIONS", nullable = false)
    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
 }
