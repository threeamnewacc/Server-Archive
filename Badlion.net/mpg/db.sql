CREATE TABLE mpg_kits (
    uuid character varying(36) NOT NULL,
    gamemode character varying(10) NOT NULL,
    type character varying(10) NOT NULL,
    preview_item integer NOT NULL,
    items bytea NOT NULL,
    armor bytea NOT NULL,
    kit_number integer NOT NULL,
    name character varying(25) NOT NULL
);

ALTER TABLE ONLY mpg_kits ADD CONSTRAINT "Nonempg_kits_pkey" PRIMARY KEY (uuid, gamemode, kit_number);
CREATE INDEX "Nonempg_kits_uuid" ON mpg_kits USING btree (uuid);

CREATE TABLE clan_duel_stats (
  clan_id integer NOT NULL,
  game_type character varying(16) NOT NULL,
  wins integer DEFAULT 0,
  losses integer DEFAULT 0
);

CREATE INDEX "Noneclan_duel_stats_clan_id" ON clan_duel_stats USING btree(clan_id);
CREATE INDEX "Noneclan_duel_stats_game_type" ON clan_duel_stats USING btree(game_type);

CREATE TABLE clan_duel_records (
  time timestamp with time zone NOT NULL,
  clan_id integer NOT NULL,
  other_clan_id integer NOT NULL,
  game_type character varying(16) NOT NULL,
  match_id character varying(36) NOT NULL,
  win BOOLEAN NOT NULL
);

CREATE INDEX "Noneclan_duel_records_clan_id" ON clan_duel_records USING btree(clan_id);
CREATE INDEX "Noneclan_duel_records_other_clan_id" ON clan_duel_records USING btree(other_clan_id);
CREATE INDEX "Noneclan_duel_records_game_type" ON clan_duel_records USING btree(game_type);