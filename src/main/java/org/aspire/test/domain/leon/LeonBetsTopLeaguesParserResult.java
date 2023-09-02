package org.aspire.test.domain.leon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeonBetsTopLeaguesParserResult {
    private List<Sport> sports;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sports.forEach(sport -> sb.append(sport.toString()).append("\n"));
        return sb.toString();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sport {
        private String name;
        private List<TopLeague> topLeagues;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            topLeagues.forEach(league -> sb.append(String.format("%s, %s", name, league.toString())));
            return sb.toString();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopLeague {
        private String name;
        private List<Match> matches;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("\n");
            matches.forEach(match -> sb.append(match.toString()));
            return sb.toString();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Match {
        private String name;
        private String time;
        private String id;
        private List<Market> markets;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\t%s, %s, %s\n", name, time, id));
            markets.forEach(market -> sb.append(market.toString()));
            return sb.toString();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Market {
        private String name;
        private List<Runner> coefficients;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\t\t").append(name).append("\n");
            coefficients.forEach(runner -> sb.append("\t\t\t").append(runner.toString()).append("\n"));
            return sb.toString();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Runner {
        private String left;
        private String right;
        private String id;

        @Override
        public String toString() {
            return String.format("%s, %s, %s", left, right, id);
        }
    }
}

