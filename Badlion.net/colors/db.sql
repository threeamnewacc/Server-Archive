CREATE TABLE colors (
    uuid character varying(64) NOT NULL,
    color character varying(20) NOT NULL
);

ALTER TABLE ONLY colors
    ADD CONSTRAINT "Nonecolors_uuid_pkey" PRIMARY KEY (uuid);
