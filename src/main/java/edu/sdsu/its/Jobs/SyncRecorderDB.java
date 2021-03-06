package edu.sdsu.its.Jobs;

import edu.sdsu.its.API.Models.Recorder;
import edu.sdsu.its.DB;
import edu.sdsu.its.Hooks.Hook;
import edu.sdsu.its.Mediasite.Recorders;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.quartz.*;

import java.io.IOException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Tom Paulus
 * Created on 5/10/17.
 */
@NoArgsConstructor
@Log4j
public class SyncRecorderDB implements Job {
    public static final String JOB_GROUP = "list_update";
    public static final String TRIGGER_NAME = "SyncRecorderListTrigger";
    public static final String JOB_NAME = "SyncRecorderList";

    /**
     * Schedule the Sync Job
     *
     * @param scheduler         {@link Scheduler} Quartz Scheduler Instance
     * @param intervalInMinutes How often the job should run in Minutes
     * @throws SchedulerException Something went wrong scheduling the job
     */
    public static void schedule(Scheduler scheduler, int intervalInMinutes) throws SchedulerException {
        JobDetail job = newJob(SyncRecorderDB.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
                .build();

        // Trigger the job to run now, and then repeat every X Minutes
        Trigger trigger = newTrigger()
                .withIdentity(TRIGGER_NAME, JOB_GROUP)
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(intervalInMinutes)
                        .repeatForever())
                .startNow()
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting Recorder Sync Job");

        Recorder[] recorders = Recorders.getRecorders();
        if (recorders == null) {
            log.fatal("Problem retrieving recorder list from API");
            return;
        }
        log.debug(String.format("Retrieved %d recorders from MS API", recorders.length));

        log.debug("Updating Recorder DB");
        for (Recorder recorder : recorders) {
            log.debug(String.format("Inserting/Updating Recorder %s - \"%s\"", recorder.getName(), recorder.toString()));
            try {
                DB.updateRecorder(Recorder.merge(DB.getRecorder("id = '" + recorder.getId() + "'")[0], recorder));
            } catch (IndexOutOfBoundsException e) {
                log.debug("New Recorder - Cannot execute Update, Inserting new record.");
                DB.updateRecorder(recorder);
            }
        }
        log.debug("Updated Recorder DB");


        try {
            Hook.fire(Hook.RECORDER_RECORD_UPDATE, recorders);
        } catch (IOException e) {
            log.error("Problem firing Recorder DB Update Hook", e);
        }

        log.info("Finished Recorder Sync Job");
    }
}
