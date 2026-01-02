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
            log.info("DatabaseMigrationRunner: ensuring users.role constraint allows USER...");

            // 1) Drop existing constraint if exists to allow update
            jdbc.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");

            // 2) Normalize data
            int updated = jdbc.update("UPDATE users SET role = 'USER' WHERE role IS NULL OR role NOT IN ('ADMIN','AGENT','USER')");
            if (updated > 0) {
                log.info("DatabaseMigrationRunner: normalized {} rows to USER", updated);
            }

            // 3) Recreate the constraint
            jdbc.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','AGENT','USER'))");
            log.info("DatabaseMigrationRunner: users_role_check updated to allow ADMIN, AGENT, USER");

            // 4) Ensure basic roles exist in the roles table
            ensureRoleExists("ROLE_USER");
            ensureRoleExists("ROLE_AGENT");
            ensureRoleExists("ROLE_ADMIN");

        } catch (Exception ex) {
            log.error("DatabaseMigrationRunner: migration failed - {}", ex.getMessage());
        }
    }

    private void ensureRoleExists(String roleName) {
        Integer count = jdbc.queryForObject("SELECT count(*) FROM roles WHERE name = ?", Integer.class, roleName);
        if (count == null || count == 0) {
            jdbc.update("INSERT INTO roles(name) VALUES (?)", roleName);
            log.info("DatabaseMigrationRunner: inserted {}", roleName);
        }
    }
}