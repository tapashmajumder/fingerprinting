package ace.fingerprinting;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "IterableLinkHandlerServlet", urlPatterns = {"/"}, loadOnStartup = 1)
public class IterableLinkHandlerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SimpleApp.main(new String[]{"derbyClient"});

        String name = request.getParameter("name");
        if (name == null) {
            name = "World";
        }

        response.getWriter().print("Hello, " + name);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null) name = "World";
        request.setAttribute("user", name);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}