package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Performs LSB encoding on several algorithmic techniques
 */
public class encode {

    /**
     * Stores the number of parameters in the image
     */
    private int param_number;

    /**
     * Store the number of bits each parameter requires
     */
    private int[] param_lengths;

    /**
     * Holds the PRNG class and all it's funcions
     */
    private pseudorandom generator;

    /**
     * The image to be manipulated
     */
    private BufferedImage coverImage;

    boolean red;
    boolean green;
    boolean blue;
    boolean random;
    int algorithm;
    int[] endPosition;
    int[] coloursToConsider;


    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public encode() {
        // Default values
        param_number = 7;
        param_lengths = new int[param_number];
        param_lengths[0] = 1;  // Length for red (Bool)
        param_lengths[1] = 1;  // Length for green (Bool)
        param_lengths[2] = 1;  // Length for blue (Bool)
        param_lengths[3] = 1;  // Length for random (bool)
        param_lengths[4] = 2;  // Length for which algorithm is in use
        param_lengths[5] = 15;  // Length for end x (Int)
        param_lengths[6] = 15;  // Length for end y (Int)

        red = false;
        green = false;
        blue = false;
        random = false;
        algorithm = 0;
        endPosition = new int[2];
        coloursToConsider = new int[3];

        coverImage = null;
    }



    // MAIN FUNCTION
    /**
     * Central HUB to control encoding into the image
     *
     * @param coverImage    Image to be used (acting as the Cover)
     * @param text          Data to be inserted (acting as the Payload)
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the blue colour channel will be used
     * @param blue          Determines whether the green colour channel will be used
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     * @return Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encodeImage(BufferedImage coverImage, String text,
                                boolean red, boolean green, boolean blue, boolean random, String seed,
                                     int algorithm) {

        // Define image
        this.coverImage = coverImage;

        //Set parameter data
        setupParameterData(red, green, blue, random, seed, algorithm);

        // Convert ASCII text to binary equivalent
        StringBuilder binaryText = getBinaryText(text);

        // Check whether text will fit into image
        int dataToInsert = (int) ((double) binaryText.length() / coloursToConsider.length) + IntStream.of(param_lengths).sum();
        int dataImageCanStore = coverImage.getWidth() * coverImage.getHeight() * coloursToConsider.length;
        if (dataToInsert < dataImageCanStore) {

            // Encode the data into the image
            int[] endPosition = encodeData(binaryText, coloursToConsider, "normal", random, algorithm);

            // Encode the parameters into the image
            encodeParameters(endPosition);

            return coverImage;
        }

        return null;
    }



    // ENCODING FUNCTIONS
    /**
     * Encodes the stream of binary data into the cover Image
     *
     * @param binary            Binary data to be encoded
     * @param coloursToConsider List of colours we will encode data into
     * @param dataType          Defines the type of data being embedded
     * @param random            Determines whether the PRNG will be used
     * @param algorithm         The algorithm being used to decode
     * @return Tuple storing final insertion parameters
     */
    public int[] encodeData(StringBuilder binary,
                            int[] coloursToConsider, String dataType, boolean random,
                            int algorithm) {

        if(algorithm == 0 || algorithm == 1){
            return encodeLSB(binary, coloursToConsider, dataType, random, algorithm);
        }

        if(algorithm == 2){
            return encodeLSBMR(binary, coloursToConsider, dataType, random);
        }
        return  null;
    }

    /**
     * Gets the position to start decoding data from
     * (HOPEFULLY USE ONE FUNCTION FOR ENCODING AND DECODING EVENTUALLY!!)
     *
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param dataType              Defines the type of data being retrieved
     * @return                      Position to start decoding (dependent on what we're decoding)
     */
    public int[] getStartPosition(boolean random, int[] coloursToConsider, String dataType){
        if(dataType.equals("colour")){
            return new int[] {0, 0};
        }

        if(dataType.equals("random")){
            return new int[] {1, 0};
        }

        if(dataType.equals("position")){
            return new int[] {2, 0};
        }

        if(random){
            int position = generator.getNextElement();
            return new int[]{position % coverImage.getWidth(), position / coverImage.getWidth()};
        }else{
            return new int[] {12, 0};
        }
    }

    /**
     * Generates next position of the data within image
     *
     * @param currentPosition The position which data has just been encoded
     * @param random          Determines whether random embedding should be used
     * @return The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition, boolean random) {
        int imageWidth = coverImage.getWidth();
        if (random) {
            int position = generator.getNextElement();
            currentPosition[0] = position % imageWidth;
            currentPosition[1] = position / imageWidth;
        } else {
            currentPosition[0] = (currentPosition[0] + 1) % imageWidth;
            if (currentPosition[0] == 0) {
                currentPosition[1] += 1;
            }
        }
        return currentPosition;
    }



    // LSB / LSBM ENCODING FUNCTIONS
    public int[] encodeLSB(StringBuilder binary,
                          int[] coloursToConsider, String dataType, boolean random,
                           int algorithm){

        // Initialise starting variables
        int[] currentPosition = getStartPosition(random, coloursToConsider, dataType);
        int[] pixelData = getPixelData(currentPosition[0], currentPosition[1]);

        // Represents current position (+1) in coloursToConsider (i.e. the colour being considered)
        // It's +1 due to difficulty with the MOD function
        int currentColour = 1;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 1) {
            // Get pixel data of current pixel
            if(currentColour % (coloursToConsider.length + 1) == 0){
                currentColour = 1;
                currentPosition = generateNextPosition(currentPosition, random);
                pixelData = getPixelData(currentPosition[0], currentPosition[1]);
            }

            // Update current pixel data
            char data = binary.charAt(i);
            pixelData[coloursToConsider[currentColour - 1]] = updateLSB(pixelData[coloursToConsider[currentColour - 1]], data, algorithm);

            // Write data to current pixel
            writePixelData(pixelData, currentPosition[0], currentPosition[1]);

            // Update current colour
            currentColour += 1;
        }

        // Return next position
        currentPosition = generateNextPosition(currentPosition, random);
        return new int[]{currentPosition[0], currentPosition[1]};
    }


    // LSBMR ENCODING FUNCTIONS
    public int[] encodeLSBMR(StringBuilder binary, int[] coloursToConsider, String dataType, boolean random){

        StringBuilder binaryData = new StringBuilder();

        // Initialise starting variables
        int[] currentPosition = getStartPosition(random, coloursToConsider, dataType);
        int[] pixelData = getPixelData(currentPosition[0], currentPosition[1]);
        int firstColour = 0;
        int secondColour = 0;

        int[] firstColourInfo = new int[3];
        int[] secondColourInfo = new int[3];
        int[] firstColourPixelData = new int[3];
        int[] secondColourPixelData = new int[3];

        // Represents current position (+1) in coloursToConsider (i.e. the colour being considered)
        // It's +1 due to difficulty with the MOD function
        int currentColour = 1;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 2) {

            // Get bits required from binary input
            char data_1 = binary.charAt(i);
            char data_2 = binary.charAt(i + 1);

            // Get first colour to consider
            if(currentColour % (coloursToConsider.length + 1) == 0){
                currentColour = 1;
                currentPosition = generateNextPosition(currentPosition, random);
            }
            pixelData = getPixelData(currentPosition[0], currentPosition[1]);
            firstColour = pixelData[coloursToConsider[currentColour - 1]];
            firstColourInfo[0] = currentPosition[0];
            firstColourInfo[1] = currentPosition[1];
            firstColourInfo[2] = currentColour;
            firstColourPixelData = pixelData;

            // Get second colour to consider (done cleverly)
            currentColour += 1;
            if(currentColour % (coloursToConsider.length + 1) == 0){
                currentColour = 1;
                currentPosition = generateNextPosition(currentPosition, random);
            }
            pixelData = getPixelData(currentPosition[0], currentPosition[1]);
            secondColour = pixelData[coloursToConsider[currentColour - 1]];
            secondColourInfo[0] = currentPosition[0];
            secondColourInfo[1] = currentPosition[1];
            secondColourInfo[2] = currentColour;
            secondColourPixelData = pixelData;

            // Increment position in coloursToConsider again
            currentColour += 1;

            // Get the binary relationships between the firstColour and secondColour
            int pixel_Relationship = (int) Math.floor(firstColour / 2) + secondColour;
            String LSB_Relationship = getBinaryLSB(pixel_Relationship);
            int pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
            String LSB_Relationship_2 = getBinaryLSB(pixel_Relationship_2);

            // Get LSBs of pixel colour
            String firstColourLSB = getBinaryLSB(firstColour);
            String secondColourLSB = getBinaryLSB(secondColour);

            // Case 1: firstColour NORMAL, secondColour +-1
            if (Character.toString(data_1).equals(firstColourLSB) &&
                        !(Character.toString(data_2).equals(LSB_Relationship))) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    secondColour -= 1;
                } else {
                    secondColour += 1;
                }
                secondColourPixelData[coloursToConsider[secondColourInfo[2]]] += 1;
                writePixelData(secondColourPixelData, secondColourInfo[0], secondColourInfo[1]);
            // Case 2: firstColour NORMAL, secondColour NORMAL
            }else if (Character.toString(data_1).equals(firstColourLSB) &&
                        Character.toString(data_2).equals(LSB_Relationship)) {
            // Case 3: firstColour -1, secondColour NORMAL
            } else if (!(Character.toString(data_1).equals(firstColourLSB)) &&
                        Character.toString(data_2).equals(LSB_Relationship_2)) {
                firstColour -= 1;
                firstColourPixelData[coloursToConsider[firstColourInfo[2] - 1]] += 1;
                writePixelData(firstColourPixelData, firstColourInfo[0], firstColourInfo[1]);
            // Case 4: firstColour +1, secondColour NORMAL
            } else {
                firstColourPixelData[coloursToConsider[firstColourInfo[2] - 1]] += 1;
                writePixelData(firstColourPixelData, firstColourInfo[0], firstColourInfo[1]);
            }
        }

        // Return next position
        currentPosition = generateNextPosition(currentPosition, random);
        return new int[]{currentPosition[0], currentPosition[1]};
    }

    public String getBinaryLSB(int number){
        String binary = Integer.toBinaryString(number);
        String LSB = binary.substring(binary.length() - 1);
        return LSB;
    }




    // PARAMETER FUNCTIONS
    /**
     * Sets the parameter data for the cover image
     *
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the blue colour channel will be used
     * @param blue          Determines whether the green colour channel will be used
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     */
    public void setupParameterData(boolean red, boolean green, boolean blue,
                                   boolean random, String seed, int algorithm){

        // Set colours used
        this.red = red;
        this.green = green;
        this.blue = blue;
        coloursToConsider = getColoursToConsider(this.red, this.green, this.blue);

        // Set random
        this.random = random;
        if (random) {
            generator = new pseudorandom(coverImage.getHeight(), coverImage.getWidth(), seed);
        }

        //Set algorithm
        this.algorithm = algorithm;

        // Cannot define end position as we don't know it yet
    }

    /**
     * Gets all the parameters and stores them with their corresponding number of bits
     * required to store
     *
     * @param endPosition           Tuple storing final insertion parameters
     */
    public void encodeParameters(int[] endPosition) {
        // Encode colours used in first bit
        StringBuilder parameters = new StringBuilder();
        parameters.append(getBinaryParameters(red ? 1 : 0, param_lengths[0]));
        parameters.append(getBinaryParameters(green ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(blue ? 1 : 0, param_lengths[2]));
        encodeData(parameters, new int[]{0, 1, 2}, "colour", false, 0);

        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(random ? 1 : 0, param_lengths[3]));
        parameters.append(getBinaryParameters(algorithm, param_lengths[4]));
        encodeData(parameters, new int[]{0, 1, 2}, "random", false, 0);

        // Encode all other parameters as you would with data
        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(endPosition[0], param_lengths[5]));
        parameters.append(getBinaryParameters(endPosition[1], param_lengths[6]));
        encodeData(parameters, new int[]{0, 1, 2}, "position", false, 0);
    }


    // LSB MANIPULATION FUNCTIONS
    /**
     * Gets pixel data from an image at a specific location
     *
     * @param x          x coordinate of pixel
     * @param y          y coordinate of pixel
     * @return Tuple of RGB values (representing the pixel)
     */
    public int[] getPixelData(int x, int y) {
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new int[]{red, green, blue};
    }

    /**
     * Inserts data into pixel's LSB
     *
     * @param colour        Original colour to be manipulated
     * @param data          Data to be inserted
     * @param algorithm     The algorithm being used (0-2)
     * @return              Updated colour
     */
    public int updateLSB(int colour, char data, int algorithm) {
        if(algorithm == 0) {
            if (colour % 2 == 0 && data == '1') {
                return colour + 1;
            } else if (colour % 2 == 1 && data == '0') {
                return colour - 1;
            }
        }
        if(algorithm == 1){
            String binaryColour = Integer.toBinaryString(colour);
            if (!(binaryColour.substring(binaryColour.length() - 1).equals(Character.toString(data)))) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    colour -= 1;
                } else {
                    colour += 1;
                }
            }
        }
        return colour;
    }

    /**
     * Writes pixel data to an image at a specific location
     *
     * @param pixelData  Typle of RGB values (representing the pixel)
     * @param x          x coordinate of pixel
     * @param y          y coordinate of pixel
     */
    public void writePixelData(int[] pixelData, int x, int y) {
        Color newColour = new Color(pixelData[0], pixelData[1], pixelData[2]);
        int newRGB = newColour.getRGB();
        coverImage.setRGB(x, y, newRGB);
    }



    // BINARY CONVERSION FUNCTIONS
    /**
     * Converts input text into binary equivalent
     *
     * @param text The ASCII text to be converted to binary
     * @return Binary Equivalent of ASCII text
     */
    public StringBuilder getBinaryText(String text) {
        byte[] bytes = text.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            String binaryData = Integer.toBinaryString(b);
            String formatted = ("00000000" + binaryData).substring(binaryData.length());
            binary.append(formatted);
        }
        return binary;
    }

    /**
     * Converts input parameter into binary equivalent
     *
     * @param parameter         Parameter to be converted
     * @param parameter_length  Required length of parameter (in bits)
     * @return The binary equivalent of the input parameter (correct to number of bits)
     */
    public String getBinaryParameters(int parameter, int parameter_length) {
        String binaryParameter = Integer.toBinaryString(parameter);
        binaryParameter = (StringUtils.repeat('0', parameter_length) + binaryParameter).substring(binaryParameter.length());
        return binaryParameter;
    }



    // SETUP FUNCTIONS
    /**
     * Gets the colours that will be used
     *
     * @param red   Boolean to whether red will be used
     * @param green Boolean to whether green will be used
     * @param blue  Boolean to whether blue will be used
     * @return An int[] array storing only the colours it considers
     */
    public int[] getColoursToConsider(boolean red, boolean green, boolean blue) {
        if (red && green && blue) {
            return new int[]{0, 1, 2};
        } else if (red && green) {
            return new int[]{0, 1};
        } else if (red && blue) {
            return new int[]{0, 2};
        } else if (blue && green) {
            return new int[]{1, 2};
        } else if (red) {
            return new int[]{0};
        } else if (green) {
            return new int[]{1};
        } else if (blue) {
            return new int[]{2};
        }
        return new int[]{0, 1, 2};
    }
}