package ace.fingerprinting.rest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ace.fingerprinting.db.FpInfoConnectionWrapper;
import ace.fingerprinting.model.BrowserFp;
import ace.fingerprinting.model.BrowserResponse;
import ace.fingerprinting.model.FpInfo;

@RestController
public class IterableLinkHandler {
    private static final String appStoreUrl = "https://itunes.apple.com/us/app/id942371713?mt=8";
    private static final String destinationUrl = "https://majumder.me/coffee/mocha";
    private static final String campaignId = "cid";
    private static final String templateId = "tid";
    private static final String messageId = "mid";

    @GetMapping("/a/*")
    public void handleDeepLink(HttpServletRequest request, HttpServletResponse response) throws Throwable {
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

    @PostMapping("/js/*")
    public void handleJs(HttpServletRequest request, HttpServletResponse response) throws Throwable {
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

    private static void createFpInfo(final String id, String ipAddress) throws SQLException, IOException {
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setIpAddress(ipAddress);
        fpInfo.setTime(new Date());
        fpInfo.setCampaignId(campaignId);
        fpInfo.setTemplateId(templateId);
        fpInfo.setMessageId(messageId);
        fpInfo.setDestinationUrl(destinationUrl);
        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            connectionWrapper.create(fpInfo);
            connectionWrapper.commit();
        }
    }

}
