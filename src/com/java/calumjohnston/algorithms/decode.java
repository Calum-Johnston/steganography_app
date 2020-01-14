package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Performs LSB decoding on several algorithmic techniques
 */
public class decode {

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

    boolean random;
    int algorithm;
    int[] endPositionData;
    int[] coloursToConsider;
    ArrayList<Integer> lsbToConsider;



    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public decode() {
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
    }



    /**
     * Central HUB to control decoding of the image
     *
     * @param stegoImage        Image to be used
     * @return                  Text hidden within the image
     */
    public String decodeImage(BufferedImage stegoImage){

        // Get parameter data
        getParameterData(stegoImage);

        // Get binary version of hidden data
        StringBuilder binaryData = decodeData(stegoImage, random, coloursToConsider, endPositionData);

        // Convert binary text to ASCII equivalent
        StringBuilder text = getText(binaryData);

        return text.toString();

    }



    // DECODING FUNCTIONS
    /**
     * Gets the binary equivalent of the hidden data in the image
     *
     * @param stegoImage            The image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param parameters            List of parameters used for determining when to stop decoding
     * @return                      The binary data encoded within the image
     */
    public StringBuilder decodeData(BufferedImage stegoImage, boolean random, int[] coloursToConsider, int[] parameters){
        if(algorithm == 0 || algorithm == 1){
            return decodeLSB(stegoImage, random, coloursToConsider, parameters);
        }
        if(algorithm == 2){
            return decodeLSBMR(stegoImage, random, coloursToConsider, parameters);
        }
        return new StringBuilder();
    }

    /**
     * Performs the decoding of data from the stego image using LSB / LSBM
     *
     * @param stegoImage            The image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param parameters            List of parameters used for determining when to stop decoding
     * @return                      The binary data encoded within the image
     */
    public StringBuilder decodeLSB(BufferedImage stegoImage, boolean random, int[] coloursToConsider, int[] parameters){

        // Initialise starting variables
        int[] currentPosition = getStartPosition(stegoImage, random);
        StringBuilder data = new StringBuilder();
        int[] pixelData;

        // Represents current colour being considered (pointing to position in coloursToConsider)
        int currentColour = 0;

        // Represents current LSB being considered (pointing to position in lsbToConsider)
        int currentLSB = 0;

        // Gets the pixel order we will consider
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        while(currentPosition[0] != parameters[0] || currentPosition[1] != parameters[1] || parameters[2] != currentLSB){
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
                    currentPosition = generateNextPosition(stegoImage.getWidth(), currentPosition, random);
                }
            }
        }

        // Define lists to store data about colours
        ArrayList<Integer> colourData;

        // Define variables to store exact data of colour channel being accessed
        int colour;

        for(int i = 0; i < order.size(); i++){

            // Get the next positional information to consider
            colourData = order.get(i);

            // Get the pixel information for each colour
            pixelData = getPixelData(stegoImage, colourData.get(0), colourData.get(1));

            // Get the colours from each pixel
            colour = pixelData[coloursToConsider[colourData.get(2)]];

            // Get data from first colour
            data.append(getBinaryLSB(colour, lsbToConsider.get(colourData.get(3))));

            // Could improve efficiency by only writing / getting new pixel data when position changes
        }

        return data;
    }

    /**
     * Performs the decoding of data from the stego image using LSB / LSBM
     *
     * @param stegoImage            The image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param parameters            List of parameters used for determining when to stop decoding
     * @return                      The binary data encoded within the image
     */
    public StringBuilder decodeLSBMR(BufferedImage stegoImage, boolean random, int[] coloursToConsider, int[] parameters){

        // Initialise starting variables
        StringBuilder data = new StringBuilder();
        int[] currentPosition = getStartPosition(stegoImage, random);

        // Represents current position (+1) in coloursToConsider (i.e. the colour being considered)
        // It's +1 due to difficulty with the MOD function
        int currentColour = 0;

        // Gets the pixel order we will consider
        ArrayList<ArrayList<Integer>> order = new ArrayList<>();
        while(currentPosition[0] != parameters[0] || currentPosition[1] != parameters[1]){
            if((currentColour + 1) % (coloursToConsider.length + 1) == 0){
                currentColour = 0;
                currentPosition = generateNextPosition(stegoImage.getWidth(), currentPosition, random);
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

        for(int i = 0; i < order.size(); i += 2){

            // Check we are not out of bounds
            if(order.size() - 1 == i){
                break;
            }

            // Get the next two positional information to consider
            firstColourData = order.get(i);
            secondColourData = order.get(i + 1);

            // Get the pixel information for each colour
            firstColourPixelData = getPixelData(stegoImage, firstColourData.get(0), firstColourData.get(1));
            secondColourPixelData = getPixelData(stegoImage, secondColourData.get(0), secondColourData.get(1));

            // Get the colours from each pixel
            firstColour = firstColourPixelData[firstColourData.get(2)];
            secondColour = secondColourPixelData[secondColourData.get(2)];

            // Get data from first colour
            data.append(getBinaryLSB(firstColour, 0));

            // Get hidden data from second colour
            data.append(getBinaryLSB((firstColour / 2) + secondColour, 0));
        }

        // Return the binary data
        return data;
    }

    /**
     * Gets the position to start decoding data from
     * (HOPEFULLY USE ONE FUNCTION FOR ENCODING AND DECODING EVENTUALLY!!)
     *
     * @param image                 The image to be used
     * @param random                Determines whether random embedding was used
     * @return                      Position to start decoding (dependent on what we're decoding)
     */
    public int[] getStartPosition(BufferedImage image, boolean random){
        if(random){
            int position = generator.getNextElement();
            return new int[] {position % image.getWidth(), position / image.getWidth()};
        }else{
            return new int[] {17, 0};
        }
    }

    /**
     * Generates next position of the data within image
     *
     * @param imageWidth      The width of the image being used
     * @param currentPosition The position which data has just been encoded
     * @param random          Determines whether random embedding should be used
     * @return The new position to consider
     */
    public int[] generateNextPosition(int imageWidth, int[] currentPosition, boolean random) {
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
     * Gets the parameter data from the stego image
     *
     * @param stegoImage            Image to be used
     */
    public void getParameterData(BufferedImage stegoImage){

        // Get colours used
        int[] pixelData = getPixelData(stegoImage, 0, 0);
        boolean red = getColour(pixelData[0]);
        boolean green = getColour(pixelData[1]);
        boolean blue = getColour(pixelData[2]);
        coloursToConsider = getColoursToConsider(red, green, blue);

        // Get random
        random = getRandom(stegoImage);
        if(random){
            String seed = JOptionPane.showInputDialog("Please select a password for the data");
            if(seed == null){ seed = ""; }
            generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), seed);
        }

        // Get algorithm
        algorithm = getAlgorithm(stegoImage);

        // Get end position
        endPositionData = getEndPosition(stegoImage);

        // Get number of LSBs used per colour
        int redBits = getLSBBits(getPixelData(stegoImage, 12, 0)) + 1;
        int greenBits = getLSBBits(getPixelData(stegoImage, 13, 0)) + 1;
        int blueBits = getLSBBits(getPixelData(stegoImage, 14, 0)) + 1;
        lsbToConsider = getLSBsToConsider(redBits, greenBits, blueBits, red, green, blue);

        // Get end LSB position
        endPositionData[2] = getEndLSBPosition(stegoImage);
    }

    /**
     * Determines whether a particular colour channel was used in image encoding
     * (Stored in the red, green and blue LSB of the pixel at (0, 0))
     *
     * @param colour        The colour channel (red, green or blue)
     * @return              Whether the colour was used or not
     */
    public boolean getColour(int colour){
        String binary = Integer.toBinaryString(colour);
        String LSB = binary.substring(binary.length() - 1);
        if(LSB.equals("0")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Determines whether random embedding was used in image encoding
     * (Stored in the red LSB of the pixel at (1, 0))
     *
     * @param stegoImage    The image to be used
     * @return              Whether random was used or not (1, 0)
     */
    public boolean getRandom(BufferedImage stegoImage){
        int pixel = stegoImage.getRGB(1, 0);
        int red = (pixel & 0x00ff0000) >> 16;

        String redBinary = Integer.toBinaryString(red);
        String redBinaryLSB = redBinary.substring(redBinary.length() - 1);

        if(redBinaryLSB.equals("0")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Determines what algorithm was used for embedding data
     * (Stored in the green and blue LSB of the pixel at (1, 0)
     *
     * @param stegoImage    The image to be used
     * @return              The algorithm used (0 - 2)
     */
    public int getAlgorithm(BufferedImage stegoImage){
        int pixel = stegoImage.getRGB(1, 0);
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;

        String greenBinary = Integer.toBinaryString(green);
        String greenBinaryLSB = greenBinary.substring(greenBinary.length() - 1);

        String blueBinary = Integer.toBinaryString(green);
        String blueBinaryLSB = blueBinary.substring(blueBinary.length() - 1);

        return Integer.parseInt(greenBinaryLSB) + Integer.parseInt(blueBinaryLSB);
    }

    /**
     * Determines how many bits were used in a colour channel
     *
     * @param pixelData     The data of the pixel storing this information
     * @return              The number of bits used in the colour channel
     */
    public int getLSBBits(int[] pixelData){
        String redBinary = Integer.toBinaryString(pixelData[0]);
        String greenBinary = Integer.toBinaryString(pixelData[1]);
        String blueBinary = Integer.toBinaryString(pixelData[2]);

        String redBinaryLSB = redBinary.substring(redBinary.length() - 1);
        String greenBinaryLSB = greenBinary.substring(greenBinary.length() - 1);
        String blueBinaryLSB = blueBinary.substring(blueBinary.length() - 1);

        String finalValue = redBinaryLSB + "" + greenBinaryLSB + "" + blueBinaryLSB;

        return Integer.parseInt(finalValue, 2);
    }

    /**
     * Determines at what position data encoding finished
     *
     * @param stegoImage            The image to be used
     * @return                      The position encoding finished
     */
    public int[] getEndPosition(BufferedImage stegoImage){
        StringBuilder position = new StringBuilder();
        for(int i = 0; i < param_lengths[5] / 3; i++){
            int[] pixelData = getPixelData(stegoImage,i + 2, 0);
            String redBinary = Integer.toBinaryString(pixelData[0]);
            String redBinaryLSB = redBinary.substring(redBinary.length() - 1);
            String greenBinary = Integer.toBinaryString(pixelData[1]);
            String greenBinaryLSB = greenBinary.substring(greenBinary.length() - 1);
            String blueBinary = Integer.toBinaryString(pixelData[2]);
            String blueBinaryLSB = blueBinary.substring(blueBinary.length() - 1);
            position.append(redBinaryLSB + greenBinaryLSB + blueBinaryLSB);
        }
        int endX = Integer.parseInt(position.toString(), 2);

        position = new StringBuilder();
        for(int i = 0; i < param_lengths[6] / 3; i++){
            int[] pixelData = getPixelData(stegoImage,i + 7, 0);
            String redBinary = Integer.toBinaryString(pixelData[0]);
            String redBinaryLSB = redBinary.substring(redBinary.length() - 1);
            String greenBinary = Integer.toBinaryString(pixelData[1]);
            String greenBinaryLSB = greenBinary.substring(greenBinary.length() - 1);
            String blueBinary = Integer.toBinaryString(pixelData[2]);
            String blueBinaryLSB = blueBinary.substring(blueBinary.length() - 1);
            position.append(redBinaryLSB + greenBinaryLSB + blueBinaryLSB);
        }
        int endY = Integer.parseInt(position.toString(), 2);

        return new int[] {endX, endY, 0};
    }

    /**
     * Determines what LSB in what colour data encoding finished
     *
     * @param stegoImage        The image to be used
     * @return                  The position of the LSB where encoding finished
     */
    public int getEndLSBPosition(BufferedImage stegoImage){
        StringBuilder position = new StringBuilder();
        for(int i = 0; i < 2; i++){
            int[] pixelData = getPixelData(stegoImage,i + 15, 0);
            String redBinary = Integer.toBinaryString(pixelData[0]);
            String redBinaryLSB = redBinary.substring(redBinary.length() - 1);
            String greenBinary = Integer.toBinaryString(pixelData[1]);
            String greenBinaryLSB = greenBinary.substring(greenBinary.length() - 1);
            String blueBinary = Integer.toBinaryString(pixelData[2]);
            String blueBinaryLSB = blueBinary.substring(blueBinary.length() - 1);
            position.append(redBinaryLSB + greenBinaryLSB + blueBinaryLSB);
        }
        int endLSB = Integer.parseInt(position.toString().substring(0, position.toString().length() - 1), 2);
        return endLSB;
    }



    // LSB MANIPULATION FUNCTIONS
    /**
     * Gets pixel data from an image at a specific location
     *
     * @param coverImage Image to be used
     * @param x          x coordinate of pixel
     * @param y          y coordinate of pixel
     * @return Tuple of RGB values (representing the pixel)
     */
    public int[] getPixelData(BufferedImage coverImage, int x, int y) {
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new int[]{red, green, blue};
    }

    /**
     * Gets the least significant bit of some number
     *
     * @param number        The number to use
     * @param LSB           The LSB we are considering
     * @return              The LSB of the number (in binary)
     */
    public char getBinaryLSB(int number, int LSB){
        StringBuilder binaryColour = new StringBuilder();
        binaryColour.append(getBinaryParameters(number, 8));
        int lsb_Position = 7 - LSB;
        return binaryColour.charAt(lsb_Position);
    }



    // BINARY CONVERSION FUNCTIONS
    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return              ASCII representation of binary data
     */
    public StringBuilder getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            try {
                String binaryData = binaryText.substring(i, i + 8);
                char characterData = (char) Integer.parseInt(binaryData, 2);
                text.append(characterData);
            } catch (Exception e) {
            }
        }
        return text;
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

    /**
     * Gets the LSBs that will be used for each colour
     *
     * @param redBits       Determines whether the red colour channel will be used
     * @param greenBits     Determines whether the green colour channel will be used
     * @param blueBits      Determines whether the blue colour channel will be used
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