package poid;

import poid.view.Main;

import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
            new Main();
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
