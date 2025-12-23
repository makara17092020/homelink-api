-- Migration: map existing RENTER rows to USER, then update check constraint to allow USER
BEGIN;

-- Update existing rows that used legacy 'RENTER' value to 'USER'
UPDATE users SET role = 'USER' WHERE role = 'RENTER';

-- Drop existing constraint if present and recreate to allow ADMIN, AGENT, USER
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','AGENT','USER'));

COMMIT;
