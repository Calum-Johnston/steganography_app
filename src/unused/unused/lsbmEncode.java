package unused.unused;

import com.java.calumjohnston.exceptions.DataOverflowException;
import com.java.calumjohnston.randomgenerators.pseudorandom;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LSBM Encode Class: This class implements the embedding of data into an image
 * using the LSBM technique
 */
public class lsbmEncode {

    BufferedImage coverImage;
    boolean random;
    int[] coloursToConsider;
    ArrayList<Integer> lsbsToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;
    int endColourChannel;
    int endLSBPosition;


    /**
     * Constructor
     */
    public lsbmEncode(){

    }

    /**
     * Acts as central controller to encoding functions
     *
     * @param coverImage    The image we will encode data into
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
    public BufferedImage encode(BufferedImage coverImage, boolean red, boolean green, boolean blue,
                                int redBits, int greenBits, int blueBits,
                                boolean random, String seed, String text){
        // Setup data to be used for encoding
        setupData(coverImage, red, green, blue, redBits, greenBits, blueBits, random, seed);

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
     * @param red           Determines whether the red colour channel will be used
     * @param green         Determines whether the green colour channel will be used
     * @param blue          Determines whether the blue colour channel will be used
     * @param redBits       Number of LSBs to use in red colour channel
     * @param greenBits     Number of LSBs to use in green colour channel
     * @param blueBits      Number of LSBs to use in blue colour channel
     * @param random        Determines whether random embedding has been used
     * @param seed          The seed used to initialise the PRNG (if used)
     */
    public void setupData(BufferedImage coverImage, boolean red, boolean green, boolean blue,
                          int redBits, int greenBits, int blueBits,
                          boolean random, String seed){

        // Define the image we are embedding data into
        this.coverImage = coverImage;

        // Get the colours to consider (some combination of red, green and blue)
        coloursToConsider = getColoursToConsider(red, green, blue);

        // Get the LSBs to consider
        lsbsToConsider = getLSBsToConsider(redBits, greenBits, blueBits, red, green, blue);

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
     * Encodes the data to be hidden into the image
     *
     * @param binary        The binary data to be hidden within the image
     */
    public void encodeSecretData(StringBuilder binary){

        // Define some variables for manipulating pixel data
        ArrayList<int[]> colourData = null;   // Stores data about the next two colours to manipulate
        int firstColour;            // Stores the first colour to be manipulated

        // Define some variables for determining which pixels to manipulate
        int currentColourPosition = -1;
        int currentLSBPosition = -1     ;
        int[] firstPosition = generateNextPosition(new int[] {17, 0}, false);

        // Loop through binary data to be inserted
        for (int i = 0; i < binary.length(); i += 1) {

            // Get the colour data required for embedding
            colourData = getNextData(firstPosition, currentColourPosition, currentLSBPosition, random);

            // Update positional information
            firstPosition = colourData.get(0);
            currentColourPosition = colourData.get(1)[0];
            currentLSBPosition = colourData.get(2)[0];

            // Check we are within bounds
            if(firstPosition[0] >= coverImage.getWidth() || firstPosition[1] >= coverImage.getHeight()){
                throw new DataOverflowException("Input text too large");
            }

            // Get bit required from binary input
            char data_1 = binary.charAt(i);

            // Get the next colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);

            // Update current colour data based on binary data to insert
            firstColour = updateColour(firstColour, data_1, lsbsToConsider.get(currentLSBPosition));

            // Write colour data back to the image
            writeColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition, firstColour);

        }

        // Write end position data (for decoding purposes)
        endPositionX = firstPosition[0];
        endPositionY = firstPosition[1];
        endColourChannel = currentColourPosition;
        endLSBPosition = currentLSBPosition;

    }

    /**
     * Determines the next two pixels we should encode into
     *
     * @param firstPosition         The positional data of the first pixel being considered
     * @param currentColourPosition The current position in coloursToConsider we are using
     * @param random                Determines whether random embedding was used
     * @return                      The order we should consider pixel whilst encoding
     */
    public ArrayList<int[]> getNextData(int[] firstPosition, int currentColourPosition, int currentLSBPosition,
                                        boolean random){

        // Define ArrayList to store data in
        ArrayList<int[]> current = new ArrayList<>();

        // Update current position to check for in coloursToConsider
        currentLSBPosition += 1;

        // Update positions (if ran out of colour channels to manipulate with current positions)
        if(currentLSBPosition == lsbsToConsider.size()){
            currentLSBPosition = 0;
        }
        if(lsbsToConsider.get(currentLSBPosition) == 0) {
            currentColourPosition += 1;
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                firstPosition = generateNextPosition(firstPosition, random);
            }
        }

        // Add data to ArrayList to return
        current.add(firstPosition);
        current.add(new int[] {currentColourPosition});
        current.add(new int[] {currentLSBPosition});

        return current;
    }

    /**
     * Generates the next pixel position to consider when encoding data
     *
     * @param currentPosition   The position which data has just been encoded
     * @param random            Determines whether random embedding will be used or not
     * @return                  The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition, boolean random) {
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
     * Inserts data into a defined significant bit of the colour
     *
     * @param colour        Original colour to be manipulated
     * @param data          Binary data to be inserted into the colour
     * @param position      The position in the colour's 8 bit representation we are considering
     * @return              Updated colour
     */
    public int updateColour(int colour, char data, int position) {
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
        parameters.append(conformBinaryLength(0, 3));
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
        int colour; int data;

        // Loop through binary data to be inserted
        for (int i = 0; i < parameters.length(); i += 1) {

            // Get the data to encode into the image
            data = parameters.charAt(i);

            // Get current colour to manipulate
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                currentPosition = generateNextPosition(currentPosition, false);
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
