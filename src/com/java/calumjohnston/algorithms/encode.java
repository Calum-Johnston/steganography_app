package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
    int redBits;
    int greenBits;
    int blueBits;
    int algorithm;
    int[] endPosition;
    int[] coloursToConsider;
    ArrayList<Integer> lsbToConsider;


    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public encode() {
        // Default values
        param_number = 11;
        param_lengths = new int[param_number];
        param_lengths[0] = 1;  // Length for red (Bool)
        param_lengths[1] = 1;  // Length for green (Bool)
        param_lengths[2] = 1;  // Length for blue (Bool)
        param_lengths[3] = 1;  // Length for random (bool)
        param_lengths[4] = 2;  // Length for which algorithm is in use
        param_lengths[5] = 15;  // Length for end x (Int)
        param_lengths[6] = 15;  // Length for end y (Int)
        param_lengths[7] = 3;  // Length for red bits (Int) - MAX VALUE 7
        param_lengths[8] = 3;  // Length for green bits (Int) - MAX VALUE 7
        param_lengths[9] = 3;  // Length for blue bits (Int) - MAX VALUE 7
        param_lengths[10] = 5;  // Length for final pos (Int) - MAX VALUE 23


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
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     * @return Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encodeImage(BufferedImage coverImage, String text,
                                        boolean red, boolean green, boolean blue,
                                        int redBits, int greenBits, int blueBits,
                                        boolean random, String seed, int algorithm) {

        // Define image
        this.coverImage = coverImage;

        //Set parameter data
        setupParameterData(red, green, blue, redBits, greenBits, blueBits, random, seed, algorithm);

        // Convert ASCII text to binary equivalent
        StringBuilder binaryText = getBinaryText(text);

        // Check whether text will fit into image
        int dataToInsert = (int) ((double) binaryText.length() / coloursToConsider.length) + IntStream.of(param_lengths).sum();
        int dataImageCanStore = coverImage.getWidth() * coverImage.getHeight() * coloursToConsider.length;
        if (dataToInsert < dataImageCanStore) {

            // Encode the data into the image
            int[] endPositionData = encodeData(binaryText, coloursToConsider, random, algorithm);

            // Encode the parameters into the image
            encodeParameterData(endPositionData);

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
     * @param random            Determines whether the PRNG will be used
     * @param algorithm         The algorithm being used to encode data
     * @return                  Position where data embedding stops
     */
    public int[] encodeData(StringBuilder binary, int[] coloursToConsider, boolean random, int algorithm) {

        if(algorithm == 0 || algorithm == 1){
            return encodeLSB(binary, coloursToConsider, random, algorithm);
        }

        if(algorithm == 2){
            return encodeLSBMR(binary, coloursToConsider, random);
        }
        return  null;
    }

    /**
     * Performs the embedding of data into the cover image using LSB / LSBM
     *
     * @param binary                The binary data to be embedded
     * @param coloursToConsider     List of colours that data was embedded into
     * @param random                Determines whether the PRNG will be used
     * @param algorithm             The algorithm being used to encode data
     * @return                      Position where data embedding stops
     */
    public int[] encodeLSB(StringBuilder binary,  int[] coloursToConsider, boolean random, int algorithm){

        // Initialise starting variables
        int[] currentPosition = getStartPosition(random);
        int[] pixelData;

        // Represents current colour being considered (pointing to position in coloursToConsider)
        int currentColour = 0;

        // Represents current LSB being considered (pointing to position in lsbToConsider)
        int currentLSB = 0;

        // Gets the pixel order we will consider
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        for(int i = 0; i < binary.length(); i++){
            ArrayList<Integer> current = new ArrayList<>();
            current.add(currentPosition[0]);
            current.add(currentPosition[1]);
            current.add(currentColour);
            current.add(currentLSB);
            order.add(current);
            currentLSB += 1;

            if(currentLSB == lsbToConsider.size()){
                currentLSB = 0;
            }
            if(lsbToConsider.get(currentLSB) == 0) {
                currentColour += 1;
                if ((currentColour + 1) % (coloursToConsider.length + 1) == 0) {
                    currentColour = 0;
                    currentPosition = generateNextPosition(currentPosition, random);
                }
            }
        }

        // Define lists to store data about colours
        ArrayList<Integer> colourData;

        // Loop through binary data to be inserted
        for(int i = 0; i < binary.length(); i++) {

            // Get bit required from binary input
            char data_1 = binary.charAt(i);

            // Get the next positional information to consider
            colourData = order.get(i);

            // Get pixel data of current pixel
            pixelData = getPixelData(colourData.get(0), colourData.get(1));

            // Get the current LSB being accessed (points to list containing LSB positions)
            currentLSB = colourData.get(3);

            // Update current pixel data
            pixelData[coloursToConsider[colourData.get(2)]] = updateLSB(pixelData[coloursToConsider[colourData.get(2)]], data_1, lsbToConsider.get(currentLSB), algorithm);

            // Write data to current pixel
            writePixelData(pixelData, colourData.get(0), colourData.get(1));

            // Could improve efficiency by only writing / getting new pixel data when position changes
        }

        // Return next position
        currentLSB += 1;
        if(currentLSB == lsbToConsider.size()){
            currentLSB = 0;
        }
        return new int[]{currentPosition[0], currentPosition[1], currentLSB};
    }

    /**
     * Performs the embedding of data into the cover image using LSBMR
     *
     * @param binary                The binary data to be embedded
     * @param coloursToConsider     List of colours that data was embedded into
     * @param random                Determines whether the PRNG will be used
     * @return                      Position where data embedding stops
     */
    public int[] encodeLSBMR(StringBuilder binary, int[] coloursToConsider, boolean random){

        // Initialise starting variables
        int[] currentPosition = getStartPosition(random);

        // Represents current position (+1) in coloursToConsider (i.e. the colour being considered)
        // It's +1 due to difficulty with the MOD function
        int currentColour = 0;

        // Gets the pixel order we will consider
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        for(int i = 0; i < binary.length(); i++){
            if((currentColour + 1) % (coloursToConsider.length + 1) == 0){
                currentColour = 0;
                currentPosition = generateNextPosition(currentPosition, random);
            }
            ArrayList<Integer> current = new ArrayList<>();
            current.add(currentPosition[0]);
            current.add(currentPosition[1]);
            current.add(currentColour);
            currentColour += 1;
            order.add(current);
        }

        // Define variables to store the pixel data of each colour being accessed
        int[] firstColourPixelData;
        int[] secondColourPixelData;

        // Define lists to store data about colours
        ArrayList<Integer> firstColourData;
        ArrayList<Integer> secondColourData;

        // Define variables to store exact data of colour channel being accessed
        int firstColour;
        int secondColour;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 2) {

            // Get bits required from binary input
            char data_1 = binary.charAt(i);
            char data_2 = binary.charAt(i + 1);

            // Get the next two positional information to consider
            firstColourData = order.get(i);
            secondColourData = order.get(i + 1);

            // Get the pixel information for each colour
            firstColourPixelData = getPixelData(firstColourData.get(0), firstColourData.get(1));
            secondColourPixelData = getPixelData(secondColourData.get(0), secondColourData.get(1));

            // Get the colours from each pixel
            firstColour = firstColourPixelData[firstColourData.get(2)];
            secondColour = secondColourPixelData[secondColourData.get(2)];

            // Get the binary relationships between the firstColour and secondColour
            int pixel_Relationship = (int) Math.floor(firstColour / 2) + secondColour;
            String LSB_Relationship = getBinaryLSB(pixel_Relationship);
            int pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
            String LSB_Relationship_2 = getBinaryLSB(pixel_Relationship_2);

            // Get LSBs of pixel colour
            String firstColourLSB = getBinaryLSB(firstColour);

            // Determines what to embed based on LSBss and relationship between them
            // Could add an update LSB function here too!! (for pixelData!!)
            if (Character.toString(data_1).equals(firstColourLSB)) {
                if(!(Character.toString(data_2).equals(LSB_Relationship))) {
                    if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                        secondColourPixelData[secondColourData.get(2)] -= 1;
                        if(secondColourPixelData[secondColourData.get(2)] == -1){
                            secondColourPixelData[secondColourData.get(2)] = 255;
                        }
                    } else {
                        secondColourPixelData[secondColourData.get(2)] += 1;
                        if(secondColourPixelData[secondColourData.get(2)] == 256){
                            secondColourPixelData[secondColourData.get(2)] = 0;
                        }
                    }

                    writePixelData(secondColourPixelData, secondColourData.get(0), secondColourData.get(1));
                }
            } else if (!(Character.toString(data_1).equals(firstColourLSB))) {
                if (Character.toString(data_2).equals(LSB_Relationship_2)) {
                    firstColourPixelData[firstColourData.get(2)] -= 1;
                    if(firstColourPixelData[firstColourData.get(2)] == -1){
                        firstColourPixelData[firstColourData.get(2)] = 255;
                    }
                    writePixelData(firstColourPixelData, firstColourData.get(0), firstColourData.get(1));
                } else {
                    firstColourPixelData[firstColourData.get(2)] += 1;
                    if(firstColourPixelData[firstColourData.get(2)] == 256){
                        firstColourPixelData[firstColourData.get(2)] = 0;
                    }
                    writePixelData(firstColourPixelData, firstColourData.get(0), firstColourData.get(1));
                }
            }
        }

        // Return next position
        currentPosition = generateNextPosition(currentPosition, random);
        return new int[]{currentPosition[0], currentPosition[1], 0};
    }

    /**
     * Gets the position to start decoding data from
     * (HOPEFULLY USE ONE FUNCTION FOR ENCODING AND DECODING EVENTUALLY!!)
     *
     * @param random                Determines whether random embedding was used
     * @return                      Position to start decoding (dependent on what we're decoding)
     */
    public int[] getStartPosition(boolean random){
        if(random){
            int position = generator.getNextElement();
            return new int[]{position % coverImage.getWidth(), position / coverImage.getWidth()};
        }else{
            return new int[] {17, 0};
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



    // PARAMETER FUNCTIONS
    /**
     * Sets the parameter data for the cover image
     *
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param random        Determines whether random embedding should be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     */
    public void setupParameterData(boolean red, boolean green, boolean blue, int redBits, int greenBits, int blueBits,
                                   boolean random, String seed, int algorithm){

        // Set colours
        this.red = red;
        this.green = green;
        this.blue = blue;
        coloursToConsider = getColoursToConsider(this.red, this.green, this.blue);

        // Set colour LSBs (store as 1 less than number (due to size in programming))
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
        lsbToConsider = getLSBsToConsider(this.redBits, this.greenBits, this.blueBits, this.red, this.green, this.blue);

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
     * @param endPositionData           Tuple storing information about the stopping point of embedding
     */
    public void encodeParameterData(int[] endPositionData) {
        StringBuilder parameters = new StringBuilder();
        parameters.append(getBinaryParameters(red ? 1 : 0, param_lengths[0]));
        parameters.append(getBinaryParameters(green ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(blue ? 1 : 0, param_lengths[2]));
        parameters.append(getBinaryParameters(random ? 1 : 0, param_lengths[3]));
        parameters.append(getBinaryParameters(algorithm, param_lengths[4]));
        parameters.append(getBinaryParameters(endPositionData[0], param_lengths[5]));
        parameters.append(getBinaryParameters(endPositionData[1], param_lengths[6]));
        parameters.append(getBinaryParameters(redBits - 1, param_lengths[7]));
        parameters.append(getBinaryParameters(greenBits - 1, param_lengths[8]));
        parameters.append(getBinaryParameters(blueBits - 1, param_lengths[9]));
        parameters.append(getBinaryParameters(endPositionData[2], param_lengths[10]));
        encodeParameters(parameters);
    }

    /**
     * Performs the embedding of parameter data into the cover image using LSB
     *
     * @param parameters        The data to be embedded into the image
     */
    public void encodeParameters(StringBuilder parameters){

        // Initialise variables for encoding
        int[] coloursToConsider = new int[] {0, 1, 2};
        int[] currentPosition = new int[] {0, 0};
        int[] pixelData =getPixelData(currentPosition[0], currentPosition[1]);
        int currentColour = 0;

        // Loop through binary data to be inserted
        for (int i = 0; i < parameters.length(); i += 1) {

            // Get pixel data of current pixel
            if ((currentColour + 1) % (coloursToConsider.length + 1) == 0) {
                currentColour = 0;
                currentPosition = generateNextPosition(currentPosition, false);
                pixelData = getPixelData(currentPosition[0], currentPosition[1]);
            }

            // Update current pixel data
            char data = parameters.charAt(i);
            pixelData[coloursToConsider[currentColour]] = updateLSB(pixelData[coloursToConsider[currentColour]], data, 0, 0);

            // Write data to current pixel
            writePixelData(pixelData, currentPosition[0], currentPosition[1]);

            // Update current colour
            currentColour += 1;
        }
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
     * @param position      The position in the colour's 8 bit representation we are considering
     * @param algorithm     The algorithm being used (0-2)
     * @return              Updated colour
     */
    public int updateLSB(int colour, char data, int position, int algorithm) {
        if(algorithm == 0) {
            StringBuilder binaryColour = new StringBuilder();
            binaryColour.append(getBinaryParameters(colour, 8));
            int lsb_Position = 7 - position;
            if(binaryColour.charAt(lsb_Position) != data){
                binaryColour.setCharAt(lsb_Position, data);
            }
            return Integer.parseInt(binaryColour.toString(), 2);
        }

        if(algorithm == 1){
            StringBuilder binaryFirstHalf = new StringBuilder();
            StringBuilder binarySecondHalf = new StringBuilder();
            int firstHalfNum;
            String binaryColour = getBinaryParameters(colour, 8);
            binaryFirstHalf.append(binaryColour.substring(0, 8 - position));
            binarySecondHalf.append(binaryColour.substring(8 - position));
            firstHalfNum = Integer.parseInt(binaryFirstHalf.toString(), 2);
            if(binaryFirstHalf.charAt(binaryFirstHalf.length() - 1) != data) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    firstHalfNum -= 1;
                    if(firstHalfNum < 0){
                        firstHalfNum = 1;
                    }
                } else {
                    firstHalfNum += 1;
                    int maxBinary = (int) Math.pow(2, binaryFirstHalf.length()) - 1;
                    if(firstHalfNum > maxBinary){
                        firstHalfNum = maxBinary - 1;
                    }
                }
            }
            return Integer.parseInt(Integer.toBinaryString(firstHalfNum) + binarySecondHalf.toString(), 2);
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

    /**
     * Gets the least significant bit of some number
     *
     * @param number        The number to use
     * @return              The LSB of the number (in binary)
     */
    public String getBinaryLSB(int number){
        String binary = Integer.toBinaryString(number);
        String LSB = binary.substring(binary.length() - 1);
        return LSB;
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
     * @return      The colours that will be considered
     */
    /**
     * Gets the colours that will be used
     *
     * @param red   Boolean to whether red will be used
     * @param green Boolean to whether green will be used
     * @param blue  Boolean to whether blue will be used
     * @return      The colours that will be considered
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

    /**
     * Gets the LSBs that will be used for each colour
     *
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @return              The LSBs that will be considered for each colour (same order as coloursToConsider)
     */
    public ArrayList<Integer> getLSBsToConsider(int redBits, int greenBits, int blueBits,
                                   boolean red, boolean green, boolean blue){
        ArrayList<Integer> lsbToConsider = new ArrayList<Integer>();
        int count = 0;
        if(red) {
            for (int i = 0; i < redBits; i++) {
                lsbToConsider.add(count);
                count += 1;
            }
        }
        if(green) {
            count = 0;
            for (int i = redBits; i < greenBits + redBits; i++) {
                lsbToConsider.add(count);
                count += 1;
            }
        }
        if(blue) {
            count = 0;
            for (int i = redBits + greenBits; i < redBits + greenBits + blueBits; i++) {
                lsbToConsider.add(count);
                count += 1;
            }
        }
        return lsbToConsider;
    }
}