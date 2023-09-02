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
            long time = System.currentTimeMillis();
            shell.println("Started counting time");
            result = parser.parse();
            shell.println(result.toString());
            shell.println(String.format("executed in %d seconds", (System.currentTimeMillis() - time) / 1000));
        } catch (Exception e) {
            shell.error(e.getMessage());
        }
    }
}
