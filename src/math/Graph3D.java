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

package math;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JPanel;
import kdimensional.kdMatrix;
import kdimensional.kdPoint;
import math.parse.Function;

/**
 *
 * @author benland100
 */
public class Graph3D extends JPanel {

    private static Color blend(Color a, Color b, float w) {
        float[] a_rgb = a.getColorComponents(null);
        float[] b_rgb = b.getColorComponents(null);
        return new Color(a_rgb[0]*(1-w)+b_rgb[0]*(w),a_rgb[1]*(1-w)+b_rgb[1]*w,a_rgb[2]*(1-w)+b_rgb[2]*w);
    }

    public static enum Shading { SMOOTH, BLOCK, SHINE, SOLID };

    private static final kdPoint AXIS_X = new kdPoint(1,0,0);
    private static final kdPoint AXIS_Y = new kdPoint(0,1,0);
    private static final kdPoint AXIS_Z = new kdPoint(0,0,1);

    private Color[] colormap = new Color[] {Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE,blend(Color.RED,Color.BLUE,0.5f)};
    private Shading shadestyle = Shading.SOLID;
    private boolean wire = true, shade = true;

    private double focal_length = 50D, v_range = 0.4;
    private kdPoint pos = new kdPoint(0,0,500);
    private double theta_z = 0, theta_x = 0, theta_y = 02;
    private kdMatrix tx = new kdMatrix(3,3,1,0,0,0,1,0,0,0,1);
    private Surface surf;

    public Graph3D() {
        initComp();
        final int w = 101, h = 101;
        kdPoint[][] mesh = new kdPoint[w][h];
        for (int i = 0; i < w; i++) {
            double x = Math.PI / (w-1) * (i - (h - 1)/2D);
            for (int j = 0; j < h; j++) {
                double y = Math.PI / (h-1) * (j - (w - 1)/2D);
                mesh[i][j] = new kdPoint(x,y,0);
            }
        }
        surf = new Surface(mesh);
    }

    public void setMesh(kdPoint[][] mesh) {
        surf = new Surface(mesh);
        repaint();
    }

    public void setCameraPos(kdPoint pos) {
        this.pos.set(pos);
    }

    private Point transform(kdPoint src, double scale) {
        kdPoint pt = tx.mul(src);
        double f = focal_length / (pt.mag[2] - pos.mag[2]) * scale;
        return new Point((int)Math.round(pt.mag[0] * f), (int)Math.round(pt.mag[1] * f));
    }

    private void shade_smooth(Vector<FillSegment> segs, ShadeData data, Graphics2D g) {
        FillSegment seg = null;
        Polygon poly = new Polygon();
        double low = data.zmin, range = data.zmax - low;
        for (int i = 0; i < segs.size(); i++) {
            seg = segs.get(i);
            poly.reset();
            poly.addPoint(seg.a.x, seg.a.y);
            poly.addPoint(seg.b.x, seg.b.y);
            poly.addPoint(seg.c.x, seg.c.y);
            poly.addPoint(seg.d.x, seg.d.y);
            float color = (float)((seg.org.mag[2] - low)/range*(colormap.length-1));
            int a = (int)Math.floor(color);
            int b = (int)Math.ceil(color);
            //System.out.println((seg.org.mag[2] - low)/range + "::" + a + "<=>" + b);
            if (a == b) {
                g.setColor(colormap[a]);
            } else {
                g.setColor(blend(colormap[a],colormap[b],color - a));
            }
            g.fill(poly);
            if (wire) {
                g.setColor(Color.BLACK);
                g.draw(poly);
            }
        }
    }

    private void shade_shine(Vector<FillSegment> segs, ShadeData data, Graphics2D g) {
        FillSegment seg = null;
        Polygon poly = new Polygon();
        Color flat = colormap[0];
        float[] hsb = Color.RGBtoHSB(flat.getRed(), flat.getGreen(), flat.getBlue(),null);
        Color shine = new Color(Color.HSBtoRGB(hsb[0], -hsb[1]*0.5f, hsb[2]));
        for (int i = 0; i < segs.size(); i++) {
            seg = segs.get(i);
            poly.reset();
            poly.addPoint(seg.a.x, seg.a.y);
            poly.addPoint(seg.b.x, seg.b.y);
            poly.addPoint(seg.c.x, seg.c.y);
            poly.addPoint(seg.d.x, seg.d.y);
            if (seg.val > 1 || seg.val <= 0) System.out.println(seg.val);
            g.setColor(blend(flat,shine,seg.val));
            g.fill(poly);
            if (wire) {
                g.setColor(Color.BLACK);
                g.draw(poly);
            }
        }
    }

    private void shade_block(Vector<FillSegment> segs, ShadeData data, Graphics2D g) {
        FillSegment seg = null;
        Polygon poly = new Polygon();
        double low = data.zmin, range = data.zmax - low;
        for (int i = 0; i < segs.size(); i++) {
            seg = segs.get(i);
            poly.reset();
            poly.addPoint(seg.a.x, seg.a.y);
            poly.addPoint(seg.b.x, seg.b.y);
            poly.addPoint(seg.c.x, seg.c.y);
            poly.addPoint(seg.d.x, seg.d.y);
            g.setColor(colormap[(int)((seg.org.mag[2]-low)/range*(colormap.length-1)+0.5)]);
            g.fill(poly);
            if (wire) {
                g.setColor(Color.BLACK);
                g.draw(poly);
            }
        }
    }

    private void shade_solid(Vector<FillSegment> segs, ShadeData data, Graphics2D g) {
        FillSegment seg = null;
        Polygon poly = new Polygon();
        Color flat = Color.BLACK;
        float[] hsb = Color.RGBtoHSB(flat.getRed(), flat.getGreen(), flat.getBlue(),null);
        Color shine = new Color(Color.HSBtoRGB(hsb[0], -hsb[1]*0.5f, hsb[2]));
        for (int i = 0; i < segs.size(); i++) {
            seg = segs.get(i);
            poly.reset();
            poly.addPoint(seg.a.x, seg.a.y);
            poly.addPoint(seg.b.x, seg.b.y);
            poly.addPoint(seg.c.x, seg.c.y);
            poly.addPoint(seg.d.x, seg.d.y);
            g.setColor(colormap[0]);
            g.fill(poly);
            if (wire) {
                g.setColor(Color.BLACK);
                g.draw(poly);
            }
        }
    }

    private void wireframe(Graphics2D g, double scale) {
        RectSurface rect = null;
        Point a = null, b = null, c = null, d = null;
        g.setColor(Color.BLACK);
        for (int i=0, j = 0; j < surf.mesh[i].length; j++) {
            rect = surf.mesh[i][j];
            a = transform(rect.a,scale);
            b = transform(rect.b,scale);
            g.drawLine(a.x, a.y, b.x, b.y);
        }
        for (int i = 0; i < surf.mesh.length; i++) {
            rect = surf.mesh[i][0];
            a = transform(rect.a,scale);
            d = transform(rect.d,scale);
            g.drawLine(d.x, d.y, a.x, a.y);
            for (int j = 0; j < surf.mesh[i].length; j++) {
                rect = surf.mesh[i][j];
                b = transform(rect.b,scale);
                c = transform(rect.c,scale);
                d = transform(rect.d,scale);
                g.drawLine(b.x, b.y, c.x, c.y);
                g.drawLine(c.x, c.y, d.x, d.y);
            }
        }
    }

    public void drawGraph(Graphics2D g, double scale) {
        if (wire && !shade) {
            wireframe(g,scale);
            return;
        }
        ShadeData data = new ShadeData();
        Vector<FillSegment> segs = new Vector<FillSegment>(surf.mesh.length * surf.mesh[0].length);
        RectSurface rect = null;
        Point a = null, b = null, c = null, d = null;
        kdPoint dir = pos.norm();
        data.zmin = surf.mesh[0][0].center.mag[2];
        data.zmax = surf.mesh[0][0].center.mag[2];
        for (int i = 0; i < surf.mesh.length; i++) {
            for (int j = 0; j < surf.mesh[i].length; j++) {
                rect = surf.mesh[i][j];
                a = transform(rect.a,scale);
                b = transform(rect.b,scale);
                c = transform(rect.c,scale);
                d = transform(rect.d,scale);
                float val = Math.abs((float)tx.mul(rect.norm).dot(dir));
                double dist = tx.mul(rect.center).dot(dir);
                if (rect.center.mag[2] < data.zmin) data.zmin = rect.center.mag[2];
                if (rect.center.mag[2] > data.zmax) data.zmax = rect.center.mag[2];
                segs.add(new FillSegment(a,b,c,d,rect.center,val,dist));
            }
        }
        Collections.sort(segs);
        if (shade) {
            switch (shadestyle) {
                case SMOOTH:
                    shade_smooth(segs,data,g);
                    break;
                case BLOCK:
                    shade_block(segs,data,g);
                    break;
                case SOLID:
                    shade_solid(segs,data,g);
                    break;
                case SHINE:
                    shade_shine(segs,data,g);
                    break;
            }
        }
    }

    public void paint(Graphics old) {
        int w = getWidth(), h = getHeight();
        double scale = Math.min(w,h) / v_range;
        Graphics2D g = (Graphics2D)old;
        g.clearRect(0, 0, w, h);
        g.translate(w/2, h/2);
        kdMatrix x_tx = new kdMatrix(3,3,1,0,0,0,Math.cos(theta_x),Math.sin(theta_x),0,-Math.sin(theta_x),Math.cos(theta_x));
        kdMatrix y_tx = new kdMatrix(3,3,Math.cos(theta_y),0,Math.sin(theta_y),0,1,0,-Math.sin(theta_y),0,Math.cos(theta_y));
        kdMatrix z_tx = new kdMatrix(3,3,Math.cos(theta_z),-Math.sin(theta_z),0,Math.sin(theta_z),Math.cos(theta_z),0,0,0,1);
        tx = x_tx.mul(y_tx.mul(z_tx));
        drawGraph(g,scale);
    }
    
    public void processKeyEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED) {
            //System.out.println(event);
            switch (event.getKeyCode()) {
                case KeyEvent.VK_UP:
                    theta_x -= Math.PI/Math.log(focal_length)/10;
                    break;
                case KeyEvent.VK_DOWN:
                    theta_x += Math.PI/Math.log(focal_length)/10;
                    break;
                case KeyEvent.VK_LEFT:
                    theta_y -= Math.PI/Math.log(focal_length)/10;
                    break;
                case KeyEvent.VK_RIGHT:
                    theta_y += Math.PI/Math.log(focal_length)/10;
                    break;
                case KeyEvent.VK_COMMA:
                    theta_z += Math.PI/Math.log(focal_length)/10;
                    break;
                case KeyEvent.VK_PERIOD:
                    theta_z -= Math.PI/Math.log(focal_length)/10;
                    break;
            }
            repaint();
        }
    
    }

    private void initComp() {
        setFocusable(true);
        enableInputMethods(true);
        enableEvents(KeyEvent.KEY_EVENT_MASK);
    }

    private class ShadeData {
        public double zmin = Double.NEGATIVE_INFINITY, zmax = Double.POSITIVE_INFINITY;
    }

    private class FillSegment implements Comparable<FillSegment> {
        final Point a, b, c, d;
        final double dist;
        final float val;
        final kdPoint org;
        public FillSegment(Point a, Point b, Point c, Point d, kdPoint org, float val, double dist) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.org = org;
            this.val = val;
            this.dist = dist;
        }
        public int compareTo(FillSegment s) {
            return dist == s.dist ? 0 : (dist > s.dist ? 1 : -1);
        }
    }

    private class RectSurface {
        final kdPoint a, b, c, d;
        final kdPoint norm;
        final kdPoint center;
        public RectSurface(kdPoint a, kdPoint b, kdPoint c, kdPoint d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            kdPoint ba = b.sub(a);
            kdPoint cb = c.sub(b);
            kdPoint dc = d.sub(c);
            kdPoint ad = a.sub(d);
            norm = ba.cross(cb).add(dc.cross(ad)).norm();
            center = a.add(b.add(c.add(d))).mul(0.25D);
        }
    }

    private class Surface {
        final RectSurface[][] mesh;
        public Surface(kdPoint[][] mesh) {
            this.mesh = new RectSurface[mesh.length-1][mesh[0].length-1];
            for (int i = 1; i < mesh.length; i++) {
                for (int j = 1; j < mesh[i].length; j++) {
                    this.mesh[i-1][j-1] = new RectSurface(mesh[i-1][j-1],mesh[i-1][j],mesh[i][j],mesh[i][j-1]);
                }
            }
        }
    }

}
