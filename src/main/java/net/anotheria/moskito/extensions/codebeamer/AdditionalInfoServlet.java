package net.anotheria.moskito.extensions.codebeamer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

@WebServlet("/additionalInfo")
public class AdditionalInfoServlet extends HttpServlet {

	private static final long serialVersionUID = -5588647713604142356L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String additionalAccumulatorName = req.getParameter("additionalAccumulatorName");
		String timestamp = req.getParameter("timestamp");
		String json = "";
		if (StringUtils.isEmpty(additionalAccumulatorName) || StringUtils.isEmpty(timestamp)) {
			json = new Gson().toJson(AdditionalInfo.INSTANCE.getAdditionalInfoForAccumulators());
		} else {
			json = new Gson().toJson(AdditionalInfo.INSTANCE.getAdditionalInfoForAccumulator(additionalAccumulatorName, timestamp));
		}
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}
}
