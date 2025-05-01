CREATE TABLE auction_items_held (
    uuid character varying(64) NOT NULL,
    auction_item bytea NOT NULL,
    purchase_time timestamp without time zone
);

CREATE TABLE auction_alerts (
    uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY auction_alerts
    ADD CONSTRAINT "Noneauction_alerts_uuid_pkey" PRIMARY KEY (uuid);

CREATE SEQUENCE auction_items_auction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE auction_items (
    auction_id integer DEFAULT nextval('auction_items_auction_id_seq'::regclass) NOT NULL,
    item character varying(32) NOT NULL,
    stack_size integer NOT NULL,
    durability integer NOT NULL,
    sold_by character varying(64) NOT NULL,
    purchased_by character varying(64) NOT NULL,
    starting_bid integer NOT NULL,
    minimum_bid integer NOT NULL,
    final_bid integer NOT NULL,
    selling_time timestamp with time zone
);

ALTER TABLE ONLY auction_items
    ADD CONSTRAINT "Noneauction_items_auction_id_pkey" PRIMARY KEY (auction_id);
CREATE INDEX "Noneauction_items_item" ON auction_items USING btree (item);
CREATE INDEX "Noneauction_items_purchased_by" ON auction_items USING btree (purchased_by);
CREATE UNIQUE INDEX "Noneauction_items_selling_time" ON auction_items USING btree (selling_time);
CREATE INDEX "Noneauction_items_sold_by" ON auction_items USING btree (sold_by);

CREATE SEQUENCE auction_bids_auction_bid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE auction_bids (
    auction_bid_id integer DEFAULT nextval('auction_bids_auction_bid_id_seq'::regclass) NOT NULL,
    auction_id integer NOT NULL,
    player character varying(64) NOT NULL,
    bid integer NOT NULL,
    bid_time timestamp with time zone
);

ALTER TABLE ONLY auction_bids
    ADD CONSTRAINT "Noneauction_bids_auction_bid_id_pkey" PRIMARY KEY (auction_bid_id);
CREATE INDEX "Noneauction_bids_player" ON auction_bids USING btree (player);

CREATE TABLE auction_items_enchantments (
    auction_id integer NOT NULL,
    enchantment character varying(32) NOT NULL,
    enchantment_level integer NOT NULL
);

ALTER TABLE ONLY auction_items_enchantments
    ADD CONSTRAINT "Noneauction_items_enchantments_auction_id_enchantment_pkey" PRIMARY KEY (auction_id, enchantment);
CREATE INDEX "Noneauction_items_enchantments_enchantment" ON auction_items_enchantments USING btree (enchantment);
CREATE INDEX "Noneauction_items_enchantments_enchantment_enchantment_level" ON auction_items_enchantments USING btree (enchantment, enchantment_level);
