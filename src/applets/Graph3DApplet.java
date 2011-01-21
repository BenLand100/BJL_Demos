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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import kdimensional.kdPoint;
import math.Graph3D;
import math.parse.Function;
import math.parse.Scope;

/**
 *
 * @author benland100
 */
public class Graph3DApplet extends JApplet implements ActionListener {

    private Scope scope = new Scope();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph3D Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Graph3DApplet());
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    private Graph3D grapher;
    private JMenuBar bar;
    private JMenu graph;
    private JMenu view;

    private String rectFunc = "sin((x^2+y^2)*5)/(x^2+y^2+1)/(x^2+y^2+1)";
    private String cylFunc = "";
    private String shpereFunc = "";
    private String paramXFunc = "u+v";
    private String paramYFunc = "v-v";
    private String paramZFunc = "u+v";

    private JCheckBoxMenuItem rect, cyl, sphere, param;

    public Graph3DApplet() {
        super();
        setLayout(new BorderLayout());
        grapher = new Graph3D();
        
        rectSelect();
        add(grapher, BorderLayout.CENTER);

        bar = new JMenuBar();
        graph = new JMenu("Graph");
        rect = new JCheckBoxMenuItem("Rectangular");
        rect.addActionListener(this);
        cyl = new JCheckBoxMenuItem("Cylindrical");
        cyl.addActionListener(this);
        sphere = new JCheckBoxMenuItem("Spherical");
        sphere.addActionListener(this);
        param = new JCheckBoxMenuItem("Parametric");
        param.addActionListener(this);
        rect.setSelected(true);
        graph.add(rect);
        graph.add(cyl);
        graph.add(sphere);
        graph.add(param);
        bar.add(graph);
        view = new JMenu("View");
        bar.add(view);
        add(bar,BorderLayout.NORTH);
    }

    private void rectSelect() {
        Function func = Function.fromString(rectFunc, scope, "x", "y");
        final int w = 101, h = 101;
        kdPoint[][] mesh = new kdPoint[w][h];
        for (int i = 0; i < w; i++) {
            double x = Math.PI / (w-1) * (i - (h - 1)/2D);
            for (int j = 0; j < h; j++) {
                double y = Math.PI / (h-1) * (j - (w - 1)/2D);
                mesh[i][j] = new kdPoint(x,y,func.eval(x,y));
            }
        }
        grapher.setMesh(mesh);
    }

    private void cylSelect() {

    }

    private void sphereSelect() {

    }

    private void paramSelect() {
        Function x = Function.fromString(paramXFunc, scope, "u", "v");
        Function y = Function.fromString(paramYFunc, scope, "u", "v");
        Function z = Function.fromString(paramZFunc, scope, "u", "v");
        final int w = 101, h = 101;
        kdPoint[][] mesh = new kdPoint[w][h];
        for (int i = 0; i < w; i++) {
            double u = (10D * i) / w - 5D;
            for (int j = 0; j < h; j++) {
                double v = (10D * j) / h - 5D;
                mesh[i][j] = new kdPoint(x.eval(u,v),y.eval(u,v),z.eval(u,v));
            }
        }
        grapher.setMesh(mesh);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == rect) {
            rect.setSelected(true);
            cyl.setSelected(false);
            sphere.setSelected(false);
            param.setSelected(false);
            rectSelect();
        } else if (src == cyl) {
            rect.setSelected(false);
            cyl.setSelected(true);
            sphere.setSelected(false);
            param.setSelected(false);
            cylSelect();
        } else if (src == sphere) {
            rect.setSelected(false);
            cyl.setSelected(false);
            sphere.setSelected(true);
            param.setSelected(false);
            sphereSelect();
        } else if (src == param) {
            rect.setSelected(false);
            cyl.setSelected(false);
            sphere.setSelected(false);
            param.setSelected(true);
            paramSelect();
        }
    }

}
