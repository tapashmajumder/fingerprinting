package ace.fingerprinting.model;

public class BrowserResponse {
    private String id;
    private BrowserFp browserFp;

    public BrowserResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BrowserFp getBrowserFp() {
        return browserFp;
    }

    public void setBrowserFp(BrowserFp browserFp) {
        this.browserFp = browserFp;
    }
}
