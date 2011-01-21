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

package demos.collide;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.Timer;
import kdimensional.kdPoint;
import kdimensional.kdTree;

/**
 *
 * @author benland100
 */
public class Collider extends JPanel {

    private kdTree tree = null;

    Mass[] a = null, b = null;
    double massA = 1D, massB = 1D;
    double sizeA = 5D, sizeB = 5D;
    int countA = 10, countB = 10;
    double maxv = 5D;

    Mass[] allmass = null;
    kdPoint[] points = null;
    Timer timer;

    public Collider() {
        rebuild();
        timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                act();
                repaint();
            }
        });
        timer.setCoalesce(true);
        timer.start();
    }

    public synchronized void rebuild() {
        allmass = new Mass[countA+countB];
        points = new kdPoint[countA+countB];
        int c = 0;
        int w = getWidth(),  h = getHeight();
        w = w == 0 ? 500 : w;
        h = h == 0 ? 500 : h;
        a = new Mass[countA];
        for (int i = 0; i < countA; i++, c++) {
            a[i] = new Mass(new kdPoint(Math.random()*w,Math.random()*h),new kdPoint(Math.random()*maxv*2 - maxv,Math.random()*maxv*2 - maxv),massA,sizeA);
            allmass[c] = a[i];
            points[c] = a[i].center;
        }
        b = new Mass[countB];
        for (int i = 0; i < countB; i++, c++) {
            b[i] = new Mass(new kdPoint(Math.random()*w,Math.random()*h),new kdPoint(Math.random()*maxv*2 - maxv,Math.random()*maxv*2 - maxv),massB,sizeB);
            allmass[c] = b[i];
            points[c] = b[i].center;
        }
    }
    
    public synchronized void act() {
        if (allmass == null || points == null) return;
        tree = new kdTree(points, allmass);
        TreeMap<Double, kdTree.Entry<Mass>> closest;
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        for (int i = 0; i < allmass.length; i++) {
            Mass m = allmass[i];
            kdPoint center = m.center;

            closest = tree.rangeSearch(2*(sizeA > sizeB ? sizeA : sizeB), center);
            if (closest.size() > 1) {
                closest.remove(closest.firstKey());
                for (kdTree.Entry<Mass> e : closest.values()) {
                    Mass x = e.data;
                    kdPoint col = center.sub(x.center);
                    if (col.len() > m.size + x.size) continue;
                    kdPoint norm = col.norm();
                    double a_mag = m.velocity.dot(norm);
                    double b_mag = x.velocity.dot(norm);
                    if (a_mag > 0 && b_mag < 0) continue; //already hit
                    kdPoint parallel_a = norm.mul(a_mag);
                    kdPoint parallel_b = norm.mul(b_mag);
                    kdPoint perpen_a = m.velocity.sub(parallel_a);
                    kdPoint perpen_b = x.velocity.sub(parallel_b);
                    m.velocity.set(parallel_b.mul(x.mass / m.mass).add(perpen_a));
                    x.velocity.set(parallel_a.mul(m.mass / x.mass).add(perpen_b));
                }
            }

            if (center.mag[0] - m.size <= 0) m.velocity.mag[0] =  Math.abs(m.velocity.mag[0]);
            if (center.mag[0] + m.size >= w) m.velocity.mag[0] = -Math.abs(m.velocity.mag[0]);
            if (center.mag[1] - m.size <= 0) m.velocity.mag[1] =  Math.abs(m.velocity.mag[1]);
            if (center.mag[1] + m.size >= h) m.velocity.mag[1] = -Math.abs(m.velocity.mag[1]);
            
            center.mag[0] += m.velocity.mag[0];
            center.mag[1] += m.velocity.mag[1];
        }
    }
    
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        for (int i = 0; i < a.length; i++) {
            double size = a[i].size;
            g.fillOval((int)Math.round(a[i].center.mag[0]-size), (int)Math.round(a[i].center.mag[1]-size), (int)Math.round(size*2), (int)Math.round(size*2));
        }
        g.setColor(Color.BLUE);
        for (int i = 0; i < b.length; i++) {
            double size = b[i].size;
            g.fillOval((int)Math.round(b[i].center.mag[0]-size), (int)Math.round(b[i].center.mag[1]-size), (int)Math.round(size*2), (int)Math.round(size*2));
        }
    }
    
    public void setCountA(int count) {
        countA = count;
    }
    
    public void setSizeA(double size) {
        sizeA = size;
    }
    
    public void setMassA(double mass) {
        massA = mass;
    }
    
    public void setCountB(int count) {
        countB = count;
    }

    public void setSizeB(double size) {
        sizeB = size;
    }

    public void setMassB(double mass) {
        massB = mass;
    }

}
