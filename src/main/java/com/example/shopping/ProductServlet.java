
package com.example.shopping;

import java.io.*;
import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.http.*;

public class ProductServlet extends HttpServlet {
    private DataSource dataSource;

    public void init() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            dataSource = (DataSource) envCtx.lookup("jdbc/ShoppingDB");
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.name, p.price, c.name AS category FROM products p LEFT JOIN categories c ON p.category_id = c.id")) {

            out.println("<h1>Product List</h1><ul>");
            while (rs.next()) {
                out.printf("<li>%s - $%.2f (%s)</li>",
                           rs.getString("name"),
                           rs.getDouble("price"),
                           rs.getString("category"));
            }
            out.println("</ul>");

        } catch (SQLException e) {
            out.println("Database error: " + e.getMessage());
        }
    }
}
