package presentation;
import javax.swing.*;
import domain.HungrySnake;
import domain.HungrySnakeException;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
public class HungrySnakeGUI extends JFrame {
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel[][] grid = new JPanel[SIZE][SIZE];

	private JPanel homeView;
	private JPanel gameView;

	private JMenuItem exitBtnMenu;
	private JButton newGameBtn;
	private JButton exitGameBtn;

	private JMenuItem changeSnakeColorItem;
	private JMenuItem changeFruitsColorItem;
	private JMenuItem changeObstaclesColorItem;
	private JMenuItem changeBoardSizeItem;

	private JMenuItem openItem;
	private JMenuItem saveItem;

	private static final Color DARK_GREEN = new Color(8, 86, 34);
	private static final Color LIGHT_GREEN = new Color(115, 230, 154);
	private static final Color NORMAL_GREEN = new Color(31, 194, 85);
	private static final Color BLUE = new Color(14, 169, 212);
	private static final Color DARK_BLUE = new Color(11, 79, 124);
	private static final Color RED = new Color(255, 0, 0);

	private static final Color GRASS_GREEN = new Color(0, 255, 70);
	private static final Color OBSTACLE_COLOR = Color.BLACK;
	private static final Color FRUIT_COLOR = Color.RED;
	private static final Color SNAKE_BODY_COLOR = new Color(110, 220, 255);
	private static final Color SNAKE_HEAD_COLOR = new Color(110, 150, 255);
	private static final Color SNAKE_DEAD_HEAD_COLOR = new Color(232, 100, 102);
	private static final Color SNAKE_EYE_COLOR = Color.BLACK;

	private static final int SIZE = 25;
	private static final int CELL_W = 28;  // ancho de cada celda
	private static final int CELL_H = 16;  // alto de cada celda

	private static final int DEFAULT_FRUITS = 1;
	private static final int DEFAULT_OBSTACLES = 2;
		
	// Movement
	private Timer timer;
	private char direction = 'E';
	private static final int MOVE_TIME = 150;

	private boolean gameOver = false;

	private HungrySnake game;
	private char[][] board;
	int width = screenSize.width;
	int height = screenSize.height;

	public HungrySnakeGUI() {
		super("HungrySnake");
		width = (int) (width * 0.5);
		height = (int) (height * 0.5);
		prepareView(width, height);
		prepareController();
	}

	private void prepareView(int width, int height) {
		// Main window
		setSize(width, height);
		setLocationRelativeTo(null);
		getContentPane().setBackground(LIGHT_GREEN);
		prepareElements();
		setVisible(true);
	}

	private void prepareElements() {
		prepareViewMenu();
		prepareHomeView();
	}

	private void prepareHomeView() {
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 0, 0);
		homeView = new JPanel(flow);
		homeView.setOpaque(false);

		JPanel centerPanel = new JPanel(new BorderLayout(0, 30));
		centerPanel.setOpaque(false);

		JLabel title = new JLabel("HUNGRY SNAKE", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(525, 100));
		
		JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 0, 15));
		buttonsPanel.setOpaque(false);

		newGameBtn = createHomeButton("Nueva Partida", BLUE, Color.BLACK);
		exitGameBtn = createHomeButton("Salir", RED, Color.WHITE);

		buttonsPanel.add(newGameBtn);
		buttonsPanel.add(exitGameBtn);

		centerPanel.add(title, BorderLayout.NORTH);
		centerPanel.add(buttonsPanel, BorderLayout.CENTER);

		homeView.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int free = homeView.getHeight() - centerPanel.getPreferredSize().height;
				flow.setVgap(Math.max(0, free / 2));
				homeView.revalidate();
			}
		});

		homeView.add(centerPanel);
		add(homeView, BorderLayout.CENTER);
		showView(homeView);
	}

	private JButton createHomeButton(
		String title,
		Color color,
		Color textColor
	) {
		JButton newBtn = new JButton(title) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
				Color bgColor = getBackground();
				if (getModel().isPressed()) {
					bgColor = bgColor.darker();
				} else if (getModel().isRollover()) {
					bgColor = bgColor.brighter();
				}
	
				g2.setColor(bgColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
				g2.dispose();
				super.paintComponent(g); // ahora solo dibuja el texto
			}
		};
		newBtn.setBackground(color);
		newBtn.setBorderPainted(false);
		newBtn.setFocusPainted(false);
		newBtn.setContentAreaFilled(false);
		newBtn.setForeground(Color.WHITE);
		newBtn.setPreferredSize(new Dimension(430, 70));
		newBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		newBtn.setFont(new Font("Arial", Font.BOLD, 28));
		return newBtn;
	}

	private JLabel createGameLabel(
		String title,
		Color backgroundColor,
		Color textColor
	) {
		JLabel newLabel = new JLabel(title, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NORMAL_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };

		newLabel.setOpaque(false);
		newLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
		newLabel.setBackground(backgroundColor);
		newLabel.setForeground(textColor);
		newLabel.setFont(new Font("Arial", Font.BOLD, 16));

		return newLabel;
	}

	private void prepareViewMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Archivo");
		menu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

		// "Archivo" JMenu items
		JMenuItem newItem = new JMenuItem("Nuevo");
		newItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		openItem = new JMenuItem("Abrir");
		openItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		saveItem = new JMenuItem("Salvar");
		saveItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		exitBtnMenu = new JMenuItem("Salir");
		exitBtnMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		menu.add(newItem);
		menu.add(openItem);
		menu.add(saveItem);
		menu.addSeparator();
		menu.add(exitBtnMenu);

		// "Configuración" JMenu items
		JMenu customizeMenu = new JMenu("Configuración");
		customizeMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

		changeSnakeColorItem = new JMenuItem("Cambiar color de Serpiente");
		changeSnakeColorItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		changeFruitsColorItem = new JMenuItem("Cambiar color de las Frutas");
		changeFruitsColorItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		
		changeObstaclesColorItem = new JMenuItem("Cambiar color de los Obstáculos");
		changeObstaclesColorItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		changeBoardSizeItem = new JMenuItem("Cambiar tamaño tablero");
		changeBoardSizeItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		customizeMenu.add(changeSnakeColorItem);
		customizeMenu.add(changeFruitsColorItem);
		customizeMenu.add(changeObstaclesColorItem);
		customizeMenu.addSeparator();
		customizeMenu.add(changeBoardSizeItem);

		bar.add(menu);
		bar.add(customizeMenu);
		setJMenuBar(bar);
	}

	private void startGame() {
		if(timer!= null) {
			timer.stop();
		}
		direction = 'E';

		timer = new Timer(MOVE_TIME, e -> {
			boolean moved;
			try {
				moved = game.move(direction);
			} catch (HungrySnakeException ex) {
				moved = false;
				JOptionPane.showMessageDialog(this, ex.getMessage());
			}

			if(!moved) {
				gameOver = true;
				refresh();
				timer.stop();
				JOptionPane.showMessageDialog(this, "¡Perdiste! Tamaño final: " + game.getSize());
				// showView(homeView);
				return;
			}
			refresh();
		});

		timer.start();
	}

	private void prepareControllerBoard() {
		gameView.setFocusable(true);
		gameView.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP, KeyEvent.VK_W    -> changeDirection('N');
					case KeyEvent.VK_DOWN, KeyEvent.VK_S  -> changeDirection('S');
					case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> changeDirection('E');
					case KeyEvent.VK_LEFT, KeyEvent.VK_A  -> changeDirection('W');
				}
			}
		});
		SwingUtilities.invokeLater(() -> gameView.requestFocusInWindow());
	}

	private void changeDirection(char newDir) {
		boolean opposite = (direction == 'N' && newDir == 'S') || (direction == 'S' && newDir == 'N')
		|| (direction == 'E' && newDir == 'W') || (direction == 'W' && newDir == 'E');
		if (!opposite || game.getSize() == 1) {
			direction = newDir;
		}
	}

	private void newGame(int rows, int cols, int fruits, int obstacles) {
		try {
			game = new HungrySnake(SIZE, SIZE, DEFAULT_FRUITS, DEFAULT_OBSTACLES);
		} catch (HungrySnakeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		prepareViewBoard(DEFAULT_FRUITS, DEFAULT_OBSTACLES);
		refresh();
		prepareControllerBoard();
		startGame();
	}

	private void prepareViewBoard(int fruits, int obstacles) {
		JPanel scoreBoard = new JPanel(new GridLayout(1, 3, 16, 0));
		scoreBoard.setOpaque(false);

		JLabel sizeLabel = createGameLabel("Tamaño: " + game.getSize() , NORMAL_GREEN, DARK_BLUE);
		JLabel fruitsLabel = createGameLabel("Frutas: " + fruits, NORMAL_GREEN, RED);
		JLabel obstaclesLabel = createGameLabel("Obstáculos: " + obstacles + " %", NORMAL_GREEN, Color.BLACK);

		scoreBoard.add(sizeLabel);
		scoreBoard.add(fruitsLabel);
		scoreBoard.add(obstaclesLabel);

		JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE, 0, 0));
		boardPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.BLACK));
		boardPanel.setOpaque(false);

		// La cantidad de celdas y el tamaño por las lineas que hay entre ellas y el borde
		int boardPanelW = CELL_W * SIZE + 1; // +1 por el borde izquierdo del tablero
		int boardPanelH = CELL_H * SIZE + 1; // +1 por el borde superior
		boardPanel.setPreferredSize(new Dimension(boardPanelW, boardPanelH));
		boardPanel.setPreferredSize(new Dimension(boardPanelW, boardPanelH));

		for(int r = 0; r < SIZE; r++) {
			for(int c = 0; c < SIZE; c++) {
				final int row = r, col = c;
				grid[r][c] = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						char type = (board != null) ? board[row][col] : 'g';
						drawCell(g, this, type);
					}
				};
				grid[r][c].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
				boardPanel.add(grid[r][c]);
			}
		}
		
		JPanel column = new JPanel(new BorderLayout(0, 10));
		column.setOpaque(false);
		column.add(scoreBoard, BorderLayout.NORTH);
		column.add(boardPanel, BorderLayout.CENTER);

		gameView = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		gameView.setOpaque(false);
		gameView.add(column);

		showView(gameView);
        refresh();
	}

	private void drawCell(Graphics g, JPanel cell, char type) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Se le resta el borde del grid
		int cw = cell.getWidth() - 1;
		int ch = cell.getHeight() - 1;

		// Fondo de pasto en todas las celdas
		g2.setColor(GRASS_GREEN);
		g2.fillRect(0, 0, cw, ch);

		switch (type) {
			case 'o' -> {
				g2.setColor(OBSTACLE_COLOR);
				g2.fillRect(0, 0, cw, ch);
			}
			case 'f' -> {
				g2.setColor(FRUIT_COLOR);
				g2.fillOval(2, 2, cw - 4, ch - 4);
			}
			case 'b' -> {
				g2.setColor(SNAKE_BODY_COLOR);
				g2.fillRect(0, 0, cw, ch);
			}
			case 'h' -> {
				g2.setColor(gameOver ? SNAKE_DEAD_HEAD_COLOR : SNAKE_HEAD_COLOR);
				g2.fillRect(0, 0, cw, ch);
				g2.setColor(SNAKE_EYE_COLOR);
				int eye = Math.max(3, Math.min(cw, ch) / 4);
				switch (direction) {
					case 'E' -> { g2.fillOval(cw - eye - 3, 2, eye, eye); g2.fillOval(cw - eye - 3, ch - eye - 2, eye, eye); }
					case 'W' -> { g2.fillOval(3, 2, eye, eye);            g2.fillOval(3, ch - eye - 2, eye, eye); }
					case 'N' -> { g2.fillOval(3, 2, eye, eye);            g2.fillOval(cw - eye - 3, 2, eye, eye); }
					case 'S' -> { g2.fillOval(3, ch - eye - 2, eye, eye); g2.fillOval(cw - eye - 3, ch - eye - 2, eye, eye); }
				}
			}
		}
		g2.dispose();
	}

	private void changeConfig() {
		timer.stop();
		JTextField rowsField = new JTextField("25", 5);
		JTextField colsField = new JTextField("25", 5);
		JTextField fruitsField = new JTextField("1", 5);
		JTextField obstaclesField = new JTextField("10", 5);

		JPanel form = new JPanel(new GridLayout(4, 2, 10, 8));
		form.add(new JLabel("Filas:"));
		form.add(rowsField);
		form.add(new JLabel("Columnas:"));
		form.add(colsField);
		form.add(new JLabel("Frutas:"));
		form.add(fruitsField);
		form.add(new JLabel("Obstáculos (%):"));
		form.add(obstaclesField);

		while (true) {
			int option = JOptionPane.showConfirmDialog(
				this, form, "Configurar partida",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
			);
			if (option != JOptionPane.OK_OPTION) {
				return;
			}

			String rowsStr = rowsField.getText().trim();
			String colsStr = colsField.getText().trim();
			String fruitsStr = fruitsField.getText().trim();
			String obstStr = obstaclesField.getText().trim();

			if (rowsStr.isEmpty() || colsStr.isEmpty() || fruitsStr.isEmpty() || obstStr.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
				continue;
			}

			String[] values = {rowsStr, colsStr, fruitsStr, obstStr};
			boolean allNumbers = true;

			for (String value : values) {
				if (value.length() > 3) {
					allNumbers = false;
				}
				for (int i = 0; i < value.length(); i++) {
					char ch = value.charAt(i);
					if (ch < '0' || ch > '9') {
						allNumbers = false;
					}
				}
			}

			if (!allNumbers) {
				JOptionPane.showMessageDialog(this, "Todos los valores deben ser números enteros positivos.");
				continue;
			}

			int rows = Integer.parseInt(rowsStr);
			int cols = Integer.parseInt(colsStr);
			int fruits = Integer.parseInt(fruitsStr);
			int obstacles = Integer.parseInt(obstStr);

			if (rows < 5 || rows > 50 || cols < 5 || cols > 50) {
				JOptionPane.showMessageDialog(this, "Las filas y columnas deben estar entre 5 y 50.");
				continue;
			}

			if (fruits < 1 || fruits > rows * cols - 1) {
				JOptionPane.showMessageDialog(this, "Las frutas deben estar entre 1 y " + (rows * cols - 1) + ".");
				continue;
			}

			if (obstacles > 50) {
				JOptionPane.showMessageDialog(this, "El porcentaje de obstáculos debe estar entre 0 y 50.");
				continue;
			}
			newGame(rows, cols, fruits, obstacles);
		}
	}

	private void refresh() {
		board = game.board();
        for(int r = 0; r < SIZE; r++) {
			for(int c = 0; c < SIZE; c++) {
				grid[r][c].repaint();
			}
		}
    }

	private void prepareControllerMenu(JMenuItem exitItem) {
		exitItem.addActionListener(e -> exit());
	}

	private void prepareControllerMenu() {
		openItem.addActionListener(e -> open());
		saveItem.addActionListener(e -> save());
		changeSnakeColorItem.addActionListener(e -> changeSnakeColor());
		changeFruitsColorItem.addActionListener(e -> changeFruitsColor());
		changeObstaclesColorItem.addActionListener(e -> changeObstaclesColor());
		changeBoardSizeItem.addActionListener(e -> changeConfig());
	}

	private void prepareController() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		prepareControllerMenu(exitBtnMenu);
		prepareControllerMenu();
		newGameBtn.addActionListener(e -> newGame(SIZE, SIZE, DEFAULT_FRUITS, DEFAULT_OBSTACLES));
		exitGameBtn.addActionListener(e -> exit());
	}

	private void changeSnakeColor() {
		System.out.println("Nuevo color de la serpiente: " + JColorChooser.showDialog(this, "Nuevo color para la serpiente: ", BLUE));
	}

	private void changeFruitsColor() {
		System.out.println("Nuevo color de frutas: " + JColorChooser.showDialog(this, "Nuevo color para las frutas: ", RED));
	}

	private void changeObstaclesColor() {
		System.out.println("Nuevo color de obstáculos: " + JColorChooser.showDialog(this, "Nuevo color para los obstáculos: ", Color.BLACK));
	}

	private void exit() {
		int option = JOptionPane.showConfirmDialog(
			this,
			"¿Estás seguro de que deseas salir?",
			"Confirmar",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE
		);
		if (option == JOptionPane.YES_OPTION) {
			dispose();
			System.exit(0);
		}
	}

	private void save() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			JOptionPane.showMessageDialog(chooser, file);
		}
	}

	private void open() {
		JFileChooser chooser = new JFileChooser();
		int option = chooser.showOpenDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			JOptionPane.showMessageDialog(chooser, file);
		}
	}

	private void showView(JPanel view) {
		getContentPane().removeAll();
		add(view, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	public static void main(String[] args) {
		HungrySnakeGUI ui = new HungrySnakeGUI();
		ui.setVisible(true);
	}
}