CREATE TABLE smellyparticles_player_particles (
  uuid CHARACTER VARYING (36) NOT NULL,
  particle_name CHARACTER VARYING (32) NOT NULL,
  particle_equipped BOOLEAN DEFAULT FALSE
);

CREATE INDEX "Nonesmellyparticles_player_particles_uuid" ON smellyparticles_player_particles USING BTREE (uuid);

CREATE TABLE smellypets_player_pets (
  uuid CHARACTER VARYING (36) NOT NULL,
  pet_type CHARACTER VARYING (16) NOT NULL,
  pet_name CHARACTER VARYING (32),
  pet_spawned BOOLEAN DEFAULT FALSE
);

CREATE INDEX "Nonesmellypets_player_pets_uuid" ON smellypets_player_pets USING BTREE (uuid);