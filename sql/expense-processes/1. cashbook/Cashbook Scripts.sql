DROP TABLE IF EXISTS cashbooks;

-- cashbooks
CREATE TABLE "cashbooks"(
    "cashbook_id" bigserial NOT NULL,
    "cashbook_name" VARCHAR(255) NOT NULL,
    "user_id" BIGINT NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "cashbooks" ADD PRIMARY KEY("cashbook_id");
ALTER TABLE
    "cashbooks" ADD CONSTRAINT "cashbooks_cashbook_name_unique" UNIQUE("cashbook_name","user_id");
ALTER TABLE
    "cashbooks" ADD CONSTRAINT "cashbooks_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user_mst"("user_id");