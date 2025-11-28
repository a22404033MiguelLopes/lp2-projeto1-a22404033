package pt.ulusofona.lp2.greatprogrammingjourney;

public class HerancaTool extends Tool {

    public HerancaTool(int position) {
        super(0, "Herança", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}

