package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Decode Class: This class implements the extraction of data from an image
 * that has been embedded using the one of several LSB techniques
 */
public class decodeData {

    BufferedImage stegoImage;
    boolean random;
    int[] coloursToConsider;
    ArrayList<Integer> lsbsToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int endLSBPosition;
    int algorithm;
    String seed;

    /**
     * Constructor
     */
    public decodeData(){

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

        // Get the algorithm
        this.algorithm = binaryToInt(parameters.substring(0, 3));

        // Get the colours to consider (some combination of red, green and blue)
        boolean red = binaryToInt(parameters.substring(3,4)) == 1;
        boolean green = binaryToInt(parameters.substring(4,5)) == 1;
        boolean blue = binaryToInt(parameters.substring(5,6)) == 1;
        this.coloursToConsider = getColoursToConsider(red, green, blue);

        int redBits = binaryToInt(parameters.substring(6, 9)) + 1;
        int greenBits = binaryToInt(parameters.substring(9, 12)) + 1;
        int blueBits = binaryToInt(parameters.substring(12, 15)) + 1;
        this.lsbsToConsider = getLSBsToConsider(redBits, greenBits, blueBits, red, green, blue);

        // Set the seed
        this.seed = seed;

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(15, 16)) == 1;

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
                currentPosition[0] = (currentPosition[0] + 1) % stegoImage.getWidth();
                currentPosition[1] = (currentPosition[0] == 0 ? currentPosition[1] + 1 : currentPosition[1]);
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
     * Determines which encoding scheme to use based on the algorithm selected
     *
     * @return        The binary data hidden within the image
     */
    public StringBuilder decodeSecretData(){
        // Determine which encoding scheme to use
        if(algorithm <= 2 || algorithm == 5){
            return decodeSecretDataLSB();
        }else if(algorithm == 3){
            return decodeSecretDataLSBMR();
        }else{
            return decodeSecretDataPVD();
        }
    }

    /**
     * Decodes the data hidden in the image by either LSB or LSBM
     *
     * @return          The binary data to be hidden within the image
     */
    public StringBuilder decodeSecretDataLSB(){

        // Define some initial variables required
        ArrayList<Integer> colourData = null;     // Stores data about the next two colours to manipulate
        int colour;                             // Stores the first colour to be manipulated
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder();
        int count = 0;
        int[] position = new int[] {0, 0};
        int currentColourPosition = 0;
        int currentLSBPosition = 0;

        // Loop through binary data to be inserted
        while(position[0] != endPositionX || position[1] != endPositionY || currentColourPosition != endColourChannel ||
                currentLSBPosition != endLSBPosition) {

            // Get the colour data required for embedding
            colourData = order.get(count);
            count += 1;

            // Update positional information
            position = new int[] {colourData.get(0), colourData.get(1)};
            currentColourPosition = colourData.get(2);
            currentLSBPosition = colourData.get(3);

            // Get the next colour channel data
            colour = getColourAtPosition(position[0], position[1], currentColourPosition);

            // Append the retrieved binary data to the final string of data
            binary.append(getSpecificLSB(colour, lsbsToConsider.get(currentLSBPosition)));

        }
        return binary;
    }

    /**
     * Decodes the data hidden in the image by LSBMR
     *
     * @return          The binary data to be hidden within the image
     */
    public StringBuilder decodeSecretDataLSBMR(){

        // Define some variables for manipulating pixel data
        ArrayList<Integer> firstColourData = null;   // Stores data about the next two colours to manipulate
        ArrayList<Integer> secondColourData = null;
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        int currentColourPosition = 0;
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder();
        int count = 0;

        // Loop through binary data to be inserted
        while(firstPosition[0] != endPositionX || firstPosition[1] != endPositionY || currentColourPosition != endColourChannel) {

            // Get the colour data required for embedding
            firstColourData = order.get(count);
            count += 1;
            secondColourData = order.get(count);
            count += 1;

            // Update positional information
            firstPosition = new int[] {firstColourData.get(0), firstColourData.get(1)};
            secondPosition = new int[] {secondColourData.get(0), secondColourData.get(1)};
            currentColourPosition = firstColourData.get(2);

            // Get the next two colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);
            secondColour = getColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition);

            // Append the retrieved binary data to the final string of data
            binary.append(getLSB(firstColour));
            binary.append(getLSB((firstColour / 2) + secondColour));
        }

        return binary;
    }

    /**
     * Decodes the data hidden in the image by PVD
     *
     * @return          The binary data to be hidden within the image
     */
    public StringBuilder decodeSecretDataPVD(){

        // Define some variables for manipulating pixel data
        ArrayList<Integer> firstColourData = null;   // Stores data about the next two colours to manipulate
        ArrayList<Integer> secondColourData = null;
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        int currentColourPosition = 0;
        String dataToEncode;
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder();
        int count = 0;

        // Loop through binary data to be inserted
        while(firstPosition[0] != endPositionX || firstPosition[1] != endPositionY || currentColourPosition != endColourChannel) {

            // Get the colour data required for embedding
            firstColourData = order.get(count);
            count += 1;
            secondColourData = order.get(count);
            count += 1;

            // Update positional information
            firstPosition = new int[] {firstColourData.get(0), firstColourData.get(1)};
            secondPosition = new int[] {secondColourData.get(0), secondColourData.get(1)};
            currentColourPosition = firstColourData.get(2);

            // Get the next two colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);
            secondColour = getColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition);

            int d = secondColour - firstColour;

            int[] decodingData = quantisationRangeTable(Math.abs(d));

            // Calculate the quantisation range width, then the number of bits to encode
            int width = decodingData[1] - decodingData[0] + 1;
            int t = (int)Math.floor(Math.log(width)/Math.log(2.0));

            // DETERMINE WHETHER DATA EMBEDDING HAS OCCURRED
            int[] newColours = updateColoursPVD(firstColour, secondColour, d, decodingData[1]);

            if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255) {
                int a = 2;
            }else{
                int b = Math.abs(d) - decodingData[0];
                binary.append(conformBinaryLength(b, t));
            }
        }

        return binary;
    }



    public ArrayList<ArrayList<Integer>> generateEmbeddingOrder(){

        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        ArrayList<int[]> pixelOrder = generatePixelOrder();

        for(int[] pixel : pixelOrder){
            definePixelInfo(pixel, order);
        }

        return order;
    }

    public void definePixelInfo(int[] pixel, ArrayList<ArrayList<Integer>> order){

        int nextX = 0; int nextY = 0;
        if(algorithm == 4){
            nextX = (pixel[0] + 1) % stegoImage.getWidth();
            nextY = (nextX == 0 ? pixel[1] + 1 : pixel[1]);
        }

        int colourToConsider = 0;
        for(int i = 0; i < lsbsToConsider.size(); i++){
            // Create a new ArrayList
            ArrayList<Integer> current = new ArrayList<>();

            // Get the pixel to store
            int lsbToConsider = lsbsToConsider.get(i);

            // Determine if a new colour component is being accessed
            if(lsbToConsider == 0 && i != 0) {
                colourToConsider += 1;
            }

            // Add data to ArrayList to return
            current.add(pixel[0]);
            current.add(pixel[1]);
            current.add(colourToConsider);
            current.add(lsbToConsider);
            order.add(current);

            if(algorithm == 4) {
                current = new ArrayList<>();
                current.add(nextX);
                current.add(nextY);
                current.add(colourToConsider);
                current.add(lsbToConsider);
                order.add(current);
            }
        }
    }

    public ArrayList<int[]> generatePixelOrder(){
        ArrayList<int[]> naiveOrder = new ArrayList<>();

        // Edge-based order
        if(algorithm == 5){
            cannyEdgeDetection detection = new cannyEdgeDetection();
            BufferedImage edgeImage = detection.detectEdges(stegoImage);
            naiveOrder = detection.getEdgePixels(edgeImage);
            if(random){
                // https://stackoverflow.com/questions/6284589/setting-a-seed-to-shuffle-arraylist-in-java-deterministically
                // https://stackoverflow.com/questions/27346809/getting-a-range-off-user-input-for-random-generation
                Collections.shuffle(naiveOrder, new Random(seed.hashCode()));
            }
            return naiveOrder;
        }

        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();
        int gap = 1;
        if(algorithm == 4){
            gap = 2;
        }

        // Change final value depending on the gap (prevents errors later)
        int finalValue = width * height;
        if(gap > 1 && (width * height) % gap != 0){
            finalValue = (width * height) - (gap - 1);
        }

        for(int i = 18; i < finalValue; i += gap){
            int x = i % width;
            int y = i / width;
            naiveOrder.add(new int[] {x, y});
        }

        // Random order
        if(random) {
            Collections.shuffle(naiveOrder, new Random(seed.hashCode()));
        }
        return naiveOrder;
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
     * Inserts data into two colours by modifying their difference
     *
     * @param firstColour       First colour to be manipulated
     * @param secondColour      Second colour to be manipulated
     * @param d                 The difference between the colours
     * @param d1                The new difference calculated from the data to be inserted
     * @return                  Updated colours
     */
    public int[] updateColoursPVD(int firstColour, int secondColour, int d, int d1){
        double m = d1 - d;

        // Obtain new colour values for firstColour and secondColour by averaging new difference to them
        if (Math.abs(d) % 2 == 1) {
            firstColour -= (int) Math.ceil(m/2);
            secondColour += (int) Math.floor(m/2);
        } else {
            firstColour -= (int) Math.floor(m/2);
            secondColour += (int) Math.ceil(m/2);
        }

        return new int[] {firstColour, secondColour};
    }

    /**
     * Returns the range the difference of two values sits in (for PVD)
     *
     * @param difference    The difference between two consecutive pixel values
     * @return              The data required for encoding
     */
    public int[] quantisationRangeTable(int difference){
        if(difference <= 7){
            return new int[] {0, 7};
        }
        if(difference <= 15){
            return new int[] {8, 15};
        }
        if(difference <= 31){
            return new int[] {16, 31};
        }
        if(difference <= 63){
            return new int[] {32, 63};
        }
        if(difference <= 127){
            return new int[] {64, 127};
        }
        if(difference <= 255){
            return new int[] {128, 255};
        }
        return null;
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
