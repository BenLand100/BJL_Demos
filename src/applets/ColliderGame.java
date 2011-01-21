/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applets;

import demos.collide.Mass;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.font.LineMetrics;
import java.util.TreeMap;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import kdimensional.kdMatrix;
import kdimensional.kdPoint;
import kdimensional.kdTree;

/**
 *
 * @author benland100
 */
public class ColliderGame extends JApplet {

    private static enum State {

        GAMEOVER, START, PLAYING
    };
    private static final int width = 500;
    private static final int height = 500;
    private static final int KEY_UP = 0x01;
    private static final int KEY_DOWN = 0x02;
    private static final int KEY_LEFT = 0x04;
    private static final int KEY_RIGHT = 0x08;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mass Collider Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new ColliderGame());
        frame.setVisible(true);
    }

    private class GameView extends JPanel {

        public GameView() {
            super();
            setFocusable(true);
            enableInputMethods(true);
            enableEvents(KeyEvent.KEY_EVENT_MASK);
        }

        public void processKeyEvent(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    if (event.getID() != KeyEvent.KEY_PRESSED) {
                        return;
                    }
                    if (state == State.START) {
                        rebuild();
                        score = 0;
                        state = State.PLAYING;
                    }
                    if (state == State.GAMEOVER) {
                        state = State.START;
                    }
                    break;
                case KeyEvent.VK_UP:
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keys |= KEY_UP;
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keys &= ~KEY_UP;
                            break;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keys |= KEY_LEFT;
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keys &= ~KEY_LEFT;
                            break;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keys |= KEY_RIGHT;
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keys &= ~KEY_RIGHT;
                            break;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            keys |= KEY_DOWN;
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keys &= ~KEY_DOWN;
                            break;
                    }
                    break;
            }
        }

        public void paint(Graphics g) {
            int w = getWidth(), h = getHeight();
            int sz = Math.min(w, h);
            int woff = (w - sz) / 2;
            int hoff = (h - sz) / 2;
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.BLACK);
            g.translate(woff, hoff);
            g.fillRect(0, 0, sz, sz);
            kdMatrix tx = new kdMatrix(2, 2, (double) sz / (double) width, 0, 0, (double) sz / (double) height);
            switch (state) {
                case START: {
                    LineMetrics metric = g.getFontMetrics().getLineMetrics("PRESS SPACE", g);
                    Graphics2D gfx = (Graphics2D) g.create();
                    gfx.scale((double) sz / g.getFontMetrics().getStringBounds("PRESS SPACE", g).getWidth(), (double) sz / (metric.getAscent() + metric.getDescent()));
                    gfx.setColor(Color.GREEN);
                    gfx.drawString("PRESS SPACE", 0, (int) metric.getAscent());
                    gfx.dispose();
                }
                break;
                case GAMEOVER: {
                    LineMetrics metric = g.getFontMetrics().getLineMetrics("GAME OVER", g);
                    Graphics2D gfx = (Graphics2D) g.create();
                    gfx.scale((double) sz / g.getFontMetrics().getStringBounds("GAME OVER", g).getWidth(), (double) sz / (metric.getAscent() + metric.getDescent()));
                    gfx.setColor(Color.YELLOW);
                    gfx.drawString("GAME OVER", 0, (int) metric.getAscent());
                    gfx.dispose();
                }
                case PLAYING:
                    g.setColor(Color.MAGENTA);
                    g.drawString("Score: " + score, 5, 15);
                    g.setColor(Color.RED);
                    kdPoint r = tx.mul(new kdPoint(size, size));
                    kdPoint pt;
                    for (int i = 0; i < count; i++) {
                        pt = tx.mul(masses[i].center).sub(r);
                        g.fillOval((int) Math.round(pt.mag[0]), (int) Math.round(pt.mag[1]), (int) Math.round(r.mag[0] * 2), (int) Math.round(r.mag[0] * 2));
                    }
                    g.setColor(Color.BLUE);
                    r = tx.mul(new kdPoint(ball.size, ball.size));
                    pt = tx.mul(ball.center).sub(r);
                    g.fillOval((int) Math.round(pt.mag[0]), (int) Math.round(pt.mag[1]), (int) Math.round(r.mag[0] * 2), (int) Math.round(r.mag[0] * 2));
            }
        }
    }
    private State state = State.START;
    private int keys = 0;
    private kdTree tree = null;
    private Mass ball = null;
    Mass[] masses = null;
    kdPoint[] points = null;
    double mass = 1D;
    double size = 5D;
    int count = 25;
    double maxv = 5D;
    Timer timer;
    GameView view;
    long score;

    public ColliderGame() {
        ball = new Mass(250, 250, 1, 20);
        view = new GameView();
        add(view);
        rebuild();
        timer = new Timer(30, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                act();
                view.repaint();
            }
        });
        timer.setCoalesce(true);
        timer.start();
    }

    public synchronized void rebuild() {
        TreeMap<Double, kdTree.Entry<Mass>> closest;
        do {
            masses = new Mass[count];
            points = new kdPoint[count];
            for (int i = 0; i < count; i++) {
                masses[i] = new Mass(new kdPoint(Math.random() * width, Math.random() * height), new kdPoint(Math.random() * maxv * 2 - maxv, Math.random() * maxv * 2 - maxv), mass, size);
                points[i] = masses[i].center;
            }
            tree = new kdTree(points,masses);
            closest = tree.rangeSearch(size + ball.size*3, ball.center);
        } while (closest.size() > 0);
    }

    public synchronized void act() {
        switch (state) {
            case PLAYING:
                score++;
                if (masses == null || points == null) {
                    return;
                }
                if ((keys & KEY_UP) == 0) {
                    if ((keys & KEY_DOWN) != 0) {
                        if (ball.center.mag[1] < height - ball.size) {
                            ball.center.mag[1] += 5;
                        }
                    }
                } else {
                    if ((keys & KEY_DOWN) == 0) {
                        if (ball.center.mag[1] > ball.size) {
                            ball.center.mag[1] -= 5;
                        }
                    }
                }
                if ((keys & KEY_LEFT) == 0) {
                    if ((keys & KEY_RIGHT) != 0) {
                        if (ball.center.mag[0] < width - ball.size) {
                            ball.center.mag[0] += 5;
                        }
                    }
                } else {
                    if ((keys & KEY_RIGHT) == 0) {
                        if (ball.center.mag[0] > ball.size) {
                            ball.center.mag[0] -= 5;
                        }
                    }
                }
                tree = new kdTree(points, masses);
                TreeMap<Double, kdTree.Entry<Mass>> closest;
                for (int i = 0; i < masses.length; i++) {
                    Mass m = masses[i];
                    kdPoint center = m.center;

                    closest = tree.rangeSearch(2 * size, center);
                    if (closest.size() > 1) {
                        closest.remove(closest.firstKey());
                        for (kdTree.Entry<Mass> e : closest.values()) {
                            Mass x = e.data;
                            kdPoint col = center.sub(x.center);
                            if (col.len() > m.size + x.size) {
                                continue;
                            }
                            kdPoint norm = col.norm();
                            double a_mag = m.velocity.dot(norm);
                            double b_mag = x.velocity.dot(norm);
                            if (a_mag > 0 && b_mag < 0) {
                                continue; //already hit
                            }
                            kdPoint parallel_a = norm.mul(a_mag);
                            kdPoint parallel_b = norm.mul(b_mag);
                            kdPoint perpen_a = m.velocity.sub(parallel_a);
                            kdPoint perpen_b = x.velocity.sub(parallel_b);
                            m.velocity.set(parallel_b.mul(x.mass / m.mass).add(perpen_a));
                            x.velocity.set(parallel_a.mul(m.mass / x.mass).add(perpen_b));
                        }
                    }

                    if (center.mag[0] - m.size <= 0) {
                        m.velocity.mag[0] = Math.abs(m.velocity.mag[0]);
                    }
                    if (center.mag[0] + m.size >= width) {
                        m.velocity.mag[0] = -Math.abs(m.velocity.mag[0]);
                    }
                    if (center.mag[1] - m.size <= 0) {
                        m.velocity.mag[1] = Math.abs(m.velocity.mag[1]);
                    }
                    if (center.mag[1] + m.size >= height) {
                        m.velocity.mag[1] = -Math.abs(m.velocity.mag[1]);
                    }

                    center.mag[0] += m.velocity.mag[0];
                    center.mag[1] += m.velocity.mag[1];
                }
                closest = tree.rangeSearch(size + ball.size, ball.center);
                if (closest.size() > 0) {
                    state = State.GAMEOVER;
                }
                break;
        }
    }
}
