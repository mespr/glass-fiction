/* Zplet, a Z-Machine interpreter in Java */
/* Copyright 1996,2001 Matthew T. Russotto */
/* As of 23 February 2001, this code is open source and covered by the */
/* Artistic License, found within this package */

package russotto.zplet.screenmodel;

import java.util.*;

import com.okappi.glass.fiction.ZMachineGlass;

class SyncVector extends Vector {
	private static final long serialVersionUID = 1L;

	public SyncVector() {
		super();
	}

	public synchronized Object syncPopFirstElement()  {
		Object first = syncFirstElement();
		if (first != null)
				removeElementAt(0);
		return first;
	}
	
	public synchronized Object syncFirstElement() {
		Object first = null;
		try {
			first = super.firstElement();
		} catch (NoSuchElementException booga) {}
//		try {
			if (first!=null) return first;
			else {
				ZMachineGlass.flush = true;
//				wait();
				return null;
			}
//		} catch (InterruptedException booga) {}
//		return null;
	}

	public synchronized void syncAddElement(Object obj) {
		super.addElement(obj);
		notify();
	}
}

