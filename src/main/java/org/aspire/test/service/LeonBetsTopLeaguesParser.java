package org.aspire.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.aspire.test.config.AppProperties;
import org.aspire.test.domain.leon.ElementsClassNames;
import org.aspire.test.domain.leon.LeonBetsTopLeaguesParserResult;
import org.aspire.test.domain.leon.SportsElements;
import org.aspire.test.exception.UnexpectedStructureException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.aspire.test.domain.leon.ElementsClassNames.*;

@Service
@RequiredArgsConstructor
public class LeonBetsTopLeaguesParser extends BaseParser {
    private final AppProperties properties;
    private static final String ALL_MARKETS_URL_WITH_PLACEHOLDER = "https://leonbets.com/api-2/betline/event/all?ctag=ru-RU&eventId=%s&flags=reg,urlv2,mm2,rrc,nodup,smg,outv2";
    private static final Pattern ID_PATTERN = Pattern.compile("\\d{16}");
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public LeonBetsTopLeaguesParserResult parse() throws Exception {
        final LeonBetsTopLeaguesParserResult result = new LeonBetsTopLeaguesParserResult();

        final Document base = load(properties.getLeonbetsBaseUrl());
        final Element allTopLeaguesButton = base.getElementsByClass(BUTTON_ALL_TOP_LEAGUES.getClassName()).first();
        if (Objects.isNull(allTopLeaguesButton)) {
            throw new UnexpectedStructureException();
        } else {
            final String relativeUrl = allTopLeaguesButton.attr("href");
            final String absoluteUrlAllTopLeagues = properties.getLeonbetsBaseUrl() + relativeUrl;
            final Document allLeagues = load(absoluteUrlAllTopLeagues);
            final Element swiperWrapper = allLeagues.getElementsByClass(SWIPER_WRAPPER.getClassName()).first();
            if (Objects.isNull(swiperWrapper)) {
                throw new UnexpectedStructureException();
            } else {
                final List<Element> sportsButtons = swiperWrapper.getElementsByClass(SportsElements.SPORT_ALL.getClassName())
                        .stream()
                        .filter(element -> properties.getSportsToParse().stream().anyMatch(sport -> element.getElementsByClass(sport.getClassName()).first() != null))
                        .flatMap(element -> allLeagues.getElementsByClass(SPORT_ELEMENT_ITEM.getClassName()).stream())
                        .filter(Objects::nonNull).toList();
                result.setSports(sportsButtons.stream().map(this::parseSports).toList());
            }
        }
        return result;
    }

    private LeonBetsTopLeaguesParserResult.Sport parseSports(Element sportElement) {
        final LeonBetsTopLeaguesParserResult.Sport sport = new LeonBetsTopLeaguesParserResult.Sport();

        final Document sportPage = load(properties.getLeonbetsBaseUrl() + sportElement.attr("href"));
        final Element topLeaguesList = sportPage.getElementsByClass(TOP_LEAGUES_LIST.getClassName()).first();
        if (Objects.isNull(topLeaguesList)) {
            throw new UnexpectedStructureException();
        } else {
            final Elements topLeagues = topLeaguesList.children();
            final List<Element> urls = topLeagues
                    .stream().map(topLeague -> topLeague.selectFirst("a"))
                    .filter(Objects::nonNull).toList();
            urls.stream().map(this::parseTopLeague);
        }

        return sport;
    }

    private LeonBetsTopLeaguesParserResult.TopLeague parseTopLeague(Element league) {
        final LeonBetsTopLeaguesParserResult.TopLeague topLeague = new LeonBetsTopLeaguesParserResult.TopLeague();

        final Element firstSpan = league.selectFirst("div > span");
        if (Objects.isNull(firstSpan)) {
            throw new UnexpectedStructureException();
        } else {
            topLeague.setName(firstSpan.text());
        }

        final String relativeUrl = league.attr("href");
        final Document topLeaguePage = load(properties.getLeonbetsBaseUrl() + relativeUrl);
        final Elements events = topLeaguePage.getElementsByClass(SPORT_EVENT_ITEM.getClassName());
        final List<LeonBetsTopLeaguesParserResult.Match> matches = events.subList(0, Math.min(properties.getPositionsToCheck(), events.size())).stream()
                .map(this::parseMatch).toList();
        topLeague.setMatches(matches);
        return topLeague;
    }

    private LeonBetsTopLeaguesParserResult.Match parseMatch(Element matchElement) {
        final LeonBetsTopLeaguesParserResult.Match match = new LeonBetsTopLeaguesParserResult.Match();
        final String relativeUrl = matchElement.selectFirst("div > a").attr("href");
        final String id = getId(relativeUrl);
        final String matchName = matchElement.getElementsByClass(ElementsClassNames.EVENT_COMPETITOR_NAME.getClassName()).stream()
                .map(Element::text).collect(Collectors.joining(" - "));
        match.setName(matchName);
        match.setId(id);
        final JsonNode allMarketsResponse = sendRequest(String.format(ALL_MARKETS_URL_WITH_PLACEHOLDER, id));
        match.setMarkets(parseMarkets(allMarketsResponse));
        return match;
    }

    private String getId(String url) {
        final Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new RuntimeException(String.format("cant get id from url: %s", url));
        }
    }

    private List<LeonBetsTopLeaguesParserResult.Market> parseMarkets(JsonNode marketsArray) {
        final List<LeonBetsTopLeaguesParserResult.Market> markets = new ArrayList<>();
        StreamSupport.stream(marketsArray.spliterator(), false).forEach(market -> {
            final LeonBetsTopLeaguesParserResult.Market parsed = new LeonBetsTopLeaguesParserResult.Market();
            parsed.setEntryName(market.get("name").asText());
            final JsonNode coefficientsArray = market.get("runners");
            final List<Pair<String, String>> coefficients = new ArrayList<>();
            StreamSupport.stream(coefficientsArray.spliterator(), false).forEach(runner -> {
                final String name = runner.get("name").asText();
                final String price = runner.get("priceStr").asText();
                coefficients.add(Pair.of(name, price));
            });
            parsed.setCoefficients(coefficients);
            markets.add(parsed);
        });
        return markets;
    }
}