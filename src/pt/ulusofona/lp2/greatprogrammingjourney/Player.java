package pt.ulusofona.lp2.greatprogrammingjourney;

import java.util.ArrayList;

class Player {
    final int id;
    String name;
    String colorLower;
    ArrayList<String> langs;
    int pos;
    int lastPos;
    int lastLastPos;
    String state;
    ArrayList<String> tools = new ArrayList<>();

    Player(int id, String name, String colorLower, ArrayList<String> langs) {
        this.id = id;
        this.name = name;
        this.colorLower = colorLower;
        this.langs = (langs != null) ? langs : new ArrayList<>();
        this.pos = 1;
        this.lastPos = 1;
        this.lastLastPos = 1;
        this.state = "Em Jogo";
    }

}
