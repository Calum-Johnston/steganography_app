package com.java.calumjohnston.algorithms.pvd;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * pvdDecode Class: This class implements the extraction of data from an image
 * that has been embedded using the PVD technique
 */
public class pvdDecode {

    BufferedImage stegoImage;
    boolean random;
    int[] coloursToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;

    /**
     * Constructor
     */
    public pvdDecode(){

    }

    /**
     * Acts as central controller to decoding functions
     *
     * @return              The data hidden within the image
     */
    public void decode(BufferedImage stegoImage){
        //Setup data to be used for decoding
        setupData(stegoImage);

        decodeSecretData();
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
        boolean red = binaryToInt(parameters.substring(0,1)) == 1;
        boolean green = binaryToInt(parameters.substring(1,2)) == 1;
        boolean blue = binaryToInt(parameters.substring(2,3)) == 1;
        this.coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(3, 4)) == 1;
        if (random) {
            this.generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), "");
        }

        // Get end position for data encoding
        endPositionX = binaryToInt(parameters.substring(6,21));
        endPositionY = binaryToInt(parameters.substring(21, 36));

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
        for (int i = 0; i < 36; i += 1) {

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



    // ======= DECODING FUNCTIONS =======
    /**
     * Decodes the data to be hidden into the image
     *
     * @return          The data hidden within the image
     */
    public String decodeSecretData(){

        // Define some initial variables required
        ArrayList<Integer> colourData = null;   // Stores data about the next two colours to manipulate
        int[] decodingData;         // Stores important encoding information (i.e. number of bits to encode)
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int colourDifference;       // Stores the colour difference
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)
        String binaryData = "";     // Stores the binary data retrieved from the image at a point

        // Generates pixel order to visit the image in
        ArrayList<ArrayList<Integer>> orderToConsider = getPixelOrder();

        // Loop through binary data to be inserted
        for (int i = 0; i < orderToConsider.size(); i += binaryData.length() ) {

            // Get the colour data required for embedding
            colourData = orderToConsider.get(i);

            // Get the next two colour channel data
            firstColour = getColourAtPosition(colourData.get(0), colourData.get(1), colourData.get(4));
            secondColour = getColourAtPosition(colourData.get(2), colourData.get(3), colourData.get(4));

            // Calculate the colour difference
            colourDifference = Math.abs(firstColour - secondColour);

            // Get the number of bits we can encode at this time
            decodingData = quantisationRangeTable(colourDifference);

            // Get the data from the into the image
            binaryData = conformBinaryLength(colourDifference - decodingData[0], decodingData[2]);

            // Append the binary data acquired to the final string of data
            binary.append(binaryData);

        }

        return binary.toString();
    }

    /**
     * Determines the order of pixels we embed data into
     *
     * @return              The order we should consider pixel whilst encoding
     */
    public ArrayList<ArrayList<Integer>> getPixelOrder(){

        // Define some initial variables required
        int currentColourPosition = 0;
        int[] firstPosition = generateNextPosition(new int[] {16, 0});
        int[] secondPosition = generateNextPosition(firstPosition);
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();

        // Gets the pixel order we will consider
        while(firstPosition[0] != endPositionX || firstPosition[1] != endPositionY){
            if((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0){
                currentColourPosition = 0;
                firstPosition = generateNextPosition(secondPosition);
                secondPosition = generateNextPosition(firstPosition);
            }
            ArrayList<Integer> current = new ArrayList<>();
            current.add(firstPosition[0]);
            current.add(firstPosition[1]);
            current.add(secondPosition[0]);
            current.add(secondPosition[1]);
            current.add(currentColourPosition);
            order.add(current);
            currentColourPosition += 1;
        }

        return order;
    }

    /**
     * Function acts as the quantisation range table
     *
     * @param difference    The difference between two consecutive pixel values
     * @return              The data required for encoding
     */
    public int[] quantisationRangeTable(int difference){
        if(difference <= 7){
            return new int[] {0, 7, 1};
        }
        if(difference <= 15){
            return new int[] {8, 15, 2};
        }
        if(difference <= 31){
            return new int[] {16, 31, 3};
        }
        if(difference <= 63){
            return new int[] {32, 63, 4};
        }
        if(difference <= 12715){
            return new int[] {64, 127, 5};
        }
        if(difference <= 255){
            return new int[] {128, 255, 6};
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
}
