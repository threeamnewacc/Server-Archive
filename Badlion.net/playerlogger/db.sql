CREATE TABLE IF NOT EXISTS death_logger (
	uuid VARCHAR (64) NOT NULL,
	server VARCHAR (10) NOT NULL,
	killer VARCHAR (64) NOT NULL,
	killer_weapon VARCHAR (32) NOT NULL,
	death_reason TEXT NOT NULL,
	death_x INT (11) NOT NULL,
	death_y INT (11) NOT NULL,
	death_z INT (11) NOT NULL,
	death_time DATETIME NOT NULL,
	user_pot_effects TEXT NOT NULL,
	killer_pot_effects TEXT NOT NULL,
	PRIMARY KEY (uuid, server, death_time),
	KEY killer (killer),
	KEY server (server),
	KEY death_time (death_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE player_ips (
    username character varying(16) NOT NULL,
    uuid character varying(64) NOT NULL,
    server character varying(10) NOT NULL,
    long_ip integer NOT NULL,
    num_of_logins bigint NOT NULL,
    first_login timestamp without time zone,
    last_login timestamp without time zone
);

ALTER TABLE ONLY player_ips
    ADD CONSTRAINT "Noneplayer_ips_username_uuid_server_long_ip_pkey" PRIMARY KEY (username, uuid, server, long_ip);
CREATE INDEX "Noneplayer_ips_last_login" ON player_ips USING btree (last_login);
CREATE INDEX "Noneplayer_ips_long_ip" ON player_ips USING btree (long_ip);
CREATE INDEX "Noneplayer_ips_server" ON player_ips USING btree (server);
CREATE INDEX "Noneplayer_ips_username" ON player_ips USING btree (username);
CREATE INDEX "Noneplayer_ips_uuid" ON player_ips USING btree (uuid);

