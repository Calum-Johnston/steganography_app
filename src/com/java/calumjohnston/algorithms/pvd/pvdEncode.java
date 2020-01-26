package com.java.calumjohnston.algorithms.pvd;
import com.java.calumjohnston.exceptions.DataOverflowException;
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
    int endColourChannel;


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

        // Encode data to the image
        encodeSecretData(binary);

        // Encode parameters to the image
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

        // Define some variables for manipulating pixel data
        ArrayList<int[]> colourData = null;   // Stores data about the next two colours to manipulate
        int[] encodingData;         // Stores important encoding information (i.e. number of bits to encode)
        int firstColour;            // Stores the first colour to be manipulated
        int newFirstColour = 0;
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        int newSecondColour = 0;
        int colourDifference;       // Stores the colour difference
        int newColourDifference;    // Stores the colour difference once range has been decided
        String dataToEncode;        // Stores the data we wish to encode at one instance (in bits)

        // Define some variables for determining which pixels to manipulate
        int currentColourPosition = -1;
        int[] firstPosition = generateNextPosition(new int[] {16, 0});
        int[] secondPosition = generateNextPosition(firstPosition);

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += dataToEncode.length()) {

            // Get the colour data required for embedding
            colourData = getNextData(firstPosition, secondPosition, currentColourPosition);

            // Update positional information
            firstPosition = colourData.get(0);
            secondPosition = colourData.get(1);
            currentColourPosition = colourData.get(2)[0];

            // Check we are within bounds
            if(secondPosition[0] >= coverImage.getWidth() || secondPosition[1] >= coverImage.getHeight()){
                throw new DataOverflowException("Input text too large");
            }

            // Get the next two colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);
            secondColour = getColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition);

            // Calculate the colour difference
            colourDifference = Math.abs(firstColour - secondColour);

            // Get the number of bits we can encode at this time
            encodingData = quantisationRangeTable(colourDifference);

            // Calculate the quantisation range width, then the number of bits to encode
            int width = encodingData[1] - encodingData[0] + 1;
            int t = (int)Math.floor(Math.log(width)/Math.log(2.0));

            // Get the data to encode into the image (ensuring we don't go out of range)
            if(i + t > binary.length()){
                dataToEncode = binary.substring(i);
            }else{
                dataToEncode = binary.substring(i, i + t);
            }

            // Check if in range
            int decimalData = (int) Math.pow(2, t) - 1;

            // Calculate the new colour difference
            newColourDifference = encodingData[0] + decimalData;

            // Obtain new colour values for firstColour and secondColour by averaging new difference to them
            if(firstColour >= secondColour && newColourDifference > colourDifference){
                newFirstColour = firstColour + (int) Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                newSecondColour = secondColour - (int) Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour < secondColour && newColourDifference > colourDifference){
                newFirstColour = firstColour - (int) Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                newSecondColour = secondColour + (int) Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour >= secondColour && newColourDifference <= colourDifference){
                newFirstColour = firstColour - (int) Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                newSecondColour = secondColour + (int) Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2);
            }else if(firstColour < secondColour && newColourDifference <= colourDifference){
                newFirstColour = firstColour + (int) Math.ceil((double) Math.abs(newColourDifference - colourDifference) / 2);
                newSecondColour = secondColour - (int) Math.floor((double) Math.abs(newColourDifference - colourDifference) / 2); }

            if(newFirstColour < 0 || newFirstColour > 255 || newSecondColour < 0 || newSecondColour > 255){
                dataToEncode = "";
            }else{
                decimalData = Integer.parseInt(dataToEncode, 2);

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
                writeColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition, firstColour);
                writeColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition, secondColour);
            }
        }

        // Write end position data (for decoding purposes)
        endPositionX = firstPosition[0];
        endPositionY = firstPosition[1];
        endColourChannel = currentColourPosition;

    }

    /**
     * Determines the next two pixels we should encode into
     *
     * @param firstPosition         The positional data of the first pixel being considered
     * @param secondPosition        The positional data of the second pixel being considered
     * @param currentColourPosition The current position in coloursToConsider we are using
     * @return                      The order we should consider pixel whilst encoding
     */
    public ArrayList<int[]> getNextData(int[] firstPosition, int[] secondPosition, int currentColourPosition){

        // Define ArrayList to store data in
        ArrayList<int[]> current = new ArrayList<>();

        // Update current position to check for in coloursToConsider
        currentColourPosition += 1;

        // Update positions (if ran out of colour channels to manipulate with current positions)
        if((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0){
            currentColourPosition = 0;
            firstPosition = generateNextPosition(secondPosition);
            secondPosition = generateNextPosition(firstPosition);
        }

        // Add data to ArrayList to return
        current.add(firstPosition);
        current.add(secondPosition);
        current.add(new int[] {currentColourPosition});

        return current;
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
    /**
     * Converts parameter data to binary and writes to image
     *
     * @param red       Determines whether the red colour channel has been used
     * @param green     Determines whether the green colour channel has been used
     * @param blue      Determines whether the blue colour channel has been used
     */
    public void encodeParameterData(boolean red, boolean green, boolean blue){
        StringBuilder parameters = new StringBuilder();
        parameters.append(conformBinaryLength(3, 3));
        parameters.append(conformBinaryLength(red ? 1 : 0, 1));
        parameters.append(conformBinaryLength(green ? 1 : 0, 1));
        parameters.append(conformBinaryLength(blue ? 1 : 0, 1));
        parameters.append(conformBinaryLength(random ? 1 : 0, 1));
        parameters.append(conformBinaryLength(endPositionX, 15));
        parameters.append(conformBinaryLength(endPositionY, 15));
        parameters.append(conformBinaryLength(endColourChannel, 2));
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
