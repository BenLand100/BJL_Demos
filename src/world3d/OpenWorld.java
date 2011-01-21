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

/**
 *
 * @author Benjamin J. Land
 */
public class OpenWorld extends World {

    double rx, ry, rz;
    double rdx, rdy, rdz;

    public OpenWorld() {
        super();
        setRotationVelocity(1, 1, 0);
    }

    public OpenWorld(Animator anim) {
        super(anim);
        rdx = rdy = rdz = 0D;
        rx = ry = rz = 0D;
    }

    public void setRotationVelocity(double vx, double vy, double vz) {
        rdx = vx;
        rdy = vy;
        rdz = vz;
    }

    public void setRotation(double tx, double ty, double tz) {
        rx = tx;
        ry = ty;
        rz = tz;
        beforePaint();
    }

    private static final double DEG_RAD = 1D/180D * Math.PI;
    private static final double RAD_DEG = 180D * 1D/Math.PI;

    protected void beforePaint() {
        rx = (rx + rdx) % 360;
        ry = (ry + rdy) % 360;
        rz = (rz + rdz) % 360;
        double tx = rx * DEG_RAD;
        double ty = ry * DEG_RAD;
        double tz = rz * DEG_RAD;
        double cosx = Math.cos(tx);
        double sinx = Math.sin(tx);
        double cosy = Math.cos(ty);
        double siny = Math.sin(ty);
        double cosz = Math.cos(tz);
        double sinz = Math.sin(tz);
        matrix.mat[0] = cosy * cosz;
        matrix.mat[1] = cosy * sinz;
        matrix.mat[2] = -siny;
        matrix.mat[3] = sinx * siny * cosz - cosx * sinz;
        matrix.mat[4] = sinx * siny * cosz + cosx * cosz;
        matrix.mat[5] = sinx * cosy;
        matrix.mat[6] = cosx * siny * cosz + sinx * sinz;
        matrix.mat[7] = cosx * siny * sinz - sinx * cosz;
        matrix.mat[8] = cosx * cosy;
    }

}