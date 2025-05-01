CREATE TABLE IF NOT EXISTS arenas (
	arena_name VARCHAR(55) NOT NULL,
	warp_1 VARCHAR(55) NOT NULL,
	warp_2 VARCHAR(55) NOT NULL,
	PRIMARY KEY (arena_name)
) ENGINE=InnoDB charset=utf8;

CREATE TABLE IF NOT EXISTS ladders (
	ladder_id INT (11) NOT NULL AUTO_INCREMENT,
	ladder_name VARCHAR(55) NOT NULL,
	PRIMARY KEY (ladder_id)
) ENGINE=InnoDB charset=utf8;

CREATE TABLE IF NOT EXISTS ladder_ratings (
	lid INT (11) NOT NULL,
	username VARCHAR (55) BINARY NOT NULL,
	rating INT (11) NOT NULL,
	wins INT (11) NOT NULL,
	losses INT (11) NOT NULL,
	PRIMARY KEY (lid, username)
) ENGINE=InnoDB charset=utf8;

CREATE TABLE IF NOT EXISTS user_limits (
	username VARCHAR (55) NOT NULL,
	amount_donated INT (11) NOT NULL,
	ranked_limit_per_day INT (11) NOT NULL,
	expiration_date DATE NOT NULL,
	PRIMARY KEY (username)
) ENGINE=InnoDB charset=utf8;

CREATE TABLE IF NOT EXISTS user_num_of_ranked (
	username VARCHAR (55) BINARY NOT NULL,
	num_of_matches INT (11) NOT NULL,
	day DATE NOT NULL,
	PRIMARY KEY (username, day)
) ENGINE=InnoDB charset=utf8;

CREATE TABLE IF NOT EXISTS sg_user_num_of_ranked (
  username VARCHAR (55) BINARY NOT NULL,
  num_of_matches INT (11) NOT NULL,
  day DATE NOT NULL,
  PRIMARY KEY (username, day)
) ENGINE=InnoDB charset=utf8;