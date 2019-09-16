package ui;

import com.sun.istack.internal.NotNull;
import model.GameField;
import model.HexIndex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class GameUI {
    private final static Dimension WINDOW_DIMENSION = new Dimension(640, 480);
    private final static Point FRAME_POSITION = new Point(200, 200);

    private final static int HEX_SIDE_SIZE = 14;
    private final static Point GAME_FIELD_POSITION = new Point(100, 100);
    private final static double L_cos30 = HEX_SIDE_SIZE * Math.cos(Math.PI / 6.0);
    private final static double L_sin30 = HEX_SIDE_SIZE * Math.sin(Math.PI / 6.0);

    private final JFrame myAppFrame;
    private final JPanel myGamePanel;

    private final GameField myGameField;

    public GameUI(@NotNull GameField field) {
        this.myGameField = field;
        JPanel gamePanel = createBoardPanel();
        JFrame frame = new JFrame("Hive");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.setSize(WINDOW_DIMENSION);
        frame.setLocation(FRAME_POSITION);
        frame.setVisible(true);

        this.myAppFrame = frame;
        this.myGamePanel = gamePanel;
    }

    public void repaintGameField() {
        myGamePanel.repaint();
    }

    @NotNull
    private JPanel createBoardPanel() {
        final JPanel result = new JPanel(true) {
            @Override
            protected void paintComponent(@NotNull Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
                drawGameField(g);
            }
        };
        setupMouseListener(result);
        return result;
    }

    private void drawGameField(Graphics g) {
        // Needed to avoid drawing same lines twice, because it leads to some
        // lines becoming think while others normal
        for (HexIndex hexIndex : myGameField.getHexIndices()) {
            drawHex(g, hexIndex);
        }
    }

    private static void setupMouseListener(@NotNull JPanel panel) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("mouseClicked " + e);
            }

            @Override
            public void mouseReleased(@NotNull MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("mouseReleased " + e);
            }

            @Override
            public void mouseDragged(@NotNull MouseEvent e) {
                super.mouseDragged(e);
                System.out.println("mouseDragged " + e);
            }
        };
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
    }

    private static void drawHex(@NotNull Graphics g, @NotNull HexIndex hexIndex) {
        int indexP = hexIndex.p;
        int indexQ = hexIndex.q;
        boolean isEvenRow = indexQ % 2 == 0;
        double offsetX = isEvenRow ? 0 : L_cos30;
        double stepY = 1.5 * HEX_SIDE_SIZE;
        double stepX = 2 * L_cos30;
        double centerX = indexP * stepX + offsetX;
        double centerY = indexQ * stepY;
        drawHex(g, (int) Math.ceil(GAME_FIELD_POSITION.x + centerX), (int) Math.ceil(GAME_FIELD_POSITION.y + centerY));
    }

    private static void drawHex(@NotNull Graphics g, int x, int y) {
        g.setColor(new Color(145, 159, 192));
        Point[] hexPoints = calculateHexPoints(x, y);
        Point previousPoint = hexPoints[hexPoints.length - 1];
        for (Point hexPoint : hexPoints) {
            System.out.println("" + previousPoint + hexPoint);
            g.drawLine(previousPoint.x, previousPoint.y, hexPoint.x, hexPoint.y);
            previousPoint = hexPoint;
        }
    }

    @NotNull
    private static Point[] calculateHexPoints(int x, int y) {
        int L = HEX_SIDE_SIZE;
        return new Point[] {
                new Point(x, y - L),
                new Point((int)(x + L_cos30), (int)(y - L_sin30)),
                new Point((int)(x + L_cos30), (int)(y + L_sin30)),
                new Point(x, y + L),
                new Point((int)(x - L_cos30), (int)(y + L_sin30)),
                new Point((int)(x - L_cos30), (int)(y - L_sin30)),
        };
    }
}
