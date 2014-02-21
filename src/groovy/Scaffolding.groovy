import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events
import org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ValidatorConstraint
import org.springframework.validation.BeanPropertyBindingResult
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.logging.LogFactory
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.apache.commons.lang.StringUtils

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rick
 */
class Scaffolding {
    private static def log = LogFactory.getLog(this)

    static List excludedProps =
        ['version',
        'id',
        Events.ONLOAD_EVENT,
        Events.BEFORE_DELETE_EVENT,
        Events.BEFORE_INSERT_EVENT,
        Events.BEFORE_UPDATE_EVENT]

    static LinkedHashMap describe(GrailsDomainClass domainClass, List exclude) {
        return describe(domainClass, exclude, "")
    }

    static LinkedHashMap describe(GrailsDomainClass domainClass, List exclude, String prefix) {
        exclude.add(domainClass)
        LinkedHashMap results = new LinkedHashMap()
        def excludedProps = ['version',
                                         'id',
            Events.ONLOAD_EVENT,
            Events.BEFORE_DELETE_EVENT,
            Events.BEFORE_INSERT_EVENT,
            Events.BEFORE_UPDATE_EVENT]
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name)}
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        Collections.sort(props, comparator)
        props.each { p ->
            if (p.manyToOne) {
                if (!domainClass.isOwningClass(p.type) && !exclude.contains(p.referencedDomainClass)) {
                    results.putAll(describe(p.referencedDomainClass, exclude, prefix + p.name + "?."))
                }
            }
            else if (!Collection.class.isAssignableFrom(p.type)) {
                results.put(prefix + p.name,p)
            }
        }
        return results
    }

    static List listableFields(GrailsDomainClass domainClass, GrailsDomainClassProperty property, String domainInstance) {
        def excludedProps = ['version',
                                               'id',
            Events.ONLOAD_EVENT,
            Events.BEFORE_DELETE_EVENT,
            Events.BEFORE_INSERT_EVENT,
            Events.BEFORE_UPDATE_EVENT]


        def props = []
        for (prop in property.referencedDomainClass.properties) {
            if (excludedProps.contains(prop.name)) {
                continue
            }
            if (Collection.class.isAssignableFrom(prop.type)) {
                continue
            }
            if (prop.referencedDomainClass == domainClass) {
                continue
            }
            if (property.referencedDomainClass.constrainedProperties[prop.name].hasAppliedConstraint("secondary")) {
                continue
            }
            props.add(prop)
        }
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(property.referencedDomainClass)
        Collections.sort(props, comparator)
        return props
    }

    static List listableFields(GrailsDomainClass domainClass) {
        def excludedProps = ['version',
                                               'id',
            Events.ONLOAD_EVENT,
            Events.BEFORE_DELETE_EVENT,
            Events.BEFORE_INSERT_EVENT,
            Events.BEFORE_UPDATE_EVENT]


        def props = []
        for (prop in domainClass.properties) {
            if (excludedProps.contains(prop.name)) {
                continue
            }
            if (Collection.class.isAssignableFrom(prop.type)) {
                continue
            }
            if (domainClass.constrainedProperties[prop.name].hasAppliedConstraint("secondary")) {
                continue
            }
            props.add(prop)
        }
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        Collections.sort(props, comparator)
        return props
    }

    static List filterList(Object target, List objects, ValidatorConstraint constraint) {
        Collections.sort(objects)
        if (!constraint) {
            return objects
        }

        def results = objects.findAll{
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target,"target")
            log.debug "Scaffolding.filterList - it: $it"
            constraint.validate(target,it,errors)
            errors.errorCount == 0
        }
        return results
    }
    
    static boolean isTemporal(GrailsDomainClass domainClass) {
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        boolean hasFilterableDate = false
        def excludedProps = ['version',
                             'id',
                             Events.ONLOAD_EVENT,
                             Events.BEFORE_DELETE_EVENT,
                             Events.BEFORE_INSERT_EVENT,
                             Events.BEFORE_UPDATE_EVENT]

        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        props.eachWithIndex { p,i ->
            if (i < 3 && p.type == Date.class) {
                hasFilterableDate = true
            }
        }
        return hasFilterableDate
    }

    static String filterableDate(GrailsDomainClass domainClass) {
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        String filterableDate = null
        def excludedProps = ['version',
                             'id',
                             Events.ONLOAD_EVENT,
                             Events.BEFORE_DELETE_EVENT,
                             Events.BEFORE_INSERT_EVENT,
                             Events.BEFORE_UPDATE_EVENT]

        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        props.eachWithIndex { p,i ->
            if (i < 3 && p.type == Date.class) {
                filterableDate = p.name
            }
        }
        return filterableDate
    }

    static public String render(domainClass,property,domainInstance,cp) {
        return render(domainClass,property,domainInstance,cp,"",null)
    }

    static public String render(domainClass,property,domainInstance,cp,prefix,parentProperty) {
        log.info("render - property.name: ${property.name}, property.type: ${property.type}, prefix: ${prefix}, cp: ${cp}, domainClass.name: ${domainClass.name}")
        String html;
        Set dependencies = getDependencyFields(domainClass)
        if(property.type == Boolean.class || property.type == boolean.class)
            html = renderBooleanEditor(domainClass,property,domainInstance,cp,prefix)
        else if(Number.class.isAssignableFrom(property.type) || (property.type.isPrimitive() && property.type != boolean.class))
        html = renderNumberEditor(domainClass,property,domainInstance,cp,prefix)
        else if(property.type == String.class)
        html = renderStringEditor(domainClass,property,domainInstance,cp,prefix)
        else if(property.type == Date.class || property.type == java.sql.Date.class || property.type == java.sql.Time.class)
        html = renderDateEditor(domainClass,property,domainInstance,cp,prefix)
        else if(property.type == Calendar.class)
        html = renderDateEditor(domainClass,property,domainInstance,cp,prefix)
        else if(property.type == URL.class)
        html = renderStringEditor(domainClass,property,domainInstance,cp)
        else if(property.isEnum())
        html = renderEnumEditor(domainClass,property,domainInstance,cp)
        else if(property.type == TimeZone.class)
        html = renderSelectTypeEditor("timeZone",domainClass,property,domainInstance,cp)
        else if(property.type == Locale.class)
        html = renderSelectTypeEditor("locale",domainClass,property,domainInstance,cp)
        else if(property.type == Currency.class)
        html = renderSelectTypeEditor("currency",domainClass,property,domainInstance,cp)
        else if(property.type==([] as Byte[]).class) //TODO: Bug in groovy means i have to do this :(
        html = renderByteArrayEditor(domainClass,property,domainInstance,cp)
        else if(property.type==([] as byte[]).class) //TODO: Bug in groovy means i have to do this :(
        html = renderByteArrayEditor(domainClass,property,domainInstance,cp)
        else if(property.manyToOne || property.oneToOne)
        html = renderManyToOne(domainClass,property,domainInstance,cp,prefix,dependencies,parentProperty)
        else if((property.oneToMany && !property.bidirectional) || (property.manyToMany && property.isOwningSide()))
        html = renderManyToMany(domainClass, property,domainInstance,cp)
        else if(property.oneToMany)
        html = renderOneToMany(domainClass,property,domainInstance,cp,dependencies)
        else if(property.type == List.class && !property.isPersistent())
        html = renderList(domainClass,property,domainInstance,cp)
        log.debug(html)
        return html
    }

    static private renderEnumEditor(domainClass,property,domainInstance,cp) {
        if(property.isEnum()) {
            return "<g:select  from=\"\${${property.type.name}?.values()}\" value=\"\${${domainInstance}?.${property.name}}\" name=\"${property.name}\" ${renderNoSelection(property)}></g:select>"
        }
    }

    static private renderStringEditor(domainClass, property,domainInstance,cp,prefix) {
        if(!cp) {
            if (property.isPersistent()) {
                return "<input type=\"text\" name=\"${prefix}${property.name}\" id=\"${prefix}${property.name}\" value=\"\${fieldValue(bean:${domainInstance},field:'${property.name}')}\" />"
            }
            else {
                return "<span id=\"${prefix}${property.name}\">\${fieldValue(bean:${domainInstance},field:'${property.name}')}</span>"
            }
        }
        else {
            if (!property.isPersistent() || !cp.editable) {
                return "<span id=\"${prefix}${property.name}\">\${fieldValue(bean:${domainInstance},field:'${property.name}')}</span>"
            }
            else if("textarea" == cp.widget || (cp.maxSize > 250 && !cp.password && !cp.inList)) {
                return "<textarea rows=\"5\" cols=\"40\" name=\"${prefix}${property.name}\">\${fieldValue(bean:${domainInstance}, field:'${property.name}')}</textarea>"
            }
             else {
                if(cp.inList) {
                    log.info "****STRING INLIST*** ${domainInstance} ${property.name}"
                    def sb = new StringBuffer('<g:select ')
                    sb << "id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" from=\"\${${domainClass.name}?.constraints?.${property.name}?.inList}\" value=\"\${${domainInstance}?.${property?.name}}\" ${renderNoSelection(property)}>"
                    sb << '</g:select>'
                    return sb.toString()
                }
                else {
                    def sb = new StringBuffer('<input ')
                    cp.password ? sb << 'type="password" ' : sb << 'type="text" '
                    if(!cp.editable) sb << 'readonly="readonly" '
                    if(cp.maxSize) sb << "maxlength=\"${cp.maxSize}\" "
                    sb << "id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" value=\"\${fieldValue(bean:${domainInstance},field:'${property.name}')}\"/>"
                    return sb.toString()
                }
            }
        }
    }

    static private renderByteArrayEditor(domainClass,property,domainInstance,cp) {
        return "<input type=\"file\" id=\"${property.name}\" name=\"${property.name}\" />"
    }

    static private renderManyToOne(domainClass,property,domainInstance,cp,prefix,dependencies,parentProperty) {
        log.info("renderManyToOne - domainClass - $domainClass, property - $property, domainInstance - $domainInstance, cp - $cp, prefix - $prefix, dependencies - $dependencies")
        if(property.association) {
            def sw = new StringWriter()
            def pw = new PrintWriter(sw)
            MarkupBuilder builder = new MarkupBuilder(sw)
            if (prefix == "") {
                builder.setDoubleQuotes(true)
                def excludedProps = ['version',
                                             'id',
                    Events.ONLOAD_EVENT,
                    Events.BEFORE_DELETE_EVENT,
                    Events.BEFORE_INSERT_EVENT,
                    Events.BEFORE_UPDATE_EVENT]
                def props = property.referencedDomainClass.properties.findAll { !excludedProps.contains(it.name) }
                
                DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(property.referencedDomainClass)
                Collections.sort(props, comparator)
                builder.div(id:"${property.name}_new_dialog", title:"Create New ${property.referencedDomainClass.naturalName}", style:"display:none;") {
                    table {
                        tbody {
                            props.each { p ->
                                if(!Collection.class.isAssignableFrom(p.type)) {
                                    def cp2 = property.referencedDomainClass.constrainedProperties[p.name]
                                    log.info "p.name - ${p.name}, cp2 - ${cp2}, domainClass.constrainedProperties - ${domainClass.constrainedProperties}, domainClass - ${domainClass}"
                                    def display = (cp2 ? cp2.display : true)        
                                    def description = ""
                                    if (cp2.hasAppliedConstraint("description")) {
                                        description = cp2.getAppliedConstraint("description").toString()
                                    }
                                    if(display && p.persistent && cp2?.editable) {
                                        tr {
                                            td(title:description) {
                                                label (for:"${property.name}_new_${p.name}", "${p.naturalName}:")
                                            }
                                            td(title:description) {
                                                mkp.yieldUnescaped(render(property.referencedDomainClass,p,"empty${property.referencedDomainClass.propertyName}",cp2,property.name + "_new_",property))
                                            }
                                        }                                        
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //def filterableDate = filterableDate(property.referencedDomainClass)
            def filterableDate = temporalField(property.referencedDomainClass)
            if (filterableDate) {
                if (cp?.hasAppliedConstraint("futureOnly")) {
                    pw.println "<g:select2 optionKey=\"id\" from='\${${property.type.name}.findAll(\"from ${property.type.name} where ${filterableDate} > ? order by ${filterableDate} asc\",[new Date()-1])}' name=\"${prefix}${property.name}.id\" value=\"\${${domainInstance}?.${property.name}?.id}\" valueInstance=\"\${${domainInstance}?.${property.name}}\" rejectIf=\"\${{it.$filterableDate < new Date()}}\" rejectClass=\"past\" valueProvided=\"\${${domainInstance}?.id != null}\" ${renderNoSelection(property)}></g:select2>"
                }
                else {
                    pw.println "<g:select optionKey=\"id\" from=\"\${Scaffolding.filterList(new ${domainClass.fullName}(), ${property.type.name}.list(),${domainClass.name}?.constraints?.get('${property.name}')?.getAppliedConstraint('validator'))}\" name=\"${prefix}${property.name}.id\" value=\"\${${domainInstance}?.${property.name}?.id}\" ${renderNoSelection(property)}></g:select>"
                }
            }
            else if (cp) {
                log.info("renderManyToOne - constraint - ${cp.getAppliedConstraint('validator')}, dependencies - $dependencies")
                // TODO - if there's a validator constraint and the field depends on something, then add handler to update list whenever selected
                // TODO - the count should reflect the number returned after applying constraints
                pw.println "<g:if test=\"\${${property.type.name}.count() < 30}\">"
                String theClass = "many-to-one-id"
                if (dependencies) {
                    theClass = theClass + " ajaxUpdate"
                    pw.println "<input id=\"${prefix}${property.name}_controller\" name=\"${prefix}${property.name}_controller\" type=\"hidden\" class=\"many-to-one-controller\" value=\"${domainClass.name}\"/>"
                    pw.println "<input id=\"${prefix}${property.name}_name\" name=\"${prefix}${property.name}_name\" type=\"hidden\" class=\"many-to-one-name\" value=\"${StringUtils.capitalize(property.name)}\"/>"
                    pw.println "<input id=\"${prefix}${property.name}_method\" name=\"${prefix}${property.name}_method\" type=\"hidden\" class=\"many-to-one-method\" value=\"\${params.controller}/ajaxUpdate${StringUtils.capitalize(parentProperty?.name)}${StringUtils.capitalize(property.name)}Options?index=###\"/>"
                }
                // TODO - create the new instance using the parent classes' newInstance method if it has one.  Must generate code "new Class() or instance.new<ClassName>().  Don't know parent.  May not be able to do this
                pw.println "<g:select class=\"${theClass}\" optionKey=\"id\" from=\"\${Scaffolding.filterList(new ${domainClass.fullName}(), ${property.type.name}.list(),${domainClass.name}?.constraints?.get('${property.name}').getAppliedConstraint('validator'))}\" name=\"${prefix}${property.name}.id\" value=\"\${${domainInstance}?.${property.name}?.id}\" ${renderNoSelection(property)}></g:select>"
                pw.println "</g:if>"
                pw.println "<g:else>"
                pw.println "<script>"
                pw.println "  \$(document).ready(function(){\$(\"#${prefix}${property.name}\").autocomplete({source:\"\${createLink(action:\"ajaxSearch\", controller:\"${property.type.name.toLowerCase()}\")}\", delay: 100, minLength: 2, select:function(event, data) {if (data.item) {\$(\"#${prefix}${property.name}_id\").val(data.item.id);\$(\"#${prefix}${property.name}_id\").change();}}});});"
                pw.println "</script>"
                if (prefix == "") {
                    builder.input(id:"${prefix}${property.name}",
                                  name:"${prefix}${property.name}",
                                  type:"text",
                                  class:"autocomplete",
                                  value:"\${${domainInstance}?.${property.name}}")
                }
                else {
                    pw.println "<input id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.fieldName}}\" type=\"text\" />"
                }
                pw.println "<input id=\"${prefix}${property.name}_id\" name=\"${prefix}${property.name}_id\" type=\"hidden\" class=\"many-to-one-id\"/>"
                pw.println "</g:else>"
            }
            else {
                pw.println "<g:select optionKey=\"id\" from=\"\${${property.type.name}.list().sort()}\" name=\"${prefix}${property.name}.id\" value=\"\${${domainInstance}?.${property.name}?.id}\" ${renderNoSelection(property)}></g:select>"
            }
            if (prefix == "") {
                builder.a(href:'#', class:"icon ui-state-default", id:"${property.name}_new", style:"display: inline-block;") {
                    span(class:'icon ui-icon ui-icon-plus', title:"Create a new ${property.naturalName} for this ${domainClass.name}")
                }
                builder.a(onclick:"goTo('${property.referencedDomainClass.propertyName}','${property.name}.id','\${${domainInstance}?.${property.name}?.id}')",
                    class:"icon ui-state-default",
                    style:"display: inline-block;") {
                    span(class:"icon ui-icon ui-icon-extlink", title:"Edit this ${property.naturalName}")
                }
                builder.script {
                    mkp.yieldUnescaped("\$(document).ready(function(){\$(\"#${property.name}_new\").click(function(){newManyToOne(\"${property.name}\");});});")
                }
            }
            return sw.toString()
        }
    }

    static private renderManyToMany(domainClass,property,domainInstance,cp) {
        def sw = new StringWriter()
        def pw = new PrintWriter(sw)

        pw.println "<g:select name=\"${property.name}\""
        pw.println "from=\"\${${property.referencedDomainClass.fullName}.list()}\""
        pw.println "size=\"5\" multiple=\"yes\" optionKey=\"id\""
        pw.println "value=\"\${${domainInstance}?.${property.name}}\" />"

        return sw.toString()
    }

    static private String renderOneToMany(domainClass,property,domainInstance,cp,dependencies) {
        log.info "*** renderOneToMany - domainClass: ${domainClass}, property - ${property}, domainInstance: ${domainInstance}, cp: ${cp}, dependencies: ${dependencies}"
        def sw = new StringWriter()
        def listableFields = Scaffolding.listableFields(domainClass, property, domainInstance)
        MarkupBuilder builder = new MarkupBuilder(sw)
        builder.setDoubleQuotes(true)
        builder.div(class:'hidden') {
            table() {
                tbody(id:"${property.name}_row"){
                    tr(class:'hidden oneToMany') {
                        'g:hiddenField'(name:property.name + "_###_id", value:"-1")
                        listableFields.each { p ->
                            if (p.persistent) {
                                def cp2 = property.referencedDomainClass.constrainedProperties[p.name]
                                td() {
                                    mkp.yieldUnescaped(render(property.referencedDomainClass,p,"empty${property.referencedDomainClass.propertyName}",cp2,property.name + "_###_",property))
                                }
                            }
                        }
                        td(class:"ui-state-default") {
                        'g:link'(action:"edit",
                                class:"icon-link",
                                controller:property.referencedDomainClass.propertyName,
                                style:"margin-right: auto;margin-left:auto;",
                                id:"\${empty${property.referencedDomainClass.propertyName}?.id}") {
                                span(class:"icon ui-icon ui-icon-extlink", style:"margin-right: auto;margin-left:auto;")
                            }
                        }
                        td(class:"ui-state-default") {
                            a(href:'#', class:"icon-link delete", style:"margin-right: auto;margin-left:auto;") {
                                span(class:'icon ui-icon ui-icon-close', style:"margin-right: auto;margin-left:auto;")
                            }
                        }
                    }
                }
            }
        }
        log.info("renderOneToMany - dependencies - $dependencies, property.name - ${property.name}")
        builder.script {
            mkp.yieldUnescaped("\$(document).ready(function(){\$(\"#${property.name}_add\").click(function(){addOneToMany(\"${property.name}\",'${domainClass.propertyName}');});")
            if (dependencies.contains(property.name)) {
                log.info("${property.name} is a one to many dependency field")
                mkp.yieldUnescaped("\$(\"#${property.name}_tbody .numeric\").live('keyup',function(event) {ajaxCalculateDependents(\"${domainClass.propertyName}\",event);});")
                mkp.yieldUnescaped("\$(\"#${property.name}_tbody .many-to-one-id\").live('change',function(event) {ajaxCalculateDependents(\"${domainClass.propertyName}\",event);});")
                mkp.yieldUnescaped("\$(\"#${property.name}_add\").click(function(event){ajaxCalculateDependents(\"${domainClass.propertyName}\",event);});")
                mkp.yieldUnescaped("\$(\"#${property.name}_tbody td a.delete\").live('click',function(event){ajaxCalculateDependents(\"${domainClass.propertyName}\",event);});")
                // TODO - reset count to 0 on page load?
            }
            mkp.yieldUnescaped("});")
        }
        sw.println("<%def empty${property.referencedDomainClass.propertyName} = new ${property.referencedDomainClass.name}()%>")
        builder.table {
            'g:hiddenField'(name:property.name + "_num", value:"\${$domainInstance?.${property.name}?.size()}", class:"oneToManyCount")
            tbody(id:"${property.name}_tbody") {
                tr {
                    listableFields.each { field ->
                        th(class:'ui-widget-content', field.naturalName)
                    }
                    th(class:'ui-widget-content', "Edit")
                    th(class:'ui-widget-content', "Delete")
                }
                'g:each'(var:property.name[0], in:"\${$domainInstance?.$property.name?}", status:"i") {
                    tr(class:"\${i%2 == 1 ? 'even' : 'odd'} oneToMany") {
                    'g:hiddenField'(name:property.name + "_\${i}_id", value:"\${${property.name[0]}.id}")
                        if (property.referencedDomainClass.getPropertyByName('version')) {
                        'g:hiddenField'(name:property.name + "_\${i}_version", value:"\${${property.name[0]}.version}")
                        }
                        listableFields.each { p ->
                                def cp2 = property.referencedDomainClass.constrainedProperties[p.name]
                                td() {
                                    mkp.yieldUnescaped(render(property.referencedDomainClass,p,property.name[0],cp2,property.name + "_\${i}_",property))
                                }
                        }
                        td(class:"ui-state-default") {
                        'g:link'(action:"edit",
                                class:"icon",
                                title:"Edit this ${property.referencedDomainClass.naturalName}",
                                style:"margin-right: auto;margin-left:auto;",
                                controller:property.referencedDomainClass.propertyName,
                                id:"\${${property.name[0]}.id}") {
                                span(class:"icon ui-icon ui-icon-extlink", style:"margin-right: auto;margin-left:auto;")
                                }
                        }
                        td(class:"ui-state-default") {
                            a(href:'#', class:"icon delete", style:"margin-right: auto;margin-left:auto;", title:"Delete this ${property.referencedDomainClass.naturalName}") {
                                span(class:'icon ui-icon ui-icon-close', style:"margin-right: auto;margin-left:auto;")
                            }
                        }
                    }
                }
            }
            log.info "renderOneToMany - property.name: ${property.name}, property.owningSide: ${property.owningSide}, property.otherSide.owningSide: ${property.otherSide.owningSide}"
            if (property.owningSide) {
                tbody {
                    tr {
                        td {
                        'g:submitButton'(class:"ui-state-default", name:"${property.name}_add", value:"Add ${property.naturalName}", onclick:"return false", title:"Add a ${property.referencedDomainClass.naturalName} to this ${domainClass.naturalName}")
                        }
                    }
                }
            }
        }
        return sw
    }
    
    static private String renderOneToMany(domainClass,property,domainInstance,listableFields,rowClass,rowIndex,rowId,rowVersion) {
        def sw = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(sw)
        builder.setDoubleQuotes(true)
        builder.tr(class:rowClass) {
            'g:hiddenField'(name:property.name + "_${rowIndex}_id", value:rowId)
            if (property.referencedDomainClass.getPropertyByName('version')) {
                'g:hiddenField'(name:property.name + "_${rowIndex}_version", value:rowVersion)
            }
            listableFields.each { p ->
                def cp2 = property.referencedDomainClass.constrainedProperties[p.name]
                td() {
                    mkp.yieldUnescaped(render(property.referencedDomainClass,p,property.name[0],cp2,property.name + "_${rowIndex}_",property))
                }
            }
            td(class:"ui-state-default") {
                'g:link'(action:"edit",
                         class:"icon",
                         title:"Edit this ${property.referencedDomainClass.naturalName}",
                         style:"margin-right: auto;margin-left:auto;",
                         controller:property.referencedDomainClass.propertyName,
                         id:rowId) {
                    span(class:"icon ui-icon ui-icon-extlink", style:"margin-right: auto;margin-left:auto;")
                }
            }
            td(class:"ui-state-default") {
                a(href:'#', class:"icon delete", style:"margin-right: auto;margin-left:auto;", title:"Delete this ${property.referencedDomainClass.naturalName}") {
                    span(class:'icon ui-icon ui-icon-close', style:"margin-right: auto;margin-left:auto;")
                }
            }
        }
        return sw
    }

    static private renderNumberEditor(domainClass,property,domainInstance,cp,prefix) {
        if(!cp) {
            if(property.type == Byte.class) {
                return "<g:select from=\"\${-128..127}\" name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.name}}\"></g:select>"
            }
            else {
                if (property.isPersistent()) {
                    return "<input type=\"text\" id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" value=\"\${fieldValue(bean:${domainInstance},field:'${property.name}')}\" class=\"numeric\"/>"
                }
                else {
                    return "<span id=\"${prefix}${property.name}\">\${fieldValue(bean:${domainInstance},field:'${property.name}')}</span>"
                }
            }
        }
        else {
            if(cp.range) {
                return "<g:select from=\"\${${cp.range.from}..${cp.range.to}}\" id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.name}}\" ${renderNoSelection(property)}></g:select>"
            }
            else if(cp.inList) {
                def sb = new StringBuffer('<g:select ')
                sb << "id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" from=\"\${${domainClass.propertyName}?.constraints?.${property.name}?.inList}\" value=\"\${${domainClass.propertyName}.${property?.name}}\" ${renderNoSelection(property)}>"
                sb << '</g:select>'
                return sb.toString()
            }
            else if (!property.isPersistent()) {
                if (cp?.format) {
                    return "<span id=\"${prefix}${property.name}\"><g:formatNumber2 format=\"${cp.format}\" number=\"\${fieldValue(bean:${domainInstance},field:'${property.name}')}\"/></span>"
                }
                else {
                    return "<span id=\"${prefix}${property.name}\">\${fieldValue(bean:${domainInstance},field:'${property.name}')}</span>"
                }
            }
            else {
                return "<input type=\"text\" id=\"${prefix}${property.name}\" name=\"${prefix}${property.name}\" value=\"\${fieldValue(bean:${domainInstance},field:'${property.name}')}\"  class=\"numeric\"/>"
            }
        }
     }

    static private renderBooleanEditor(domainClass,property,domainInstance,cp,prefix) {
        if(!cp) {
            return "<g:checkBox name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.name}}\"></g:checkBox>"
        }
        else {
            def buf = new StringBuffer('<g:checkBox ')
            if(cp.widget) buf << "widget=\"${cp.widget}\"";

            buf << "name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.name}}\" "
            cp.attributes.each { k,v ->
                buf << "${k}=\"${v}\" "
            }
            buf << '></g:checkBox>'
            return buf.toString()
        }
    }

    static private renderDateEditor(domainClass,property,domainInstance,cp,prefix) {
        def precision = property.type == java.sql.Date ? 'day' : 'minute';
        if(!cp) {
            return "<g:RTTdatePicker name=\"${prefix}${property.name}\" value=\"\${${domainInstance}?.${property.name}}\" precision=\"${precision}\"></g:RTTdatePicker>"
        }
        else {
            if(!cp.editable) {
                return "\${${domainInstance}?.${property.name}?.format(\"EEE, M/d/yyyy 'at' h:mm aa\")}"
            }
            else {
                def sw = new StringWriter()
                def pw = new PrintWriter(sw)
                pw.print("<input type=\"test\" class=\"calendarField\" ");
                pw.print("id=\"${prefix}${property.name}\" ")
                pw.print("name=\"${prefix}${property.name}\" ")
                pw.print("value=\"\${${domainInstance}?.${property.name}?.format(\"EEE, M/d/yyyy 'at' h:mm aa\")}\"")
                pw.println("/>")
                return sw
            }
        }
    }

    static private renderSelectTypeEditor(type,domainClass,property,domainInstance,cp) {
        if(!cp) {
            return "<g:${type}Select name=\"${property.name}\" value=\"\${${domainInstance}?.${property.name}}\"></g:${type}Select>"
        }
        else {
            def buf = new StringBuffer("<g:${type}Select ")
            if(cp.widget) buf << "widget=\"${cp.widget}\" ";
            cp.attributes.each { k,v ->
                buf << "${k}=\"${v}\" "
            }
            buf << "name=\"${property.name}\" value=\"\${${domainInstance}?.${property.name}}\" ${renderNoSelection(property)}></g:${type}Select>"
            return buf.toString()
        }
    }

    static private renderNoSelection(property) {
        if(property.optional) {
            if(property.manyToOne || property.oneToOne) {
                return "noSelection=\"['null':'']\""
            }
            else {
                return "noSelection=\"['':'']\""
            }
        }
        return ""
    }

    static private renderList(domainClass,property,domainInstance,cp) {
        def sw = new StringWriter()
        def pw = new PrintWriter(sw)
        pw.println()
        pw.println "<g:each var=\"${property.name[0]}\" in=\"\${${domainInstance}?.${property.name}?}\">"
        pw.println "    \${${property.name[0]}?.encodeAsHTML()}<p>"
        pw.println "</g:each>"
        return sw.toString()
    }

    static public void setFromParams(GrailsDomainClass domainClass, Object domainObject, params) {
        setFromParams(domainClass, domainObject, "", params)
    }

    static void setFromParams(GrailsDomainClass domainClass, Object domainObject, String prefix, params) {
        def newParams = [:]
        def childErrors = [:]
        log.info("update: prefix - $prefix, domainClass - $domainClass, domainObject - $domainObject, params - $params")
        params.each {
            //log.info("${it.key} - ${it.value}")
        }
        domainClass.properties.each { p ->
            if (p.persistent) {
                //log.info("checking ${p.name} - ${p.type.name} - ${params.get(prefix + p.name)} - $p")
                if (p.manyToOne || p.oneToOne) {
                    //log.info "manyToOne - ${p.name} - ${prefix + p.name + "_id"} - ${params[prefix + p.name + "_id"]} or ${params[prefix + p.name + ".id"]}"
                    if (params[prefix + p.name + "_id"]) {
                        newParams[p.name + ".id"] = params[prefix + p.name + "_id"]
                    }
                    else if (params[prefix + p.name + ".id"]) {
                        newParams[p.name + ".id"] = params[prefix + p.name + ".id"]
                    }
                }
                else if (p.type == Boolean.class || p.type == String.class || p.type == Long.class || Number.class.isAssignableFrom(p.type) || (p.type.isPrimitive())) {
                    newParams[p.name] = params[prefix + p.name]
                }
                else if (p.type == Date.class) {
                    // TODO - make sure this works when a field isn't editable or when it's nullable
                    if (params[prefix + p.name]) {
                        newParams[p.name] = Date.parse("EEE, M/d/yyyy 'at' h:mm aa",params[prefix + p.name])
                    }
                }
                else if (p.oneToMany) {
                    if (params[p.name + "_num"]) {
                        log.info "${p.name}_num - ${params[p.name + "_num"]}"
                        int num = params[p.name + "_num"].toInteger()
                        log.info("Found oneToMany - ${p.name} with $num entries - ${domainObject[p.name]} - ${domainObject[p.name]?.class?.name}")
                        def instanceMap = [:]
                        domainObject[p.name].each{ instance ->
                            instanceMap[instance.id] = instance
                        }
                        def foundIds = [] as Set
                        for (int i=0;i<num;i++) {
                            log.info "checking ${p.name}_${i}_id - " + params["${p.name}_${i}_id"]
                            if (params["${p.name}_${i}_id"] != null  && params["${p.name}_${i}_id"] != ""  && params["${p.name}_${i}_id"] != "-1") {
                                Long id = params["${p.name}_${i}_id"].toLong()
                                foundIds.add(id)
                                setFromParams(p.referencedDomainClass,instanceMap[id],"${p.name}_${i}_",params)
                                childErrors[p] = instanceMap[id].errors.allErrors
                            }
                            else if (params["${p.name}_${i}_id"] == "-1") {
                                log.info "found new oneToMany"
                                def newInstance = p.referencedDomainClass.newInstance()
                                log.info "newInstance - $newInstance"
                                setFromParams(p.referencedDomainClass,newInstance,"${p.name}_${i}_",params)
                                childErrors[p] = newInstance.errors.allErrors
                                if (domainObject[p.name] == null) {
                                    domainObject[p.name] = [] as Set
                                }
                                domainObject[p.name].add(newInstance)
                                log.info "domainClass.propertyName - ${domainClass.propertyName}"
                                newInstance[domainClass.propertyName] = domainObject
                                //def saveStatus = newInstance.save()
                            }
                        }
                        log.info("foundIds - $foundIds")
                        instanceMap.keySet().each{ key ->
                            if (!foundIds.contains(key)) {
                                log.info("deleted $key")
                                domainObject[p.name].remove(instanceMap[key])
                                instanceMap[key].delete()
                            }
                        }
                    }
                }
            }
        }
        log.info("setFromParams: newParams - $newParams")
        log.debug "Before binding - ${domainObject.errors}, domainObject - ${domainObject}"
        domainObject.properties = newParams
        log.debug "After binding - ${domainObject.errors}, domainObject - ${domainObject}"
        childErrors.each {
            def property = it.key
            it.value.each{ error->
                log.info "${property.name} - error - $error"
                if (error instanceof FieldError) {
                    def errorCode = error.code + ".child"
                    def errorArgs = [property.naturalName,error.field,error.rejectedValue] as Object[]
                    def errorDefaultMessage = error.defaultMessage
                    domainObject.errors.rejectValue(property.name, errorCode, errorArgs, errorDefaultMessage)
                }
            }
        }
        log.info("setFromParams: updatedObject - $domainObject")
    }

    static private void mergeErrors(Object parent, Object child, GrailsDomainClassProperty property) {
        if (child.hasErrors()) {
            log.info "${property.name} has errors"
            child.errors.allErrors.each{ error ->
                log.info "error - $error"
                if (error instanceof FieldError) {
                    parent.errors.rejectValue(property.name, error.code, error.arguments, error.defaultMessage)
                }
            }
        }
    }

    static private Set getDependencyFields(GrailsDomainClass domainClass) {
        log.info("getDependencyFields - ${domainClass.getPropertyValue('dependencies')?.values()}")
        def result = [] as Set
        domainClass.getPropertyValue('dependencies')?.values().each { list ->
            list.each { field ->
                result.add(field)
            }

        }
        log.info "result - $result"
        return result
    }

    static private Set getDependencyKeys(GrailsDomainClass domainClass) {
        log.info("getDependencyKeys - ${domainClass.getPropertyValue('dependencies')?.keySet()}")
        def result = [] as Set
        domainClass.getPropertyValue('dependencies')?.keySet().each { list ->
            list.each { field ->
                result.add(field)
            }

        }
        log.info "result - $result"
        return result
    }

    static boolean canTransientChange(GrailsDomainClassProperty prop, GrailsDomainClass domainClass, List persistentProperties) {
        for (entry in domainClass.getPropertyValue('dependencies')) {
            if (entry.key.contains(prop.name)) {
                for (persistentPropery in persistentProperties) {
                    if (entry.value.contains(persistentPropery.name)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    static List determineCreateFields(GrailsDomainClass domainClass) {
        def excludedProps = ['version',
                                               'id',
            Events.ONLOAD_EVENT,
            Events.BEFORE_DELETE_EVENT,
            Events.BEFORE_INSERT_EVENT,
            Events.BEFORE_UPDATE_EVENT]


        def props = []
        for (prop in domainClass.properties) {
            if (!excludedProps.contains(prop.name) && prop.persistent && (!prop.oneToMany || prop.owningSide)) {
                def cp = domainClass.constrainedProperties[prop.name]
                if (cp) {
                    if (cp.display && cp.editable) {
                        log.debug "determineCreateFields - adding persistent property ${prop.name}"
                        props.add(prop)
                    }
                }
                else {
                    log.debug "determineCreateFields - adding persistent property ${prop.name}"
                    props.add(prop)
                }
            }
        }
        log.debug "determineCreateFields - props: ${props.size()}"
        for (prop in domainClass.properties) {
            if (!excludedProps.contains(prop.name) && !prop.persistent) {
                boolean shouldAdd = canTransientChange(prop, domainClass, props)
                log.debug "determineCreateFields - shouldAdd: ${shouldAdd}, prop.name - ${prop.name}"
                if (shouldAdd) {
                    log.debug "determineCreateFields - adding transient property ${prop.name}"
                    props.add(prop)
                }
            }
        }
        log.debug "determineCreateFields - props: ${props.size()}"
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        Collections.sort(props, comparator)
        return props
    }

    static createNew(GrailsDomainClass domainClass, String field,instance) {
        log.info "createNew - domainClass: ${domainClass}, field: ${field}, instance: ${instance}"
        def methodName = 'new' + domainClass.getPropertyByName(field)?.referencedDomainClass.name
        log.info "createNew - methodName: ${methodName}"
        MetaClass metaClass = domainClass.getMetaClass()
        for (method in metaClass.getMethods()) {
            if (method.name == methodName) {
                log.info "found method ${method.name}"
                return method.invoke(instance)
            }
        }
        return [:]
    }

    // TODO filterableStrings needs to take into account toStringFields for manytoones.  Could be recursive

    static getCharacteristics(String theClass) {
        GrailsDomainClass domainClass = ApplicationHolder.application.getArtefact("Domain", theClass)
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        boolean hasFilterableDate = false
        boolean hasFilterableStrings = false
        String defaultSortFieldNames
        boolean sortFieldSpecialCase = false
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        List filterableStrings = filterableStringFields(domainClass).collect{"'$it'"}
        hasFilterableStrings = filterableStrings.size() > 0
        def specialSortFields = []
        defaultSortFieldNames = defaultSortField(domainClass).join(',')
        props.eachWithIndex { p,i ->
            if (i < 3 && isTemporal(p)) {
                hasFilterableDate = true
            }
            if (!p.persistent) {
                specialSortFields.add("'${p.name}'")
            } 
        }
        def response = ['hasFilterableDate':hasFilterableDate,
                        'hasFilterableStrings':hasFilterableStrings,
                        'defaultSortField':"'$defaultSortFieldNames'",
                        'filterableStrings':filterableStrings,
                        'specialSortFields':specialSortFields]
        log.debug "getCharacteristics - theClass: $theClass, response: $response"
        return response
    }

    static List filterableStringFields(GrailsDomainClass domainClass) {
        List results = []
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        props.eachWithIndex { p,i ->
            if (i < 5) {
                results.addAll(filterableStringFields(p))
            }
        }
        return results
    }

    static List filterableStringFields(GrailsDomainClassProperty property) {
        if (property.type == String.class && property.isPersistent()) {
            return [property.name]
        }
        List results = []
        if (property.manyToOne) {
            List toStringFields = property.referencedDomainClass.getPropertyValue('toStringFields')
            if (toStringFields) {
                toStringFields.each { toStringField ->
                    GrailsDomainClassProperty subProperty = property.referencedDomainClass.getPropertyByName(toStringField)
                    log.debug "filterableStringFields - checking ${property.name}"
                    results.addAll(filterableStringFields(subProperty).collect{property.name + '.' + it})
                }
            }
        }
        return results
    }

    // TODO - use toStringFields if provided
    // TODO - I think this needs to return a list of strings so can prefix all returned fields correctly
    // Any temporal field has precedence, so need to first search for temporal field
    // an alternative is to replace the defaultSortField previously found if find a temporal.  but then need to pass flag back
    static List defaultSortField(GrailsDomainClass domainClass) {
        String temporal = temporalField(domainClass)
        if (temporal) {
            log.debug "defaultSortField - temporal field found: $temporal"
            return [temporal]
        }
        List toStringFields = domainClass.getPropertyValue('toStringFields')
        if (toStringFields) {
            List defaultSortFields = []
            log.debug "defaultSortField - ${domainClass.name} has toStringFields"
            // TODO for each toStringField, get property, find default, build list of strings and join with ,
            toStringFields.each { toStringField ->
                GrailsDomainClassProperty property = domainClass.getPropertyByName(toStringField)
                log.debug "defaultSortField - checking ${property.name}"
                defaultSortFields.add(defaultSortField(property).join(','))
            }
            log.debug "defaultSortField - got ${defaultSortFields}"
            return defaultSortFields

        }
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        GrailsDomainClassProperty defaultSortFieldProperty = props[0]
        if (defaultSortFieldProperty.isAssociation()) {
            return defaultSortField(defaultSortFieldProperty).collect{defaultSortFieldProperty.name + '.' + it}
        }
        return [defaultSortFieldProperty.name]
    }

    static List defaultSortField(GrailsDomainClassProperty property) {
        if (!property.isAssociation()) {
            log.debug "defaultSortField - property.name: ${property.name}, is not association"
            return [property.name]
        }
        return defaultSortField(property.referencedDomainClass).collect{property.name + '.' + it}
    }

    static boolean isTemporal(GrailsDomainClassProperty property) {
        if (property.type == Date.class) {
            return true
        }
        if (!property.manyToOne) {
            return false
        }
        GrailsDomainClass domainClass = property.referencedDomainClass
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name)}
        Collections.sort(props, comparator)
        boolean temporal = false
        props.eachWithIndex { p,i ->
            if (i < 3 && isTemporal(p)) {
                temporal = true
            }
        }
        return temporal
    }

    static String temporalField(GrailsDomainClass domainClass) {
        DomainClassPropertyComparator comparator = new DomainClassPropertyComparator(domainClass)
        String result
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) && !Collection.class.isAssignableFrom(it.type)}
        Collections.sort(props, comparator)
        props.eachWithIndex { prop,i ->
            if (i < 3) {
                if (prop.type == Date.class) {
                    result = prop.name
                }
                else if (prop.manyToOne && result == null) {
                    result = temporalField(prop.referencedDomainClass)
                    if (result) {
                        result = prop.name + '.' + result
                    }
                }
            }
        }
        return result
    }

    static boolean canBeCreatedBy(GrailsDomainClass domainClass, GrailsDomainClassProperty property) {
        boolean canCreate = property.oneToMany && !property.owningSide
        if (canCreate) {
            property.referencedDomainClass.persistentProperties.each { p ->
                if (p.manyToOne && p.referencedDomainClass != domainClass) {
                    if (p.otherSide.owningSide) {
                        canCreate = false
                    }
                    log.info "canBeCreatedBy - domainClass.name: ${domainClass.name}, property.name: ${property.name}, p.name: ${p.name}, p.owningSide: ${p.owningSide}, p.otherSide.owningSide: ${p.otherSide.owningSide}"
                }

            }
        }
        return canCreate
    }

    static List dynamicManyToOnes(GrailsDomainClass domainClass) {
        def dependents = getDependencyKeys(domainClass)
        def results = []
        def props = domainClass.properties.findAll { (it.manyToOne || it.oneToOne) && dependents.contains(it.name)}
        for (prop in props) {
            results.add([prop])
        }
        for (property in domainClass.properties) {
            if (property.oneToMany) {
                GrailsDomainClass subdomainClass = property.referencedDomainClass
                Set subdependents = getDependencyKeys(subdomainClass)
                def subprops = subdomainClass.properties.findAll { (it.manyToOne || it.oneToOne) && subdependents.contains(it.name)}
                for (prop in subprops) {
                    results.add([property,prop])
                }
            }
        }
        if (!results.empty) {
            log.info "dynamicManyToOnes - domainClass - $domainClass, results - $results"
        }
        return results
    }

}

