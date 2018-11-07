package com.assets.integration.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.assets.api")
@ComponentScan("com.assets.transfer")
@ComponentScan("com.assets.model")
public class PersistanceTestConfiguration {

    @Autowired
    Environment env;

    @Bean
    public DatabaseStructureSetup getDatabaseStructureSetup() {
        return new DatabaseStructureSetup(env);
    }

    @Bean
    public DataSource getDataSource() {
        HikariDataSource ds = (HikariDataSource) DataSourceBuilder
                .create()
                .url(env.getProperty("spring.datasource.url", String.class, "jdbc:h2:~/testData"))
                .username(env.getProperty("spring.datasource.username", String.class, "asset"))
                .password(env.getProperty("spring.datasource.password", String.class, "asset"))
                .driverClassName("org.h2.Driver")
                .build();
        ds.setSchema(env.getProperty("spring.datasource.schema", String.class, "asset"));
        return ds;
    }

    @Bean
    public DataSourceTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean
    public DSLContext getMockContext() {
        DefaultExecuteListenerProvider listenerProvider = new DefaultExecuteListenerProvider(new JooqExceptionTranslator());
        DefaultConfiguration config = new DefaultConfiguration();
        config.setSQLDialect(SQLDialect.valueOf(env.getProperty("jooq.sql.dialect")));
        config.setConnectionProvider(new DataSourceConnectionProvider(getDataSource()));
        config.setExecuteListenerProvider(listenerProvider);
        return new DefaultDSLContext(config);
    }
}
