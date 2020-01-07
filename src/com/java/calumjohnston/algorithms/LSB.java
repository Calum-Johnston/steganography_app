package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * The LSB class performs LSB encoding and decoding on images
 */
public class LSB {

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


    // ======= CONSTRUCTOR(S) =======

    /**
     * Constructor for the class
     */
    public LSB() {
        // Default values
        param_number = 6;
        param_lengths = new int[param_number];
        param_lengths[0] = 1;  // Length for red
        param_lengths[1] = 1;  // Length for green
        param_lengths[2] = 1;  // Length for blue
        param_lengths[3] = 2;  // Length for end colour
        param_lengths[4] = 15;  // Length for end x
        param_lengths[5] = 15;  // Length for end y
    }


    // ======= Encoding Functions =======
    /**
     * Performs the LSB encoding algorithm
     * - Detailed in lsb.txt
     *
     * @param coverImage    Image to be used (acting as the Cover)
     * @param text          Data to be inserted (acting as the Payload)
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the blue colour channel will be used
     * @param blue          Determines whether the green colour channel will be used
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @return Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encode(BufferedImage coverImage, String text,
                                boolean red, boolean green, boolean blue, boolean random, String seed) {
        // SETUP STUFF
        // Determine the colours to consider when overwriting LSBs
        int[] coloursToConsider = getColoursToConsider(red, green, blue);

        // Get binary text from ASCII text
        StringBuilder binaryText = getBinaryText(text);

        // Initialise pseudo random number generator
        if (random) {
            generator = new pseudorandom(coverImage.getHeight(), coverImage.getWidth(), seed);
        }

        // Check whether text will fit into image
        int dataToInsert = (int) ((double) binaryText.length() / coloursToConsider.length) + IntStream.of(param_lengths).sum();
        int dataImageCanStore = coverImage.getWidth() * coverImage.getHeight() * coloursToConsider.length;
        if (dataToInsert < dataImageCanStore) {

            // Encode the data into the image
            int[] finalValues = encodeData(coverImage, binaryText, coloursToConsider, "normal", random);

            // Encode the parameters into the image
            encodeParameters(coverImage, random, coloursToConsider, red, green, blue, finalValues);

            System.out.println(Integer.toBinaryString(finalValues[0]) + " " + Integer.toBinaryString(finalValues[1]) + " " + finalValues[2]);

            return coverImage;
        }

        return null;
    }


    // ENCODING FUNCTIONS
    /**
     * Gets all the parameters and stores them with their corresponding number of bits
     * required to store
     *
     *
     * @param coverImage            The image to be used
     * @param random                Determines whether the PRNG will be used
     * @param coloursToConsider     List of colours we will encode data into
     * @param red                   Boolean to whether red will be used
     * @param green                 Boolean to whether green will be used
     * @param blue                  Boolean to whether blue will be used
     * @param finalValues           Tuple storing final insertion parameters
     */
    public void encodeParameters(BufferedImage coverImage, boolean random, int[] coloursToConsider,
                                 boolean red, boolean green, boolean blue, int[] finalValues) {
        // Encode colours used in first bit
        StringBuilder parameters = new StringBuilder();
        parameters.append(getBinaryParameters(red ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(green ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(blue ? 1 : 0, param_lengths[1]));
        encodeData(coverImage, parameters, new int[]{0, 1, 2}, "colour", false);

        // Encode all other parameters as you would with data
        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(finalValues[2], param_lengths[3]));
        parameters.append(getBinaryParameters(finalValues[0], param_lengths[4]));
        parameters.append(getBinaryParameters(finalValues[1], param_lengths[5]));
        encodeData(coverImage, parameters, coloursToConsider, "parameter", random);
    }

    /**
     * Encodes the stream of binary data into the cover Image
     *
     * @param coverImage        Image to be used
     * @param binary            Binary data to be encoded
     * @param coloursToConsider List of colours we will encode data into
     * @param dataType          Defines the type of data being inserted
     * @param random            Boolean to determine whether a PRNG will be used
     * @return Tuple storing final insertion parameters
     */
    public int[] encodeData(BufferedImage coverImage, StringBuilder binary,
                            int[] coloursToConsider, String dataType, boolean random) {
        int[] pixelData;
        int endColour = coloursToConsider[coloursToConsider.length - 1];
        int imageWidth = coverImage.getWidth();

        // Get starting Position
        int[] currentPosition = getStartingPosition(coverImage, random, dataType, coloursToConsider);

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += coloursToConsider.length) {

            // Get pixel data of current pixel
            pixelData = getPixelData(coverImage, currentPosition[0], currentPosition[1]);

            // Encode data into LSBs of colours used
            for (int j = 0; j < coloursToConsider.length; j++) {
                if (i + j >= binary.length()) {
                    endColour = j - 1;
                    writePixelData(coverImage, pixelData, currentPosition[0], currentPosition[1]);
                    return new int[]{currentPosition[0], currentPosition[1], endColour};
                }
                char data = binary.charAt(i + j);
                pixelData[coloursToConsider[j]] = insertLSB(pixelData[coloursToConsider[j]], data);
            }

            // Write data t current pixel
            writePixelData(coverImage, pixelData, currentPosition[0], currentPosition[1]);

            // Increment coordinates
            currentPosition = generateNextPosition(imageWidth, currentPosition, random);
        }
        // Returns data (acting as parameter data)
        return new int[]{currentPosition[0], currentPosition[1], endColour};
    }

    /**
     * Gets the position to start encoding data from (in encodeData() method)
     *
     * @param coverImage            The image to be used
     * @param random                Determines whether the PRNG is used
     * @param dataType              Defines the type of data being inserted
     * @param coloursToConsider     List of colours we will encode data into
     * @return                      The starting position
     */
    public int[] getStartingPosition(BufferedImage coverImage, boolean random, String dataType, int[] coloursToConsider){
        // We always insert what colours we're using into the first pixel
        // (since this is needed to decode everything else
        if(dataType.equals("colour")){
            return new int[] {0, 0};
        }

        // Define starting position when random LSB replacement
        if(random){
            if(dataType.equals("normal")){
                // Set position for payload data insertion
                int newPosition = (int) Math.ceil((IntStream.of(param_lengths).sum() / coloursToConsider.length));
                generator.setPosition(newPosition);
            }else{
                // Set position for parameter data insertion
                generator.setPosition(0);
            }
            int position = generator.getNextElement();
            return new int[] {position % coverImage.getWidth(), position / coverImage.getWidth()};
        }

        // Define starting positions when linear LSB replacement
        if(!(random)){
            if(dataType.equals("normal")){
                // Set position for payload data insertion
                int startX = (int) Math.ceil(((double) IntStream.of(param_lengths).sum() % coverImage.getWidth()) / coloursToConsider.length);
                int startY = (IntStream.of(param_lengths).sum() / coverImage.getWidth()) / coloursToConsider.length;
                return new int[] {startX, startY};
            }else{
                // Set position for payload data insertion
                return new int[] {1, 0};
            }
        }
        return null;
    }

    /**
     * Generates next position in image to encode data into
     *
     * @param imageWidth      The width of the image being used
     * @param currentPosition The position which data has just been encoded
     * @param random          Boolean determining whether PRNG is being used
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


    // PIXEL MANIPULATION FUNCTIONS
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
     * Inserts data into pixel data (by LSB technique)
     *
     * @param colour Original colour to be manipulated
     * @param data   Data to be inserted
     * @return Updated colur
     */
    public int insertLSB(int colour, char data) {
        if (colour % 2 == 0 && data == '1') {
            return colour + 1;
        } else if (colour % 2 == 1 && data == '0') {
            return colour - 1;
        }
        return colour;
    }

    /**
     * Writes pixel data to an image at a specific location
     *
     * @param coverImage Image to be used
     * @param pixelData  Typle of RGB values (representing the pixel)
     * @param x          x coordinate of pixel
     * @param y          y coordinate of pixel
     */
    public void writePixelData(BufferedImage coverImage, int[] pixelData, int x, int y) {
        Color newColour = new Color(pixelData[0], pixelData[1], pixelData[2]);
        int newRGB = newColour.getRGB();
        coverImage.setRGB(x, y, newRGB);
    }


    // BINARY CONVERSION FUNCTIONS and SETUP STUFF
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




    // ======= Decoding Functions =======
    /**
     * Performs the LSB decoding algorithm
     * (detailed in LSB.txt)
     *
     * @param stegoImage        Image to be used
     * @param random            Determines whether random encoding has been used
     * @return
     */
    public String decode(BufferedImage stegoImage, boolean random, String seed){

        // Initialise pseudo random number generator
        if (random) {
            generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), seed);
        }

        // Gets the colours to consider when reading LSBs
        int[] pixelData = getPixelData(stegoImage, 0, 0);
        boolean red = getColourParameter(pixelData[0]);
        boolean green = getColourParameter(pixelData[1]);
        boolean blue = getColourParameter(pixelData[2]);
        int[] coloursToConsider = getColoursToConsider(red, green, blue);

        // Get all other parameter data
        int[] parameters = getParameterData(stegoImage, random, coloursToConsider);

        System.out.println(coloursToConsider[0] + " " + coloursToConsider[1] + " " + coloursToConsider[2]);
        System.out.println(parameters[0] + " " + parameters[1] + " " + parameters[2]);

        return "";

    }

    /**
     * Determines whether a colour channel was used for encoding
     *
     * @param colour            The colour to check against
     * @return                  Whether the colour channel was used
     */
    public boolean getColourParameter(int colour){
        String binary = Integer.toBinaryString(colour);
        String LSB = binary.substring(binary.length() - 1);
        if(LSB.equals("0")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Gets the parameter data from the stego image
     *
     * @param stegoImage    Image to be used
     * @return              Tuple of data storing parameter values
     */
    public int[] getParameterData(BufferedImage stegoImage, boolean random, int[] coloursToConsider){
        int paramLen = IntStream.of(param_lengths).sum() - 3;
        String binary = getParameterBinary(stegoImage, paramLen, random, coloursToConsider, "Parameter");

        int startParameterPos = 0;
        int endColour = Integer.parseInt(binary.substring(startParameterPos, param_lengths[3]), 2); startParameterPos += param_lengths[3];
        int endX = Integer.parseInt(binary.substring(startParameterPos, param_lengths[4] + startParameterPos), 2); startParameterPos += param_lengths[4];
        int endY = Integer.parseInt(binary.substring(startParameterPos), 2);

        return new int[] {endX, endY, endColour};
    }

    /**
     * Gets an binary equivalent of parameter data from the image
     *
     * @param stegoImage        Image to be used
     * @param reserved_length   Length of parameter (number of bits)
     * @param random            Determines whether the PRNG has been used in encoding
     * @return                  String parameter (in binary)
     */
    public String getParameterBinary(BufferedImage stegoImage, int reserved_length, boolean random, int[] coloursToConsider,
                                      String dataType){
        StringBuilder value = new StringBuilder();
        int[] pixelData;

        // Get starting position
        int[] currentPosition = getDecodeStartingPosition(stegoImage, random, coloursToConsider, dataType);

        for(int i = 0; i < reserved_length; i += coloursToConsider.length){

            // Get the pixel data
            pixelData = getPixelData(stegoImage, currentPosition[0], currentPosition[1]);

            // Encode data into LSBs of colours used
            for (int j = 0; j < coloursToConsider.length; j++) {
                if (i + j >= reserved_length) {
                    break;
                }
                String LSB = Integer.toBinaryString(pixelData[coloursToConsider[j]]);
                LSB = LSB.substring(LSB.length() - 1);
                value.append(LSB);
            }

            // Update position
            currentPosition = generateNextPosition(stegoImage.getWidth(), currentPosition, random);
        }
        return value.toString();
    }

    public int[] getDecodeStartingPosition(BufferedImage stegoImage, boolean random, int[] coloursToConsider, String dataType){
        if(random){
            if(dataType.equals("normal")){
                // Set position for payload data decoding
                int newPosition = (int) Math.ceil((IntStream.of(param_lengths).sum() / coloursToConsider.length));
                generator.setPosition(newPosition);
            }else{
                // Set position for parameter data insertion
                generator.setPosition(0);
            }
            int position = generator.getNextElement();
            return new int[] {position % stegoImage.getWidth(), position / stegoImage.getWidth()};
        }

        if(!random) {
            if(dataType.equals("normal")){
                // Set position for payload data insertion
                int startX = (int) Math.ceil(((double) IntStream.of(param_lengths).sum() % stegoImage.getWidth()) / coloursToConsider.length);
                int startY = (IntStream.of(param_lengths).sum() / stegoImage.getWidth()) / coloursToConsider.length;
                return new int[] {startX, startY};
            }else{
                // Set position for parameter data insertion
                return new int[] {1, 0};
            }
        }
        return null;
    }


    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return
     */
    public StringBuilder getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();

        // Check if binaryText length is a multiple of 8
        // (if not then it has not been encoded with our algorithm)
        if(binaryText.length() % 8 != 0){
            return null;
        }

        for(int i = 0; i < binaryText.length(); i += 8){
            String binaryData = binaryText.substring(i, i + 8);
            char characterData = (char) Integer.parseInt(binaryData, 2);
            text.append(characterData);
        }
        return text;
    }
}




/**
 * UNUSED FUNCTIONS
 *
 *     /**
 *      * Determines optimal parameter length based on image size
 *      *
 *      * @param coverImage    Image to be used
 *      *
 *      *public void determineOptimalParameterLength(BufferedImage coverImage){
        *   param_lengths[0]=Integer.toBinaryString(coverImage.getWidth()).length();
        *   param_lengths[1]=Integer.toBinaryString(coverImage.getWidth()).length();
        *}
 *
 *     /**
 *      * Gets the LSB of a pixel from an image at a specific location
 *      *
 *      * @param stegoImage    Image to be used
 *      * @param x             x coordinate of pixel
 *      * @param y             y coordinate of pixel
 *      * @param colour        Colour channel to be selected
 *      * @return              Least significant bit of a particular colour channel of a pixel
 *      */
 /**public String getPixelLSB(BufferedImage stegoImage,int x,int y,int colour){
        *int pixel=stegoImage.getRGB(x,y);
        *if(colour==0){
        *int red=(pixel&0x00ff0000)>>16;
        *String red_binary=Integer.toBinaryString(red);
        *return red_binary.substring(red_binary.length()-1);
        *}else if(colour==1){
        *int green=(pixel&0x00ff0000)>>16;
        *String green_binary=Integer.toBinaryString(green);
        *return green_binary.substring(green_binary.length()-1);
        *}else{
        *int blue=(pixel&0x00ff0000)>>16;
        *String blue_binary=Integer.toBinaryString(blue);
        *return blue_binary.substring(blue_binary.length()-1);
        *}
        *}
*/