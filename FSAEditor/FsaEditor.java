import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FsaEditor{
	private JFrame mainFrame;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem openFile, saveFile, quit, newState, newTransition;
	private JMenuItem setInitial, unsetInitial, setFinal, unsetFinal, delete;
	private JFileChooser open, save;

	private FsaReaderWriter fileStream;
	private FsaImpl fsa;
	private FsaPanel panel;
	private JPanel controlBar;

	private JButton reset, step;
	private JTextField stepEvent;
	private JLabel recognised;


	public FsaEditor(){
		fileStream = new FsaReaderWriter();
		fsa = new FsaImpl();
	}

	public void init(){
		//creating frame
		mainFrame = new JFrame("FsaEditor");
		mainFrame.setVisible(true);
		//mainFrame.setSize(500,500);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setSize(screenSize.width, screenSize.height);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//creating menubar
		menuBar = new JMenuBar();

		//file menu
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		openFile = new JMenuItem("Open...");
		openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
		openFile.addActionListener(new MyActionListener());
		fileMenu.add(openFile);

		saveFile = new JMenuItem("Save as...");
		saveFile.setAccelerator(KeyStroke.getKeyStroke("control S"));
		saveFile.addActionListener(new MyActionListener());
		fileMenu.add(saveFile);

		quit = new JMenuItem("Quit");
		quit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
		quit.addActionListener(new MyActionListener());
		fileMenu.add(quit);

		//edit menu
		editMenu = new JMenu("Edit");
		editMenu.addActionListener(new MyActionListener());
		menuBar.add(editMenu);

		newState = new JMenuItem("New state");
		newState.addActionListener(new MyActionListener());
		editMenu.add(newState);

		newTransition = new JMenuItem("New transition");
		newTransition.addActionListener(new MyActionListener());
		editMenu.add(newTransition);

		setInitial = new JMenuItem("Set initial");
		setInitial.addActionListener(new MyActionListener());
		editMenu.add(setInitial);

		unsetInitial = new JMenuItem("Unset initial");
		unsetInitial.addActionListener(new MyActionListener());
		editMenu.add(unsetInitial);

		setFinal = new JMenuItem("Set final");
		setFinal.addActionListener(new MyActionListener());
		editMenu.add(setFinal);

		unsetFinal = new JMenuItem("Unset final");
		unsetFinal.addActionListener(new MyActionListener());
		editMenu.add(unsetFinal);

		delete = new JMenuItem("Delete");
		delete.addActionListener(new MyActionListener());
		editMenu.add(delete);

		mainFrame.setJMenuBar(menuBar);

		//creating fsaPanel
		panel = new FsaPanel(fsa);
		fsa.addListener(panel);
		
		mainFrame.add(panel);

		controlBar = new JPanel();
		controlBar.setLayout(null);
		controlBar.setBackground(Color.GRAY);

		Font f = new Font("Arial", Font.PLAIN, 30);

		reset = new JButton("Reset");
		reset.setVisible(true);
		reset.setBounds(20, 5, 130, 70);
		reset.setFont(f);
		reset.addActionListener(new MyActionListener());

		step = new JButton("Step");
		step.setVisible(true);
		step.setBounds(170, 5, 130, 70);
		step.setFont(f);
		step.addActionListener(new MyActionListener());

		stepEvent = new JTextField();
		stepEvent.setVisible(true);
		stepEvent.setBounds(320, 5, 600, 70);
		stepEvent.setFont(f);

		f = new Font("Arial", Font.PLAIN, 23);

		recognised = new JLabel("", SwingConstants.CENTER);
		recognised.setVisible(true);
		recognised.setBounds(930, 5, 335, 70);
		recognised.setFont(f);

		controlBar.add(reset);
		controlBar.add(step);
		controlBar.add(stepEvent);
		controlBar.add(recognised);
		controlBar.setVisible(true);
		controlBar.setBounds(0, screenSize.height-180, screenSize.width, 80);

		panel.add(controlBar);

		fsa.reset();
		panel.repaint();
		mainFrame.repaint();

	}

	public static void main(String[] args) {
		FsaEditor editor = new FsaEditor();
		editor.init();
	}

	private class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == openFile){
				open = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Fsa Files", "fsa");
				open.setFileFilter(filter);

				File currDir = new File(System.getProperty("user.dir"));
				open.setCurrentDirectory(currDir);
				int returnVal = open.showOpenDialog(null);

				if(returnVal == JFileChooser.APPROVE_OPTION){
					try{
						openFile();
					}
					catch(Exception ex){
					}
				}
			}
			if(e.getSource() == saveFile){
				save = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Fsa Files", "fsa");
				save.setFileFilter(filter);
				
				File currDir = new File(System.getProperty("user.dir"));
				save.setCurrentDirectory(currDir);
				int returnVal = save.showSaveDialog(null);

				if(returnVal == JFileChooser.APPROVE_OPTION){
					try{
						saveFile();
					}
					catch(Exception ex){

					}
				}
			}
			if(e.getSource() == quit){ System.exit(0); }
			if(e.getSource() == newState){
				String s = (String)JOptionPane.showInputDialog(mainFrame, (Object)"Enter the new states name", "New State", JOptionPane.PLAIN_MESSAGE, null, null, null);

				if(s != null){
					panel.addState(s);
				}
			}
			if(e.getSource() == newTransition){
				String s = (String)JOptionPane.showInputDialog(mainFrame, (Object)"Enter the transitions event name", "New Transition", JOptionPane.PLAIN_MESSAGE, null, null, null);

				if(s != null){
					panel.addTransition(s);
					mainFrame.repaint();
				}
			}
			if(e.getSource() == setInitial){
				Set<StateIcon> stateIcons = panel.getStateIcons();

				for(StateIcon si : stateIcons){
					if(si.isSelected()){
						si.getState().setInitial(true);
					}
				}
			}
			if(e.getSource() == unsetInitial){
				Set<StateIcon> stateIcons = panel.getStateIcons();

				for(StateIcon si : stateIcons){
					if(si.isSelected()){
						si.getState().setInitial(false);
					}
				}
			}
			if(e.getSource() == setFinal){
				Set<StateIcon> stateIcons = panel.getStateIcons();

				for(StateIcon si : stateIcons){
					if(si.isSelected()){
						si.getState().setFinal(true);
					}
				}
			}
			if(e.getSource() == unsetFinal){
				Set<StateIcon> stateIcons = panel.getStateIcons();

				for(StateIcon si : stateIcons){
					if(si.isSelected()){
						si.getState().setFinal(false);
					}
				}
			}
			if(e.getSource() == delete){
				panel.deleteSelected();
			}

			if(e.getSource() == reset){
				fsa.reset();
				recognised.setText("");
				panel.repaint();
			}

			if(e.getSource() == step){
				String input = stepEvent.getText();
				fsa.step(input);
				stepEvent.setText("");

				if(fsa.isRecognised()){
					recognised.setText("This sequence is recognised!");
				}
				else{
					recognised.setText("This sequence is not recognised");
				}
				panel.repaint();
			}
		}

		public void openFile(){
			File input = open.getSelectedFile();
			try{
				Reader r = new FileReader(input);
				fsa = new FsaImpl();
				panel.resetFsa(fsa);
				fileStream.read(r, fsa);
				panel.repaint();
			}
			catch(Exception ex){
				errorDialog("ERROR " + ex.getMessage());
				System.out.println("ERROR");
			}
		}

		public void saveFile(){
			File output = save.getSelectedFile();
			
			String fname = output.getAbsolutePath();

            if(!fname.endsWith(".fsa")){
                output = new File(fname + ".fsa");
            }

			try{
				Writer w = new FileWriter(output);
				fileStream.write(w, fsa);	
			}
			catch(Exception ex){
				errorDialog(ex.getMessage());
				System.out.println("ERROR");
			}
		}

		public void errorDialog(String msg){
			JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
			fsa = new FsaImpl();
			panel.resetFsa(fsa);
		}
	}
}