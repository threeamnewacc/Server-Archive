CREATE TABLE build_arenas_s14 (
  arena_name character varying(55) NOT NULL,
  types character varying(55) NOT NULL,
  warp_1 character varying(55) NOT NULL,
  warp_2 character varying(55) NOT NULL,
  extra_data text NOT NULL DEFAULT ''
);

ALTER TABLE ONLY build_arenas_s14
ADD CONSTRAINT "Nonebuild_arenas_s14_arena_name_pkey" PRIMARY KEY (arena_name);

CREATE TABLE build_warps_s14 (
  warp_name character varying(55) NOT NULL,
  x double precision NOT NULL,
  y double precision NOT NULL,
  z double precision NOT NULL,
  yaw real NOT NULL,
  pitch real NOT NULL
);

ALTER TABLE ONLY build_warps_s14
ADD CONSTRAINT "Nonebuild_warps_s14_warp_name_pkey" PRIMARY KEY (warp_name);
