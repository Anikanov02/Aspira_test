package org.aspire.test.config;

import lombok.Data;
import org.aspire.test.domain.leon.SportsElements;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties("parser")
public class AppProperties {
    public LeonBets leonBets;

    @Data
    public static class LeonBets {
        private Integer positionsToCheck = 0;
        private String baseUrl;
        private List<SportsElements> sports;

        public void setSports(List<String> shortNames) {
            sports = shortNames.stream().map(SportsElements::fromShortName).toList();
        }
    }
}
