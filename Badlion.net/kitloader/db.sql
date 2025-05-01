CREATE TABLE kits (
    owner character varying(64) NOT NULL,
    kitname character varying(64) NOT NULL,
    items bytea NOT NULL,
    armor bytea NOT NULL,
    tag character varying(55) DEFAULT ''::character varying NOT NULL
);

ALTER TABLE ONLY kits
    ADD CONSTRAINT "Nonekits_tag_kitname_pkey" PRIMARY KEY (tag, kitname);