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
import java.awt.Color;
import java.awt.Graphics2D;
import kdimensional.kdMatrix;
import kdimensional.kdPoint;

/**
 *
 * @author benland100
 */
public class Syncer extends Entity {

    //velocity vectors
    public double dx, dy, dz;

    public Syncer(double x, double y, double z) {
        super(x,y,z);
        dx = Math.random();
        dy = Math.random();
        dz = Math.random();
    }

    public void draw(Graphics2D g, kdMatrix matrix, kdPoint view, double focallen) {
        kdPoint tx = matrix.mul(point);
        g.setColor(Color.BLUE);
        double f = focallen / (tx.mag[2] - view.mag[2]);
        int size = (int)Math.round(10*f*f);
        g.fillOval((int) (tx.mag[0]*f) - size/2, (int) (tx.mag[1]*f) - size/2, size, size);
    }

}