package pt.ulusofona.lp2.greatprogrammingjourney;

public class TratamentoExcepcoesTool extends Tool {

    public TratamentoExcepcoesTool(int position) {
        super(3, "Tratamento de Excepções", position);
    }

    @Override
    public boolean protects(Abyss abyss) {
        return false;
    }
}
