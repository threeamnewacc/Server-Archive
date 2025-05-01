CREATE TABLE ban_list (
 banned_uuid VARCHAR(64) NOT NULL,
 banner_uuid VARCHAR(64) NOT NULL,
 banned_time DATETIME NOT NULL,
 unban_time DATETIME NOT NULL,
 server VARCHAR(16) NOT NULL,
 reason VARCHAR(255) NOT NULL,
 UNIQUE INDEX banned_player (banned_uuid)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

CREATE TABLE ban_records (
 banned_uuid VARCHAR(64) NOT NULL,
 banner_uuid VARCHAR(64) NOT NULL,
 banned_time DATETIME NOT NULL,
 unban_time DATETIME NOT NULL,
 server VARCHAR(16) NOT NULL,
 reason VARCHAR(255) NOT NULL,
 unbanner_uuid VARCHAR(64) NOT NULL,
 unbanned_time DATETIME NOT NULL
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

CREATE TABLE kick_list (
 kicked_uuid VARCHAR(64) NOT NULL,
 kicker_uuid VARCHAR(64) NOT NULL,
 reason VARCHAR(255) NOT NULL,
 time DATETIME NOT NULL,
 server VARCHAR(15) NULL DEFAULT NULL
)
COLLATE='latin1_swedish_ci'
ENGINE=MyISAM;

CREATE TABLE mute_list (
 muted_uuid VARCHAR(64) NOT NULL,
 muter_uuid VARCHAR(64) NOT NULL,
 muted_time DATETIME NOT NULL,
 unmute_time DATETIME NOT NULL,
 server VARCHAR(16) NOT NULL,
 reason VARCHAR(255) NOT NULL,
 UNIQUE INDEX muted_uuid (muted_uuid)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

CREATE TABLE mute_records (
 muted_uuid VARCHAR(64) NOT NULL,
 muter_uuid VARCHAR(64) NOT NULL,
 muted_time DATETIME NOT NULL,
 unmute_time DATETIME NOT NULL,
 server VARCHAR(16) NOT NULL,
 reason VARCHAR(255) NOT NULL,
 unmuter_uuid VARCHAR(64) NOT NULL,
 unmuted_time DATETIME NOT NULL
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

CREATE SEQUENCE punishments_punishment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE punishments (
    punishment_id integer DEFAULT nextval('punishments_punishment_id_seq'::regclass) NOT NULL,
    punished_uuid character varying(36) NOT NULL,
    punisher_uuid character varying(36) NOT NULL,
    punishment_time timestamp with time zone NOT NULL,
    unpunish_time timestamp with time zone NOT NULL,
    unpunisher_uuid character varying(36) NULL,
    server character varying(20) NOT NULL,
    reason character varying(255) NOT NULL,
    type integer NOT NULL,
    ip bigint NOT NULL default 0,
    false_punishment boolean default FALSE,
    un_appealable boolean default FALSE
);

ALTER TABLE ONLY punishments
    ADD CONSTRAINT "Nonepunishments_punishment_id" PRIMARY KEY (punishment_id);
CREATE INDEX "Nonepunishments_punished_uuid" ON punishments USING btree (punished_uuid);
CREATE INDEX "Nonepunishments_punisher_uuid" ON punishments USING btree (punisher_uuid);
CREATE INDEX "Nonepunishments_unpunisher_uuid" ON punishments USING btree (unpunisher_uuid);
CREATE INDEX "Nonepunishments_punishment_time" ON punishments USING btree (punishment_time);
CREATE INDEX "Nonepunishments_type" ON punishments USING btree (type);
CREATE INDEX "Nonepunishments_ip" ON punishments USING btree (ip);
CREATE INDEX "Nonepunishments_unpunish_time" ON punishments USING btree (unpunish_time);
