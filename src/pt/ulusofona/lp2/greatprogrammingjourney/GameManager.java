package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class GameManager {

    private int worldSize = 0;
    private boolean initialized = false;
    private int turnCount = 0;
    private Integer winnerId = null;

    private final ArrayList<Integer> playerOrder = new ArrayList<>();
    private final HashMap<Integer, Player> players = new HashMap<>();

    private final HashMap<Integer, Abyss> abysses = new HashMap<>();
    private final HashMap<Integer, Tool> tools = new HashMap<>();

    private int currentIdx = 0;

    public GameManager() {}

    public boolean createInitialBoard(String[][] playerInfo, int worldSize) {
        this.winnerId = null;
        this.initialized = false;

        this.turnCount = 1;

        this.currentIdx = 0;

        if (playerInfo == null || playerInfo.length < 2 || playerInfo.length > 4) {
            return false;
        }
        if (worldSize < 4) {
            return false;
        }

        this.worldSize = worldSize;
        playerOrder.clear();
        players.clear();

        abysses.clear();
        tools.clear();

        java.util.HashSet<Integer> usedIds = new java.util.HashSet<>();
        java.util.HashSet<String> usedColors = new java.util.HashSet<>();
        java.util.HashSet<String> allowed = new java.util.HashSet<>(Arrays.asList("blue","green","brown","purple"));

        for (String[] row : playerInfo) {
            if (row == null || row.length < 3) {
                return false;
            }

            int id;
            try { id = Integer.parseInt(row[0]); } catch (Exception e) { return false; }
            if (id <= 0) {
                return false;
            }
            if (!usedIds.add(id)) {
                return false;
            }

            String name = (row[1] == null) ? "" : row[1].trim();
            if (name.isEmpty()) {
                return false;
            }

            ArrayList<String> langs = new ArrayList<>();
            String colorRaw;
            if (row.length >= 4) {
                String langsRaw = (row[2] == null) ? "" : row[2].trim();
                if (!langsRaw.isEmpty()) {
                    String[] parts = langsRaw.split(";");
                    for (String p : parts) {
                        String s = p.trim();
                        if (!s.isEmpty()) {
                            langs.add(s);
                        }
                    }
                }
                colorRaw = (row[3] == null) ? "" : row[3].trim();
            } else {
                colorRaw = (row[2] == null) ? "" : row[2].trim();
                langs.add("Java");
            }

            String color = colorRaw.toLowerCase(java.util.Locale.ROOT);
            if (!allowed.contains(color)) {
                return false;
            }
            if (!usedColors.add(color)) {
                return false;
            }

            Player p = new Player(id, name, color, langs);
            p.pos = 1;
            p.state = "Em Jogo";

            players.put(id, p);
            playerOrder.add(id);
        }

        Collections.sort(playerOrder);

        initialized = true;
        return true;
    }

    public boolean createInitialBoard(String[][] playerInfo, int worldSize, String[][] abyssesAndTools) {

        if (!validateAbyssesAndTools(abyssesAndTools, worldSize)) {
            return false;
        }

        if (!createInitialBoard(playerInfo, worldSize)) {
            return false;
        }

        abysses.clear();
        tools.clear();

        if (abyssesAndTools == null) {
            return true;
        }

        for (String[] row : abyssesAndTools) {
            int type;
            int subtype;
            int position;

            try {
                type = Integer.parseInt(row[0]);
                subtype = Integer.parseInt(row[1]);
                position = Integer.parseInt(row[2]);
            } catch (Exception e) {
                return false;
            }

            if (type == 0) {
                abysses.put(position, new Abyss(subtype, position));
            } else {
                tools.put(position, new Tool(subtype, position));
            }
        }

        return true;
    }

    private boolean validateAbyssesAndTools(String[][] abyssesAndTools, int worldSizeArg) {
        if (abyssesAndTools == null) {
            return true;
        }

        for (String[] row : abyssesAndTools) {
            if (row == null || row.length != 3) {
                return false;
            }

            int type;
            int subtype;
            int position;

            try {
                type = Integer.parseInt(row[0]);
                subtype = Integer.parseInt(row[1]);
                position = Integer.parseInt(row[2]);
            } catch (Exception e) {
                return false;
            }

            if (type != 0 && type != 1) {
                return false;
            }

            if (type == 0) {
                if (subtype < 0 || subtype > 9) {
                    return false;
                }
            } else {
                if (subtype < 0 || subtype > 5) {
                    return false;
                }
            }

            if (position < 1 || position > worldSizeArg) {
                return false;
            }
        }

        return true;
    }

    public String getImagePng(int nrSquare) {
        if (nrSquare < 1 || nrSquare > worldSize) {
            return null;
        }
        if (nrSquare == worldSize) {
            return "glory.png";
        }
        if (abysses.containsKey(nrSquare)) {
            return "abismo_" + abysses.get(nrSquare).subtypeId + ".png";
        }
        if (tools.containsKey(nrSquare)) {
            return "ferramenta_" + tools.get(nrSquare).subtypeId + ".png";
        }
        return null;
    }


    public String[] getProgrammerInfo(int id) {
        Player p = players.get(id);
        if (p == null) {
            return null;
        }

        String langs = String.join(";", p.langs);
        String color = p.colorLower.toUpperCase();
        String pos = String.valueOf(p.pos);

        ArrayList<String> orderedTools = new ArrayList<>(p.tools);
        Collections.sort(orderedTools, String.CASE_INSENSITIVE_ORDER);
        String toolsStr = String.join(";", orderedTools);

        return new String[]{String.valueOf(p.id), p.name, langs, color, pos, toolsStr, p.state};
    }


    public String getProgrammerInfoAsStr(int id) {
        Player p = players.get(id);
        if (p == null) {
            return null;
        }

        ArrayList<String> langs = new ArrayList<>(p.langs);
        Collections.sort(langs, String.CASE_INSENSITIVE_ORDER);
        String langsStr = String.join("; ", langs);

        ArrayList<String> tools = new ArrayList<>(p.tools);
        Collections.sort(tools, String.CASE_INSENSITIVE_ORDER);
        String toolsStr = tools.isEmpty() ? "No tools" : String.join(";", tools);

        return p.id + " | " + p.name + " | " + p.pos + " | " + toolsStr + " | " + langsStr + " | " + p.state;
    }

    public String getProgrammersInfo() {
        ArrayList<String> parts = new ArrayList<>();

        for (Player p : players.values()) {
            if (!p.state.equals("Derrotado")) {
                ArrayList<String> tools = new ArrayList<>(p.tools);
                Collections.sort(tools, String.CASE_INSENSITIVE_ORDER);
                String toolsStr = tools.isEmpty() ? "No tools" : String.join(";", tools);
                parts.add(p.name + " : " + toolsStr);
            }
        }

        return String.join(" | ", parts);
    }


    public String[] getSlotInfo(int position) {
        if (position < 1 || position > worldSize) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Player p : players.values()) {
            if (p.pos == position) {
                if (!first) sb.append(",");
                sb.append(p.id);
                first = false;
            }
        }
        String playersStr = sb.toString();

        String desc = "";
        if (abysses.containsKey(position)) {
            desc = "Abyss";
        } else if (tools.containsKey(position)) {
            desc = "Tool";
        }

        String typeId = "";
        if (abysses.containsKey(position)) {
            typeId = "A:" + abysses.get(position).subtypeId;
        } else if (tools.containsKey(position)) {
            typeId = "T:" + tools.get(position).subtypeId;
        }

        return new String[]{ playersStr, desc, typeId };
    }


    public int getCurrentPlayerID() {
        if (playerOrder.isEmpty()) {
            return -1;
        }
        return playerOrder.get(currentIdx);
    }

    public boolean moveCurrentPlayer(int nrSpaces) {
        if (nrSpaces < 1 || nrSpaces > 6) {
            return false;
        }
        if (playerOrder.isEmpty() || worldSize <= 0) {
            return false;
        }

        int currentId = playerOrder.get(currentIdx);
        Player p = players.get(currentId);
        if (p == null) {
            return false;
        }

        int destino = p.pos + nrSpaces;

        if (destino > worldSize) {
            int excesso = destino - worldSize;
            destino = worldSize - excesso;
            if (destino < 1) {
                destino = 1;
            }
        }

        p.pos = destino;

        boolean ganhouAgora = (p.pos == worldSize && winnerId == null);
        if (ganhouAgora) {
            winnerId = p.id;
        }
        turnCount++;

        if (!ganhouAgora) {
            advanceTurn();
        }

        return true;
    }

    public boolean gameIsOver() {
        if (worldSize <= 0 || players.isEmpty()) {
            return false;
        }
        for (Player p : players.values()) {
            if (p.pos == worldSize) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getGameResults() {
        ArrayList<String> out = new ArrayList<>();
        if (!gameIsOver() || winnerId == null) {
            return out;
        }

        out.add("THE GREAT PROGRAMMING JOURNEY");
        out.add("");
        out.add("NR. DE TURNOS");
        out.add(String.valueOf(turnCount + 1));
        out.add("");
        out.add("VENCEDOR");
        out.add(players.get(winnerId).name);
        out.add("");
        out.add("RESTANTES");

        ArrayList<Integer> restantes = new ArrayList<>();
        for (int id : playerOrder) {
            if (winnerId != null && id != winnerId) {
                restantes.add(id);
            }
        }
        restantes.sort((a, b) -> {
            int pa = players.get(a).pos;
            int pb = players.get(b).pos;
            if (pa != pb) {
                return Integer.compare(pb, pa);
            }
            return Integer.compare(a, b);
        });

        for (Integer id : restantes) {
            Player p = players.get(id);
            out.add(p.name + " " + p.pos);
        }

        return out;
    }

    public JPanel getAuthorsPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setPreferredSize(new Dimension(360, 240));
        root.setBackground(hex("#0B1220"));
        root.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, hex("#111827")), BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("THE GREAT PROGRAMMING JOURNEY", SwingConstants.CENTER);
        title.setForeground(hex("#FBBF24"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(authorLine("Número: 22404033  |  Nome: Miguel Lopes"));
        content.add(Box.createVerticalStrut(6));
        content.add(authorLine("Turma: LP2-2D1"));
        content.add(authorLine("Ano letivo: 2025/26"));

        root.add(content, BorderLayout.CENTER);

        JLabel footer = new JLabel("Universidade Lusófona", SwingConstants.CENTER);
        footer.setForeground(hex("#CBD5E1"));
        footer.setFont(footer.getFont().deriveFont(Font.PLAIN, 12f));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    public HashMap<String, String> customizeBoard() {
        HashMap<String, String> m = new HashMap<>();
        m.put("gridBackgroundColor",   "#0B1220");
        m.put("toolbarBackgroundColor","#111827");
        m.put("slotBackgroundColor",   "#1F2937");
        m.put("slotNumberColor",       "#FBBF24");
        m.put("slotNumberFontSize",    "14");
        m.put("cellSpacing",           "3");
        m.put("logoImage",             "logo.png");
        return m;
    }

    private void advanceTurn() {
        if (!playerOrder.isEmpty()) {
            currentIdx = (currentIdx + 1) % playerOrder.size();
        }
    }

    private String cap(String s) {
        return (s == null || s.isEmpty()) ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private JLabel authorLine(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(hex("#E2E8F0"));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        return lbl;
    }

    private Color hex(String rgb) {
        return Color.decode(rgb);
    }
}
