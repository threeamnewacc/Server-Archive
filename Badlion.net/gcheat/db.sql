CREATE SEQUENCE gcheat_logs_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE gcheat_logs (
    record_id integer DEFAULT nextval('gcheat_logs_record_id_seq'::regclass) NOT NULL,
    server character varying(16) NOT NULL,
    msg character varying(255) NOT NULL,
    username character varying(16) NOT NULL,
    uuid character varying(36) NOT NULL,
    log_time timestamp with time zone NOT NULL
);

ALTER TABLE ONLY gcheat_logs ADD CONSTRAINT "Nonegcheat_logs_record_id_pkey" PRIMARY KEY (record_id);
CREATE INDEX "Nonegcheat_logs_uuid" ON gcheat_logs USING btree (uuid);
CREATE INDEX "Nonegcheat_logs_username" ON gcheat_logs USING btree (username);
CREATE INDEX "Nonegcheat_logs_time" ON gcheat_logs USING btree (log_time);
CREATE INDEX "Nonegcheat_logs_server" ON gcheat_logs USING btree (server);

CREATE TABLE gcheat_swing_logs (
    server CHARACTER VARYING(16) NOT NULL,
    uuid CHARACTER VARYING(36) NOT NULL,
    swings INTEGER NOT NULL,
    hits INTEGER NOT NULL,
    swing_hit_percentage DOUBLE PRECISION NOT NULL
);

ALTER TABLE ONLY gcheat_swing_logs
    ADD CONSTRAINT "Nonegcheat_swing_logs_server_uuid_pkey" PRIMARY KEY (server, uuid);
