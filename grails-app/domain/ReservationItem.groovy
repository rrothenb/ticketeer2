import java.text.NumberFormat;
import org.apache.commons.logging.LogFactory

class ReservationItem {
    private static def log = LogFactory.getLog(this)
    int quantity
    Price price
    float discount
    String toString() {
        String result = ""
        if (quantity) {
            result = result + quantity
        }
        if (price) {
            result = result + " " + price.name + " @ " + NumberFormat.getCurrencyInstance().format(price.cost - discount)
        }
        return result
    }
    static toStringFields = ['price']
    static belongsTo = [reservation:Reservation]
    static dependencies = [['price']:['reservation']]
    static constraints = {
        quantity(validator:{ value,obj ->
                if (value == 0) {
                    return "must.be.positive"
                }
        })
        price(validator:{ value,obj ->
                try {
                    //log.debug "ReservationItem price validator value - $value, obj - ${obj}, reservation - ${obj?.reservation}, performance - ${obj?.reservation?.performance}, show - ${obj?.reservation?.performance?.show}, prices - ${obj?.reservation?.performance?.show?.prices}"
                    Set prices = obj?.reservation?.performance?.show?.prices
                    boolean ok = true;
                    if (prices == null) {
                        log.debug "ReservationItem price validator - no prices so OK to go!"
                        return ok;
                    }
                    if (prices.empty) {
                        return ok;
                    }
                    //ok = prices.contains(value)
                    ok = prices.any { it.price == value }
                    log.debug "ReservationItem price validator - $value OK? - $ok"
                    return ok
                }
                catch (Exception e) {
                    log.info "ReservationItem price validator - bad - $e"
                }
            })
        discount(format:"\\\$0.00")
    }
}
