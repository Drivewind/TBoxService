package com.autopet.hardware.aidl.customview.volume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.autopet.hardware.aidl.R;

public class VolumeView extends LinearLayout {
	private View view;
	private ImageView imageview_mute;
	private SeekBar seekbar_volume;
	private OnVolumeChangeListener volumeListener;
	private onMuteChangeListener muteListener;

	private boolean isVolStartTrack = false, isVolEndTrack = true;

	@SuppressLint("NewApi")
	public VolumeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VolumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VolumeView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		view = layoutInflater.inflate(R.layout.view_volume, this);
		imageview_mute = (ImageView) view.findViewById(R.id.view_volume_image);
		seekbar_volume = (SeekBar) view.findViewById(R.id.view_volume_SeekBar);
		seekbar_volume
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						isVolStartTrack = false;
						isVolEndTrack = true;

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						isVolStartTrack = true;
						isVolEndTrack = false;

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser) {
							if (isVolStartTrack) {
								if (progress % 5 == 0 && !isVolEndTrack) {
									onVolumeChange(progress);
								}
							}
						}
					}
				});
		imageview_mute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				muteListener.onMuteChange();
			}
		});
	}

	public void setVolumeProgress(int volume) {
		seekbar_volume.setProgress(volume);
	}

	public void setVolumeMute() {
		imageview_mute.setImageResource(R.drawable.volume_image_mute);
	}

	public void setVolumeUnMute() {
		imageview_mute.setImageResource(R.drawable.volume_image_unmute);
	}

	public interface OnVolumeChangeListener {
		void onVolumeChange(int volume);
	}

	public interface onMuteChangeListener {
		void onMuteChange();
	}

	public void setOnVolumeChangeListener(OnVolumeChangeListener listener) {
		this.volumeListener = listener;
	}

	public void setOnMuteChangeListener(onMuteChangeListener listener) {
		this.muteListener = listener;
	}

	public void onVolumeChange(int volume) {
		if (this.volumeListener != null)
			this.volumeListener.onVolumeChange(volume);
	}

	public void onMuteChange(int mute) {
		if (this.muteListener != null)
			this.muteListener.onMuteChange();
	}
}
