package BackgroundThread;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/* GUI that calculates all "Happy" numbers up to a user-inputted value
 * Happy numbers are numbers that eventually reach 1 when you sum the squares of each of their digits
 * i.e. 31 --> 3^2+1^2 = 9+1 = 10 --> 1^2 + 0^2 = 1
 * https://en.wikipedia.org/wiki/Happy_number
 * Press start button and input an integer to start calculating
 * Runs on one background thread and updates the GUI every second
 * Future improvements: larger inputs (>10 million) tend to slow the updates down
 * Couldn't get cancel button to work while program is running
 */

public class BackgroundGUI extends JFrame 
{
	private JButton cancelButton = new JButton("Cancel");
	private JButton startButton = new JButton("Start");
	private JTextArea textArea = new JTextArea("");
	private JTextField userInput= new JTextField("");
	private static int maxCount = 0;
	private int timerCounter = 0;
	final long interval = 1000; // milliseconds per update
	private volatile boolean endThread = false;
	
	public BackgroundGUI() 
	{
		super("Happy Numbers Calculator :)");
		
		setLocationRelativeTo(null);
		setSize(800,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(startButton, BorderLayout.NORTH);
		getContentPane().add(cancelButton, BorderLayout.SOUTH);
		JScrollPane sp = new JScrollPane(textArea);
		getContentPane().add(sp, BorderLayout.CENTER);
		
		// Set default scroll position to bottom when text area updates
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		startButton.addActionListener( new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		        // Create a method named "createFrame()", and set up an new frame there
		         createInputFrame();
		    }
		});
		cancelButton.addActionListener( new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		         endThread = true;
		    }
		});
		setVisible(true);
	} 
	
	
	private void createInputFrame() 
	{
		JFrame f= new JFrame();
		f.setLocationRelativeTo(null);
		f.setSize(400,120);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        
        JButton inputButton = new JButton("Submit");
        JButton cancelButton2 = new JButton("Cancel");
        JLabel label1 = new JLabel();        
        label1.setText("Enter Max Number: ");
        JLabel errorMessage = new JLabel("Enter valid integer!");
        
        label1.setBounds(10,10, 120,25);
        userInput.setBounds(150,10,200,25);
        inputButton.setBounds(150,40,80,25);
        cancelButton2.setBounds(250,40,80,25);
        errorMessage.setBounds(10,40,120,25);
        
        JPanel p = new JPanel(); 
        
		p.setLayout(null);
        
        p.add(label1);
        p.add(userInput);
        p.add(inputButton);
        p.add(cancelButton2);
        p.add(errorMessage);
        errorMessage.setVisible(false);
        
        f.add(p);
        
        inputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
               if (isInt(userInput.getText())) {
            	   textArea.setText("");
            	   maxCount = Integer.parseInt(userInput.getText());
            	   executeTimer();
                   f.dispose();

               } else {
            	   errorMessage.setVisible(true);
               }
               
            }
        });
        
        cancelButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
               f.dispose();
            }
        });
	}
	private void executeTimer() 
	{
		long startTime = System.currentTimeMillis();
		endThread = false;
		Runnable runnable = new Runnable() {
			public void run() {
				
				// Stop calculating if the same number is used (if reaches max)
				timerCounter = 0;
				while (!endThread) {
					for (int i=timerCounter;i<=maxCount;i++) {
						if (isHappy(i)) {
							if (timerCounter != i) {
								timerCounter=i;
								textArea.append(String.valueOf(i)+"\n");
							} else {
								endThread = true;
							}	
						}
					}

					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
				
				// End of calculations output
				if (endThread) {
		        	long endTime = System.currentTimeMillis();
		        	textArea.append("Total run time: "+String.valueOf((double)(endTime-startTime)/1000));
				}
				
			}
		};
        Thread thread = new Thread(runnable);
        thread.start();  

	}
	
	// Check if String is a valid int
	static boolean isInt(String s)
	{
		try
		{ 
			int i = Integer.parseInt(s); 
			return true; 
		}

		catch(NumberFormatException er)
		{ 
			return false; 
		}
	}
	
	public static int pdi_func(int numb) 
	{
		// Perfect digital invariant
		// parse through an integer (ex. 439) digit-by-digit and square each digit to add to sum (4^2+3^2+9^2).
		// assumes base 10
		
		
		int sum = 0;
		String numStr = ""+numb;
		int numLen = numStr.length();
		for (int i=0;i<numLen;++i) {
			int digit = Character.getNumericValue(numStr.charAt(i));
			sum = sum + digit*digit;
		}
		return sum;
	}
	public static boolean isHappy(int number) 
	{
		// Store sums of added digit squares in ArrayList. 
		// Update number using pdi_func
		// Loop until number reaches 1 (making it happy) or it hits a repeat sum (unhappy)
		
		ArrayList<Integer> list1 = new ArrayList<>();
		while (number > 1 && list1.contains(number)==false) {
			list1.add(number);
			number = pdi_func(number);
		}
		if (number==1) {
			return true;
		} else return false;
	}
	
	public static void main(String[] args)
	{
		new BackgroundGUI();
	}
}



