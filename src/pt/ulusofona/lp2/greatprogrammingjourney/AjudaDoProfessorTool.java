package pt.ulusofona.lp2.greatprogrammingjourney;

public class AjudaDoProfessorTool extends Tool {

    public AjudaDoProfessorTool(int position) {
        super(5, "Ajuda Do Professor", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}
