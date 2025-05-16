package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class BezierEditor extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private final ArrayList<Point> points = new ArrayList<>();
    private int dragIndex = -1;
    private boolean useBSpline = false;

    public BezierEditor() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);

        if (useBSpline && points.size() >= 4) {
            drawBSpline(g2);
        } else {
            for (int i = 0; i + 3 < points.size(); i += 3) {
                Point p0 = points.get(i);
                Point p1 = points.get(i + 1);
                Point p2 = points.get(i + 2);
                Point p3 = points.get(i + 3);

                CubicCurve2D curve = new CubicCurve2D.Float();
                curve.setCurve(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
                g2.draw(curve);
            }
        }

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            g2.setColor(i % 3 == 0 ? Color.BLUE : Color.RED);
            g2.fillOval(p.x - 5, p.y - 5, 10, 10);
        }

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Mode: " + (useBSpline ? "B-spline" : "Bezier"), 10, 20);
        g2.drawString("Left click: drag | Right click: add | Delete: remove | B: toggle mode", 10, 40);
    }

    private void drawBSpline(Graphics2D g2) {
        for (int i = 0; i <= points.size() - 4; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);
            Point p2 = points.get(i + 2);
            Point p3 = points.get(i + 3);
            Point prev = null;
            for (double t = 0; t <= 1.0; t += 0.02) {
                double x = (
                        (-t * t * t + 3 * t * t - 3 * t + 1) * p0.x +
                                (3 * t * t * t - 6 * t * t + 4) * p1.x +
                                (-3 * t * t * t + 3 * t * t + 3 * t + 1) * p2.x +
                                (t * t * t) * p3.x
                ) / 6.0;
                double y = (
                        (-t * t * t + 3 * t * t - 3 * t + 1) * p0.y +
                                (3 * t * t * t - 6 * t * t + 4) * p1.y +
                                (-3 * t * t * t + 3 * t * t + 3 * t + 1) * p2.y +
                                (t * t * t) * p3.y
                ) / 6.0;
                Point current = new Point((int) x, (int) y);
                if (prev != null) g2.drawLine(prev.x, prev.y, current.x, current.y);
                prev = current;
            }
        }
    }

    private Point getMousePosition(MouseEvent e) {
        return new Point(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        requestFocusInWindow();
        Point mouse = getMousePosition(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            points.add(mouse);
            repaint();
            return;
        }
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (mouse.distance(p) < 10) {
                dragIndex = i;
                break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragIndex != -1) {
            points.set(dragIndex, getMousePosition(e));
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragIndex = -1;
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && !points.isEmpty()) {
            points.remove(points.size() - 1);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_B) {
            useBSpline = !useBSpline;
            repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bezier/B-spline Curve Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new BezierEditor());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
