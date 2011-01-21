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
import kdimensional.kdMatrix;
import kdimensional.kdPoint;

/**
 *
 * @author Benjamin J. Land
 */
public class Entity {

    public final kdPoint point;

    public Entity() {
        this(0, 0, 0);
    }

    public Entity(double x, double y, double z) {
        point = new kdPoint(x, y, z);
    }

    public void draw(Graphics2D g, kdMatrix matrix, kdPoint view, double focallen) {
        kdPoint tx = matrix.mul(point);
        double f = focallen / (tx.mag[2] - view.mag[2]);
        g.setColor(Color.BLACK);
        g.fillRect((int) (tx.mag[0] * f), (int) (tx.mag[1] * f), (int)(5*f), (int)(5*f));
    }
    
}