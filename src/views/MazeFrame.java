package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MazeFrame extends JFrame {

    private MazePanel mazePanel;
    private JButton btnGenerateMaze;
    private JButton btnSolveMaze;
    private JButton btnVisualizeStepByStep;
    private JButton btnClearResults;
    private JButton btnViewResults;
    private JButton btnGuardar; 

    private JButton btnStartMode;
    private JButton btnEndMode;
    private JButton btnWallMode;
    private JButton btnEraseMode;

    private JComboBox<String> cmbAlgorithms;
    private JLabel lblStatus;
    private JSpinner spinnerRows;
    private JSpinner spinnerCols;
    private JSpinner spinnerDelay;

    public enum EditMode { NONE, START, END, WALL, ERASE }

    public MazeFrame() {
        setTitle("Maze Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        mazePanel = new MazePanel(20, 20);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(3, 1));

        JPanel topControls = new JPanel();
        btnGenerateMaze = new JButton("Generar Laberinto");
        btnSolveMaze = new JButton("Resolver Rápido");
        btnVisualizeStepByStep = new JButton("Visualizar Paso a Paso");
        btnClearResults = new JButton("Limpiar Resultados");
        btnViewResults = new JButton("Ver Resultados");
        btnGuardar = new JButton("Guardar Resultados"); 

        topControls.add(btnGenerateMaze);
        topControls.add(btnSolveMaze);
        topControls.add(btnVisualizeStepByStep);
        topControls.add(btnClearResults);
        topControls.add(btnViewResults);
        topControls.add(btnGuardar); 

        JPanel editModePanel = new JPanel();
        btnStartMode = new JButton("Colocar Inicio");
        btnEndMode = new JButton("Colocar Fin");
        btnWallMode = new JButton("Colocar Muros");
        btnEraseMode = new JButton("Borrar");
        editModePanel.add(btnStartMode);
        editModePanel.add(btnEndMode);
        editModePanel.add(btnWallMode);
        editModePanel.add(btnEraseMode);
        

        JPanel bottomControls = new JPanel();
        bottomControls.add(new JLabel("Filas:"));
        spinnerRows = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        bottomControls.add(spinnerRows);

        bottomControls.add(new JLabel("Columnas:"));
        spinnerCols = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        bottomControls.add(spinnerCols);

        bottomControls.add(new JLabel("Retraso Visualización (ms):"));
        spinnerDelay = new JSpinner(new SpinnerNumberModel(100, 0, 2000, 50));
        bottomControls.add(spinnerDelay);

        bottomControls.add(new JLabel("Algoritmo:"));
        cmbAlgorithms = new JComboBox<>(new String[] {
            "MazeSolverBFS",
            "MazeSolverDFS",
            "MazeSolverRecursivo",
            "MazeSolverDFSCompleto",
            "MazeSolverRecursivoCompleto",
            "MazeSolverRecursivoCompletoBT"
        });
        bottomControls.add(cmbAlgorithms);

        // Panel para status
        lblStatus = new JLabel("Estado: Listo");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(lblStatus, BorderLayout.CENTER);

       
        controlsPanel.add(topControls);
        controlsPanel.add(editModePanel);
        controlsPanel.add(bottomControls);


        add(mazePanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Getters para que el controlador acceda

    public MazePanel getMazePanel() {
        return mazePanel;
    }

    public int getNumRows() {
        return (int) spinnerRows.getValue();
    }

    public int getNumCols() {
        return (int) spinnerCols.getValue();
    }

    public int getVisualizationDelay() {
        return (int) spinnerDelay.getValue();
    }

    public String getSelectedAlgorithm() {
        return (String) cmbAlgorithms.getSelectedItem();
    }

    // Setters para listeners, que el controlador usará

    public void setGenerateMazeButtonListener(ActionListener listener) {
        btnGenerateMaze.addActionListener(listener);
    }

    public void setSolveMazeButtonListener(ActionListener listener) {
        btnSolveMaze.addActionListener(listener);
    }

    public void setVisualizeStepByStepButtonListener(ActionListener listener) {
        btnVisualizeStepByStep.addActionListener(listener);
    }

    public void setClearResultsButtonListener(ActionListener listener) {
        btnClearResults.addActionListener(listener);
    }

    public void setViewResultsButtonListener(ActionListener listener) {
        btnViewResults.addActionListener(listener);
    }

    public void setGuardarButtonListener(ActionListener listener) { // <-- NUEVO
        btnGuardar.addActionListener(listener);
    }

    public void setStartModeButtonListener(ActionListener listener) {
        btnStartMode.addActionListener(listener);
    }

    public void setEndModeButtonListener(ActionListener listener) {
        btnEndMode.addActionListener(listener);
    }

    public void setWallModeButtonListener(ActionListener listener) {
        btnWallMode.addActionListener(listener);
    }

    public void setEraseModeButtonListener(ActionListener listener) {
        btnEraseMode.addActionListener(listener);
    }

    public void setButtonsEnabled(boolean enabled) {
        btnGenerateMaze.setEnabled(enabled);
        btnSolveMaze.setEnabled(enabled);
        btnVisualizeStepByStep.setEnabled(enabled);
        btnClearResults.setEnabled(enabled);
        btnViewResults.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
        btnStartMode.setEnabled(enabled);
        btnEndMode.setEnabled(enabled);
        btnWallMode.setEnabled(enabled);
        btnEraseMode.setEnabled(enabled);
    }

    public void updateStatus(String message) {
        lblStatus.setText("Estado: " + message);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
