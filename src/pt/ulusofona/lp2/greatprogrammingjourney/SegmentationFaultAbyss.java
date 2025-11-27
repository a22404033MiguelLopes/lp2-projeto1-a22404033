package pt.ulusofona.lp2.greatprogrammingjourney;

import java.util.List;

public class SegmentationFaultAbyss extends Abyss {

    public SegmentationFaultAbyss(int position) {
        super(9, "Segmentation Fault", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        List<Player> jogadores = gm.getPlayersAt(position);

        if (jogadores.size() < 2) {
            return "Nada aconteceu porque só existe um programador na casa para o Segmentation Fault.";
        }

        for (Player pl : jogadores) {
            pl.pos = Math.max(1, pl.pos - 3);
        }

        return "Segmentation Fault! Todos os programadores nesta casa recuaram 3 casas.";
    }
}
