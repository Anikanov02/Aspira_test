package org.aspire.test.controller;

import lombok.RequiredArgsConstructor;
import org.aspire.test.domain.leon.LeonBetsTopLeaguesParserResult;
import org.aspire.test.service.LeonBetsTopLeaguesParser;
import org.aspire.test.shell.ShellHelper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class ParseController {
    private final ShellHelper shell;
    private final LeonBetsTopLeaguesParser parser;

    @ShellMethod(key = "parse-lb-tl", value = "Parse LeonBets top-leagues data")
    public void parse() {
        final LeonBetsTopLeaguesParserResult result;
        try {
            result = parser.parse();
            shell.println(result.toString());
        } catch (Exception e) {
            shell.error(e.getMessage());
        }
    }
}
