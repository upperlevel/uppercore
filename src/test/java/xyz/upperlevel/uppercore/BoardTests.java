package xyz.upperlevel.uppercore;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import xyz.upperlevel.uppercore.board.Board;

import java.util.HashSet;
import java.util.Set;

public class BoardTests {
    private static final String TEST_FORWARD_ENTRY = "yolo";
    private static final int FORWARD_STEPS = 5;

    private static final String TEST_BACKWARD_ENTRY = "hello_world";

    private static final Set<String> ENTRIES = new HashSet<String>() {{
        for (int i = 0; i <= FORWARD_STEPS; i++) {
            add(TEST_FORWARD_ENTRY + StringUtils.repeat("+", i));
        }

        for (int i = 0; i <= Board.MAX_TEAM_ENTRY_CHARACTERS - TEST_BACKWARD_ENTRY.length(); i++) {
            add(TEST_BACKWARD_ENTRY + StringUtils.repeat("+", i));
        }
    }};

    @Test
    public void testForwardEntryGeneration() {
        StringBuilder entry = new StringBuilder(TEST_FORWARD_ENTRY);
        Board.generateEntry(ENTRIES, entry, "+");
        assert entry.toString().equals(TEST_FORWARD_ENTRY + StringUtils.repeat("+", FORWARD_STEPS + 1));
    }

    @Test
    public void testBackwardEntryGeneration() {
        StringBuilder entry = new StringBuilder(TEST_BACKWARD_ENTRY);
        Board.generateEntry(ENTRIES, entry, "+");
        assert entry.toString().equals("hello_worl+" + StringUtils.repeat("+", Board.MAX_TEAM_ENTRY_CHARACTERS - TEST_BACKWARD_ENTRY.length()));
    }
}
