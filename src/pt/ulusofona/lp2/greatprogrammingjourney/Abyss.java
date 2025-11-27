package pt.ulusofona.lp2.greatprogrammingjourney;

public abstract class Abyss {

    protected final int id;
    protected final String name;
    protected final int position;

    public Abyss(int id, String name, int position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public abstract String applyEffect(Player p, GameManager gm, int dice);
}
