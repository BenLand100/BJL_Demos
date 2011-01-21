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

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import math.parse.Parser.Element;
import math.parse.Parser.Invoke;
import math.parse.Parser.Operator;
import math.parse.Parser.Symbol;
import math.parse.Parser.Number;
import math.parse.Parser.Token;

/**
 *
 * @author benland100
 */
public class Function {

    public static Function fromString(String expression, Scope scope, String... vars) {
        ListIterator<Token> toks = Parser.lex(expression);
        return Parser.parse(toks, vars, scope);
    }

    Scope scope;
    LinkedList<Element> elems;
    String[] vars;

    public Function(Scope superscope, String[] vars, LinkedList<Element> elems) {
        scope = superscope.subscope();
        for (int i = 0; i < vars.length; i++) {
            scope.createLocal(vars[i]);
        }
        this.elems = elems;
        this.vars = vars;
    }

    protected Function() {
        scope = null;
        elems = null;
        vars  = null;
    }

    public double eval(double... args) {
        Stack<Number> stack = new Stack<Number>();
        if (args.length != vars.length) throw new RuntimeException("Invalid number of arguments");
        for (int i = 0; i < vars.length; i++) {
            scope.set(vars[i], args[i]);
        }
        for (Element elem : elems) {
            switch (elem.lextype) {
                case SYMBOL:
                    stack.push(new Number(((Symbol)elem).resolve(scope)));
                    break;
                case OPERATOR:
                    ((Operator)elem).preform(stack);
                    break;
                case NUMBER:
                    stack.push((Number)elem);
                    break;
                case INVOKE:
                    stack.push(new Number(((Invoke)elem).resolve(scope,stack)));
            }
        }
        return stack.peek().val;
    }

}
