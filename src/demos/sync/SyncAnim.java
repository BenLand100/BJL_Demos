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

package demos.sync;

import world3d.Entity;
import world3d.Animator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import kdimensional.kdPoint;
import kdimensional.kdTree;
import static java.lang.Math.*;

/**
 *
 * @author Benjamin J. Land
 */
public class SyncAnim extends Animator {

    private int n = 8;
    private double dthreshold = 45;
    private double degrade = 1D;
    private double random = 1D;
    private double away = 0.05;
    private double towards = 0.05;
    private double flocking = 0.005;
    private double maxv = 5D;

    private double halfrandom = random / 2D;
    private double unflocking = 1 - flocking;

    LinkedList<Syncer> flock = new LinkedList<Syncer>();
    kdPoint[] flockPts;
    Syncer[] flockDat;

    public int getNChecked() {
        return n;
    }

    public void setNChecked(int n) {
        this.n = n;
    }

    public double getDThreshold() {
        return dthreshold;
    }

    public void setDThreshold(double dthreshold) {
        this.dthreshold = dthreshold;
    }

    public double getFDegrade() {
        return degrade;
    }

    public void setFDegrade(double degrade) {
        this.degrade = degrade;
    }

    public double getFRandom() {
        return random;
    }

    public void setFRandom(double random) {
        this.random = random;
        halfrandom = random / 2D;
    }

    public double getFAway() {
        return away;
    }

    public void setFAway(double away) {
        this.away = away;
    }

    public double getFTowards() {
        return towards;
    }

    public void setFTowards(double towards) {
        this.towards = towards;
    }

    public double getFTendancy() {
        return flocking;
    }

    public void setFTendancy(double surpercent) {
        this.flocking = surpercent;
        unflocking = 1 - surpercent;
    }

    /*private class Food extends Entity {

        public Food(double x, double y, double z) {
            super(x, y, z);
        }

        public void draw(Graphics2D g, double[] matrix, double focallen) {
            double x = point.mag[0];
            double y = point.mag[1];
            double z = point.mag[2];
            double dx = x * matrix[0] + y * matrix[1] + z * matrix[2];
            double dy = x * matrix[3] + y * matrix[4] + z * matrix[5];
            double dz = x * matrix[6] + y * matrix[7] + z * matrix[8];
            double f = focallen / (focallen - dz);
            g.setColor(Color.GREEN);
            g.fillRect((int) (dx * f), (int) (dy * f), 2, 2);
        }
    }*/

    public SyncAnim() {
        Vector<kdPoint> points = new Vector<kdPoint>();
        Vector<Syncer> syncers = new Vector<Syncer>();
        for (int x = -100; x <= 100; x += 50) {
            for (int y = -100; y <= 100; y += 50) {
                for (int z = -100; z <= 100; z += 50) {
                    Syncer s = new Syncer(x, y, z);
                    entities.add(s);
                    syncers.add(s);
                    flock.add(s);
                    points.add(s.point);
                }
            }
        }
        flockPts = points.toArray(new kdPoint[points.size()]);
        flockDat = syncers.toArray(new Syncer[syncers.size()]);
    }

    public synchronized void act() {
        double xacc = 0, yacc = 0, zacc = 0;
        kdTree tree = new kdTree(flockPts,flockDat);
        for (Syncer s : flock) {

            kdPoint p = s.point;
            double px = p.mag[0];
            double py = p.mag[1];
            double pz = p.mag[2];

            double dx = s.dx / degrade + random() * random - halfrandom;
            double dy = s.dy / degrade + random() * random - halfrandom;
            double dz = s.dz / degrade + random() * random - halfrandom;

            //System.out.println(p.dimension());
            //System.out.println(tree.getDimension());
            TreeMap<Double, kdTree.Entry<Syncer>> nearest = tree.nnSearch(n + 1, p);
            nearest.remove(nearest.firstKey());
            SortedMap<Double, kdTree.Entry<Syncer>> min = nearest.headMap(dthreshold);
            for (Entry<Double,kdTree.Entry<Syncer>> from : min.entrySet()) {
                kdPoint f = from.getValue().point;
                dx += signum(px - f.mag[0]) * towards;
                dy += signum(py - f.mag[1]) * towards;
                dz += signum(pz - f.mag[2]) * towards;
            }
            SortedMap<Double, kdTree.Entry<Syncer>> max = nearest.tailMap(dthreshold);
            for (Entry<Double, kdTree.Entry<Syncer>> to : max.entrySet()) {
                kdPoint t = to.getValue().point;
                dx += away * signum(t.mag[0] - px);
                dy += away * signum(t.mag[1] - py);
                dz += away * signum(t.mag[2] - pz);
            }

            for (kdTree.Entry<Syncer> ap : nearest.values()) {
                Syncer as = ap.data;
                dx = (as.dx * flocking + dx * unflocking);
                dy = (as.dy * flocking + dy * unflocking);
                dz = (as.dz * flocking + dz * unflocking);
            }

            dx = Math.abs(dx) > maxv ? Math.signum(dx) * maxv : dx;
            dy = Math.abs(dy) > maxv ? Math.signum(dy) * maxv : dy;
            dz = Math.abs(dz) > maxv ? Math.signum(dz) * maxv : dz;

            p.mag[0] += dx;
            p.mag[1] += dy;
            p.mag[2] += dz;

            s.dx = dx;
            s.dy = dy;
            s.dz = dz;
        }
        double size = flockPts.length;
        for (kdPoint p : flockPts) {
            if (p.mag[0] < -500) p.mag[0] += 1000;
            if (p.mag[0] > 500) p.mag[0] += -1000;
            if (p.mag[1] < -500) p.mag[1] += 1000;
            if (p.mag[1] > 500) p.mag[1] += -1000;
            if (p.mag[2] < -500) p.mag[2] += 1000;
            if (p.mag[2] > 500) p.mag[2] += -1000;
        }
    }
}