package models;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class SolveResults implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    private List<Cell> path;
    private long timeTakenMillis;
    private boolean solved;
    private Exception exception; // Para capturar errores en la resolución

    // Constructor para resultados exitosos
    public SolveResults(List<Cell> path, long timeTakenMillis, boolean solved) {
        this.path = path != null ? path : new ArrayList<>();
        this.timeTakenMillis = timeTakenMillis;
        this.solved = solved;
        this.exception = null;
    }

    // Constructor para resultados con error (por ejemplo, si doInBackground falla)
    public SolveResults(Exception exception, long timeTakenMillis) {
        this.path = new ArrayList<>(); // Lista vacía si hubo error
        this.timeTakenMillis = timeTakenMillis;
        this.solved = false;
        this.exception = exception;
    }

    // Getters
    public List<Cell> getPath() {
        return path;
    }

    public long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public boolean isSolved() {
        return solved;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "SolveResults{" +
                "pathLength=" + (path != null ? path.size() : 0) +
                ", timeTakenMillis=" + timeTakenMillis +
                ", solved=" + solved +
                ", exception=" + (exception != null ? exception.getMessage() : "none") +
                '}';
    }
}
