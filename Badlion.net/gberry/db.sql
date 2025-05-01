CREATE TABLE user_data (
   uuid character varying(36) NOT NULL,
   currency integer NOT NULL DEFAULT 0,
   cosmetics json NOT NULL DEFAULT '{}',
   player_visibility boolean NOT NULL DEFAULT 'true',
   cases json NOT NULL DEFAULT '{}',
   sg_settings json NOT NULL DEFAULT '{}',
   disguise_settings json NOT NULL DEFAULT '{}',
   chat_settings json NOT NULL DEFAULT '{}',
   arena_settings json NOT NULL DEFAULT '{}',
   stat_resets json NOT NULL DEFAULT '{}',
   banned_stats json NOT NULL DEFAULT '{}',
   false_ban BOOLEAN NOT NULL DEFAULT '{}',
   lobby_flight BOOLEAN NOT NULL DEFAULT '{}'
);

ALTER TABLE ONLY user_data ADD CONSTRAINT "Noneuser_data_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE global_settings (
   setting character varying(36) NOT NULL,
   val character varying(36) NOT NULL
);

ALTER TABLE ONLY global_settings ADD CONSTRAINT "Noneglobal_settings" PRIMARY KEY (setting);

CREATE TABLE gchat_filters (
   regex character varying(128) NOT NULL,
   punishment_length character varying(5) NOT NULL,
   punishment_type character varying(1) NOT NULL,
   reason character varying(128) NOT NULL
);

ALTER TABLE ONLY gchat_filters ADD CONSTRAINT "Nonegchat_filters" PRIMARY KEY (regex);

CREATE TABLE badlion_performance (
   server_name character varying(32) NOT NULL,
   ts timestamp with time zone NOT NULL,
   traces text NOT NULL
);

CREATE INDEX "Nonebadlion_performance_server_name" ON badlion_performance USING btree (server_name);
CREATE INDEX "Nonebadlion_performance_ts" ON badlion_performance USING btree (ts);

CREATE TABLE player_command_logs (
  uuid character varying(36) NOT NULL,
  log_time timestamp without time zone NOT NULL,
  server_name character varying(16) NOT NULL,
  command TEXT NOT NULL
);

CREATE INDEX "Noneplayer_command_logs_uuid" ON player_command_logs USING btree (uuid);
CREATE INDEX "Noneplayer_command_logs_log_time" ON player_command_logs USING btree (log_time);

CREATE TABLE server_reboot_times (
  server_name character varying(16) NOT NULL,
  reboot_time character varying(5) NOT NULL,
  reboot_with_players BOOLEAN NOT NULL
);

ALTER TABLE ONLY server_reboot_times
ADD CONSTRAINT "Noneserver_reboot_times_server_name_reboot_time_pkey" PRIMARY KEY (server_name, reboot_time);
