DROP TABLE IF EXISTS user_dtl;
DROP TABLE IF EXISTS user_mst;
DROP TABLE IF EXISTS user_dtl_hst;
DROP TABLE IF EXISTS user_pwd_changed_hst;
DROP TABLE IF EXISTS blacklisted_jwt_txn;


-- user_mst
CREATE TABLE "user_mst"(
    "user_id" BIGSERIAL NOT NULL,
    "email" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "pwd_change_type" BIGINT NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "user_mst" ADD PRIMARY KEY("user_id");
ALTER TABLE
    "user_mst" ADD CONSTRAINT "user_mst_email_unique" UNIQUE("email");

-- user_dtl
CREATE TABLE "user_dtl"(
    "user_dtl_id" BIGSERIAL NOT NULL,
    "user_id" BIGINT NOT NULL,
    "full_name" VARCHAR(255) NOT NULL,
    "dob" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "profile_attachment_id" BIGINT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "user_dtl" ADD PRIMARY KEY("user_dtl_id");
ALTER TABLE
    "user_dtl" ADD CONSTRAINT "user_dtl_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user_mst"("user_id");
ALTER TABLE
    "user_dtl" ADD CONSTRAINT "user_dtl_profile_attachment_id_foreign" FOREIGN KEY("profile_attachment_id") REFERENCES "attachment_mst"("attachment_id");

-- blacklisted_jwt_txn
CREATE TABLE "blacklisted_jwt_txn"(
    "blacklisted_jwt_txn_id" BIGSERIAL PRIMARY KEY NOT NULL,
    "token" VARCHAR(500) NOT NULL,
    "valid_till" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL
);

----------------------  Triggers For History ----------------------
-- Creating Trigger for maintaining automatic history of important tables.

--------------- user_dtl history table trigger starts --------------

-- 1. History Table
CREATE TABLE user_dtl_hst AS (SELECT 0 AS hst_mode, current_timestamp AS hst_log_date, * FROM user_dtl);
ALTER TABLE user_dtl_hst ALTER COLUMN hst_log_date SET NOT NULL;
ALTER TABLE user_dtl_hst ALTER COLUMN hst_mode SET NOT NULL;

-- 2. Trigger Function
CREATE OR REPLACE
FUNCTION user_dtl_hst() RETURNS TRIGGER AS
'
    BEGIN
        IF TG_OP = ''DELETE'' THEN
            INSERT INTO user_dtl_hst SELECT -1, CURRENT_TIMESTAMP, OLD.*;
            RETURN OLD;
        ELSEIF TG_OP = ''UPDATE'' THEN
            INSERT INTO user_dtl_hst SELECT 1, CURRENT_TIMESTAMP, NEW.*;
            RETURN NEW;
        ELSEIF TG_OP = ''INSERT'' THEN
            INSERT INTO user_dtl_hst SELECT 0, CURRENT_TIMESTAMP, NEW.*;
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
'
LANGUAGE plpgsql;

-- 3. Trigger Creation
CREATE TRIGGER
user_dtl_trigger
AFTER INSERT OR UPDATE OR DELETE
ON user_dtl
FOR EACH ROW
EXECUTE FUNCTION
user_dtl_hst();

--------------- user_dtl history table trigger ends --------------



--------------- user_pwd_changed_hst history table trigger starts --------------

-- 1. History Table
CREATE TABLE user_pwd_changed_hst AS (SELECT 0 AS hst_mode, current_timestamp AS hst_log_date, user_id, PASSWORD, pwd_change_type, active_flag, created_date, created_by_ip, updated_date, updated_by_ip  FROM user_mst);
ALTER TABLE user_pwd_changed_hst ALTER COLUMN hst_log_date SET NOT NULL;
ALTER TABLE user_pwd_changed_hst ALTER COLUMN hst_mode SET NOT NULL;

-- 2. Trigger Function
CREATE OR REPLACE FUNCTION user_pwd_changed_hst() RETURNS TRIGGER AS
'
    BEGIN
  		IF TG_OP = ''DELETE'' THEN
			INSERT INTO user_pwd_changed_hst SELECT 0, CURRENT_TIMESTAMP, OLD.user_id, OLD.PASSWORD, OLD.pwd_change_type, OLD.active_flag, OLD.created_date, OLD.created_by_ip, OLD.updated_date, OLD.updated_by_ip;
            RETURN OLD;
        ELSEIF TG_OP = ''UPDATE'' AND OLD.password <> NEW.password THEN
            INSERT INTO user_pwd_changed_hst SELECT 1, CURRENT_TIMESTAMP, NEW.user_id, NEW.PASSWORD, NEW.pwd_change_type, NEW.active_flag, NEW.created_date, NEW.created_by_ip, NEW.updated_date, NEW.updated_by_ip;
            RETURN NEW;
        ELSEIF TG_OP = ''INSERT'' THEN
            INSERT INTO user_pwd_changed_hst SELECT 0, CURRENT_TIMESTAMP, NEW.user_id, NEW.PASSWORD, NEW.pwd_change_type, NEW.active_flag, NEW.created_date, NEW.created_by_ip, NEW.updated_date, NEW.updated_by_ip;
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
'
LANGUAGE plpgsql;



-- 3. Trigger Creation
CREATE TRIGGER
user_pwd_changed_hst_trigger
AFTER INSERT OR UPDATE OR DELETE
ON user_mst
FOR EACH ROW
EXECUTE FUNCTION
user_pwd_changed_hst();

--------------- user_pwd_changed_hst history table trigger ends --------------