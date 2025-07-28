package views;

import models.Cell;
import models.CellState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MazePanel extends JPanel {

    private Cell[][] maze;
    private int cellSize = 25;
    private Cell startCell;
    private Cell endCell;

    // Listener para clics en celdas (lo define el controlador)
    public interface MazePanelClickListener {
        void onCellClicked(int row, int col, int mouseButton);
    }

    private MazePanelClickListener clickListener;

    public MazePanel(int rows, int cols) {
        setPreferredSize(new Dimension(cols * cellSize, rows * cellSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickListener != null && maze != null) {
                    int col = e.getX() / cellSize;
                    int row = e.getY() / cellSize;
                    if (row >= 0 && row < maze.length && col >= 0 && col < maze[0].length) {
                        clickListener.onCellClicked(row, col, e.getButton());
                    }
                }
            }
        });
    }

    public void setMaze(Cell[][] maze) {
        this.maze = maze;
        if (maze != null) {
            setPreferredSize(new Dimension(maze[0].length * cellSize, maze.length * cellSize));
            revalidate();
            repaint();
        }
    }

    public void setStartCell(Cell cell) {
        this.startCell = cell;
    }

    public void setEndCell(Cell cell) {
        this.endCell = cell;
    }

    public void setMazePanelClickListener(MazePanelClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (maze == null) return;

        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                Cell cell = maze[r][c];
                Color color;

                switch (cell.getState()) {
                    case WALL: color = Color.BLACK; break;
                    case PATH: color = Color.WHITE; break;
                    case START: color = Color.GREEN; break;
                    case END: color = Color.RED; break;
                    case VISITED: color = Color.CYAN; break;
                    case SOLUTION: color = Color.YELLOW; break;
                    default: color = Color.WHITE; break;
                }

                g.setColor(color);
                g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
                g.setColor(Color.GRAY);
                g.drawRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }
    }
}
