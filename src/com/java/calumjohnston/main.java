package com.java.calumjohnston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main_GUI class runs the GUI for the main page
 */
public class main extends JFrame implements ActionListener{
    private JPanel rootPanel;
    private JPanel cards;

    /**
     * Constructor for this class
     */
    public main() {
        setLayout(new GridLayout());
        setSize(500, 500);
        setTitle("Steganography");
        setBackground(Color.YELLOW);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create cards
        JPanel card0 = new JPanel();
        JButton encode = new JButton("Encode!!");
        encode.addActionListener(this);
        card0.add(encode);

        main_GUI main_gui = new main_GUI();
        JPanel card1 = main_gui.getPanel();
        main_gui.setActionListener(this);

        JPanel card2 = new encoder().getPanel();

        // Create panel that contains cards
        cards = new JPanel(new CardLayout());
        cards.add(card0, "Temp");
        cards.add(card1, "Main");
        cards.add(card2, "Encode");
        //cards.add(card2, "Decode");

        getContentPane().add(cards);
        setVisible(true); //Make JFrame visible

    }

    public void actionPerformed(ActionEvent e){
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, "Main");
    }

    public static void main(String[] args) {
        new main();
    }
}
