package poid.view;

import poid.utils.FilterDetailsExtraction;

import javax.swing.*;
import java.awt.*;

public class ParametersMatrixPane extends JPanel {
    private final FilterDetailsExtraction filterDetailsExtraction;
    private final int ordinal;

    public ParametersMatrixPane(FilterDetailsExtraction filterDetailsExtraction, int ordinal) {
        this.filterDetailsExtraction = filterDetailsExtraction;
        this.ordinal = ordinal;
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        String columnNames[] = {"1", "2", "3"};
        JTable table = new JTable(filterDetailsExtraction.getMasks()[ordinal], columnNames);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setEnabled(false);
        this.add(table);
        this.setSize(100, 50);
    }
}
