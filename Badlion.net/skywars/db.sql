ALTER TABLE swcffa_ministats ADD COLUMN tier1_opened integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN tier2_opened integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN snow_egg_shot integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN snow_egg_hit integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN snow_egg_accuracy double precision NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN levels integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN mobs_spawned integer NOT NULL;
ALTER TABLE swcffa_ministats ADD COLUMN blocks_placed integer NOT NULL;

CREATE INDEX "Noneswcffa_ministats_tier1_opened" ON swcffa_ministats USING btree (tier1_opened);
CREATE INDEX "Noneswcffa_ministats_tier2_opened" ON swcffa_ministats USING btree (tier2_opened);
CREATE INDEX "Noneswcffa_ministats_snow_egg_shot" ON swcffa_ministats USING btree (snow_egg_shot);
CREATE INDEX "Noneswcffa_ministats_snow_egg_hit" ON swcffa_ministats USING btree (snow_egg_hit);
CREATE INDEX "Noneswcffa_ministats_snow_egg_accuracy" ON swcffa_ministats USING btree (snow_egg_accuracy);
CREATE INDEX "Noneswcffa_ministats_levels" ON swcffa_ministats USING btree (levels);
CREATE INDEX "Noneswcffa_ministats_mobs_spawned" ON swcffa_ministats USING btree (mobs_spawned);
CREATE INDEX "Noneswcffa_ministats_blocks_placed" ON swcffa_ministats USING btree (blocks_placed);