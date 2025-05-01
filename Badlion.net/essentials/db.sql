CREATE TABLE build_homes (
    uuid character varying(36) NOT NULL,
    name character varying(36) NOT NULL,
    x double precision NOT NULL,
    y double precision NOT NULL,
    z double precision NOT NULL,
    yaw double precision NOT NULL,
    pitch double precision NOT NULL,
    world character varying(36) NOT NULL
);

ALTER TABLE ONLY build_homes ADD CONSTRAINT "Nonebuild_homes_pkey" PRIMARY KEY (uuid, name);
CREATE INDEX "Nonebuild_homes_uuid" ON build_homes USING btree (uuid);
CREATE INDEX "Nonebuild_homes_name" ON build_homes USING btree (name);

CREATE TABLE build_essential_warps (
    name character varying(36) NOT NULL,
    x double precision NOT NULL,
    y double precision NOT NULL,
    z double precision NOT NULL,
    yaw double precision NOT NULL,
    pitch double precision NOT NULL,
    world character varying(36) NOT NULL
);

ALTER TABLE ONLY build_essential_warps ADD CONSTRAINT "Nonebuild_essential_warps_pkey" PRIMARY KEY (name);
CREATE INDEX "Nonebuild_essential_warps_name" ON build_essential_warps USING btree (name);
