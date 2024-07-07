DROP TABLE IF EXISTS categories;

-- categories
CREATE TABLE "categories"(
    "category_id" bigserial NOT NULL,
    "category_name" VARCHAR(255) NOT NULL,
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
    "categories" ADD PRIMARY KEY("category_id");

CREATE UNIQUE INDEX categories_category_name_cashbook_id_unique
ON categories (category_name, cashbook_id)
WHERE active_flag = 1;