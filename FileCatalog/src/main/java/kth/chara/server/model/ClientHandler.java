package kth.chara.server.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Handle one unique client.
 */

@NamedQueries({
        @NamedQuery(
                name = "findClientByName",
                query = "SELECT client FROM ClientHandler client WHERE client.username LIKE :username"
        ),
        @NamedQuery(
                name = "deleteClientByName",
                query = "DELETE FROM ClientHandler client WHERE client.username LIKE :username"
        )
})

@Entity (name = "ClientHandler")
@Table(name = "CLIENTS", schema = "APP", catalog = "")
public class ClientHandler implements Serializable {
    @Id
    @Column(name = "CLIENTID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long clientId;
    @Basic
    @Column(name = "USERNAME", nullable = false, length = 20)
    private String username;
    @Basic
    @Column(name = "PASSWORD", nullable = false, length = 20)
    private String password;

    public ClientHandler(String username, String password){
        this.username = username;
        this.password = password;
    }

    public ClientHandler() {
        this(null,null);
    }

    public long getClientId(){return clientId;}

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {return username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }
}
