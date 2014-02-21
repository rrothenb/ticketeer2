<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title>Login</title>
</head>
<body>
  <div class="body ui-widget ui-helper-reset">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:form action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
    <div class="main dialog">
    <table class="ui-widget-content">
      <tbody>
        <tr class="prop">
          <td valign="top" class="name"><label for="username">Username:</label></td>
          <td class="value"><input type="text" name="username" value="${username}" /></td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><label for="password">Password:</label></td>
          <td class="value"><input type="password" name="password" value="" /></td>
        </tr>
        <tr class="prop">
          <td valign="top" class="name"><label for="rememberMe">Remember me?:</label></td>
          <td class="value"><g:checkBox name="rememberMe" value="${rememberMe}" /></td>
        </tr>
      </tbody>
    </table>
    </div>
    <div class="action ui-widget-header">
      <input class="ui-state-default" type="submit" value="Sign in" />
    </div>
  </g:form>
  </div>
</body>
</html>
