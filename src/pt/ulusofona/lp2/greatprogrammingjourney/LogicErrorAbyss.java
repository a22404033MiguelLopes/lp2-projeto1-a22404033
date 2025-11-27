package pt.ulusofona.lp2.greatprogrammingjourney;

public class LogicErrorAbyss extends Abyss {

    public LogicErrorAbyss(int position) {
        super(1, "Erro de lógica", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        int recuar = dice / 2;          // floor(dice / 2)
        int novaPos = Math.max(1, p.pos - recuar);
        p.pos = novaPos;
        return "O programador " + p.name + " recuou " + recuar +
                " casas devido a um Erro de lógica.";
    }
}
