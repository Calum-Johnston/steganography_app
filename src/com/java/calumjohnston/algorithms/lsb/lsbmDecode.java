package com.java.calumjohnston.algorithms.lsb;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * LSBM Decode Class: This class implements the extraction of data from an image
 * that has been embedded using the LSBM technique
 */
public class lsbmDecode {

    BufferedImage stegoImage;
    boolean random;
    int[] coloursToConsider;
    ArrayList<Integer> lsbsToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int endLSBPosition;

    /**
     * Constructor
     */
    public lsbmDecode(){

    }

    /**
     * Acts as central controller to decoding functions
     *
     * @param stegoImage    The image we will decode data from
     * @return              The data to be hidden within the image
     */
    public String decode(BufferedImage stegoImage){
        // Setup data to be used for decoding
        setupData(stegoImage);

        // Check parameter data will allow for successful decoding
        checkData();

        // Decode data from the image
        StringBuilder binary = decodeSecretData();

        // Convert the data back to text
        String text = getText(binary);

        // Return the retrieved text
        return text;
    }



    // ======= SETUP & PARAMETER FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param stegoImage    The image we will decode data from
     */
    public void setupData(BufferedImage stegoImage){

        // Define the image we are reading data from
        this.stegoImage = stegoImage;

        // Get parameter binary data
        StringBuilder parameters = decodeParameters();

        // Get the colours to consider (some combination of red, green and blue)
        boolean red = binaryToInt(parameters.substring(3,4)) == 1;
        boolean green = binaryToInt(parameters.substring(4,5)) == 1;
        boolean blue = binaryToInt(parameters.substring(5,6)) == 1;
        this.coloursToConsider = getColoursToConsider(red, green, blue);

        int redBits = binaryToInt(parameters.substring(6, 9));
        int greenBits = binaryToInt(parameters.substring(9, 12));
        int blueBits = binaryToInt(parameters.substring(12, 15));
        this.lsbsToConsider = getLSBsToConsider(redBits, greenBits, blueBits, red, green, blue);

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(15, 16)) == 1;
        if (random) {
            this.generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), "");
        }

        // Get end position for data encoding
        endPositionX = binaryToInt(parameters.substring(16,31));
        endPositionY = binaryToInt(parameters.substring(31,46));
        endColourChannel = binaryToInt(parameters.substring(46, 48));
        endLSBPosition = binaryToInt(parameters.substring(48));

    }

    /**
     * Decodes the parameter data from the image using LSB
     *
     * @return      The binary parameter data that was hidden within the image
     */
    public StringBuilder decodeParameters(){

        // Initialise variables for encoding
        int[] coloursToConsider = new int[] {0, 1, 2};
        int[] currentPosition = new int[] {0, 0};
        int currentColourPosition = 0;
        int colour;
        StringBuilder parameters = new StringBuilder();

        // Loop through binary data to be inserted
        for (int i = 0; i < 53; i += 1) {

            // Get current colour to manipulate
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                currentPosition = generateNextPosition(currentPosition);
            }
            colour = getColourAtPosition(currentPosition[0], currentPosition[1], currentColourPosition);

            // Add data to binary parameters
            parameters.append(getLSB(colour));

            // Update current colour
            currentColourPosition += 1;
        }

        // Return the parameter binary data
        return parameters;
    }

    /**
     * Generates the next pixel position to consider when encoding data
     *
     * @param currentPosition   The position which data has just been encoded
     * @return                  The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition) {
        int imageWidth = stegoImage.getWidth();
        if (random) {
            int position = generator.getNextElement();
            return new int[] {position % imageWidth, position / imageWidth};
        } else {
            int newLine = (currentPosition[0] + 1) % imageWidth;
            if (newLine == 0) {
                return new int[] {0, currentPosition[1] + 1};
            }else{
                return new int[] {currentPosition[0] + 1, currentPosition[1]};
            }
        }
    }

    /**
     * Gets colour channel data from an image at a specific location
     *
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param colourChannel     The colour channel we are considering (R, G or B)
     * @return                  The value of the colour channel at position (x,y) in image
     */
    public int getColourAtPosition(int x, int y, int colourChannel) {
        int pixel = stegoImage.getRGB(x, y);
        if(colourChannel == 0){
            int red = (pixel & 0x00ff0000) >> 16;
            return red;
        }
        if(colourChannel == 1){
            int green = (pixel & 0x0000ff00) >> 8;
            return green;
        }
        if(colourChannel == 2){
            int blue = pixel & 0x000000ff;
            return blue;
        }
        return -1;
    }

    /**
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public int getLSB(int colour){
        if(colour % 2 == 0) {
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * Converts binary input to its integer equivalent
     *
     * @param binary        The binary input (1+ bits)
     * @return              The integer equivalent of binary
     */
    public int binaryToInt(String binary){
        return Integer.parseInt(binary, 2);
    }

    /**
     * Gets the colour channels that will be used
     *
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @return              The colours that will be considered when encoding data
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



    // ======= CHECK FUNCTIONS =======
    public void checkData(){

    }



    // ======= DECODING FUNCTIONS =======
    /**
     * Decodes the data to be hidden into the image
     *
     * @return          The data hidden within the image
     */
    public StringBuilder decodeSecretData(){

        // Define some initial variables required
        ArrayList<int[]> colourData = null;   // Stores data about the next two colours to manipulate
        int firstColour;            // Stores the first colour to be manipulated
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)

        // Define some variables for determining which pixels to manipulate
        int currentColourPosition = -1;
        int currentLSBPosition = -1     ;
        int[] firstPosition = generateNextPosition(new int[] {16, 0});

        // Loop through binary data to be inserted
        while(firstPosition[0] != endPositionX || firstPosition[1] != endPositionY || currentLSBPosition != endLSBPosition) {

            // Get the colour data required for embedding
            colourData = getNextData(firstPosition, currentColourPosition, currentLSBPosition);

            // Update positional information
            firstPosition = colourData.get(0);
            currentColourPosition = colourData.get(1)[0];
            currentLSBPosition = colourData.get(2)[0];

            // Get the next colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);

            // Append the retrieved binary data to the final string of data
            binary.append(getSpecificLSB(firstColour, lsbsToConsider.get(currentLSBPosition)));
        }

        return binary;
    }

    /**
     * Determines the next two pixels we should encode into
     *
     * @param firstPosition         The positional data of the first pixel being considered
     * @param currentColourPosition The current position in coloursToConsider we are using
     * @return                      The order we should consider pixel whilst encoding
     */
    public ArrayList<int[]> getNextData(int[] firstPosition, int currentColourPosition, int currentLSBPosition){

        // Define ArrayList to store data in
        ArrayList<int[]> current = new ArrayList<>();

        // Update current position to check for in coloursToConsider
        currentLSBPosition += 1;

        // Update positions (if ran out of colour channels to manipulate with current positions)
        if(currentLSBPosition == lsbsToConsider.size()){
            currentLSBPosition = 0;
        }
        if(lsbsToConsider.get(currentLSBPosition) == 0) {
            currentColourPosition += 1;
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                firstPosition = generateNextPosition(firstPosition);
            }
        }

        // Add data to ArrayList to return
        current.add(firstPosition);
        current.add(new int[] {currentColourPosition});
        current.add(new int[] {currentLSBPosition});

        return current;
    }

    /**
     * Gets the least significant bit of some integer at a certain point
     *
     * @param number        The number retrieve the data from
     * @param LSB           The LSB we are considering
     * @return              The LSB of the number (in binary)
     */
    public char getSpecificLSB(int number, int LSB){
        StringBuilder binaryColour = new StringBuilder();
        binaryColour.append(conformBinaryLength(number, 8));
        int lsb_Position = 7 - LSB;
        return binaryColour.charAt(lsb_Position);
    }

    /**
     * Converts some integer to binary of a defined length
     *
     * @param data      The data to be converted to binary
     * @param length    The number of bits to represent the data as
     * @return          The binary equivalent of data
     */
    public String conformBinaryLength(int data, int length){
        String binaryParameter = Integer.toBinaryString(data);
        binaryParameter = (StringUtils.repeat('0', length) + binaryParameter).substring(binaryParameter.length());
        return binaryParameter;
    }


    // ======= CONVERSION FUNCTIONS =======
    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return              ASCII representation of binary data
     */
    public String getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            try {
                String binaryData = binaryText.substring(i, i + 8);
                char characterData = (char) Integer.parseInt(binaryData, 2);
                text.append(characterData);
            } catch (Exception e) {
            }
        }
        return text.toString();
    }
}
