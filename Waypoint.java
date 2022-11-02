/**
 * Этот класс представляет собой один шаг в пути, сгенерированном алгоритмом поиска пути A*.  
 * Путевые точки состоят из местоположения, предыдущей путевой точки на пути 
 * и некоторых значений затрат, используемых для определения наилучшего пути.
 **/
public class Waypoint
{
    /** The location of this waypoint. **/
    Location loc;

    /**
     * Предыдущая путевая точка на этом пути 
     * или <code>null</code>, если это корень поиска A*.
     **/
    Waypoint prevWaypoint;

    /**
     * В этом поле хранится общая предыдущая стоимость проезда от начального 
     * местоположения до этой путевой точки по цепочке путевых точек. Это фактическая 
     * стоимость следования по пути; она не включает никаких оценок.
     **/
    private float prevCost;

    /**
     * В этом поле хранится оценка оставшейся стоимости проезда
     * от этой путевой точки до конечного пункта назначения.
     **/
    private float remainingCost;


    /**
     * Создайте новую путевую точку для указанного местоположения. Необязательно 
     * может быть указана предыдущая путевая точка, или ссылка может быть <code>null</code>, 
     * чтобы указать, что путевая точка является началом пути.
     **/
    public Waypoint(Location loc, Waypoint prevWaypoint)
    {
        this.loc = loc;
        this.prevWaypoint = prevWaypoint;
    }

    /** Returns the location of the waypoint. **/
    public Location getLocation()
    {
        return loc;
    }
    
    /**
     * Returns the previous waypoint in the path, or <code>null</code> if this
     * is the start of the path.
     **/
    public Waypoint getPrevious()
    {
        return prevWaypoint;
    }
    
    /**
     * Этот мутатор позволяет установить как предыдущую стоимость, так и 
     * оставшуюся стоимость в одном вызове метода.  Обычно эти значения 
     * в любом случае устанавливаются в одно и то же время.
     **/
    public void setCosts(float prevCost, float remainingCost)
    {
        this.prevCost = prevCost;
        this.remainingCost = remainingCost;
    }

    /**
     * Возвращает фактическую стоимость проезда до этой точки из начального 
     * местоположения через ряд путевых точек в этой цепочке.
     **/
    public float getPreviousCost()
    {
        return prevCost;
    }

    /**
     * Возвращает оценку оставшейся стоимости проезда от этой точки 
     * до конечного пункта назначения.
     **/
    public float getRemainingCost()
    {
        return remainingCost;
    }

    /**
     * Возвращает общую смету затрат для этой путевой точки.  Это включает 
     * в себя фактическую стоимость проезда до этой точки из начального 
     * местоположения, плюс оценку оставшейся стоимости проезда от этой 
     * точки до конечного пункта назначения.
     **/
    public float getTotalCost()
    {
        return prevCost + remainingCost;
    }
}
