CREATE TABLE XXX_money_balances (
    uuid character varying(64) NOT NULL,
    balance integer NOT NULL
);

CREATE INDEX "None_XXX_money_balances_balance" ON XXX_money_balances USING btree (balance);
CREATE UNIQUE INDEX "None_XXX_money_balances_uuid" ON XXX_money_balances USING btree (uuid);

CREATE TABLE XXX_money_history (
    to_uuid character varying(64) NOT NULL,
    from_uuid character varying(64) NOT NULL,
    amount integer NOT NULL,
    reason character varying(255) NOT NULL,
    "time" timestamp with time zone
);

CREATE INDEX "NoneXXX_money_history_from_uuid" ON XXX_money_history USING btree (from_uuid);
CREATE INDEX "NoneXXX_money_history_to_uuid" ON XXX_money_history USING btree (to_uuid);
