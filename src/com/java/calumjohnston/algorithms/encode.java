package com.java.calumjohnston.algorithms;

import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Performs LSB encoding on several algorithmic techniques
 */
public class encode {

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

    boolean red;
    boolean green;
    boolean blue;
    boolean random;
    int algorithm;
    int[] endPosition;
    int[] coloursToConsider;


    // ======= CONSTRUCTOR(S) =======
    /**
     * Constructor for the class
     */
    public encode() {
        // Default values
        param_number = 7;
        param_lengths = new int[param_number];
        param_lengths[0] = 1;  // Length for red (Bool)
        param_lengths[1] = 1;  // Length for green (Bool)
        param_lengths[2] = 1;  // Length for blue (Bool)
        param_lengths[3] = 1;  // Length for random (bool)
        param_lengths[4] = 2;  // Length for which algorithm is in use
        param_lengths[5] = 15;  // Length for end x (Int)
        param_lengths[6] = 15;  // Length for end y (Int)

        red = false;
        green = false;
        blue = false;
        random = false;
        algorithm = 0;
        endPosition = new int[2];
        coloursToConsider = new int[3];
    }



    // MAIN FUNCTION
    /**
     * Central HUB to control encoding into the image
     *
     * @param coverImage    Image to be used (acting as the Cover)
     * @param text          Data to be inserted (acting as the Payload)
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the blue colour channel will be used
     * @param blue          Determines whether the green colour channel will be used
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     * @return Image with data hidden within it (acting as the Stego Image)
     */
    public BufferedImage encodeImage(BufferedImage coverImage, String text,
                                boolean red, boolean green, boolean blue, boolean random, String seed,
                                     int algorithm) {

        //Set parameter data
        setupParameterData(coverImage, red, green, blue, random, seed, algorithm);

        // Convert ASCII text to binary equivalent
        StringBuilder binaryText = getBinaryText(text);

        // Check whether text will fit into image
        int dataToInsert = (int) ((double) binaryText.length() / coloursToConsider.length) + IntStream.of(param_lengths).sum();
        int dataImageCanStore = coverImage.getWidth() * coverImage.getHeight() * coloursToConsider.length;
        if (dataToInsert < dataImageCanStore) {

            // Encode the data into the image
            int[] endPosition = encodeData(coverImage, binaryText, coloursToConsider, "normal", random, algorithm);

            // Encode the parameters into the image
            encodeParameters(coverImage, random, coloursToConsider, red, green, blue, endPosition, algorithm);

            return coverImage;
        }

        return null;
    }



    // ENCODING FUNCTIONS
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
                            int[] coloursToConsider, String dataType, boolean random,
                            int algorithm) {
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
                pixelData[coloursToConsider[j]] = updateLSB(pixelData[coloursToConsider[j]], data, algorithm);
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
     * Sets the parameter data for the cover image
     *
     * @param coverImage    The image to be used
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the blue colour channel will be used
     * @param blue          Determines whether the green colour channel will be used
     * @param random        Determines whether the PRNG will be used
     * @param seed          Acts as the seed for the PRNG
     * @param algorithm     The algorithm being used to decode
     */
    public void setupParameterData(BufferedImage coverImage, boolean red, boolean green, boolean blue,
                                   boolean random, String seed, int algorithm){

        // Set colours used
        this.red = red;
        this.green = green;
        this.blue = blue;
        coloursToConsider = getColoursToConsider(this.red, this.green, this.blue);

        // Set random
        this.random = random;
        if (random) {
            generator = new pseudorandom(coverImage.getHeight(), coverImage.getWidth(), seed);
        }

        //Set algorithm
        this.algorithm = algorithm;

        // Cannot define end position as we don't know it yet
    }

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
                                 boolean red, boolean green, boolean blue, int[] finalValues,
                                 int algorithm) {
        // Encode colours used in first bit
        StringBuilder parameters = new StringBuilder();
        parameters.append(getBinaryParameters(red ? 1 : 0, param_lengths[0]));
        parameters.append(getBinaryParameters(green ? 1 : 0, param_lengths[1]));
        parameters.append(getBinaryParameters(blue ? 1 : 0, param_lengths[2]));
        encodeData(coverImage, parameters, new int[]{0, 1, 2}, "colour", random, algorithm);

        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(random ? 1 : 0, param_lengths[3]));
        parameters.append(getBinaryParameters(algorithm, param_lengths[4]));
        encodeData(coverImage, parameters, new int[]{0, 1, 2}, "random", random, algorithm);

        // Encode all other parameters as you would with data
        parameters = new StringBuilder();
        parameters.append(getBinaryParameters(finalValues[0], param_lengths[5]));
        parameters.append(getBinaryParameters(finalValues[1], param_lengths[6]));
        encodeData(coverImage, parameters, coloursToConsider, "parameter", random, algorithm);
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
     * Inserts data into pixel's LSB
     *
     * @param colour        Original colour to be manipulated
     * @param data          Data to be inserted
     * @param algorithm     The algorithm being used (0-2)
     * @return              Updated colour
     */
    public int updateLSB(int colour, char data, int algorithm) {
        if(algorithm == 0) {
            if (colour % 2 == 0 && data == '1') {
                return colour + 1;
            } else if (colour % 2 == 1 && data == '0') {
                return colour - 1;
            }
        }
        if(algorithm == 1){
            String binaryColour = Integer.toBinaryString(colour);
            if (!(binaryColour.substring(binaryColour.length() - 1).equals(Character.toString(data)))) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    colour -= 1;
                } else {
                    colour += 1;
                }
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



    // BINARY CONVERSION FUNCTIONS
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
}