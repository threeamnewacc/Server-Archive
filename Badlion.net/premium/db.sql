CREATE TABLE IF NOT EXISTS user_roles (
	username VARCHAR (55) NOT NULL,
	role_id INT (11) NOT NULL,
	PRIMARY KEY (username, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS user_donation_total (
	username VARCHAR (55) NOT NULL,
	donation_total INT (11) NOT NULL,
	PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE purchases (
	transaction_id character varying (64) NOT NULL,
	status character varying (16) NOT NULL,
    uuid character varying (36) NOT NULL,
    username character varying (16) NOT NULL,
    price double precision NOT NULL,
    currency character varying (16) NOT NULL,
    ts timestamp with time zone NOT NULL,
    email character varying (255) NOT NULL,
    ip character varying(64) NOT NULL,
    package_id integer NOT NULL,
    package_price double precision NOT NULL,
    package_expiration_date timestamp with time zone,
    country character varying (5) NOT NULL DEFAULT '',
    gateway character varying (16) NOT NULL DEFAULT ''
);

CREATE INDEX "Nonepurchases_transaction_id" ON purchases USING btree (transaction_id);
CREATE INDEX "Nonepurchases_uuid" ON purchases USING btree (uuid);
CREATE INDEX "Nonepurchases_username" ON purchases USING btree (username);
