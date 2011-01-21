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

package sound;

import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author benland100
 */
public class Sound {
    
    public static void main(String[] args) {
        Sound.morseCode("kera").play();
    }

    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_BIT_DEPTH = 16;

    public static final boolean[][] morse_letters = new boolean[][] {
        {true, false},                          //A
        {false, true, true, true},              //B
        {false, true, false, true},             //C
        {false, true, true},                    //D
        {true},                                 //E
        {true, true, false, true},              //F
        {false, false, true},                   //G
        {true, true, true, true},               //H
        {true, true},                           //I
        {true, false, false, false},            //J
        {false, true, false},                   //K
        {true, false, true, true},              //L
        {false, false},                         //M
        {false, true},                          //N
        {false, false, false},                  //O
        {true, false, false, true},             //P
        {false, false, true, false},            //Q
        {true, false, true},                    //R
        {true, true, true},                     //S
        {false},                                //T
        {true, true, false},                    //U
        {true, true, true, false},              //V
        {true, false, false},                   //W
        {false, true, true, false},             //X
        {false, true, false, false},            //Y
        {false, false, true, true}              //Z
    };
    public static final boolean[][] morse_numbers = new boolean[][] {
        {false, false, false, false, false},    //0
        {true, false, false, false, false},     //1
        {true, true, false, false, false},      //2
        {true, true, true, false, false},       //3
        {true, true, true, true, false},        //4
        {true, true, true, true, true},         //5
        {false, true, true, true, true},        //6
        {false, false, true, true, true},       //7
        {false, false, false, true, true},      //8
        {false, false, false, false, true},     //9
    };
    public static final Sound morse_dot = Sound.sine(0.075,440);
    public static final Sound morse_dash = Sound.sine(0.200,440);
    public static final Sound morse_pause_1 = new Sound(0.1d);
    public static final Sound morse_pause_2 = new Sound(0.4d);
    public static final Sound morse_pause_3 = new Sound(0.6d);

    public final int[] samples;
    public final int sample_rate;
    public final int bit_depth;

    public Sound(int[] samples, int sample_rate, int bit_depth) {
         this.samples = samples;
         this.sample_rate = sample_rate;
         this.bit_depth = bit_depth;
    }

    public Sound(int length) {
        this(new int[length], DEFAULT_SAMPLE_RATE, DEFAULT_BIT_DEPTH);
    }

    public Sound(double sec) {
        this(new int[(int)(sec*DEFAULT_SAMPLE_RATE+0.5)], DEFAULT_SAMPLE_RATE, DEFAULT_BIT_DEPTH);
    }

    public Sound(Sound sound) {
        this(new int[sound.samples.length], sound.sample_rate, sound.bit_depth);
        System.arraycopy(sound.samples, 0, samples, 0, samples.length);
    }

    public static Sound sine(double sec, double freq) {
        int[] samples = new int[(int)(sec*DEFAULT_SAMPLE_RATE+0.5)];
        double wave = DEFAULT_SAMPLE_RATE / freq;
        double amplitude = Math.pow(2, DEFAULT_BIT_DEPTH) * 0.375;
        for (int i = 0; i < samples.length; i++) {
            samples[i] = (int)(Math.sin(2D*Math.PI*i/wave)*amplitude);
        }
        return new Sound(samples, DEFAULT_SAMPLE_RATE, DEFAULT_BIT_DEPTH);
    }

    public static Sound morseCode(String str) {
        LinkedList<Sound> pieces = new LinkedList<Sound>();
        str = str.toUpperCase();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            boolean[] mask = null;
            if (Character.isLetter(c)) {
                mask = morse_letters[c - 'A'];
            } else if (Character.isDigit(c)) {
                mask = morse_numbers[c - '0'];
            } else if (c == ' ') {
                pieces.add(morse_pause_3);
            }//add other characters if any
            if (mask != null) {
                for (int j = 0; j < mask.length; j++) {
                    pieces.add(mask[j] ? morse_dot : morse_dash);
                    pieces.add(morse_pause_1);
                }
                pieces.removeLast();
                pieces.add(morse_pause_2);
            }
        }
        int length = 0, offset = 0;
        for (Sound snd : pieces) length += snd.samples.length;
        int[] result = new int[length];
        for (Sound snd : pieces) {
            for (int i = 0; i < snd.samples.length; i++) {
                result[offset++] = snd.samples[i];
            }
        }
        return new Sound(result, DEFAULT_SAMPLE_RATE, DEFAULT_BIT_DEPTH);
    }


    public void play() {
        final int bits = bit_depth;
        final int bytes = bits / 8;
        final int buffer = 2048;
        try {
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits, 1, bytes, sample_rate, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, AudioSystem.NOT_SPECIFIED);
            line.start();
            byte[] data = new byte[buffer*bytes];
            for (int offset = 0, end = Math.min(offset+buffer,samples.length); offset < samples.length; end = Math.min((offset += buffer) + buffer, samples.length)) {
                int j = 0;
                for (int i = offset; i < end; i++) {
                    for (int c = bits / 8 - 1; c >= 0; c--) {
                        data[j++] = (byte)((samples[i] >>  8*c) & 0xFF);
                    }
                }
                line.write(data, 0, j);
            }
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
