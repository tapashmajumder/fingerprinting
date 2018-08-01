package ace.fingerprinting.db;

import java.util.Date;
import java.util.Optional;

import org.testng.annotations.Test;

import ace.fingerprinting.model.FpInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class DBTests {
    @Test
    public void create() throws Exception {
        final String id = "id4";
        final Date time = new Date();
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setTime(time);

        DB db = new DB();
        try {
            db.open();

            db.create(fpInfo);
            db.commit();

            Optional<FpInfo> found = db.select(id);
            assertThat(found.isPresent()).isTrue();
            assertThat(found.get().getId()).isEqualTo(id);
            assertThat(found.get().getTime()).isEqualToIgnoringMillis(time);

            db.delete(fpInfo);
            db.commit();

            Optional<FpInfo> found2 = db.select(id);
            db.commit();
            assertThat(found2).isEmpty();

        } catch (Throwable t) {
            t.printStackTrace();
            db.close();
        }
    }
}
