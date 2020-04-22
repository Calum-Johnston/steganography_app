package testingEnv;

import com.java.calumjohnston.algorithms.decodeData;
import com.java.calumjohnston.algorithms.encodeData;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.sobelEdgeDetection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.Buffer;

import static org.junit.Assert.*;

/**
 * Runs tests on the various algorithms in the program
 */
public class unittest {

    /**
     * Constructor for the class
     */
    public unittest() {
        runTests();
    }

    /**
     * Runs the tests
     */
    public void runTests() {

        encodeData encoder = new encodeData();
        decodeData decoder = new decodeData();

        BufferedImage coverImage = null;
        String image_name = "2.png";
        try{
            coverImage = ImageIO.read(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\src\\testingEnv\\" + image_name));
        }catch(Exception e){}

        coverImage = convert(coverImage);

        String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxys1234567890";
        String seed = "Calum";
        text = "Research more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run fromEncoding Menu:- Remove main method from class- Add drop down to select algorithm- Inprove design\t- Spacing improvements\t- Add textures / background- Add verification (i.e. to when we can encode, etc)Decoding Menu:- Setup decoding class (main method, constructor, etc)- Implement decoding for LSBM- Implement decoding for LSBMR- Add drop down to select algorithm when decoding- POTENTIAL: Automatically detect which algorithm- Improve design:\t- Spacing improvements\t- More user friendly (textures, backgrouds, etc)- Error checking, etcPRNG:- Develop method to randomly hide data in image based on seed - Seed must be provided by the userSources:- Sort out sources document (between sources.txt and word document)- Ensure to annonate correctly'Dissertation Structure'- Define all section headings / subsections and what to include in diss- Relate each heading / sub-heading to sources in Sources.txt- Confirm with MatthewText Documents:- Make an LSB.txt to describe LSB- Make an LSBM.txt to describe LSBM- Make an LSBMR.txt to describe LSBMR- Make a PRNG.txt to describe Pseudo Random Number GeneratorResearch more algorithms to implementImprove sources:- Remove unused sources- Acquire more sources for paper writing (i.e ones that discuss history, etc)- Acquire more sources on algorithms (even if not implemented) - for related work!To do:'Program Structure.txt'- Update project structure so it is known what to include in the programMain Menu:- Implement methods to get to GUI's from there- Add some design to it (logo,background,etc)- Implement main method that the program will run from";
        //text = "ABCDEFGHIJKLMNOPQRSUVWXYZa";
        //text = "abcdefgh";
        text = "d5uUIAO8wpZ6uE5WdzkAumQpaB1pdy8L8z4ry882PTMG5hxSunzmcERFwF8O3LfRnyevyvTpvAN46wGgLASsZsjrfmSLjpclgc3Re8XEQE0yyUBOkVzoteiXVrGaCG0sqiacYgjr510CAXpaKQT4ROckoscbN4T3Dssbvb8UEIodJeR6VxSfiax5QKMnRBBD4X59rKnOXVkFHoGBIITgLoHcdK85eXjpZN7rIpilhSwRgxeErESEjtdPadqYMFPqwjIRcqpLA1ZrlPPMD3VMP3l8JUmiLTxYaia9v38wh5E4e1C12sDTFNfrahNkeMu5hfMpwxrH1HMEvaH8X6m9oqbC11elTsKNJ4IZVi0znfQdcAbsWoi2CwQLq3RbNvCzlmKViHzBY7rFPZ58EWoJN4d0wzUjzmajjYHTpQayA9Kl2YenDr2Nddf9JkfAM2hZNDgooXi9QCECsYcafBpaHxOdcH4FgHNtvXMunSmrSikfE1sD1EdJYy6MpQvuhfJVXOWpZ3OHKSgh8WW7KUpOMr9rQkczPhlkDj9TV20vEBXqRkyAVsMVZawE1uRf8CPjBM6RugKek4tyOYQtcZ5EOgMdfLu9yaygiZMCFyPIbP2seHM77AYnuyGzOo2IG8ZGJ3av9pyX4TfWkYmdSFQddweErNPS5rWELRjRj0tlZMwpnMzT4ht01dFau07wEgUk5ywbD2ud521vMVXu8RXQsWwcBLo0SJEoaJkQqSgWSNc9e20pqaLCEZjC0qcO8XgSIUIYEwWGJWDeBqb6TStTIuNRUrdsCfTtrRnABickcXPjnrjhzBR8vcQaN7tVfNQoa40i92sdx7aVM9MQEWncMOzWR9BPKeiFOmUISRVKVSQCHaOfdtmqNaQeAjP9oVuG0lfLoBlL3uX8OdEW9MrttO7c1iOx4dFcyleIXgcLfiLp0H8oVWgIR60QLH6x4MN7AQrWvSURbAz6NVSJsYnOsTNT2myU4eB84Hn926nIeifPhjLF7m0a8M8Gi01EJmEjlEYq7DWtTmchUM8Np1qgd0WytcZUKzuoicwWXqOk1mes1vr4J4zAYYhTqgZsoVnVPVohtup9NhIz97m0DQC6pmRPvGRYkWcWXDpqc2aJay9Rkp5rgFmMEgpbTREsqXjb8ZTawiR6Y5C0iD9LhFovyshWvVuWDEI9dtiCGaVhQDlmANoB9V9EiLKcYSGg1n1cwuHuWcMFoYbWBUyLBzZFd3UmhFxh3EAP4VI3KJ1Q7rdZw72vciPXh0Tifew5JCYeOMCOpCLlRIbAslO0GVC5g441axJXk0H5pWIK3Y0YVnL5zFyPMJfDXUTcmFlzceKBeo0xXDK48ZtFOksaUmn8Z2F6ZA6LoU36zE52G1KSaFSlQ1d3ruIqmd3unPmp1012441Hzwx7MesBNP7vLUrboXb4UapPEJuup8kWOxjzQLQiMNf8C9wyrALwFJPNYIKA6lhT0cM3CGujfBEEA0xSltYP1oatvbFFF5bX80QfMooXyDvHhWUUUQLKQVHrY82SjAWIOVX7c2jahQ6ZOgcFLe5WDF0CxfhWcpC0A3wp13XOQKS7IIUpx7TMfN3JnUBc33st7FoamBwL6Ei6XOqosiuYEARuHpejIzGJHX";
        long startTime = System.currentTimeMillis();

        // LSB, OPAP & LSBM
        int count = 0; int fail = 0;
        for(int algorithm = 6; algorithm < 7; algorithm++) {
            for(boolean random : new boolean[] {false, true}) {
                count += 1;
                String result = "";
                BufferedImage stegoImage = encoder.encode(deepCopy(coverImage), algorithm, random, seed, text);
                String assertionError = null;
                try {
                    result = decoder.decode(stegoImage, true);
                    assertEquals(text, result);
                } catch (AssertionError ae) {
                    assertionError = ae.toString();
                }
                if (assertionError != null) {
                    System.out.println("Algorithm: " + algorithm);
                    System.out.println("Random: " + random + ", with seed: " + seed);
                    System.out.println("Text: " + text);
                    System.out.println("Result: " + result);
                    fail += 1;
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
        unittest t = new unittest();
    }

    public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public BufferedImage convert(BufferedImage src){
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(src, 0, 0,null);
        g2d.dispose();
        return img;
    }

}
