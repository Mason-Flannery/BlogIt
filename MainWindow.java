import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.awt.event.ActionEvent;

import org.apache.commons.io.FileUtils;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	private DateTimeFormatter urlGen = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy: HH:mm:ss");

	//TODO
	private static final String path = "C:/Program Files/Git/bin/bash.exe";

	private String newUrlPage = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		frame.setBounds(500, 500, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("Name");
		lblNewLabel.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		frame.getContentPane().add(textField, gbc_textField);
		textField.setColumns(3);

		JLabel lblNewLabel_1 = new JLabel("Title");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 10));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		frame.getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		frame.getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(3);

		JTextArea textArea = new JTextArea();
		textArea.setRows(20);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(0, 0, 5, 0);
		gbc_textArea.gridheight = 9;
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 4;
		frame.getContentPane().add(textArea, gbc_textArea);

		JButton btnNewButton = new JButton("Submit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				buildHTML(); //builds blog page 

				updateListing(); //creates a formatted href of the new blog listing on the site

				runBash(); //stage commits and push to the site
			}

			private void runBash() {
				//runs bash script
				ProcessBuilder pb = new ProcessBuilder();
				boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

				if(isWindows) {
					pb.command(path, System.getProperty("user.dir") + "\\src\\blogIt.sh");
				}
				else {
					pb.command("sh", "-c", System.getProperty("user.dir") + "/src/blogIt.sh");
				}

				try {
					//runs script; checks for updates, stages & pushes commits
					Process process = pb.start();

					//------------

					StringBuilder output = new StringBuilder();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));

					String line;
					while ((line = reader.readLine()) != null) {
						output.append(line + "\n");
					}
					int exitVal = process.waitFor();
					if (exitVal == 0) {
						System.out.println(" --- Command run successfully");
						System.out.println(" --- Output=" + output);
						JOptionPane.showMessageDialog(frame, "Congrats! "
								+ "Your blog post has been successfully uploaded to the site!");

					} else {
						System.out.println(" --- Command run unsuccessfully");

						JOptionPane.showMessageDialog(frame, "There was an issue "
								+ "uploading the files. They should be ready to stage in your repo");

						System.out.println(exitVal);
					}

					//--------

				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}

			private void updateListing() {

				//local is current, moves

				//replace <row> $list </row> with $list + <href="link-to-blogPost> $Title (get from field) </href>

				//$list

				// $list = <row> $list, hidden </row> + <row> <href="link-to-blogPost> $title (get from field) </href> </row>

				//replaces to

				//<row> $list </row>
				//<row> link </row>
				String htmlString = "";

				//TODO, path to the blog page of the site
				//String toBlog = "\\Users\\Bill Gates\\Desktop\\Costellae\\CostellaeWebsite\\pages\\blog.html";

				File file = new File("\\Users\\Bill Gates\\Desktop\\Costellae\\CostellaeWebsite\\pages\\blog.html");

				try {
					//reads in blog file
					htmlString = FileUtils.readFileToString(file, "UTF-8");

				} catch (IOException e) {
					e.printStackTrace();
				}

				String title = textField_1.getText();

				String update = "$list \n <br> <row> <href=blog_pages/" + newUrlPage + "> " + title + " </href> </row>";

				htmlString = htmlString.replace("$list", update);

				File newHtmlFile = new File("blog" + ".html");

				try {
					//writes to the new file
					FileUtils.writeStringToFile(newHtmlFile, htmlString, "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					//moves new file to the target directory
					//FileUtils.remo

					//copies file to current directory - it doesn't appear to be saved in the local directory - copy <- move
					FileUtils.copyFileToDirectory(newHtmlFile, new File("\\Users\\Bill Gates\\Desktop\\Costellae\\CostellaeWebsite\\pages\\"), false);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return;
			}

			private void buildHTML() {
				if(textField.getText().isBlank() ||
						textField_1.getText().isBlank() ||
						textArea.getText().isBlank()) return; //input check

				URL url = getClass().getResource("template.html");

				//creates a new template file, copies template in from URL
				File htmlTemplateFile = null;
				try {
					htmlTemplateFile = new File(url.toURI());
				} catch (URISyntaxException e2) {
					e2.printStackTrace();
					System.out.println("issue with the url to uri");
				}

				//string to read into body; reads into body, utf-8 encoding
				String htmlString = "";

				try {
					htmlString = FileUtils.readFileToString(htmlTemplateFile, "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();

					JOptionPane.showMessageDialog(frame, "There was an issue encountered parsing the template file, your change did NOT happen");
					return;
				}

				//pulls data from the window
				String author = textField.getText();
				String title = textField_1.getText();
				String body = textArea.getText();

				//generates date & time, formats
				LocalDateTime now = LocalDateTime.now(); 
				String newName = urlGen.format(now);
				String date = formatter.format(now);

				newUrlPage = newName + ".html"; //stores in global value

				//replaces $tags in document body
				htmlString = htmlString.replace("$author", author);
				htmlString = htmlString.replace("$date", date);
				htmlString = htmlString.replace("$title", title);
				htmlString = htmlString.replace("$body", body);

				File newHtmlFile = new File(newName + ".html"); //concatenates date-time.html
				try {
					FileUtils.writeStringToFile(newHtmlFile, htmlString, "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				//move created html file to the website repo
				try {
					//TODO
					FileUtils.moveFileToDirectory(newHtmlFile, new File("\\Users\\Bill Gates\\Desktop\\Costellae\\CostellaeWebsite\\pages\\blog_pages\\"), false);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("Destination could not be found");
					JOptionPane.showMessageDialog(frame, "There was an issue encountered moving to the repo, your change did NOT go live."
							+ "It can be found in the eclipse workspace.");
				}
			}	
		});

		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.SOUTH;
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 14;
		frame.getContentPane().add(btnNewButton, gbc_btnNewButton);

	}

}
