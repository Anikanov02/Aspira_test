package org.aspire.test.shell;

import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

@RequiredArgsConstructor
public class ShellHelper {
    private static final PromptColor SUCCESS = PromptColor.GREEN;
    private static final PromptColor WARNING = PromptColor.YELLOW;
    private static final PromptColor INFO = PromptColor.CYAN;
    private static final PromptColor ERROR = PromptColor.RED;
    private final Terminal terminal;

    public static String getColored(String message, PromptColor color) {
        final AttributedStyle foreground = AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle());
        return (new AttributedStringBuilder()).append(message, foreground).toAnsi();
    }

    public void println(String message) {
        print(message, INFO, true);
    }

    public void print(String message) {
        print(message, null, false);
    }

    public void println(String message, PromptColor color) {
        print(message, color, true);
    }

    public void warning(String message) {
        print(message, WARNING, true);
    }

    public void error(String message) {
        print(message, ERROR, true);
    }

    private void print(String message, PromptColor color, boolean newLine) {
        if(message == null) return;
        String toPrint = message;
        if (color != null) {
            toPrint = getColored(message, color);
        }
        if(newLine) {
            terminal.writer().println(toPrint);
        } else {
            terminal.writer().print(toPrint);
        }
        terminal.flush();
    }
}
