package net.sourceforge.jibs.backgammon;

public class Move {
    public int destination;
    public int source;

    public Move(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }
}
