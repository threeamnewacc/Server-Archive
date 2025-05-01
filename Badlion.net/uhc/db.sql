CREATE SEQUENCE uhc_match_times_uhc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE uhc_match_times (
    uhc_id integer DEFAULT nextval('uhc_match_times_uhc_id_seq'::regclass) NOT NULL,
    uhc_time timestamp without time zone NOT NULL,
    uhc_uid integer NOT NULL,
    uhc_hosts text NOT NULL,
    uhc_guests text NOT NULL,
    uhc_max_players integer NOT NULL,
    uhc_game_mode text NOT NULL,
    region character varying(1) NOT NULL
);

ALTER TABLE ONLY uhc_match_times
    ADD CONSTRAINT "Noneuhc_match_times_uhc_id_pkey" PRIMARY KEY (uhc_id);

CREATE SEQUENCE uhc_stat_resets_reset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE uhc_stat_resets (
    reset_id integer DEFAULT nextval('uhc_stat_resets_reset_id_seq'::regclass) NOT NULL,
    uuid character varying(36) NOT NULL
);

ALTER TABLE ONLY uhc_stat_resets
    ADD CONSTRAINT "Noneuhc_stat_resets_reset_id_pkey" PRIMARY KEY (reset_id);


ALTER TABLE uhc_ministats ADD COLUMN levels integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN hearts_healed integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN horses_tamed integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN fall_damage double precision NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN absorption_hearts integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN golden_heads integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN golden_apples integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN nether_portals integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN end_portals integer NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN blocks_broken json NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN animal_mobs json NOT NULL;
ALTER TABLE uhc_ministats ADD COLUMN potions json NOT NULL;

CREATE INDEX "Noneuhc_ministats_levels" ON uhc_ministats USING btree (levels);
CREATE INDEX "Noneuhc_ministats_hearts_healed" ON uhc_ministats USING btree (hearts_healed);
CREATE INDEX "Noneuhc_ministats_horses_tamed" ON uhc_ministats USING btree (horses_tamed);
CREATE INDEX "Noneuhc_ministats_fall_damage" ON uhc_ministats USING btree (fall_damage);
CREATE INDEX "Noneuhc_ministats_absorption_hearts" ON uhc_ministats USING btree (absorption_hearts);
CREATE INDEX "Noneuhc_ministats_golden_heads" ON uhc_ministats USING btree (golden_heads);
CREATE INDEX "Noneuhc_ministats_golden_apples" ON uhc_ministats USING btree (golden_apples);
CREATE INDEX "Noneuhc_ministats_nether_portals" ON uhc_ministats USING btree (nether_portals);
CREATE INDEX "Noneuhc_ministats_end_portals" ON uhc_ministats USING btree (end_portals);

