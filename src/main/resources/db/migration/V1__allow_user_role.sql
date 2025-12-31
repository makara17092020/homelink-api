-- Migration: normalize any NULL/invalid roles to USER and update check constraint
BEGIN;

<<<<<<< HEAD
-- 1) Drop the constraint first so we can safely normalize existing values
=======
-- 1) Drop the constraint first so normalization is not blocked
>>>>>>> bdf8a9ad667eadebd25e44a19c264b9b09d401a0
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- 2) Normalize any NULL or unexpected role values to 'USER'
UPDATE users SET role = 'USER' WHERE role IS NULL OR role NOT IN ('ADMIN','AGENT','USER');

-- 3) Recreate the constraint to enforce allowed values going forward
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','AGENT','USER'));

COMMIT;
