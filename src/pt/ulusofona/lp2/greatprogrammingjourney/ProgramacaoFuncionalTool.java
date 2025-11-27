package pt.ulusofona.lp2.greatprogrammingjourney;

public class ProgramacaoFuncionalTool extends Tool {

    public ProgramacaoFuncionalTool(int position) {
        super(1, "Programação Funcional", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return abyss.getId() == 5;
    }
}
