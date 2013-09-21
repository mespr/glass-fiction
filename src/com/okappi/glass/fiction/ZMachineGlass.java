package com.okappi.glass.fiction;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.glassware.AuthUtil;
import com.google.glassware.MainServlet;
import com.google.glassware.MirrorClient;
import com.google.glassware.WebUtil;

import russotto.zplet.screenmodel.ZScreen;
import russotto.zplet.screenmodel.ZStatus;
import russotto.zplet.zmachine.ZMachine;
import russotto.zplet.zmachine.state.ZState;
import russotto.zplet.zmachine.zmachine3.ZMachine3;
import russotto.zplet.zmachine.zmachine5.ZMachine5;
import russotto.zplet.zmachine.zmachine5.ZMachine8;

public class ZMachineGlass {
	public static boolean flush = false;
	private static final Logger LOG = Logger.getLogger(ZMachineGlass.class.getSimpleName());
	ZMachine zm;
	ZScreen screen;
	ZStatus status_line;
	ZStateCapture zsc;
    String userId;
    Credential credential;
    String story;

	public ZMachineGlass(String user, Credential cred)  {
		screen = new ZScreen();
		userId = user;
		credential = cred;
	}
	
	void write(String txt) {
		ZMachineGlass.flush = false;
		screen.pushInput(txt);
	}
	
	public void play() {
		if (zm != null) {
			zm.start();
			this.send(zm.window[0].glass_buffer);
			LOG.info(zm.window[0].glass_buffer);
		} else {
			LOG.warning("z machine is null");
		}
	}
	
	public void start(String story) {
		this.load(story);
		if (zm != null) {
			zm.start();
			this.send(zm.window[0].glass_buffer);
			LOG.info(zm.window[0].glass_buffer);
		} else {
			LOG.warning("z machine is null for story: "+story);
		}
	}

	public void play(String input) {
		if (zm != null) {
			this.write(input);
			zm.start();
			clearBuffer();
			zm.run();
			this.send(zm.window[0].glass_buffer);
			LOG.info(zm.window[0].glass_buffer);
		} else {
			LOG.warning("z machine is null");
		}
	}
	
	public void play(String input, String state) {
		try {
			ZStateCapture zsc = (ZStateCapture) ObjectEncoder.fromString(state);
			this.load(zsc.getStory());
			this.write(input);
			zm.restore2(zsc.getZState(zm));
			zm.run();
			clearBuffer();
			zm.run();
			this.send(zm.window[0].glass_buffer);
			LOG.info(zm.window[0].glass_buffer);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void clearBuffer() {
		zm.window[0].glass_buffer = "";
		ZMachineGlass.flush = false;		
	}

	public void load(String zcodefile)  {
		URL myzfile;
		InputStream myzstream;

		byte zmemimage[];

		this.story = zcodefile;
		
		zmemimage = null;
		try {
			myzfile = new URL(zcodefile);
			myzstream = myzfile.openStream();
			zmemimage = suckstream(myzstream);
		} catch (MalformedURLException booga) {
			LOG.warning("Error openning story file - Malformed URL");
		} catch (IOException booga) {
			LOG.warning("Error openning story file - I/O error");
		}
		if (zmemimage != null) {
			switch (zmemimage[0]) {
			case 3:
				LOG.info("ZMachine3 starting");
				zm = new ZMachine3(screen, status_line, zmemimage);
				break;
			case 5:
				LOG.info("ZMachine5 starting");
				zm = new ZMachine5(screen, zmemimage);
				break;
			case 8:
				LOG.info("ZMachine8 starting");
				zm = new ZMachine8(screen, zmemimage);
				break;
			default:
				LOG.warning("Not a valid V3,V5, or V8 story file");
			}
		}
	}

	byte[] suckstream(InputStream mystream) throws IOException {
		byte buffer[];
		byte oldbuffer[];
		int currentbytes = 0;
		int bytesleft;
		int got;
		int buffersize = 2048;

		buffer = new byte[buffersize];
		bytesleft = buffersize;
		got = 0;
		while (got != -1) {
			bytesleft -= got;
			currentbytes += got;
			if (bytesleft == 0) {
				oldbuffer = buffer;
				buffer = new byte[buffersize + currentbytes];
				System.arraycopy(oldbuffer, 0, buffer, 0, currentbytes);
				oldbuffer = null;
				bytesleft = buffersize;
			}
			got = mystream.read(buffer, currentbytes, bytesleft);
		}
		if (buffer.length != currentbytes) {
			oldbuffer = buffer;
			buffer = new byte[currentbytes];
			System.arraycopy(oldbuffer, 0, buffer, 0, currentbytes);
		}
		return buffer;
	}
	
	public void send(String card) {
	    TimelineItem timelineItem = new TimelineItem();
		timelineItem.setSpeakableText(card.replaceAll("\\<.*?>","."));
		timelineItem.set("speakableType", "story update");
		timelineItem.setHtml("<article class=\"auto-paginate text-auto-size\">"+card+"</article>");
//		timelineItem.setText(card);
		
		String stateString = getStateString();
//		LOG.info("State String is: "+stateString);
		
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
		MenuItem replyAction = new MenuItem().setAction("REPLY"); 
		menuItemList.add(replyAction);

	      // And custom actions
//		List<MenuValue> menuValues = new ArrayList<MenuValue>();
//	    menuValues.add(new MenuValue().setDisplayName("Drill In"));
//		MenuItem customAction = new MenuItem().setValues(menuValues).setId(stateString).setAction("CUSTOM");
//		menuItemList.add(customAction);
		
		timelineItem.setMenuItems(menuItemList);
		timelineItem.setSourceItemId(getStateString());
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		
		try {
			MirrorClient.insertTimelineItem(credential, timelineItem);
			LOG.info("the timelineItemId is "+timelineItem.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String getStateString() {
		String stateString = "";
		ZState state = new ZState(zm);
		state.save_current();
		ZStateCapture stateCapture = new ZStateCapture(state);
		stateCapture.setStory(this.story);
		try {
			stateString = ObjectEncoder.toString(stateCapture);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.warning("save state failed");
			e.printStackTrace();
		}
		return stateString;
	}
	
}
