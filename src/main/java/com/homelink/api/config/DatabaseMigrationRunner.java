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

            // Update existing legacy values (RENTER -> USER)
            int updated = jdbc.update("UPDATE users SET role = 'USER' WHERE role = 'RENTER'");
            if (updated > 0) {
                log.info("DatabaseMigrationRunner: updated {} rows from RENTER -> USER", updated);
            }

            // Drop existing constraint if exists and recreate allowing ADMIN, AGENT, USER
            jdbc.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            jdbc.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','AGENT','USER'))");

            log.info("DatabaseMigrationRunner: users_role_check updated to allow ADMIN, AGENT, USER");
        } catch (Exception ex) {
            log.error("DatabaseMigrationRunner: migration failed or not applicable - {}", ex.getMessage());
            log.debug("DatabaseMigrationRunner: full exception", ex);
        }
    }
}
