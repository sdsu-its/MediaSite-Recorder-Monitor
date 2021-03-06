package edu.sdsu.its.Hooks;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.sdsu.its.API.Models.Recorder;
import edu.sdsu.its.API.Models.User;
import edu.sdsu.its.DB;
import edu.sdsu.its.Notify;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Tom Paulus
 * Created on 5/2/18.
 */
@SuppressWarnings("unused")
@Log4j
public class SlackHook extends EventHook {
    private static final String ALERT_TEMPLATE_PATH = "slack_templates/recorder_in_alarm.twig";
    private static final String CLEARED_TEMPLATE_PATH = "slack_templates/recorder_alarm_cleared.twig";

    static HttpResponse<String> postSlackWebhook(final String templatePath, Recorder recorder) {
        HttpResponse<String> response = null;

        try {
            response = Unirest.post(DB.getPreference("slack.webhook_url"))
                    .body(Notify.messageFromTemplate(templatePath, new HashMap<String, Object>() {{
                        if (recorder != null) {
                            Recorder recorderRecord = DB.getRecorder("id='" + recorder.getId() + "'")[0];
                            put("recorder", recorderRecord);
                            put("generated_ts", recorderRecord.getLastSeen().getTime() / 1000);  // Divide by 1000 to get seconds, rather than millis
                        } else {
                            put("generated_ts", System.currentTimeMillis() / 1000);  // Divide by 1000 to get seconds, rather than millis
                        }
                    }}))
                    .asString();

            log.debug("Response from Slack - " + response.getBody());
        } catch (UnirestException e) {
            log.error("Could not post Webhook to Slack", e);
        } catch (IOException e) {
            log.warn("Could not make JSON message to post to Slack", e);
        }

        return response;
    }

    @Override Object onUserCreate(User user) {
        return null;
    }

    @Override Object onUserUpdate(User user) {
        return null;
    }

    @Override Object onRecorderRecordUpdate(Recorder[] recorders) {
        return null;
    }

    @Override Object onRecorderStatusUpdate(Recorder recorder) {
        return null;
    }

    @Override Object onRecorderAlarmActivate(Recorder recorder) {
        if (!Boolean.parseBoolean(DB.getPreference("slack.enable"))) {
            log.info("Skipping Slack Notification for Recorder in Alarm - Integration Disabled");
            return null;
        }

        postSlackWebhook(ALERT_TEMPLATE_PATH, recorder);

        return true;
    }

    @Override Object onRecorderAlarmClear(Recorder recorder) {
        if (!Boolean.parseBoolean(DB.getPreference("slack.enable"))) {
            log.info("Skipping Slack Notification for Recorder Alarm Cleared - Integration Disabled");
            return null;
        }

        postSlackWebhook(CLEARED_TEMPLATE_PATH, recorder);

        return null;
    }
}
