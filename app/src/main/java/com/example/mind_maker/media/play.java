package com.example.mind_maker.media;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.mind_maker.R;

public class play extends android.app.Activity {
	private VideoView mVideoView;
	private Button playBtn, stopBtn;
	MediaController mMediaController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
		mVideoView = new VideoView(this);
		mVideoView = (VideoView) findViewById(R.id.video);
		mMediaController = new MediaController(this);
		playBtn = (Button) findViewById(R.id.playbutton);
		stopBtn = (Button) findViewById(R.id.stopbutton);
		playBtn.setOnClickListener(new mClick());
		stopBtn.setOnClickListener(new mClick());
	}

	class mClick implements OnClickListener {
		@Override
		public void onClick(View v) {

			mVideoView.setVideoPath(getIntent().getStringExtra("path"));
			mVideoView.setMediaController(new MediaController(play.this));
			if (v == playBtn) {
				mVideoView.start();
			} else if (v == stopBtn) {
				mVideoView.stopPlayback();
			}
		}
	}
}
