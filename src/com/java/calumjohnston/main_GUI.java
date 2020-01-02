package com.java.calumjohnston;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class main_GUI{
    private static JFrame frame;
    private JPanel rootPanel;
    private JButton encodeButton;

    public main_GUI(){

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                // Open encoder GUI
            }
        });
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new main_GUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Steganography - MAIN MENU");
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
