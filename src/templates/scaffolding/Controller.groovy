import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator
import javax.servlet.http.Cookie
import grails.converters.JSON
<%import org.apache.commons.lang.StringUtils%>

<%=packageName ? "package ${packageName}\n\n" : ''%>class ${className}Controller {

    static Map features = ${Scaffolding.getCharacteristics(className)}


    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    // TODO - try making a search params map that I manipulate specifically for doing the search
    // TODO - fix this shit.  this is so fucked up.  I don't know what the fuck I'm doing
    // TODO - I should totally rewrite this piece of shit
    // cool.  I'm doing this remotely! So how should this work?
    // TODO - should allow both filtering by date and string if appropriate
    def list = {
        log.info "list - \$params"
        if (!session.max) {
            def cookie = request.cookies.find {it.name == "maxListSize"}
            log.info "cookie - \$cookie"
            if (cookie) {
                session.max = cookie.value
            }
            else {
                session.max = 10;
            }
        }
        int defaultMax = session.max.toInteger()
        if (params.max) {
            Cookie cookie = new Cookie('maxListSize',params.max)
            cookie.maxAge = 60*60*24*90
            response.addCookie(cookie)
            session.max = params.max
        }
        log.info "list - features: \${features}"
        boolean sortFieldSpecialCase = features.specialSortFields.contains(params.sort)
        params.max = Math.min( params.max ? params.max.toInteger() : defaultMax,  100)
        if(!params.when) params.when = "Future"
        if(!params.order) params.order = "asc"
        if(!params.sort) params.sort = features.defaultSortField
        if(!params.offset) params.offset = 0
        boolean allSelected = params.when == "All"
        boolean futureSelected = params.when == "Future"
        boolean pastSelected = params.when == "Past"
        Map shownParams = [:]
        shownParams.putAll(params)
        if (shownParams?.offset == 0) {
            shownParams.remove("offset")
        }
        if (shownParams?.order == "asc") {
            shownParams.remove("order")
        }
        shownParams.remove("max")
        if (shownParams?.when == "Future") {
            shownParams.remove("when")
        }
        Map searchParams = [:]
        searchParams.putAll(params)
        if (sortFieldSpecialCase) {
            searchParams.remove('sort')
            searchParams.remove('max')
            searchParams.remove('offset')
            // must also clear the max and offset
            // also, would be better to completely remove the sort rather than change to default
        }
        if (shownParams?.sort == features.defaultSortField) {
            shownParams.remove("sort")
        }
        log.info "list - shownParams - \$shownParams"
        log.info "list - params - \$params"
        List queryParameters = []
        boolean showDateFilter =  features.hasFilterableDate
        boolean showStringFilter = features.hasFilterableStrings
        String query = "from $className "
        if (showDateFilter) {
            if (futureSelected) {
                query += "where \${features.defaultSortField} > ? "
                queryParameters.add(new Date() - 1)
            }
            else if (pastSelected) {
                query += "where \${features.defaultSortField} < ? "
                queryParameters.add(new Date() + 1)
            }
        }
        log.info "list - query: '\$query'"
        if (showStringFilter && params.filter) {
            if (showDateFilter && (futureSelected || pastSelected)) {
                query += "and ("
            }
            else {
                query += "where "
            }
            log.info "list - query: '\$query', filterableStrings: \${features.filterableStrings}"
            query +=
                features.filterableStrings.collect{"lower(\$it) like lower('%\${params.filter}%') "}.join(' or ')
            log.info "list - query: '\$query'"
            if (showDateFilter && (futureSelected || pastSelected)) {
                query += ") "
            }
        }
        def numEntries = ${className}.executeQuery( "select count(*) " + query, queryParameters)[0]
        def list
        if (sortFieldSpecialCase) {
            list = ${className}.findAll(query, queryParameters)
        }
        else {
            list = ${className}.findAll(query + "order by \${params.sort} \${params.order}",
                                        queryParameters,
                                        [max:params.max, offset:params.offset?.toInteger()])
        }
        // initial query to get numEntries does not need sort
        log.info "list - query: '\$query', queryParameters: \${queryParameters}, numEntries: \$numEntries"
        //def list = ${className}.list( searchParams )
        //def numEntries = ${className}.count()
        // This seems to depend on defaultSortField being the date field which may not be true, although
        // it probably should be.  This code may support the dot notation as is.
        //  Probably need to have a new var that is the filterableDate (as is being used elsewhere)
        /*
        if (showDateFilter) {
            if (futureSelected) {
                def c = ${className}.createCriteria()
                list = c {
                    gt(features.defaultSortField,new Date()-1)
                }
                numEntries = list.size()
                c = ${className}.createCriteria()
                list = c {
                    gt(features.defaultSortField,new Date()-1)
                    if (!sortFieldSpecialCase) {
                        maxResults(params.max)
                        firstResult(params.offset?.toInteger())
                        order(params.sort, params.order)
                    }
                }
            }
            else if (pastSelected) {
                def c = ${className}.createCriteria()
                list = c {
                    lt(features.defaultSortField,new Date()+1)
                }
                numEntries = list.size()
                c = ${className}.createCriteria()
                list = c {
                    lt(features.defaultSortField,new Date()+1)
                    if (!sortFieldSpecialCase) {
                        maxResults(params.max)
                        firstResult(params.offset?.toInteger())
                        order(params.sort, params.order)
                    }
                }
            }
        }
       // TODO use toStringFields, allow use when also date filtering, use HQL (lower(name) like lower('%cats%') or ...)
        if (showStringFilter && params.filter) {
            def c = ${className}.createCriteria()
            list = c {
                or {
                    features.filterableStrings.each {
                        ilike(it,params.filter + "%")
                    }
                }
            }
            numEntries = list.size()
            c = ${className}.createCriteria()
            list = c {
                or {
                    features.filterableStrings.each {
                        ilike(it,params.filter + "%")
                    }
                }
                if (!sortFieldSpecialCase) {
                    maxResults(params.max)
                    firstResult(params.offset?.toInteger())
                    order(params.sort, params.order)
                }
            }
        }
                */

        if (sortFieldSpecialCase) {
            list.sort { a,b ->
                params.order == "asc" ? a[params.sort].compareTo(b[params.sort]) : b[params.sort].compareTo(a[params.sort])
            }
            list = list[params.offset.toInteger()..Math.min(params.offset.toInteger()+params.max.toInteger()-1,list.size-1)]
        }
        [ ${propertyName}List: list,
          ${propertyName}Total: numEntries,
          'allSelected': allSelected,
          'pastSelected': pastSelected,
          'futureSelected': futureSelected,
          'showDateFilter' : showDateFilter,
          'showStringFilter' : showStringFilter,
          'shownParams' : shownParams]
    }

    def show = {
        log.info "show - \$params"
        def ${propertyName} = ${className}.get( params.id )

        if(!${propertyName}) {
            flash.error = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else { return [ ${propertyName} : ${propertyName} ] }
    }

    def print = {
        log.info "print - \$params"
        def ${propertyName} = ${className}.get( params.id )

        if(!${propertyName}) {
            flash.error = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else { return [ ${propertyName} : ${propertyName} ] }
    }

    def delete = {
        log.info "delete - \$params"
        def ${propertyName} = ${className}.get( params.id )
        if(${propertyName}) {
            try {
                ${propertyName}.delete(flush:true)
                flash.message = "${className} \${params.id} deleted"
                redirect(action:list)
            }
            catch(org.springframework.dao.DataIntegrityViolationException e) {
                flash.error = "${className} \${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.error = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        log.info "edit - \$params"
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        def excludedProps = ['version',
                             'id',
                             Events.ONLOAD_EVENT,
                             Events.BEFORE_DELETE_EVENT,
                             Events.BEFORE_INSERT_EVENT,
                             Events.BEFORE_UPDATE_EVENT]

        def props = domainClass.properties.findAll { !excludedProps.contains(it.name)}
        Collections.sort(props, comparator)
        def oneToManys = []
        def manyToOnes = []
        props.eachWithIndex { p,i ->
            if (p.oneToMany) {
                oneToManys.add(p)
            }
            else if (p.manyToOne || p.oneToOne) {
                manyToOnes.add(p)
            }
        }
        def ${propertyName}
        if (flash.${propertyName}) {
            ${propertyName} = flash.${propertyName}
        }
        else {
            ${propertyName} = ${className}.get( params.id )
        }

        if(!${propertyName}) {
            flash.error = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ ${propertyName} : ${propertyName}, 'oneToManys': oneToManys, 'manyToOnes': manyToOnes ]
        }
    }

    def update = {
        log.info "update - \$params"
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        def ${propertyName} = ${className}.get( params.id )
        if(${propertyName}) {
            if(params.version) {
                def version = params.version.toLong()
                if(${propertyName}.version > version) {
                    <%def lowerCaseName = grails.util.GrailsNameUtils.getPropertyName(className)%>
                    ${propertyName}.errors.rejectValue("version", "${lowerCaseName}.optimistic.locking.failure", "Another user has updated this ${className} while you were editing.")
                    flash.${propertyName} = ${propertyName}
                    redirect(action:edit)
                    return
                }
            }
            //${propertyName}.properties = fixedParams
            Scaffolding.setFromParams(domainClass,${propertyName},params)
            if(!${propertyName}.hasErrors() && ${propertyName}.save()) {
                flash.message = "${className} updated"
                redirect(action:'edit',id:${propertyName}.id)
            }
            else {
                flash.${propertyName} = ${propertyName}
                redirect(action:edit)
            }
        }
        else {
            flash.error = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        log.info "create - \$params"
        log.info "create - referer: \${request.getHeader('referer')}"
        log.info "create - \$flash"
        if (!request.getHeader('referer') ==~ /auth\\/login/) {
            session.referer = request.getHeader('referer')
        }
        def ${propertyName} = new ${className}()
        if (flash.${propertyName}) {
            ${propertyName} = flash.${propertyName}
            log.info "create - got from flash - \${${propertyName}.errors}"
        }
        else {
            ${propertyName}.properties = params
        }
        return ['${propertyName}':${propertyName}]
    }

    def save = {
        log.info "save - \$params"
        log.info "save - session.referer: \${session.referer}"
        log.info "save - \$flash"
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        def ${propertyName} = new ${className}()
        Scaffolding.setFromParams(domainClass,${propertyName},params)
        if(${propertyName}.hasErrors()) {
            //render(view:'create',model:[${propertyName}:${propertyName}])
            flash.${propertyName} = ${propertyName}
            log.info "save - updated - \$flash"
            redirect(action:create)
            return
        }
        <%
        domainClass.properties.each { p ->
            if (p.oneToMany) {
                def cp = domainClass.constrainedProperties[p.name]
                if (cp?.minSize > 0) {
        %>
        log.info "checking ${p.name} for minSize constraint"
        if (${propertyName}.${p.name}?.size() < ${cp.minSize}) {
            log.info "ooops! - not enough ${p.name}"
            // TODO Need to make this a warning and not just info
            if (${cp.minSize} == 1) {
                flash.error = "You can't create a ${className} without some ${p.naturalName}"
            }
            else {
                flash.error = "You can't create a ${className} without at least ${cp.minSize} ${p.naturalName}"
            }
            //render(view:'create',model:[${propertyName}:${propertyName}])
            redirect(action:create)
            return
        }
        <%
        }}}
        %>
        if(${propertyName}.save()) {
            flash.message = "${className} created"
            if (session.referer) {
                redirect(url:session.referer)
            }
            else {
                redirect(action:list)
            }
        }
        else {
            //render(view:'create',model:[${propertyName}:${propertyName}])
            flash.${propertyName} = ${propertyName}
            log.info "save - updated - \$flash"
            redirect(action:create)
        }
    }

    def ajaxSearch = {
        log.info "ajaxSearch - " + params
        def jsonList = []
        ${className}.list().sort().each {
            if (!params.term || it.toString().toLowerCase().contains(params.term.toLowerCase())) {
                jsonList.add([ id: it.id, value: it.toString() ])
            }
        }
        render jsonList as JSON
    }

    def ajaxSave = {
        log.info "ajaxSave - \$params"
        def response = [:]
        def ${propertyName} = new ${className}(params)
        if (${propertyName}.hasErrors() || !${propertyName}.save(flush: true)) {
            ${propertyName}.errors.allErrors.each {
                log.info "ajaxSave - found error - \$it"
            }
            response.errors = renderErrors(bean:${propertyName})
        }
        else {
            response.display = ${propertyName}.toString()
            response.id = ${propertyName}.id
        }
        log.info "ajaxSave - \$response"
        render response as JSON
    }

    def ajaxCalculateDependents = {
        log.info "ajaxCalculateDependents - \$params"
        ${className}.withTransaction { status ->
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        def ${propertyName}
        if (params.id) {
            ${propertyName} = ${className}.get( params.id )
            ${propertyName}.discard()
        }
        else {
            ${propertyName} = new ${className}()
        }
        Scaffolding.setFromParams(domainClass,${propertyName},params)
        def response = [:]
        <%
        def dependencies = domainClass.getPropertyValue('dependencies')
        dependencies?.keySet().each { dependencies2 ->
            dependencies2.each { dependency ->
                def cp = domainClass.constrainedProperties[dependency]
                if (cp?.format) {
        %>
        response.$dependency = formatNumber2(number:$propertyName.$dependency,format:"${cp.format}")
        <%
                }
                else {
        %>
        response.$dependency = $propertyName.$dependency
        <%
                }
            }
        }
        %>
        render response as JSON
        status.setRollbackOnly()}

    }

    def ajaxAddOneToMany = {
        log.info "ajaxAddOneToMany - \$params"
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        def ${propertyName}
        if (params.id) {
            ${propertyName} = ${className}.get( params.id )
            ${propertyName}.discard()
        }
        else {
            ${propertyName} = new ${className}()
        }
        Scaffolding.setFromParams(domainClass,${propertyName},params)
        def response = Scaffolding.createNew(domainClass,params.field,${propertyName})
        log.info "ajaxAddOneToMany - response - \$response"
        // now try generating the html
        /*
        def dependencies = domainClass.getPropertyValue('dependencies')
        log.info dependencies
        def values = dependencies.values()
        log.info values
        def value = values.toList()[0]
        log.info value
        String html = Scaffolding.renderOneToMany(domainClass,domainClass.getPropertyByName(params.field),"${propertyName}",domainClass.constrainedProperties[params.field],value)
        log.info "generated html for one to many - \$html"
        */
        render response as JSON
    }

        <%
        // Call scaffolding to get a list of ManyToOnes that have dependencies
        // returns list of properties
        Scaffolding.dynamicManyToOnes(domainClass)?.each { result->
            if (result.size() == 1) {
        %>
            def ajaxUpdate${StringUtils.capitalize(result[0].name)}Options = {

            }
        <%
            }
            else {
                String methodName = "ajaxUpdate${StringUtils.capitalize(result[0].name)}${StringUtils.capitalize(result[1].name)}Options"
        %>
    def $methodName = {
        log.info "$methodName - \$params"
        ${className}.withTransaction { status ->
        GrailsDomainClass domainClass = grailsApplication.getArtefact("Domain", "${className}")
        def ${propertyName}
        if (params.id) {
            ${propertyName} = ${className}.get( params.id )
            ${propertyName}.discard()
        }
        else {
            ${propertyName} = new ${className}()
        }
        Scaffolding.setFromParams(domainClass,${propertyName},params)
        def options = ${result[1].referencedDomainClass.name}.list()
        def response = []
        <%
                // if the property has a validator constraint, then filter out those that don't pass
                def cp = result[0].referencedDomainClass.constrainedProperties[result[1].name]
                if (cp.getAppliedConstraint('validator')) {
                    %>
        // filter out the options that don't pass the validator constraint
        def validator = domainClass.getPropertyByName('${result[0].name}').referencedDomainClass.constrainedProperties['${result[1].name}'].getAppliedConstraint('validator')
        options = Scaffolding.filterList(${propertyName}.${result[0].name}.toList().get(params.int('index')),options,validator)
        for (option in options) {
            response.add([id:option.id, value:option.toString()])
        }
        log.info "$methodName - response - \$response"
        render response as JSON
        status.setRollbackOnly()}
                    <%
                }
        %>
    }
        <%
            }
        }
        %>

}
