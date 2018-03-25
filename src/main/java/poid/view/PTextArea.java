package poid.view;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class PTextArea extends JTextArea {
    PTextArea(final String promptText) {
        super(promptText);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(getText().equals(promptText)) {
                    setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if(getText().isEmpty()) {
                    setText(promptText);
                }
            }
        });
    }
}
