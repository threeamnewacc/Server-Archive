CREATE TABLE disguise_history (
  uuid character varying(36) NOT NULL,
  disguise_name character varying(16) NOT NULL,
  disguise_time TIMESTAMP without time zone NOT NULL,
  undisguise_time TIMESTAMP without time zone
);

CREATE INDEX "Nonedisguise_history_uuid" ON disguise_history USING btree (uuid);

CREATE TABLE disguise_names (
  disguise_name character varying(16) NOT NULL,
  in_use BOOLEAN NOT NULL
);

CREATE TABLE disguise_skins (
  texture TEXT NOT NULL,
  signature TEXT NOT NULL
);