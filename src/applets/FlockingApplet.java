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

package applets;

import demos.sync.SyncAnim;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import world3d.BoundedWorld;

/**
 *
 * @author benland100
 */
public class FlockingApplet  extends JApplet implements ChangeListener {

    final static String TITLE = "Flocking Simulation - Benjamin Land";
    BoundedWorld w;
    SyncAnim a;
    JSlider xsld, ysld, zsld;
    JSlider nsld, dsld, vsld;

    public void init() {
        a = new SyncAnim();
        w = new BoundedWorld(a,-500,-500,-500,500,500,500);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(w, BorderLayout.CENTER);
        /*xsld = new JSlider(-10,10,0);
        xsld.addChangeListener(this);
        JPanel xpan = new JPanel(new BorderLayout());
        xpan.add(new JLabel(" X Velocity:"), BorderLayout.WEST);
        xpan.add(xsld, BorderLayout.CENTER);
        ysld = new JSlider(-10,10,0);
        ysld.addChangeListener(this);
        JPanel ypan = new JPanel(new BorderLayout());
        ypan.add(new JLabel(" Y Velocity:"), BorderLayout.WEST);
        ypan.add(ysld, BorderLayout.CENTER);
        zsld = new JSlider(-10,10,0);
        zsld.addChangeListener(this);
        JPanel zpan = new JPanel(new BorderLayout());
        zpan.add(new JLabel(" Z Velocity:"), BorderLayout.WEST);
        zpan.add(zsld, BorderLayout.CENTER);
        JPanel south = new JPanel(new GridLayout(3,1));
        south.add(xpan);
        south.add(ypan);
        south.add(zpan);
        c.add(south,BorderLayout.SOUTH);*/

        nsld = new JSlider(JSlider.VERTICAL,0,30,8);
        nsld.addChangeListener(this);
        JPanel npan = new JPanel(new BorderLayout());
        npan.add(new JLabel("N",JLabel.CENTER), BorderLayout.NORTH);
        npan.add(nsld, BorderLayout.CENTER);
        dsld = new JSlider(JSlider.VERTICAL,0,200,45);
        dsld.addChangeListener(this);
        JPanel dpan = new JPanel(new BorderLayout());
        dpan.add(new JLabel("D",JLabel.CENTER), BorderLayout.NORTH);
        dpan.add(dsld, BorderLayout.CENTER);
        vsld = new JSlider(JSlider.VERTICAL,0,100,50);
        vsld.addChangeListener(this);
        JPanel vpan = new JPanel(new BorderLayout());
        vpan.add(new JLabel("F",JLabel.CENTER), BorderLayout.NORTH);
        vpan.add(vsld, BorderLayout.CENTER);
        JPanel east = new JPanel(new GridLayout(1,3));
        east.add(npan);
        east.add(dpan);
        east.add(vpan);
        c.add(east,BorderLayout.EAST);

        c.add(new JLabel(TITLE,JLabel.CENTER),BorderLayout.NORTH);
    }

    public void start() {
        repaint();
    }

    public void stop() {
       // w.setRotationVelocity(0, 0, 0);
        System.exit(0);
    }

    public void stateChanged(ChangeEvent e) {
        //w.setRotationVelocity(xsld.getValue()/5.0, ysld.getValue()/5.0, zsld.getValue()/5.0);
        a.setNChecked(nsld.getValue());
        a.setDThreshold(dsld.getValue());
        a.setFTendancy(vsld.getValue() / 10000D);
    }

     public static void main(String[] args) {
        /*LinkedList<Point3D> pts = new LinkedList<Point3D>();
        pts.add(new Point3D(-1,0,0));
        pts.add(new Point3D(5,0,0));
        pts.add(new Point3D(7,0,0));
        pts.add(new Point3D(10,0,0));
        pts.add(new Point3D(9.8,0,0));
        pts.add(new Point3D(10.1,0,0));
        Point3DTree tree = new Point3DTree(pts.toArray(new Point3D[0]));
        System.out.println(tree.kNearest(new Point3D(10.5,0,0),7));*/
        JApplet a = new FlockingApplet();
        JFrame frame = new JFrame("Test");
        frame.setSize(500,500);
        frame.add(a);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        a.init();
        a.start();
        frame.repaint();
    }

}
