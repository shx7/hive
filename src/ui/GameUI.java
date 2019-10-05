package ui;

import com.sun.istack.internal.NotNull;
import math.Point;
import math.Vector;
import model.GameModel;
import model.HexIndex;
import model.units.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class GameUI {
    private final static Dimension WINDOW_DIMENSION = new Dimension(640, 480);
    private final static java.awt.Point FRAME_POSITION = new java.awt.Point(200, 200);
    private static final Color SELECTED_COLOR = new Color(68, 200, 13);
    private static final Color EMPTY_HEX_COLOR = new Color(145, 159, 192);
    private static final Color DUMMY_UNIT_COLOR = new Color(21, 122, 202);

    private final static int HEX_SIDE_SIZE = 14;
    private final static java.awt.Point GAME_FIELD_POSITION = new java.awt.Point(100, 100);
    private final static double L_cos30 = HEX_SIDE_SIZE * Math.cos(Math.PI / 6.0);
    private final static double L_sin30 = HEX_SIDE_SIZE * Math.sin(Math.PI / 6.0);

    private final JFrame myAppFrame;
    private final JPanel myGamePanel;

    private final GameModel myGameModel;
    private final PlayerActionListener myPlayerActionListener;

    public GameUI(@NotNull GameModel field, @NotNull PlayerActionListener myPlayerActionListener) {
        this.myGameModel = field;
        this.myPlayerActionListener = myPlayerActionListener;

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
        for (HexIndex hexIndex : myGameModel.getHexIndices()) {
            boolean isSelected = hexIndex.equals(myGameModel.getSelectedHex());
            Unit unit = myGameModel.getUnit(hexIndex);
            final Color color;
            if (isSelected) {
                color = SELECTED_COLOR;
            }
            else {
                color = unit != null ? DUMMY_UNIT_COLOR : EMPTY_HEX_COLOR;
            }
            drawHex(g, hexIndex, color);
        }
    }

    private void setupMouseListener(@NotNull JPanel panel) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("mouseClicked " + e);
                HexIndex hexIndex = getHexIndexByMousePosition(e.getX(), e.getY());
                if (hexIndex != null) {
                    System.out.println("Hex with index clicked " + hexIndex);
                    myPlayerActionListener.clickedHex(hexIndex);
                }
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

    private void drawHex(@NotNull Graphics g, @NotNull HexIndex hexIndex, @NotNull Color color) {
        Point coordinates = hexIndexToCoordinates(hexIndex);
        drawHex(g, (int)coordinates.x, (int)coordinates.y, color);
    }

    private static void drawHex(@NotNull Graphics g, int x, int y, @NotNull Color color) {
        g.setColor(color);
        Point[] hexPoints = calculateHexPoints(x, y);
        Point previousPoint = hexPoints[hexPoints.length - 1];
        for (Point hexPoint : hexPoints) {
            g.drawLine((int)previousPoint.x, (int)previousPoint.y, (int)hexPoint.x, (int)hexPoint.y);
            previousPoint = hexPoint;
        }
    }

    @NotNull
    private static Point[] calculateHexPoints(double x, double y) {
        int L = HEX_SIDE_SIZE;
        return new Point[] {
                Point.create(x, y - L),
                Point.create(x + L_cos30, y - L_sin30),
                Point.create(x + L_cos30, y + L_sin30),
                Point.create(x, y + L),
                Point.create(x - L_cos30, y + L_sin30),
                Point.create(x - L_cos30, y - L_sin30),
        };
    }

    @NotNull
    private HexIndex getHexIndexByMousePosition(int x, int y) {
        for (HexIndex hexIndex : myGameModel.getHexIndices()) {
            if (isHexOverMouse(hexIndex, x, y)) {
                return hexIndex;
            }
        }
        return null;
    }

    @NotNull
    private static Point hexIndexToCoordinates(@NotNull HexIndex hexIndex) {
        int indexP = hexIndex.p;
        int indexQ = hexIndex.q;
        boolean isEvenRow = indexQ % 2 == 0;
        double offsetX = isEvenRow ? 0 : L_cos30;
        double stepY = 1.5 * HEX_SIDE_SIZE;
        double stepX = 2 * L_cos30;
        double centerX = indexP * stepX + offsetX;
        double centerY = indexQ * stepY;
        return Point.create((int) Math.ceil(GAME_FIELD_POSITION.x + centerX),
                (int) Math.ceil(GAME_FIELD_POSITION.y + centerY));
    }

    private boolean isHexOverMouse(@NotNull HexIndex hexIndex, int x, int y) {
        Point center = hexIndexToCoordinates(hexIndex);
        Point mousePoint = Point.create(x, y);
        Point[] points = calculateHexPoints(center.x, center.y);

        Point lastPoint = points[points.length - 1];
        for (Point point : points) {
            Vector centerToMouse = Vector.create(center, mousePoint);
            double crossProductAlpha = Vector.create(center, lastPoint).pseudoscalarProduct(centerToMouse);
            double crossProductBeta = Vector.create(center, point).pseudoscalarProduct(centerToMouse);
            if (crossProductAlpha >= 0 && crossProductBeta <= 0) {
                return Vector.create(lastPoint, point).pseudoscalarProduct(Vector.create(lastPoint, mousePoint)) >= 0;
            }
            lastPoint = point;
        }
        return false;
    }
}
