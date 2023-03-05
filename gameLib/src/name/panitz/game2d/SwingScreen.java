package name.panitz.game2d;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class SwingScreen extends JPanel{
  private static final long serialVersionUID = 1403492898373497054L;
  public static final int tick = 13;

  public static final double umwandlung = 1000D/tick;
  Game logic;
  Timer t;

  public SwingScreen(Game gl) {
    this.logic = gl;


    t = new Timer(tick, (ev)->{
        logic.move();
        logic.doChecks();
        repaint();
        getToolkit().sync();
        requestFocusInWindow();
      });
      t.start();

		
    addKeyListener(new KeyAdapter() {	
        @Override public void keyPressed(KeyEvent e) {
          logic.keyPressedReaction(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            logic.keyReleasedReaction(e);
        }

    });
    setFocusable(true);
    requestFocus();
    }

	
  @Override public Dimension getPreferredSize() {
    return new Dimension((int)logic.width(),(int)logic.height());
  }

	
  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    logic.paintTo(g);
  }
}

