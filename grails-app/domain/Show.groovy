class Show implements Comparable{
    String name
    SortedSet performances
    static hasMany = [ performances : Performance, prices : ShowPrice ]
    String toString() {
        if (name.length() > 60) {
            return name.substring(0,60) + "..."
        }
        return name
    }
    static toStringFields = ['name']
    static mapping = {
        table 'showz'
        version false
    }
    int compareTo(show) {
        return name.compareTo(show.name)
    }
    static constraints = {
        name()
        prices()
        performances()
    }
}
