package models;

import java.util.Objects;

public class Cell {
    private int row;
    private int col;
    private CellState state;
    private Cell parent;      // útil para backtracking y pathfinding
    private int distance;     // distancia desde el inicio, opcional para algunos algoritmos

    public Cell(int row, int col, CellState state) {
        this.row = row;
        this.col = col;
        this.state = state;
        this.parent = null;
        this.distance = -1;
    }

    // Getters y setters
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public CellState getState() { return state; }
    public void setState(CellState state) { this.state = state; }

    public Cell getParent() { return parent; }
    public void setParent(Cell parent) { this.parent = parent; }

    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }

    // Métodos para saber el tipo de celda
    public boolean isWall() { return state == CellState.WALL; }
    public boolean isStart() { return state == CellState.START; }
    public boolean isEnd() { return state == CellState.END; }
    public boolean isVisited() { return state == CellState.VISITED; }
    public boolean isSolution() { return state == CellState.SOLUTION; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                ", state=" + state +
                '}';
    }
}
