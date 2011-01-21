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

package math.parse;

import math.parse.Parser.Number;
import java.util.HashMap;

/**
 *
 * @author benland100
 */
public class Scope {

    public static Function SINE = new Function() { public double eval(double... args) { return Math.sin(args[0]); } };
    public static Function COSINE = new Function() { public double eval(double... args) { return Math.cos(args[0]); } };
    public static Function TANGENT = new Function() { public double eval(double... args) { return Math.tan(args[0]); } };

    public static class Slot {
        public double num = 0D;
    }

    private HashMap<String,Slot> vars;
    private HashMap<String,Function> functs;

    public Scope() {
        vars = new HashMap<String,Slot>();
        functs = new HashMap<String,Function>();
        createLocal("pi");
        set("pi",Math.PI);
        createLocal("e");
        set("e",Math.E);
        set("sin",SINE);
        set("cos",COSINE);
        set("tan",TANGENT);
    }

    protected Scope(Scope parrent) {
        vars = new HashMap<String,Slot>(parrent.vars);
        functs = new HashMap<String,Function>(parrent.functs);
    }

    public Scope subscope() {
        return new Scope(this);
    }

    public void createLocal(String var) {
        vars.put(var,new Slot());
    }

    public void set(String var, double val) {
        vars.get(var).num = val;
    }

    public void set(String var, Function f) {
        functs.put(var,f);
    }

    public double resolve(String var) {
        Slot slot = vars.get(var);
        if (slot == null) throw new RuntimeException("Symbol unbound as a variable");
        return slot.num;
    }

    public double resolve(String var, double... args) {
        Function funct = functs.get(var);
        if (funct == null) throw new RuntimeException("Symbol unbound as a function");
        return funct.eval(args);
    }

}
