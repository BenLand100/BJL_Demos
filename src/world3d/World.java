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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.Timer;
import kdimensional.kdMatrix;
import kdimensional.kdPoint;

/**
 *
 * @author Benjamin J. Land
 */
public abstract class World extends JPanel {

    Animator anim;
    kdPoint viewpoint = new kdPoint(0, 0, -2000);
    kdMatrix matrix = new kdMatrix(3,3);
    double focuslen = 1500;
    Timer painter, actor;

    public World() {
        init();
        LinkedList<Entity> entities = new LinkedList<Entity>();
        for (int x = -100; x <= 100; x += 20)
            for (int y = -100; y <= 100; y += 20)
                for (int z = -100; z <= 100; z += 20)
                    entities.add(new Entity(x, y, z));
        anim = new Animator(entities);
    }

    public World(Animator anim) {
        init();
        this.anim = anim;
    }

    private void init() {
        painter = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                beforePaint();
                repaint();
            }
        });
        painter.setCoalesce(true);
        painter.start();
        actor = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                anim.act();
            }
        });
        actor.setCoalesce(true);
        actor.start();
    }

    protected abstract void beforePaint();
    protected void paintWorld(Graphics2D g) { }

    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        int width = getWidth();
        int height = getHeight();
        g.clearRect(0, 0, width, height);
        double tx = width / 2;
        double ty = height / 2;
        g.translate(tx, ty);
        paintWorld(g);
        for (Entity e : anim.getEntities())
            e.draw(g, matrix, viewpoint, focuslen);
    }
}