package ace.fingerprinting.rest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ace.fingerprinting.db.FpInfoConnectionWrapper;
import ace.fingerprinting.matching.Matcher;
import ace.fingerprinting.model.FpInfo;

@RestController
public class IterableDDLMatcher {
    public static class DDLRequest {
        public Map<String, String> deviceInfo;
    }

    public static class DDLResponse {
        public boolean isMatch = false;
        public String destinationUrl;
        public String campaignId;
        public String templateId;
        public String messageId;
    }

    @PostMapping("/ddl/match")
    public DDLResponse match(HttpServletRequest request, @RequestBody DDLRequest ddlRequest) throws Throwable {
        DDLResponse ddlResponse = new DDLResponse();

        String ipAddress = request.getRemoteAddr();
        if (ipAddress == null) {
            return ddlResponse;
        }
        if (ddlRequest.deviceInfo == null){
            return ddlResponse;
        }


        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            List<FpInfo> possibleMatches = connectionWrapper.findAllByIp(ipAddress);
            Optional<FpInfo> possibleFound = possibleMatches
                    .stream()
                    .filter((possibleMatch) -> {
                        Map<String, String> rightMap = getBrowserFpMap(possibleMatch);
                        return rightMap != null && Matcher.match(ddlRequest.deviceInfo, rightMap);
                    })
                    .findFirst();
            if (possibleFound.isPresent()) {
                FpInfo found = possibleFound.get();
                ddlResponse.isMatch = true;
                ddlResponse.destinationUrl = found.getDestinationUrl();
                ddlResponse.campaignId = found.getCampaignId();
                ddlResponse.templateId = found.getTemplateId();
                ddlResponse.messageId = found.getMessageId();

                // Delete it from db
                connectionWrapper.delete(found);
            }

            connectionWrapper.commit();
        }

        return ddlResponse;
    }

    private static Map<String, String> getBrowserFpMap(FpInfo fpInfo) {
        try {
            String browserFp = fpInfo.getBrowserFp();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(browserFp, new TypeReference<Map<String, String>>(){});
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

}
