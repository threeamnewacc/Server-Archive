CREATE TABLE XXXX_scores (
    group_id integer NOT NULL,
    score integer NOT NULL
);

ALTER TABLE ONLY XXXX_scores ADD CONSTRAINT "NoneXXXX_scores_group_id" PRIMARY KEY (group_id);
CREATE INDEX "NoneXXXX_scores_score" ON XXXX_scores USING btree(score);

CREATE TABLE XXXX_score_users (
    group_id integer NOT NULL,
    uuid character varying(36) NOT NULL,
    score integer NOT NULL,
);

ALTER TABLE ONLY XXXX_score_users ADD CONSTRAINT "NoneXXXX_score_users_group_id_uuid" PRIMARY KEY (group_id, uuid);
CREATE INDEX "NoneXXXX_score_users_group_id" ON XXXX_score_users USING btree(group_id);
CREATE INDEX "NoneXXXX_score_users_uuid" ON XXXX_score_users USING btree(uuid);
CREATE INDEX "NoneXXXX_score_users_score" ON XXXX_score_users USING btree(score);

CREATE SEQUENCE XXXX_score_user_history_XXXX_score_user_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE XXXX_score_user_history (
    XXXX_score_user_history_id integer DEFAULT nextval('XXXX_score_user_history_XXXX_score_user_history_id_seq'::regclass) NOT NULL,
    uuid character varying(36) NOT NULL,
    group_id integer NOT NULL,
    score integer NOT NULL,
    achieved_time timestamp with time zone NOT NULL,
    reason integer reason NOT NULL,
    active character varying(1) NOT NULL,
);

ALTER TABLE ONLY XXXX_score_user_history ADD CONSTRAINT "NoneXXXX_score_user_history_XXXX_score_user_history_id" PRIMARY KEY (XXXX_score_user_history);
CREATE INDEX "NoneXXXX_score_user_history_uuid" ON XXXX_score_user_history USING btree(uuid);
CREATE INDEX "NoneXXXX_score_user_history_group_id" ON XXXX_score_user_history USING btree(group_id);
CREATE INDEX "NoneXXXX_score_user_history_achieved_time" ON XXXX_score_user_history USING btree(achieved_time);
CREATE INDEX "NoneXXXX_score_user_history_active" ON XXXX_score_user_history USING btree(active);

CREATE SEQUENCE XXXX_score_rollback_XXXX_score_rollback_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE XXXX_score_rollback (
    XXXX_score_rollback_id integer DEFAULT nextval('XXXX_score_rollback_XXXX_score_rollback_id_seq'::regclass) NOT NULL,
    uuid character varying(36) NOT NULL,
    group_id integer NOT NULL,
    num_of_minutes_reverted integer NOT NULL,
    rollback_time timestamp with time zone NOT NULL,
);

ALTER TABLE ONLY XXXX_score_rollback ADD CONSTRAINT "NoneXXXX_score_rollback_XXXX_score_rollback_id" PRIMARY KEY (XXXX_score_rollback_id);
CREATE INDEX "NoneXXXX_score_rollback_uuid" ON XXXX_score_rollback USING btree(uuid);
CREATE INDEX "NoneXXXX_score_rollback_group_id" ON XXXX_score_rollback USING btree(group_id);
CREATE INDEX "NoneXXXX_score_rollback_rollback_time" ON XXXX_score_rollback USING btree(rollback_time);

CREATE TABLE XXXX_score_rollbacks (
    XXXX_score_rollback_id integer NOT NULL,
    XXXX_score_user_history_id integer NOT NULL
);

CREATE INDEX "NoneXXXX_score_rollbacks_XXXX_score_rollback_id" ON XXXX_score_rollbacks USING btree(XXXX_score_rollback_id);

CREATE SEQUENCE XXXX_score_unrollback_XXXX_score_unrollback_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE XXXX_score_unrollback (
    XXXX_score_unrollback_id integer DEFAULT nextval('XXXX_score_unrollback_XXXX_score_unrollback_id_seq'::regclass) NOT NULL,
    XXXX_score_rollback_id integer NOT NULL,
    uuid character varying(36) NOT NULL,
    unrollback_time timestamp with time zone NOT NULL
);

ALTER TABLE ONLY XXXX_score_unrollback ADD CONSTRAINT "NoneXXXX_score_unrollback_XXXX_score_unrollback_id" PRIMARY KEY (XXXX_score_unrollback_id);
CREATE INDEX "NoneXXXX_score_unrollback_uuid" ON XXXX_score_unrollback USING btree(uuid);
CREATE INDEX "NoneXXXX_score_unrollback_XXXX_score_rollback_id" ON XXXX_score_unrollback USING btree(XXXX_score_rollback_id);
CREATE INDEX "NoneXXXX_score_unrollback_unrollback_time" ON XXXX_score_unrollback USING btree(unrollback_time);