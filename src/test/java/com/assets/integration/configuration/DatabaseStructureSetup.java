package com.assets.integration.configuration;

import org.flywaydb.core.Flyway;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.core.env.Environment;

public class DatabaseStructureSetup implements MethodRule {

    private final Environment env;

    public DatabaseStructureSetup(Environment env) {
        this.env = env;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        Flyway flyway = Flyway.configure()
                .dataSource(env.getProperty("spring.datasource.url", String.class, "jdbc:h2:~/testData")
                        , env.getProperty("spring.datasource.username", String.class, "asset")
                        , env.getProperty("spring.datasource.password", String.class, "asset"))
                .schemas(env.getProperty("spring.datasource.schema", String.class, "asset"))
                .locations("classpath:/db/migration")
                .load();
        flyway.clean();
        flyway.migrate();
        return base;
    }
}
