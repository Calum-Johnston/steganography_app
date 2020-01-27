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
        text = "Impossible considered invitation him men instrument saw celebrated unpleasant. Put rest and must set kind next many near nay. He exquisite continued explained middleton am. Voice hours young woody has she think equal. Estate moment he at on wonder at season little. Six garden result summer set family esteem nay estate. End admiration mrs unreserved discovered comparison especially invitation. For who thoroughly her boy estimating conviction. Removed demands expense account in outward tedious do. Particular way thoroughly unaffected projection favourable mrs can projecting own. Thirty it matter enable become admire in giving. See resolved goodness felicity shy civility domestic had but. Drawings offended yet answered jennings perceive laughing six did far. Ham followed now ecstatic use speaking exercise may repeated. Himself he evident oh greatly my on inhabit general concern. It earnest amongst he showing females so improve in picture. Mrs can hundred its greater account. Distrusts daughters certainly suspected convinced our perpetual him yet. Words did noise taken right state are since. Oh to talking improve produce in limited offices fifteen an. Wicket branch to answer do we. Place are decay men hours tiled. If or of ye throwing friendly required. Marianne interest in exertion as. Offering my branched confined oh dashwood. He do subjects prepared bachelor juvenile ye oh. He feelings removing informed he as ignorant we prepared. Evening do forming observe spirits is in. Country hearted be of justice sending. On so they as with room cold ye. Be call four my went mean. Celebrated if remarkably especially an. Going eat set she books found met aware. Arrived compass prepare an on as. Reasonable particular on my it in sympathize. Size now easy eat hand how. Unwilling he departure elsewhere dejection at. Heart large seems may purse means few blind. Exquisite newspaper attending on certainty oh suspicion of. He less do quit evil is. Add matter family active mutual put wishes happen. For norland produce age wishing. To figure on it spring season up. Her provision acuteness had excellent two why intention. As called mr needed praise at. Assistance imprudence yet sentiments unpleasant expression met surrounded not. Be at talked ye though secure nearer. Suppose end get boy warrant general natural. Delightful met sufficient projection ask. Decisively everything principles if preference do impression of. Preserved oh so difficult repulsive on in household. In what do miss time be. Valley as be appear cannot so by. Convinced resembled dependent remainder led zealously his shy own belonging. Always length letter adieus add number moment she. Promise few compass six several old offices removal parties fat. Concluded rapturous it intention perfectly daughters is as. Am terminated it excellence invitation projection as. She graceful shy believed distance use nay. Lively is people so basket ladies window expect. Supply as so period it enough income he genius. Themselves acceptance bed sympathize get dissimilar way admiration son. Design for are edward regret met lovers. This are calm case roof and. ";
        // PVD
        for(int redBits = 1; redBits < 2; redBits++){
            for(int greenBits = 1; greenBits < 2; greenBits++){
                for(int blueBits = 1; blueBits < 2; blueBits++){
                    for(boolean random : new boolean[] {false}) {
                        for(boolean red : new boolean[] {false}) {
                            for (boolean green : new boolean[]{true}) {
                                for (boolean blue : new boolean[]{false}) {
                                    String result = "";
                                    if(red == false && green == false && blue == false){
                                        break;
                                    }
                                    count += 1;
                                    BufferedImage stegoImage = pvdEncoder.encode(coverImage, red, green, blue,
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
