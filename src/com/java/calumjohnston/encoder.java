package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.lsb.lsbEncode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * The encoder class defines the GUI for the main form
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
    private JComboBox redLSBComboBox;
    private JComboBox greenLSBComboBox;
    private JComboBox blueLSBComboBox;

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

    private lsbEncode lsbEncoder;


    // ======= CONSTRUCTOR =======
    /**
     * Constructor for the class
     */
    public encoder() {

        // Create a new encode object
        lsbEncoder = new lsbEncode();

        // Define initial parameter values
        red = true;
        green = true;
        blue = true;
        random = false;
        seed = "";

        // Defines the file chooser (for selecting images)
        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\Users\\Calum\\Documents\\3rd year - Dissertation\\Steganography Desktop App\\rsts"));

        // Set check boxes to default as selected
        redCheckBox.setSelected(true);
        greenCheckBox.setSelected(true);
        blueCheckBox.setSelected(true);

        // Sets up algorithm options in combo box
        algorithmComboBox.addItem("LSB");
        algorithmComboBox.addItem("LSBM");
        algorithmComboBox.addItem("LSBMR");

        // Sets up LSB options in combo box
        redLSBComboBox.addItem(1); greenLSBComboBox.addItem(1); blueLSBComboBox.addItem(1);
        redLSBComboBox.addItem(2); greenLSBComboBox.addItem(2); blueLSBComboBox.addItem(2);
        redLSBComboBox.addItem(3); greenLSBComboBox.addItem(3); blueLSBComboBox.addItem(3);
        redLSBComboBox.addItem(4); greenLSBComboBox.addItem(4); blueLSBComboBox.addItem(4);
        redLSBComboBox.addItem(5); greenLSBComboBox.addItem(5); blueLSBComboBox.addItem(5);
        redLSBComboBox.addItem(6); greenLSBComboBox.addItem(6); blueLSBComboBox.addItem(6);
        redLSBComboBox.addItem(7); greenLSBComboBox.addItem(7); blueLSBComboBox.addItem(7);
        redLSBComboBox.addItem(8); greenLSBComboBox.addItem(8); blueLSBComboBox.addItem(8);

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
                if(!red){
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        redLSBComboBox.setEnabled(false);
                    }
                }else{
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        redLSBComboBox.setEnabled(true);
                    }
                }
            }
        });

        greenCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                green = !green;
                if(!green){
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        greenLSBComboBox.setEnabled(false);
                    }
                }else{
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        greenLSBComboBox.setEnabled(true);
                    }
                }
            }
        });

        blueCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blue = !blue;
                if(!blue){
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        blueLSBComboBox.setEnabled(false);
                    }
                }else{
                    if(algorithmComboBox.getSelectedIndex() != 2) {
                        blueLSBComboBox.setEnabled(true);
                    }
                }
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
        algorithmComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(algorithmComboBox.getSelectedIndex() == 0 || algorithmComboBox.getSelectedIndex() == 1){
                    redLSBComboBox.setEnabled(true);
                    greenLSBComboBox.setEnabled(true);
                    blueLSBComboBox.setEnabled(true);
                }
                if(algorithmComboBox.getSelectedIndex() == 2){
                    redLSBComboBox.setEnabled(false);
                    greenLSBComboBox.setEnabled(false);
                    blueLSBComboBox.setEnabled(false);
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
        if(!checkEncode()){
            return;
        }

        // Get data from combo boxes
        int algorithm = algorithmComboBox.getSelectedIndex();
        int redLSBs = redLSBComboBox.getSelectedIndex() + 1;
        int greenLSBs = greenLSBComboBox.getSelectedIndex() + 1;
        int blueLSBs = blueLSBComboBox.getSelectedIndex() + 1;
        String text = textField.getText();

        // Calls algorithm to embed the data
        BufferedImage stegoImage = lsbEncoder.encodeImage(coverImage, text, red, green, blue,
                redLSBs, greenLSBs, blueLSBs, random, seed, algorithm);

        if(stegoImage == null){
            String message = "Input text too large - try increasing number of colours components to use!";
            JOptionPane.showMessageDialog(null, message);
        }else{
            writeImageFile(stegoImage);
        }
    }

    /**
     * Determines whether enough options have been selected to encode
     *
     * @return      Value defines whether encoding is possible or not
     */
    public boolean checkEncode(){
        // Check whether text has been provided
        System.out.println(textField.getText());
        if(textField.getText() == null || textField.getText().equals("")){
            JOptionPane.showMessageDialog(rootPanel, "Text is required to complete this action");
            return false;
        }

        // Check whether an image has been provided
        if(coverImage == null){
            JOptionPane.showMessageDialog(rootPanel, "You have forgotten to select an image!");
            return false;
        }

        // Check whether at least one colour channel is selected
        if(redCheckBox.isSelected() == false && greenCheckBox.isSelected() == false && blueCheckBox.isSelected() == false){
            JOptionPane.showMessageDialog(rootPanel, "Please select at least one colour channel");
            return false;
        }

        return true;
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
