import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Простое приложение Swing для демонстрации алгоритма поиска пути A*.  
 * Пользователю предоставляется карта, содержащая начальное и конечное 
 * местоположение.  Пользователь может нарисовать или устранить препятствия 
 * на карте, а затем нажать кнопку, чтобы вычислить путь от начала до конца, 
 * используя алгоритм поиска пути A *.  Если путь найден, он отображается 
 * зеленым цветом.
 **/
public class AStarApp {

    /** The number of grid cells in the X direction. **/
    private int width;
    
    /** The number of grid cells in the Y direction. **/
    private int height;
    
    /** The location where the path starts from. **/
    private Location startLoc;
    
    /** The location where the path is supposed to finish. **/
    private Location finishLoc;
    
    /**
     * This is a 2D array of UI components that provide display and manipulation
     * of the cells in the map.
     ***/
    private JMapCell[][] mapCells;

    
    /**
     * Этот внутренний класс обрабатывает события мыши в основной сетке 
     * ячеек карты, изменяя ячейки на основе состояния кнопки мыши и 
     * выполненного первоначального редактирования.
     **/
    private class MapCellHandler implements MouseListener
    {
        /**
         * Это значение будет истинным, если была нажата кнопка мыши и 
         * в данный момент мы находимся в процессе операции модификации.
         **/
        private boolean modifying;
        
        /**
         * Это значение определяет, делаем ли мы ячейки проходимыми или 
         * непроходимыми.  Что это такое, зависит от исходного состояния 
         * ячейки, в которой была запущена операция.
         **/
        private boolean makePassable;
        
        /** Initiates the modification operation. **/
        public void mousePressed(MouseEvent e)
        {
            modifying = true;
            
            JMapCell cell = (JMapCell) e.getSource();
            
            /**
             * Если текущая ячейка проходима, то мы делаем их непроходимыми; 
             * если она непроходима, то мы делаем их проходимыми.
             **/
            
            makePassable = !cell.isPassable();
            
            cell.setPassable(makePassable);
        }

        /** Ends the modification operation. **/
        public void mouseReleased(MouseEvent e)
        {
            modifying = false;
        }
        
        /**
         * If the mouse has been pressed, this continues the modification
         * operation into the new cell.
         **/
        public void mouseEntered(MouseEvent e)
        {
            if (modifying)
            {
                JMapCell cell = (JMapCell) e.getSource();
                cell.setPassable(makePassable);
            }
        }

        /** Not needed for this handler. **/
        public void mouseExited(MouseEvent e)
        {
            // This one we ignore.
        }
        
        /** Not needed for this handler. **/
        public void mouseClicked(MouseEvent e)
        {
            // And this one too.
        }
    }
    
    
    /**
     * Creates a new instance of AStarApp with the specified map width and
     * height.
     **/
    public AStarApp(int w, int h) {
        if (w <= 0)
            throw new IllegalArgumentException("w must be > 0; got " + w);
        
        if (h <= 0)
            throw new IllegalArgumentException("h must be > 0; got " + h);
        
        width = w;
        height = h;
        
        startLoc = new Location(2, h / 2);
        finishLoc = new Location(w - 3, h / 2);
    }
    
    
    /**
     * Simple helper method to set up the Swing user interface.  This is called
     * from the Swing event-handler thread to be threadsafe.
     **/
    private void initGUI()
    {
        JFrame frame = new JFrame("Pathfinder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();

        contentPane.setLayout(new BorderLayout());

        // Use GridBagLayout because it actually respects the preferred size
        // specified by the components it lays out.
        
        GridBagLayout gbLayout = new GridBagLayout();
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.insets.set(0, 0, 1, 1);

        JPanel mapPanel = new JPanel(gbLayout);
        mapPanel.setBackground(Color.GRAY);
        
        mapCells = new JMapCell[width][height];
        
        MapCellHandler cellHandler = new MapCellHandler();
        
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                mapCells[x][y] = new JMapCell();

                gbConstraints.gridx = x;
                gbConstraints.gridy = y;

                gbLayout.setConstraints(mapCells[x][y], gbConstraints);
                
                mapPanel.add(mapCells[x][y]);
                mapCells[x][y].addMouseListener(cellHandler);
            }
        }
        
        contentPane.add(mapPanel, BorderLayout.CENTER);
        
        JButton findPathButton = new JButton("Find Path");
        findPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { findAndShowPath(); }
        });
        
        contentPane.add(findPathButton, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);

        mapCells[startLoc.xCoord][startLoc.yCoord].setEndpoint(true);
        mapCells[finishLoc.xCoord][finishLoc.yCoord].setEndpoint(true);
    }

    
    /** Kicks off the application.  Called from the {@link #main} method. **/
    private void start()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { initGUI(); }
        });
    }
    

    /**
     * Этот вспомогательный метод пытается вычислить путь, используя текущее 
     * состояние карты.  Реализация довольно медленная; создается новый 
     * объект {@link Map2D} и инициализируется из текущего состояния 
     * приложения.  Затем вызывается навигатор A*, и если путь найден, 
     * дисплей обновляется, чтобы показать путь, который был найден.  
     * (Лучшим решением было бы использовать шаблон проектирования 
     * контроллера представления модели.)
     **/
    private void findAndShowPath()
    {
        // Create a Map2D object containing the current state of the user input.

        Map2D map = new Map2D(width, height);
        map.setStart(startLoc);
        map.setFinish(finishLoc);
        
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                mapCells[x][y].setPath(false);

                if (mapCells[x][y].isPassable())
                    map.setCellValue(x, y, 0);
                else
                    map.setCellValue(x, y, Integer.MAX_VALUE);
            }
        }
        
        // Try to compute a path.  If one can be computed, mark all cells in the
        // path.
        
        Waypoint wp = AStarPathfinder.computePath(map);
        
        while (wp != null)
        {
            Location loc = wp.getLocation();
            mapCells[loc.xCoord][loc.yCoord].setPath(true);
            
            wp = wp.getPrevious();
        }
    }
    
    
    /**
     * Entry-point for the application.  No command-line arguments are
     * recognized at this time.
     **/
    public static void main(String[] args) {
        AStarApp app = new AStarApp(40, 30);
        app.start();
    }   
}