CREATE TABLE potion_message_options_s14 (
  uuid character varying(55) NOT NULL,
  message_options bytea NOT NULL
);

ALTER TABLE ONLY potion_message_options_s14
ADD CONSTRAINT "Nonepotion_message_options_s14_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE potion_donator_events_s14 (
  uuid character varying(55) NOT NULL,
  event_time bigint NOT NULL
);

ALTER TABLE ONLY potion_donator_events_s14
ADD CONSTRAINT "Nonepotion_donator_events_s14_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE potion_matches_left_s14 (
  uuid character varying(55) NOT NULL,
  num_ranked_left integer NOT NULL,
  num_unranked_left integer NOT NULL,
  day date NOT NULL
);

CREATE UNIQUE INDEX "Nonepotion_matches_left_s14_uuid_day" ON potion_matches_left_s14 USING btree (uuid, day);

CREATE TABLE potion_gifted_matches_s14 (
  uuid character varying(55) NOT NULL,
  day date NOT NULL
);

ALTER TABLE ONLY potion_gifted_matches_s14
ADD CONSTRAINT "Nonepotion_gifted_matches_s14_uuid_day_pkey" PRIMARY KEY (uuid, day);

CREATE TABLE matches_played_region_s14 (
   uuid character varying(64) NOT NULL,
   region character varying(5) NOT NULL,
   matches integer NOT NULL
);

ALTER TABLE ONLY matches_played_region_s14
    ADD CONSTRAINT "Nonematches_played_region_s14_uuid_region_pkey" PRIMARY KEY (uuid, region);
CREATE INDEX "Nonematches_played_region_s14_uuid" ON matches_played_region_s14 USING btree (uuid);
CREATE INDEX "Nonematches_played_region_s14_mu" ON matches_played_region_s14 USING btree (region);

CREATE TABLE rating_resets_s14 (
    uuid character varying(64) NOT NULL,
    ts timestamp with time zone NOT NULL,
    data bytea NOT NULL
);

CREATE INDEX "Nonerating_resets_s14_uuid" ON rating_resets_s14 USING btree (uuid);

CREATE TABLE ladder_ratings_s14 (
    lid integer NOT NULL,
    uuid character varying(64) NOT NULL,
    mu double precision NOT NULL,
    phi double precision NOT NULL,
    sigma double precision NOT NULL,
    ranked_wins integer NOT NULL,
    ranked_losses integer NOT NULL,
    unranked_wins integer NOT NULL,
    unranked_losses integer NOT NULL,
    holdover double precision NOT NULL,
    highest_mu double precision NOT NULL,
    main_region character varying(5)
);

ALTER TABLE ONLY ladder_ratings_s14
    ADD CONSTRAINT "Noneladder_ratings_s14_lid_uuid_pkey" PRIMARY KEY (lid, uuid);
CREATE INDEX "Noneladder_ratings_s14_uuid" ON ladder_ratings_s14 USING btree (uuid);
CREATE INDEX "Noneladder_ratings_s14_mu" ON ladder_ratings_s14 USING btree (mu);
CREATE INDEX "Noneladder_ratings_s14_lid" ON ladder_ratings_s14 USING btree (lid);

CREATE TABLE ladder_ratings_party_2_s14 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    mu double precision NOT NULL,
    phi double precision NOT NULL,
    sigma double precision NOT NULL,
    ranked_wins integer NOT NULL,
    ranked_losses integer NOT NULL,
    holdover double precision NOT NULL,
    highest_mu double precision NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_2_s14
    ADD CONSTRAINT "Noneladder_ratings_party_2_s14_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, lid);
CREATE INDEX "Noneladder_ratings_party_2_s14_player1" ON ladder_ratings_party_2_s14 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_2_s14_player2" ON ladder_ratings_party_2_s14 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_2_s14_mu" ON ladder_ratings_party_2_s14 USING btree (mu);

CREATE TABLE ladder_ratings_party_3_s14 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    player3 character varying(64) NOT NULL,
    mu double precision NOT NULL,
    phi double precision NOT NULL,
    sigma double precision NOT NULL,
    ranked_wins integer NOT NULL,
    ranked_losses integer NOT NULL,
    holdover double precision NOT NULL,
    highest_mu double precision NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_3_s14
    ADD CONSTRAINT "Noneladder_ratings_party_3_s14_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, player3, lid);
CREATE INDEX "Noneladder_ratings_party_3_s14_player1" ON ladder_ratings_party_3_s14 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_3_s14_player2" ON ladder_ratings_party_3_s14 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_3_s14_player3" ON ladder_ratings_party_3_s14 USING btree (player3);
CREATE INDEX "Noneladder_ratings_party_3_s14_mu" ON ladder_ratings_party_3_s14 USING btree (mu);

CREATE TABLE ladder_ratings_clan_5_s14 (
    lid integer NOT NULL,
    clan_id integer NOT NULL,
    mu double precision NOT NULL,
    phi double precision NOT NULL,
    sigma double precision NOT NULL,
    ranked_wins integer NOT NULL,
    ranked_losses integer NOT NULL,
    holdover double precision NOT NULL,
    highest_mu double precision NOT NULL
);

ALTER TABLE ONLY ladder_ratings_clan_5_s14
    ADD CONSTRAINT "Noneladder_ratings_clan_5_s14_clan_id_lid_pkey" PRIMARY KEY (clan_id, lid);
CREATE INDEX "Noneladder_ratings_clan_5_s14_mu" ON ladder_ratings_clan_5_s14 USING btree (mu);


CREATE SEQUENCE kit_pvp_matches_s14_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_s14 (
    match_id integer DEFAULT nextval('kit_pvp_matches_s14_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    region character varying(5) NOT NULL,
    winner character varying(64) NOT NULL,
    loser character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_s14
    ADD CONSTRAINT "Nonekit_pvp_matches_s14_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_s14_loser" ON kit_pvp_matches_s14 USING btree (loser);
CREATE INDEX "Nonekit_pvp_matches_s14_season" ON kit_pvp_matches_s14 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_s14_region" ON kit_pvp_matches_s14 USING btree (region);
CREATE INDEX "Nonekit_pvp_matches_s14_winner" ON kit_pvp_matches_s14 USING btree (winner);
CREATE INDEX "Nonekit_pvp_matches_s14_winner_loser" ON kit_pvp_matches_s14 USING btree (winner, loser);

CREATE SEQUENCE kit_pvp_matches_2_s14_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_2_s14 (
    match_id integer DEFAULT nextval('kit_pvp_matches_2_s14_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    region character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_2_s14
    ADD CONSTRAINT "Nonekit_pvp_matches_2_s14_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_2_s14_loser1" ON kit_pvp_matches_2_s14 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_2_s14_loser2" ON kit_pvp_matches_2_s14 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_2_s14_season" ON kit_pvp_matches_2_s14 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_2_s14_region" ON kit_pvp_matches_2_s14 USING btree (region);
CREATE INDEX "Nonekit_pvp_matches_2_s14_winner1" ON kit_pvp_matches_2_s14 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_2_s14_winner2" ON kit_pvp_matches_2_s14 USING btree (winner2);

CREATE SEQUENCE kit_pvp_matches_3_s14_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_3_s14 (
    match_id integer DEFAULT nextval('kit_pvp_matches_3_s14_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    region character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    winner3 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    loser3 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_3_s14
    ADD CONSTRAINT "Nonekit_pvp_matches_3_s14_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_3_s14_loser1" ON kit_pvp_matches_3_s14 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_3_s14_loser2" ON kit_pvp_matches_3_s14 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_3_s14_loser3" ON kit_pvp_matches_3_s14 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_3_s14_season" ON kit_pvp_matches_3_s14 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_3_s14_region" ON kit_pvp_matches_3_s14 USING btree (region);
CREATE INDEX "Nonekit_pvp_matches_3_s14_winner1" ON kit_pvp_matches_3_s14 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_3_s14_winner2" ON kit_pvp_matches_3_s14 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_3_s14_winner3" ON kit_pvp_matches_3_s14 USING btree (winner3);

CREATE SEQUENCE kit_pvp_matches_clan_5_s14_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_clan_5_s14 (
    match_id integer DEFAULT nextval('kit_pvp_matches_clan_5_s14_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    region character varying(5) NOT NULL,
    winner_clan_id integer NOT NULL,
    loser_clan_id integer NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    winner3 character varying(64) NOT NULL,
    winner4 character varying(64) NOT NULL,
    winner5 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    loser3 character varying(64) NOT NULL,
    loser4 character varying(64) NOT NULL,
    loser5 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_clan_5_s14
    ADD CONSTRAINT "Nonekit_pvp_matches_clan_5_s14_match_id_winner_clan_id_loser_clan_id_pkey" PRIMARY KEY (match_id, winner_clan_id, loser_clan_id);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_loser1" ON kit_pvp_matches_clan_5_s14 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_loser2" ON kit_pvp_matches_clan_5_s14 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_loser3" ON kit_pvp_matches_clan_5_s14 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_loser4" ON kit_pvp_matches_clan_5_s14 USING btree (loser4);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_loser5" ON kit_pvp_matches_clan_5_s14 USING btree (loser5);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_season" ON kit_pvp_matches_clan_5_s14 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_region" ON kit_pvp_matches_clan_5_s14 USING btree (region);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_winner1" ON kit_pvp_matches_clan_5_s14 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_winner2" ON kit_pvp_matches_clan_5_s14 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_winner3" ON kit_pvp_matches_clan_5_s14 USING btree (winner3);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_winner4" ON kit_pvp_matches_clan_5_s14 USING btree (winner4);
CREATE INDEX "Nonekit_pvp_matches_clan_5_s14_winner5" ON kit_pvp_matches_clan_5_s14 USING btree (winner5);

CREATE TABLE potion_event_stats_s14 (
    uuid character varying(36) NOT NULL,
    type character varying(12) NOT NULL,
    wins integer NOT NULL,
    rating integer NOT NULL,
    games integer NOT NULL,
    kills integer NOT NULL,
    deaths integer NOT NULL
);

ALTER TABLE ONLY potion_event_stats_s14 ADD CONSTRAINT "Nonepotion_event_stats_s14_uuid_type_pkey" PRIMARY KEY (uuid, type);
CREATE INDEX "Nonepotion_event_stats_s14_uuid" ON potion_event_stats_s14 USING btree (uuid);
CREATE INDEX "Nonepotion_event_stats_s14_wins" ON potion_event_stats_s14 USING btree (wins);
CREATE INDEX "Nonepotion_event_stats_s14_rating" ON potion_event_stats_s14 USING btree (rating);
CREATE INDEX "Nonepotion_event_stats_s14_games" ON potion_event_stats_s14 USING btree (games);
CREATE INDEX "Nonepotion_event_stats_s14_kills" ON potion_event_stats_s14 USING btree (kills);
CREATE INDEX "Nonepotion_event_stats_s14_deaths" ON potion_event_stats_s14 USING btree (deaths);
CREATE INDEX "Nonepotion_event_stats_s14_type" ON potion_event_stats_s14 USING btree (type);

CREATE TABLE potion_pvp_ffa_s14 (
  uuid character varying(64) NOT NULL,
  ruleset character varying(20) NOT NULL,
  kills integer NOT NULL,
  max_kill_streak integer NOT NULL,
  deaths integer NOT NULL
);

ALTER TABLE ONLY potion_pvp_ffa_s14
ADD CONSTRAINT "Nonepotion_pvp_ffa_s14_uuid_pkey" PRIMARY KEY (uuid, ruleset);
CREATE INDEX "Nonepotion_pvp_ffa_s14_uuid" ON potion_pvp_ffa_s14 USING btree (uuid);

CREATE TABLE potion_tdm_stats_s14 (
  uuid character varying(64) NOT NULL,
  kills integer NOT NULL,
  max_kill_streak integer NOT NULL,
  deaths integer NOT NULL,
  assists integer NOT NULL
);

ALTER TABLE ONLY potion_tdm_stats_s14
ADD CONSTRAINT "Nonepotion_tdm_stats_s14_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE ladders_unlimited_unranked_s14 (
  day date NOT NULL,
  ladders character varying(32) NOT NULL
);

ALTER TABLE ONLY ladders_unlimited_unranked_s14 ADD CONSTRAINT "Noneladders_unlimited_unranked_s14_day_ladders_pkey" PRIMARY KEY (day, ladders);