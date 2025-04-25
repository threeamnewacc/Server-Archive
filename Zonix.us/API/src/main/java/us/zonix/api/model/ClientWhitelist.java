package us.zonix.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "client_whitelist")
public class ClientWhitelist {

    @Id
    @Column(name = "uuid")
    private String uuid;

    private String ip;

}
