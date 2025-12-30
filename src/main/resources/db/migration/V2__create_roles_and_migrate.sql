-- Create a proper roles table and migrate existing role data from the
-- legacy `users.role` column and the `user_roles` element-collection table.
-- This migration creates a new join table `user_role_map` referencing roles.id.
BEGIN;

-- 1) Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2) Seed roles from the legacy users.role column
INSERT INTO roles (name)
SELECT DISTINCT role FROM users WHERE role IS NOT NULL
ON CONFLICT (name) DO NOTHING;

-- 3) If the old element-collection table `user_roles` exists (stores role strings), copy distinct names
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='user_roles') THEN
        INSERT INTO roles (name)
        SELECT DISTINCT role FROM user_roles WHERE role IS NOT NULL
        ON CONFLICT (name) DO NOTHING;
    END IF;
END$$;

-- 4) Create new join table to map users -> roles by id
CREATE TABLE IF NOT EXISTS user_role_map (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

ALTER TABLE user_role_map
    ADD CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_role_map
    ADD CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;

-- 5) Populate user_role_map from users.role (legacy single column)
INSERT INTO user_role_map (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = u.role
WHERE u.role IS NOT NULL
ON CONFLICT DO NOTHING;

-- 6) If legacy element-collection table `user_roles` exists, migrate those rows too
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='user_roles') THEN
        INSERT INTO user_role_map (user_id, role_id)
        SELECT ur.user_id, r.id
        FROM user_roles ur
        JOIN roles r ON r.name = ur.role
        ON CONFLICT DO NOTHING;
    END IF;
END$$;

COMMIT;
