package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The LSB class performs LSB encoding and decoding on images
 */
public class LSB {

    /**
     * PARAMETER VARIABLES
     * Store the number of bits each parameter requires
     */
    private int param_endX;
    private int param_endY;
    private int param_redUsed;
    private int param_greenUsed;
    private int param_blueUsed;
    private int param_endColour;


    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public LSB(){
        param_endX = 15;
        param_endY = 15;
        param_redUsed = 1;
        param_greenUsed = 1;
        param_blueUsed = 1;
        param_endColour = 2;
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
    public BufferedImage encode(BufferedImage coverImage, String text){

        boolean red = true;
        boolean blue = true;
        boolean green = true;
        boolean random = false;

        // Determine the colours to consider when overwriting LSBs
        int[] coloursToConsider = getColoursToConsider(red, green, blue);
        int endColour = coloursToConsider[coloursToConsider.length - 1];

        // Check here whether data will fit into image (or not)

        // Get binary text from ASCII text
        StringBuilder binaryText = getBinaryText(text);

        // Encode the binary text into the image
        int[] endPoint = encodeData(coverImage, binaryText, coloursToConsider, endColour);

        // Encode the parameters into the image
        encodeParameters(coverImage, endPoint[0], endPoint[1]);

        return coverImage;
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
     * Encodes the stream of binary data into the cover Image
     *
     * @param coverImage            Image to be used
     * @param binaryText            Binary data to be encoded
     * @param coloursToConsider     List of colours we will encode data into
     * @param endColour             Stores the final colour we encode on
     * @return              Tuple storing final insertion parameters
     */
    public int[] encodeData(BufferedImage coverImage, StringBuilder binaryText, int[] coloursToConsider, int endColour){
        int[] pixelData;
        int x = 0; int y = 0;
        for(int i = 0; i < binaryText.length(); i += coloursToConsider.length){
            pixelData = getPixelData(coverImage, x, y);

            // Encode data into LSBs of colours used
            for(int j = 0; j < coloursToConsider.length; j++){
                if(i + j >= binaryText.length()){
                    endColour = j - 1;
                    break;
                }
                char data = binaryText.charAt(i);
                pixelData[i] = insertLSB(pixelData[i], data);
            }

            writePixelData(coverImage, pixelData, x, y);
            x = (x + 1) % coverImage.getWidth();
            if(x == 0){
                y += 1;
            }
        }
        return new int[] {x, y};
    }

    /**
     * Encodes the parameter data into the cover image
     *
     * @param coverImage    Image to be used
     * @param finalX        Parameter storing x coordinate of final data
     * @param finalY        Parameter storing y coordinate of final data
     */
    public void encodeParameters(BufferedImage coverImage, int finalX, int finalY){
        // Encode x coordinate of final encoding
        String param_finalX = Integer.toBinaryString(finalX);
        param_finalX = ("000000000000000" + param_finalX).substring(param_finalX.length());
        encodeIndividualParameter(coverImage, param_finalX, 0);

        String param_finalY = Integer.toBinaryString(finalY);
        param_finalY = ("000000000000000" + param_finalY).substring(param_finalY.length());
        encodeIndividualParameter(coverImage, param_finalY, param_endX_length);
    }

    /**
     * Encodes an individual parameter into the cover image
     *
     * @param coverImage        Image to be used
     * @param parameter         String parameter (in binary)
     * @param reserved_start    Value used to determine starting position of parameter in image
     */
    public void encodeIndividualParameter(BufferedImage coverImage, String parameter, int reserved_start){
        char data;
        int x = reserved_start % coverImage.getWidth();
        int y = reserved_start / coverImage.getWidth();
        int[] pixelData;
        for(int i = 0; i < parameter.length(); i++){
            data = parameter.charAt(i);
            pixelData = getPixelData(coverImage, x, y);
            insertLSB(pixelData, data);
            writePixelData(coverImage, pixelData, x, y);
            x = (x + 1) % coverImage.getWidth();
            if(x == 0){
                y += 1;
            }
        }
    }

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

    /**
     * Converts input text string into binary
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




    // ======= Decoding Functions =======
    /**
     * Performs the LSB decoding algorithm
     * (detailed in LSB.txt)
     *
     * @return
     */
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

    /**
     * Gets the parameter data from the stego image
     *
     * @param stegoImage    Image to be used
     * @return              Tuple of data storing parameter values
     */
    public int[] getParameterData(BufferedImage stegoImage){
        String endX_String = getIndividualParameter(stegoImage, 0, param_endX_length);
        int endX = Integer.parseInt(endX_String, 2);
        String endY_String = getIndividualParameter(stegoImage, param_endX_length, param_endY_length);
        int endY = Integer.parseInt(endY_String, 2);
        return new int[] {endX, endY};
    }

    /**
     * Gets an individual parameter from the stego image
     *
     * @param stegoImage        Image to be used
     * @param reserved_start    Value used to determine starting position of parameter in image
     * @param reserved_length   Length of parameter (number of bits)
     * @return                  String parameter (in binary)
     */
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

    /**
     * Gets the stream of binary data hidden within the stego Image
     *
     * @param stegoImage    Image to be used
     * @param endX          x coordinate for final data (in image)
     * @param endY          y coordinate for final data (in image)
     * @return              Binary stream of data
     */
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

    /**
     * Gets the LSB of a pixel from an image at a specific location
     *
     * @param stegoImage    Image to be used
     * @param x             x coordinate of pixel
     * @param y             y coordinate of pixel
     * @return              Least significant bit of pixel
     */
    public String getPixelLSB(BufferedImage stegoImage, int x, int y){
        int pixel = stegoImage.getRGB(x, y);
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;

        String binaryGreen = Integer.toBinaryString(green);

        return binaryGreen.substring(binaryGreen.length() - 1);
    }
}