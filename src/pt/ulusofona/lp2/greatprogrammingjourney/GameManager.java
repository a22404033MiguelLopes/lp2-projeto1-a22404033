package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



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

    public String getImagePng(int position) {
        if (position < 1 || position > worldSize) {
            return null;
        }
        if (position == worldSize) {
            return "glory.png";
        }

        Abyss a = abysses.get(position);
        if (a != null) {
            switch (a.subtypeId) {
                case 0: return "syntax.png";
                case 1: return "crash.png";
                case 2: return "core-dumped.png";
                case 3: return "exception.png";
                case 4: return "secondary-effects.png";
                case 5: return "catch.png";
                case 6: return "infinite-loop.png";
                case 7: return "duplicated-code.png";
                case 8: return "bsod.png";
                case 9: return "file-not-found-exception.png";
                default: return null;
            }
        }

        Tool t = tools.get(position);
        if (t != null) {
            switch (t.subtypeId) {
                case 0: return "IDE.png";
                case 1: return "unit-tests.png";
                case 2: return "functional.png";
                case 3: return "logic.png";
                case 4: return "inheritance.png";
                case 5: return "ajuda-professor.png";
                default: return null;
            }
        }

        return null;
    }



    public String[] getProgrammerInfo(int id) {
        Player p = players.get(id);
        if (p == null) {
            return null;
        }

        String langs = String.join(";", p.langs);
        String color = cap(p.colorLower);
        String pos = String.valueOf(p.pos);

        ArrayList<String> orderedTools = new ArrayList<>(p.tools);
        Collections.sort(orderedTools, String.CASE_INSENSITIVE_ORDER);
        String toolsStr = String.join(";", orderedTools);

        return new String[]{
                String.valueOf(p.id),
                p.name,
                langs,
                color,
                pos,
                toolsStr,
                p.state
        };
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
                if (!first) {
                    sb.append(",");
                }
                sb.append(p.id);
                first = false;
            }
        }
        String playersStr = sb.toString();

        String desc = "";
        String typeId = "";

        Abyss abyss = abysses.get(position);
        if (abyss != null) {
            desc = getAbyssDescription(abyss.subtypeId);
            typeId = "A:" + abyss.subtypeId;
        } else {
            Tool tool = tools.get(position);
            if (tool != null) {
                desc = getToolDescription(tool.subtypeId);
                typeId = "T:" + tool.subtypeId;
            }
        }

        return new String[]{ playersStr, desc, typeId };
    }


    private String getAbyssDescription(int subtypeId) {
        switch (subtypeId) {
            case 0: return "Erro de sintaxe";
            // depois completas os outros se/quando for preciso:
            // case 1: return "...";
            // case 2: return "...";
            default: return "Abismo";
        }
    }

    private String getToolDescription(int subtypeId) {
        switch (subtypeId) {
            case 0: return "Herança";
            case 1: return "Programação Funcional";
            case 2: return "Testes Unitários";
            case 3: return "Tratamento de Excepções";
            case 4: return "IDE";
            case 5: return "Ajuda Do Professor";
            default: return "Ferramenta";
        }
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

        Player p = players.get(playerOrder.get(currentIdx));
        if (p == null) {
            return false;
        }
        if (p.state.equals("Derrotado")) {
            return false;
        }

        if (!p.langs.isEmpty()) {
            String first = p.langs.get(0);
            if (first.equalsIgnoreCase("Assembly") && nrSpaces > 2) {
                return false;
            }
            if (first.equalsIgnoreCase("C") && nrSpaces > 3) {
                return false;
            }
        }

        p.lastLastPos = p.lastPos;
        p.lastPos = p.pos;

        int destino = p.pos + nrSpaces;
        if (destino > worldSize) {
            int excesso = destino - worldSize;
            destino = worldSize - excesso;
            if (destino < 1) destino = 1;
        }

        p.pos = destino;
        return true;
    }

    public String reactToAbyssOrTool() {
        if (playerOrder.isEmpty() || worldSize <= 0) {
            return null;
        }

        Player p = players.get(playerOrder.get(currentIdx));
        if (p == null) {
            return null;
        }

        String message = null;
        int pos = p.pos;

        Tool tool = tools.get(pos);
        if (tool != null) {
            String toolName = getToolDescription(tool.subtypeId);

            if (!p.tools.contains(toolName)) {
                p.tools.add(toolName);
                message = "Jogador " + p.name + " apanhou a ferramenta " + toolName + ".";
            } else {
                message = "Jogador " + p.name + " já tinha a ferramenta " + toolName + ".";
            }

            tools.remove(pos);
        } else {
            Abyss abyss = abysses.get(pos);
            if (abyss != null) {
                if (!p.tools.isEmpty()) {
                    String usedTool = p.tools.remove(0);
                    message = "Jogador " + p.name + " usou a ferramenta " + usedTool + " para evitar o abismo A:" + abyss.subtypeId + ".";
                } else {
                    p.state = "Derrotado";
                    message = "Jogador " + p.name + " caiu no abismo A:" + abyss.subtypeId + " e foi derrotado.";
                }
            }
        }

        if (p.pos == worldSize && winnerId == null && !p.state.equals("Derrotado")) {
            winnerId = p.id;
        }

        turnCount++;
        advanceTurn();

        return message;
    }


    public boolean gameIsOver() {
        if (playerOrder.isEmpty() || worldSize <= 0) {
            return false;
        }

        for (Player p : players.values()) {
            if (!p.state.equals("Derrotado") && p.pos == worldSize) {
                return true;
            }
        }

        int vivos = 0;
        for (Player p : players.values()) {
            if (!p.state.equals("Derrotado")) {
                vivos++;
            }
        }

        return vivos <= 1;
    }


    public ArrayList<String> getGameResults() {
        ArrayList<String> out = new ArrayList<>();

        if (!gameIsOver()) {
            return out;
        }

        if (winnerId == null) {
            Player winner = null;
            for (int id : playerOrder) {
                Player p = players.get(id);
                if (p == null || p.state.equals("Derrotado")) {
                    continue;
                }
                if (winner == null
                        || p.pos > winner.pos
                        || (p.pos == winner.pos && p.id < winner.id)) {
                    winner = p;
                }
            }
            if (winner != null) {
                winnerId = winner.id;
            } else {
                return out;
            }
        }

        out.add("THE GREAT PROGRAMMING JOURNEY");
        out.add("");
        out.add("NR. DE TURNOS");
        out.add(String.valueOf(turnCount));
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

    public void loadGame(File file) throws FileNotFoundException, InvalidFileException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException("Ficheiro não encontrado");
        }

        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (Exception e) {
            throw new InvalidFileException("Não foi possível ler o ficheiro", e);
        }

        try {
            if (!sc.hasNextLine()) {
                throw new InvalidFileException("Ficheiro vazio");
            }

            String header = sc.nextLine().trim();
            String[] headerParts = header.split(";");
            if (headerParts.length != 3) {
                throw new InvalidFileException("Cabeçalho inválido");
            }

            int wSize = Integer.parseInt(headerParts[0]);
            int tCount = Integer.parseInt(headerParts[1]);
            int currentPlayerId = Integer.parseInt(headerParts[2]);

            if (!sc.hasNextLine()) {
                throw new InvalidFileException("Dados de jogadores em falta");
            }

            String line = sc.nextLine().trim();
            int numPlayers = Integer.parseInt(line);
            if (numPlayers < 2 || numPlayers > 4) {
                throw new InvalidFileException("Número de jogadores inválido");
            }

            playerOrder.clear();
            players.clear();
            abysses.clear();
            tools.clear();

            this.worldSize = wSize;
            this.turnCount = tCount;
            this.winnerId = null;
            this.initialized = false;

            for (int i = 0; i < numPlayers; i++) {
                if (!sc.hasNextLine()) {
                    throw new InvalidFileException("Linha de jogador em falta");
                }
                String pl = sc.nextLine().trim();
                String[] parts = pl.split(";");
                if (parts.length != 7) {
                    throw new InvalidFileException("Formato de jogador inválido");
                }

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String colorLower = parts[2].toLowerCase(java.util.Locale.ROOT);
                int pos = Integer.parseInt(parts[3]);
                String state = parts[4];

                String langsRaw = parts[5];
                String toolsRaw = parts[6];

                if (id <= 0) {
                    throw new InvalidFileException("ID de jogador inválido");
                }
                if (!state.equals("Em Jogo") && !state.equals("Preso") && !state.equals("Derrotado")) {
                    throw new InvalidFileException("Estado de jogador inválido");
                }
                if (pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Posição de jogador inválida");
                }

                ArrayList<String> langs = new ArrayList<>();
                if (!langsRaw.isEmpty()) {
                    String[] lp = langsRaw.split(",");
                    for (String l : lp) {
                        String s = l.trim();
                        if (!s.isEmpty()) {
                            langs.add(s);
                        }
                    }
                }

                Player p = new Player(id, name, colorLower, langs);
                p.pos = pos;
                p.state = state;

                ArrayList<String> toolsList = new ArrayList<>();
                if (!toolsRaw.isEmpty()) {
                    String[] tp = toolsRaw.split(",");
                    for (String t : tp) {
                        String s = t.trim();
                        if (!s.isEmpty()) {
                            toolsList.add(s);
                        }
                    }
                }
                p.tools = toolsList;

                players.put(id, p);
                playerOrder.add(id);
            }

            if (!sc.hasNextLine()) {
                throw new InvalidFileException("Número de abismos em falta");
            }
            int numAbysses = Integer.parseInt(sc.nextLine().trim());
            for (int i = 0; i < numAbysses; i++) {
                if (!sc.hasNextLine()) {
                    throw new InvalidFileException("Linha de abismo em falta");
                }
                String al = sc.nextLine().trim();
                String[] parts = al.split(";");
                if (parts.length != 2) {
                    throw new InvalidFileException("Formato de abismo inválido");
                }
                int subtype = Integer.parseInt(parts[0]);
                int pos = Integer.parseInt(parts[1]);

                if (pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Posição de abismo inválida");
                }
                abysses.put(pos, new Abyss(subtype, pos));
            }

            if (!sc.hasNextLine()) {
                throw new InvalidFileException("Número de ferramentas em falta");
            }
            int numTools = Integer.parseInt(sc.nextLine().trim());
            for (int i = 0; i < numTools; i++) {
                if (!sc.hasNextLine()) {
                    throw new InvalidFileException("Linha de ferramenta em falta");
                }
                String tl = sc.nextLine().trim();
                String[] parts = tl.split(";");
                if (parts.length != 2) {
                    throw new InvalidFileException("Formato de ferramenta inválido");
                }
                int subtype = Integer.parseInt(parts[0]);
                int pos = Integer.parseInt(parts[1]);

                if (pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Posição de ferramenta inválida");
                }
                tools.put(pos, new Tool(subtype, pos));
            }

            java.util.Collections.sort(playerOrder);

            this.currentIdx = 0;
            for (int i = 0; i < playerOrder.size(); i++) {
                if (playerOrder.get(i) == currentPlayerId) {
                    this.currentIdx = i;
                    break;
                }
            }

            for (Player p : players.values()) {
                if (!p.state.equals("Derrotado") && p.pos == wSize) {
                    this.winnerId = p.id;
                    break;
                }
            }

            this.initialized = true;
        } catch (NumberFormatException e) {
            throw new InvalidFileException("Valor numérico inválido", e);
        } catch (InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileException("Erro ao carregar ficheiro", e);
        } finally {
            sc.close();
        }
    }

    public boolean saveGame(File file) {
        if (file == null) {
            return false;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            int currentPlayerId = getCurrentPlayerID();
            out.println(worldSize + ";" + turnCount + ";" + currentPlayerId);

            out.println(playerOrder.size());
            for (int id : playerOrder) {
                Player p = players.get(id);
                if (p == null) {
                    continue;
                }

                String langsStr = String.join(",", p.langs);
                String toolsStr = String.join(",", p.tools);

                out.println(
                        p.id + ";" +
                                p.name + ";" +
                                p.colorLower + ";" +
                                p.pos + ";" +
                                p.state + ";" +
                                langsStr + ";" +
                                toolsStr
                );
            }

            out.println(abysses.size());
            for (Abyss a : abysses.values()) {
                out.println(a.subtypeId + ";" + a.position);
            }

            out.println(tools.size());
            for (Tool t : tools.values()) {
                out.println(t.subtypeId + ";" + t.position);
            }

            return true;
        } catch (IOException e) {
            return false;
        }
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
