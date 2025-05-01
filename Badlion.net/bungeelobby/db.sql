CREATE SEQUENCE clans_clan_id_seq
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE clans (
    clan_id integer DEFAULT nextval('clans_clan_id_seq'::regclass) NOT NULL,
    tag character varying(7) NOT NULL,
    lower_tag character varying(7) NOT NULL,
    name character varying(32) NOT NULL,
    lower_name character varying(32) NOT NULL,
    leader character varying(36) NOT NULL,
    description character varying(255) NOT NULL,
    creation_date timestamp with time zone NOT NULL,
    lower_name character varying(32) NOT NULL,
    lower_tag character varying(7) NOT NULL
);

ALTER TABLE ONLY clans ADD CONSTRAINT "Noneclans_id" PRIMARY KEY (clan_id);
ALTER TABLE ONLY clans ADD CONSTRAINT "Noneclans_uq_tag" UNIQUE (tag);
ALTER TABLE ONLY clans ADD CONSTRAINT "Noneclans_uq_name" UNIQUE (name);
ALTER TABLE ONLY clans ADD CONSTRAINT "Noneclans_uq_lower_tag" UNIQUE (lower_tag);
ALTER TABLE ONLY clans ADD CONSTRAINT "Noneclans_uq_lower_name" UNIQUE (lower_name);

CREATE TABLE clan_members (
    clan_id integer NOT NULL,
    member character varying(36) NOT NULL,
    rank integer NOT NULL,
    join_date timestamp with time zone NOT NULL
);

ALTER TABLE ONLY clan_members ADD CONSTRAINT "Noneclan_members_pk" PRIMARY KEY (clan_id, member);
CREATE INDEX "Noneclans_members_clan_id" ON clan_members USING btree (clan_id);
CREATE INDEX "Noneclans_members_member" ON clan_members USING btree (member);

CREATE TABLE clan_history (
    clan_id integer NOT NULL,
    member_taking_action character varying(36) NOT NULL,
    member_receiving_action character varying(36) NOT NULL,
    clan_action character varying(10) NOT NULL,
    action_date timestamp with time zone NOT NULL
);

ALTER TABLE ONLY clan_history ADD CONSTRAINT "Noneclan_history_pk" PRIMARY KEY (clan_id, member_taking_action, member_receiving_action, clan_action, action_date);
CREATE INDEX "Noneclan_history_clan_id" ON clan_history USING btree (clan_id);
CREATE INDEX "Noneclan_history_member_taking_action" ON clan_history USING btree (member_taking_action);
CREATE INDEX "Noneclan_history_member_receiving_action" ON clan_history USING btree (member_receiving_action);

CREATE TABLE clan_invites (
    clan_id integer NOT NULL,
    invitee_uuid character varying(36) NOT NULL,
    inviter_uuid character varying(36) NOT NULL,
    invite_date timestamp with time zone NOT NULL
);

CREATE INDEX "Noneclan_invites_clan_id" ON clan_invites USING btree (clan_id);
CREATE INDEX "Noneclan_invites_invitee_uuid" ON clan_invites USING btree (invitee_uuid);
CREATE INDEX "Noneclan_invites_inviter_uuid" ON clan_invites USING btree (inviter_uuid);

CREATE TABLE clan_banned_tags (
    tag character varying(7) NOT NULL
);

ALTER TABLE ONLY clan_banned_tags ADD CONSTRAINT "Noneclan_banned_tags_tag" PRIMARY KEY (tag);

CREATE TABLE clan_ratings (
    gamemode character varying(10) NOT NULL,
    lid integer NOT NULL,
    cid integer NOT NULL,
    rating integer NOT NULL
);

ALTER TABLE ONLY clan_ratings ADD CONSTRAINT "Noneclan_ratings_pk" PRIMARY KEY (gamemode, lid, cid);
CREATE INDEX "Noneclan_ratings_gamemode" ON clan_ratings USING btree (gamemode);
CREATE INDEX "Noneclan_ratings_lid" ON clan_ratings USING btree (lid);
CREATE INDEX "Noneclan_ratings_cid" ON clan_ratings USING btree (cid);

CREATE TABLE maxmind_ips (
    ip bigint not null default 0,
    country_iso character varying(10) NULL,
    country_confidence integer NULL,
    country_geo_name_id integer NULL,
    state_iso character varying(255) NULL,
    state_confidence integer NULL,
    state_geo_name_id integer NULL,
    city_name character varying(255) NULL,
    city_confidence integer NULL,
    city_geo_name_id integer NULL,
    postal_code character varying(55) NULL,
    postal_confidence integer NULL,
    location_latitude float NULL,
    location_longitude float NULL,
    location_radius integer NULL,
    location_timezone character varying(255) NULL,
    asn integer NULL,
    aso character varying(255) NULL,
    gdomain character varying(255) NULL,
    isp character varying(255) NULL,
    organization character varying(255) NULL,
    user_type character varying(55) NULL,
    is_anon_proxy boolean NULL,
    is_satellite_provider boolean NULL,
    fetch_date timestamp with time zone NULL
);

ALTER TABLE ONLY maxmind_ips ADD CONSTRAINT "Nonemaxmind_ips_ip" PRIMARY KEY (ip);

CREATE SEQUENCE banned_ips_ip_ban_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE banned_ips (
    ip_ban_id integer DEFAULT nextval('banned_ips_ip_ban_id_seq'::regclass) NOT NULL,
    ip bigint not null default 0,
    reason character varying(255) NULL,
    ban_time timestamp with time zone NULL,
    unban_time timestamp with time zone NULL,
    unpunish_uuid character varying(36) NOT NULL
);

ALTER TABLE ONLY banned_ips ADD CONSTRAINT "Nonebanned_ips_ip_ban_id" PRIMARY KEY (ip_ban_id);
CREATE INDEX "Nonebanned_ips_ip" ON banned_ips USING btree (ip);

CREATE TABLE banned_isps (
    isp character varying(255) NULL,
    asn integer NULL,
    aso character varying(255) NULL
);

CREATE INDEX "Nonebanned_isps_isp" ON banned_isps USING btree (isp);
CREATE INDEX "Nonebanned_isps_asn" ON banned_isps USING btree (asn);
CREATE INDEX "Nonebanned_isps_aso" ON banned_isps USING btree (aso);

CREATE TABLE ts_accounts (
    uuid character varying (36) NOT NULL,
    ts_uuid character varying (36) NOT NULL,
    time_set timestamp with time zone NOT NULL
);

ALTER TABLE ONLY ts_accounts ADD CONSTRAINT "Nonets_accounts_uuid" PRIMARY KEY (uuid);
CREATE INDEX "Nonets_accounts_ts_uuid" ON ts_accounts USING btree (ts_uuid);

CREATE TABLE ts_account_history (
    uuid character varying (36) NOT NULL,
    ts_uuid character varying (36) NOT NULL,
    time_set timestamp with time zone NOT NULL
);

CREATE INDEX "Nonets_account_history_uuid" ON ts_account_history USING btree (uuid);
CREATE INDEX "Nonets_account_history_ts_uuid" ON ts_account_history USING btree (ts_uuid);
CREATE INDEX "Nonets_account_history_time_set" ON ts_account_history USING btree (time_set);

CREATE TABLE player_uuid_mapping (
  	lower_username character varying(16) NOT NULL,
  	username character varying(16) NOT NULL,
  	uuid character varying(36) NOT NULL
);

ALTER TABLE ONLY player_uuid_mapping ADD CONSTRAINT "Noneplayer_uuid_mapping_pkey" PRIMARY KEY (uuid);
CREATE INDEX "Noneplayer_uuid_mapping_lower_username" ON player_uuid_mapping USING btree (lower_username);
CREATE INDEX "Noneplayer_uuid_mapping_username" ON player_uuid_mapping USING btree (username);

CREATE TABLE discord_accounts (
    uuid character varying (36) NOT NULL,
    discord_uuid character varying (36) NOT NULL,
    time_set timestamp with time zone NOT NULL
);

ALTER TABLE ONLY discord_accounts ADD CONSTRAINT "Nonediscord_accounts_uuid" PRIMARY KEY (uuid);
CREATE INDEX "Nonediscord_accounts_discord_uuid" ON discord_accounts USING btree (discord_uuid);

CREATE TABLE discord_account_history (
    uuid character varying (36) NOT NULL,
    discord_uuid character varying (36) NOT NULL,
    time_set timestamp with time zone NOT NULL
);

CREATE INDEX "Nonediscord_account_history_uuid" ON discord_account_history USING btree (uuid);
CREATE INDEX "Nonediscord_account_history_discord_uuid" ON discord_account_history USING btree (discord_uuid);
CREATE INDEX "Nonediscord_account_history_time_set" ON discord_account_history USING btree (time_set);

CREATE TABLE discord_chat_history (
    message_id bigint NOT NULL,
    message TEXT NOT NULL,
    sender bigint NOT NULL,
    channel bigint NOT NULL,
    ts timestamp with time zone NOT NULL,
    deleted boolean NOT NULL DEFAULT false
);

CREATE TABLE discord_chat_history_edits (
    message_id bigint NOT NULL,
    orig_message TEXT NULL DEFAULT NULL,
    orig_ts timestamp with time zone NULL DEFAULT NULL
);

