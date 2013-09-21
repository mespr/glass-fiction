package com.okappi.glass.fiction;

import java.io.Serializable;
import java.util.Stack;

import russotto.zplet.zmachine.ZHeader;
import russotto.zplet.zmachine.ZMachine;
import russotto.zplet.zmachine.state.ZState;

public class ZStateCapture implements Serializable {
	Stack zstack;
	ZHeader header;
	int pc;
	byte dynamic[];
	short locals[];
	String story;

	public ZStateCapture(ZState zs) {
		zstack = zs.zstack;
		header = zs.header;
		pc = zs.pc;
		dynamic = zs.dynamic;
		locals = zs.locals;
	}
	
	public ZState getZState(ZMachine zm) {
		ZState zs = new ZState(zm);
		
		zs.header = header;
		zs.zstack = zstack;
		zs.pc = pc;
		zs.dynamic = dynamic;
		zs.locals = locals;		

		return(zs);
	}
	
	public void setStory(String story) {
		this.story = story;
	}
	
	public String getStory() {
		return this.story;
	}
}
