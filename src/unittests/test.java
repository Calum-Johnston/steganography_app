package unittests;

import com.java.calumjohnston.algorithms.lsb.*;
import com.java.calumjohnston.algorithms.pvd.pvdDecode;
import com.java.calumjohnston.algorithms.pvd.pvdEncode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Runs tests on the various algorithms in the program
 */
public class test {

    /**
     * Constructor for the class
     */
    public test() {
        runTests();
    }

    /**
     * Runs the tests
     */
    public void runTests() {

        lsbEncode lsbEncoder = new lsbEncode();
        lsbmEncode lsbmEncoder = new lsbmEncode();
        lsbmrEncode lsbmrEncoder = new lsbmrEncode();
        pvdEncode pvdEncoder = new pvdEncode();

        lsbDecode lsbDecoder = new lsbDecode();
        lsbmDecode lsbmDecoder = new lsbmDecode();
        lsbmrDecode lsbmrDecoder = new lsbmrDecode();
        pvdDecode pvdDecoder = new pvdDecode();

        BufferedImage coverImage = null;
        BufferedImage stegoImage = null;
        try{
            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\src\\unittests\\testdata\\test_image.png"));
        }catch(Exception e){}

        String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890";
        String seed = "calum";
        text = "a";
        String result = "";

        long startTime = System.currentTimeMillis();

        // The actual tests
        int count = 0; int fail = 0;

        // LSB
        for(int redBits = 1; redBits < 9; redBits++){
            for(int greenBits = 1; greenBits < 9; greenBits++){
                for(int blueBits = 1; blueBits < 9; blueBits++){
                    for(boolean random : new boolean[] {false}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    count += 1;
                                    if(red == false && green == false && blue == false){
                                        break;
                                    }
                                    stegoImage = lsbEncoder.encode(coverImage, red, green, blue,
                                            redBits, greenBits, blueBits, random, seed, text);
                                    String assertionError = null;
                                    try {
                                        result = lsbDecoder.decode(stegoImage);
                                        assertEquals(text, result);
                                    } catch (AssertionError ae) {
                                        assertionError = ae.toString();
                                    }
                                    if (assertionError != null ) {
                                        System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                        System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                        System.out.println("Algorithm: LSB");
                                        System.out.println("Random: " + random + ", with seed: " + seed);
                                        System.out.println("Text: " + text);
                                        System.out.println("Result: " + result);
                                        fail += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // LSB
        for(int redBits = 1; redBits < 9; redBits++){
            for(int greenBits = 1; greenBits < 9; greenBits++){
                for(int blueBits = 1; blueBits < 9; blueBits++){
                    for(boolean random : new boolean[] {false}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    count += 1;
                                    if(red == false && green == false && blue == false){
                                        break;
                                    }
                                    stegoImage = lsbmEncoder.encode(coverImage, red, green, blue,
                                            redBits, greenBits, blueBits, random, seed, text);
                                    String assertionError = null;
                                    try {
                                        result = lsbmDecoder.decode(stegoImage);
                                        assertEquals(text, result);
                                    } catch (AssertionError ae) {
                                        assertionError = ae.toString();
                                    }
                                    if (assertionError != null ) {
                                        System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                        System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                        System.out.println("Algorithm: LSBM");
                                        System.out.println("Random: " + random + ", with seed: " + seed);
                                        System.out.println("Text: " + text);
                                        System.out.println("Result: " + result);
                                        fail += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // LSBMR (Works perfectly)
        for(int redBits = 1; redBits < 2; redBits++){
            for(int greenBits = 1; greenBits < 2; greenBits++){
                for(int blueBits = 1; blueBits < 2; blueBits++){
                    for(boolean random : new boolean[] {false, true}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    count += 1;
                                    if(red == false && green == false && blue == false){
                                        break;
                                    }
                                    stegoImage = lsbmrEncoder.encode(coverImage, red, green, blue,
                                                        random, seed, text);
                                    String assertionError = null;
                                    try {
                                        result = lsbmrDecoder.decode(stegoImage);
                                        assertEquals(text, result);
                                    } catch (AssertionError ae) {
                                        assertionError = ae.toString();
                                    }
                                    if (assertionError != null ) {
                                        System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                        System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                        System.out.println("Algorithm: LSBMR");
                                        System.out.println("Random: " + random + ", with seed: " + seed);
                                        System.out.println("Text: " + text);
                                        System.out.println("Result: " + result);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PVD (Works without random for small stuff!!)
        for(int redBits = 1; redBits < 2; redBits++){
            for(int greenBits = 1; greenBits < 2; greenBits++){
                for(int blueBits = 1; blueBits < 2; blueBits++){
                    for(boolean random : new boolean[] {false}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    count += 1;
                                    if(red == false && green == false && blue == false){
                                        break;
                                    }
                                    stegoImage = pvdEncoder.encode(coverImage, red, green, blue,
                                            random, seed, text);
                                    String assertionError = null;
                                    try {
                                        result = pvdDecoder.decode(stegoImage);
                                        assertEquals(text, result);
                                    } catch (AssertionError ae) {
                                        assertionError = ae.toString();
                                    }
                                    if (assertionError != null ) {
                                        System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                        System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                        System.out.println("Algorithm: PVD");
                                        System.out.println("Random: " + random + ", with seed: " + seed);
                                        System.out.println("Text: " + text);
                                        System.out.println("Result: " + result);
                                        fail += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        float differenceMS = (endTime - startTime);
        float differenceS = differenceMS / 1000;
        System.out.println("Ran " + count + " test cases in " + differenceS + "s");
        System.out.println("Passed test cases: " + (count - fail));
        System.out.println("Failed test cases: " + fail);
    }

    public static void main(String[] args){
        test t = new test();
    }
}
