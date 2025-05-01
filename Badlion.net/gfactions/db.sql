CREATE TABLE XXX_faction_lotto_tickets (
    uuid character varying(64) NOT NULL,
    lotto_ticket_number integer NOT NULL,
    purchase_time timestamp without time zone
);

ALTER TABLE ONLY XXX_faction_lotto_tickets
    ADD CONSTRAINT "NoneXXX_faction_lotto_tickets_uuid_lotto_ticket_number_pkey" PRIMARY KEY (uuid, lotto_ticket_number);
CREATE UNIQUE INDEX "NoneXXX_faction_lotto_tickets_lotto_ticket_number" ON XXX_faction_lotto_tickets USING btree (lotto_ticket_number);
CREATE INDEX "NoneXXX_faction_lotto_tickets_uuid" ON XXX_faction_lotto_tickets USING btree (uuid);

CREATE TABLE XXX_pvp_protection (
    uuid character varying(64) NOT NULL,
    num_of_milliseconds_remaining integer NOT NULL
);

ALTER TABLE ONLY XXX_pvp_protection
    ADD CONSTRAINT "NoneXXX_pvp_protection_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_admin_command_log (
    used_on character varying(64) NOT NULL,
    used_by character varying(64) NOT NULL,
    command character varying(16) NOT NULL,
    time_used timestamp without time zone NOT NULL
);

ALTER TABLE ONLY XXX_faction_admin_command_log
    ADD CONSTRAINT "NoneXXX_faction_admin_command_log_used_on_time_used_pkey" PRIMARY KEY (used_on, time_used);
CREATE INDEX "NoneXXX_faction_admin_command_log_command" ON XXX_faction_admin_command_log USING btree (command);
CREATE INDEX "NoneXXX_faction_admin_command_log_time_used" ON XXX_faction_admin_command_log USING btree (time_used);
CREATE INDEX "NoneXXX_faction_admin_command_log_used_by" ON XXX_faction_admin_command_log USING btree (used_by);
CREATE INDEX "NoneXXX_faction_admin_command_log_used_on" ON XXX_faction_admin_command_log USING btree (used_on);

CREATE TABLE XXX_faction_property_level (
    type character varying(16) NOT NULL,
    lvl integer NOT NULL,
    current_uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY XXX_faction_property_level
    ADD CONSTRAINT "NoneXXX_faction_property_level_type_pkey" PRIMARY KEY (type);

CREATE TABLE XXX_faction_property_deeds (
    type character varying(16) NOT NULL,
    uuid character varying(64) NOT NULL,
    owner character varying(16) NOT NULL,
    time_acquired timestamp without time zone,
    last_used timestamp without time zone
);

ALTER TABLE ONLY XXX_faction_property_deeds
    ADD CONSTRAINT "NoneXXX_faction_property_deeds_uuid_pkey" PRIMARY KEY (uuid);
CREATE INDEX "NoneXXX_faction_property_deeds_owner" ON XXX_faction_property_deeds USING btree (owner);
CREATE INDEX "NoneXXX_faction_property_deeds_time_acquired" ON XXX_faction_property_deeds USING btree (time_acquired);
CREATE INDEX "NoneXXX_faction_property_deeds_type" ON XXX_faction_property_deeds USING btree (type);

CREATE TABLE XXX_faction_kill_noob_streak (
    uuid character varying(64) NOT NULL,
    kill_streak integer NOT NULL,
    noob_streak integer NOT NULL
);

ALTER TABLE ONLY XXX_faction_kill_noob_streak
    ADD CONSTRAINT "NoneXXX_faction_kill_noob_streak_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_mcmmo_levels_available (
    uuid character varying(64) NOT NULL,
    levels_available integer NOT NULL
);

ALTER TABLE ONLY XXX_faction_mcmmo_levels_available
    ADD CONSTRAINT "NoneXXX_faction_mcmmo_levels_available_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_xp_boost (
    uuid character varying(64) NOT NULL,
    multiplier double precision NOT NULL,
    expiration_date timestamp without time zone
);

ALTER TABLE ONLY XXX_faction_xp_boost
    ADD CONSTRAINT "NoneXXX_faction_xp_boost_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_kit_load_times (
    uuid character varying(64) NOT NULL,
    kit character varying(16) NOT NULL,
    last_load_time bigint NOT NULL
);

ALTER TABLE ONLY XXX_faction_kit_load_times
    ADD CONSTRAINT "NoneXXX_faction_kit_load_times_uuid_kit_pkey" PRIMARY KEY (uuid, kit);

CREATE TABLE XXX_faction_tickets (
    uuid character varying(64) NOT NULL,
    num_of_tickets integer NOT NULL
);

ALTER TABLE ONLY XXX_faction_tickets
    ADD CONSTRAINT "NoneXXX_faction_tickets_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE factions_bans (
    uuid character varying(64) NOT NULL,
    reason character varying(255) NOT NULL,
    ban_time timestamp without time zone,
    banned_by character varying(16) NOT NULL,
    still_banned smallint NOT NULL
);

ALTER TABLE ONLY factions_bans
    ADD CONSTRAINT "Nonefactions_bans_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_reserved_slot (
    uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY XXX_faction_reserved_slot
    ADD CONSTRAINT "NoneXXX_faction_reserved_slot_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_homes (
    uuid character varying(64) NOT NULL,
    home character varying(32) NOT NULL,
    x double precision NOT NULL,
    y double precision NOT NULL,
    z double precision NOT NULL,
    world character varying(32) NOT NULL
);

ALTER TABLE ONLY XXX_faction_homes
    ADD CONSTRAINT "NoneXXX_faction_homes_uuid_home_pkey" PRIMARY KEY (uuid, home);
CREATE INDEX "NoneXXX_faction_homes_uuid" ON XXX_faction_homes USING btree (uuid);

CREATE TABLE XXX_faction_vote_records (
    uuid character varying(64) NOT NULL,
    vote_date timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_vote_records_uuid" ON XXX_faction_vote_records USING btree (uuid);
CREATE INDEX "NoneXXX_faction_vote_records_vote_date" ON XXX_faction_vote_records USING btree (vote_date);

CREATE SEQUENCE XXX_faction_faction_faction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE XXX_faction_faction (
    faction_id integer DEFAULT nextval('XXX_faction_faction_faction_id_seq'::regclass) NOT NULL,
    faction_tag character varying(64) NOT NULL,
    faction_description character varying(128) NOT NULL,
    money double precision NOT NULL,
    leader_uuid character varying(64) NOT NULL,
    create_time timestamp without time zone,
    disband_time timestamp without time zone,
    kills integer NOT NULL,
    deaths integer NOT NULL,
    kdr double precision NOT NULL,
    towers integer NOT NULL,
    koths integer NOT NULL,
    manhunts integer NOT NULL,
    bloodbowls integer NOT NULL,
    dragons integer NOT NULL
);

ALTER TABLE ONLY XXX_faction_faction
    ADD CONSTRAINT "NoneXXX_faction_faction_faction_id_pkey" PRIMARY KEY (faction_id);
CREATE INDEX "NoneXXX_faction_faction_kills" ON XXX_faction_faction USING btree(kills);
CREATE INDEX "NoneXXX_faction_faction_deaths" ON XXX_faction_faction USING btree(deaths);
CREATE INDEX "NoneXXX_faction_faction_kdr" ON XXX_faction_faction USING btree(kdr);
CREATE INDEX "NoneXXX_faction_faction_towers" ON XXX_faction_faction USING btree(towers);
CREATE INDEX "NoneXXX_faction_faction_koths" ON XXX_faction_faction USING btree(koths);
CREATE INDEX "NoneXXX_faction_faction_manhunts" ON XXX_faction_faction USING btree(manhunts);
CREATE INDEX "NoneXXX_faction_faction_bloodbowls" ON XXX_faction_faction USING btree(bloodbowls);
CREATE INDEX "NoneXXX_faction_faction_dragons" ON XXX_faction_faction USING btree(dragons);

ALTER TABLE ONLY XXX_faction_faction
    ADD CONSTRAINT "NoneXXX_faction_faction_faction_id_pkey" PRIMARY KEY (faction_id);
CREATE INDEX "NoneXXX_faction_faction_faction_tag" ON XXX_faction_faction USING btree (faction_tag);

CREATE TABLE XXX_faction_faction_player (
    uuid character varying(64) NOT NULL,
    faction_id integer NOT NULL,
    role character varying(24) NOT NULL
);

ALTER TABLE ONLY XXX_faction_faction_player
    ADD CONSTRAINT "NoneXXX_faction_faction_player_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_faction_faction_relation_history (
    faction_from integer NOT NULL,
    faction_to integer NOT NULL,
    relationship character varying(24) NOT NULL,
    relation_change_time timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_faction_relation_history_faction_from" ON XXX_faction_faction_relation_history USING btree (faction_from);
CREATE INDEX "NoneXXX_faction_faction_relation_history_faction_to" ON XXX_faction_faction_relation_history USING btree (faction_to);

CREATE TABLE XXX_faction_faction_relation (
    faction_from integer NOT NULL,
    faction_to integer NOT NULL,
    relationship character varying(24) NOT NULL,
    relation_change_time timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_faction_relation_faction_from" ON XXX_faction_faction_relation USING btree (faction_from);
CREATE INDEX "NoneXXX_faction_faction_relation_faction_to" ON XXX_faction_faction_relation USING btree (faction_to);

CREATE TABLE XXX_faction_faction_member_history (
    faction_id integer NOT NULL,
    type character varying(24) NOT NULL,
    uuid character varying(64) NOT NULL,
    history_time timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_faction_member_history_faction_id" ON XXX_faction_faction_member_history USING btree (faction_id);
CREATE INDEX "NoneXXX_faction_faction_member_history_history_time" ON XXX_faction_faction_member_history USING btree (history_time);
CREATE INDEX "NoneXXX_faction_faction_member_history_type" ON XXX_faction_faction_member_history USING btree (type);
CREATE INDEX "NoneXXX_faction_faction_member_history_uuid" ON XXX_faction_faction_member_history USING btree (uuid);

CREATE TABLE XXX_faction_mining_records (
    uuid character varying(64) NOT NULL,
    material character varying(32) NOT NULL,
    mined_time timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_mining_records_uuid" ON XXX_faction_mining_records USING btree (uuid);
CREATE INDEX "NoneXXX_faction_mining_records_mined_time" ON XXX_faction_mining_records USING btree (mined_time);

CREATE TABLE XXX_faction_xray_records (
    uuid character varying(64) NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL
);

CREATE INDEX "NoneXXX_faction_xray_records_uuid" ON XXX_faction_xray_records USING btree (uuid);

CREATE TABLE XXX_faction_bonuses (
    uuid_donator character varying(64) NOT NULL,
    type character varying(32) NOT NULL,
    data character varying(32) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    end_date timestamp without time zone NOT NULL
);

CREATE TABLE XXX_faction_wild_command_records (
    uuid character varying(64) NOT NULL,
    "time" timestamp without time zone
);

CREATE INDEX "NoneXXX_faction_wild_command_records" ON XXX_faction_wild_command_records USING btree (uuid);

CREATE TABLE XXX_player_kills_deaths (
    uuid character varying(64) NOT NULL,
    kills integer NOT NULL,
    deaths integer NOT NULL,
    kdr double precision NOT NULL
);

ALTER TABLE ONLY XXX_player_kills_deaths
    ADD CONSTRAINT "NoneXXX_player_kills_deaths_uuid_pkey" PRIMARY KEY (uuid);
CREATE INDEX "NoneXXX_player_kills_deaths_kills" ON XXX_player_kills_deaths USING btree(kills);
CREATE INDEX "NoneXXX_player_kills_deaths_deaths" ON XXX_player_kills_deaths USING btree(deaths);
CREATE INDEX "NoneXXX_player_kills_deaths_kdr" ON XXX_player_kills_deaths USING btree(kdr);

CREATE SEQUENCE XXX_stronghold_events_stronghold_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
CACHE 1;

CREATE TABLE XXX_stronghold_events (
  stronghold_id integer DEFAULT nextval('XXX_stronghold_events_stronghold_id_seq'::regclass) NOT NULL,
  start_time timestamp without time zone NOT NULL,
  end_time timestamp without time zone NOT NULL,
  beginning_owners character varying(128) NOT NULL,
  ending_owners character varying(128) NOT NULL
);

CREATE INDEX "NoneXXX_stronghold_events_stronghold_id" ON XXX_stronghold_events USING btree (stronghold_id);

CREATE TABLE XXX_stronghold_participants (
  stronghold_id integer NOT NULL,
  participant character varying(16) NOT NULL
);

CREATE INDEX "NoneXXX_stronghold_participants_stronghold_id" ON XXX_stronghold_participants USING btree(stronghold_id);

CREATE TABLE XXX_stronghold_deterioration (
  stronghold_id integer NOT NULL,
  keep_name character varying(16) NOT NULL,
  money_owed integer NOT NULL,
  items_owed character varying(512) NOT NULL
);

CREATE INDEX "NoneXXX_stronghold_deterioration_stronghold_id" ON XXX_stronghold_deterioration USING btree(stronghold_id);

CREATE TABLE XXX_stronghold_blacklists (
  faction_id integer NOT NULL
);

CREATE INDEX "NoneXXX_stronghold_blacklists_faction_id" ON XXX_stronghold_blacklists USING btree(faction_id);

CREATE TABLE XXX_death_bans (
  uuid character varying(36) NOT NULL,
  unban_time timestamp with time zone NOT NULL
);

CREATE INDEX "NoneXXX_death_bans_unban_time" ON XXX_death_bans USING btree(unban_time);

CREATE TABLE XXX_num_of_lives (
  uuid character varying(36) NOT NULL,
  num_of_lives integer NOT NULL
);

CREATE INDEX "NoneXXX_num_of_lives_uuid" ON XXX_num_of_lives USING btree(uuid);

CREATE TABLE XXX_heart_shards (
  uuid character varying(36) NOT NULL,
  num_of_shards integer NOT NULL
);

CREATE INDEX "NoneXXX_heart_shards_uuid" ON XXX_heart_shards USING btree(uuid);

CREATE TABLE XXX_num_of_death_bans (
  uuid character varying(36) NOT NULL,
  num_of_bans integer NOT NULL
);

CREATE INDEX "NoneXXX_num_of_death_bans_uuid" ON XXX_num_of_death_bans USING btree(uuid);

CREATE SEQUENCE faction_events_faction_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE faction_events (
    faction_event_id integer DEFAULT nextval('faction_events_faction_event_id_seq'::regclass) NOT NULL,
    faction_time timestamp without time zone NOT NULL,
    faction_uid integer NOT NULL,
    faction_event_name text NOT NULL,
    region character varying(10) NOT NULL
);

ALTER TABLE ONLY faction_events ADD CONSTRAINT "Nonefaction_events_faction_event_id_pkey" PRIMARY KEY (faction_event_id);

