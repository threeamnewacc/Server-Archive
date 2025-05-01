CREATE TABLE XXX_ministats (
    uuid character varying(36) NOT NULL,
    kills integer NOT NULL,
    deaths integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL,
    time_played bigint NOT NULL,
    damage_dealt double precision NOT NULL,
    damage_taken double precision NOT NULL,
    highest_kill_streak integer NOT NULL,
    sword_hits integer NOT NULL,
    sword_swings integer NOT NULL,
    sword_blocks integer NOT NULL,
    bow_punches integer NOT NULL,
    arrows_shot integer NOT NULL,
    arrows_hit integer NOT NULL
);

ALTER TABLE ONLY XXX_ministats ADD CONSTRAINT "NoneXXX_ministats_uuid_pkey" PRIMARY KEY (uuid);
CREATE INDEX "NoneXXX_ministats_kills" ON XXX_ministats USING btree (kills);
CREATE INDEX "NoneXXX_ministats_deaths" ON XXX_ministats USING btree (deaths);
CREATE INDEX "NoneXXX_ministats_wins" ON XXX_ministats USING btree (wins);
CREATE INDEX "NoneXXX_ministats_losses" ON XXX_ministats USING btree (losses);
CREATE INDEX "NoneXXX_ministats_time_played" ON XXX_ministats USING btree (time_played);
CREATE INDEX "NoneXXX_ministats_damage_dealt" ON XXX_ministats USING btree (damage_dealt);
CREATE INDEX "NoneXXX_ministats_damage_taken" ON XXX_ministats USING btree (damage_taken);
CREATE INDEX "NoneXXX_ministats_highest_kill_streak" ON XXX_ministats USING btree (highest_kill_streak);
CREATE INDEX "NoneXXX_ministats_sword_hits" ON XXX_ministats USING btree (sword_hits);
CREATE INDEX "NoneXXX_ministats_sword_swings" ON XXX_ministats USING btree (sword_swings);
CREATE INDEX "NoneXXX_ministats_sword_blocks" ON XXX_ministats USING btree (sword_blocks);
CREATE INDEX "NoneXXX_ministats_bow_punches" ON XXX_ministats USING btree (bow_punches);
CREATE INDEX "NoneXXX_ministats_arrows_shot" ON XXX_ministats USING btree (arrows_shot);
CREATE INDEX "NoneXXX_ministats_arrows_hit" ON XXX_ministats USING btree (arrows_hit);

ALTER TABLE XXX_ministats ADD COLUMN sword_accuracy double precision NOT NULL DEFAULT '0';
ALTER TABLE XXX_ministats ADD COLUMN arrow_accuracy double precision NOT NULL DEFAULT '0';
ALTER TABLE XXX_ministats ADD COLUMN kdr double precision NOT NULL DEFAULT '0';
CREATE INDEX "NoneXXX_ministats_sword_accuracy" ON XXX_ministats USING btree (sword_accuracy);
CREATE INDEX "NoneXXX_ministats_arrow_accuracy" ON XXX_ministats USING btree (arrow_accuracy);
CREATE INDEX "NoneXXX_ministats_kdr" ON XXX_ministats USING btree (kdr);

CREATE TABLE XXX_ministats_maps (
    uuid character varying(36) NOT NULL,
    map_name character varying(36) NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

CREATE INDEX "NoneXXX_ministats_maps_uuid" ON XXX_ministats_maps USING btree (uuid);
CREATE INDEX "NoneXXX_ministats_maps_map_name" ON XXX_ministats_maps USING btree (map_name);
CREATE INDEX "NoneXXX_ministats_maps_wins" ON XXX_ministats_maps USING btree (wins);
CREATE INDEX "NoneXXX_ministats_maps_losses" ON XXX_ministats_maps USING btree (losses);