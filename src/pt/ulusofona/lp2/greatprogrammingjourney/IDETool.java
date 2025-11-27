package pt.ulusofona.lp2.greatprogrammingjourney;

public class IDETool extends Tool {

    public IDETool(int position) {
        super(4, "IDE", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}
