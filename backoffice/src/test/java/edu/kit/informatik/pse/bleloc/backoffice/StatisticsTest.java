package edu.kit.informatik.pse.bleloc.backoffice;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StatisticsTest extends BackofficeTest {
	private Statistics statistics;

	public StatisticsTest() {
		statistics = new Statistics();
		injectObjectsInto(statistics);
	}

	@Test
	public void testShowPage() {
		List<Statistics.Graph> graphs = (List<Statistics.Graph>)statistics.showPage().get("graphs");
		for (Statistics.Graph g : graphs) {
			Assert.assertFalse(g.getName().isEmpty());
			Assert.assertFalse(g.getXaxis().isEmpty());
			Assert.assertFalse(g.getYaxis().isEmpty());
			for (Statistics.Curve c : g.getCurves()) {
				Assert.assertFalse(c.getName().isEmpty());
				for (Statistics.Point p : c.getPoints()) {
					Assert.assertFalse(new Double(p.getX()).isNaN());
					Assert.assertFalse(new Double(p.getY()).isNaN());
				}
			}
		}
	}
}
