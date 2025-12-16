package pt.ulusofona.lp2.greatprogrammingjourney;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class GameManager {

    private int worldSize = 0;
    private boolean initialized = false;
    private int turnCount = 0;
    private Integer winnerId = null;
    private int lastDice = 0;

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
                Abyss a = createAbyss(subtype, position);
                if (a == null) {
                    return false;
                }
                abysses.put(position, a);
            } else {
                Tool t = createTool(subtype, position);
                if (t == null) {
                    return false;
                }
                tools.put(position, t);
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

    public java.util.List<Player> getPlayersAt(int position) {
        java.util.List<Player> res = new java.util.ArrayList<>();
        for (Player p : players.values()) {
            if (p.pos == position) {
                res.add(p);
            }
        }
        return res;
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
            switch (a.getId()) {
                case 0: return "syntax.png";
                case 1: return "logic.png";
                case 2: return "exception.png";
                case 3: return "file-not-found-exception.png";
                case 4: return "crash.png";
                case 5: return "duplicated-code.png";
                case 6: return "secondary-effects.png";
                case 7: return "bsod.png";
                case 8: return "infinite-loop.png";
                case 9: return "core-dumped.png";
                default: return null;
            }
        }

        Tool t = tools.get(position);
        if (t != null) {
            switch (t.getId()) {
                case 0: return "inheritance.png";
                case 1: return "functional.png";
                case 2: return "unit-tests.png";
                case 3: return "exception.png";
                case 4: return "IDE.png";
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
            desc = abyss.getName();
            typeId = "A:" + abyss.getId();
        } else {
            Tool tool = tools.get(position);
            if (tool != null) {
                desc = tool.getName();
                typeId = "T:" + tool.getId();
            }
        }

        return new String[]{ playersStr, desc, typeId };
    }



    private String getAbyssDescription(int subtypeId) {
        switch (subtypeId) {
            case 0: return "Erro de sintaxe";
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

        if (gameIsOver() && winnerId != null) {
            return winnerId;
        }

        Player current = players.get(playerOrder.get(currentIdx));
        if (current == null || current.state.equals("Derrotado")) {
            for (int i = 0; i < playerOrder.size(); i++) {
                Player cand = players.get(playerOrder.get(i));
                if (cand != null && !cand.state.equals("Derrotado")) {
                    currentIdx = i;
                    break;
                }
            }
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
        if (p.state.equals("Derrotado") || p.state.equals("Preso")) {
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

        lastDice = nrSpaces;

        p.lastLastPos = p.lastPos;
        p.lastPos = p.pos;

        int destino = p.pos + nrSpaces;
        if (destino > worldSize) {
            int excesso = destino - worldSize;
            destino = worldSize - excesso;
            if (destino < 1) {
                destino = 1;
            }
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
            String toolName = tool.getName();

            if (!p.tools.contains(toolName)) {
                p.tools.add(toolName);
                message = "Jogador " + p.name + " agarrou " + toolName + ".";
            } else {
                message = "Jogador " + p.name + " já tinha a ferramenta " + toolName + ".";
            }

        }


        Abyss abyss = abysses.get(pos);
        if (abyss != null) {
            if (playerHasToolForAbyss(p, abyss)) {
                consumeToolForAbyss(p, abyss);
                message = "Jogador " + p.name + " usou uma ferramenta para evitar o abismo " + abyss.getName() + ".";
            } else {
                message = abyss.applyEffect(p, this, lastDice);
            }
        }

        if (!p.state.equals("Derrotado") && p.pos == worldSize && winnerId == null) {
            winnerId = p.id;
        }

        turnCount++;

        if (!gameIsOver()) {
            advanceTurn();
        }

        return message;
    }



    public boolean gameIsOver() {
        if (worldSize <= 0 || players.isEmpty()) {
            return false;
        }

        int vivos = 0;

        for (Player p : players.values()) {
            if (!p.state.equals("Derrotado")) {
                vivos++;
                if (p.pos == worldSize) {
                    return true;
                }
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

            String na = players.get(a).name;
            String nb = players.get(b).name;
            return na.compareToIgnoreCase(nb);
        });


        for (Integer id : restantes) {
            Player p = players.get(id);
            out.add(p.name + " " + p.pos);
        }

        return out;
    }

    public void loadGame(File file) throws FileNotFoundException, InvalidFileException {
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException("Ficheiro não encontrado");
        }
        try (Scanner sc = new Scanner(file)) {
            String header = sc.hasNextLine() ? sc.nextLine().trim() : "";
            String[] hp = header.split(";");
            if (hp.length != 3) {
                throw new InvalidFileException("Formato inválido");
            }
            int wSize = Integer.parseInt(hp[0]);
            int tCount = Integer.parseInt(hp[1]);
            int currentPlayerId = Integer.parseInt(hp[2]);

            int numPlayers = Integer.parseInt(sc.nextLine().trim());
            if (numPlayers < 2 || numPlayers > 4) {
                throw new InvalidFileException("Formato inválido");
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
                String[] parts = sc.nextLine().trim().split(";");
                if (parts.length != 6 && parts.length != 7) {
                    throw new InvalidFileException("Formato inválido");
                }

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String colorLower = parts[2].toLowerCase(Locale.ROOT);
                int pos = Integer.parseInt(parts[3]);
                String state = parts[4];
                if (id <= 0 || pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Formato inválido");
                }
                if (!state.equals("Em Jogo") && !state.equals("Preso") && !state.equals("Derrotado")) {
                    throw new InvalidFileException("Formato inválido");
                }
                ArrayList<String> langs = new ArrayList<>();
                String langsRaw = parts[5].trim();
                if (!langsRaw.isEmpty()) {
                    for (String l : langsRaw.split(",")) {
                        l = l.trim();
                        if (!l.isEmpty()) {
                            langs.add(l);
                        }
                    }
                }

                Player p = new Player(id, name, colorLower, langs);
                p.pos = pos;
                p.state = state;
                ArrayList<String> toolsList = new ArrayList<>();
                String toolsRaw = (parts.length == 7) ? parts[6].trim() : "";
                if (!toolsRaw.isEmpty()) {
                    for (String t : toolsRaw.split(",")) {
                        t = t.trim();
                        if (!t.isEmpty()) {
                            toolsList.add(t);
                        }
                    }
                }
                p.tools = toolsList;
                players.put(id, p);
                playerOrder.add(id);
            }

            int numAbysses = Integer.parseInt(sc.nextLine().trim());
            for (int i = 0; i < numAbysses; i++) {
                String[] parts = sc.nextLine().trim().split(";");
                if (parts.length != 2) {
                    throw new InvalidFileException("Formato inválido");
                }
                int subtype = Integer.parseInt(parts[0]);
                int pos = Integer.parseInt(parts[1]);
                if (pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Formato inválido");
                }
                Abyss a = createAbyss(subtype, pos);
                if (a == null) {
                    throw new InvalidFileException("Formato inválido");
                }
                abysses.put(pos, a);
            }

            int numTools = Integer.parseInt(sc.nextLine().trim());
            for (int i = 0; i < numTools; i++) {
                String[] parts = sc.nextLine().trim().split(";");
                if (parts.length != 2) {
                    throw new InvalidFileException("Formato inválido");
                }

                int subtype = Integer.parseInt(parts[0]);
                int pos = Integer.parseInt(parts[1]);
                if (pos < 1 || pos > wSize) {
                    throw new InvalidFileException("Formato inválido");
                }

                Tool t = createTool(subtype, pos);
                if (t == null) {
                    throw new InvalidFileException("Formato inválido");
                }
                tools.put(pos, t);
            }
            Collections.sort(playerOrder);

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
        } catch (FileNotFoundException | InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileException("Formato inválido", e);
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
                out.println(a.getId() + ";" + a.getPosition());
            }

            out.println(tools.size());
            for (Tool t : tools.values()) {
                out.println(t.getId() + ";" + t.getPosition());
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
        if (playerOrder.isEmpty()) {
            return;
        }

        int attempts = 0;

        do {
            currentIdx = (currentIdx + 1) % playerOrder.size();
            Player candidate = players.get(playerOrder.get(currentIdx));

            if (candidate != null && !candidate.state.equals("Derrotado")) {
                break;
            }

            attempts++;
        } while (attempts < playerOrder.size());
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

    private Abyss createAbyss(int subtypeId, int position) {
        switch (subtypeId) {
            case 0: return new SyntaxErrorAbyss(position);
            case 1: return new LogicErrorAbyss(position);
            case 2: return new ExceptionAbyss(position);
            case 3: return new FileNotFoundExceptionAbyss(position);
            case 4: return new CrashAbyss(position);
            case 5: return new DuplicatedCodeAbyss(position);
            case 6: return new SideEffectsAbyss(position);
            case 7: return new BlueScreenOfDeathAbyss(position);
            case 8: return new InfiniteLoopAbyss(position);
            case 9: return new SegmentationFaultAbyss(position);
            default: return null;
        }
    }

    private Tool createTool(int subtypeId, int position) {
        switch (subtypeId) {
            case 0: return new HerancaTool(position);
            case 1: return new ProgramacaoFuncionalTool(position);
            case 2: return new UnitTestsTool(position);
            case 3: return new TratamentoExcepcoesTool(position);
            case 4: return new IDETool(position);
            case 5: return new AjudaDoProfessorTool(position);
            default: return null;
        }
    }

    private boolean hasToolNamed(Player p, String... names) {
        for (String t : p.tools) {
            for (String n : names) {
                if (t.equalsIgnoreCase(n)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean playerHasToolForAbyss(Player p, Abyss abyss) {
        int id = abyss.getId();

        if (id == 0) {
            return hasToolNamed(p, "IDE");
        }

        if (id == 1) {
            return hasToolNamed(p, "Testes Unitários");
        }

        if (id == 2 || id == 3) {
            return hasToolNamed(p, "Tratamento de Excepções", "Tratamento de Excecoes");
        }

        if (id == 5 || id == 6 || id == 8) {
            return hasToolNamed(p, "Programação Funcional");
        }
        return false;
    }


    private void consumeToolForAbyss(Player p, Abyss abyss) {
        int id = abyss.getId();

        if (id == 0) {
            p.tools.removeIf(t -> t.equalsIgnoreCase("IDE"));
        } else if (id == 1) {
            p.tools.removeIf(t -> t.equalsIgnoreCase("Testes Unitários"));
        } else if (id == 2 || id == 3) {
            p.tools.removeIf(t -> t.equalsIgnoreCase("Tratamento de Excepções"));
        } else if (id == 5 || id == 6 || id == 8) {
            p.tools.removeIf(t -> t.equalsIgnoreCase("Programação Funcional"));
        }
    }
}
