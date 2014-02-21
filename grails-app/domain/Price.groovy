import java.text.NumberFormat
import org.apache.commons.logging.LogFactory

class Price implements Comparable {
    private static def log = LogFactory.getLog(this)
    String name;
    float cost;
    String toString() {return name + " (" + NumberFormat.getCurrencyInstance().format(cost) + ")"}
    static toStringFields = ['name']
    static Price findDefault(Show show) {
        log.info("findDefault - $show")
        Price price = findByNameIlike('%' + show.name + '%')
        if (price) {
            log.info("found show within price")
            return price
        }
        price = find("from Price where ? like '%' || lower(name) || '%'", [show.name.toLowerCase()])
        if (price) {
            log.info("found price within show")
            return price
        }
        return Theater.instance.defaultPrice
    }
    int compareTo(price) {
        return name.compareTo(price.name)
    }
    static constraints = {
        name()
        cost(format:"\\\$0.00")
    }
}
