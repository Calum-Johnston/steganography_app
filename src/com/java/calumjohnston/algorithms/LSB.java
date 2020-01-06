package com.java.calumjohnston.algorithms;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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





    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public LSB(){
        // Default values
        param_number = 6;
        param_lengths = new int[param_number];
        param_lengths[0] = 15;  // Length for endX
        param_lengths[1] = 15;  // Length for endY
        param_lengths[2] = 2;  // Length for endColour
        param_lengths[3] = 1;  // Length for red
        param_lengths[4] = 1;  // Length for green
        param_lengths[5] = 1;  // Length for blue
    }




    // ======= Encoding Functions =======
    /**
     * Performs the LSB encoding algorithm
     * - Detailed in lsb.txt
     *
     * @param coverImage    Image to be used (acting as the Cover)
     * @param text          Data to be inserted (acting as the Payload)
     * @return              Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encode(BufferedImage coverImage, String text,
                                boolean red, boolean green, boolean blue, boolean random){

        // SETUP STUFF
        // Determine the colours to consider when overwriting LSBs
        int[] coloursToConsider = getColoursToConsider(red, green, blue);

        // Get binary text from ASCII text
        StringBuilder binaryText = getBinaryText(text);

        // Determine optimal parameter lengths
        determineOptimalParameterLength(coverImage);

        // Check whether text will fit into image
        if(((double) binaryText.length() / 3) + IntStream.of(param_lengths).sum() <= coverImage.getWidth() * coverImage.getHeight() * 3) {

            // Encode the binary text into the image
            int[] finalValues = encodeData(coverImage, binaryText, coloursToConsider,
                    (int) Math.ceil(((double) IntStream.of(param_lengths).sum() % coverImage.getWidth()) / 3),
                    (IntStream.of(param_lengths).sum() / coverImage.getWidth()) / 3);

            // Get all the parameters and number of bits they use
            ArrayList<Integer> parameters = getParameters(red, green, blue, finalValues);

            // Get binary version of parameters
            StringBuilder binaryParameters = getBinaryParameters(coverImage, parameters);
            System.out.println(param_lengths[0] + " " + param_lengths[1]);
            System.out.println(binaryParameters);
            // Encode binary parameters into the image
            encodeData(coverImage, binaryParameters, coloursToConsider, 0, 0);

            for(int i = 0;  i < 15; i++){
                int[] pixelData = getPixelData(coverImage, i, 0);
                System.out.println(Integer.toBinaryString(pixelData[0]).substring(Integer.toBinaryString(pixelData[0]).length() - 1));
                System.out.println(Integer.toBinaryString(pixelData[1]).substring(Integer.toBinaryString(pixelData[1]).length() - 1));
                System.out.println(Integer.toBinaryString(pixelData[2]).substring(Integer.toBinaryString(pixelData[2]).length() - 1));
            }

            return coverImage;
        }

        return null;
    }

    // SETUP FUNCTIONS FOR ENCODING
    /**
     * Determines optimal parameter length based on image size
     *
     * @param coverImage    Image to be used
     */
    public void determineOptimalParameterLength(BufferedImage coverImage){
            param_lengths[0] = Integer.toBinaryString(coverImage.getWidth()).length();
            param_lengths[1] = Integer.toBinaryString(coverImage.getWidth()).length();
    }

    /**
     * Gets the colours that will be used
     *
     * @param red       Boolean to whether red will be used
     * @param green     Boolean to whether green will be used
     * @param blue      Boolean to whether blue will be used
     * @return          An int[] array storing only the colours it considers
     */
    public int[] getColoursToConsider(boolean red, boolean green, boolean blue){
        if(red && green && blue){
            return new int[] {0, 1, 2};
        }else if(red && green){
            return new int[] {0, 1};
        }else if(red && blue){
            return new int[] {0, 2};
        }else if(blue && green){
            return new int[] {1, 2};
        }else if(red){
            return new int[] {0};
        }else if(green){
            return new int[] {1};
        } else if (blue){
            return new int[] {2};
        }
        return new int[] {0, 1, 2};
    }

    /**
     * Gets all the parameters and stores them with their corresponding number of bits
     * required to store
     *
     * @param red           Boolean to whether red will be used
     * @param green         Boolean to whether green will be used
     * @param blue          Boolean to whether blue will be used
     * @param finalValues   Tuple storing final insertion parameters
     * @return              A map which stores parameters and their corresponding no. of bits required
     */
    public ArrayList<Integer> getParameters(boolean red, boolean green, boolean blue, int[] finalValues){
        ArrayList<Integer> parameters = new ArrayList<Integer>();
        parameters.add(finalValues[0]);
        parameters.add(finalValues[1]);
        parameters.add(finalValues[2]);
        parameters.add(red ? 1 : 0);
        parameters.add(green ? 1 : 0);
        parameters.add(blue ? 1 : 0);
        return parameters;
    }


    // ENCODING FUNCTIONS
    /**
     * Encodes the stream of binary data into the cover Image
     *
     * @param coverImage            Image to be used
     * @param binary            Binary data to be encoded
     * @param coloursToConsider     List of colours we will encode data into
     * @param startEncodingX        Position to start the encoding from (x)
     * @param startEncodingY        Position to start the encoding from (y)
     * @return                      Tuple storing final insertion parameters
     */
    public int[] encodeData(BufferedImage coverImage, StringBuilder binary,
                            int[] coloursToConsider, int startEncodingX, int startEncodingY){
        int[] pixelData;
        int endColour = coloursToConsider[coloursToConsider.length - 1];
        int x = startEncodingX ; int y = startEncodingY;
        for(int i = 0; i < binary.length(); i += coloursToConsider.length){

            // Get pixel data of current pixel
            pixelData = getPixelData(coverImage, x, y);

            // Encode data into LSBs of colours used
            for(int j = 0; j < coloursToConsider.length; j++){
                if(i + j >= binary.length()) {
                    endColour = j - 1;
                    writePixelData(coverImage, pixelData, x, y);
                    return new int[] {x, y, endColour};
                }
                char data = binary.charAt(i + j);
                pixelData[j] = insertLSB(pixelData[j], data);
            }

            // Write data t current pixel
            writePixelData(coverImage, pixelData, x, y);

            // Increment coordinates
            x = (x + 1) % coverImage.getWidth();
            if(x == 0){
                y += 1;
            }
        }
        // Returns data (acting as parameter data)
        return new int[] {x, y, endColour};
    }


    // PIXEL MANIPULATION FUNCTIONS
    /**
     * Gets pixel data from an image at a specific location
     *
     * @param coverImage  Image to be used
     * @param x           x coordinate of pixel
     * @param y           y coordinate of pixel
     * @return            Tuple of RGB values (representing the pixel)
     */
    public int[] getPixelData(BufferedImage coverImage, int x, int y){
        int pixel = coverImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new int[] {red, green, blue};
    }

    /**
     * Inserts data into pixel data (by LSB technique)
     *
     * @param colour        Original colour to be manipulated
     * @param data          Data to be inserted
     * @return              Updated colur
     */
    public int insertLSB(int colour, char data){
        if(colour % 2 == 0 && data == '1'){
            return colour + 1;
        }else if(colour % 2 == 1 && data == '0') {
            return colour - 1;
        }
        return colour;
    }

    /**
     * Writes pixel data to an image at a specific location
     *
     * @param coverImage    Image to be used
     * @param pixelData     Typle of RGB values (representing the pixel)
     * @param x             x coordinate of pixel
     * @param y             y coordinate of pixel
     */
    public void writePixelData(BufferedImage coverImage, int[] pixelData, int x, int y){
        Color newColour = new Color(pixelData[0], pixelData[1], pixelData[2]);
        int newRGB = newColour.getRGB();
        coverImage.setRGB(x, y, newRGB);
    }


    // BINARY CONVERSION FUNCTIONS
    /**
     * Converts input text into binary equivalent
     *
     * @param text  The ASCII text to be converted to binary
     * @return      Binary Equivalent of ASCII text
     */
    public StringBuilder getBinaryText(String text){
        byte[] bytes = text.getBytes();
        StringBuilder binary = new StringBuilder();
        for(byte b : bytes){
            String binaryData = Integer.toBinaryString(b);
            String formatted = ("00000000" + binaryData).substring(binaryData.length());
            binary.append(formatted);
        }
        return binary;
    }

    /**
     * Converts input parameters into binary equivalent
     * (Parameters represented by int[][]
     *
     * @param coverImage    Image to be used
     * @param parameters    Parameters to be inserted into the image (with number of bits to use)
     * @return              The binary equivalent of the input parameters (correct to number of bits)
     */
    public StringBuilder getBinaryParameters(BufferedImage coverImage, ArrayList<Integer> parameters){
        StringBuilder binaryParameters = new StringBuilder();
        for(int i = 0; i < parameters.size(); i++){
            String binaryParameter = Integer.toBinaryString(parameters.get(i));
            binaryParameters.append((StringUtils.repeat('0', param_lengths[i]) + binaryParameter).substring(binaryParameter.length()));
        }
        return binaryParameters;
    }





/*    // ======= Decoding Functions =======
    /**
     * Performs the LSB decoding algorithm
     * (detailed in LSB.txt)
     *
     * @return
     *//*
    public String decode(BufferedImage stegoImage){

        // Gets the parameter data (i.e. stopping position)
        int[] endPoint = getParameterData(stegoImage);

        // Gets the binary text
        StringBuilder binaryText = getBinaryText(stegoImage, endPoint[0], endPoint[1]);
        if(binaryText == null){
            return "";
        }

        // Converts binary text to ASCII text
        StringBuilder text = getText(binaryText);
        if(text == null){
            return "";
        }

        return text.toString();

    }

    *//**
     * Gets the parameter data from the stego image
     *
     * @param stegoImage    Image to be used
     * @return              Tuple of data storing parameter values
     *//*
    public int[] getParameterData(BufferedImage stegoImage){
        String endX_String = getIndividualParameter(stegoImage, 0, param_endX_length);
        int endX = Integer.parseInt(endX_String, 2);
        String endY_String = getIndividualParameter(stegoImage, param_endX_length, param_endY_length);
        int endY = Integer.parseInt(endY_String, 2);
        return new int[] {endX, endY};
    }

    *//**
     * Gets an individual parameter from the stego image
     *
     * @param stegoImage        Image to be used
     * @param reserved_start    Value used to determine starting position of parameter in image
     * @param reserved_length   Length of parameter (number of bits)
     * @return                  String parameter (in binary)
     *//*
    public String getIndividualParameter(BufferedImage stegoImage, int reserved_start, int reserved_length){
        StringBuilder value = new StringBuilder();
        int x = reserved_start % stegoImage.getWidth();
        int y = reserved_start / stegoImage.getWidth();
        for(int i = 0; i < reserved_length; i++){
            value.append(getPixelLSB(stegoImage, x, y));
            x += 1;
            if(x == stegoImage.getWidth()){
                x = 0; y += 1;
            }
        }
        return value.toString();
    }

    *//**
     * Gets the stream of binary data hidden within the stego Image
     *
     * @param stegoImage    Image to be used
     * @param endX          x coordinate for final data (in image)
     * @param endY          y coordinate for final data (in image)
     * @return              Binary stream of data
     *//*
    public StringBuilder getBinaryText(BufferedImage stegoImage, int endX, int endY){
        StringBuilder binaryText = new StringBuilder();

        // Get position to start collecting data from
        int currentX = param_endX_length + param_endY_length;
        int currentY = (param_endX_length + param_endY_length) / stegoImage.getWidth();

        // Check if positions are out of bounds
        // (i.e. it is an image that has been encoded)
        if(endX >= stegoImage.getWidth() || endY >= stegoImage.getHeight()){
            return null;  // Return null to indicate not an encoded image
        }

        while(currentX != endX || currentY != endY){
            binaryText.append(getPixelLSB(stegoImage, currentX, currentY));
            currentX = (currentX + 1) % stegoImage.getWidth();
            if(currentX == 0){
                currentY += 1;
            }
        }
        return binaryText;
    }

    *//**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return
     *//*
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

    *//**
     * Gets the LSB of a pixel from an image at a specific location
     *
     * @param stegoImage    Image to be used
     * @param x             x coordinate of pixel
     * @param y             y coordinate of pixel
     * @return              Least significant bit of pixel
     *//*
    public String getPixelLSB(BufferedImage stegoImage, int x, int y) {
        int pixel = stegoImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;

        String binaryGreen = Integer.toBinaryString(green);

        return binaryGreen.substring(binaryGreen.length() - 1);
    }*/
}