package edu.sdsu.its;

import lombok.extern.log4j.Log4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Paulus
 * Created on 5/5/17.
 */
@Log4j
public class TestDB {
    @Before
    public void setUp() throws Exception {
        DB.setup();
    }

    /**
     * Check if the KeyServer has access to the correct credentials
     */
    @Test
    public void checkParams() {
        final String db_url = Vault.getParam("db-url");
        log.debug("Vault.db-url = " + db_url);
        assertTrue("URL is Empty", db_url != null && db_url.length() > 0);
        assertTrue("Invalid URL", db_url.startsWith("jdbc:mysql://"));

        final String db_user = Vault.getParam("db-user");
        log.debug("Vault.db-user = " + db_user);
        assertTrue("Username is Empty", db_user != null && db_user.length() > 0);

        final String db_password = Vault.getParam("db-password");
        log.debug("Vault.db-password = " + db_password);
        assertTrue("Password is Empty", db_password != null && db_password.length() > 0);
    }


    /**
     * Test DB Connection
     */
    @Test
    public void connect() {
        log.debug("Attempting to connect to the DB Server");
        assertNotNull(DB.getSessionFactory());
        log.info("DB Connection established");
        assertTrue(DB.getSessionFactory().isOpen());
    }

    @After
    public void tearDown() throws Exception {
        DB.shutdown();
    }
}
