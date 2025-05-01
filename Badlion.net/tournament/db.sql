CREATE SEQUENCE tournaments_tournament_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tournaments (
    tournament_id integer DEFAULT nextval('tournaments_tournament_id_seq'::regclass) NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone,

);

CREATE SEQUENCE tournaments_series_series_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tournaments_series (
    series_id integer DEFAULT nextval('tournaments_series_series_id_seq'::regclass) NOT NULL,
    tournament_id integer NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone,
    teams json NOT NULL,
    extra_data json NULL
);

CREATE SEQUENCE tournaments_rounds_round_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE tournaments_rounds (
    round_id integer DEFAULT nextval('tournaments_rounds_round_id_seq'::regclass) NOT NULL,
    tournament_id integer NOT NULL,
    start_time timestamp with time zone NOT NULL,
    end_time timestamp with time zone,
    teams json NOT NULL,
    match_data json NOT NULL
);