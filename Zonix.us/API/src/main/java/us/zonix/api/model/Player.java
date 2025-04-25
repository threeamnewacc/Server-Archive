package us.zonix.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "players")
public class Player implements Serializable {

    @Id
    private String uuid;

    @Column(name = "name")
    private String name = null;

    @Column(name = "rank")
    private String rank = null;

    @Column(name = "symbol")
    private String symbol = null;

    @Column(name = "bought_symbols")
    private Boolean boughtSymbols = false;

    @Column(name = "ip")
    private String ip = null;

    @Column(name = "first_login")
    private Timestamp firstLogin = null;

    @Column(name = "last_login")
    private Timestamp lastLogin = null;

    @Column(name = "last_server")
    private String lastServer = null;

    @Column(name = "email")
    private String emailAddress = null;

    @Column(name = "confirmation_id")
    private String confirmationId = null;

    @Column(name = "registered")
    private Boolean registered = false;

    @Column(name = "two_factor_authentication")
    private String twoFactorAuthentication = null;

    @Column(name = "authenticated")
    private Boolean authenticated = true;

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", this.uuid);
        object.addProperty("name", this.name);
        object.addProperty("rank", this.rank);
        object.addProperty("symbol", this.symbol);
        object.addProperty("bought_symbols", this.boughtSymbols);
        object.addProperty("ip", this.ip);
        object.addProperty("first_login", this.firstLogin.getTime());
        object.addProperty("last_login", this.lastLogin.getTime());
        object.addProperty("last_server", this.lastServer);
        object.addProperty("email_address", this.emailAddress);
        object.addProperty("confirmation_id", this.confirmationId);
        object.addProperty("registered", this.registered);
        object.addProperty("authenticated", this.authenticated);
        object.addProperty("two_factor_authentication", this.twoFactorAuthentication);
        return object;
    }

}
