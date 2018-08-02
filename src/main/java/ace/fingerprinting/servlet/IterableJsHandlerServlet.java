package ace.fingerprinting.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ace.fingerprinting.db.FpInfoConnectionWrapper;
import ace.fingerprinting.model.BrowserFp;
import ace.fingerprinting.model.BrowserResponse;
import ace.fingerprinting.model.FpInfo;

@WebServlet(name = "IterableJsHandlerServlet", urlPatterns = {"/js/*"}, loadOnStartup = 1)
public class IterableJsHandlerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        super.doGet(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            BrowserResponse browserResponse = parse(request.getInputStream());
            try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
                Optional<FpInfo> fpInfoOptional = connectionWrapper.select(browserResponse.id);
                if (!fpInfoOptional.isPresent()) {
                    // handler error and bail
                    return;
                }

                FpInfo fpInfo = fpInfoOptional.get();
                fpInfo.setBrowserFp(serialize(browserFpFromBrowserResponse(browserResponse)));
                connectionWrapper.update(fpInfo);
                connectionWrapper.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BrowserResponse parse(InputStream inputStream) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, BrowserResponse.class);
    }

    private static BrowserFp browserFpFromBrowserResponse(BrowserResponse browserResponse) {
        BrowserFp browserFp = new BrowserFp();
        browserFp.userAgent = browserResponse.userAgent;
        browserFp.navigatorPlatform = browserResponse.navigatorPlatform;
        browserFp.screenWidth = browserResponse.screenWidth;
        browserFp.screenHeight = browserResponse.screenHeight;
        browserFp.scale = browserResponse.scale;
        browserFp.navigatorLanguage = browserResponse.navigatorLanguage;
        browserFp.timezoneOffset = browserResponse.timezoneOffset;
        return browserFp;
    }

    private static String serialize(BrowserFp browserFp) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(browserFp);
    }
}