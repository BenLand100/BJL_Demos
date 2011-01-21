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

package world3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import kdimensional.kdMatrix;
import kdimensional.kdPoint;

/**
 *
 * @author benland100
 */
public class BoundedWorld extends World {

    double sx,sy,sz,ex,ey,ez;
    kdPoint ful, fur, fdr, fdl, bul, bur, bdr, bdl;

    public BoundedWorld(double sx, double sy, double sz, double ex, double ey, double ez) {
        super();
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
        ful = new kdPoint(sx,sy,sz);
        fur = new kdPoint(ex,sy,sz);
        fdr = new kdPoint(ex,ey,sz);
        fdl = new kdPoint(sx,ey,sz);
        bul = new kdPoint(sx,sy,ez);
        bur = new kdPoint(ex,sy,ez);
        bdr = new kdPoint(ex,ey,ez);
        bdl = new kdPoint(sx,ey,ez);
    }

     public BoundedWorld(Animator anim,double sx, double sy, double sz, double ex, double ey, double ez) {
        super(anim);
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
        ful = new kdPoint(sx,sy,sz);
        fur = new kdPoint(ex,sy,sz);
        fdr = new kdPoint(ex,ey,sz);
        fdl = new kdPoint(sx,ey,sz);
        bul = new kdPoint(sx,sy,ez);
        bur = new kdPoint(ex,sy,ez);
        bdr = new kdPoint(ex,ey,ez);
        bdl = new kdPoint(sx,ey,ez);
     }

    protected void beforePaint() {
        matrix = new kdMatrix(3,3,getWidth()/(ex-sx),0D,0D,0D,getHeight()/(ey-sy),0D,0D,0D,1D);
    }

    private kdPoint project(kdPoint pt) {
        double f = focuslen / (pt.mag[2] - viewpoint.mag[2]);
        return pt.mul(f);
    }

    protected void paintWorld(Graphics2D gfx) {
        kdPoint a = project(matrix.mul(ful));
        kdPoint b = project(matrix.mul(fur));
        kdPoint c = project(matrix.mul(fdr));
        kdPoint d = project(matrix.mul(fdl));
        kdPoint e = project(matrix.mul(bul));
        kdPoint f = project(matrix.mul(bur));
        kdPoint g = project(matrix.mul(bdr));
        kdPoint h = project(matrix.mul(bdl));
        //gfx.drawString("A", (int)a.mag[0],(int)a.mag[1]);
        //gfx.drawString("G", (int)g.mag[0],(int)g.mag[1]);
        Polygon left = new Polygon();
        left.addPoint((int)a.mag[0],(int)a.mag[1]);
        left.addPoint((int)e.mag[0],(int)e.mag[1]);
        left.addPoint((int)h.mag[0],(int)h.mag[1]);
        left.addPoint((int)d.mag[0],(int)d.mag[1]);
        Polygon right = new Polygon();
        right.addPoint((int)b.mag[0],(int)b.mag[1]);
        right.addPoint((int)f.mag[0],(int)f.mag[1]);
        right.addPoint((int)g.mag[0],(int)g.mag[1]);
        right.addPoint((int)c.mag[0],(int)c.mag[1]);
        Polygon top = new Polygon();
        top.addPoint((int)a.mag[0],(int)a.mag[1]);
        top.addPoint((int)b.mag[0],(int)b.mag[1]);
        top.addPoint((int)f.mag[0],(int)f.mag[1]);
        top.addPoint((int)e.mag[0],(int)e.mag[1]);
        Polygon bottom = new Polygon();
        bottom.addPoint((int)d.mag[0],(int)d.mag[1]);
        bottom.addPoint((int)c.mag[0],(int)c.mag[1]);
        bottom.addPoint((int)g.mag[0],(int)g.mag[1]);
        bottom.addPoint((int)h.mag[0],(int)h.mag[1]);
        Polygon back = new Polygon();
        back.addPoint((int)e.mag[0],(int)e.mag[1]);
        back.addPoint((int)f.mag[0],(int)f.mag[1]);
        back.addPoint((int)g.mag[0],(int)g.mag[1]);
        back.addPoint((int)h.mag[0],(int)h.mag[1]);
        gfx.setColor(Color.GRAY.brighter());
        gfx.fill(left);
        gfx.fill(right);
        gfx.setColor(Color.GRAY);
        gfx.fill(back);
        gfx.setColor(Color.GRAY.darker());
        gfx.fill(top);
        gfx.fill(bottom);
        gfx.setColor(Color.BLACK);
        gfx.drawLine((int)a.mag[0],(int)a.mag[1],(int)b.mag[0],(int)b.mag[1]);
        gfx.drawLine((int)b.mag[0],(int)b.mag[1],(int)c.mag[0],(int)c.mag[1]);
        gfx.drawLine((int)c.mag[0],(int)c.mag[1],(int)d.mag[0],(int)d.mag[1]);
        gfx.drawLine((int)d.mag[0],(int)d.mag[1],(int)a.mag[0],(int)a.mag[1]);
        gfx.drawLine((int)e.mag[0],(int)e.mag[1],(int)f.mag[0],(int)f.mag[1]);
        gfx.drawLine((int)f.mag[0],(int)f.mag[1],(int)g.mag[0],(int)g.mag[1]);
        gfx.drawLine((int)g.mag[0],(int)g.mag[1],(int)h.mag[0],(int)h.mag[1]);
        gfx.drawLine((int)h.mag[0],(int)h.mag[1],(int)e.mag[0],(int)e.mag[1]);
        gfx.drawLine((int)a.mag[0],(int)a.mag[1],(int)e.mag[0],(int)e.mag[1]);
        gfx.drawLine((int)b.mag[0],(int)b.mag[1],(int)f.mag[0],(int)f.mag[1]);
        gfx.drawLine((int)c.mag[0],(int)c.mag[1],(int)g.mag[0],(int)g.mag[1]);
        gfx.drawLine((int)d.mag[0],(int)d.mag[1],(int)h.mag[0],(int)h.mag[1]);
    }


}
