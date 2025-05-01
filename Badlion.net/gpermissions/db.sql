CREATE TABLE XXX_gperms_groups (
    group_name character varying(55) NOT NULL,
    prefix character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_groups
    ADD CONSTRAINT "NoneXXX_gperms_groups_group_name_pkey" PRIMARY KEY (group_name);

CREATE TABLE XXX_gperms_group_permissions (
    group_name character varying(55) NOT NULL,
    permission_name character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_group_permissions
    ADD CONSTRAINT "NoneXXX_gperms_group_permissions_group_name_permission_name_p" PRIMARY KEY (group_name, permission_name);

CREATE TABLE XXX_gperms_users (
    uuid character varying(64) NOT NULL,
    "group" character varying(55) NOT NULL,
    prefix character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_users
    ADD CONSTRAINT "NoneXXX_gperms_users_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_gperms_user_permissions (
    uuid character varying(64) NOT NULL,
    permission_name character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_user_permissions
    ADD CONSTRAINT "NoneXXX_gperms_user_permissions_uuid_permission_name_pkey" PRIMARY KEY (uuid, permission_name);

CREATE TABLE XXX_gperms_donators (
    uuid character varying(64) NOT NULL
);

CREATE TABLE XXX_gperms_user_subgroups (
    uuid character varying(36) NOT NULL,
    group_name character varying(55) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_user_subgroups
    ADD CONSTRAINT "NoneXXX_gperms_user_subgroups_uuid_group_name_pkey" PRIMARY KEY (uuid, group_name);

ALTER TABLE ONLY XXX_gperms_donators
    ADD CONSTRAINT "NoneXXX_gperms_donators_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_gperms_famous (
    uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_famous
    ADD CONSTRAINT "NoneXXX_gperms_famous_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_gperms_youtubers (
    uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_youtubers
    ADD CONSTRAINT "NoneXXX_gperms_youtubers_uuid_pkey" PRIMARY KEY (uuid);

CREATE TABLE XXX_gperms_twitch (
    uuid character varying(64) NOT NULL
);

ALTER TABLE ONLY XXX_gperms_twitch
    ADD CONSTRAINT "NoneXXX_gperms_twitch_uuid_pkey" PRIMARY KEY (uuid);
