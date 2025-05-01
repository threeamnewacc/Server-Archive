CREATE TABLE sg_gifted_matches (
  uuid character varying(55) NOT NULL,
  day date NOT NULL
);

ALTER TABLE ONLY sg_gifted_matches
ADD CONSTRAINT "Nonesg_gifted_matches_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE sg_vote_records (
  uuid character varying(64) NOT NULL,
  vote_date timestamp without time zone NOT NULL,
  PRIMARY KEY (vote_date, uuid)
);
CREATE INDEX "Nonesg_vote_records_vote_date" ON sg_vote_records USING BTREE (vote_date);

CREATE TABLE sg_ranked_left (
  uuid character varying(55) NOT NULL,
  ranked_left integer NOT NULL,
  day date NOT NULL
);

ALTER TABLE ONLY sg_ranked_left
ADD CONSTRAINT "Nonesg_ranked_left_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE mcsg_ladder_ratings_s1 (
    lid integer NOT NULL,
    uuid character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY mcsg_ladder_ratings_s1
    ADD CONSTRAINT "Nonemcsg_ladder_ratings_s1_lid_uuid_pkey_1" PRIMARY KEY (lid, uuid);
CREATE INDEX "Nonemcsg_ladder_ratings_s1_uuid_1_1" ON mcsg_ladder_ratings_s1 USING btree (uuid);
CREATE INDEX "Nonemcsg_ladder_ratings_s1_rating_1_1" ON mcsg_ladder_ratings_s1 USING btree (rating);

ALTER TABLE sg_ministats ADD COLUMN tier1_opened integer NOT NULL;
ALTER TABLE sg_ministats ADD COLUMN tier2_opened integer NOT NULL;

CREATE INDEX "Nonesg_ministats_tier1_opened" ON sg_ministats USING btree (tier1_opened);
CREATE INDEX "Nonesg_ministats_tier2_opened" ON sg_ministats USING btree (tier2_opened);
