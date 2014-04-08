package fi.nls.oskari.jetty;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.naming.NamingException;

public class JettyRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("JettyRunner.main");

        Server server = new Server(2373);
        WebAppContext servletContext = createServletContext();
        server.setHandler(servletContext);
        server.start();
        server.join();
    }

    private static WebAppContext createServletContext() throws NamingException {
        WebAppContext servletContext = new WebAppContext();
        servletContext.setConfigurationClasses(new String[]{"org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration"});
        servletContext.setResourceBase("src/main/webapp");
        servletContext.setContextPath("/");
        servletContext.addServlet(DebugServlet.class, "/");

        setupDatabaseConnectionInContext(servletContext);

        return servletContext;
    }

    private static void setupDatabaseConnectionInContext(WebAppContext servletContext) throws NamingException {
        new EnvEntry(servletContext, "jdbc/OskariPool", new Integer(1), true);
    }
}
