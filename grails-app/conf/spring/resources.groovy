import grails.util.GrailsUtil
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.apache.shiro.authc.credential.Md5CredentialsMatcher
// Place your Spring DSL code here
beans = {
    if (GrailsUtil.environment != GrailsApplication.ENV_TEST) {
        dataSource(ComboPooledDataSource) { bean ->
            bean.destroyMethod = 'close'
            user = CH.config.dataSource.username
            password = CH.config.dataSource.password
            driverClass = CH.config.dataSource.driverClassName
            jdbcUrl = CH.config.dataSource.url
            maxPoolSize = 50
            maxStatements = 180
            maxConnectionAge = 4 * 60 * 60 // 4 hours
            //maxIdleTime = 1 * 60 * 60 // 1 hour
        }
    }
    credentialMatcher(Md5CredentialsMatcher) { storedCredentialsHexEncoded = true }

}
