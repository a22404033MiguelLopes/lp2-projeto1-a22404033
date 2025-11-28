package pt.ulusofona.lp2.greatprogrammingjourney;

public class SideEffectsAbyss extends Abyss {

    public SideEffectsAbyss(int position) {
        super(6, "Efeitos secundários", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        int alvo = (p.lastLastPos <= 0) ? 1 : p.lastLastPos;
        p.pos = alvo;
        return "O jogador " + p.name + " voltou à posição de há 2 jogadas.";
    }
}

