package pt.ulusofona.lp2.greatprogrammingjourney;

public class InfiniteLoopAbyss extends Abyss {

    public InfiniteLoopAbyss(int position) {
        super(8, "Ciclo Infinito", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        p.state = "Preso";
        return "O programador " + p.name + " ficou preso devido a um Ciclo Infinito.";
    }
}
