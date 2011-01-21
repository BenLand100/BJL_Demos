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

package kdimensional;

/**
 * Elements are containd in row x col order
 *
 * @author benland100
 */
public class kdMatrix {

    public final double[] mat;
    public final int r,c;

    public kdMatrix(int r, int c) {
        if (r <= 0 || c <= 0) throw new RuntimeException("Invalid matrix dimensions");
        mat = new double[r*c];
        this.r = r;
        this.c = c;
    }

    public kdMatrix(int r, int c, double... matrix) {
        if (r <= 0 || c <= 0) throw new RuntimeException("Invalid matrix dimensions");
        if (matrix.length != r*c) throw new RuntimeException("Invalid matrix dimensions");
        this.mat = matrix;
        this.r = r;
        this.c = c;
    }

    public kdMatrix mul(kdMatrix mat) {
        if (c != mat.r) throw new RuntimeException("Invalid matrix dimensions");
        double[] res = new double[r*mat.c];
        for (int i = 0; i < res.length; i++) {
           res[i] = row(i / mat.c).dot(mat.col(i % mat.c));
        }
        return new kdMatrix(r,mat.c,res);
    }

    public kdMatrix add(kdMatrix mat) {
        if (r != mat.r || c != mat.c) throw new RuntimeException("Invalid matrix dimensions");
        double[] res = new double[r*c];
        for (int i = 0; i < this.mat.length; i++) {
           res[i] = this.mat[i] + mat.mat[i];
        }
        return new kdMatrix(r,c,res);
    }

    public kdMatrix mul(double scaler) {
        double[] res = new double[r*c];
        for (int i = 0; i < mat.length; i++) {
            res[i] = scaler * mat[i];
        }
        return new kdMatrix(r,c,res);
    }

    public kdPoint mul(kdPoint vector) {
        if (c != vector.dimension()) throw new RuntimeException("Invalid vector dimensions");
        double[] res = new double[r];
        for (int i = 0; i < mat.length; i++) {
            res[i/c] += vector.mag[i%c] * mat[i];
        }
        return new kdPoint(res);
    }

    public kdPoint row(int row) {
        if (row < 0 || row >= r) throw new RuntimeException("Invalid row");
        double[] dat = new double[r];
        for (int i = 0; i < r; i++) {
            dat[i] = mat[c*row+i];
        }
        return new kdPoint(dat);
    }

    public kdPoint col(int col) {
        if (col < 0 || col >= c) throw new RuntimeException("Invalid col");
        double[] dat = new double[c];
        for (int i = 0; i < c; i++) {
            dat[i] = mat[c*i+col];
        }
        return new kdPoint(dat);
    }

    public double pos(int row, int col) {
        if (col < 0 || col >= c || row < 0 || row >= r) throw new RuntimeException("Invalid pos");
        return mat[c*row + col];
    }


    public void row(int row, kdPoint dat) {
        if (row < 0 || row >= r || dat.mag.length != r) throw new RuntimeException("Invalid row");
        for (int i = 0; i < r; i++) {
            mat[c*row+i] = dat.mag[i];
        }
    }

    public void col(int col, kdPoint dat) {
        if (col < 0 || col >= c || dat.mag.length != c) throw new RuntimeException("Invalid col");
        for (int i = 0; i < c; i++) {
             mat[c*i+col] = dat.mag[i];
        }
    }

    public void pos(int row, int col, double dat) {
        if (col < 0 || col >= c || row < 0 || row >= r) throw new RuntimeException("Invalid pos");
        mat[c*row + col] = dat;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int row = 0; row < r; row++) {
                str.append(row(row));
            if (row+1!=r) str.append(',');
        }
        return str.append(']').toString();
    }

}
