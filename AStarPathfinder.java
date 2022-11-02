//import java.util.HashMap;
//import java.util.HashSet;


/**
 * Этот класс содержит реализацию алгоритма поиска пути A*.  
 * Алгоритм реализован как статический метод, поскольку 
 * алгоритму поиска пути действительно не нужно поддерживать какое-либо 
 * состояние между вызовами алгоритма.
 */
public class AStarPathfinder
{
    /**
     * Эта константа содержит максимальный предел отсечения для стоимости 
     * путей.  Если конкретная путевая точка превышает этот лимит затрат, 
     * путевая точка отбрасывается.
     **/
    public static final float COST_LIMIT = 1e6f;

    
    /**
     * Пытается вычислить путь, который перемещается между начальным и 
     * конечным местоположениями указанной карты.  Если путь может быть 
     * найден, возвращается путевая точка последнего шага пути; эта путевая 
     * точка может быть использована для обратного перехода к начальной 
     * точке.  Если путь не может быть найден, возвращается <код>null</code>.
     **/
    public static Waypoint computePath(Map2D map)
    {
        // Variables necessary for the A* search.
        AStarState state = new AStarState(map);
        Location finishLoc = map.getFinish();

        // Установите начальную путевую точку, чтобы начать поиск A*.
        Waypoint start = new Waypoint(map.getStart(), null);
        start.setCosts(0, estimateTravelCost(start.getLocation(), finishLoc));
        state.addOpenWaypoint(start);

        Waypoint finalWaypoint = null;
        boolean foundPath = false;
        
        while (!foundPath && state.numOpenWaypoints() > 0)
        {
            // Find the "best" (i.e. lowest-cost) waypoint so far.
            Waypoint best = state.getMinOpenWaypoint();
            
            // If the best location is the finish location then we're done!
            if (best.getLocation().equals(finishLoc))
            {
                finalWaypoint = best;
                foundPath = true;
            }
            
            /** 
             * Добавьте / обновите всех соседей текущего наилучшего 
             * местоположения. Это эквивалентно попытке выполнить все 
             * "следующие шаги" из этого местоположения.
             **/
            takeNextStep(best, state);
            
            /** 
             * Наконец, переместите это местоположение из списка "открыто" 
             * в список "закрыто".
             **/
            state.closeWaypoint(best.getLocation());
        }
        
        return finalWaypoint;
    }

    /**
     * Этот статический вспомогательный метод принимает путевую точку и 
     * генерирует все допустимые "следующие шаги" из этой путевой точки.  
     * Новые путевые точки добавляются в коллекцию "открытые путевые точки" 
     * переданного объекта A* state.
     **/
    private static void takeNextStep(Waypoint currWP, AStarState state)
    {
        Location loc = currWP.getLocation();
        Map2D map = state.getMap();
        
        for (int y = loc.yCoord - 1; y <= loc.yCoord + 1; y++)
        {
            for (int x = loc.xCoord - 1; x <= loc.xCoord + 1; x++)
            {
                Location nextLoc = new Location(x, y);
                
                // If "next location" is outside the map, skip it.
                if (!map.contains(nextLoc))
                    continue;
                
                // If "next location" is this location, skip it.
                if (nextLoc == loc)
                    continue;
                
                // If this location happens to already be in the "closed" set
                // then continue on with the next location.
                if (state.isLocationClosed(nextLoc))
                    continue;

                // Make a waypoint for this "next location."
                
                Waypoint nextWP = new Waypoint(nextLoc, currWP);
                
                /**
                 * Хорошо, мы обманываем и используем смету затрат для 
                 * вычисления фактической стоимости из предыдущей ячейки.  
                 * Затем мы добавляем стоимость из ячейки карты, на которую 
                 * мы наступаем, чтобы включить барьеры и т.д.
                 **/

                float prevCost = currWP.getPreviousCost() +
                    estimateTravelCost(currWP.getLocation(),
                                       nextWP.getLocation());

                prevCost += map.getCellValue(nextLoc);
                
                // Skip this "next location" if it is too costly.
                if (prevCost >= COST_LIMIT)
                    continue;
                
                nextWP.setCosts(prevCost,
                    estimateTravelCost(nextLoc, map.getFinish()));

                /**
                 * Добавьте путевую точку в набор открытых путевых точек.  
                 * Если для этого местоположения уже существует путевая точка, 
                 * новая путевая точка заменяет старую путевую точку только 
                 * в том случае, если она дешевле старой.
                 **/
                state.addOpenWaypoint(nextWP);
            }
        }
    }
    
    /**
     * Оценивает стоимость проезда между двумя указанными точками. 
     * Вычисленная фактическая стоимость - это просто расстояние по прямой 
     * между двумя точками.
     **/
    private static float estimateTravelCost(Location currLoc, Location destLoc)
    {
        int dx = destLoc.xCoord - currLoc.xCoord;
        int dy = destLoc.yCoord - currLoc.yCoord;
        
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}