package org.tasgo.coherence.client.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tasgo.coherence.client.JavaCommandBuilder;

import scala.actors.threadpool.Arrays;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ProgramOutputDisplay extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] arguments) {
		final List<String> args = Arrays.asList(arguments);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgramOutputDisplay frame = new ProgramOutputDisplay(args.get(0), String.join(" ", args.subList(1, args.size() - 1)));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Process start(String program, String title) throws IOException {
		JavaCommandBuilder jcp = new JavaCommandBuilder();
		jcp.classPath.add(JavaCommandBuilder.getCurrentJar().getAbsolutePath());
		jcp.mainClass = ProgramOutputDisplay.class.getName();
		jcp.programArgs.add(title); jcp.programArgs.add(program);
		return jcp.launch();
	}

	/**
	 * Create the frame.
	 */
	public ProgramOutputDisplay(String title, String program) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
	}

}
