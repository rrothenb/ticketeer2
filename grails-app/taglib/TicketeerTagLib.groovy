import java.text.DecimalFormatSymbols
import org.springframework.web.servlet.support.RequestContextUtils as RCU

import java.text.DateFormat
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.springframework.beans.SimpleTypeConverter

class TicketeerTagLib {
    def formatNumber2 = { attrs ->

        if (!attrs.containsKey('number'))
            throwTagError("Tag [formatNumber] is missing required attribute [number]")

        def number = attrs.get('number')
        if (number == null) return
        else if(!(number instanceof Number)) {
            number = number.toString().toDouble()
        }

        def formatName = attrs.get('formatName')
        def format = attrs.get('format')

        if(!format && formatName) { 
            format = message(code:formatName)
            if(!format)
            throwTagError("Attribute [formatName] of Tag [formatNumber] specifies a format key [$formatName] that does not exist within a message bundle!")
        } else if (!format) {
            format = message(code: "number.format", default: message(code: "default.number.format", default: "0"))
        }

        if (format == "#" && number == 0) {
            return
        }

        def decimalFormat = new java.text.DecimalFormat( format )

        out << decimalFormat.format((Double)number)
    }

    def checkBox2 = {attrs ->
        log.debug attrs
        attrs.id = attrs.id ? attrs.id : attrs.name
        def value = attrs.remove('value')
        def name = attrs.remove('name')
        def disabled = attrs.remove('disabled')
        if (disabled && Boolean.valueOf(disabled)) {
            attrs.disabled = 'disabled'
        }

        // Deal with the "checked" attribute. If it doesn't exist, we
        // default to a value of "true", otherwise we use Groovy Truth
        // to determine whether the HTML attribute should be displayed
        // or not.

        def checked = true
        if (attrs.containsKey('checked')) {
            checked = attrs.remove('checked')
        }

        if (checked instanceof String) checked = Boolean.valueOf(checked)

        if (value == null) value = false

        out << "<input type=\"checkbox\" name=\"${name}\" "
        if (value && checked) {
            out << 'checked="checked" '
        }
        def outputValue = !(value instanceof Boolean || value?.class == boolean.class)
        if (outputValue) out << "value=\"${value}\" "
        // process remaining attributes
        outputAttributes(attrs)

        // close the tag, with no body

        out << ' />'

    }

    void outputAttributes(attrs)
    {
        attrs.remove('tagName') // Just in case one is left
        def writer = getOut()
        attrs.each {k, v ->
            writer << "$k=\"${v.encodeAsHTML()}\" "
        }
    }

    def TicketeerDatePicker = { attrs ->
        def value = (attrs['value'] ? attrs['value'] : new Date())
        def name = attrs['name']
        def c = null
        if(value instanceof Calendar) {
        	c = value
        }
        else {
	        c = new GregorianCalendar();
	        c.setTime(value)
        }
        def day = c.get(GregorianCalendar.DAY_OF_MONTH)
        def month = c.get(GregorianCalendar.MONTH)
        def year = c.get(GregorianCalendar.YEAR)
        def hour = c.get(GregorianCalendar.HOUR)
        def minute = c.get(GregorianCalendar.MINUTE)
        def am_pm = c.get(GregorianCalendar.AM_PM)
	def dfs = new java.text.DateFormatSymbols()

        out << "<input type='hidden' name='${name}' value='struct' />"

        // create month select
        out.println "<select name='${name}_month'>"
        dfs.months.eachWithIndex { m,i ->
            if(m) {
                def monthIndex = i + 1
                out << "<option value='${monthIndex}'"
                if(month == i) out << 'selected="selected"'
                out << '>'
                out << monthIndex
                out.println '</option>'
            }
        }
        out.println '</select>'
        // create day select
        out.println "/<select name='${name}_day'>"

        for(i in 1..(day-1)) {
               out.println "<option value='${i}'>${i}</option>"
        }
        out.println "<option value='${day}' selected='selected'>${day}</option>"
        for(i in (day+1)..31) {
               out.println "<option value='${i}'>${i}</option>"
        }
        out.println '</select>'
		// create year select
		out.println "/<select name='${name}_year'>"
        out.println "<option value='${year}' selected='selected'>${year.toString().substring(2,4)}</option>"
        out.println "<option value='${year + 1}'>${(year + 1).toString().substring(2,4)}</option>"
		out.println '</select>'
		// do hour select
		out.println "<select name='${name}_hour'>"
        for(i in 1..12) {
            def i2 = i==12 ? 0 : i
			def h = '' + i
            def h2 = '' + i2
			out << "<option value='${h2}' "
			if(hour == i2) out << "selected='selected'"
			out << '>' << h << '</option>'
			out.println()
		}
		out.println '</select> :'

		// do minute select
		out.println "<select name='${name}_minute'>"
        for(i in [0,30]) {
			def m = '' + i
			if(i < 10) m = '0' + m
			out << "<option value='${m}' "
			if(minute == i) out << "selected='selected'"
			out << '>' << m << '</option>'
			out.println()
		}
		out.println '</select>'

		// do AM/PM select
		out.println "<select name='${name}_am_pm'>"
        for(i in [0,1]) {
            def am_pm_string = i == 0 ? 'AM' : 'PM'
			out << "<option value='${am_pm_string}' "
			if(am_pm == i) out << "selected='selected'"
			out << '>' << am_pm_string << '</option>'
			out.println()
		}
		out.println '</select>'
    }
    def sortableColumn2 = { attrs ->
        def writer = out
        if(!attrs.property)
            throwTagError("Tag [sortableColumn] is missing required attribute [property]")

        if(!attrs.title && !attrs.titleKey)
            throwTagError("Tag [sortableColumn] is missing required attribute [title] or [titleKey]")

        def property = attrs.remove("property")

        def action = attrs.action ? attrs.remove("action") : (actionName ?: "list")

        def defaultOrder = attrs.remove("defaultOrder")

        if(defaultOrder != "desc")
            defaultOrder = "asc"

        // current sorting property and order
        def sort = params.sort
        def order = params.order

        // add sorting property and params to link params
        def linkParams = [:]
        if(params.id)
            linkParams.put("id",params.id)
        if(attrs.params)
            linkParams.putAll(attrs.remove("params"))
        linkParams.sort = property

        // determine and add sorting order for this column to link params
        attrs.class = "ui-icon "
        if(property == sort) {
            if(order == "asc") {
                linkParams.order = "desc"
                attrs.class = attrs.class + " ui-icon-triangle-1-n "
            }
            else {
                linkParams.order = "asc"
                attrs.class = attrs.class + " ui-icon-triangle-1-s "
            }
        }
        else {
            linkParams.order = defaultOrder
            attrs.class = attrs.class + " ui-icon-carat-2-n-s "
        }

        // determine column title
        def title = attrs.remove("title")
        def titleKey = attrs.remove("titleKey")
        if(titleKey) {
            if(!title)
                title = titleKey
            def messageSource = grailsAttributes.messageSource
            def locale = RCU.getLocale(request)
            title = messageSource.getMessage(titleKey, null, title, locale)
        }

        writer << "<th class=\"ui-state-default\">"
        writer << "<span "
        // process remaining attributes
        attrs.each { k, v ->
            log.debug "sortableColumn2 - $k: $v"
            writer << "${k}=\"${v.encodeAsHTML()}\" "
        }
        writer <<  " style=\"float:right;\"></span>${link(action:action, params:linkParams) { title }}"
        writer << "</th>"
    }

    /**
     * A helper tag for creating HTML selects
     *
     * Examples:
     * <g:select name="user.age" from="${18..65}" value="${age}" />
     * <g:select name="user.company.id" from="${Company.list()}" value="${user?.company.id}" optionKey="id" />
     * Adding parameters rejectIf(closure) and rejectClass(String)
     * To start, just mark stuff with rejectClass if pass rejectIf
     * If valueProvided is false, then default to first entry that passes the rejectIf closure (if provided)
     */
    def select2 = {attrs ->
        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)
        def writer = out
        attrs.id = attrs.id ? attrs.id : attrs.name
        def from = attrs.remove('from')
        def keys = attrs.remove('keys')
        def optionKey = attrs.remove('optionKey')
        def optionValue = attrs.remove('optionValue')
        def valueProvided = attrs.remove('valueProvided')
        def value = attrs.remove('value')
        def valueInstance = attrs.remove('valueInstance')
        if (value instanceof Collection) {
            attrs.multiple = true
        }
        def valueMessagePrefix = attrs.remove('valueMessagePrefix')
        def noSelection = attrs.remove('noSelection')
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }
        def rejectIf = attrs.remove("rejectIf")
        def rejectClass = attrs.remove("rejectClass")

        writer << "<select name=\"${attrs.remove('name')}\" "
        // process remaining attributes
        outputAttributes(attrs)

        writer << '>'
        writer.println()

        if (noSelection) {
            renderNoSelectionOption(noSelection.key, noSelection.value, value)
            writer.println()
        }

        // create options from list
        boolean defaultSelected = false
        if (from) {
            if (value) {
                if (!from.contains(valueInstance)) {
                    from.add(0,valueInstance)
                    log.debug "*** select2 - from: $from"
                }
            }
            from.eachWithIndex {el, i ->
                log.debug "*** select2 - from[$i]: $el"
                def keyValue = null
                writer << '<option '
                if (keys) {
                    keyValue = keys[i]
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                }
                else if (optionKey) {
                    if (optionKey instanceof Closure) {
                        keyValue = optionKey(el)
                    }
                    else if (el != null && optionKey == 'id' && grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, el.getClass().name)) {
                        keyValue = el.ident()
                    }
                    else {
                        keyValue = el[optionKey]
                    }
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                }
                else {
                    keyValue = el
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                }
                if (rejectIf) {
                    if (rejectIf(el)) {
                        if (rejectClass) {
                            writer << " class=\"$rejectClass\""
                        }
                    }
                    else {
                        if (!valueProvided && !defaultSelected) {
                            defaultSelected = true
                            writer << 'selected="selected"'
                        }
                    }
                }
                writer << '>'
                if (optionValue) {
                    if (optionValue instanceof Closure) {
                        writer << optionValue(el).toString().encodeAsHTML()
                    }
                    else {
                        writer << el[optionValue].toString().encodeAsHTML()
                    }
                }
                else if (valueMessagePrefix) {
                    def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        writer << message.encodeAsHTML()
                    }
                    else if (keyValue) {
                        writer << keyValue.encodeAsHTML()
                    }
                    else {
                        def s = el.toString()
                        if (s) writer << s.encodeAsHTML()
                    }
                }
                else {
                    def s = el.toString()
                    if (s) writer << s.encodeAsHTML()
                }
                writer << '</option>'
                writer.println()
            }
        }
        // close tag
        writer << '</select>'
    }

    def renderNoSelectionOption = {noSelectionKey, noSelectionValue, value ->
        // If a label for the '--Please choose--' first item is supplied, write it out
        out << '<option value="' << (noSelectionKey == null ? "" : noSelectionKey) << '"'
        if (noSelectionKey.equals(value)) {
            out << ' selected="selected" '
        }
        out << '>' << noSelectionValue.encodeAsHTML() << '</option>'
    }
    
    def typeConverter = new SimpleTypeConverter()
    private writeValueAndCheckIfSelected(keyValue, value, writer) {

        boolean selected = false
        def keyClass = keyValue?.getClass()
        if (keyClass.isInstance(value)) {
            selected = (keyValue == value)
        }
        else if (value instanceof Collection) {
            selected = value.contains(keyValue)
        }
        else if (keyClass && value) {
            try {
                value = typeConverter.convertIfNecessary(value, keyClass)
                selected = (keyValue == value)
            } catch (Exception) {
                // ignore
            }
        }
        writer << "value=\"${keyValue}\" "
        if (selected) {
            writer << 'selected="selected" '
        }
    }
}
