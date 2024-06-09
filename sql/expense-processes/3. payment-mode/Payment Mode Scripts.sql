DROP TABLE IF EXISTS payment_modes;

-- payment_modes
CREATE TABLE "payment_modes"(
    "payment_mode_id" bigserial NOT NULL,
    "payment_mode_name" VARCHAR(255) NOT NULL,
    "cashbook_id" BIGINT NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "payment_modes" ADD PRIMARY KEY("payment_mode_id");

CREATE UNIQUE INDEX "payment_modes_payment_mode_name_cashbook_id_unique"
ON "payment_modes" ("payment_mode_name", "cashbook_id")
WHERE active_flag = 1;