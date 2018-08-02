package ace.fingerprinting.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ace.fingerprinting.db.FpInfoConnectionWrapper;
import ace.fingerprinting.model.FpInfo;

@WebServlet(name = "IterableLinkHandlerServlet", urlPatterns = {"/a/*"}, loadOnStartup = 1)
public class IterableLinkHandlerServlet extends HttpServlet {
    private static final String appStoreUrl = "https://itunes.apple.com/us/app/id942371713?mt=8";
    private static final String destinationUrl = "https://majumder.me/coffee/mocha";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String id = UUID.randomUUID().toString().replace("-", "");

        final String ipAddress = request.getRemoteAddr();
        try {
            createFpInfo(id, ipAddress);
        } catch (SQLException e) {
            //TODO: handle properly
            e.printStackTrace();
        }

        request.setAttribute("id", id);
        request.setAttribute("app-store-url", appStoreUrl);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private static void createFpInfo(final String id, String ipAddress) throws SQLException, IOException {
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setIpAddress(ipAddress);
        fpInfo.setTime(new Date());
        fpInfo.setDestinationUrl(destinationUrl);
        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            connectionWrapper.create(fpInfo);
            connectionWrapper.commit();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doPost(request, response);
    }
}