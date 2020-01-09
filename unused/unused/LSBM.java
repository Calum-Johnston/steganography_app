package com.java.calumjohnston.algorithms.unused;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * The LSB class performs LSB encoding and decoding on images
 */
public class LSBM {

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
    public LSBM() {
        // Default values
        param_number = 6;
        param_lengths = new int[param_number];
        param_lengths[0] = 1;  // Length for red (Bool)
        param_lengths[1] = 1;  // Length for green (Bool)
        param_lengths[2] = 1;  // Length for blue (Bool)
        param_lengths[3] = 1;  // Length for random (bool)
        param_lengths[4] = 15;  // Length for end x (Int)
        param_lengths[5] = 15;  // Length for end y (Int)
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
        System.out.println(dataToInsert + " " + dataImageCanStore);
        if (dataToInsert < dataImageCanStore) {

            // Encode the data into the image
            int[] endPosition = encodeData(coverImage, binaryText, coloursToConsider, "normal", random);

            // Encode the parameters into the image
            encodeParameters(coverImage, random, coloursToConsider, red, green, blue, endPosition);

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
        parameters.append(getBinaryParameters(red ? 1 : 0, param_lengths[0]));
        parameters.append(getBinaryParameters(green ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(blue ? 1 : 0, param_lengths[2]));
        encodeData(coverImage, parameters, new int[]{0, 1, 2}, "colour", random);

        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(random ? 1 : 0, param_lengths[3]));
        encodeData(coverImage, parameters, new int[]{0, 1, 2}, "random", random);

        // Encode all other parameters as you would with data
        parameters = new StringBuilder();
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
     * @param dataType          Defines the type of data being embedded
     * @param random            Determines
     * @return Tuple storing final insertion parameters
     */
    public int[] encodeData(BufferedImage coverImage, StringBuilder binary,
                            int[] coloursToConsider, String dataType, boolean random) {
        int[] pixelData;
        int endColour = coloursToConsider[coloursToConsider.length - 1];
        int imageWidth = coverImage.getWidth();

        // Get starting Position
        int[] currentPosition = getStartPosition(coverImage, random, coloursToConsider, dataType);

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += coloursToConsider.length) {

            // Get pixel data of current pixel
            pixelData = getPixelData(coverImage, currentPosition[0], currentPosition[1]);

            // Encode data into LSBs of colours used
            for (int j = 0; j < coloursToConsider.length; j++) {
                if (i + j >= binary.length()) {
                    endColour = j - 1;
                    writePixelData(coverImage, pixelData, currentPosition[0], currentPosition[1]);
                    currentPosition = generateNextPosition(imageWidth, currentPosition, random);
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


    // LSB MANIPULATION FUNCTIONS
    /**
     * Inserts data into pixel data (by LSB technique)
     *
     * @param colour Original colour to be manipulated
     * @param data   Data to be inserted
     * @return Updated colur
     */
    public int insertLSB(int colour, char data) {
        String binaryColour = Integer.toBinaryString(colour);
        if (!(binaryColour.substring(binaryColour.length() - 1).equals(Character.toString(data)))) {
            if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                colour -= 1;
            } else {
                colour += 1;
            }
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
     * @return                  Text hidden within the image
     */
    public String decode(BufferedImage stegoImage){

        // Gets the colours to consider when reading LSBs
        int[] pixelData = getPixelData(stegoImage, 0, 0);
        boolean red = getColourParameter(pixelData[0]);
        boolean green = getColourParameter(pixelData[1]);
        boolean blue = getColourParameter(pixelData[2]);
        int[] coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether the random embedding was used
        pixelData = getPixelData(stegoImage, 1, 0);
        boolean random = getColourParameter(pixelData[0]);

        // Initialise pseudo random number generator
        if (random) {
            String seed = JOptionPane.showInputDialog("Please select a password for the data");
            if(seed == null){ seed = ""; }
            generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), seed);
        }

        // Get remaining parameter data (simply the end position of data encoding)
        int[] endPosition = getParameterData(stegoImage, random, coloursToConsider);

        // Get binary version of hidden data
        StringBuilder binaryData = getHiddenData(stegoImage, random, coloursToConsider, endPosition);

        // Convert to ASCII equivalent
        StringBuilder text = getText(binaryData);

        return text.toString();

    }

    /**
     * Determines whether an LSB is 1 or 0
     *
     * @param colour            The colour whose LSB will be used
     * @return                  False = 0; True = 1;
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
     * @param stegoImage            Image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @return                      Tuple of data storing parameter values
     */
    public int[] getParameterData(BufferedImage stegoImage, boolean random, int[] coloursToConsider){
        int paramLen = IntStream.of(param_lengths).sum() - 4;
        String binary = getParameterBinary(stegoImage, paramLen, random, coloursToConsider, "Parameter");

        int startParameterPos = 0;
        int endX = Integer.parseInt(binary.substring(startParameterPos, param_lengths[4]), 2);
        int endY = Integer.parseInt(binary.substring(startParameterPos + param_lengths[5]), 2);

        return new int[] {endX, endY};
    }

    /**
     * Gets the binary equivalent of the parameters needed for decoding
     *
     * @param stegoImage            Image to be used
     * @param reserved_length       Total bit length of parameters to be retrieved
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param dataType              Defines the type of data being retrieved
     * @return                      String parameter (in binary)
     */
    public String getParameterBinary(BufferedImage stegoImage, int reserved_length, boolean random, int[] coloursToConsider,
                                     String dataType){
        StringBuilder value = new StringBuilder();
        int[] pixelData;

        // Get starting position
        int[] currentPosition = getStartPosition(stegoImage, random, coloursToConsider, dataType);

        for(int i = 0; i < reserved_length; i += coloursToConsider.length){

            // Get the pixel data
            pixelData = getPixelData(stegoImage, currentPosition[0], currentPosition[1]);

            // Read data from LSBs of colours used
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

    /**
     * Gets the binary equivalent of the hidden data in the image
     *
     * @param stegoImage            The image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param parameters            List of parameters used for determining when to stop decoding
     * @return                      The text hidden within the image
     */
    public StringBuilder getHiddenData(BufferedImage stegoImage, boolean random, int[] coloursToConsider, int[] parameters){
        StringBuilder data = new StringBuilder();
        int[] pixelData;

        // Get starting position
        int[] currentPosition = getStartPosition(stegoImage, random, coloursToConsider, "normal");

        while(currentPosition[0] != parameters[0] || currentPosition[1] != parameters[1]){

            // Get the pixel data
            pixelData = getPixelData(stegoImage, currentPosition[0], currentPosition[1]);

            // Read data from LSBs of colours used
            for (int j = 0; j < coloursToConsider.length; j++) {
                String LSB = Integer.toBinaryString(pixelData[coloursToConsider[j]]);
                LSB = LSB.substring(LSB.length() - 1);
                data.append(LSB);
            }

            // Update position
            currentPosition = generateNextPosition(stegoImage.getWidth(), currentPosition, random);
        }

        return data;

    }

    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return              ASCII representation of binary data
     */
    public StringBuilder getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < binaryText.length(); i += 8){
            try {
                String binaryData = binaryText.substring(i, i + 8);
                char characterData = (char) Integer.parseInt(binaryData, 2);
                text.append(characterData);
            }catch(Exception e){}
        }
        return text;
    }




    // ======= Shared Functions =======
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

    /**
     * Gets the position to start decoding data from
     * (HOPEFULLY USE ONE FUNCTION FOR ENCODING AND DECODING EVENTUALLY!!)
     *
     * @param image                 The image to be used
     * @param random                Determines whether random embedding was used
     * @param coloursToConsider     List of colours that data was embedded into
     * @param dataType              Defines the type of data being retrieved
     * @return                      Position to start decoding (dependent on what we're decoding)
     */
    public int[] getStartPosition(BufferedImage image, boolean random, int[] coloursToConsider, String dataType){

        if(dataType.equals("colour")){
            return new int[] {0, 0};
        }

        if(dataType.equals("random")){
            return new int[] {1, 0};
        }

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
            return new int[] {position % image.getWidth(), position / image.getWidth()};
        }

        if(!random) {
            if(dataType.equals("normal")){
                // Set position for payload data insertion
                int startX = (int) Math.ceil(((double) IntStream.of(param_lengths).sum() % image.getWidth()) / coloursToConsider.length);
                int startY = (IntStream.of(param_lengths).sum() / image.getWidth()) / coloursToConsider.length;
                return new int[] {startX, startY};
            }else{
                // Set position for parameter data insertion
                return new int[] {2, 0};
            }
        }
        return null;
    }



}