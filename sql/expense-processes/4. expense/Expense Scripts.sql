DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS expenses_hst;

-- expenses
CREATE TABLE "expenses"(
    "expense_id" bigserial NOT NULL,
    "entry_type" VARCHAR(255) NOT NULL,
    "entry_date_time" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "amount" DECIMAL(8, 2) NOT NULL,
    "remarks" VARCHAR(2000) NULL,
    "category_id" BIGINT NOT NULL,
    "payment_mode_id" BIGINT NULL,
    "cashbook_id" BIGINT NULL,
    "attachment_id" BIGINT NULL,
    "active_flag" SMALLINT NOT NULL,
    "created_by_user_id" BIGINT NOT NULL,
    "created_date" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "created_by_ip" VARCHAR(255) NOT NULL,
    "updated_by_user_id" BIGINT NULL,
    "updated_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL,
    "updated_by_ip" VARCHAR(255) NULL
);
ALTER TABLE
    "expenses" ADD PRIMARY KEY("expense_id");
ALTER TABLE
    "expenses" ADD CONSTRAINT "expenses_category_id_foreign" FOREIGN KEY("category_id") REFERENCES "categories"("category_id");
ALTER TABLE
    "expenses" ADD CONSTRAINT "expenses_payment_mode_id_foreign" FOREIGN KEY("payment_mode_id") REFERENCES "payment_modes"("payment_mode_id");
ALTER TABLE
    "expenses" ADD CONSTRAINT "expenses_attachment_id_foreign" FOREIGN KEY("attachment_id") REFERENCES "attachment_mst"("attachment_id");
ALTER TABLE
    "expenses" ADD CONSTRAINT "expenses_cashbook_id_foreign" FOREIGN KEY("cashbook_id") REFERENCES "cashbooks"("cashbook_id");


----------------------  Triggers For History ----------------------
-- Creating Trigger for maintaining automatic history of important tables.

--------------- expenses history table trigger starts --------------

-- 1. History Table
CREATE TABLE expenses_hst AS (SELECT 0 AS hst_mode, current_timestamp AS hst_log_date, * FROM expenses);
ALTER TABLE expenses_hst ALTER COLUMN hst_mode SET NOT NULL;
ALTER TABLE expenses_hst ALTER COLUMN hst_log_date SET NOT NULL;

-- 2. Trigger Function
CREATE OR REPLACE FUNCTION expenses_hst() RETURNS TRIGGER AS
'
    BEGIN
        IF TG_OP = ''DELETE'' THEN
            INSERT INTO expenses_hst SELECT -1, CURRENT_TIMESTAMP, OLD.*;
            RETURN OLD;
        ELSEIF TG_OP = ''UPDATE'' THEN
            INSERT INTO expenses_hst SELECT 1, CURRENT_TIMESTAMP, NEW.*;
            RETURN NEW;
        ELSEIF TG_OP = ''INSERT'' THEN
            INSERT INTO expenses_hst SELECT 0, CURRENT_TIMESTAMP, NEW.*;
            RETURN NEW;
        END IF;
        RETURN NULL;
    END;
'
LANGUAGE plpgsql;

-- 3. Trigger Creation
CREATE TRIGGER
expenses_trigger
AFTER INSERT OR UPDATE OR DELETE
ON expenses
FOR EACH ROW
EXECUTE FUNCTION
expenses_hst();

--------------- expenses history table trigger ends --------------