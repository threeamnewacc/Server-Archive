package me.devkevin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 20:29
 */
@Data @NoArgsConstructor @AllArgsConstructor @Entity(name = "players")
public class Player implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int playerId;

    @Column
    private String ipAddress = null;
    @Column
    private String name = null;

    @Column(name = "uuid")
    private String uniqueId;

    @Column(name = "mute_time")
    private Timestamp muteTime = null;

    @Column(name = "ban_time")
    private Timestamp banTime = null;

    @Column(name = "first_login")
    private Timestamp firstLogin = null;

    @Column(name = "last_login")
    private String lastLogin = null;

    @Column(name = "rank")
    private String rank = null;

    @Column(name = "blacklisted")
    private boolean blacklisted = false;

    @Column(name = "ip_banned")
    private boolean ipBanned = false;

    @Column(name = "banned")
    private boolean banned = false;

    @Column(name = "muted")
    private boolean muted = false;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;
}
