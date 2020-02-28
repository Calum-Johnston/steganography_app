package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.exceptions.DataOverflowException;
import com.java.calumjohnston.utilities.cannyEdgeDetection;
import com.java.calumjohnston.utilities.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Encode Class: This class implements the embedding of data into an image
 * using one of several LSB techniques
 */
public class encodeData {

    BufferedImage coverImage;
    ArrayList<Integer> lsbsToConsider;
    int[] coloursToConsider;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int endLSBPosition;
    int algorithm;
    boolean random;
    String seed;

    /**
     * Constructor
     */
    public encodeData(){

    }

    /**
     * Acts as central controller to encoding functions
     *
     * @param coverImage    The image we will encode data into
     * @param algorithm     The algorithm being used for encoding
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     * @param text          The data to be hidden within the image
     * @return              The image with the hidden data embedded into it
     */
    public BufferedImage encode(BufferedImage coverImage, int algorithm, boolean red, boolean green, boolean blue,
                                int redBits, int greenBits, int blueBits,
                                boolean random, String seed, String text){
        // Setup data to be used for encoding
        setupData(coverImage, algorithm, red, green, blue, redBits, greenBits, blueBits, random, seed);

        // Get the binary data to encode
        StringBuilder binary = getBinaryText(text);

        // Encode data to the image
        encodeSecretData(binary);

        // Encode parameters to the image
        encodeParameterData(red, green, blue, redBits, greenBits, blueBits);

        // Return the manipulated image
        return coverImage;
    }



    // ======= SETUP FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param coverImage    The image we will encode data into
     * @param algorithm     The algorithm being used for encoding
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     */
    public void setupData(BufferedImage coverImage, int algorithm, boolean red, boolean green, boolean blue,
                          int redBits, int greenBits, int blueBits,
                          boolean random, String seed){

        // Define the image we are embedding data into
        this.coverImage = coverImage;

        // Define the algorithm being used
        this.algorithm = algorithm;

        // Get the colours to consider (some combination of red, green and blue)
        coloursToConsider = getColoursToConsider(red, green, blue);

        // Get the LSBs to consider
        lsbsToConsider = getLSBsToConsider(redBits, greenBits, blueBits, red, green, blue);

        // Set the seed
        this.seed = seed;

        // Determine whether random embedding is being used
        this.random = random;
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

    /**
     * Converts character data (ASCII) into its binary equivalent
     *
     * @param text      The character data to be converted
     * @return          Binary equivalent of text
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



    // ======= ENCODING FUNCTIONS =======
    /**
     * Determines which encoding scheme to use based on the algorithm selected
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretData(StringBuilder binary){
        // Determine which encoding scheme to use
        if(algorithm <= 2 || algorithm == 5){
            encodeSecretDataLSB(binary);
        }else if(algorithm == 3){
            encodeSecretDataLSBMR(binary);
        }else{
            encodeSecretDataPVD(binary);
        }
    }

    /**
     * Encodes the data to be hidden into the image by either
     * LSB or LSBM
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretDataLSB(StringBuilder binary){

        // Define some variables for manipulating pixel data
        ArrayList<Integer> colourData = null;     // Stores data about the next two colours to manipulate
        int colour;                             // Stores the colour to be manipulated
        int newColour = 0;                          // Stores the manipulated colour

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder(binary.length());
        int count = 0;
        int[] position = new int[2];
        int currentColourPosition = 0;
        int currentLSBPosition = 0;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 1) {

            // Get the colour data required for embedding
            colourData = order.get(count);
            count += 1;

            // Update positional information
            position = new int[] {colourData.get(0), colourData.get(1)};
            currentColourPosition = colourData.get(2);
            currentLSBPosition = colourData.get(3);

            // Get bit required from binary input
            char data_1 = binary.charAt(i);

            // Get the next colour channel data
            colour = getColourAtPosition(position[0], position[1], currentColourPosition);

            // Update current colour data based on binary data to insert
            if(algorithm == 0 || algorithm == 1){
                newColour = updateColourLSB(colour, data_1, currentLSBPosition);
            }else if(algorithm == 2){
                newColour = updateColourLSBM(colour, data_1, currentLSBPosition);
            }

            // OPAP
            if(algorithm == 1){
                newColour = optimalPixelAdjustment(colour, newColour, currentLSBPosition + 1);
            }

            // Write colour data back to the image
            writeColourAtPosition(position[0], position[1], currentColourPosition, newColour);

        }

        // Write end position data (for decoding purposes)
        if(!random) {
            endPositionX = position[0];
            endPositionY = position[1];
        }
        endColourChannel = currentColourPosition;
        endLSBPosition = currentLSBPosition;

    }

    /**
     * Encodes the data to be hidden into the image by lSBMR
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretDataLSBMR(StringBuilder binary){

        // Define some variables for manipulating pixel data
        ArrayList<Integer> firstColourData = null;   // Stores data about the next two colours to manipulate
        ArrayList<Integer> secondColourData = null;
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        int currentColourPosition = 0;

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder(binary.length());
        int count = 0;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 2) {

            // Get the colour data required for embedding
            firstColourData = order.get(count);
            count += 1;
            secondColourData = order.get(count);
            count += 1;

            // Update positional information
            firstPosition = new int[] {firstColourData.get(0), firstColourData.get(1)};
            secondPosition = new int[] {secondColourData.get(0), secondColourData.get(1)};
            currentColourPosition = firstColourData.get(2);

            // Get bits required from binary input
            char data_1 = binary.charAt(i);
            char data_2 = binary.charAt(i + 1);

            // Get the next two colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);
            secondColour = getColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition);

            // Get the binary relationships between the firstColour and secondColour
            int pixel_Relationship = (int) Math.floor(firstColour / 2) + secondColour;
            String LSB_Relationship = getLSB(pixel_Relationship);
            int pixel_Relationship_2;
            if(firstColour == 0){
                // Fix for 0 - 1 / 2 giving 0 instead of -0.5
                pixel_Relationship_2 = -1 + secondColour;
            }else{
                pixel_Relationship_2 = (int) Math.floor((firstColour - 1) / 2) + secondColour;
            }
            String LSB_Relationship_2 = getLSB(pixel_Relationship_2);

            // Get LSBs of pixel colour
            String firstColourLSB = getLSB(firstColour);

            // Determines what to embed based on LSBss and relationship between them
            // Could add an update LSB function here too!! (for pixelData!!)
            if (Character.toString(data_1).equals(firstColourLSB)) {
                if(!(Character.toString(data_2).equals(LSB_Relationship))) {
                    if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                        secondColour -= 1;
                        if(secondColour == -1){
                            secondColour = 255;
                        }
                    } else {
                        secondColour += 1;
                        if(secondColour == 256){
                            secondColour = 0;
                        }
                    }
                    // Write colour data back to the image
                    writeColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition, secondColour);
                }
            } else if (!(Character.toString(data_1).equals(firstColourLSB))) {
                if (Character.toString(data_2).equals(LSB_Relationship_2)) {
                    firstColour -= 1;
                    if(firstColour == -1){
                        firstColour = 255;
                    }
                } else {
                    firstColour += 1;
                    if(firstColour == 256){
                        firstColour = 0;
                    }
                }
                // Write colour data back to the image
                writeColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition, firstColour);
            }

        }

        // Write end position data (for decoding purposes)
        if(!random) {
            endPositionX = secondPosition[0];
            endPositionY = secondPosition[1];
        }
        endColourChannel = currentColourPosition;

    }

    /**
     * Encodes the data to be hidden into the image by PVD
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretDataPVD(StringBuilder binary){

        // Define some variables for manipulating pixel data
        ArrayList<Integer> firstColourData = null;   // Stores data about the next two colours to manipulate
        ArrayList<Integer> secondColourData = null;
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];
        int currentColourPosition = 0;
        String dataToEncode;

        // Generate Order to embed data in
        ArrayList<ArrayList<Integer>> order = generateEmbeddingOrder(binary.length());
        int count = 0;

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += dataToEncode.length()) {

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

            // Calculate pixel difference
            int d = secondColour - firstColour;

            // Get the range data for this colour interval
            int[] encodingData = quantisationRangeTable(Math.abs(d));

            // Calculate the quantisation range width, then the number of bits to encode
            int width = encodingData[1] - encodingData[0] + 1;
            int n = (int)Math.floor(Math.log(width)/Math.log(2.0));

            // Get the data to encode into the image (ensuring we don't go out of range)
            if(i + n > binary.length()){
                dataToEncode = binary.substring(i);
                while(dataToEncode.length() < n){
                    dataToEncode = dataToEncode + "0";
                }
            }else{
                dataToEncode = binary.substring(i, i + n);
            }
            int decVal = Integer.parseInt(dataToEncode, 2);

            // CHECK RANGE
            int[] newColours = updateColoursPVD(firstColour, secondColour, d, encodingData[1]);

            if(newColours[0] < 0 || newColours[0] > 255 || newColours[1] < 0 || newColours[1] > 255){
                dataToEncode = "";
            }else {

                // Calculate embedding values
                int d1;
                if(d >= 0){ d1 = encodingData[0] + decVal; }
                else{ d1 = -(encodingData[0] + decVal); }

                // Encode the data
                newColours = updateColoursPVD(firstColour, secondColour, d, d1);

                // Write colour data back to the image
                writeColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition, newColours[0]);
                writeColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition, newColours[1]);
            }

        }

        // Write end position data (for decoding purposes)
        if(!random) {
            endPositionX = secondPosition[0];
            endPositionY = secondPosition[1];
        }
        endColourChannel = currentColourPosition;

    }



    public ArrayList<ArrayList<Integer>> generateEmbeddingOrder(int dataSize){

        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        return generatePixelOrder(dataSize);

    }

    public ArrayList<ArrayList<Integer>> generatePixelOrder(int dataSize){
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        int[] naiveOrder = new int[2];

        // Edge-based order
        /**if(algorithm == 5){
            cannyEdgeDetection detection = new cannyEdgeDetection();
            BufferedImage edgeImage = detection.detectEdges(coverImage);
            naiveOrder = detection.getEdgePixels(edgeImage);
            if(random){
                // https://stackoverflow.com/questions/6284589/setting-a-seed-to-shuffle-arraylist-in-java-deterministically
                // https://stackoverflow.com/questions/27346809/getting-a-range-off-user-input-for-random-generation
                Collections.shuffle(naiveOrder, new Random(seed.hashCode()));
            }
            return naiveOrder;
        }*/

        int width = coverImage.getWidth();
        int height = coverImage.getHeight();
        int gap = 1;
        if(algorithm == 4){
            gap = 2;
        }

        // Change final value depending on the gap (prevents errors later)
        int finalValue = width * height;
        if(gap > 1 && (width * height) % gap != 0){
            finalValue = (width * height) - (gap - 1);
        }

        int multipler = 1;
        if(algorithm == 3){
            gap = 2;
            multipler = 2;
        }

        int x = 0; int y = 0;
        for(int i = 18; i < finalValue && (dataSize - 1) >= 0; i += gap){
            dataSize -= lsbsToConsider.size() * multipler;
            x = i % width;
            y = i / width;
            naiveOrder = new int[] {x, y};
            definePixelInfo(naiveOrder, order);
        }

        if(random){
            if(algorithm == 3 || algorithm == 4){
                endPositionX = (x + 1) % coverImage.getWidth();
                endPositionY = (endPositionX == 0 ? y + 1 : y);
            }else {
                endPositionX = x;
                endPositionY = y;
            }
        }

        // Random order
        if(random) {
            Collections.shuffle(order, new Random(seed.hashCode()));
        }
        return order;
    }

    public void definePixelInfo(int[] pixel, ArrayList<ArrayList<Integer>> order){

        int nextX = 0; int nextY = 0;

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
            current.add(coloursToConsider[colourToConsider]);
            current.add(lsbToConsider);
            order.add(current);

            if(algorithm == 4 || algorithm == 3) {
                current = new ArrayList<>();
                nextX = (pixel[0] + 1) % coverImage.getWidth();
                nextY = (nextX == 0 ? pixel[1] + 1 : pixel[1]);
                current.add(nextX);
                current.add(nextY);
                current.add(coloursToConsider[colourToConsider]);
                current.add(lsbToConsider);
                order.add(current);
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
        int pixel = coverImage.getRGB(x, y);
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
     * Writes colour channel data to an image at a specific location
     *
     * @param x                 x coordinate of pixel
     * @param y                 y coordinate of pixel
     * @param colourChannel     The colour channel we are considering (R, G or B)
     * @param data              The data to be written
     */
    public void writeColourAtPosition(int x, int y, int colourChannel, int data){
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        if(colourChannel == 0){
            red = data;
        }
        if(colourChannel == 1){
            green = data;
        }
        if(colourChannel == 2){
            blue = data;
        }
        Color color = new Color(red, green, blue);
        coverImage.setRGB(x, y, color.getRGB());
    }

    /**
     * Inserts data into a defined significant bit of the colour (LSB replace style)
     *
     * @param colour        Original colour to be manipulated
     * @param data          Binary data to be inserted into the colour
     * @param position      The position in the colour's 8 bit representation we are considering
     * @return              Updated colour
     */
    public int updateColourLSB(int colour, char data, int position) {
        StringBuilder binaryColour = new StringBuilder();
        binaryColour.append(conformBinaryLength(colour, 8));
        int lsb_Position = 7 - position;
        if(binaryColour.charAt(lsb_Position) != data){
            binaryColour.setCharAt(lsb_Position, data);
        }
        return Integer.parseInt(binaryColour.toString(), 2);
    }

    /**
     * Inserts data into a defined significant bit of the colour (LSBM +- style)
     *
     * @param colour        Original colour to be manipulated
     * @param data          Binary data to be inserted into the colour
     * @param position      The position in the colour's 8 bit representation we are considering
     * @return              Updated colour
     */
    public int updateColourLSBM(int colour, char data, int position) {
        StringBuilder binaryFirstHalf = new StringBuilder();
        StringBuilder binarySecondHalf = new StringBuilder();
        int firstHalfNum;
        String binaryColour = conformBinaryLength(colour, 8);
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
     * Gets the least significant bit of a colour
     *
     * @param colour        The colour to get the LSB of
     * @return              The LSB of colour
     */
    public String getLSB(int colour){
        if(colour % 2 == 0) {
            return "0";
        }else{
            return "1";
        }
    }

    /**
     * Adjusts pixel value to optimal values to enhance image quality
     *
     * @return      The optimised value of the pixel
     */
    public int optimalPixelAdjustment(int coverValue, int stegoValue, int k){

        int embeddingError = stegoValue - coverValue;

        if(embeddingError > Math.pow(2, k - 1) && embeddingError < Math.pow(2, k)){
            if(stegoValue >= Math.pow(2, k)) {
                return stegoValue - (int) Math.pow(2, k);
            }
            return stegoValue;
        }else if(embeddingError >= -Math.pow(2, k-1) && embeddingError <= Math.pow(2, k-1)){
            return stegoValue;
        }else if(embeddingError > -Math.pow(2, k) && embeddingError < -Math.pow(2, k-1) ){
            if(stegoValue < 256 - Math.pow(2, k)){
                return stegoValue + (int) Math.pow(2, k);
            }
            return stegoValue;
        }
        return 0;
    }



    // ======= PARAMETER ENCODING =======
    /**
     * Converts parameter data to binary and writes to image
     *
     * @param red       Determines whether the red colour channel has been used
     * @param green     Determines whether the green colour channel has been used
     * @param blue      Determines whether the blue colour channel has been used
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     */
    public void encodeParameterData(boolean red, boolean green, boolean blue,
                                    int redBits, int greenBits, int blueBits){
        StringBuilder parameters = new StringBuilder();
        parameters.append(conformBinaryLength(algorithm, 3));
        parameters.append(conformBinaryLength(red ? 1 : 0, 1));
        parameters.append(conformBinaryLength(green ? 1 : 0, 1));
        parameters.append(conformBinaryLength(blue ? 1 : 0, 1));
        parameters.append(conformBinaryLength(redBits - 1, 3));
        parameters.append(conformBinaryLength(greenBits - 1, 3));
        parameters.append(conformBinaryLength(blueBits - 1, 3));
        parameters.append(conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(conformBinaryLength(endPositionX, 15));
        parameters.append(conformBinaryLength(endPositionY, 15));
        parameters.append(conformBinaryLength(endColourChannel, 2));
        parameters.append(conformBinaryLength(endLSBPosition, 5));
        encodeParameters(parameters);
    }

    /**
     * Encodes the parameter data into the image using LSB
     *
     * @param parameters    The binary parameter data to be hidden within the image
     */
    public void encodeParameters(StringBuilder parameters){
        // Initialise variables for encoding
        int[] coloursToConsider = new int[] {0, 1, 2};
        int[] currentPosition = new int[] {0, 0};
        int currentColourPosition = 0;
        int colour;

        // Loop through binary data to be inserted
        for (int i = 0; i < parameters.length(); i += 1) {

            // Get the data to encode into the image
            char data = parameters.charAt(i);

            // Get current colour to manipulate
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                currentPosition[0] = (currentPosition[0] + 1) % coverImage.getWidth();
                currentPosition[1] = (currentPosition[0] == 0 ? currentPosition[1] + 1 : currentPosition[1]);
            }
            colour = getColourAtPosition(currentPosition[0], currentPosition[1], currentColourPosition);

            // Update LSB of colour
            colour = updateColourLSB(colour, data, 0);

            // Write colour data back to the image
            writeColourAtPosition(currentPosition[0], currentPosition[1], currentColourPosition, colour);

            // Update current colour
            currentColourPosition += 1;
        }
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

}
