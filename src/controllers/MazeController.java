package controllers;

import models.Cell;
import models.CellState;
import models.AlgorithmResult;
import models.SolveResults;
import views.MazeFrame;
import views.MazePanel;
import views.ResultadosDialog;
import dao.AlgorithmResultDAO;
import dao.daoImpl.AlgorithmResultDAOFile;
import solver.MazeSolver;
import solver.ProgressPublisher;
import solver.solverImpl.MazeSolverBFS;
import solver.solverImpl.MazeSolverDFS;
import solver.solverImpl.MazeSolverRecursivo;
import solver.solverImpl.MazeSolverRecursivoCompleto;
import solver.solverImpl.MazeSolverRecursivoCompletoBT;
import solver.solverImpl.MazeSolverDFSCompleto;
// Si NO quieres usar MazeSolverRecursivoCompletoBT, esta línea debe estar comentada o eliminada:
// import solver.solverImpl.MazeSolverRecursivoCompletoBT;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MazeController implements MazePanel.MazePanelClickListener {
    private MazeFrame view;
    private Cell[][] currentMaze;
    private Cell startCell;
    private Cell endCell;
    private AlgorithmResultDAO resultDAO;
    private Map<String, MazeSolver> solvers;
    
    private MazeFrame.EditMode currentEditMode;

    public MazeController(MazeFrame view) {
    this.view = view;
    this.resultDAO = new AlgorithmResultDAOFile();
    this.solvers = new HashMap<>();
    initializeSolvers();

    // Configurar todos los listeners de los botones
    setupListeners();

    // Listener adicional del botón Guardar
    view.setGuardarButtonListener(e -> guardarResultados());

    // Estado inicial
    this.currentEditMode = MazeFrame.EditMode.NONE;
    generateNewMaze(view.getNumRows(), view.getNumCols());
}

    private void initializeSolvers() {
        solvers.put("MazeSolverBFS", new MazeSolverBFS());
        solvers.put("MazeSolverDFS", new MazeSolverDFS());
        solvers.put("MazeSolverRecursivo", new MazeSolverRecursivo());
        solvers.put("MazeSolverDFSCompleto", new MazeSolverDFSCompleto());
        solvers.put("MazeSolverRecursivoCompleto", new MazeSolverRecursivoCompleto());
        solvers.put("MazeSolverRecursivoCompletoBT", new MazeSolverRecursivoCompletoBT());
        // solvers.put("MazeSolverRecursivoCompletoBT", new MazeSolverRecursivoCompletoBT());
    }

    private void setupListeners() {
        view.setGenerateMazeButtonListener(e -> generateNewMaze(view.getNumRows(), view.getNumCols()));
        view.setSolveMazeButtonListener(e -> solveMaze(false));
        view.setVisualizeStepByStepButtonListener(e -> solveMaze(true));
        view.setClearResultsButtonListener(e -> clearResults());
        view.setViewResultsButtonListener(e -> viewResults());
        view.getMazePanel().setMazePanelClickListener(this);

        view.setStartModeButtonListener(e -> {
            this.currentEditMode = MazeFrame.EditMode.START;
            view.updateStatus("Modo: Colocar Inicio (verde). Click izquierdo en celda.");
        });
        view.setEndModeButtonListener(e -> {
            this.currentEditMode = MazeFrame.EditMode.END;
            view.updateStatus("Modo: Colocar Fin (rojo). Click izquierdo en celda.");
        });
        view.setWallModeButtonListener(e -> {
            this.currentEditMode = MazeFrame.EditMode.WALL;
            view.updateStatus("Modo: Colocar Bloqueo (negro). Click izquierdo en celda.");
        });
        view.setEraseModeButtonListener(e -> {
            this.currentEditMode = MazeFrame.EditMode.ERASE;
            view.updateStatus("Modo: Borrar (blanco). Click izquierdo en celda.");
        });
    }

    private void generateNewMaze(int rows, int cols) {
        currentMaze = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                currentMaze[r][c] = new Cell(r, c, CellState.PATH);
            }
        }
        
        startCell = null;
        endCell = null;
        view.getMazePanel().setStartCell(null);
        view.getMazePanel().setEndCell(null);

        view.getMazePanel().setMaze(currentMaze);
        view.updateStatus("Laberinto " + rows + "x" + cols + " generado. Selecciona un modo de edición para empezar.");
    }

    @Override
    public void onCellClicked(int row, int col, int mouseButton) {
        if (mouseButton != MouseEvent.BUTTON1) {
            return;
        }

        Cell clickedCell = currentMaze[row][col];
        switch (this.currentEditMode) {
            case START:
                if (!clickedCell.isWall() && !clickedCell.isEnd()) {
                    if (startCell != null) {
                        startCell.setState(CellState.PATH);
                        view.getMazePanel().setStartCell(null);
                    }
                    clickedCell.setState(CellState.START);
                    startCell = clickedCell;
                    view.getMazePanel().setStartCell(startCell);
                    view.updateStatus("Inicio establecido en (" + row + ", " + col + ").");
                } else {
                    view.updateStatus("No puedes colocar el inicio en un muro o en la celda de fin.");
                }
                break;
            case END:
                if (!clickedCell.isWall() && !clickedCell.isStart()) {
                    if (endCell != null) {
                        endCell.setState(CellState.PATH);
                        view.getMazePanel().setEndCell(null);
                    }
                    clickedCell.setState(CellState.END);
                    endCell = clickedCell;
                    view.getMazePanel().setEndCell(endCell);
                    view.updateStatus("Fin establecido en (" + row + ", " + col + ").");
                } else {
                    view.updateStatus("No puedes colocar el fin en un muro o en la celda de inicio.");
                }
                break;
            case WALL:
                if (!clickedCell.isStart() && !clickedCell.isEnd()) {
                    clickedCell.setState(CellState.WALL);
                    view.updateStatus("Muro colocado en (" + row + ", " + col + ").");
                } else {
                    view.updateStatus("No puedes colocar un muro en la celda de inicio o fin.");
                }
                break;
            case ERASE:
                if (clickedCell.isStart()) {
                    clickedCell.setState(CellState.PATH);
                    startCell = null;
                    view.getMazePanel().setStartCell(null);
                    view.updateStatus("Inicio quitado de (" + row + ", " + col + ").");
                } else if (clickedCell.isEnd()) {
                    clickedCell.setState(CellState.PATH);
                    endCell = null;
                    view.getMazePanel().setEndCell(null);
                    view.updateStatus("Fin quitado de (" + row + ", " + col + ").");
                } else if (clickedCell.isWall() || clickedCell.isVisited() || clickedCell.isSolution()) {
                    clickedCell.setState(CellState.PATH);
                    view.updateStatus("Celda borrada en (" + row + ", " + col + ").");
                } else {
                    view.updateStatus("La celda (" + row + ", " + col + ") ya es un camino o no se puede borrar.");
                }
                break;
            case NONE:
                view.updateStatus("Selecciona un modo de edición (Inicio, Fin, Bloqueo, Borrar) para editar el laberinto.");
                break;
        }
        view.getMazePanel().repaint();
    }

    private void solveMaze(boolean visualize) {
        if (startCell == null || endCell == null) {
            view.showMessage("Por favor, establece el punto de inicio y fin del laberinto.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Limpiar el estado visual anterior del laberinto
        for (int r = 0; r < currentMaze.length; r++) {
            for (int c = 0; c < currentMaze[0].length; c++) {
                Cell cell = currentMaze[r][c];
                if (cell.getState() == CellState.VISITED || cell.getState() == CellState.SOLUTION) {
                    cell.setState(CellState.PATH);
                }
            }
        }
        view.getMazePanel().repaint(); // Repintar el laberinto limpio

        String selectedAlgorithmName = view.getSelectedAlgorithm();
        MazeSolver solver = solvers.get(selectedAlgorithmName);

        if (solver == null) {
            view.showMessage("Algoritmo '" + selectedAlgorithmName + "' no encontrado o no implementado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        view.updateStatus("Preparando " + (visualize ? "visualización" : "resolución rápida") + " con " + solver.getName() + "...");
        view.setButtonsEnabled(false); // Deshabilitar botones durante la resolución

        // Crear una copia del laberinto para que el solver no modifique el original
        int rows = currentMaze.length;
        int cols = currentMaze[0].length;
        Cell[][] mazeForSolver = new Cell[rows][cols];
        Cell clonedStartCell = null;
        Cell clonedEndCell = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell originalCell = currentMaze[r][c];
                mazeForSolver[r][c] = new Cell(r, c, originalCell.getState());
                
                if (originalCell.equals(startCell)) {
                    clonedStartCell = mazeForSolver[r][c];
                    mazeForSolver[r][c].setState(CellState.START);
                } else if (originalCell.equals(endCell)) {
                    clonedEndCell = mazeForSolver[r][c];
                    mazeForSolver[r][c].setState(CellState.END);
                }
            }
        }
        
        view.getMazePanel().setMaze(mazeForSolver);
        view.getMazePanel().setStartCell(clonedStartCell);
        view.getMazePanel().setEndCell(clonedEndCell);

        final String currentAlgorithmName = selectedAlgorithmName;
        final Cell[][] finalMazeForSolver = mazeForSolver;
        final Cell finalClonedStartCell = clonedStartCell;
        final Cell finalClonedEndCell = clonedEndCell;
        final int finalRows = rows;
        final int finalCols = cols;
        final int delay = view.getVisualizationDelay();

        // INICIO DE LA SECCIÓN CRÍTICA DEL SWINGWORKER
        SwingWorker<SolveResults, Cell> worker = new SwingWorker<SolveResults, Cell>() {
            // ¡¡¡¡ESTA LÍNEA ES FUNDAMENTAL Y DEBE ESTAR AQUÍ!!!!
            // Captura la referencia a 'this' (la instancia de SwingWorker) en una variable final.
            // Esto permite acceder a métodos protegidos de SwingWorker como publish() desde clases internas.
            private final SwingWorker<SolveResults, Cell> thisWorkerInstance = this;

            @Override
            protected SolveResults doInBackground() throws Exception {
                ProgressPublisher publisher = null;
                if (visualize) {
                    publisher = new ProgressPublisher() {
                        @Override
                        public void publish(Cell cell) {
                            // Llama al método publish() de la instancia del SwingWorker capturada.
                            thisWorkerInstance.publish(cell); 
                        }
                        @Override
                        public void publish(Cell... cells) {
                            // Llama al método publish() de la instancia del SwingWorker capturada.
                            thisWorkerInstance.publish(cells);
                        }
                    };
                }
                
                return solvers.get(currentAlgorithmName).solve(finalMazeForSolver, finalClonedStartCell, finalClonedEndCell, publisher);
            }

            @Override
            protected void process(List<Cell> chunks) {
                view.getMazePanel().repaint();
                if (visualize && delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    SolveResults results = get();

                    view.getMazePanel().repaint();

                    if (results.isSolved()) {
                        view.updateStatus("¡Laberinto resuelto! Longitud del camino: " + results.getPath().size() +
                                          " celdas. Tiempo: " + results.getTimeTakenMillis() + " ms.");
                        AlgorithmResult algoResult = new AlgorithmResult(
                            currentAlgorithmName,
                            results.getPath().size(),
                            results.getTimeTakenMillis(),
                            finalRows * finalCols
                        );
                        resultDAO.saveResult(algoResult);
                    } else {
                        String errorMessage = "No se encontró solución.";
                        String errorDetails = "";
                        if (results.getException() != null) {
                            errorMessage = "Error durante la resolución: " + results.getException().getClass().getSimpleName();
                            errorDetails = results.getException().getMessage();
                        }
                        view.updateStatus(errorMessage + " Tiempo: " + results.getTimeTakenMillis() + " ms.");
                        view.showMessage(errorMessage + (errorDetails.isEmpty() ? "" : ": " + errorDetails), "Sin Solución / Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    view.updateStatus("La resolución del laberinto fue interrumpida.");
                    view.showMessage("La operación fue interrumpida.", "Interrupción", JOptionPane.WARNING_MESSAGE);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    view.updateStatus("Error inesperado en la tarea de fondo: " + cause.getMessage());
                    view.showMessage("Un error inesperado ocurrió: " + cause.getClass().getSimpleName() + ": " + cause.getMessage(), "Error de Ejecución", JOptionPane.ERROR_MESSAGE);
                    cause.printStackTrace();
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void clearResults() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "¿Estás seguro de que quieres borrar todos los resultados?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            resultDAO.clearAllResults();
            view.updateStatus("Resultados guardados han sido limpiados.");
            view.showMessage("Todos los resultados han sido borrados.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    private BufferedImage generarGraficaImagen(List<AlgorithmResult> results) {
        int width = 800;
        int height = 600;
        int margin = 50;

        // Crear imagen vacía
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // Fondo blanco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Título
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Tiempos de Ejecución (ms)", margin, margin - 10);

        // Ejes
        int axisX = margin;
        int axisY = height - margin;
        int chartWidth = width - 2 * margin;
        int chartHeight = height - 2 * margin;

        g.setStroke(new BasicStroke(2));
        g.drawLine(axisX, axisY, axisX + chartWidth, axisY); // Eje X
        g.drawLine(axisX, axisY, axisX, axisY - chartHeight); // Eje Y

        // Calcular valor máximo
        long maxValue = 1;
        for (AlgorithmResult r : results) {
            long tiempo = obtenerTiempo(r);
            if (tiempo > maxValue) maxValue = tiempo;
        }

        // Dibujar barras
        int barWidth = chartWidth / results.size() - 20;
        int x = axisX + 10;
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        for (AlgorithmResult r : results) {
            long tiempo = obtenerTiempo(r);
            int barHeight = (int) ((tiempo * 1.0 / maxValue) * (chartHeight - 20));

            // Barra
            g.setColor(Color.BLUE);
            g.fillRect(x, axisY - barHeight, barWidth, barHeight);

            // Etiqueta algoritmo
            g.setColor(Color.BLACK);
            g.drawString(r.getAlgorithmName(), x, axisY + 20);

            // Etiqueta valor
            g.drawString(tiempo + " ms", x, axisY - barHeight - 5);

            x += barWidth + 20;
        }

        g.dispose();
        return image;
    }

    /**
     * Muestra el gráfico en pantalla y lo guarda como PNG
     */
    private void resultadosGraficaG(List<AlgorithmResult> results) {
        if (results.isEmpty()) return;

        try {
            BufferedImage image = generarGraficaImagen(results);

            // Guardar imagen en raíz del proyecto
            File out = new File("resultados_tiempo.png");
            ImageIO.write(image, "png", out);

            // Mostrar imagen en JOptionPane
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);
            JOptionPane.showMessageDialog(null, label, "Gráfico de Resultados", JOptionPane.PLAIN_MESSAGE);

            view.showMessage("Gráfico generado en: " + out.getAbsolutePath(),
                    "Imagen Creada", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Error generando imagen: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene el tiempo de ejecución de AlgorithmResult de forma compatible
     */
    private long obtenerTiempo(AlgorithmResult r) {
        try {
            return r.getTimeTakenMillis();
        } catch (NoSuchMethodError | Exception e1) {
            try {
                return (long) r.getClass().getMethod("getExecutionTimeMillis").invoke(r);
            } catch (Exception e2) {
                return 0;
            }
        }
    }

    /**
     * Visualizar resultados y gráfico
     */
    private void viewResults() {
        List<AlgorithmResult> allResults = resultDAO.getAllResults();
        if (allResults.isEmpty()) {
            view.showMessage("No hay resultados guardados para mostrar.",
                    "Resultados Vacíos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ResultadosDialog resultsDialog = new ResultadosDialog(view, allResults);
        resultsDialog.setVisible(true);

        resultadosGraficaG(allResults);

        view.updateStatus("Mostrando resultados y generando gráfico.");
    }

    /**
     * Guardar imagen y tabla en carpeta /resultados
     */
    private void guardarResultados() {
        List<AlgorithmResult> results = resultDAO.getAllResults();
        if (results.isEmpty()) {
            view.showMessage("No hay resultados para guardar.", "Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BufferedImage image = generarGraficaImagen(results);
        guardarImagen(image);
        guardarTablaResultados(results);
    }

    private File crearCarpetaReportes() {
        File carpeta = new File("resultados");
        if (!carpeta.exists()) carpeta.mkdir();
        return carpeta;
    }

    private void guardarImagen(BufferedImage image) {
        try {
            File carpeta = crearCarpetaReportes();
            File out = new File(carpeta, "grafico_tiempo.png");
            ImageIO.write(image, "png", out);
            view.showMessage("Imagen guardada en: " + out.getAbsolutePath(),
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            view.showMessage("Error guardando imagen: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarTablaResultados(List<AlgorithmResult> results) {
        try {
            File carpeta = crearCarpetaReportes();
            File out = new File(carpeta, "resultados.csv");
            try (PrintWriter pw = new PrintWriter(out)) {
                pw.println("Algoritmo, Longitud Camino, Tiempo(ms), Tamaño Laberinto");
                for (AlgorithmResult r : results) {
                    pw.println(r.getAlgorithmName() + "," +
                            r.getPathLength() + "," +
                            obtenerTiempo(r) + "," +
                            r.getMazeSize());
                }
            }
            view.showMessage("Resultados guardados en: " + out.getAbsolutePath(),
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            view.showMessage("Error guardando resultados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

}