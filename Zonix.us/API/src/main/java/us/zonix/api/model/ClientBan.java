package us.zonix.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "client_ban")
public class ClientBan {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @NotNull private String hwid;
    @NotNull private String ip;
    @NotNull private String reason;
    private Timestamp timestamp;
    private boolean active = false;

}
