package pt.ulusofona.lp2.greatprogrammingjourney;

public class UnitTestsTool extends Tool {

    public UnitTestsTool(int position) {
        super(2, "Testes Unitários", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}

