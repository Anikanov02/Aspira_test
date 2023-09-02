package org.aspire.test.config;

import lombok.Getter;
import org.aspire.test.domain.leon.SportsElements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class AppProperties {
    @Value(value = "${parser.leonbets.positionsToCheck:0}")
    private Integer positionsToCheck;
    @Value("${parser.leonbets.baseUrl}")
    private String leonbetsBaseUrl;
    private List<SportsElements> sportsToParse;

    @Value("${parser.leonbets.sports}")
    public void setSportsToParse(List<String> shortNames) {
        sportsToParse = shortNames.stream().map(SportsElements::fromShortName).toList();
    }
}
