package edu.sdsu.its.Routes;

import edu.sdsu.its.DB;
import org.jtwig.web.servlet.JtwigRenderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static edu.sdsu.its.Routes.Route.addMeta;
import static edu.sdsu.its.Routes.Route.checkAuth;
import static edu.sdsu.its.Routes.Route.setUserData;

/**
 * @author Tom Paulus
 *         Created on 5/28/17.
 */
public class ManageRates extends HttpServlet {
    private final JtwigRenderer renderer = JtwigRenderer.defaultRenderer();
    private static final String TEMPLATE_PATH = "/WEB-INF/templates/manage-rates.twig";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAuth(request, response)) return;
        addMeta(request);
        setUserData(request);

        request.setAttribute("list_sync", DB.getPreference("sync_db.enable"));
        request.setAttribute("list_frequency", DB.getPreference("sync_db.frequency"));
        request.setAttribute("status_sync", DB.getPreference("sync_recorder.enable"));
        request.setAttribute("status_frequency", DB.getPreference("sync_recorder.frequency"));
        request.setAttribute("status_retry_count", DB.getPreference("sync_recorder.retry_count"));

        renderer.dispatcherFor(TEMPLATE_PATH)
                .render(request, response);
    }
}
