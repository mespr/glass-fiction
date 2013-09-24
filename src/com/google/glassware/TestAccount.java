// This class has been locally added to the package by MJS

package com.google.glassware;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestAccount extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(AuthServlet.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		if (AuthUtil.getUserId(req) == null
				|| AuthUtil.getCredential(AuthUtil.getUserId(req)) == null
				|| AuthUtil.getCredential(AuthUtil.getUserId(req))
						.getAccessToken() == null) {
			out.println("{\"status\":false}");
		} else {
			out.println("{\"status\":true}");
		}
	}
}