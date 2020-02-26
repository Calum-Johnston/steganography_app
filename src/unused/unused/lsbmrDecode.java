package unused.unused;

import com.java.calumjohnston.utilities.pseudorandom;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * lSBMR Decode Class: This class implements the extraction of data from an image
 * that has been embedded using the LSBMR technique
 */
public class lsbmrDecode {

    BufferedImage stegoImage;
    boolean random;
    int[] coloursToConsider;
    pseudorandom generator;
    int endPositionX;
    int endPositionY;
    int endColourChannel;

    /**
     * Constructor
     */
    public lsbmrDecode(){

    }

    /**
     * Acts as central controller to decoding functions
     *
     * @param stegoImage    The image we will decode data from
     * @return              The data to be hidden within the image
     */
    public String decode(BufferedImage stegoImage){
        // Setup data to be used for decoding
        setupData(stegoImage);

        // Check parameter data will allow for successful decoding
        checkData();

        // Decode data from the image
        StringBuilder binary = decodeSecretData();

        // Convert the data back to text
        String text = getText(binary);

        // Return the retrieved text
        return text;
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
        boolean red = binaryToInt(parameters.substring(3,4)) == 1;
        boolean green = binaryToInt(parameters.substring(4,5)) == 1;
        boolean blue = binaryToInt(parameters.substring(5,6)) == 1;
        this.coloursToConsider = getColoursToConsider(red, green, blue);

        // Determine whether random embedding is being used
        this.random = binaryToInt(parameters.substring(6, 7)) == 1;
        if (random) {
            this.generator = new pseudorandom(stegoImage.getHeight(), stegoImage.getWidth(), "calum");
        }

        // Get end position for data encoding
        endPositionX = binaryToInt(parameters.substring(7,22));
        endPositionY = binaryToInt(parameters.substring(22,37));
        endColourChannel = binaryToInt(parameters.substring(37));

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
        for (int i = 0; i < 39; i += 1) {

            // Get current colour to manipulate
            if ((currentColourPosition + 1) % (coloursToConsider.length + 1) == 0) {
                currentColourPosition = 0;
                currentPosition = generateNextPosition(currentPosition, false);
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
     * @param random            Determines whether random embedding will be used or not
     * @return                  The new position to consider
     */
    public int[] generateNextPosition(int[] currentPosition, boolean random) {
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



    // ======= CHECK FUNCTIONS =======
    public void checkData(){

    }



    // ======= DECODING FUNCTIONS =======
    /**
     * Decodes the data to be hidden into the image
     *
     * @return          The data hidden within the image
     */
    public StringBuilder decodeSecretData(){

        // Define some initial variables required
        ArrayList<int[]> colourData = null;   // Stores data about the next two colours to manipulate
        int firstColour;            // Stores the first colour to be manipulated
        int secondColour;           // Stores the neighbouring second colour to be manipulated
        StringBuilder binary = new StringBuilder();       // Stores the data we wish have decoded (in bits)

        // Define some variables for determining which pixels to manipulate
        int currentColourPosition = -1;
        int[] firstPosition = generateNextPosition(new int[] {16, 0}, false);
        int[] secondPosition = generateNextPosition(firstPosition, false);

        // Loop through binary data to be inserted
        while(firstPosition[0] != endPositionX || firstPosition[1] != endPositionY || currentColourPosition != endColourChannel) {

            // Get the colour data required for embedding
            colourData = getNextData(firstPosition, secondPosition, currentColourPosition);

            // Update positional information
            firstPosition = colourData.get(0);
            secondPosition = colourData.get(1);
            currentColourPosition = colourData.get(2)[0];

            // Get the next two colour channel data
            firstColour = getColourAtPosition(firstPosition[0], firstPosition[1], currentColourPosition);
            secondColour = getColourAtPosition(secondPosition[0], secondPosition[1], currentColourPosition);

            // Append the retrieved binary data to the final string of data
            binary.append(getLSB(firstColour));
            binary.append(getLSB((firstColour / 2) + secondColour));
        }

        return binary;
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
            firstPosition = generateNextPosition(secondPosition, random);
            secondPosition = generateNextPosition(firstPosition, random);
        }

        // Add data to ArrayList to return
        current.add(firstPosition);
        current.add(secondPosition);
        current.add(new int[] {currentColourPosition});

        return current;
    }



    // ======= CONVERSION FUNCTIONS =======
    /**
     * Converts the binary stream of data to it's ASCII equivalent
     *
     * @param binaryText    Binary stream of data
     * @return              ASCII representation of binary data
     */
    public String getText(StringBuilder binaryText){
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            try {
                String binaryData = binaryText.substring(i, i + 8);
                char characterData = (char) Integer.parseInt(binaryData, 2);
                text.append(characterData);
            } catch (Exception e) {
            }
        }
        return text.toString();
    }
}
