DROP TABLE IF EXISTS attachment_operation_txn;
DROP TABLE IF EXISTS attachment_file_mpg;
DROP TABLE IF EXISTS attachment_mst;

-- attachment_mst
CREATE TABLE "attachment_mst"(
    "attachment_id" bigserial NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "attachment_mst" ADD PRIMARY KEY("attachment_id");

-- attachment_file_mpg
CREATE TABLE "attachment_file_mpg"(
    "attachment_file_id" bigserial NOT NULL,
    "attachment_id" BIGINT NOT NULL,
    "file_name" VARCHAR(255) NOT NULL,
    "file_desc" VARCHAR(500) NOT NULL,
    "file_size_in_bytes" BIGINT NOT NULL,
    "content_type" VARCHAR(255) NOT NULL,
    "file_data" bytea NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "attachment_file_mpg" ADD PRIMARY KEY("attachment_file_id");
ALTER TABLE
    "attachment_file_mpg" ADD CONSTRAINT "attachment_file_mpg_attachment_id_foreign" FOREIGN KEY("attachment_id") REFERENCES "attachment_mst"("attachment_id");

-- attachment_operation_txn
CREATE TABLE "attachment_operation_txn"(
    "operation_id" bigserial NOT NULL,
    "session_id" UUID NOT NULL,
    "attachment_file_id" BIGINT NOT NULL,
    "operation_type" VARCHAR(255) NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "attachment_operation_txn" ADD PRIMARY KEY("operation_id");
ALTER TABLE
    "attachment_operation_txn" ADD CONSTRAINT "attachment_operation_txn_attachment_file_id" FOREIGN KEY("attachment_file_id") REFERENCES "attachment_file_mpg"("attachment_file_id");