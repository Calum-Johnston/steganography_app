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

        encodeData encoder = new encodeData();
        pvdEncode pvdEncoder = new pvdEncode();

        decodeData decoder = new decodeData();
        pvdDecode pvdDecoder = new pvdDecode();

        BufferedImage coverImage = null;
        BufferedImage stegoImage = null;
        try{
            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\src\\unittests\\testdata\\test_image.png"));
        }catch(Exception e){}

        String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890";
        String seed = "calum";
        text = "Research more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run from";

        long startTime = System.currentTimeMillis();

        // The actual tests
        int count = 0; int fail = 0;
        /**for(int redBits = 1; redBits < 9; redBits++){
            for(int greenBits = 1; greenBits < 9; greenBits++){
                for(int blueBits = 1; blueBits < 9; blueBits++){
                    for(boolean random : new boolean[] {false, true}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    for(int algorithm = 0; algorithm < 3; algorithm++) {
                                        count += 1;
                                        String result = "";
                                        if (red == false && green == false && blue == false) {
                                            break;
                                        }
                                        stegoImage = encoder.encode(coverImage, algorithm, red, green, blue,
                                                redBits, greenBits, blueBits, random, seed, text);
                                        String assertionError = null;
                                        try {
                                            result = decoder.decode(stegoImage);
                                            assertEquals(text, result);
                                        } catch (AssertionError ae) {
                                            assertionError = ae.toString();
                                        }
                                        if (assertionError != null) {
                                            System.out.println("Red: " + red + ", Green: " + green + ", Blue: " + blue);
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
        }*/


        text = "Research more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algori";
        // PVD
        for(int redBits = 1; redBits < 2; redBits++){
            for(int greenBits = 1; greenBits < 2; greenBits++){
                for(int blueBits = 1; blueBits < 2; blueBits++){
                    for(boolean random : new boolean[] {false}) {
                        for(boolean red : new boolean[] {false, true}) {
                            for (boolean green : new boolean[]{false, true}) {
                                for (boolean blue : new boolean[]{false, true}) {
                                    count += 1;
                                    String result = "";
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
                                        System.out.println("Red: " + red + ", Green: " + green + ", Blue: " + blue);
                                        System.out.println("Red Bits: " + redBits + " Green Bits: " + greenBits + ", Blue bits: " + blueBits);
                                        System.out.println("Algorithm: PVD");
                                        System.out.println("Random: " + random + ", with seed: " + seed);
                                        System.out.println("Text: " + text);
                                        System.out.println("Result: ");
                                        System.out.println(result);
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
