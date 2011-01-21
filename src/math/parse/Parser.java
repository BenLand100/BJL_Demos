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

import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

/**
 *
 * @author benland100
 */
public class Parser {
    
    public static enum LexType { OPERATOR, NUMBER, SYMBOL, SPECIAL, INVOKE };
    public static enum ParseElem { RIGHT_PAREN, LEFT_PAREN, COMMA };
    
    public static class Token {
        public final Parser.LexType lextype;
        protected Token(Parser.LexType type) {
            lextype = type;
        }
    }

    public static class Special extends Token {
        public final Parser.ParseElem elem;
        public Special(Parser.ParseElem elem) {
            super(Parser.LexType.SPECIAL);
            this.elem = elem;
        }
    }

    public static abstract class Element extends Token {
        protected Element(Parser.LexType type) {
            super(type);
        }
    }

    static abstract class Operator extends Parser.Element {
        public static final int PREFIX = 4;
        public static final int EXPONE = 3;
        public static final int MULDIV = 2;
        public static final int ADDSUB = 1;

        public final int precedence;

        protected Operator(int precedence) {
            super(Parser.LexType.OPERATOR);
            this.precedence = precedence;
        }

        public abstract void preform(Stack<Number> stack);


        static class Add extends Operator {
            public Add() { super(ADDSUB); }
            public void preform(Stack<Number> stack) {
                double b = stack.pop().val, a = stack.pop().val;
                stack.push(new Number(a+b));
            }
        }
        static class Sub extends Operator {
            public Sub() { super(ADDSUB); }
            public void preform(Stack<Number> stack) {
                double b = stack.pop().val, a = stack.pop().val;
                stack.push(new Number(a-b));
            }
        }
        static class Mul extends Operator {
            public Mul() { super(MULDIV); }
            public void preform(Stack<Number> stack) {
                double b = stack.pop().val, a = stack.pop().val;
                stack.push(new Number(a*b));
            }
        }
        static class Div extends Operator {
            public Div() { super(MULDIV); }
            public void preform(Stack<Number> stack) {
                double b = stack.pop().val, a = stack.pop().val;
                stack.push(new Number(a/b));
            }
        }
        static class Exp extends Operator {
            public Exp() { super(EXPONE); }
            public void preform(Stack<Number> stack) {
                double b = stack.pop().val, a = stack.pop().val;
                stack.push(new Number(Math.pow(a,b)));
            }
        }
        static class Neg extends Operator {
            public Neg() { super(PREFIX); }
            public void preform(Stack<Number> stack) {
                stack.push(new Number(-stack.pop().val));
            }
        }
    }

   static class Symbol extends Parser.Element {
        public final String name;

        public Symbol(String name) {
            super(Parser.LexType.SYMBOL);
            this.name = name;
        }

        public double resolve(Scope scope) {
            return scope.resolve(name);
        }

    }

   static class Invoke extends Parser.Element {

       public int count;
       public String name;

       public Invoke(String name, int count) {
           super(Parser.LexType.INVOKE);
           this.count = count;
           this.name = name;
       }

       public double resolve(Scope scope, Stack<Number> stack) {
            double[] nums = new double[count];
            for (int i = nums.length-1; i >= 0; i--) {
                nums[i] = stack.pop().val;
            }
            return scope.resolve(name,nums);
       }

   }

    static class Number extends Parser.Element {
        public final double val;
        public Number(double val) {
            super(Parser.LexType.NUMBER);
            this.val = val;
        }
    }

    public static boolean numchar(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean namechar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
    }

    public static ListIterator<Token> lex(String str) {
        StringCharacterIterator chars = new StringCharacterIterator(str);
        LinkedList<Token> tokens = new LinkedList<Token>();
        int start, end;
        for (char c = chars.current(); chars.getIndex() < chars.getEndIndex(); c = chars.next()) {
            switch (c) {
                //***BEGIN NAME/FAUX_OPERATOR***
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    start = chars.getIndex();
                    while (namechar(chars.next()));
                    end = chars.getIndex();
                    chars.previous();
                    String name = str.substring(start,end);
                    tokens.add(new Symbol(name));
                    break;
                //***BEGIN NUMBER***
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    start = chars.getIndex();
                    while (numchar(chars.next()));
                    switch (chars.current()) {
                        case '.':
                            while (numchar(chars.next()));
                            end = chars.getIndex();
                            chars.previous();
                            tokens.add(new Number(Double.parseDouble(str.substring(start,end))));
                            break;
                        //case '\\':
                        //    while (numchar(chars.next()));
                        //    lolwat
                        default:
                            end = chars.getIndex();
                            chars.previous();
                            tokens.add(new Number(Double.parseDouble(str.substring(start,end))));
                    }
                    break;
                //***BEGIN SPECIAL***
                case ',':
                    tokens.add(new Special(ParseElem.COMMA));
                    break;
                case '(':
                    tokens.add(new Special(ParseElem.LEFT_PAREN));
                    break;
                case ')':
                    tokens.add(new Special(ParseElem.RIGHT_PAREN));
                    break;
                //***BEGIN OPERATOR***
                case '^':
                    tokens.add(new Operator.Exp());
                    break;
                case '+':
                    tokens.add(new Operator.Add());
                    break;
                case '-':
                    if (tokens.size() == 0 || tokens.getLast().lextype == LexType.SPECIAL || tokens.getLast().lextype == LexType.OPERATOR) {
                        tokens.add(new Operator.Neg());
                    } else {
                        tokens.add(new Operator.Sub());
                    }
                    break;
                case '/':
                    tokens.add(new Operator.Div());
                    break;
                case '*':
                    tokens.add(new Operator.Mul());
                    break;
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    continue;
                default:
                    throw new RuntimeException("Invalid character: " + chars.current());
            }
        }
        return tokens.listIterator();
    }

    public static Function parse(ListIterator<Token> toks, String[] vars, Scope scope) {
        return new Function(scope, vars == null ? new String[0] : vars, parse(toks));
    }

    private static LinkedList<Element> parse(ListIterator<Token> toks) {
        LinkedList<Element> elems = new LinkedList<Element>();
        Stack<Stack<Operator>> depth = new Stack<Stack<Operator>>();
        Stack<Operator> opers = new Stack<Operator>();
        LexType thisType = null;
        outer_loop:
        for (Token tok = toks.next(); ; tok = toks.next()) {
            thisType = tok.lextype;
            switch (thisType) {
                case SPECIAL:
                    Special spc = (Special)tok;
                    switch (spc.elem) {
                        case COMMA:
                            break outer_loop;
                        case RIGHT_PAREN:
                            while (!opers.empty()) elems.add(opers.pop());
                            if (depth.empty()) break outer_loop;
                            opers = depth.pop();
                            break;
                        case LEFT_PAREN:
                            depth.push(opers);
                            opers = new Stack<Operator>();
                            break;
                    }
                    break;
                case OPERATOR:
                    Operator oper = (Operator)tok;
                    while (!opers.empty() && oper.precedence <= opers.peek().precedence) {
                        elems.add(opers.pop());
                    }
                    opers.push(oper);
                    break;
                case SYMBOL:
                    Symbol sym = (Symbol)tok;
                    if (toks.hasNext()) {
                        tok = toks.next();
                        if (tok.lextype == LexType.SPECIAL && ((Special)tok).elem == ParseElem.LEFT_PAREN) {
                            int count = 0;
                            if (tok.lextype != LexType.SPECIAL || ((Special)tok).elem != ParseElem.RIGHT_PAREN) {
                                for ( ; toks.hasNext(); ) {
                                    //System.out.println("Subparsing");
                                    elems.addAll(parse(toks));
                                    count++;
                                    toks.previous();
                                    tok = toks.next();
                                    //System.out.println(tok);
                                    if (tok.lextype == LexType.SPECIAL && ((Special)tok).elem == ParseElem.RIGHT_PAREN)
                                        break;
                                    if (tok.lextype != LexType.SPECIAL || ((Special)tok).elem != ParseElem.COMMA)
                                        throw new RuntimeException("Comma or parenthesis expected");
                                }
                            }
                            elems.add(new Invoke(sym.name, count));
                        } else {
                            toks.previous();
                            elems.add(new Symbol(sym.name));
                        }
                    } else {
                        elems.add(new Symbol(sym.name));
                    }
                    break;
                case NUMBER:
                    elems.add((Number)tok);
            }
            //System.out.println(elems);
            if (!toks.hasNext()) break;
        }
        while (!opers.empty()) elems.add(opers.pop());
        //System.out.println(elems);
        return elems;
    }

}
