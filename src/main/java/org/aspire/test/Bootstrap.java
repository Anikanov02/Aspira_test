package org.aspire.test;

import lombok.RequiredArgsConstructor;
import org.aspire.test.shell.ShellHelper;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bootstrap {
    private final ShellHelper shell;

    @EventListener(ContextRefreshedEvent.class)
    public void help() {
        shell.println("To run a program pass 'parse-lb-tl' command");
    }
}
