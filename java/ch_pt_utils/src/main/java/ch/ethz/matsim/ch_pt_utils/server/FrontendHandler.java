package ch.ethz.matsim.ch_pt_utils.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.javalin.Context;
import io.javalin.Handler;

public class FrontendHandler implements Handler {
	@Override
	public void handle(Context ctx) throws Exception {
		InputStream inputStream = FrontendHandler.class.getResourceAsStream("index.html");

		String output = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;

		while ((line = reader.readLine()) != null) {
			output += line + "\n";
		}

		ctx.html(output);
	}
}
