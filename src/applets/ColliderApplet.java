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

import demos.collide.Collider;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author benland100
 */
public class ColliderApplet extends JApplet {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mass Collider Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new ColliderApplet());
        frame.setVisible(true);
    }

    private JSlider a_count, a_mass, a_size;
    private JSlider b_count, b_mass, b_size;

    private JButton regen;


    private Collider c;

    public ColliderApplet() {
        super();
        setLayout(new BorderLayout());
        JPanel a = new JPanel(new GridLayout(1,3));
        JPanel a_count_pan = new JPanel(new BorderLayout());
        a_count = new JSlider(JSlider.VERTICAL,1,500,10);
        a_count_pan.add(new JLabel(" C"),BorderLayout.NORTH);
        a_count_pan.add(a_count,BorderLayout.CENTER);
        a.add(a_count_pan);
        JPanel a_mass_pan = new JPanel(new BorderLayout());
        a_mass = new JSlider(JSlider.VERTICAL,1,1000,10);
        a_mass_pan.add(new JLabel(" M"),BorderLayout.NORTH);
        a_mass_pan.add(a_mass,BorderLayout.CENTER);
        a.add(a_mass_pan);
        JPanel a_size_pan = new JPanel(new BorderLayout());
        a_size = new JSlider(JSlider.VERTICAL,1,15,5);
        a_size_pan.add(new JLabel(" S"),BorderLayout.NORTH);
        a_size_pan.add(a_size,BorderLayout.CENTER);
        a.add(a_size_pan);
        add(a,BorderLayout.WEST);


        JPanel b = new JPanel(new GridLayout(1,3));
        JPanel b_count_pan = new JPanel(new BorderLayout());
        b_count = new JSlider(JSlider.VERTICAL,1,500,10);
        b_count_pan.add(new JLabel(" C"),BorderLayout.NORTH);
        b_count_pan.add(b_count,BorderLayout.CENTER);
        b.add(b_count_pan);
        JPanel b_mass_pan = new JPanel(new BorderLayout());
        b_mass = new JSlider(JSlider.VERTICAL,1,1000,10);
        b_mass_pan.add(new JLabel(" M"),BorderLayout.NORTH);
        b_mass_pan.add(b_mass,BorderLayout.CENTER);
        b.add(b_mass_pan);
        JPanel b_size_pan = new JPanel(new BorderLayout());
        b_size = new JSlider(JSlider.VERTICAL,1,15,5);
        b_size_pan.add(new JLabel(" S"),BorderLayout.NORTH);
        b_size_pan.add(b_size,BorderLayout.CENTER);
        b.add(b_size_pan);
        add(b,BorderLayout.EAST);

        c = new Collider();
        add(c,BorderLayout.CENTER);

        regen = new JButton("Regenerate Environment");
        regen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                c.setCountA(a_count.getValue());
                c.setCountB(b_count.getValue());
                c.setMassA(a_mass.getValue() / 10D);
                c.setMassB(b_mass.getValue() / 10D);
                c.setSizeA(a_size.getValue());
                c.setSizeB(b_size.getValue());
                c.rebuild();
            }

        });
        add(regen,BorderLayout.SOUTH);

    }

}
