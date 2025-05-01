CREATE TABLE cmdsigns (
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL,
    commands text NOT NULL,
    p integer DEFAULT 0 NOT NULL,
    world character varying(32) NOT NULL
);

ALTER TABLE ONLY cmdsigns
    ADD CONSTRAINT "Nonecmdsigns_x_y_z_world_pkey" PRIMARY KEY (x, y, z, world);