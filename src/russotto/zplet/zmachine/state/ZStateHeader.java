/* Zplet, a Z-Machine interpreter in Java */
/* Copyright 1996,2001 Matthew T. Russotto */
/* As of 23 February 2001, this code is open source and covered by the */
/* Artistic License, found within this package */

package russotto.zplet.zmachine.state;

import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.applet.Applet;
import russotto.iff.*;
import russotto.zplet.zmachine.ZHeader;

class ZStateHeader extends ZHeader implements Serializable
{
	ZStateHeader (byte [] memory_image)
	{
		this.memory_image = memory_image;
	}

	/* yes, a kludge */
	public int file_length() {
		return 0;
	}
}

