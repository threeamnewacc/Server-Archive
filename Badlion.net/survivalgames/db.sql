CREATE TABLE sg_ladder_ratings_s2 (
    gamemode character varying(64) NOT NULL,
    uuid character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL,
    rating_visibility BOOLEAN NOT NULL DEFAULT TRUE,
    stats_visibility BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE ONLY sg_ladder_ratings_s2
    ADD CONSTRAINT "Nonesg_ladder_ratings_s2_gamemode_uuid_pkey_1" PRIMARY KEY (gamemode, uuid);
CREATE INDEX "Nonesg_ladder_ratings_s2_uuid_1_1" ON sg_ladder_ratings_s2 USING btree (uuid);
CREATE INDEX "Nonesg_ladder_ratings_s2_rating_1_1" ON sg_ladder_ratings_s2 USING btree (rating);

ALTER TABLE sg_s2_ministats ADD COLUMN tier1_opened integer NOT NULL;
ALTER TABLE sg_s2_ministats ADD COLUMN tier2_opened integer NOT NULL;
ALTER TABLE sg_s2_ministats ADD COLUMN tier3_opened integer NOT NULL;
ALTER TABLE sg_s2_ministats ADD COLUMN supply_drops_opened integer NOT NULL;

CREATE INDEX "Nonesg_s2_ministats_tier1_opened" ON sg_s2_ministats USING btree (tier1_opened);
CREATE INDEX "Nonesg_s2_ministats_tier2_opened" ON sg_s2_ministats USING btree (tier2_opened);
CREATE INDEX "Nonesg_s2_ministats_tier3_opened" ON sg_s2_ministats USING btree (tier3_opened);
CREATE INDEX "Nonesg_s2_ministats_supply_drops_opened" ON sg_s2_ministats USING btree (supply_drops_opened);
