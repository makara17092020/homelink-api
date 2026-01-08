package com.homelink.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigrationRunner implements ApplicationRunner {
    private final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);
    private final JdbcTemplate jdbc;

    public DatabaseMigrationRunner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("DatabaseMigrationRunner: starting cleanup and constraint check...");

            // 1) Drop existing constraint to allow updating the list
            jdbc.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");

            // 2) Normalize data in users table (Ensure NO "ROLE_" prefix here)
            // This turns "ROLE_USER" into "USER", etc., if they exist
            jdbc.update("UPDATE users SET role = 'USER' WHERE role = 'ROLE_USER'");
            jdbc.update("UPDATE users SET role = 'AGENT' WHERE role = 'ROLE_AGENT'");
            jdbc.update("UPDATE users SET role = 'ADMIN' WHERE role = 'ROLE_ADMIN'");
            
            int updated = jdbc.update("UPDATE users SET role = 'USER' WHERE role IS NULL OR role NOT IN ('ADMIN','AGENT','USER')");
            if (updated > 0) {
                log.info("DatabaseMigrationRunner: normalized {} rows to USER", updated);
            }

            // 3) Recreate the constraint with clean names
            jdbc.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','AGENT','USER'))");
            log.info("DatabaseMigrationRunner: users_role_check updated to allow ADMIN, AGENT, USER");

            // 4) Ensure basic roles exist WITHOUT the 'ROLE_' prefix
            ensureRoleExists("USER");
            ensureRoleExists("AGENT");
            ensureRoleExists("ADMIN");

        } catch (Exception ex) {
            log.error("DatabaseMigrationRunner: migration failed - {}", ex.getMessage());
        }
    }

    private void ensureRoleExists(String roleName) {
        // First, check if the table 'roles' even exists to avoid crashing
        try {
            Integer count = jdbc.queryForObject("SELECT count(*) FROM roles WHERE name = ?", Integer.class, roleName);
            if (count == null || count == 0) {
                jdbc.update("INSERT INTO roles(name) VALUES (?)", roleName);
                log.info("DatabaseMigrationRunner: inserted {}", roleName);
            }
        } catch (Exception e) {
            log.warn("DatabaseMigrationRunner: Could not update 'roles' table. It might not exist or has a different schema.");
        }
    }
}