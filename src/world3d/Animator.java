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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Benjamin J. Land
 */
public class Animator {

    protected List<Entity> entities;

    public Animator() {
        this(new LinkedList<Entity>());
    }

    public Animator(List<Entity> entities) {
        this.entities = entities;
    }

    public void act() {
    }

    public List<Entity> getEntities() {
        return entities;
    }

}