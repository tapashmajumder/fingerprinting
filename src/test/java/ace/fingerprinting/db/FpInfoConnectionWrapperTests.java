package ace.fingerprinting.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ace.fingerprinting.model.FpInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class FpInfoConnectionWrapperTests {

    @BeforeMethod
    void before() throws Exception {
        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            connectionWrapper.deleteAll();
            connectionWrapper.commit();
        }
    }

    @Test
    void create() throws Exception {
        final String id = UUID.randomUUID().toString().replace("-", "");
        final Date time = new Date();
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setTime(time);

        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            connectionWrapper.create(fpInfo);
            connectionWrapper.commit();

            Optional<FpInfo> found = connectionWrapper.select(id);
            assertThat(found.isPresent()).isTrue();
            assertThat(found.get().getId()).isEqualTo(id);
            assertThat(found.get().getTime()).isEqualToIgnoringMillis(time);

            connectionWrapper.delete(fpInfo);
            connectionWrapper.commit();

            Optional<FpInfo> found2 = connectionWrapper.select(id);
            assertThat(found2).isEmpty();
        }
    }

    @Test
    void update() throws Exception {
        final String id = UUID.randomUUID().toString().replace("-", "");
        final Date time = new Date();
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setTime(time);

        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            connectionWrapper.create(fpInfo);
            connectionWrapper.commit();

            Optional<FpInfo> found = connectionWrapper.select(id);
            assertThat(found.isPresent()).isTrue();
            FpInfo foundFp = found.get();
            assertThat(foundFp.getId()).isEqualTo(id);
            assertThat(foundFp.getTime()).isEqualToIgnoringMillis(time);

            foundFp.setIpAddress("1.2.3.4");
            foundFp.setCampaignId("campaignId");
            foundFp.setTemplateId("templateId");
            foundFp.setMessageId("messageId");
            foundFp.setDestinationUrl("http://somewhere.com");
            foundFp.setBrowserFp("This is a very long browser fp.");
            connectionWrapper.update(foundFp);
            connectionWrapper.commit();

            Optional<FpInfo> found2 = connectionWrapper.select(id);
            assertThat(found2).isPresent();
            FpInfo foundFp2 = found2.get();
            assertThat(foundFp2.getId()).isEqualTo(foundFp.getId());
            assertThat(foundFp2.getTime()).isEqualTo(foundFp.getTime());
            assertThat(foundFp2.getIpAddress()).isEqualTo(foundFp.getIpAddress());
            assertThat(foundFp2.getCampaignId()).isEqualTo(foundFp.getCampaignId());
            assertThat(foundFp2.getTemplateId()).isEqualTo(foundFp.getTemplateId());
            assertThat(foundFp2.getMessageId()).isEqualTo(foundFp.getMessageId());
            assertThat(foundFp2.getDestinationUrl()).isEqualTo(foundFp.getDestinationUrl());
            assertThat(foundFp2.getBrowserFp()).isEqualTo(foundFp.getBrowserFp());

            connectionWrapper.delete(found2.get());
        }
    }

    @Test
    void selectAll() throws Exception {
        try (FpInfoConnectionWrapper connectionWrapper = new FpInfoConnectionWrapper()) {
            for (int i = 0; i < 10; i++) {
                createOne(connectionWrapper);
            }
            List<FpInfo> fpInfos = connectionWrapper.selectAll();
            assertThat(fpInfos.size()).isEqualTo(10);

            connectionWrapper.deleteAll();
            connectionWrapper.commit();
        }
    }

    private static void createOne(FpInfoConnectionWrapper connectionWrapper) throws Exception {
        final String id = UUID.randomUUID().toString().replace("-", "");
        final Date time = new Date();
        FpInfo fpInfo = new FpInfo();
        fpInfo.setId(id);
        fpInfo.setTime(time);
        connectionWrapper.create(fpInfo);
        connectionWrapper.commit();
    }
}
