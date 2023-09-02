package org.aspire.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.aspire.test.config.AppProperties;
import org.aspire.test.domain.leon.LeonBetsTopLeaguesParserResult;
import org.aspire.test.domain.leon.SportsElements;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LeonBetsTopLeaguesParser extends BaseParser {
    private final AppProperties properties;
    private static final String MATCH_DATA_URL_WITH_PLACEHOLDER = "https://leonbets.com/api-2/betline/event/all?ctag=en-US&eventId=%s&flags=reg,urlv2,mm2,rrc,nodup,smg,outv2";
    private static final String LEAGUE_MATCHES_PATTERN_WITH_PLACEHOLDERS = "https://leonbets.com/api-2/betline/changes/all?ctag=en-US&vtag=&league_id=%s&hideClosed=true&flags=reg,urlv2,mm2,rrc,nodup";
    private static final String FETCH_SPORTS_DATA_URL = "https://leonbets.com/api-2/betline/sports?ctag=en-US&flags=urlv2";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");

    public LeonBetsTopLeaguesParserResult parse() {
        final ForkJoinPool pool = new ForkJoinPool(3);
        final LeonBetsTopLeaguesParserResult result = new LeonBetsTopLeaguesParserResult();
        final JsonNode sports = sendRequest(FETCH_SPORTS_DATA_URL);
        final List<ForkJoinTask<LeonBetsTopLeaguesParserResult.Sport>> tasks = StreamSupport.stream(sports.spliterator(), false)
                .filter(sport -> {
                    if (properties.getLeonBets().getSports().contains(SportsElements.SPORT_ALL)) {
                        return true;
                    } else {
                        return properties.getLeonBets().getSports().stream().anyMatch(acceptable ->
                                acceptable.getName().equalsIgnoreCase(sport.get("name").asText())
                                        || acceptable.getName().equalsIgnoreCase(sport.get("family").asText()));
                    }
                })
                .map(sport -> pool.submit(new RecursiveTask<LeonBetsTopLeaguesParserResult.Sport>() {
                    @Override
                    protected LeonBetsTopLeaguesParserResult.Sport compute() {
                        return parseSports(sport);
                    }
                })).toList();
        final List<LeonBetsTopLeaguesParserResult.Sport> parsed = tasks.stream().map(ForkJoinTask::join).toList();
        result.setSports(parsed);
        return result;
    }

    private LeonBetsTopLeaguesParserResult.Sport parseSports(JsonNode sportNode) {
        final LeonBetsTopLeaguesParserResult.Sport sport = new LeonBetsTopLeaguesParserResult.Sport();
        sport.setName(sportNode.get("name").asText());
        final List<LeonBetsTopLeaguesParserResult.TopLeague> topLeagues = StreamSupport.stream(sportNode.get("regions").spliterator(), false).flatMap(reg -> {
            final JsonNode leagues = reg.get("leagues");
            return StreamSupport.stream(leagues.spliterator(), false)
                    .filter(league -> league.get("top").asBoolean())
                    .map(league -> parseTopLeague(reg.get("name").asText(), league));
        }).toList();
        sport.setTopLeagues(topLeagues);
        return sport;
    }

    private LeonBetsTopLeaguesParserResult.TopLeague parseTopLeague(String country, JsonNode league) {
        final LeonBetsTopLeaguesParserResult.TopLeague topLeague = new LeonBetsTopLeaguesParserResult.TopLeague();
        final String id = league.get("id").asText();
        topLeague.setName(String.format("%s - %s", country, league.get("name").asText()));
        final String url = String.format(LEAGUE_MATCHES_PATTERN_WITH_PLACEHOLDERS, id);
        final JsonNode matchesArray = sendRequest(url).get("data");
        final List<LeonBetsTopLeaguesParserResult.Match> matches = StreamSupport.stream(matchesArray.spliterator(), false).limit(properties.getLeonBets().getPositionsToCheck())
                .map(match -> match.get("id").asText())
                .map(matchId -> sendRequest(String.format(MATCH_DATA_URL_WITH_PLACEHOLDER, matchId)))
                .map(this::parseMatch).toList();
        topLeague.setMatches(matches);
        return topLeague;
    }

    private LeonBetsTopLeaguesParserResult.Match parseMatch(JsonNode matchNode) {
        final LeonBetsTopLeaguesParserResult.Match match = new LeonBetsTopLeaguesParserResult.Match();
        match.setId(matchNode.get("id").asText());
        match.setName(matchNode.get("name").asText());
        match.setTime(Instant.ofEpochMilli(matchNode.get("kickoff").asLong()).atZone(ZoneId.of("UTC")).format(formatter));

        final Map<String, List<JsonNode>> groupedByType = StreamSupport.stream(matchNode.get("markets").spliterator(), false)
                .collect(Collectors.groupingBy(node -> node.get("name").asText()));
        final List<LeonBetsTopLeaguesParserResult.Market> markets = groupedByType.keySet().stream().map(marketName -> mergeMarkets(marketName, groupedByType.get(marketName)))
                .toList();
        match.setMarkets(markets);
        return match;
    }

    private LeonBetsTopLeaguesParserResult.Market mergeMarkets(String marketName, List<JsonNode> markets) {
        final LeonBetsTopLeaguesParserResult.Market market = new LeonBetsTopLeaguesParserResult.Market();
        market.setName(marketName);
        final List<LeonBetsTopLeaguesParserResult.Runner> coefficients = markets.stream()
                .flatMap(row -> StreamSupport.stream(row.get("runners").spliterator(), false))
                .map(this::parseCoefficients).toList();
        market.setCoefficients(coefficients);
        return market;
    }

    private LeonBetsTopLeaguesParserResult.Runner parseCoefficients(JsonNode runner) {
        return new LeonBetsTopLeaguesParserResult.Runner(runner.get("name").asText(), runner.get("priceStr").asText(), runner.get("id").asText());
    }

    @Override
    protected List<Header> headers() {
        return List.of(new BasicHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"),
                new BasicHeader("Content-Encoding", "gzip"),
                new BasicHeader("Content-Type", "application/json"));
    }
}