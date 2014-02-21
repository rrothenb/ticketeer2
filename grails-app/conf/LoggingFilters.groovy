/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rick
 */
class LoggingFilters {
    def filters = {
        logURL(controller:'*', action:'*') {
            before = {
                String name = request.forwardURI
                if (request.queryString) {
                    name += "?" + request.queryString
                }
                Thread.currentThread().name = name
            }
        }
    }
}

