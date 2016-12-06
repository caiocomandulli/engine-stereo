package com.comandulli.engine.panoramic.playback.entity.playback;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.physics.BoxCollider;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer.Alignment;
import com.comandulli.engine.panoramic.playback.entity.focus.InteractiveObject;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData.Chapter;

import android.media.MediaPlayer;

public class ChapterBox extends Entity implements InteractiveObject {

	private final Chapter chapter;
	private final MediaPlayer player;
	private final Subtitle subs;

	private Entity chapterNumber;
	private Entity chapterText;

	private final ChapterSelector selector;

	private final int fontTexture;
	private final int fontShader;

	public ChapterBox(Chapter chapter, MediaPlayer player, Subtitle subs, ChapterSelector selector, int mesh, int shader, int fontTexture) {
		super("Box-" + chapter.name);
		this.chapter = chapter;
		this.player = player;
		this.subs = subs;
		addComponent(new MeshRenderer(mesh, new Material(mesh, shader)));
		addComponent(new BoxCollider(Vector3.scaled(Vector3.SCALE_ONE, 2.0f)));
		this.selector = selector;
		this.fontShader = shader;
		this.fontTexture = fontTexture;
	}

	@Override
	public void start() {
		super.start();
		chapterNumber = new Entity("number");
		chapterNumber.addComponent(new TextRenderer(toRomanNumerals(chapter.number), 0.02f, Alignment.CENTER, new Material(fontTexture, fontShader)));
		chapterNumber.setEnabled(isEnabled());
		chapterNumber.transform.position = new Vector3(0.0f, -0.2f, -1.0f);
		chapterNumber.transform.parent = transform;
		Engine.getScene().addEntity(chapterNumber);
		chapterText = new Entity("text");
		String trimmedText = chapter.name;
		if (trimmedText.length() > 10) {
			trimmedText = trimmedText.substring(0, 10);
		}
		chapterText.addComponent(new TextRenderer(trimmedText, 0.01f, Alignment.CENTER, new Material(fontTexture, fontShader)));
		chapterText.setEnabled(isEnabled());
		chapterText.transform.position = new Vector3(0.0f, -1.0f, -1.0f);
		chapterText.transform.parent = transform;
		Engine.getScene().addEntity(chapterText);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (chapterNumber != null && chapterText != null) {
			chapterNumber.setEnabled(enabled);
			chapterText.setEnabled(enabled);
		}
	}

	@Override
	public float getTimeToFocus() {
		return 1.0f;
	}

	@Override
	public void FocusStarted() {
	}

	@Override
	public void FocusCanceled() {
	}

	@Override
	public void FocusIn() {
		int msec = Math.round(chapter.timestamp * 1000);
		player.seekTo(msec);
		subs.init(chapter.timestamp);
		selector.action();
	}

	@Override
	public void FocusOut() {
	}

	@Override
	public void FocusUpdate(float time) {
	}

	private final String[] romanDecimals = { "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC" };
	private final String[] romanUnits = { "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX" };

	private String toRomanNumerals(int number) {
		if (number > 99) {
			return String.valueOf(number);
		}
		int unit = number % 10;
		int dec = number / 10;
        return romanDecimals[dec] + romanUnits[unit];
	}

}
