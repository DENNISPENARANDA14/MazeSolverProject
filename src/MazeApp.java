import controllers.MazeController;
import views.MazeFrame;

import javax.swing.SwingUtilities;

public class MazeApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeFrame frame = new MazeFrame();
            MazeController controller = new MazeController(frame);
            frame.setVisible(true);
        });
    }
}