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

package edgedetect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author benland100
 */
public class DetectorPanel extends JPanel {

    private static class DetectorOutput {
        BufferedImage intensity,gaussian,direction,magnitudes,composite,edges,trace,binary;
    }
    public static enum ShowState { Source, Intensity, Gaussian, Direction, Magnitude, Composite, Edges, Trace, Binary };

    private ShowState state;
    private double scale;
    private double sigma;
    private int low, high;
    private BufferedImage raw;
    private DetectorOutput detected;
    private BufferedImage canvas;

    public DetectorPanel() {
        scale = 1;
        state = ShowState.Binary;
        low = 300;
        high = 500;
        sigma = 0D;
        loadImage("");
    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(),canvas.getHeight());
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(canvas, 0, 0, null);
    }

    private static double[][] gaussian(double sigma) {
        int effective = 6 * (int) Math.round(sigma) + 1;
        effective = effective % 2 == 0 ? effective : effective + 1;
        int radius = effective / 2;
        double[][] kernal = new double[effective][effective];
        for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                        kernal[x + radius][y + radius] = (1D / (2D * Math.PI * sigma * sigma)) * Math.exp(-(x * x + y * y) / (2D * sigma * sigma));
                }
        }
        return kernal;
    }

    private static DetectorOutput process(BufferedImage input, double sigma, int low, int high) {
        int w = input.getWidth(), h = input.getHeight();
        //First, convert the image into some scaler field of intensity, here I
        //just used grayscale, since thats easy enough to calculate.
        int[][] img = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = input.getRGB(x,y);
                img[x][y] = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            }
        }
        //Second, apply a gaussian blur if desired. It can improve edge quality
        //by removing the impact of noise.
        double[][] gaussian = gaussian(sigma);
        int radius = gaussian.length / 2;
        int[][] working;
        if (radius > 1) {
            working = new int[w][h];
            for (int x = radius; x < w - radius; x++) {
                for (int y = radius; y < h - radius; y++) {
                    double sum = 0D;
                    for (int kx = -radius; kx < radius; kx++) {
                        for (int ky = -radius; ky < radius; ky++) {
                            sum += gaussian[kx + radius][ky + radius] * (double) img[x + kx][y + ky];
                        }
                    }
                    working[x][y] = (int) Math.round(sum);
                }
            }
        } else {
            working = img;
        }
        //Third, calculate the gradient of the scaler field. The gradient is
        //a vector field in two dimensions, so i represent it with two component
        //arrays for simplicity. I calculate the magnitude and direction of the
        //gradient here, as well as the min and max magnitude.
        int[][] xgrad = new int[w][h];
        int[][] ygrad = new int[w][h];
        int[][] norms = new int[w][h];
        double[][] angles = new double[w][h];
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int x = 1; x < w-1; x++) {
            for (int y = 1; y < h-1; y++) {
                int xval = (working[x+1][y] - working[x-1][y]) * 10 + (working[x+1][y-1] + working[x+1][y+1] - working[x-1][y-1] - working[x-1][y+1]) * 3;
                xgrad[x][y] = xval;
                int yval = (working[x][y+1] - working[x][y-1]) * 10 + (working[x-1][y+1] + working[x+1][y+1] - working[x-1][y-1] - working[x+1][y-1]) * 3;
                ygrad[x][y] = yval;
                angles[x][y] = Math.atan2(yval,xval);
                int norm = (int)Math.sqrt(xval*xval + yval*yval);
                norms[x][y] = norm;
                if (norm > max) max = norm;
                if (norm < min) min = norm;
            }
        }
//        for (int x = 1; x < w-1; x++) {
//            for (int y = 1; y < h-1; y++) {
//                int norm = xgrad[x+1][y] - xgrad[x-1][y] + ygrad[x][y+1] - ygrad[x][y-1];
//                norms[x][y] = norm;
//            }
//        }
//        for (int x = 1; x < w-1; x++) {
//            for (int y = 1; y < h-1; y++) {
//                int xval = (norms[x+1][y] - norms[x-1][y]) * 10 + (norms[x+1][y-1] + norms[x+1][y+1] - norms[x-1][y-1] - norms[x-1][y+1]) * 3;
//                xgrad[x][y] = xval;
//                int yval = (norms[x][y+1] - norms[x][y-1]) * 10 + (norms[x-1][y+1] + norms[x+1][y+1] - norms[x-1][y-1] - norms[x+1][y-1]) * 3;
//                ygrad[x][y] = yval;
//                angles[x][y] = Math.atan2(yval,xval);
//            }
//        }
//        for (int x = 1; x < w-1; x++) {
//            for (int y = 1; y < h-1; y++) {
//                int norm = (int)Math.sqrt(xgrad[x][y] * xgrad[x][y] + ygrad[x][y] * ygrad[x][y]);
//                norms[x][y] = norm;
//                if (norm > max) max = norm;
//                if (norm < min) min = norm;
//            }
//        }
        int range = max - min;
//        if (range == 0) throw new RuntimeException
        //Fourth, suppress the non-edge pixels. This is done by determining if
        //it's a local maximum in the direction of the gradient.
        int[][] edges = new int[w][h];
        for (int x = 1; x < w-1; x++) {
            for (int y = 1; y < h-1; y++) {
                switch ((int)(((angles[x][y] % Math.PI) / (Math.PI / 4.0)) + 0.5)) {
                    case -4:
                    case 4:
                    case 0:
                        if (norms[x][y] >= norms[x-1][y] && norms[x][y] >= norms[x+1][y]) {
                            edges[x][y] = norms[x][y];
                        }
                        break;
                    case -3:
                    case 1:
                        if (norms[x][y] >= norms[x+1][y+1] && norms[x][y] >= norms[x-1][y-1]) {
                            edges[x][y] = norms[x][y];
                        }
                        break;
                    case -2:
                    case 2:
                        if (norms[x][y] >= norms[x][y-1] && norms[x][y] >= norms[x][y+1]) {
                            edges[x][y] = norms[x][y];
                        }
                        break;
                    case -1:
                    case 3:
                        if (norms[x][y] >= norms[x+1][y-1] && norms[x][y] >= norms[x-1][y+1]) {
                            edges[x][y] = norms[x][y];
                        }
                        break;
                }
            }
        }
        //Fifth, trace edges through the image. This basically amounts to following
        //each pixel in 'edges' that is above the 'high' value in the direction
        //specified in 'angles' as long as the edge intensity in 'edges' is above
        //the 'low' value
        boolean[][] visited = new boolean[w][h];
        int[][] trace = new int[w][h];
        for (int x = 1; x < w-1; x++) {
            for (int y = 1; y < h-1; y++) {
                if (!visited[x][y] && edges[x][y] >= high) {
                    for (int i = 0; i < 2; i++) {
                    int tx = x, ty = y;
                    while (!visited[tx][ty]) {
                        visited[tx][ty] = true;
                        if (edges[tx][ty] > low) {
                            trace[tx][ty] = edges[tx][ty];
                            switch ((int)(((angles[x][y] % Math.PI) / (Math.PI / 4.0)) + 0.5)) {
                                case -4:
                                case 4:
                                case 0:
                                        ty--;
                                        if (visited[tx][ty]) ty += 2;
                                        break;
                                    case -3:
                                    case 1:
                                        tx--;
                                        ty++;
                                        if (visited[tx][ty]) {
                                            tx += 2;
                                            ty -= 2;
                                        }
                                        break;
                                    case -2:
                                    case 2:
                                        tx--;
                                        if (visited[tx][ty]) tx += 2;
                                        break;
                                    case -1:
                                    case 3:
                                        tx--;
                                        ty--;
                                        if (visited[tx][ty]) {
                                            tx += 2;
                                            ty += 2;
                                        }
                                        break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
                visited[x][y] = false;
            }
        }
        //Finally, generate the various outputs of interest
        DetectorOutput out = new DetectorOutput();
        out.intensity = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.gaussian = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.edges = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.magnitudes = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.direction = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.composite = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.trace = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        out.binary = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        if (range == 0) return out;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int val = img[x][y]/3;
                out.intensity.setRGB(x,y,((val & 0xff) << 16) | ((val & 0xff) << 8) | (val & 0xff));
                val = working[x][y]/3;
                out.gaussian.setRGB(x,y,((val & 0xff) << 16) | ((val & 0xff) << 8) | (val & 0xff));
                val = (norms[x][y] - min) * 255 / range;
                out.magnitudes.setRGB(x,y,((val & 0xff) << 16) | ((val & 0xff) << 8) | (val & 0xff));
                out.direction.setRGB(x,y,Color.HSBtoRGB((float)angles[x][y] / 6.282f, 1f, 1f));
                out.composite.setRGB(x,y,Color.HSBtoRGB((float)angles[x][y] / 6.282f, 1f, val / 255f));
                val = (edges[x][y] - min) * 255 / range;
                out.edges.setRGB(x,y,((val & 0xff) << 16) | ((val & 0xff) << 8) | (val & 0xff));
                val = (trace[x][y] - min) * 255 / range;
                out.trace.setRGB(x,y,((val & 0xff) << 16) | ((val & 0xff) << 8) | (val & 0xff));
                if (val != 0) out.binary.setRGB(x,y,0xFFFFFF);
            }
        }
        return out;
    }

    private static BufferedImage scale(BufferedImage input, double scale) {
        if (scale == 0) return input;
        AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
        BufferedImage output = new BufferedImage((int)(input.getWidth()*scale),(int)(input.getHeight()*scale),BufferedImage.TYPE_INT_RGB);
        ((Graphics2D)output.getGraphics()).drawImage(input, tx, null);
        return output;
    }

    public void updateView() {
        switch (state) {
            case Source:
                canvas = scale(raw,scale);
                break;
            case Gaussian:
                canvas = scale(detected.gaussian,scale);
                break;
            case Intensity:
                canvas = scale(detected.intensity,scale);
                break;
            case Direction:
                canvas = scale(detected.direction,scale);
                break;
            case Magnitude:
                canvas = scale(detected.magnitudes,scale);
                break;
            case Composite:
                canvas = scale(detected.composite,scale);
                break;
            case Edges:
                canvas = scale(detected.edges,scale);
                break;
            case Trace:
                canvas = scale(detected.trace,scale);
                break;
            case Binary:
                canvas = scale(detected.binary,scale);
                break;
        }
        revalidate();
        repaint();
    }

    public void runDetection() {
        detected = process(raw,sigma,low,high);
        updateView();
    }

    public void loadImage(String path) {
        try {
            loadImage(new URL(path).openStream());
        } catch (Exception e) {
            raw = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
            canvas = raw;
        }
    }

    public void loadImage(InputStream in) {
        if (in != null) {
            try {
                raw = ImageIO.read(in);
                runDetection();
            } catch (Exception e) {
                raw = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
            canvas = raw;
                e.printStackTrace();
            }
        } else {
            raw = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
            canvas = raw;
        }
    }

    public void setVisible(ShowState state) {
        this.state = state;
        updateView();
    }

    public int getLowThreshold() {
        return low;
    }

    public int getHighThreshold() {
        return high;
    }

    public double getSigma() {
        return sigma;
    }

    public double getScale() {
        return scale;
    }

    public void setLowThreshold(int low) {
        this.low = low;
        runDetection();
    }

    public void setHighThreshold(int high) {
        this.high = high;
        runDetection();
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
        runDetection();
    }

    public void setScale(double scale) {
        this.scale = scale;
        updateView();
    }

}
