package de.shiirroo.tps.webserver.adapter.nitrado;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import de.shiirroo.tps.history.TpsHistory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nitrado.hytale.plugins.webserver.WebServerPlugin;
import net.nitrado.hytale.plugins.webserver.servlets.TemplateServlet;

import java.io.IOException;

public class TpsServlet extends TemplateServlet {

    public TpsServlet(WebServerPlugin parentPlugin, JavaPlugin thisPlugin) {
        super(parentPlugin, thisPlugin);
    }


    /*@RequirePermissions(
            mode = RequirePermissions.Mode.ANY,
            value = {
                    "tps.web.get.history",
                    "tps.web.get.*",
                    "tps.web.*",
                    "tps.*",
                    "*"
                    }
    )*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(TpsHistory.get().asJson());
    }


}