/**
 * Этот класс представляет определенное местоположение на 2D-карте.  
 * Координаты - это целочисленные значения.
 **/
public class Location
{
    /** X coordinate of this location. **/
    public int xCoord;

    /** Y coordinate of this location. **/
    public int yCoord;


    /** Creates a new location with the specified integer coordinates. **/
    public Location(int x, int y)
    {
        xCoord = x;
        yCoord = y;
    }

    /** Creates a new location with coordinates (0, 0). **/
    public Location()
    {
        this(0, 0);
    }
    
    /**
     * Проверяем является ли объект экземпляром класса Location (получаем ссылку на объект).
     * Если является, сравниваем его координаты, иначе возвращаем false.
     **/
    public boolean equals(Object object) {
        if (object instanceof Location) {
            Location loc = (Location) object;
            return (loc.xCoord == xCoord && loc.yCoord == yCoord);
        }
        return false;
    }

    /**
     * Возвращаем хэш-код объекта по его координатам.
     **/
    public int hashCode() {
        return 31 * xCoord + 9 * yCoord;
    }
}
