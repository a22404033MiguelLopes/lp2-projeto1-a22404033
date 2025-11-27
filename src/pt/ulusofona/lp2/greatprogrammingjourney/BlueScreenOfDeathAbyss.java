package pt.ulusofona.lp2.greatprogrammingjourney;

public class BlueScreenOfDeathAbyss extends Abyss {

    public BlueScreenOfDeathAbyss(int position) {
        super(7, "Blue Screen of Death", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        p.state = "Derrotado";
        return "O programador " + p.name + " perdeu o jogo devido a Blue Screen of Death.";
    }
}
