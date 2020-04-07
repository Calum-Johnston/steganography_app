package com.java.calumjohnston;

import com.java.calumjohnston.algorithms.PSNR;
import com.java.calumjohnston.algorithms.encodeData;
import com.java.calumjohnston.exceptions.DataOverflowException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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
    private JCheckBox randomCheckBox;
    private JComboBox algorithmComboBox;
    private JButton mainMenuButton;
    private JTextField psnrTextField;

    private final JFileChooser openFileChooser;
    private BufferedImage coverImage;
    private BufferedReader textFile;
    private String text;
    private String coverImageName;

    private boolean random;
    private String seed;

    private encodeData encode;


    // ======= CONSTRUCTOR =======
    /**
     * Constructor for the class
     */
    public encoder() {

        // Define initial parameter values
        random = false;
        seed = "";

        // Define encoders
        encode = new encodeData();

        // Defines the file chooser (for selecting images)
        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("C:\\Users\\Calum\\Documents\\Projects\\Dissertation\\Dissertation Project\\rsts"));

        // Sets up algorithm options in combo box
        algorithmComboBox.addItem("LSB");
        algorithmComboBox.addItem("LSBM");
        algorithmComboBox.addItem("LSBMR");
        algorithmComboBox.addItem("PVD");
        algorithmComboBox.addItem("Canny-LSB");
        algorithmComboBox.addItem("AE-LSB");
        algorithmComboBox.addItem("Sobel-LSBMR");
        algorithmComboBox.addItem("NEW Technique");

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

        randomCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                random = !random;
                if (randomCheckBox.isSelected()) {
                    seed = getSeed();
                }
                if (seed == null) {
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
        if(!checkEncode()){
            return;
        }

        // Get data from combo boxes
        int algorithm = algorithmComboBox.getSelectedIndex();
        String text = textField.getText();

        // Calls algorithm to embed the data
        encode = new encodeData();
        PSNR psnr = new PSNR();
        BufferedImage stegoImage = null;
        try{
            stegoImage = encode.encode(deepCopy(coverImage), algorithm, random, seed, text);
            psnrTextField.setText(Double.toString(psnr.calculatePSNR(deepCopy(coverImage), deepCopy(stegoImage))));
        }catch(DataOverflowException e){
            System.out.println("Error");
        }

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
        if(textField.getText() == null || textField.getText().equals("")){
            JOptionPane.showMessageDialog(rootPanel, "Text is required to complete this action");
            return false;
        }

        // Check whether an image has been provided
        if(coverImage == null){
            JOptionPane.showMessageDialog(rootPanel, "You have forgotten to select an image!");
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
            ImageIO.write(image, "png", outputFile);

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

    /**
     * Returns a clone of a buffered image
     *
     * @param bi    The image to be cloned
     * @return      The cloned image
     */
    public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /** Gets the main panel of the form
     *
     * @return The JPanel of the current form
     */
    public JPanel getPanel(){
        return rootPanel;
    }
}
