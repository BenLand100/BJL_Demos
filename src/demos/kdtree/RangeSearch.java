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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.TreeMap;
import kdimensional.kTreeView;
import kdimensional.kdPoint;
import kdimensional.kdTree;

/**
 * Contains the code for a very nice (if i do say so myself) demo of a kdTree
 * range search.
 *
 * @author benland100
 */
public class RangeSearch extends kTreeView {

    private int range = 50;
    private kdPoint point = new kdPoint(500, 500);
    private TreeMap<Double, kdTree.Entry> kFound = null;

    public RangeSearch() {
        super();
    }

    public void paintView(Graphics g) {
        if (point != null) {
            Point2D target = new Point2D.Double(), dest = new Point2D.Double(), radius = new Point2D.Double();
            comp.transform(new Point2D.Double(point.mag[0], point.mag[1]), target);
            g.setColor(Color.GREEN);
            g.fillOval((int) target.getX() - 2, (int) target.getY() - 2, 5, 5);
            comp.transform(new Point2D.Double(range, range), radius);
            g.setColor(Color.CYAN);
            g.drawOval((int) (target.getX() - radius.getX()), (int) (target.getY() - radius.getY()), (int) (radius.getX() * 2D), (int) (radius.getY() * 2D));
            if (kFound != null) {
                for (kdTree.Entry pt : kFound.values()) {
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
        g.drawString("Points Found: " + kFound.size(), 5, 15);
        g.drawString("Range Searched: " + range, 5, 45);
        g.drawString("Distance Calcs: " + tree.lastComplexity(), 5, 30);
    }

    public void setTarget(double x, double y) {
        point = new kdPoint(x, y);
        recalc();
    }

    public void setRange(int range) {
        this.range = range;
    }

    protected void recalcView() {
        kFound = tree.rangeSearch(range, point);
    }

}
