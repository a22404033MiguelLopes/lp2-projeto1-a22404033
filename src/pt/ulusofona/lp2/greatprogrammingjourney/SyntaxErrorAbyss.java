package pt.ulusofona.lp2.greatprogrammingjourney;

public class SyntaxErrorAbyss extends Abyss {

    public SyntaxErrorAbyss(int position) {
        super(0, "Erro de sintaxe", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        int novaPos = Math.max(1, p.pos - 1);
        p.pos = novaPos;
        return "O programador " + p.name + " recuou 1 casa devido a um Erro de sintaxe.";
    }
}
