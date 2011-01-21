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
package demos.kdtree;

import kdimensional.kTreeView;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.TreeMap;
import kdimensional.kdPoint;
import kdimensional.kdTree;

/**
 * Contains the code for a very nice (if i do say so myself) demo of a kdTree
 * nearest neighbor search.
 *
 * @author benland100
 */
public class NNSearch extends kTreeView {

    private int find = 1;
    private kdPoint point = new kdPoint(500, 500);
    private TreeMap<Double, kdTree.Entry> kClosest = null;

    public NNSearch() {
        super();
    }

    public void paintView(Graphics g) {
        if (point != null) {
            Point2D target = new Point2D.Double(), dest = new Point2D.Double(), radius = new Point2D.Double();
            comp.transform(new Point2D.Double(point.mag[0], point.mag[1]), target);
            g.setColor(Color.GREEN);
            g.fillOval((int) target.getX() - 2, (int) target.getY() - 2, 5, 5);
            if (kClosest != null) {
                for (kdTree.Entry pt : kClosest.values()) {
                    comp.transform(new Point2D.Double(pt.point.mag[0], pt.point.mag[1]), dest);
                    double dist = pt.point.dist(point);
                    comp.transform(new Point2D.Double(dist, dist), radius);
                    g.setColor(Color.MAGENTA);
                    g.drawOval((int) (target.getX() - radius.getX()), (int) (target.getY() - radius.getY()), (int) (radius.getX() * 2D), (int) (radius.getY() * 2D));
                    g.setColor(Color.YELLOW);
                    g.fillOval((int) dest.getX() - 2, (int) dest.getY() - 2, 5, 5);
                    g.setColor(Color.GRAY);
                    g.drawLine((int) dest.getX(), (int) dest.getY(), (int) target.getX(), (int) target.getY());
                }
            }
        }
        g.setColor(Color.GREEN);
        g.drawString("Total Points: " + count, 5, 15);
        g.drawString("Distance Calcs: " + tree.lastComplexity(), 5, 30);
        g.drawString("Neighbors Found: " + find, 5, 45);
    }

    public void setTarget(double x, double y) {
        point = new kdPoint(x, y);
        recalc();
    }

    public void setFindCount(int find) {
        this.find = find;
    }

    protected void recalcView() {
        kClosest = tree.nnSearch(find, point);
    }

    public static void runBenchmark() {
        for (int numpts = 50; numpts <= 2000; numpts += 50) {
            Point[] pts = randomPoints(numpts, 0, 1000);
            long ltime = System.currentTimeMillis();
            for (int trial = 0; trial < 500; trial++) {
                kdTree tree = new kdTree(pts);
                for (int i = 0; i < numpts; i++) {
                    tree.nnSearch(4, pts[i].x, pts[i].y);
                }
            }
            double time = (System.currentTimeMillis() - ltime) / 500D;
            System.out.println("" + pts + "," + time);
        }
    }
}
