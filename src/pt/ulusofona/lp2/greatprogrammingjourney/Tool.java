package pt.ulusofona.lp2.greatprogrammingjourney;

public abstract class Tool {

    protected final int id;
    protected final String name;
    protected final int position;

    public Tool(int id, String name, int position) {
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

    public abstract boolean protects(Abyss abyss);
}
