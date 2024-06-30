DROP TABLE IF EXISTS email_audit_trail;

CREATE TABLE "email_audit_trail"(
    "email_audit_id" bigserial NOT NULL,
    "request_data" VARCHAR(2000) NOT NULL,
    "response_data" VARCHAR(2000) NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "email_audit_trail" ADD PRIMARY KEY("email_audit_id");