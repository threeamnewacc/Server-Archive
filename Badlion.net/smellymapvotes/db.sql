CREATE TABLE smelly_map_votes (
  server_type CHARACTER VARYING (16) NOT NULL,
  map_name CHARACTER VARYING (36) NOT NULL,
  uuid CHARACTER VARYING (36) NOT NULL,
  points INTEGER
);

CREATE INDEX "Nonesmelly_map_votes_map_name" ON smelly_map_votes USING btree (map_name);
CREATE INDEX "Nonesmelly_map_votes_uuid" ON smelly_map_votes USING btree (uuid);
CREATE INDEX "Nonesmelly_map_votes_server_type" ON smelly_map_votes USING btree (server_type);