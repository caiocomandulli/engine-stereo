package com.comandulli.engine.panoramic.playback.entity.component;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Transform;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer.Alignment;

public class Logger extends Component {

	private static TextRenderer textRenderer;
	private static Logger logger;

	public static final float logSize = 0.02f;

	private static String currentText = "";

	public Logger(int fontTexture, int fontShader) {
		super();
		if (logger != null) {
			throw new RuntimeException("DONT BUILD TWO LOGGERS WHATAHELL!");
		}
		textRenderer = new TextRenderer("", logSize, Alignment.CENTER, new Material(fontTexture, fontShader));
		textRenderer.material.color = Color.YELLOW;

		logger = this;
	}

	@Override
	public void start() {
		super.start();
		entity.transform.translate(new Vector3(0.0f, -3.0f, 8.0f));
		entity.addComponent(textRenderer);
	}

	public static void setParent(Transform parent) {
		if (logger != null) {
			logger.entity.transform.parent = parent;
		}
	}

	public static void setText(String text) {
		if (logger != null && !currentText.equals(text)) {
			textRenderer = new TextRenderer(text, logSize, Alignment.CENTER, logger.entity.renderer.material);
			logger.entity.addComponent(textRenderer);
			currentText = text;
		}
	}

	@Override
	public void destroy() {
		logger = null;
	}

}
