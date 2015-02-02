package de.hansinator.fun.jgp.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.hansinator.fun.jgp.simulation.Simulator;

/**
 * 
 * @author hansinator
 */
public class MainFrame extends JFrame implements WindowListener
{

	private MainView mainView;

	private final JScrollPane centerPane;

	public final JPanel sidePaneLeft, sidePaneRight;

	public final BottomPanel bottomPane;

	private final Simulator simulator;

	public MainFrame(int width, int height, Simulator simulator)
	{
		super("BAH! Bonn!!1!11!!!");
		this.simulator = simulator;

		// create all direct clients
		centerPane = new JScrollPane();
		sidePaneLeft = new JPanel();
		sidePaneRight = new StatisticsHistoryPanel(simulator.statisticsHistory);
		bottomPane = new BottomPanel(simulator);

		// set default center pane size properties
		centerPane.setPreferredSize(new Dimension(800, 600));

		// init application specific content
		initClientViews(width, height);

		// add the menu bar
		setJMenuBar(createMenuBar());

		// setup and add all stuff to the content pane
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(centerPane, BorderLayout.CENTER);
		contentPane.add(sidePaneLeft, BorderLayout.LINE_START);
		contentPane.add(sidePaneRight, BorderLayout.LINE_END);
		contentPane.add(bottomPane, BorderLayout.PAGE_END);

		// get ready for action
		pack();
		setVisible(true);
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(new JMenu("File")).add(new JMenuItem("Exit")).addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				stopSimulation();
			}

		});

		JMenu simulationMenu = new JMenu("Simulation");
		simulationMenu.add(new JMenuItem("Reset")).addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				simulator.restart(mainView, bottomPane.infoPanel);
			}

		});
		simulationMenu.add(new JMenuItem("Preferences"));
		menuBar.add(simulationMenu);

		return menuBar;
	}

	private void initClientViews(int width, int height)
	{
		// to be put elsewhere
		mainView = new MainView(simulator.getSimulation().world);
		mainView.setPreferredSize(new Dimension(width, height));

		setCenterPaneView(mainView);
	}

	public void setCenterPaneView(Component view)
	{
		centerPane.setViewportView(view);
	}

	public void startSimulation()
	{
		System.out.println("MainView size: " + mainView.getWidth() + "x" + mainView.getHeight());
		// TODO: to be put somewhere else
		addWindowListener(this);
		simulator.start(mainView, bottomPane.infoPanel);
	}

	public void stopSimulation()
	{
		simulator.stop();
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		stopSimulation();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		stopSimulation();
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

}