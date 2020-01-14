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
            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\3rd year - Dissertation\\Steganography Desktop App\\src\\unittests\\testdata\\test_image.png"));
        }catch(Exception e){}

        String text = "What does a man say to a women if they aren't the same?";
        String seed = "calum";

        // The actual tests
        for(int redBits = 1; redBits < 9; redBits++){
            for(int greenBits = 1; greenBits < 9; greenBits++){
                for(int blueBits = 1; blueBits < 9; blueBits++){
                    for(int algorithm = 0; algorithm < 2; algorithm ++) {
                        for(boolean random : new boolean[] { false, true}) {
                            stegoImage = encoder.encodeImage(coverImage, text, true, true, true,
                                    redBits, greenBits, blueBits, random, "", algorithm);
                            String assertionError = null;
                            try {
                                assertEquals(text, decoder.decodeImage(stegoImage));
                            } catch (AssertionError ae) {
                                assertionError = ae.toString();
                            }
                            if (assertionError != null) {
                                System.out.println("Red: " + true + ", Green: " + true + ", Blue: " + true);
                                System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                System.out.println("Algorithm: " + algorithm);
                                System.out.println("Random: " + random + ", with seed: " + seed);
                                System.out.println("Text: " + text);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        test t = new test();
    }
}
