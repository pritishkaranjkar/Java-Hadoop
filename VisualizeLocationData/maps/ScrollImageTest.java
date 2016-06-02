import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ScrollImageTest extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage image;
    private JPanel canvas;
    
    private static boolean guiLoaded = false;
    
    private JButton changeFloor;

    public ScrollImageTest() {
        try {
            this.image = ImageIO.read(new File("maps/ghc1.jpg"));
        }catch(IOException ex) {
            Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.canvas = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        
        Box control = Box.createVerticalBox();
        
        changeFloor = new JButton("Change Floor");
        changeFloor.addActionListener(new ActionHandler());
        
        control.add(changeFloor);
        control.add(Box.createVerticalStrut(20));
        control.add(new JLabel("Hello World"));
        
        canvas.add(control);
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        JScrollPane sp = new JScrollPane(canvas);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    class ActionHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if(guiLoaded) {
				try {
		            image = ImageIO.read(new File("maps/ghc4.jpg"));
		        }catch(IOException ex) {
		            Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
		        }
				
				canvas.repaint();
			}
			
		}
		
	}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel p = new ScrollImageTest();
                JFrame f = new JFrame();
                f.setContentPane(p);
                f.setSize(1400, 800);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
                
                guiLoaded = true;
            }
        });
    }
}