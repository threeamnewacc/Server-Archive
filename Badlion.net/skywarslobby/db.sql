CREATE TABLE skywars_unlocked_kit_items (
  uuid character varying(36) NOT NULL,
  items text NOT NULL
);

ALTER TABLE ONLY skywars_unlocked_kit_items ADD CONSTRAINT "Noneskywars_unlocked_kit_items_pkey" PRIMARY KEY (uuid);
