<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<%=packageName%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="print" />
        <link rel="stylesheet" href="\${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="\${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <title>Print ${className}</title>
    </head>
    <body onload="window.print()">
        <div class="body print">
                    <%
                        excludedProps = ['version',
                                         'id',
                                           Events.ONLOAD_EVENT,
                                           Events.BEFORE_DELETE_EVENT,
                                           Events.BEFORE_INSERT_EVENT,
                                           Events.BEFORE_UPDATE_EVENT]
                        props = domainClass.properties.findAll { !excludedProps.contains(it.name)}
                        Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                    %>
          <h1>\${${propertyName}}</h1>
            <g:if test="\${flash.message}">
            <div class="message">\${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table class="print">
                    <tbody>
                       <%
                        props.each { p ->
                                   cp = domainClass.constrainedProperties[p.name] %>
                        <tr class="prop">
                          <%if(!Collection.class.isAssignableFrom(p.type)) {%>
                            <td valign="top" class="name">${p.naturalName}:</td>
                            <% }
                               if(p.isEnum()) { %>
                            <td valign="top" class="value">\${${propertyName}?.${p.name}?.encodeAsHTML()}</td>
                            <% } else if(Collection.class.isAssignableFrom(p.type)) { %>
                            <% } else if(p.oneToMany || p.manyToMany) { %>
                            <td  valign="top" style="text-align:left;" class="value">
                                <g:each var="${p.name[0]}" in="\${${propertyName}.${p.name}}">
                                    \${${p.name[0]}?.encodeAsHTML()}
                                </g:each>
                            </td>
                            <%  } else if(p.manyToOne || p.oneToOne) { %>
                            <td valign="top" class="value">\${${propertyName}?.${p.name}?.encodeAsHTML()}</td>
                            <%  } else if (p.type == Date.class) {%>
                            <td><g:formatDate format="EEE, MM/dd/yy 'at' h:mm a" date="\${${propertyName}?.${p.name}}"/></td>
                            <%} else if (cp?.format) {%>
                            <td class="test">
                                <g:if test="\${${propertyName}?.${p.name}}">
                                  <g:formatNumber2 format="${cp.format}" number="\${${propertyName}?.${p.name}}"/>
                                </g:if>
                            </td>
                            <%  } else  { %>
                            <td valign="top" class="value">\${fieldValue(bean:${propertyName}, field:'${p.name}')}</td>
                            <%  } %>
                        </tr>
                    <%  } %>
                    </tbody>
                </table>
                       <%
                        props.each { p ->
                                   cp = domainClass.constrainedProperties[p.name]
                                   if (p.oneToMany || p.manyToMany) {
                                     def exclude = []
                                     exclude.add(domainClass)
                                     def fields = Scaffolding.describe(p.referencedDomainClass, exclude)
                                     %>
              <g:if test="\${!${propertyName}.${p.name}.empty}">
                <table class="print">
                    <thead>
                        <tr>
                        <%
                            fields.values().each { f ->  %>
                                <th valign="top" class="namePrint">${f.naturalName}</th>
                        <%  } %>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each var="r" status="i" in="\${${propertyName}.${p.name}}">
                                    <tr class="\${(i % 2) == 0 ? 'odd' : 'even'}">
                                      <% fields.keySet().each { f ->
                                   def fp = fields.get(f)
                                   def fcp = fp.domainClass.constrainedProperties[fp.name]
                                   if (fp.type == Date.class) {%>
                            <td valign="top" class="namePrint">&nbsp;<g:formatDate format="EEE, MM/dd/yy 'at' h:mm a" date="\${r?.${f}}"/></td>
                            <%} else if (fcp?.format) {%>
                            <td valign="top" class="namePrint">&nbsp;
                                <g:if test="\${r?.${f}}">
                                  <g:formatNumber2 format="${fcp.format}" number="\${r?.${f}}"/>
                                </g:if>
                            </td>
                            <% } else { %>
                                        <td valign="top" class="namePrint">&nbsp;\${r?.${f}}</td>
                                      <% }} %>
                                    </tr>
                                </g:each>
                    </tbody>
                </table>
              </g:if>
                       <% } } %>
            </div>
        </div>
<script type="text/javascript">
        function PrintWindow()
        {
           window.print();
           CheckWindowState();
        }

        function CheckWindowState()
        {
            if(document.readyState=="complete")
            {
                window.close();
            }
            else
            {
                setTimeout("CheckWindowState()", 2000)
            }
        }

       PrintWindow();
</script>
    </body>
</html>
