CREATE TABLE XXX_shops (
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL,
    item bytea NOT NULL,
    item_description character varying(28) NOT NULL,
    amount integer NOT NULL,
    price integer NOT NULL,
    buy character varying(4) NOT NULL,
    world character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_shops
    ADD CONSTRAINT "NoneXXX_shops_x_y_z_world_pkey" PRIMARY KEY (x, y, z, world);

CREATE TABLE XXX_item_shops (
  x integer NOT NULL,
  y integer NOT NULL,
  z integer NOT NULL,
  item bytea NOT NULL,
  item_description character varying(28) NOT NULL,
  amount integer NOT NULL,
  price integer NOT NULL,
  item_price character varying(512) NOT NULL,
  world character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_item_shops
  ADD CONSTRAINT "NoneXXX_item_shops_x_y_z_world_pkey" PRIMARY KEY (x, y, z, world);

CREATE TABLE XXX_repair_shops (
    x integer NOT NULL,
    y integer NOT NULL,
    z integer NOT NULL,
    durability integer NOT NULL,
    price integer NOT NULL,
    world character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_repair_shops
    ADD CONSTRAINT "NoneXXX_repair_shops_x_y_z_world_pkey" PRIMARY KEY (x, y, z, world);
