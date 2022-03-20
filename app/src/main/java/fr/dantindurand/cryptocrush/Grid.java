package fr.dantindurand.cryptocrush;

public class Grid extends Game {
    protected int collumns;
    protected int rows;
    private int displacements_max;
    private int displacements;

    private void defineGrid(int collumnsNb, int rowsNb) {
        collumns = collumnsNb;
        rows = rowsNb;
    }

    private void addDisplacement(int displacementsNb) {
        displacements += displacementsNb;
    }
}
