<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<%=packageName%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ${className}</title>
        <script>
          baseUrl = "<g:resource/>";
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
            <g:form action="save" method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
                <div class="main dialog">
                    <table class="ui-widget-content">
                        <tbody>
                        <%
                            props = Scaffolding.determineCreateFields(domainClass)
                            props.each { p -> %>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="${p.name}">${p.naturalName}:</label>
                                </td>
                                <td valign="middle" class="value \${hasErrors(bean:${propertyName},field:'${p.name}','errors')}">
                                    ${renderEditor(p)}
                                </td>
                            </tr> 
                        <%  } %>
                        </tbody>
                    </table>
                </div>
                <div class="action ui-widget-header">
                    <input class="ui-state-default" type="submit" value="Create" />
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
