package unittests;

import com.java.calumjohnston.algorithms.decode;
import com.java.calumjohnston.algorithms.encode;

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

        encode encoder = new encode();
        decode decoder = new decode();

        BufferedImage coverImage = null;
        BufferedImage stegoImage = null;
        try{
            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\src\\unittests\\testdata\\test_image.png"));
        }catch(Exception e){}

        String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890";
        String seed = "calum";
        String result = "";

        long startTime = System.currentTimeMillis();

        // The actual tests
        int count = 0;
        for(int redBits = 1; redBits < 9; redBits++){
            for(int greenBits = 1; greenBits < 9; greenBits++){
                for(int blueBits = 1; blueBits < 9; blueBits++){
                    for(int algorithm = 0; algorithm < 2; algorithm ++) {
                        for(boolean random : new boolean[] {false, true}) {
                            for(boolean red : new boolean[] {false, true}) {
                                for (boolean green : new boolean[]{false, true}) {
                                    for (boolean blue : new boolean[]{false, true}) {
                                        count += 1;
                                        if(red == false && green == false && blue == false){
                                            break;
                                        }
                                        stegoImage = encoder.encodeImage(coverImage, text, red, green, blue,
                                                redBits, greenBits, blueBits, random, seed, algorithm);
                                        String assertionError = null;
                                        try {
                                            result = decoder.decodeImage(stegoImage, seed);
                                            assertEquals(text, result);
                                        } catch (AssertionError ae) {
                                            assertionError = ae.toString();
                                        }
                                        if (assertionError != null ) {
                                            System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                            System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                            System.out.println("Algorithm: " + algorithm);
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
        }
        for(int redBits = 1; redBits < 2; redBits++){
            for(int greenBits = 1; greenBits < 2; greenBits++){
                for(int blueBits = 1; blueBits < 2; blueBits++){
                    for(int algorithm = 2; algorithm < 3; algorithm ++) {
                        for(boolean random : new boolean[] {false, true}) {
                            for(boolean red : new boolean[] {false, true}) {
                                for (boolean green : new boolean[]{false, true}) {
                                    for (boolean blue : new boolean[]{false, true}) {
                                        count += 1;
                                        if(red == false && green == false && blue == false){
                                            break;
                                        }
                                        stegoImage = encoder.encodeImage(coverImage, text, red, green, blue,
                                                redBits, greenBits, blueBits, random, seed, algorithm);
                                        String assertionError = null;
                                        try {
                                            result = decoder.decodeImage(stegoImage, seed);
                                            assertEquals(text, result);
                                        } catch (AssertionError ae) {
                                            assertionError = ae.toString();
                                        }
                                        if (assertionError != null) {
                                            System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                            System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                            System.out.println("Algorithm: " + algorithm);
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
        }
        long endTime = System.currentTimeMillis();
        float differenceMS = (endTime - startTime);
        float differenceS = differenceMS / 1000;
        System.out.println("Ran " + count + " test cases in " + differenceS + "s");
    }

    public static void main(String[] args){
        test t = new test();
    }
}
