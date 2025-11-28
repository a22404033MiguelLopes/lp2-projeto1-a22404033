package pt.ulusofona.lp2.greatprogrammingjourney;

public class InfiniteLoopAbyss extends Abyss {

    public InfiniteLoopAbyss(int position) {
        super(8, "Ciclo Infinito", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {

        boolean hasIDE = false;
        for (String t : p.tools) {
            if (t.equalsIgnoreCase("IDE")) {
                hasIDE = true;
                break;
            }
        }
        if (hasIDE) {
            return "O programador " + p.name + " evitou ficar preso num Ciclo Infinito graças à IDE.";
        }

        for (Player other : gm.getPlayersAt(position)) {
            if (other != p && other.state.equals("Preso")) {
                other.state = "Em Jogo";
            }
        }
        p.state = "Preso";
        return "O programador " + p.name + " ficou preso devido a um Ciclo Infinito.";
    }
}
