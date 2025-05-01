CREATE TABLE kits_s13 (
  owner character varying(64) NOT NULL,
  kitname character varying(64) DEFAULT ''::character varying NOT NULL,
  kitid integer NOT NULL,
  items bytea NOT NULL,
  armor bytea NOT NULL,
  tag character varying(55) DEFAULT ''::character varying NOT NULL
);

ALTER TABLE ONLY kits_s13
ADD CONSTRAINT "Nonekits_s13_tag_kitname_kitid_pkey" PRIMARY KEY (kitname, tag, kitid);
CREATE INDEX "Nonekits_s13_owner" ON kits_s13 USING btree (owner);
CREATE INDEX "Nonekits_s13_tag" ON kits_s13 USING BTREE (tag);

CREATE TABLE kits_s13_v19 (
  owner character varying(64) NOT NULL,
  kitname character varying(64) DEFAULT ''::character varying NOT NULL,
  kitid integer NOT NULL,
  items bytea NOT NULL,
  extra_items bytea NOT NULL,
  armor bytea NOT NULL,
  tag character varying(55) DEFAULT ''::character varying NOT NULL
);

ALTER TABLE ONLY kits_s13_v19
ADD CONSTRAINT "Nonekits_s13_tag_kitname_kitid_pkey" PRIMARY KEY (kitname, tag, kitid);
CREATE INDEX "Nonekits_s13_v19_owner" ON kits_s13_v19 USING btree (owner);
CREATE INDEX "Nonekits_s13_v19_tag" ON kits_s13_v19 USING BTREE (tag);
