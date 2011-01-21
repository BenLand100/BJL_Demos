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

import kdimensional.kdPoint;

/**
 *
 * @author benland100
 */
public class Mass {

    public final kdPoint center;
    public final kdPoint velocity;
    public final double mass;
    public final double size;

    public Mass(double x, double y, double mass, double size) {
        this(new kdPoint(x,y), new kdPoint(0,0), mass, size);
    }
    
    public Mass(kdPoint center, double mass, double size) {
        this(center, new kdPoint(0,0), mass, size);
    }

    public Mass(kdPoint center, kdPoint velocity, double mass, double size) {
        this.center = center;
        this.velocity = velocity;
        this.mass = mass;
        this.size = size;
    }


}
