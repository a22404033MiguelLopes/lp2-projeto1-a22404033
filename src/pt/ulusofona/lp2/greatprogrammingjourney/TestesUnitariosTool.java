package pt.ulusofona.lp2.greatprogrammingjourney;

public class TestesUnitariosTool extends Tool {

    public TestesUnitariosTool(int position) {
        super(2, "Testes Unitários", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}
