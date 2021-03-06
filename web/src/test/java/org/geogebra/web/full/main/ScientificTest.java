package org.geogebra.web.full.main;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.test.MockApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class ScientificTest {
	private static AppW app;
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void startApp() {
		app = MockApp
				.mockApplet(new TestArticleElement("prerelease", "scientific"));
	}

}
