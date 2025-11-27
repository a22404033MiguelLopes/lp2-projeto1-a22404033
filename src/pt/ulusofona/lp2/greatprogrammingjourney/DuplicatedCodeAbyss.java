package pt.ulusofona.lp2.greatprogrammingjourney;

public class DuplicatedCodeAbyss extends Abyss {

    public DuplicatedCodeAbyss(int position) {
        super(5, "Código duplicado", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        p.pos = p.lastPos;
        return "O programador " + p.name + " voltou à posição anterior devido a Código duplicado.";
    }
}
