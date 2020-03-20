package edu.kit.informatik.pse.bleloc.backoffice;

import edu.kit.informatik.pse.bleloc.annotations.RequireBackofficeAccount;
import org.glassfish.jersey.server.mvc.Template;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.*;
import java.util.List;

@Path("stats")
@Stateless
public class Statistics {
	/**
	 * Shows the statistics.
	 */
	@GET
	@Produces("text/html")
	@Template(name = "/stats.ftl")
	@RequireBackofficeAccount
	public Map<String, Object> showPage() {
		Map<String, Object> result = new HashMap<>();

		List<Curve> curves = new ArrayList<>();

		List<Point> points = new ArrayList<>();
		for (double i=-1; i<=1; i+=0.1) {
			points.add(new Point(i, i*i));
		}
		curves.add(new Curve("X Squared", points));

		points = new ArrayList<>();
		for (double i=-1; i<=1; i+=0.1) {
			points.add(new Point(i, i));
		}
		curves.add(new Curve("X linear", points));

		points = new ArrayList<>();
		for (double i=0; i <= 2.0*Math.PI; i+=0.1) {
			points.add(new Point(Math.sin(i), Math.cos(i)));
		}
		curves.add(new Curve("Circle", points));

		List<Graph> graphs = new ArrayList<>();
		graphs.add(new Graph("Example graph", "X Axis", "Y Axis", curves));
		result.put("graphs", graphs);

		return result;
	}

	public static class Graph {
		private String name;
		private String x;
		private String y;
		private List<Curve> curves;

		public Graph(String name, String xAxis, String yAxis, List<Curve> curves) {
			this.name = name;
			this.x = xAxis;
			this.y = yAxis;
			this.curves = curves;
		}

		public String getName() {
			return name;
		}

		public String getXaxis() {
			return x;
		}

		public String getYaxis() {
			return y;
		}

		public List<Curve> getCurves() {
			return curves;
		}
	}

	public static class Curve {
		private String name;
		private List<Point> points;

		public Curve(String title, List<Point> points) {
			this.name = title;
			this.points = points;
		}

		public String getName() {
			return this.name;
		}

		public List<Point> getPoints() {
			return this.points;
		}
	}

	public static class Point {
		private double x;
		private double y;

		public Point (double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return this.x;
		}

		public double getY() {
			return this.y;
		}
	}
}
