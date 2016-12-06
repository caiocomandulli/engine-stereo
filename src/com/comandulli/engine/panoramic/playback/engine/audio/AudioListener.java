package com.comandulli.engine.panoramic.playback.engine.audio;

import com.comandulli.engine.panoramic.playback.engine.core.Component;

public class AudioListener extends Component {

    @Override
	protected void register() {
		super.register();
		Audio.registerListener(this);
	}

	@Override
	protected void unregister() {
		super.unregister();
		Audio.removeListener(this);
	}

}
