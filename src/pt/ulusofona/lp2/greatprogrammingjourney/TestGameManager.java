package pt.ulusofona.lp2.greatprogrammingjourney;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameManager {

    private String[][] players2Basic() {
        return new String[][]{
                {"1", "Alice", "blue"},
                {"2", "Bob", "green"}
        };
    }

    private String[][] players3WithLangs() {
        return new String[][]{
                {"1", "Alice", "Java;Python", "blue"},
                {"2", "Bob", "C;Java", "green"},
                {"5", "Carol", "COBOL", "brown"}
        };
    }

    private String[][] players4WithLangs() {
        return new String[][]{
                {"10", "Ana", "Java;Kotlin", "blue"},
                {"2", "Bruno", "C;Java", "green"},
                {"7", "Carla", "Python;Ada", "brown"},
                {"3", "Duarte", "Assembly;C", "purple"}
        };
    }

    private String[][] boardStuffOK(int worldSize) {
        return new String[][]{
                {"1", "0", "2"},               // tool id 0 at pos 2
                {"1", "4", "3"},               // tool id 4 at pos 3
                {"0", "0", "5"},               // abyss id 0 at pos 5
                {"0", "8", String.valueOf(Math.min(worldSize, 7))} // abyss id 8 at pos 7 (or last)
        };
    }

    private String[][] boardStuffInvalidType() {
        return new String[][]{
                {"2", "0", "4"} // type must be 0 or 1
        };
    }

    private String[][] boardStuffInvalidPos() {
        return new String[][]{
                {"1", "0", "0"} // pos must be 1..worldSize
        };
    }

    private String[][] boardStuffInvalidSubtype() {
        return new String[][]{
                {"0", "99", "4"} // abyss subtype must be 0..9
        };
    }

    @Test
    void createInitialBoard_basic_ok() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10));
        assertEquals(1, gm.getCurrentPlayerID());
        assertNotNull(gm.getProgrammerInfo(1));
        assertNotNull(gm.getProgrammerInfo(2));
    }

    @Test
    void createInitialBoard_extended_ok_null_boardStuff() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players3WithLangs(), 20, null));
        assertEquals(1, gm.getCurrentPlayerID());
        assertNotNull(gm.getProgrammerInfo(1));
        assertNotNull(gm.getSlotInfo(1));
    }

    @Test
    void createInitialBoard_extended_ok_with_boardStuff() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players3WithLangs(), 20, boardStuffOK(20)));
        assertEquals(1, gm.getCurrentPlayerID());
        assertNotNull(gm.getSlotInfo(2));
        assertNotNull(gm.getSlotInfo(5));
    }

    @Test
    void createInitialBoard_reject_players_count() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{{"1", "A", "blue"}}, 10));
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", "A", "blue"},
                {"2", "B", "green"},
                {"3", "C", "brown"},
                {"4", "D", "purple"},
                {"5", "E", "blue"}
        }, 10));
    }

    @Test
    void createInitialBoard_reject_worldSize() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(players2Basic(), 3));
    }

    @Test
    void createInitialBoard_reject_duplicate_ids() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", "A", "blue"},
                {"1", "B", "green"}
        }, 10));
    }

    @Test
    void createInitialBoard_reject_invalid_ids() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{
                {"0", "A", "blue"},
                {"2", "B", "green"}
        }, 10));
        assertFalse(gm.createInitialBoard(new String[][]{
                {"-5", "A", "blue"},
                {"2", "B", "green"}
        }, 10));
        assertFalse(gm.createInitialBoard(new String[][]{
                {"x", "A", "blue"},
                {"2", "B", "green"}
        }, 10));
    }

    @Test
    void createInitialBoard_reject_empty_names() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", "   ", "blue"},
                {"2", "B", "green"}
        }, 10));
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", null, "blue"},
                {"2", "B", "green"}
        }, 10));
    }

    @Test
    void createInitialBoard_reject_invalid_colors_or_duplicates() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", "A", "pink"},
                {"2", "B", "green"}
        }, 10));
        assertFalse(gm.createInitialBoard(new String[][]{
                {"1", "A", "blue"},
                {"2", "B", "blue"}
        }, 10));
    }

    @Test
    void createInitialBoard_extended_reject_invalid_boardStuff_type() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(players2Basic(), 12, boardStuffInvalidType()));
    }

    @Test
    void createInitialBoard_extended_reject_invalid_boardStuff_pos() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(players2Basic(), 12, boardStuffInvalidPos()));
    }

    @Test
    void createInitialBoard_extended_reject_invalid_boardStuff_subtype() {
        GameManager gm = new GameManager();
        assertFalse(gm.createInitialBoard(players2Basic(), 12, boardStuffInvalidSubtype()));
    }

    @Test
    void getImagePng_bounds_and_finish() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 8));
        assertNull(gm.getImagePng(0));
        assertNull(gm.getImagePng(9));
        assertNotNull(gm.getImagePng(8));
    }

    @Test
    void getProgrammerInfo_format_7_fields_part2() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players3WithLangs(), 20, boardStuffOK(20)));
        String[] p = gm.getProgrammerInfo(2);
        assertNotNull(p);
        assertEquals(7, p.length);
        assertEquals("2", p[0]);
        assertEquals("Bob", p[1]);
        assertNotNull(p[2]);
        assertNotNull(p[3]);
        assertNotNull(p[4]);
        assertNotNull(p[5]);
        assertNotNull(p[6]);
    }

    @Test
    void getProgrammerInfo_unknown_id_returns_null() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10));
        assertNull(gm.getProgrammerInfo(999));
    }

    @Test
    void getProgrammerInfoAsStr_basic_contains_fields() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players3WithLangs(), 20, boardStuffOK(20)));
        String s = gm.getProgrammerInfoAsStr(1);
        assertNotNull(s);
        assertTrue(s.contains("1"));
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("|"));
    }

    @Test
    void getProgrammersInfo_not_null_and_contains_names() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players3WithLangs(), 20, boardStuffOK(20)));
        String s = gm.getProgrammersInfo();
        assertNotNull(s);
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("Bob"));
    }

    @Test
    void getSlotInfo_bounds() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10, boardStuffOK(10)));
        assertNull(gm.getSlotInfo(0));
        assertNull(gm.getSlotInfo(11));
        assertNotNull(gm.getSlotInfo(1));
    }

    @Test
    void getSlotInfo_ids_in_position1_initially() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10, null));
        String[] slot1 = gm.getSlotInfo(1);
        assertNotNull(slot1);
        assertTrue(slot1[0].contains("1"));
        assertTrue(slot1[0].contains("2"));
    }

    @Test
    void moveCurrentPlayer_reject_invalid_dice_values() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10, null));
        assertFalse(gm.moveCurrentPlayer(0));
        assertFalse(gm.moveCurrentPlayer(7));
    }

    @Test
    void moveCurrentPlayer_basic_and_react_advances_turn() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 20, boardStuffOK(20)));
        assertEquals(1, gm.getCurrentPlayerID());
        assertTrue(gm.moveCurrentPlayer(2));
        gm.reactToAbyssOrTool();
        assertEquals(2, gm.getCurrentPlayerID());
    }

    @Test
    void move_bounce_back_when_overshoot() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 6, null));
        assertTrue(gm.moveCurrentPlayer(6)); // from 1 to 5 (bounce: 1+6=7, overshoot 1 -> 5)
        gm.reactToAbyssOrTool();
        String[] info = gm.getProgrammerInfo(1);
        assertEquals("5", info[4]);
    }

    @Test
    void tool_should_not_disappear_from_board_multiple_players_can_catch() {
        GameManager gm = new GameManager();
        String[][] stuff = new String[][]{
                {"1", "4", "2"} // IDE at pos 2
        };
        assertTrue(gm.createInitialBoard(players2Basic(), 10, stuff));

        assertEquals(1, gm.getCurrentPlayerID());
        assertTrue(gm.moveCurrentPlayer(1));
        String msg1 = gm.reactToAbyssOrTool();
        assertNotNull(msg1);

        assertEquals(2, gm.getCurrentPlayerID());
        assertTrue(gm.moveCurrentPlayer(1));
        String msg2 = gm.reactToAbyssOrTool();
        assertNotNull(msg2);
    }

    @Test
    void abyss_should_not_disappear_from_board() {
        GameManager gm = new GameManager();
        String[][] stuff = new String[][]{
                {"0", "0", "2"} // Syntax error abyss at pos 2
        };
        assertTrue(gm.createInitialBoard(players2Basic(), 10, stuff));

        assertEquals(1, gm.getCurrentPlayerID());
        assertTrue(gm.moveCurrentPlayer(1));
        String msg1 = gm.reactToAbyssOrTool();
        assertNotNull(msg1);

        assertEquals(2, gm.getCurrentPlayerID());
        assertTrue(gm.moveCurrentPlayer(1));
        String msg2 = gm.reactToAbyssOrTool();
        assertNotNull(msg2);
    }

    @Test
    void gameIsOver_when_player_reaches_end() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 4, null));
        assertFalse(gm.gameIsOver());
        assertTrue(gm.moveCurrentPlayer(3)); // from 1 to 4
        gm.reactToAbyssOrTool();
        assertTrue(gm.gameIsOver());
    }

    @Test
    void getGameResults_empty_before_game_over() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 10, null));
        ArrayList<String> r = gm.getGameResults();
        assertNotNull(r);
        assertTrue(r.isEmpty());
    }

    @Test
    void getGameResults_has_header_and_winner_after_game_over() {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players2Basic(), 4, null));
        assertTrue(gm.moveCurrentPlayer(3));
        gm.reactToAbyssOrTool();
        assertTrue(gm.gameIsOver());

        ArrayList<String> r = gm.getGameResults();
        assertNotNull(r);
        assertFalse(r.isEmpty());
        assertTrue(r.contains("THE GREAT PROGRAMMING JOURNEY"));
    }

    @Test
    void authorsPanel_not_null() {
        GameManager gm = new GameManager();
        JPanel p = gm.getAuthorsPanel();
        assertNotNull(p);
    }

    @Test
    void customizeBoard_has_expected_keys() {
        GameManager gm = new GameManager();
        HashMap<String, String> m = gm.customizeBoard();
        assertNotNull(m);
        assertTrue(m.containsKey("gridBackgroundColor"));
        assertTrue(m.containsKey("logoImage"));
    }

    @Test
    void save_and_load_roundtrip(@TempDir Path tempDir) {
        GameManager gm = new GameManager();
        assertTrue(gm.createInitialBoard(players4WithLangs(), 25, boardStuffOK(25)));

        assertTrue(gm.moveCurrentPlayer(2));
        gm.reactToAbyssOrTool();
        assertTrue(gm.moveCurrentPlayer(2));
        gm.reactToAbyssOrTool();

        File f = tempDir.resolve("save1.txt").toFile();
        assertTrue(gm.saveGame(f));

        GameManager gm2 = new GameManager();
        assertDoesNotThrow(() -> gm2.loadGame(f));
        assertNotNull(gm2.getProgrammerInfo(2));
        assertNotNull(gm2.getProgrammerInfoAsStr(10));
    }

    @Test
    void loadGame_missing_file_throws(@TempDir Path tempDir) {
        GameManager gm = new GameManager();
        File f = tempDir.resolve("nope.txt").toFile();
        assertThrows(FileNotFoundException.class, () -> gm.loadGame(f));
    }

    @Test
    void loadGame_invalid_file_throws(@TempDir Path tempDir) throws Exception {
        File f = tempDir.resolve("bad.txt").toFile();
        assertTrue(f.createNewFile());
        java.nio.file.Files.writeString(f.toPath(), "lixo\nsem\nformato\n");

        GameManager gm = new GameManager();
        assertThrows(InvalidFileException.class, () -> gm.loadGame(f));
    }

    @Test
    void getCurrentPlayerID_returns_minus1_if_not_initialized() {
        GameManager gm = new GameManager();
        assertEquals(-1, gm.getCurrentPlayerID());
    }

    @Test
    void getImagePng_returns_null_when_not_initialized() {
        GameManager gm = new GameManager();
        assertNull(gm.getImagePng(1));
    }

    @Test
    void moveCurrentPlayer_returns_false_when_not_initialized() {
        GameManager gm = new GameManager();
        assertFalse(gm.moveCurrentPlayer(3));
    }
}
