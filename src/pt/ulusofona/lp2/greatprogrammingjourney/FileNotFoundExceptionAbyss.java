package pt.ulusofona.lp2.greatprogrammingjourney;

public class FileNotFoundExceptionAbyss extends Abyss {

    public FileNotFoundExceptionAbyss(int position) {
        super(3, "FileNotFoundException", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        int novaPos = Math.max(1, p.pos - 3);
        p.pos = novaPos;
        return "O programador " + p.name + " recuou 3 casas devido a um FileNotFoundException.";
    }
}
