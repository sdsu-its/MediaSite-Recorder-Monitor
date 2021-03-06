package edu.sdsu.its.Mediasite;

import edu.sdsu.its.API.Models.Recorder;
import edu.sdsu.its.DB;
import lombok.extern.log4j.Log4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Tom Paulus
 * Created on 5/5/17.
 */
@Log4j
public class TestRecorders {
    @Before
    public void setUp() throws Exception {
        DB.setup();
    }

    @Test
    public void testGetAllRecorders() throws Exception {
        Recorder[] recorders = Recorders.getRecorders();
        assertNotNull(recorders);
        for (Recorder recorder : recorders) {
            log.debug(recorder.toString());
            assertNotNull(recorder.getId());
            assertNotNull(recorder.getName());
            // recorder.Description can be null
            assertNotNull(recorder.getSerialNumber());
            assertNotNull(recorder.getVersion());
            assertNotNull(recorder.getLastVersionUpdateDate());
            assertNotNull(recorder.getPhysicalAddress());
            // recorder.getImageVersion() can be null
        }
    }

    @After
    public void tearDown() throws Exception {
        DB.shutdown();
    }
}
