import java.util.HashMap;
//import java.util.Map;
/**
 * Этот класс хранит базовое состояние, необходимое алгоритму A* для 
 * вычисления пути по карте.  Это состояние включает в себя коллекцию 
 * "открытых путевых точек" и другую коллекцию "закрытых путевых точек".  
 * Кроме того, этот класс предоставляет основные операции, необходимые 
 * алгоритму поиска пути A* для выполнения его обработки.
 **/
public class AStarState
{
    /** Это ссылка на карту, по которой перемещается алгоритм A*. **/
    private Map2D map;

    private HashMap<Location, Waypoint> openWaypoints = new HashMap<Location, Waypoint>(); //ключ-значение
    private HashMap<Location, Waypoint> closeWaypoints = new HashMap<Location, Waypoint>();


     /** Инициализировать новый объект состояния для использования алгоритмом поиска пути A*. **/
    public AStarState(Map2D map)
    {
        if (map == null)
            throw new NullPointerException("map cannot be null");

        this.map = map;
    }

    /** Returns the map that the A* pathfinder is navigating. **/
    public Map2D getMap()
    {
        return map;
    }


    /**
     * Этот метод проверяет все вершины в наборе открытых вершин, 
     * и после этого возваращет ссылку на вершину с наименьшей
     * общей стоимостью. Если открытых путевых точек нет, этот метод возвращает null.
     **/
    public Waypoint getMinOpenWaypoint() {
        Waypoint minWaypoint = null;
        Waypoint temp = null;
        float minCost = Float.MAX_VALUE; // Лучшая цена по умолчанию устанавливается максимальное значение
        //Находим минимальную цену пути, проходим по открытым точкам
        for (int i = 0; i < openWaypoints.size(); i++) {
            temp = (Waypoint) openWaypoints.values().toArray()[i];
            if (temp.getTotalCost() < minCost) {
                minCost = temp.getTotalCost();
                minWaypoint = temp;
            }
        }
        return minWaypoint;
    }

    /**
     * Этот метод добавляет указанную вершину только в том случае, 
     * если существующая вершина хуже новой.
     * Если в наборе "открытых вершин" нет вершины для данной локации, 
     * то необходимо просто добавить новую вершину.
     * Если в наборе "открытых вершин" уже есть вершина для этой 
     * локации, новая вершина добавляется только, если стоимость пути 
     * до новой вершины меньше стоимости пути до текущей.
     * Если путь через новую вершину короче, чем путь через текущую, 
     * замените текущую вершину на новую.
     **/
    public boolean addOpenWaypoint(Waypoint newWP) {
        //Поиск точки в открытых точках.
        Waypoint newPoint = openWaypoints.get(newWP.getLocation());
        //Если не найдена, то добавляем, или, если стоимость новой вершины меньше, то заменяем ее в списке открытых вершин
        if (newPoint == null || newPoint.getPreviousCost() > newWP.getPreviousCost()) {
            openWaypoints.put(newWP.getLocation(),newWP);
            return true;
        }
        return false;
    }

    /** Возвращает текущее количество открытых путевых точек. **/
    public int numOpenWaypoints() {
        return openWaypoints.size();
    }

    /**
     * Этот метод перемещает вершину из набора "открытых вершин" 
     * в набор "закрытых вершин". Принимает местоположение вершины.
     **/
    public void closeWaypoint(Location loc) {
        //Если не можем пройти через вершин, добавляем её в список закрытых вершин
        //Получаем вершину из открытых по хэшу локейшн
        Waypoint isPoint = openWaypoints.get(loc);
        //Если вершины нет в открытых, то ничего не делаем
        if (isPoint == null) return;
        //Удаляем вершину из открытых
        openWaypoints.remove(loc);
        //Добавляем вершину в закрытые
        closeWaypoints.put(loc,isPoint);
    }

    /**
     * Возвращает значение true, если указанное местоположение 
     * встречается в наборе закрытых вершин, иначе false .
     **/
    public boolean isLocationClosed(Location loc) {
        return closeWaypoints.get(loc) != null;
    }

}
