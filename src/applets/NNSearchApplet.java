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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import demos.kdtree.NNSearch;
import kdimensional.kdPoint;

/**
 *
 * @author benland100
 */
public class NNSearchApplet  extends JApplet {

    public static void main(String[] args) {
        JFrame frame = new JFrame("kdTree Nearest Neighbor Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new NNSearchApplet());
        frame.setVisible(true);
    }

    private NNSearch searcher;

    public NNSearchApplet() {
        searcher = new NNSearch();
        searcher.setFindCount(3);
        searcher.setGenValues(100, 0, 1000);
        searcher.setView(0, 0, 1000, 1000);
        searcher.setTarget(400, 400);
        searcher.genpts();
        addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                kdPoint pt = searcher.txView(e.getPoint());
                searcher.setTarget(pt.mag[0], pt.mag[1]);
                repaint();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        setLayout(new BorderLayout());
        add(searcher, BorderLayout.CENTER);

        final JSlider points = new JSlider(JSlider.HORIZONTAL, 1, 1000, 100);
        final JLabel pointslbl = new JLabel("100");
        points.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                pointslbl.setText(Integer.toString(points.getValue()));
                searcher.setGenValues(points.getValue(), 0, 1000);
            }
        });
        points.setSnapToTicks(true);
        final JSlider find = new JSlider(JSlider.HORIZONTAL, 1, 50, 3);
        final JLabel findlbl = new JLabel("3");
        find.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                findlbl.setText(Integer.toString(find.getValue()));
                searcher.setFindCount(find.getValue());
                searcher.recalc();
                searcher.repaint();
            }
        });
        find.setSnapToTicks(true);
        final JButton recalc = new JButton("Generate Points");
        recalc.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                searcher.genpts();
                searcher.recalc();
                repaint();
            }
        });
        JPanel south = new JPanel(new BorderLayout());
        JPanel southcenter = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new GridLayout(2, 1)), center = new JPanel(new GridLayout(2, 1)), right = new JPanel(new GridLayout(2, 1));
        left.add(new JLabel(" #Points to Find"));
        center.add(find);
        right.add(findlbl);
        left.add(new JLabel(" #Points to Generate "));
        center.add(points);
        right.add(pointslbl);
        southcenter.add(left, BorderLayout.WEST);
        southcenter.add(center, BorderLayout.CENTER);
        southcenter.add(right, BorderLayout.EAST);
        south.add(southcenter, BorderLayout.CENTER);
        south.add(recalc, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
}
}
