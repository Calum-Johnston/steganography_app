package com.java.calumjohnston;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class main_GUI{
    private static JFrame frame;
    private JPanel mainPanel;
    private JButton encodeButton;
    private JButton decodeButton;

    private JPanel encodePanel;
    private JPanel decodePanel;

    /**
     * Constructor for this class
     */
    public main_GUI(){

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("s");
                encodePanel = new encoder_GUI().getPanel();
                mainPanel.setVisible(false);
                encodePanel.setVisible(true);
            }
        });

    }


    // ======= MAIN MENU =======
    /**
     * Main Method - instantiates the program
     *
     * @param args  Arguments input when program is run
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new main_GUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - MAIN MENU");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

}
