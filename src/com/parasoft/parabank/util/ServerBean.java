package com.parasoft.parabank.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.hsqldb.ServerConfiguration;
import org.hsqldb.ServerConstants;
import org.hsqldb.persist.HsqlProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility bean for embedding HSQLDB into the ParaBank application
 */
public class ServerBean implements InitializingBean, DisposableBean {
    private static final Log log = LogFactory.getLog(ServerBean.class);

    private Properties serverProperties;
    private Server server;
    private DataSource dataSource;
    private String databasePath =
        System.getProperty("catalina.base") + "/webapps/parabank/db/db";
    private String databasePath2 =
        System.getProperty("catalina.base") + "/webapps/parabank/db/db2";

    public void setServerProperties(Properties serverProperties) {
        this.serverProperties = serverProperties;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public void afterPropertiesSet() throws Exception {
        HsqlProperties configProps = new HsqlProperties(serverProperties);

        ServerConfiguration.translateDefaultDatabaseProperty(configProps);

        server = new Server();
        server.setRestartOnShutdown(false);
        server.setNoSystemExit(true);
        server.setProperties(configProps);
        server.setDatabasePath(0, databasePath);
        server.setDatabasePath(1, databasePath2);

        log.info("HSQL Server Startup sequence initiated");

        server.start();

        String portMsg = "port " + server.getPort();
        log.info("HSQL Server listening on " + portMsg);
    }

    public void destroy() {
        log.info("HSQL Server Shutdown sequence initiated");
        if (dataSource != null) {
            Connection con = null;
            try {
                con = dataSource.getConnection();
                con.createStatement().execute("SHUTDOWN");
            } catch (SQLException e) {
                log.error("HSQL Server Shutdown failed: " + e.getMessage());
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception e) { }
            }
        } else {
            log.warn("HSQL ServerBean needs a dataSource property set to shutdown database safely.");
        }
        server.signalCloseAllServerConnections();
        int status = server.stop();
        long timeout = System.currentTimeMillis() + 5000;
        while (status != ServerConstants.SERVER_STATE_SHUTDOWN && System.currentTimeMillis() < timeout) {
            try {
                Thread.sleep(100);
                status = server.getState();
            } catch (InterruptedException e) {
                log.error("Error while shutting down HSQL Server: " + e.getMessage());
                break;
            }
        }
        if (status != ServerConstants.SERVER_STATE_SHUTDOWN) {
            log.warn("HSQL Server failed to shutdown properly.");
        } else {
            log.info("HSQL Server Shutdown completed");
        }
        server = null;
    }
}
