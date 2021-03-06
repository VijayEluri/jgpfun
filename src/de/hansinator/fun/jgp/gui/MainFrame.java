package de.hansinator.fun.jgp.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.simulation.FindingFoodScenario;
import de.hansinator.fun.jgp.simulation.Scenario;
import de.hansinator.fun.jgp.util.Settings;

/**
 * MainFrame is some sort of a view for a Simulator instance
 * @author hansinator
 */
public class MainFrame extends JFrame implements WindowListener
{
	static
	{
		Settings.load(new File("default.properties"));
	}

	private JPanel simulationClientView;

	public final JPanel sidePaneRight;
	
	private final Scenario<Genome> scenario;

	public MainFrame(int width, int height, Scenario<Genome> scenario)
	{
		super("BAH! Bonn!!1!11!!!");
		this.scenario = scenario;
		
        //scenario.getEngine().engine.addEvolutionObserver(monitor);

		// create all sub views
		sidePaneRight = new StatisticsHistoryPanel(scenario.getEvoStats().statisticsHistory);
		simulationClientView = scenario.getSimulationView();
		simulationClientView.setPreferredSize(new Dimension(width, height));

		// add the menu bar
		setJMenuBar(createMenuBar());

		// setup and add all stuff to the content pane
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(simulationClientView, BorderLayout.CENTER);
		contentPane.add(sidePaneRight, BorderLayout.LINE_END);
		
		// get ready for action
		addWindowListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		
		// debugging
		System.out.println("Simulation client view size: " + simulationClientView.getWidth() + "x" + simulationClientView.getHeight());
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(new JMenu("File")).add(new JMenuItem("Exit")).addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				scenario.stop();
			}

		});

		JMenu simulationMenu = new JMenu("Simulation");
		// TODO implement proper scenario restart
		/*simulationMenu.add(new JMenuItem("Restart")).addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				scenario.stop();
				startSimulation();
			}

		});*/
		simulationMenu.add(new JMenuItem("Preferences"));
		menuBar.add(simulationMenu);

		return menuBar;
	}

	public void startSimulation()
	{	
		scenario.start();
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		scenario.stop();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{
		Scenario<Genome> scenario = new FindingFoodScenario();
		new MainFrame(Settings.getInt("worldWidth"), Settings.getInt("worldHeight"), scenario);
		scenario.start();
	}
}
