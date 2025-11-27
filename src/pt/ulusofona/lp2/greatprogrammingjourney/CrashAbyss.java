package pt.ulusofona.lp2.greatprogrammingjourney;

public class CrashAbyss extends Abyss {

    public CrashAbyss(int position) {
        super(4, "Crash", position);
    }

    @Override
    public String applyEffect(Player p, GameManager gm, int dice) {
        p.pos = 1;
        return "O programador " + p.name + " voltou ao início devido a um Crash.";
    }
}
