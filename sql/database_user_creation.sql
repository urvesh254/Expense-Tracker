-- Create Database
CREATE DATABASE expense-tracker;

USE expense-tracker;

-- 1. Create a new role with login capability
CREATE ROLE expense_tracker WITH LOGIN PASSWORD 'J1$6SufzjH1@';

-- 2. Grant the new role access to all existing databases
DO
$$
DECLARE
    db_name text;
BEGIN
    FOR db_name IN (SELECT datname FROM pg_database WHERE datistemplate = false) LOOP
        EXECUTE format('GRANT CONNECT ON DATABASE %I TO expense_tracker;', db_name);
        EXECUTE format('GRANT USAGE ON SCHEMA public TO expense_tracker;');
        EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO expense_tracker;');
        EXECUTE format('ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO expense_tracker;');
    END LOOP;
END
$$;

-- 3. Configure default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO expense_tracker;
