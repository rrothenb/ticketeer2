<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<%=packageName%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit ${className}</title>
        <script>
          var baseUrl = "<g:resource/>";
        </script>
    </head>
    <body>
      <div class="body ui-widget ui-helper-reset">
        <g:if test="\${flash.message}">
          <div class="message ui-state-highlight">
            <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>\${flash.message}</p>
          </div>
        </g:if>
            <g:if test="\${flash.error}">
            <div class="message ui-state-error">
              <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>\${flash.error}</p>
            </div>
            </g:if>
            <g:hasErrors bean="\${${propertyName}}">
            <div class="message ui-state-error">
                <g:renderErrors bean="\${${propertyName}}" as="list" />
            </div>
            </g:hasErrors>
          <!--
          oneToManys - "\${oneToManys}" <p>
          manyToOnes - "\${manyToOnes}" <p>
          -->
            <g:form method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
                <input type="hidden" name="id" value="\${${propertyName}?.id}" />
                <input type="hidden" name="version" value="\${${propertyName}?.version}" />
                <div class="main dialog">
                    <table class="ui-widget-content">
                        <tbody>
                        <%
                            excludedProps = ['version',
                                             'id',
                                               Events.ONLOAD_EVENT,
                                               Events.BEFORE_DELETE_EVENT,
                                               Events.BEFORE_INSERT_EVENT,
                                               Events.BEFORE_UPDATE_EVENT]
                            props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
                            
                            Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                            props.each { p ->
                                cp = domainClass.constrainedProperties[p.name]
                                display = (cp ? cp.display : true)
                                title = ""
                                if (cp.hasAppliedConstraint("description")) {
                                  title = cp.getAppliedConstraint("description").toString()
                                }
                                if(display) { %>
                            <tr class="prop">
                                <td valign="top" class="name" title="${title}">
                                    <label for="${p.name}">${p.naturalName}:</label>
                                </td>
                                <td valign="middle" class="value \${hasErrors(bean:${propertyName},field:'${p.name}','errors')}" title="${title}">
                                    ${renderEditor(p)}
                                </td>
                            </tr> 
                        <%  }   } %>
                        </tbody>
                    </table>
                </div>
                <div class="action ui-widget-header">
                    <g:actionSubmit class="ui-state-default" value="Update" title="Save your changes to this ${className}"/>
                    <%
                    if (!domainClass.clazz.interfaces*.name.contains('SingletonDomainClass')) {
                    %>
                    <button type="submit" class="ui-state-default" onclick="return confirm('Are you sure?');" title="Delete this ${className}">
                      <span class="ui-icon ui-icon-close" style="display:inline-block"></span>
                      Delete
                    </button>
                    <button type="button" class="ui-state-default linkButton" value="\${createLink(action:'create')}" title="Create a new ${className}">
                      New ${className}
                    </button>
                    <%
                    }
                    %>
                    <%
                    props.each { p ->
                      if (Scaffolding.canBeCreatedBy(domainClass, p)) {

                    %>
                    <button type="button" class="ui-state-default linkButton" value="\${createLink(action:'create',controller:'${p.referencedDomainClass.propertyName}')}?${domainClass.propertyName}.id=\${${propertyName}?.id}&${domainClass.propertyName}_id=\${${propertyName}?.id}" title="Create a new ${p.referencedDomainClass.naturalName}">
                      Add ${p.referencedDomainClass.naturalName}
                    </button>
                    <%
                    } }
                    %>
                    <button type="button" class="ui-state-default linkButton" value="\${createLink(action:'print',id:${propertyName}.id)}" title="Format the current ${className} for printing">
                      Print
                    </button>
                </div>
            </g:form>
        </div>
      <div id="dateTimeDialog" title="Select Date and Time" style="display:none;">
        <div id="datepicker"></div>
        <form style="margin-left:auto;margin-right:auto;width:60%;">
          <select name='dateTime_hour' id='dateTime_hour' class="ui-state-default">
            <option value='1' >1</option>
            <option value='2' >2</option>
            <option value='3' >3</option>

            <option value='4' >4</option>
            <option value='5' >5</option>
            <option value='6' >6</option>
            <option value='7' >7</option>
            <option value='8' >8</option>
            <option value='9' >9</option>
            <option value='10' >10</option>
            <option value='11' >11</option>
            <option value='12' >12</option>

          </select> :
          <select name='dateTime_minute' id='dateTime_minute' class="ui-state-default">
            <option value='00' >00</option>
            <option value='30' >30</option>
          </select>
          <select name='dateTime_am_pm' id='dateTime_am_pm' class="ui-state-default">
            <option value='AM' >AM</option>
            <option value='PM' >PM</option>
          </select>
        </form>
      </div>
    </body>
</html>
