package animtest;

import javax.swing.JFrame;

public class Blit extends JFrame {

	private int boardW = 720;
	private int boardH = 480;
	
    public Blit() {

        add(new Board());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(boardW, boardH);
        setLocationRelativeTo(null);
        setTitle("Testing");
        setResizable(false);
        setVisible(true);

    }

    public static void main(String[] args) {
        new Blit();
    }
}