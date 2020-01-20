package com.java.calumjohnston.algorithms.pvd;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * pvdEncode Class: This class implements the embedding of data into an image
 * using the PVD technique
 */
public class pvdEncode {

    BufferedImage coverImage;
    boolean random;
    int[] coloursToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;


    /**
     * Constructor
     */
    public pvdEncode(){

    }

    /**
     * Acts as central controller to encoding functions
     *
     * @param coverImage    The image we will encode data into
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     * @param text          The data to be hidden within the image
     * @return              The image with the hidden data embedded into it
     */
    public BufferedImage encode(BufferedImage coverImage, boolean red, boolean green, boolean blue,
                                boolean random, String seed, String text){
        // Setup data to be used for encoding
        setupData(coverImage, red, green, blue, random, seed);

        // Get the binary data to encode
        StringBuilder binary = getBinaryText(text);

        // Determine data will fit

        // Encode data
        encodeSecretData(binary);

        // Encode parameters
        encodeParameterData(red, green, blue);

        // Return the manipulated image
        return coverImage;
    }



    // ======= SETUP FUNCTIONS =======
    /**
     * Sets up the initial data required for encoding
     * @param coverImage    The image we will encode data into
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     */
    public void setupData(BufferedImage coverImage, boolean red, boolean green, boolean blue,
                          boolean random, String seed){

        // Define the image we are embedding data into
        this.coverImage = coverImage;

        // Get the colours to consider (some combination of red, green and blue)
        coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether random embedding is being used
        this.random = random;
        if (random) {
            generator = new pseudorandom(coverImage.getHeight(), coverImage.getWidth(), seed);
        }
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
     * Encodes the data to be hidden into the image
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretData(StringBuilder binary){

        // Define some initial variables required
        ArrayList<Integer> colourData = null;   // Stores data about the next two colours to manipulate
        int[] encodingData;         // Stores important encoding information (i.e. number of bits to encode)
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int colourDifference;       // Stores the colour difference
        int newColourDifference;    // Stores the colour difference once range has been decided
        String dataToEncode;        // Stores the data we wish to encode (in bits)

        // Generates pixel order to visit the image in
        ArrayList<ArrayList<Integer>> orderToConsider = getPixelOrder(binary);

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += dataToEncode.length()) {

            // Get the colour data required for embedding
            colourData = orderToConsider.get(i);

            // Get the next two colour channel data
            firstColour = getColourAtPosition(colourData.get(0), colourData.get(1), colourData.get(4));
            secondColour = getColourAtPosition(colourData.get(2), colourData.get(3), colourData.get(4));

            // Calculate the colour difference
            colourDifference = Math.abs(firstColour - secondColour);

            // Get the number of bits we can encode at this time
            encodingData = quantisationRangeTable(colourDifference);

            // Get the data to encode into the image
            dataToEncode = binary.substring(i, i + encodingData[2]);
            int decimalData = Integer.parseInt(dataToEncode, 2);

            // Calculate the new colour difference
            newColourDifference = encodingData[0] + decimalData;

            // Obtain new colour values for firstColour and secondColour by averaging new difference to them
            if(firstColour >= secondColour && newColourDifference > colourDifference){
                firstColour += Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                secondColour -= Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour < secondColour && newColourDifference > colourDifference){
                firstColour -= Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                secondColour += Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour >= secondColour && newColourDifference <= colourDifference){
                firstColour -= Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                secondColour += Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour < secondColour && newColourDifference <= colourDifference){
                firstColour += Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                secondColour -= Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }

            // Write colour data back to the image
            writeColourAtPosition(colourData.get(0), colourData.get(1), colourData.get(4), firstColour);
            writeColourAtPosition(colourData.get(2), colourData.get(3), colourData.get(4), secondColour);

        }

        // Write end position data (for decoding purposes)
        endPositionX = colourData.get(0);
        endPositionY = colourData.get(1);

    }

    /**
     * Determines the order of pixels we embed data into
     *
     * @param binary        The binary data to be hidden within the image
     * @return              The order we should consider pixel whilst encoding
     */
    public ArrayList<ArrayList<Integer>> getPixelOrder(StringBuilder binary){

        // Define some initial variables required
        int currentColourPosition = 0;
        int[] firstPosition = generateNextPosition(new int[] {16, 0});
        int[] secondPosition = generateNextPosition(firstPosition);
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();

        // Gets the pixel order we will consider
        for(int i = 0; i < binary.length(); i += 1){
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
     * Generates the next pixel position to consider when encoding data
     *
     * @param currentPosition   The position which data has just been encoded
     * @return                  The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition) {
        int imageWidth = coverImage.getWidth();
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



    // ======= PARAMETER ENCODING =======
    public void encodeParameterData(boolean red, boolean green, boolean blue){
        StringBuilder parameters = new StringBuilder();
        parameters.append(conformBinaryLength(red ? 1 : 0, 1));
        parameters.append(conformBinaryLength(green ? 1 : 0, 1));
        parameters.append(conformBinaryLength(blue ? 1 : 0, 1));
        parameters.append(conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(conformBinaryLength(3, 2));
        parameters.append(conformBinaryLength(endPositionX, 15));
        parameters.append(conformBinaryLength(endPositionY, 15));
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
        int colour; int data;

        // Loop through binary data to be inserted
        for (int i = 0; i < parameters.length(); i += 1) {

            // Get the data to encode into the image
            data = parameters.charAt(i);

            // Get current colour to manipulate
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                currentPosition = generateNextPosition(currentPosition);
            }
            colour = getColourAtPosition(currentPosition[0], currentPosition[1], currentColourPosition);

            // Update LSB of colour
            colour = updateLSB(colour, data);

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

    /**
     * Updates the least significant bit of a colour to that of some data
     *
     * @param colour        The colour to update the LSB of
     * @param data          The new LSB of the colour
     * @return              The updated colour
     */
    public int updateLSB(int colour, int data){
        if(colour % 2 == 0 && data == '1') {
            colour += 1;
        }else if(colour % 2 != 0 && data == '0'){
            colour -= 1;
        }
        return colour;
    }
}
