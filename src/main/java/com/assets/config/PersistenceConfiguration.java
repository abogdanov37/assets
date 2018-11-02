package com.assets.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.concurrent.*;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    private DataSourceConnectionProvider connectionProvider;
    private Environment env;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PersistenceConfiguration(DataSourceConnectionProvider connectionProvider, Environment env) {
        this.connectionProvider = connectionProvider;
        this.env = env;
    }

    @Bean(name = "dsl")
    public DSLContext getDsl() {
        DefaultExecuteListenerProvider listenerProvider = new DefaultExecuteListenerProvider(new JooqExceptionTranslator ());

        DefaultConfiguration config = new DefaultConfiguration();
        config.setSQLDialect(SQLDialect.valueOf(env.getProperty("jooq.sql.dialect")));
        config.setConnectionProvider(connectionProvider);
        config.set(new ThreadPoolExecutor(env.getProperty("pool.bio.core-size", Integer.class, 2)
                , env.getProperty("pool.bio.max-size", Integer.class, 10)
                , env.getProperty("pool.bio.alive-time", Long.class, 60L)
                , TimeUnit.SECONDS
                , new LinkedBlockingQueue<>()
                , new ThreadFactoryBuilder()
                .setNamePrefix("jooq.bio.thread").setDaemon(true)
                .setUncaughtExceptionHandler((t, e) -> logger.warn(
                        "Thread %s threw exception - %s", t.getName(),
                        e.getMessage())).build()));
        config.setExecuteListenerProvider(listenerProvider);
        return new DefaultDSLContext(config);
    }

}
