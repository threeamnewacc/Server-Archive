CREATE TABLE race_tracks (
    race_track_name character varying(128) NOT NULL,
    num_of_laps integer NOT NULL,
    slowblocks character varying(128) NOT NULL,
    allowblocks character varying(128) NOT NULL
);

ALTER TABLE ONLY race_tracks
    ADD CONSTRAINT "Nonerace_tracks_race_track_name_pkey" PRIMARY KEY (race_track_name);

CREATE TABLE race_track_itemblock_locations (
    race_track_name character varying(55) NOT NULL,
    x real NOT NULL,
    y real NOT NULL,
    z real NOT NULL
);

ALTER TABLE ONLY race_track_itemblock_locations
    ADD CONSTRAINT "Nonerace_track_itemblock_locations_x_y_z_pkey" PRIMARY KEY (x, y, z);

CREATE SEQUENCE race_track_checkpoints_race_track_checkpoint_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE race_track_checkpoints (
    race_track_checkpoint_id integer DEFAULT nextval('race_track_checkpoints_race_track_checkpoint_id_seq'::regclass) NOT NULL,
    race_track_name character varying(55) NOT NULL,
    race_track_checkpoint_name character varying(55) NOT NULL,
    race_track_next_checkpoint_name character varying(55) NOT NULL,
    race_track_point_id integer NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL
);

ALTER TABLE ONLY race_track_checkpoints
    ADD CONSTRAINT "Nonerace_track_checkpoints_race_track_checkpoint_id_pkey" PRIMARY KEY (race_track_checkpoint_id);

CREATE TABLE race_track_finish_line_blocks (
    race_track_name character varying(55) NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL
);

ALTER TABLE ONLY race_track_finish_line_blocks
    ADD CONSTRAINT "Nonerace_track_finish_line_blocks_x_y_z_pkey" PRIMARY KEY (x, y, z);