CREATE TABLE arenas (
  arena_name character varying(55) NOT NULL,
  types character varying(55) NOT NULL,
  warp_1 character varying(55) NOT NULL,
  warp_2 character varying(55) NOT NULL,
  extra_data text NOT NULL
);

ALTER TABLE ONLY arenas
ADD CONSTRAINT "Nonearenas_arena_name_pkey" PRIMARY KEY (arena_name);

CREATE TABLE potion_warps (
  warp_name character varying(55) NOT NULL,
  x double precision NOT NULL,
  y double precision NOT NULL,
  z double precision NOT NULL,
  yaw real NOT NULL,
  pitch real NOT NULL
);

ALTER TABLE ONLY potion_warps
ADD CONSTRAINT "Nonepotion_warps_warp_name_pkey" PRIMARY KEY (warp_name);

CREATE TABLE potion_message_options (
  uuid character varying(55) NOT NULL,
  message_options bytea NOT NULL
);

ALTER TABLE ONLY potion_message_options
ADD CONSTRAINT "Nonepotion_message_options_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE potion_donator_events (
  uuid character varying(55) NOT NULL,
  event_time bigint NOT NULL
);

ALTER TABLE ONLY potion_donator_events
ADD CONSTRAINT "Nonepotion_donator_events_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE potion_ranked_left (
  uuid character varying(55) NOT NULL,
  num_ranked_left integer NOT NULL,
  day date NOT NULL
);

CREATE INDEX "Nonepotion_ranked_left_uuid_day" ON potion_ranked_left USING btree (uuid, day);

CREATE TABLE potion_gifted_matches (
  uuid character varying(55) NOT NULL,
  day date NOT NULL
);

ALTER TABLE ONLY potion_gifted_matches
ADD CONSTRAINT "Nonepotion_gifted_matches_uuid_day_pkey" PRIMARY KEY (uuid, day);


-- This is the stuff we use to do new seasons
ALTER TABLE ladder_ratings RENAME TO ladder_ratings_1_7_7;
ALTER TABLE ladder_ratings_party_2 RENAME TO ladder_ratings_party_2_1_7_7;
ALTER SEQUENCE kit_pvp_matches_match_id_seq RENAME TO kit_pvp_matches_match_id_seq_1_7_7;
ALTER TABLE kit_pvp_matches RENAME TO kit_pvp_matches_1_7_7;
ALTER SEQUENCE kit_pvp_matches_2_match_id_seq RENAME TO kit_pvp_matches_2_match_id_seq_1_7_7;
ALTER TABLE kit_pvp_matches_2 RENAME TO kit_pvp_matches_2_1_7_7;

CREATE TABLE kits (
  owner character varying(64) NOT NULL,
  kitname character varying(64) DEFAULT ''::character varying NOT NULL,
  items bytea NOT NULL,
  armor bytea NOT NULL,
  tag character varying(55) DEFAULT ''::character varying NOT NULL
);

ALTER TABLE ONLY kits
ADD CONSTRAINT "Nonekits_tag_kitname_pkey" PRIMARY KEY (kitname, tag);
CREATE INDEX "Nonekits_owner" ON kits USING btree (owner);
CREATE INDEX "Nonekits_tag" ON kits USING BTREE (tag);

CREATE TABLE ladder_ratings_s12 (
    lid integer NOT NULL,
    uuid character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_s12
    ADD CONSTRAINT "Noneladder_ratings_s12_lid_uuid_pkey" PRIMARY KEY (lid, uuid);
CREATE INDEX "Noneladder_ratings_s12_uuid" ON ladder_ratings_s12 USING btree (uuid);
CREATE INDEX "Noneladder_ratings_s12_rating" ON ladder_ratings_s12 USING btree (rating);
CREATE INDEX "Noneladder_ratings_s12_lid" ON ladder_ratings_s12 USING btree (lid);

CREATE TABLE ladder_ratings_party_2_s12 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_2_s12
    ADD CONSTRAINT "Noneladder_ratings_party_2_s12_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, lid);
CREATE INDEX "Noneladder_ratings_party_2_s12_player1" ON ladder_ratings_party_2_s12 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_2_s12_player2" ON ladder_ratings_party_2_s12 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_2_s12_rating" ON ladder_ratings_party_2_s12 USING btree (rating);

CREATE TABLE ladder_ratings_party_3_s12 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    player3 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_3_s12
    ADD CONSTRAINT "Noneladder_ratings_party_3_s12_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, player3, lid);
CREATE INDEX "Noneladder_ratings_party_3_s12_player1" ON ladder_ratings_party_3_s12 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_3_s12_player2" ON ladder_ratings_party_3_s12 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_3_s12_player3" ON ladder_ratings_party_3_s12 USING btree (player3);
CREATE INDEX "Noneladder_ratings_party_3_s12_rating_1_7_7" ON ladder_ratings_party_3_s12 USING btree (rating);

CREATE TABLE ladder_ratings_party_5_s12 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    player3 character varying(64) NOT NULL,
    player4 character varying(64) NOT NULL,
    player5 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_5_s12
    ADD CONSTRAINT "Noneladder_ratings_party_5_s12_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, player3, player4, player5, lid);
CREATE INDEX "Noneladder_ratings_party_5_s12_player1" ON ladder_ratings_party_5_s12 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_5_s12_player2" ON ladder_ratings_party_5_s12 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_5_s12_player3" ON ladder_ratings_party_5_s12 USING btree (player3);
CREATE INDEX "Noneladder_ratings_party_5_s12_player4" ON ladder_ratings_party_5_s12 USING btree (player4);
CREATE INDEX "Noneladder_ratings_party_5_s12_player5" ON ladder_ratings_party_5_s12 USING btree (player5);
CREATE INDEX "Noneladder_ratings_party_5_s12_rating_1_7_7" ON ladder_ratings_party_5_s12 USING btree (rating);

CREATE SEQUENCE kit_pvp_matches_s12_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_s12 (
    match_id integer DEFAULT nextval('kit_pvp_matches_s12_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner character varying(64) NOT NULL,
    loser character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_s12
    ADD CONSTRAINT "Nonekit_pvp_matches_s12_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_s12_loser" ON kit_pvp_matches_s12 USING btree (loser);
CREATE INDEX "Nonekit_pvp_matches_s12_season" ON kit_pvp_matches_s12 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_s12_winner" ON kit_pvp_matches_s12 USING btree (winner);
CREATE INDEX "Nonekit_pvp_matches_s12_winner_loser" ON kit_pvp_matches_s12 USING btree (winner, loser);

CREATE SEQUENCE kit_pvp_matches_2_s12_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_2_s12 (
    match_id integer DEFAULT nextval('kit_pvp_matches_2_s12_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_2_s12
    ADD CONSTRAINT "Nonekit_pvp_matches_2_s12_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_2_s12_loser1" ON kit_pvp_matches_2_s12 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_2_s12_loser2" ON kit_pvp_matches_2_s12 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_2_s12_season" ON kit_pvp_matches_2_s12 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_2_s12_winner1" ON kit_pvp_matches_2_s12 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_2_s12_winner2" ON kit_pvp_matches_2_s12 USING btree (winner2);

CREATE SEQUENCE kit_pvp_matches_3_s12_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_3_s12 (
    match_id integer DEFAULT nextval('kit_pvp_matches_3_s12_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    winner3 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    loser3 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_3_s12
    ADD CONSTRAINT "Nonekit_pvp_matches_3_s12_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_3_s12_loser1" ON kit_pvp_matches_3_s12 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_3_s12_loser2" ON kit_pvp_matches_3_s12 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_3_s12_loser3" ON kit_pvp_matches_3_s12 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_3_s12_season" ON kit_pvp_matches_3_s12 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_3_s12_winner1" ON kit_pvp_matches_3_s12 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_3_s12_winner2" ON kit_pvp_matches_3_s12 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_3_s12_winner3" ON kit_pvp_matches_3_s12 USING btree (winner3);

CREATE SEQUENCE kit_pvp_matches_5_s12_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_5_s12 (
    match_id integer DEFAULT nextval('kit_pvp_matches_5_s12_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
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

ALTER TABLE ONLY kit_pvp_matches_5_s12
    ADD CONSTRAINT "Nonekit_pvp_matches_5_s12_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_5_s12_loser1" ON kit_pvp_matches_5_s12 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_5_s12_loser2" ON kit_pvp_matches_5_s12 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_5_s12_loser3" ON kit_pvp_matches_5_s12 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_5_s12_loser4" ON kit_pvp_matches_5_s12 USING btree (loser4);
CREATE INDEX "Nonekit_pvp_matches_5_s12_loser5" ON kit_pvp_matches_5_s12 USING btree (loser5);
CREATE INDEX "Nonekit_pvp_matches_5_s12_season" ON kit_pvp_matches_5_s12 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_5_s12_winner1" ON kit_pvp_matches_5_s12 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_5_s12_winner2" ON kit_pvp_matches_5_s12 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_5_s12_winner3" ON kit_pvp_matches_5_s12 USING btree (winner3);
CREATE INDEX "Nonekit_pvp_matches_5_s12_winner4" ON kit_pvp_matches_5_s12 USING btree (winner4);
CREATE INDEX "Nonekit_pvp_matches_5_s12_winner5" ON kit_pvp_matches_5_s12 USING btree (winner5);

CREATE TABLE potion_event_stats_s12 (
    uuid character varying(36) NOT NULL,
    type character varying(12) NOT NULL,
    wins integer NOT NULL,
    rating integer NOT NULL,
    games integer NOT NULL,
    kills integer NOT NULL,
    deaths integer NOT NULL
);

ALTER TABLE ONLY potion_event_stats_s12 ADD CONSTRAINT "Nonepotion_event_stats_s12_uuid_type_pkey" PRIMARY KEY (uuid, type);
CREATE INDEX "Nonepotion_event_stats_s12_uuid" ON potion_event_stats_s12 USING btree (uuid);
CREATE INDEX "Nonepotion_event_stats_s12_wins" ON potion_event_stats_s12 USING btree (wins);
CREATE INDEX "Nonepotion_event_stats_s12_rating" ON potion_event_stats_s12 USING btree (rating);
CREATE INDEX "Nonepotion_event_stats_s12_games" ON potion_event_stats_s12 USING btree (games);
CREATE INDEX "Nonepotion_event_stats_s12_kills" ON potion_event_stats_s12 USING btree (kills);
CREATE INDEX "Nonepotion_event_stats_s12_deaths" ON potion_event_stats_s12 USING btree (deaths);
CREATE INDEX "Nonepotion_event_stats_s12_type" ON potion_event_stats_s12 USING btree (type);

CREATE TABLE potion_pvp_ffa_s11 (
  uuid character varying(64) NOT NULL,
  ruleset character varying(20) NOT NULL,
  kills integer NOT NULL,
  max_kill_streak integer NOT NULL,
  deaths integer NOT NULL
);

ALTER TABLE ONLY potion_pvp_ffa_s11
ADD CONSTRAINT "Nonepotion_pvp_ffa_s11_uuid_pkey" PRIMARY KEY (uuid, ruleset);
CREATE INDEX "Nonepotion_pvp_ffa_s11_uuid" ON potion_pvp_ffa_s11 USING btree (uuid);

CREATE TABLE potion_tdm_stats_s12 (
  uuid character varying(64) NOT NULL,
  kills integer NOT NULL,
  max_kill_streak integer NOT NULL,
  deaths integer NOT NULL,
  assists integer NOT NULL
);

ALTER TABLE ONLY potion_tdm_stats_s12
ADD CONSTRAINT "Nonepotion_tdm_stats_s12_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE potion_unranked_kits (
  kit_name character varying(16) NOT NULL
);

ALTER TABLE ONLY potion_unranked_kits ADD CONSTRAINT "Nonepotion_unranked_kits_kit_name_pkey" PRIMARY KEY (kit_name);

CREATE TABLE kits_v19 (
  owner character varying(64) NOT NULL,
  kitname character varying(64) DEFAULT ''::character varying NOT NULL,
  items bytea NOT NULL,
  extra_items bytea NOT NULL,
  armor bytea NOT NULL,
  tag character varying(55) DEFAULT ''::character varying NOT NULL
);

ALTER TABLE ONLY kits_v19
ADD CONSTRAINT "Nonekits_v19_tag_kitname_pkey" PRIMARY KEY (kitname, tag);
CREATE INDEX "Nonekits_v19_owner" ON kits_v19 USING btree (owner);
CREATE INDEX "Nonekits_v19_tag" ON kits_v19 USING BTREE (tag);

CREATE TABLE potion_unranked_kits_v19 (
  kit_name character varying(16) NOT NULL
);

ALTER TABLE ONLY potion_unranked_kits_v19 ADD CONSTRAINT "Nonepotion_unranked_kits_v19_kit_name_pkey" PRIMARY KEY (kit_name);

CREATE TABLE potion_event_stats_s12_v19 (
    uuid character varying(36) NOT NULL,
    type character varying(12) NOT NULL,
    wins integer NOT NULL,
    rating integer NOT NULL,
    games integer NOT NULL,
    kills integer NOT NULL,
    deaths integer NOT NULL
);

ALTER TABLE ONLY potion_event_stats_s12_v19 ADD CONSTRAINT "Nonepotion_event_stats_s12_v19_uuid_type_pkey" PRIMARY KEY (uuid, type);
CREATE INDEX "Nonepotion_event_stats_s12_v19_uuid" ON potion_event_stats_s12_v19 USING btree (uuid);
CREATE INDEX "Nonepotion_event_stats_s12_v19_wins" ON potion_event_stats_s12_v19 USING btree (wins);
CREATE INDEX "Nonepotion_event_stats_s12_v19_rating" ON potion_event_stats_s12_v19 USING btree (rating);
CREATE INDEX "Nonepotion_event_stats_s12_v19_games" ON potion_event_stats_s12_v19 USING btree (games);
CREATE INDEX "Nonepotion_event_stats_s12_v19_kills" ON potion_event_stats_s12_v19 USING btree (kills);
CREATE INDEX "Nonepotion_event_stats_s12_v19_deaths" ON potion_event_stats_s12_v19 USING btree (deaths);
CREATE INDEX "Nonepotion_event_stats_s12_v19_type" ON potion_event_stats_s12_v19 USING btree (type);

CREATE TABLE ladder_ratings_s12_v19 (
    lid integer NOT NULL,
    uuid character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_s12_v19
    ADD CONSTRAINT "Noneladder_ratings_s12_v19_lid_uuid_pkey" PRIMARY KEY (lid, uuid);
CREATE INDEX "Noneladder_ratings_s12_v19_uuid" ON ladder_ratings_s12_v19 USING btree (uuid);
CREATE INDEX "Noneladder_ratings_s12_v19_rating" ON ladder_ratings_s12_v19 USING btree (rating);
CREATE INDEX "Noneladder_ratings_s12_v19_lid" ON ladder_ratings_s12_v19 USING btree (lid);

CREATE TABLE ladder_ratings_party_2_s12_v19 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_2_s12_v19
    ADD CONSTRAINT "Noneladder_ratings_party_2_s12_v19_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, lid);
CREATE INDEX "Noneladder_ratings_party_2_s12_v19_player1" ON ladder_ratings_party_2_s12_v19 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_2_s12_v19_player2" ON ladder_ratings_party_2_s12_v19 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_2_s12_v19_rating" ON ladder_ratings_party_2_s12_v19 USING btree (rating);

CREATE TABLE ladder_ratings_party_3_s12_v19 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    player3 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_3_s12_v19
    ADD CONSTRAINT "Noneladder_ratings_party_3_s12_v19_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, player3, lid);
CREATE INDEX "Noneladder_ratings_party_3_s12_v19_player1" ON ladder_ratings_party_3_s12_v19 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_3_s12_v19_player2" ON ladder_ratings_party_3_s12_v19 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_3_s12_v19_player3" ON ladder_ratings_party_3_s12_v19 USING btree (player3);
CREATE INDEX "Noneladder_ratings_party_3_s12_v19_rating_1_7_7" ON ladder_ratings_party_3_s12_v19 USING btree (rating);

CREATE TABLE ladder_ratings_party_5_s12_v19 (
    lid integer NOT NULL,
    player1 character varying(64) NOT NULL,
    player2 character varying(64) NOT NULL,
    player3 character varying(64) NOT NULL,
    player4 character varying(64) NOT NULL,
    player5 character varying(64) NOT NULL,
    rating integer NOT NULL,
    wins integer NOT NULL,
    losses integer NOT NULL
);

ALTER TABLE ONLY ladder_ratings_party_5_s12_v19
    ADD CONSTRAINT "Noneladder_ratings_party_5_s12_v19_player1_player2_lid_pkey" PRIMARY KEY (player1, player2, player3, player4, player5, lid);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_player1" ON ladder_ratings_party_5_s12_v19 USING btree (player1);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_player2" ON ladder_ratings_party_5_s12_v19 USING btree (player2);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_player3" ON ladder_ratings_party_5_s12_v19 USING btree (player3);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_player4" ON ladder_ratings_party_5_s12_v19 USING btree (player4);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_player5" ON ladder_ratings_party_5_s12_v19 USING btree (player5);
CREATE INDEX "Noneladder_ratings_party_5_s12_v19_rating_1_7_7" ON ladder_ratings_party_5_s12_v19 USING btree (rating);

CREATE SEQUENCE kit_pvp_matches_s12_v19_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_s12_v19 (
    match_id integer DEFAULT nextval('kit_pvp_matches_s12_v19_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner character varying(64) NOT NULL,
    loser character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_s12_v19
    ADD CONSTRAINT "Nonekit_pvp_matches_s12_v19_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_s12_v19_loser" ON kit_pvp_matches_s12_v19 USING btree (loser);
CREATE INDEX "Nonekit_pvp_matches_s12_v19_season" ON kit_pvp_matches_s12_v19 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_s12_v19_winner" ON kit_pvp_matches_s12_v19 USING btree (winner);
CREATE INDEX "Nonekit_pvp_matches_s12_v19_winner_loser" ON kit_pvp_matches_s12_v19 USING btree (winner, loser);

CREATE SEQUENCE kit_pvp_matches_2_s12_v19_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_2_s12_v19 (
    match_id integer DEFAULT nextval('kit_pvp_matches_2_s12_v19_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_2_s12_v19
    ADD CONSTRAINT "Nonekit_pvp_matches_2_s12_v19_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_2_s12_v19_loser1" ON kit_pvp_matches_2_s12_v19 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_2_s12_v19_loser2" ON kit_pvp_matches_2_s12_v19 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_2_s12_v19_season" ON kit_pvp_matches_2_s12_v19 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_2_s12_v19_winner1" ON kit_pvp_matches_2_s12_v19 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_2_s12_v19_winner2" ON kit_pvp_matches_2_s12_v19 USING btree (winner2);

CREATE SEQUENCE kit_pvp_matches_3_s12_v19_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_3_s12_v19 (
    match_id integer DEFAULT nextval('kit_pvp_matches_3_s12_v19_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
    winner1 character varying(64) NOT NULL,
    winner2 character varying(64) NOT NULL,
    winner3 character varying(64) NOT NULL,
    loser1 character varying(64) NOT NULL,
    loser2 character varying(64) NOT NULL,
    loser3 character varying(64) NOT NULL,
    ladder_id smallint NOT NULL,
    data bytea NOT NULL
);

ALTER TABLE ONLY kit_pvp_matches_3_s12_v19
    ADD CONSTRAINT "Nonekit_pvp_matches_3_s12_v19_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_loser1" ON kit_pvp_matches_3_s12_v19 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_loser2" ON kit_pvp_matches_3_s12_v19 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_loser3" ON kit_pvp_matches_3_s12_v19 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_season" ON kit_pvp_matches_3_s12_v19 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_winner1" ON kit_pvp_matches_3_s12_v19 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_winner2" ON kit_pvp_matches_3_s12_v19 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_3_s12_v19_winner3" ON kit_pvp_matches_3_s12_v19 USING btree (winner3);

CREATE SEQUENCE kit_pvp_matches_5_s12_v19_match_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE kit_pvp_matches_5_s12_v19 (
    match_id integer DEFAULT nextval('kit_pvp_matches_5_s12_v19_match_id_seq'::regclass) NOT NULL,
    season character varying(5) NOT NULL,
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

ALTER TABLE ONLY kit_pvp_matches_5_s12_v19
    ADD CONSTRAINT "Nonekit_pvp_matches_5_s12_v19_match_id_pkey" PRIMARY KEY (match_id);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_loser1" ON kit_pvp_matches_5_s12_v19 USING btree (loser1);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_loser2" ON kit_pvp_matches_5_s12_v19 USING btree (loser2);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_loser3" ON kit_pvp_matches_5_s12_v19 USING btree (loser3);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_loser4" ON kit_pvp_matches_5_s12_v19 USING btree (loser4);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_loser5" ON kit_pvp_matches_5_s12_v19 USING btree (loser5);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_season" ON kit_pvp_matches_5_s12_v19 USING btree (season);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner1" ON kit_pvp_matches_5_s12_v19 USING btree (winner1);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner2" ON kit_pvp_matches_5_s12_v19 USING btree (winner2);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner3" ON kit_pvp_matches_5_s12_v19 USING btree (winner3);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner4" ON kit_pvp_matches_5_s12_v19 USING btree (winner4);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner5" ON kit_pvp_matches_5_s12_v19 USING btree (winner5);
CREATE INDEX "Nonekit_pvp_matches_5_s12_v19_winner5" ON kit_pvp_matches_5_s12_v19 USING btree (winner5);

CREATE TABLE potion_pvp_ffa_s11_v19 (
  uuid character varying(64) NOT NULL,
  ruleset character varying(20) NOT NULL,
  kills integer NOT NULL,
  max_kill_streak integer NOT NULL,
  deaths integer NOT NULL
);

ALTER TABLE ONLY potion_pvp_ffa_s11_v19
ADD CONSTRAINT "Nonepotion_pvp_ffa_s11_v19_uuid_pkey" PRIMARY KEY (uuid, ruleset);
CREATE INDEX "Nonepotion_pvp_ffa_s11_v19_uuid" ON potion_pvp_ffa_s11_v19 USING btree (uuid);
