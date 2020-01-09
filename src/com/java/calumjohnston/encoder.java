package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.LSB;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The encoder_GUI class runs the GUI for the encoding page
 */
public class encoder {

    private JPanel rootPanel;
    private JButton selectImageButton;
    private JButton encodeButton;
    private JTextField textField;
    private JButton textButton;
    private JCheckBox redCheckBox;
    private JCheckBox greenCheckBox;
    private JCheckBox blueCheckBox;
    private JCheckBox randomCheckBox;
    private JComboBox algorithmComboBox;
    private JPanel designPanel;
    private JButton button1;

    private final JFileChooser openFileChooser;
    private BufferedImage coverImage;
    private BufferedReader textFile;
    private String text;
    private String coverImageName;

    private boolean red;
    private boolean green;
    private boolean blue;
    private boolean random;
    private String seed;


    // ======= CONSTRUCTOR =======

    /**
     * Constructor for the class
     */
    public encoder() {

        red = false;
        green = false;
        blue = false;
        random = false;
        seed = "";

        encodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\"));

        algorithmComboBox.addItem("LSB");
        algorithmComboBox.addItem("LSBM");
        algorithmComboBox.addItem("LSBMR");

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readImageFile();
            }
        });

        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                readTextFile();
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                encodeData();
            }
        });

        redCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                red = !red;
            }
        });

        greenCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                green = !green;
            }
        });

        blueCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blue = !blue;
            }
        });

        randomCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                random = !random;
            }
        });
    }


    // ======= ENCODE FUNCTIONS =======

    /**
     * Determines which algorithm to apply when encoding the data
     */
    public void encodeData() {
        // Check enough options have been selected

        // Calls algorithm to embed the data
        BufferedImage stegoImage = null;
        int algorithm = algorithmComboBox.getSelectedIndex();
        if (algorithm == 0) {
            LSB lsb = new LSB();
            stegoImage = lsb.encode(coverImage, textField.getText(), red, green, blue, random, seed);
        } else if (algorithm == 1) {
            //LSBM lsbm = new LSBM();
            //BufferedImage stegoImage = lsbm.encode(coverImage, textField.getText(), red, green, blue, random, seed);
        } else {
            //LSBMR lsbmr = new LSBMR();
            //BufferedImage stegoImage = lsbmr.encode(coverImage, textField.getText(), red, green, blue, random, seed);
        }
        writeImageFile(stegoImage);
    }

    /**
     * Performs the LSBM algorithm - detailed in LSBM.txt
     *
     * @param coverImage
     * @param binaryText
     */
    public void LSBM(BufferedImage coverImage, StringBuilder binaryText) {
        // Initialise starting image pixel position
        int x = 0;
        int y = 0;
        char data;

        for (int pos = 0; pos < binaryText.length(); pos++) {
            // Get message data from text at a specific point
            data = binaryText.charAt(pos);

            // Get pixel data from image at a specific location
            int pixel = coverImage.getRGB(x, y);
            int red = (pixel & 0x00ff0000) >> 16;
            int green = (pixel & 0x0000ff00) >> 8;
            int blue = pixel & 0x000000ff;

            // Manipulate pixel data (only writes to Green LSB currently)
            String binaryGreen = Integer.toBinaryString(green);
            // If the bit to be embedded is not equal to the LSB of the pixel (Green)
            if (!(binaryGreen.substring(binaryGreen.length() - 1).equals(Character.toString(data)))) {
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    green -= 1;
                } else {
                    green += 1;
                }
            }

            // Write new pixel data to image at specified location
            Color newColour = new Color(red, green, blue);
            int newRGB = newColour.getRGB();
            coverImage.setRGB(x, y, newRGB);

            // Update position in image to manipulate pixel
            x += 1;
            if (x == coverImage.getWidth()) {
                x = 0;
                y += 1;
            }
        }

        // Call a function to write image to disk
        writeImageFile(coverImage);

    }

    /**
     * Performs the LSBMR algorithm - detailed in LSBMR.txt
     *
     * @param coverImage
     * @param binaryText
     */
    public void LSBMR(BufferedImage coverImage, StringBuilder binaryText) {
        // Initialise starting image pixel position
        int x = 0;
        int y = 0;
        char data_1;
        char data_2;

        for (int pos = 0; pos < binaryText.length(); pos += 2) {
            // Get message data from text at specific points
            data_1 = binaryText.charAt(pos);
            data_2 = binaryText.charAt(pos + 1);

            // Get first pixel data from image at a specific location
            int pixel_left = coverImage.getRGB(x, y);
            int red_left = (pixel_left & 0x00ff0000) >> 16;
            int green_left = (pixel_left & 0x0000ff00) >> 8;
            int blue_left = pixel_left & 0x000000ff;

            // Get second pixel data from image at a specific location
            int pixel_right = coverImage.getRGB(x + 1, y);
            int red_right = (pixel_right & 0x00ff0000) >> 16;
            int green_right = (pixel_right & 0x0000ff00) >> 8;
            int blue_right = pixel_right & 0x000000ff;

            // Manipulate pixel data
            // Binary relationship between both pixels LSB
            int pixel_Relationship = (int) Math.floor(green_left / 2) + green_right;
            String binary_Relationship = Integer.toBinaryString(pixel_Relationship);
            String LSB_Relationship = binary_Relationship.substring(binary_Relationship.length() - 1);

            int pixel_Relationship_2 = (int) Math.floor((green_left - 1) / 2) + green_right;
            String binary_Relationship_2 = Integer.toBinaryString(pixel_Relationship_2);
            String LSB_Relationship_2 = binary_Relationship_2.substring(binary_Relationship_2.length() - 1);

            // Get binary equivalent of green
            String binary_green_left = Integer.toBinaryString(green_left);
            String binary_green_right = Integer.toBinaryString(green_right);
            // Get LSBs of pixel colour
            String binary_green_left_LSB = binary_green_left.substring(binary_green_left.length() - 1);
            String binary_green_right_LSB = binary_green_right.substring(binary_green_right.length() - 1);

            // Case 1:
            if (Character.toString(data_1).equals(binary_green_left_LSB) &&
                    !(Character.toString(data_2).equals(LSB_Relationship))) {
                // normal, +-1
                if (ThreadLocalRandom.current().nextInt(0, 2) < 1) {
                    green_right -= 1;
                } else {
                    green_right += 1;
                }
            } else if (Character.toString(data_1).equals(binary_green_left_LSB) &&
                    Character.toString(data_2).equals(LSB_Relationship)) {
                // normal, normal

            } else if (!(Character.toString(data_1).equals(binary_green_left_LSB)) &&
                    Character.toString(data_2).equals(LSB_Relationship_2)) {
                // -1, normal
                green_left -= 1;
            } else {
                // +1, normal
                green_right += 1;
            }

            // Write new pixel data to image at specified location
            Color newColour = new Color(red_left, green_left, blue_left);
            int newRGB = newColour.getRGB();
            coverImage.setRGB(x, y, newRGB);

            Color newColour_2 = new Color(red_right, green_right, blue_right);
            int newRGB_2 = newColour.getRGB();
            coverImage.setRGB(x + 1, y, newRGB_2);


            // Update position in image to manipulate pixel
            x += 2;
            if (x >= coverImage.getWidth() - 1) {
                x = 0;
                y += 1;
            }
        }

        // Call a function to write image to disk
        writeImageFile(coverImage);

    }


    // ======= PURPOSE FUNCTIONS =======

    /**
     * Reads an image from file explorer
     * (TYPE: .png)
     */
    public void readImageFile() {
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                // Get image based from file explorer
                coverImage = ImageIO.read(openFileChooser.getSelectedFile());

                // Store image name (ensuring to remove extension)
                coverImageName = openFileChooser.getSelectedFile().getName();
                coverImageName = coverImageName.replaceFirst("[.][^.]+$", "");

                // User can now encode
                encodeButton.setEnabled(true);

                // Debugging purposes
                System.out.println("Successfully read in image");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to read in image");
            }
        }
    }

    /**
     * Reads a text document from file explorer
     * (TYPE: .txt)
     */
    public void readTextFile() {
        // Sets the type of file to get
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnValue = openFileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                // Reads in text file selected in file explorer
                textFile = new BufferedReader(new FileReader(openFileChooser.getSelectedFile()));

                // Converts text file to string
                // https://stackoverflow.com/questions/15040504/how-to-easily-convert-a-bufferedreader-to-a-string
                text = textFile.lines().collect(Collectors.joining());

                // Write text to text field
                textField.setText(text);

                // Debugging purposes
                System.out.println("Successfully read in text");

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to read in text");
            }
        }
    }

    /**
     * Writes the image parameter to the disk
     *
     * @param image The image to be written to file
     */
    public void writeImageFile(BufferedImage image) {
        try {
            // Writes file to the disk (w/extension of algorithm used)
            File outputFile = new File("rsts/" + coverImageName + "_encoded.png");
            ImageIO.write(coverImage, "png", outputFile);

            // Debugging purposes
            System.out.println("Successfully written file to disk");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to write file to disk");
        }
    }

    /**
     *
     * @return The JPanel of the current form
     */
    public JPanel getPanel(){
        return rootPanel;
    }
}
