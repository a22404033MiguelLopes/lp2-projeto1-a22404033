package pt.ulusofona.lp2.greatprogrammingjourney;

public class ExceptionAbyss extends Abyss {

    public ExceptionAbyss(int position) {
        super(2, "Exception", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        int novaPos = Math.max(1, p.pos - 2);
        p.pos = novaPos;
        return "O programador " + p.name + " recuou 2 casas devido a uma Exception.";
    }
}
