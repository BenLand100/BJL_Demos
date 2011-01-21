/**
 *  Copyright 2010 by Benjamin J. Land (a.k.a. BenLand100)
 *
 *  This file is part of BJL_Demos.
 *
 *  BJL_Demos is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BJL_Demos is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BJL_Demos. If not, see <http://www.gnu.org/licenses/>.
 */

package kdimensional;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

/**
 * Contains the code for a very nice (if i do say so myself) demo of a kdTree
 * nearest neighbor search.
 *
 * @author benland100
 */
public class kTreeView extends JPanel {

    protected Point[] points = null;
    protected int count = 100, min = 0, max = 1000;
    protected kdTree tree = null;
    protected AffineTransform comp = null, view = null;
    protected AffineTransform tx = AffineTransform.getScaleInstance(1 / 1000D, 1 / 1000D);

    public kTreeView() {
        super();
        setGenValues(100, 0, 1000);
        setView(0, 0, 1000, 1000);
        recalc();
    }

    protected void paintView(Graphics g) {
    }

    public void paint(Graphics g) {
        int w = getWidth(), h = getHeight();
        comp = AffineTransform.getScaleInstance(w, h);
        comp.concatenate(tx);
        try {
            view = comp.createInverse();
        } catch (NoninvertibleTransformException ex) {
            view = null;
        }
        if (tree == null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
            drawNode(tree.getTreeRoot(), 0, 0, 0, w, h, g);
            paintView(g);
        }
    }

    public kdPoint txView(Point pt) {
        Point2D tx = new Point2D.Double();
        view.transform(new Point2D.Double(pt.x, pt.y), tx);
        return new kdPoint(tx.getX(), tx.getY());
    }

    public Point txComp(kdPoint pt) {
        Point2D tx = new Point2D.Double();
        comp.transform(new Point2D.Double(pt.mag[0], pt.mag[1]), tx);
        return new Point((int) Math.round(tx.getX()), (int) Math.round(tx.getY()));
    }

    protected void drawNode(kdTree.TreeNode node, int depth, int sx, int sy, int ex, int ey, Graphics g) {
        Point2D pt = new Point2D.Double();
        comp.transform(new Point2D.Double(node.data.point.mag[0], node.data.point.mag[1]), pt);
        switch (depth % 2) {
            case 0:
                g.setColor(Color.BLUE);
                g.drawLine((int) pt.getX(), sy, (int) pt.getX(), ey);
                g.setColor(Color.RED);
                g.fillOval((int) pt.getX() - 2, (int) pt.getY() - 2, 5, 5);
                if (node.left != null) {
                    drawNode(node.left, depth + 1, sx, sy, (int) pt.getX(), ey, g);
                }
                if (node.right != null) {
                    drawNode(node.right, depth + 1, (int) pt.getX(), sy, ex, ey, g);
                }
                break;
            case 1:
                g.setColor(Color.BLUE);
                g.drawLine(sx, (int) pt.getY(), ex, (int) pt.getY());
                g.setColor(Color.RED);
                g.fillOval((int) pt.getX() - 2, (int) pt.getY() - 2, 5, 5);
                if (node.left != null) {
                    drawNode(node.left, depth + 1, sx, sy, ex, (int) pt.getY(), g);
                }
                if (node.right != null) {
                    drawNode(node.right, depth + 1, sx, (int) pt.getY(), ex, ey, g);
                }
                break;
        }
        //g.setColor(Color.YELLOW);
        //g.drawString(node.point.toString(), (int)pt.getX()+5, (int)pt.getY()+5);
    }

    public void genpts() {
        points = randomPoints(count, min, max);
        recalc();
    }

    protected void recalcView() {
    }

    public void recalc() {
        if (points == null) {
            return;
        }
        tree = new kdTree(points);
        recalcView();
    }

    public void setGenValues(int count, int min, int max) {
        this.count = count;
        this.min = min;
        this.max = max;
    }


    public void setView(int x, int y, int width, int height) {
        tx = AffineTransform.getScaleInstance(1D / (double) width, 1D / (double) height);
        tx.concatenate(AffineTransform.getTranslateInstance(-x, -y));
    }

    public static Point[] randomPoints(int num, int min, int max) {
        Point[] res = new Point[num];
        for (int i = 0; i < num; i++) {
            res[i] = new Point((int) (Math.random() * (max - min) + min), (int) (Math.random() * (max - min) + min));
        }
        return res;
    }


}
