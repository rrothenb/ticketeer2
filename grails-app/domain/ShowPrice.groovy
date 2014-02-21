class ShowPrice {
    Price price
    String toString() {"$price"}
    static belongsTo = [show:Show]
    static constraints = {
        price()
        show()
    }
}
