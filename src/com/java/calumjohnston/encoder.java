package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.encode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
    private JButton mainMenuButton;
    private JSpinner redSpinner;
    private JSpinner greenSpinner;
    private JSpinner blueSpinner;

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

    private encode encoder;


    // ======= CONSTRUCTOR =======
    /**
     * Constructor for the class
     */
    public encoder() {

        encoder = new encode();

        red = false;
        green = false;
        blue = false;
        random = false;
        seed = "";

        encodeButton.setEnabled(false);

        openFileChooser = new JFileChooser();
        // Directory is for testing - change for release
        openFileChooser.setCurrentDirectory(new File("C:\\Users\\Calum\\Documents\\3rd year - Dissertation\\Steganography Desktop App\\rsts"));

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
                if(randomCheckBox.isSelected()) {
                    seed = getSeed();
                }
                if(seed == null){
                    randomCheckBox.setSelected(false);
                }
            }
        });
    }


    // ======= ENCODE FUNCTIONS =======
    /**
     * Determines which algorithm to apply when encoding the data
     */
    public void encodeData() {
        // Check enough options have been selected

        // Get algorithm to be used
        int algorithm = algorithmComboBox.getSelectedIndex();

        // Calls algorithm to embed the data
        BufferedImage stegoImage = encoder.encodeImage(coverImage, textField.getText(),
                red, green, blue, (Integer) redSpinner.getValue(), (Integer) greenSpinner.getValue(),
                (Integer) blueSpinner.getValue(), random, seed, algorithm);

        if(stegoImage == null){
            String message = "Input text too large - try increasing number of colours components to use!";
            JOptionPane.showMessageDialog(null, message);
        }else{
            writeImageFile(stegoImage);
        }
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
     * Gets the password from the user - to act as the seed for PRNG
     *
     * @return The user's input (from a dialog box)
     */
    public String getSeed(){
        String seed = JOptionPane.showInputDialog("Please select a password for the data");
        return seed;
    }

    /** Gets the main panel of the form
     *
     * @return The JPanel of the current form
     */
    public JPanel getPanel(){
        return rootPanel;
    }
}
