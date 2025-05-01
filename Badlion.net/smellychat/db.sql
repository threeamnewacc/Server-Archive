CREATE SEQUENCE reports_report_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE reports (
    report_id integer DEFAULT nextval('reports_report_id_seq'::regclass) NOT NULL,
    time timestamp without time zone NOT NULL,
    server_name character varying(32) NOT NULL,
    reporter character varying(64) NOT NULL,
    reported character varying(64) NOT NULL,
    reason text NOT NULL
);

CREATE UNIQUE INDEX "Nonereports_report_id" ON reports USING btree (report_id);
CREATE INDEX "Nonereports_reported" ON reports USING btree (reported);
CREATE INDEX "Nonereports_reporter" ON reports USING btree (reporter);
CREATE INDEX "Nonereports_server_name" ON reports USING btree (server_name);

CREATE SEQUENCE smelly_chat_logs_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE smelly_chat_logs (
    record_id integer DEFAULT nextval('smelly_chat_logs_record_id_seq'::regclass) NOT NULL,
    log_time timestamp without time zone NOT NULL,
    server_name character varying(16) NOT NULL,
    channel character varying(16),
    sender_uuid character varying(36) NOT NULL,
    sender_username character varying(16) NOT NULL,
    receiver_uuid character varying(36),
    receiver_username character varying(16),
    message text NOT NULL
);

CREATE UNIQUE INDEX "Nonesmelly_chat_logs_record_id" ON smelly_chat_logs USING btree (record_id);
CREATE INDEX "Nonesmelly_chat_logs_server_name" ON smelly_chat_logs USING btree (server_name);
CREATE INDEX "Nonesmelly_chat_logs_sender_uuid" ON smelly_chat_logs USING btree (sender_uuid);
CREATE INDEX "Nonesmelly_chat_logs_sender_username" ON smelly_chat_logs USING btree (sender_username);
CREATE INDEX "Nonesmelly_chat_logs_receiver_uuid" ON smelly_chat_logs USING btree (receiver_uuid);
CREATE INDEX "Nonesmelly_chat_logs_receiver_username" ON smelly_chat_logs USING btree (receiver_username);

CREATE TABLE smelly_chat_settings (
    uuid character varying(36) NOT NULL,
    friends text,
    ignored text,
    marked_players BYTEA,
    settings BYTEA NOT NULL
);

CREATE INDEX "Nonesmelly_chat_settings_uuid" ON smelly_chat_settings USING btree(uuid);