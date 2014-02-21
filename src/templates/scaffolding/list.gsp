<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<%=packageName%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>List ${className}s</title>
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
            <div class="list">
              <%
                            hasFilterableDate = false
                            hasFilterableName = false
                            props = Scaffolding.listableFields(domainClass)
                            props.eachWithIndex { p,i ->
                              if (i < 3 && p.type == Date.class ) {
                                hasFilterableDate = true
                              }
                            }
              %>
              <!--
              params - \${params} <p>
              max - \${params.max} <p>
              num ${className} - \${${className}.count()} <p>
              numPages - \${Math.ceil(${className}.count()/10)} <p>
              numPagesEnough - \${Math.ceil(${className}.count()/10) > 2.0} <p>
              shownParams - \${shownParams} <p>
              showStringFilter - \${showStringFilter} <p>
              -->

              <div class="filter ui-widget-header ui-helper-clearfix">
                <g:form name="filterForm" action="list" method="get">
                  <div class="list-filter-control">
                  <g:if test="\${shownParams.sort}">
                    <input type="hidden" name="sort" value="\${shownParams.sort}"/>
                  </g:if>
                  <g:if test="\${shownParams.order}">
                    <input type="hidden" name="order" value="\${shownParams.order}"/>
                  </g:if>
              <g:if test="\${showDateFilter}">
                  All
                  <g:radio value="All" name="when" checked="\${allSelected}" class="ui-state-default" title="Display all ${className}s" onClick="this.form.submit()"/>
                  Future
                  <g:radio value="Future" name="when" checked="\${futureSelected}" class="ui-state-default" title="Only display future ${className}s" onClick="this.form.submit()"/>
                  Past
                  <g:radio value="Past" name="when" checked="\${pastSelected}" class="ui-state-default" title="Only display past ${className}s" onClick="this.form.submit()"/>
              </g:if>

              <g:if test="\${showStringFilter}">
                  <g:textField name="filter" value="\${params.filter}" title="Only display ${className}s that match text entered here" style="display: inline-block;width: 50%;"/>
                  <button type="submit" class="ui-state-default" style="display: inline-block;">Filter</button>
                  <button type="submit" class="ui-state-default" style="display: inline-block;" onclick="document.filterForm.filter.value=''">Reset</button>
              </g:if>
                  </div>
                  <div class="list-length-control">
                Show <select name='max' class="ui-state-default" onchange="this.form.submit()">
                <option \${params.max == 10 ? 'selected' : 'notSelected'}>10</option>
                <option \${params.max == 20 ? 'selected' : 'notSelected'}>20</option>
                <option \${params.max == 50 ? 'selected' : 'notSelected'}>50</option>
                <option \${params.max == 100 ? 'selected' : 'notSelected'}>100</option>
              </select> entries
                  </div>
                </g:form>
              </div>

                <table class="main">
                    <thead>
                        <tr>
                        <%
                        // Hmm.  Sort field will no longer just be the field but a dot notation subfield
                        // Or the controller can translate it from the field to the subfield.
                        // but if done here at build time, less to do at runtime
                        // I think that makes the most sense.  Can have a Scaffolding routine that is called
                        // from here that does the translation.  Should only need the prop
                            props.eachWithIndex { p,i ->
                   	            if(i < 16) {
                                      if (p.type == Boolean.class) { %>
                                <g:sortableColumn2 property="${Scaffolding.defaultSortField(p).join(',')}" title="${p.naturalName}?" params="\${shownParams}"/>
                            <%          } else { %>
                                <g:sortableColumn2 property="${Scaffolding.defaultSortField(p).join(',')}" title="${p.naturalName}" params="\${shownParams}"/>
                        <%}   }   } %>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
                        <tr class="ui-widget-content \${(i % 2) == 0 ? 'odd' : 'even'}">
                        <%  props.eachWithIndex { p,i ->
                                 if(i < 16) {
                                   cp = domainClass.constrainedProperties[p.name]
                                   if (p.type == Date.class) {%>
                            <td><g:link action="edit" id="\${${propertyName}.id}"><g:formatDate format="EEE, MM/dd/yy 'at' h:mm a" date="\${${propertyName}?.${p.name}}"/></g:link></td>
                            <%} else if (cp?.format) {%>
                            <td class="test">
                              <g:link action="edit" id="\${${propertyName}.id}" title="Click here to edit this ${className}">
                                <g:if test="\${${propertyName}?.${p.name}}">
                                  <g:formatNumber2 format="${cp.format}" number="\${${propertyName}?.${p.name}}"/>
                                </g:if>
                              </g:link>
                            </td>
                            <%} else if (cp?.format) {%>
                            <td class="test"><g:link action="edit" id="\${${propertyName}.id}" title="Click here to edit this ${className}"><g:formatNumber2 format="${cp.format}" number="\${${propertyName}?.${p.name}}"/></g:link></td>
                            <%} else {%>
                            <td><g:link action="edit" id="\${${propertyName}.id}" title="Click here to edit this ${className}">\${fieldValue(bean:${propertyName}, field:'${p.name}')}</g:link></td>
                        <%  } }  } %>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
          <g:if test="\${${propertyName}Total > params.max}">
            <div class="pagination ui-widget-header">
              <div class="pagination-buttons ui-state-default">
                <g:paginate total="\${${propertyName}Total}" params="\${shownParams}"/>
              </div>
            </div>
          </g:if>
            <div class="action ui-widget-header">
                    <button type="button" class="ui-state-default linkButton" title="Click here to create a new ${className}" value="\${createLink(action:'create')}">
                      New ${className}
                    </button>
            </div>
        </div>
    </body>
</html>
