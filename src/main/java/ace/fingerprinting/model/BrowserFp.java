package ace.fingerprinting.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BrowserFp {
    public String userAgent;

    public String navigatorPlatform;

    public String screenWidth;

    public String screenHeight;

    public String scale;

    public String navigatorLanguage;

    public String timezoneOffset;
}
