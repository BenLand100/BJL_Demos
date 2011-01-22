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

import edgedetect.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author benland100
 */
public class DetectApplet extends JApplet implements ActionListener, ChangeListener {

    private DetectorPanel edge = new DetectorPanel();
    private JComboBox combo = new JComboBox(DetectorPanel.ShowState.values());
    private JButton load = new JButton("Load Image");
    private JSlider low = new JSlider(JSlider.VERTICAL,0,2000,300);
    private JSlider high = new JSlider(JSlider.VERTICAL,0,2000,500);
    private JSlider sigma = new JSlider(JSlider.VERTICAL,0,100,0);
    private JSlider scale = new JSlider(JSlider.HORIZONTAL,0,200,50);

    public void init() {
        setLayout(new BorderLayout());
        add(new JScrollPane(edge),BorderLayout.CENTER);
        JPanel north = new JPanel(new GridLayout());
        north.add(load);
        load.addActionListener(this);
        north.add(new JLabel("<= Choose Image", JLabel.LEFT));
        north.add(new JLabel("Choose View =>", JLabel.RIGHT));
        north.add(combo);
        combo.setSelectedItem(DetectorPanel.ShowState.valueOf("Binary"));
        combo.addActionListener(this);
        JPanel south = new JPanel(new BorderLayout());
        south.add(new JLabel("Zoom: "),BorderLayout.WEST);
        south.add(scale,BorderLayout.CENTER);
        scale.addChangeListener(this);
        south.add(north,BorderLayout.SOUTH);
        add(south,BorderLayout.SOUTH);
        JPanel east = new JPanel(new GridLayout());
        JPanel l = new JPanel(new BorderLayout());
        l.add(new JLabel("L",JLabel.CENTER),BorderLayout.NORTH);
        l.add(low,BorderLayout.CENTER);
        low.addChangeListener(this);
        east.add(l);
        JPanel h = new JPanel(new BorderLayout());
        h.add(new JLabel("H",JLabel.CENTER),BorderLayout.NORTH);
        h.add(high,BorderLayout.CENTER);
        high.addChangeListener(this);
        east.add(h);
        JPanel s = new JPanel(new BorderLayout());
        s.add(new JLabel("S",JLabel.CENTER),BorderLayout.NORTH);
        s.add(sigma,BorderLayout.CENTER);
        sigma.addChangeListener(this);
        east.add(s);
        add(east,BorderLayout.EAST);
    }

    public void start() {
        //edge.loadImage("http://img254.imageshack.us/img254/3860/hahajg5.png");
        //edge.loadImage("http://upload.wikimedia.org/wikipedia/commons/2/2e/Valve_gaussian_%282%29.PNG");
        edge.loadImage(getClass().getClassLoader().getResourceAsStream("horses.jpg"));
    }

    public void stop() {
    }

    public void destroy() {
        removeAll();
    }

    public static void main(String[] args) {
        DetectApplet applet = new DetectApplet();
        JFrame frame = new JFrame("Detect Applet");
        frame.add(applet);
        applet.init();
        applet.start();
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == combo) {
            edge.setVisible((DetectorPanel.ShowState)combo.getSelectedItem());
        } else if (e.getSource() == load) {
            JFileChooser choose = new JFileChooser();
            if (JFileChooser.APPROVE_OPTION == choose.showOpenDialog(this)) {
                edge.loadImage("file://" + choose.getSelectedFile().getPath());
            }

        }
    }

    public void stateChanged(ChangeEvent e) {
        JSlider slide = (JSlider)e.getSource();
        if (slide.getValueIsAdjusting()) return;
        if (slide == scale) {
            edge.setScale(slide.getValue() / 50D);
        } else if (slide == sigma) {
            edge.setSigma(slide.getValue() / 10D);
        } else if (slide == high) {
            edge.setHighThreshold(slide.getValue());
        } else if (slide == low) {
            edge.setLowThreshold(slide.getValue());
        }
    }
}
