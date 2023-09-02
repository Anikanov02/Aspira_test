package org.aspire.test.domain.leon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeonBetsTopLeaguesParserResult {
    private List<Sport> sports;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sport {
        private String name;
        private List<TopLeague> topLeagues;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopLeague {
        private String name;
        private List<Match> matches;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Match {
        private String name;
        private String id;
        private List<Market> markets;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Market {
        private String entryName;
        private List<Pair<String, String>> coefficients;
    }
}
