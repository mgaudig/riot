/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.media.service;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class VideoMetaData {

	private int width;
	
	private int height;

	private long duration;

	private int bps;
	
	private String videoCodec;
	
	private float fps;
	
	private String audioCodec;
	
	private int samplingRate;
	
	private boolean stereo;

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getBps() {
		return this.bps;
	}

	public void setBps(int bps) {
		this.bps = bps;
	}

	public String getVideoCodec() {
		return this.videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	public float getFps() {
		return this.fps;
	}

	public void setFps(float fps) {
		this.fps = fps;
	}

	public String getAudioCodec() {
		return this.audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public int getSamplingRate() {
		return this.samplingRate;
	}

	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}

	public boolean isStereo() {
		return this.stereo;
	}

	public void setStereo(boolean stereo) {
		this.stereo = stereo;
	}
	
}
