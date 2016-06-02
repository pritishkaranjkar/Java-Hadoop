import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/** @see http://stackoverflow.com/questions/9184476 */
public class SwingTest extends JFrame {

    private static final int N = 8;

    public SwingTest() {
        initComponents();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SwingTest().setVisible(true);
            }
        });
    }
    private JPanel panelCenter, panelCreating;
    private JScrollPane scrollPaneCreating, scrollPaneCenter;

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800,800));
        panelCreating = new JPanel();
        scrollPaneCreating = new JScrollPane(panelCreating,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panelCenter = new JPanel();
        panelCenter.setBackground(Color.blue);
        scrollPaneCenter = new JScrollPane(panelCenter);

        // ----------------- Left Panel Init -----------------------
        panelCreating.setLayout(new BoxLayout(panelCreating, BoxLayout.Y_AXIS));
        panelCreating.setBackground(Color.orange);
        panelCreating.setBorder(BorderFactory.createEmptyBorder(N, N, N, N));

        panelCreating.add(createImagePane());
        panelCreating.add(Box.createVerticalStrut(N));
        panelCreating.add(createTextPane());
        panelCreating.add(Box.createVerticalStrut(N));
        panelCreating.add(createTextPane());

        // -------------------------------------------------------
        setLayout(new GridLayout(1, 0));
        add(scrollPaneCreating);
        //add(scrollPaneCenter);
        pack();
    }

    private JScrollPane createImagePane() {
    	JLabel pane = new JLabel(new ImageIcon("maps/ghc1.jpg"));
    	//pane.setPreferredSize(new Dimension(600,600));
    	
    	JScrollPane content = new JScrollPane(pane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    	
    	return content;
    }
    
    private JTextPane createTextPane() {
        JTextPane pane = new JTextPane();
        pane.setText(""
            + "Twas brillig and the slithy toves\n"
            + "Did gyre and gimble in the wabe;\n"
            + "All mimsy were the borogoves,\n"
            + "And the mome raths outgrabe.");

        pane.setBorder(BorderFactory.createEmptyBorder(N, N, N, N));
        return pane;
    }
}