<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <link type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/themes/ui-lightness/jquery-ui.css" rel="stylesheet" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.2/jquery-ui.min.js"></script>
        <script type="text/javascript" src="${resource(dir:'js',file:'scaffolding.js')}"></script>
        <g:layoutHead />
    </head>
    <body class="ui-helper-reset">
                <div class="ui-widget tabs">
                    <table class="ui-state-default">
                        <tbody>
                            <tr class="${pageProperty(name:'title') ==~ /Help/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <a href="${resource(file:'help.gsp')}" title="View Ticketeer Blog">Help</a>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Reservation.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="reservation" title="Show the list of Reservations">Reservations<span class="ui-icon ui-icon-plus" title="Create a new Reservation" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Show.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="show" title="Show the list of Shows">Shows<span class="ui-icon ui-icon-plus" title="Create a new Show" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Performance.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="performance" title="Show the list of Performances">Performances<span class="ui-icon ui-icon-plus" title="Create a new Performance" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Sale.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="sale" title="Show the list of Sales">Sales<span class="ui-icon ui-icon-plus" title="Create a new Sale" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Price.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="price" title="Show the list of Prices">Prices<span class="ui-icon ui-icon-plus" title="Create a new Price" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Customer.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="customer" title="Show the list of Customers">Customers<span class="ui-icon ui-icon-plus" title="Create a new Customer" /></g:link>
                                </td>
                            </tr>
                                <tr class="${pageProperty(name:'title') ==~ /.* Transaction.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="transaction" title="Show the list of Transactions">Transactions<span class="ui-icon ui-icon-plus" title="Create a new Transaction" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Merchandise.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="merchandise" title="Show the list of Merchandise">Merchandise<span class="ui-icon ui-icon-plus" title="Create new Merchandise" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Tax Code.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="taxCode" title="Show the list of Tax Codes">Tax Codes<span class="ui-icon ui-icon-plus" title="Create a new Tax Code" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Register.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="register" title="Show the list of Registers">Registers<span class="ui-icon ui-icon-plus" title="Create a new Register" /></g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /.* Theater.*/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="theater" action="edit" id="1" title="Show the settings for the Theater">Theater</g:link>
                                </td>
                            </tr>
                            <tr class="${pageProperty(name:'title') ==~ /Login/ ? 'ui-state-active' : 'ui-state-default'}">
                                <td valign="middle">
                                  <g:link controller="auth" action="login" title="Login">Login</g:link>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

        <g:layoutBody />
        <g:if test="${grailsApplication.config.google.analytics.profile.code}">
          <script type="text/javascript">
            var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
            document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
          </script>
          <script type="text/javascript">
            try {
              var pageTracker = _gat._getTracker("${grailsApplication.config.google.analytics.profile.code}");
              pageTracker._trackPageview();
              var user = "${request.remoteUser}";
              user = user.replace(/@.*$/,"")
              var message = "${flash.message}";
              if (user.length > 0) {
                pageTracker._setCustomVar(1,"user",user,1);
              }
              if (message.length > 0) {
                pageTracker._setCustomVar(2,"message",message,3);
              }
		<g:if test="${exception}">
                  pageTracker._setCustomVar(3,"exception","${exception.message?.encodeAsHTML()}",3);
		</g:if>
            } catch(err) {}
          </script>
        </g:if>
  </body>
</html>