CREATE TABLE forge_blacklisted_mods (
    mod_name character varying (128) NOT NULL,
    mod_version character varying (128) NOT NULL
);

CREATE TABLE forge_mod_logins (
    uuid character varying(36) NOT NULL,
    ip bigint NOT NULL,
    mod_name character varying (128) NOT NULL,
    mod_version character varying (128) NOT NULL,
    first_login timestamp with time zone,
    last_login timestamp with time zone,
    num_of_logins integer NOT NULL
);

CREATE INDEX "Noneforge_mod_logins_uuid" ON forge_mod_logins USING btree (uuid);
CREATE INDEX "Noneforge_mod_logins_ip" ON forge_mod_logins USING btree (ip);
CREATE INDEX "Noneforge_mod_logins_mod_name" ON forge_mod_logins USING btree (mod_name);
CREATE INDEX "Noneforge_mod_logins_mod_version" ON forge_mod_logins USING btree (mod_version);
