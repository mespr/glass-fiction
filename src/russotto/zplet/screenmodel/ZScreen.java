/* Zplet, a Z-Machine interpreter in Java */
/* Copyright 1996,2001 Matthew T. Russotto */
/* As of 23 February 2001, this code is open source and covered by the */
/* Artistic License, found within this package */

package russotto.zplet.screenmodel;

import java.util.*;
import java.util.logging.Logger;

import com.google.glassware.MainServlet;

public class ZScreen  {
	private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());

	SyncVector inputcodes;
	public ZWindow inputwindow;
	public Vector bufferedcodes;
	boolean bufferdone;
		final static char accent_table[] = {
			 	'\u00e4',			/* a-umlaut */
			 	'\u00f6',			/* o-umlaut */
			 	'\u00fc',			/* u-umlaut */
			 	'\u00c4',			/* A-umlaut */
			 	'\u00d6',			/* O-umlaut */
			 	'\u00dc',			/* U-umlaut */
			 	'\u00df',			/* sz-ligature */
			 	'\u00bb',			/* right-pointing quote */
			 	'\u00ab',			/* left-pointing quote */
			 	'\u00eb',			/* e-umlaut */
			 	'\u00ef',			/* i-umlaut */
			 	'\u00ff',			/* y-umlaut */
			 	'\u00cb',			/* E-umlaut */
			 	'\u00cf',			/* I-umlaut */
			 	'\u00e1',			/* a-acute */
			 	'\u00e9',			/* e-acute */
			 	'\u00ed',			/* i-acute */
			 	'\u00f3',			/* o-acute */
			 	'\u00fa',			/* u-acute */
			 	'\u00fd',			/* y-acute */
			 	'\u00c1',			/* A-acute */
			 	'\u00c9',			/* E-acute */
			 	'\u00cd',			/* I-acute */
			 	'\u00d3',			/* O-acute */
			 	'\u00da',			/* U-acute */
			 	'\u00dd',			/* Y-acute */
			 	'\u00e0',			/* a-grave */
			 	'\u00e8',			/* e-grave */
			 	'\u00ec',			/* i-grave */
			 	'\u00f2',			/* o-grave */
			 	'\u00f9',			/* u-grave */
			 	'\u00c0',			/* A-grave */
			 	'\u00c8',			/* E-grave */
			 	'\u00cc',			/* I-grave */
			 	'\u00d2',			/* O-grave */
			 	'\u00d9',			/* U-grave */
			 	'\u00e2',			/* a-circumflex */
			 	'\u00ea',			/* e-circumflex */
			 	'\u00ee',			/* i-circumflex */
			 	'\u00f4',			/* o-circumflex */
			 	'\u00fb',			/* u-circumflex */
			 	'\u00c2',			/* A-circumflex */
			 	'\u00ca',			/* E-circumflex */
			 	'\u00ce',			/* I-circumflex */
			 	'\u00d4',			/* O-circumflex */
			 	'\u00da',			/* U-circumflex */
			 	'\u00e5',			/* a-ring */
			 	'\u00c5',			/* A-ring */
			 	'\u00f8',			/* o-slash */
			 	'\u00d8',			/* O-slash */
			 	'\u00e3',			/* a-tilde */
			 	'\u00f1',			/* n-tilde */
			 	'\u00f5',			/* o-tilde */
			 	'\u00c3',			/* A-tilde */
			 	'\u00d1',			/* N-tilde */
			 	'\u00d5',			/* O-tilde */
			 	'\u00e6',			/* ae-ligature */
			 	'\u00c6',			/* AE-ligature */
			 	'\u00e7',			/* c-cedilla */
			 	'\u00c7',			/* C-cedilla */
			 	'\u00fe',			/* Icelandic thorn */
			 	'\u00f0',			/* Icelandic eth */
			 	'\u00de',			/* Icelandic Thorn */
			 	'\u00d0',			/* Icelandic Eth */
			 	'\u00a3',			/* UK pound symbol */
			 	'\u0153',			/* oe ligature */
			 	'\u0152',			/* OE ligature */
			 	'\u00a1',			/* inverse-! */
			 	'\u00bf',			/* inverse-? */
	     };


		public ZScreen() {
			inputcodes = new SyncVector();
			bufferedcodes = new Vector();
		}

		protected boolean isterminator(int key) {
				return ((key == 10) || (key == 13));
		}

		static char zascii_to_unicode(short zascii) {
				if ((zascii >= 32) && (zascii <= 126)) /* normal ascii */
					return (char)zascii;
				else if ((zascii >= 155) && (zascii <= 251)) {
					if ((zascii - 155) < accent_table.length) {
						return accent_table[zascii - 155];
					}
					else
						return '?';
				}
				else if ((zascii == 0) || (zascii >= 256)) {
					return '?';
				}
				else {
					System.err.println("Illegal character code: " + zascii);
					return '?';
				}
		}

		public static short unicode_to_zascii(char unicode) throws NoSuchKeyException {
				short i;
				
				if (unicode == '\n')
					return 13;
				if (unicode == '\b')
					return 127;
				else if (((int)unicode < 0x20) &&
					 (unicode != '\r' /*'\uu000d'*/) &&
					 (unicode != '\uu001b'))
					throw new NoSuchKeyException("Illegal character input: " + (short)unicode);
				else if ((int)unicode < 0x80) /* normal ascii, including DELETE */
					return (short)unicode;
				else {
					for (i = 0; i < accent_table.length; i++) {
						if (accent_table[i] == unicode)
							return (short)(155 + i);
					}
					throw new NoSuchKeyException("Illegal character input: " + (short)unicode);
				}
		}
	
		public void set_input_window(ZWindow thewindow)
		{
				inputwindow = thewindow; 
		}		
		
		public void pushInput(String input) {
			int i = 0;
			short code;

			try {
				while (i < input.length()) {
					char c = input.charAt(i);
					code = unicode_to_zascii(c);
					inputcodes.syncAddElement(new Integer(code));
					i++;
				}
			}
			catch (NoSuchKeyException excpt) {
				System.err.println(excpt);
			}
		}
		
		public short read_code()  {
				Integer thecode = null;
				
//				while (thecode == null) {
						thecode = (Integer)inputcodes.syncPopFirstElement();
//				}
				if (thecode != null) {
					char c = (char) thecode.shortValue();
					return (short)thecode.intValue();
				} else {
					return 13;
				}
		}
		
		public short read_buffered_code()  { /* should really be synched */
						   Integer thecode;
				int incode;
				short result;
				int cw, ch;

				inputwindow.flush();
//				cw = fixedmetrics.charWidth(' ');
//				ch = fixedmetrics.getHeight();

//				inputcursor.setGraphics(getGraphics());
//				inputcursor.setcolors(getForeground(), getBackground());
//				inputcursor.setcolors(getForeground(), zbcolor);
//				inputcursor.size(cw, ch);

				while (!bufferdone) {
//						inputwindow.flush();
//						inputcursor.move((inputwindow.getLeft() + inputwindow.cursorx) * cw,
//														 (inputwindow.getTop() + inputwindow.cursory) * ch);
//						inputcursor.show();
//						Toolkit.getDefaultToolkit().sync();
//						incode = read_code();
////						inputcursor.setGraphics(getGraphics());
//						inputcursor.hide();
//						if ((incode == 8) || (incode == 127)) {
//								try {
//										thecode = (Integer)bufferedcodes.lastElement();
//										bufferedcodes.removeElementAt(bufferedcodes.size() - 1);
//										inputwindow.flush();
//										inputwindow.movecursor(inputwindow.cursorx - 1, 
//																				   inputwindow.cursory);
//										inputwindow.printzascii((short)' ');
//										inputwindow.flush();
//										inputwindow.movecursor(inputwindow.cursorx - 1, 
//																				   inputwindow.cursory);
//								}
//								catch (NoSuchElementException booga) {
//										/* ignore */
//								}
//						}
//						else {
//								if (isterminator(incode)) {
//										bufferdone = true;
//										if ((incode==10) || (incode == 13))
//												inputwindow.newline();
//								}
//								else {
//										inputwindow.printzascii((short)incode);
//										inputwindow.flush();
//								}
//								bufferedcodes.addElement(new Integer(incode));
//						}
				}
				thecode = (Integer)bufferedcodes.firstElement();
				bufferedcodes.removeElementAt(0);
				if (bufferedcodes.isEmpty()) {
						bufferdone = false;
				}
				return (short)(thecode.intValue());
		}

		public int getlines() {
				return 0;//lines;
		}
		
		public int getchars() {
				return 0;//chars;
		}


		public synchronized void settext(int y, int x, char newtext[],
																		 int offset, int length)
		{
				//settext(y,x,newtext,offset,length,false, fixedfont);
		}

//		public synchronized void settext(int y, int x, char newtext[],
//																		 int offset, int length, boolean reverse,
//																		 Font textfont) {
//				int i;
//				char newbuffer[];
//				Graphics g;
//
//				try {
//						g_store.setFont(textfont);
//						drawtext(g_store, y, x, newtext, offset, length, reverse);
//						if (!hasscrolled) {
//								g = getGraphics();
//								g.setFont(textfont);
//								drawtext(g, y, x, newtext, offset, length, reverse);
//						}
//				}
//				catch (NullPointerException booga) {
//						System.err.println("No graphics in settext");
//				}
//		}
//
//		protected synchronized void drawtext(Graphics g, int y, int x, char newtext[], int offset, int length, boolean reverse) {
//				int tw, th;
//				int tx, ty;
//				
//				tw = length * fixedmetrics.charWidth(' ');
//				th = fixedmetrics.getHeight();
//				tx = x * fixedmetrics.charWidth(' ');
//				ty = th * (y + 1) - fixedmetrics.getDescent();
//				if (reverse) {
//						g.setColor(getForeground());
//						g.fillRect(tx, th * y, tw, th);
////						g.setColor(getBackground());
//						g.setColor(zbcolor);
//				}
//				else {
////						g.setColor(getBackground());
//						g.setColor(zbcolor);
//						g.fillRect(tx, th * y, tw, th);
//						g.setColor(getForeground());
//				}
//				g.drawChars(newtext, offset, length, tx, ty);
//				g.setColor(getForeground());
//		}
//		
//		public synchronized void scrollLines(int top, int height, int lines) {
//				int j;
//				Graphics g;
//				int texttop;
//
////				System.err.println("scrollLines top height lines "
////												   + top + " "
////												   + height + " "
////												   + lines + " ");
//				try {
//						texttop = top * fixedmetrics.getHeight();
//						g_store.copyArea(0, texttop + lines * fixedmetrics.getHeight(),
//														 size().width, (height - lines) * fixedmetrics.getHeight(),
//														 0, -lines * fixedmetrics.getHeight());
////						g_store.setColor(getBackground());
//						g_store.setColor(zbcolor);
//						g_store.fillRect(0, texttop + ((height-1) * fixedmetrics.getHeight()),
//														 size().width, fixedmetrics.getHeight());
////						Toolkit.getDefaultToolkit().sync();
//				}
//				catch (NullPointerException booga) {
//						System.err.println("No graphics in scrollLines");
//				}
//				repaint();
//				hasscrolled = true;
//		}
//		
//		public synchronized void paint(Graphics g) {
//				int y;
//				int ypixels;
//
////				super.paint(g);
////				System.err.println("paint");
//				g.drawImage(backing_store, 0, 0, null);
//				inputcursor.redraw(g);
//				hasscrolled = false;
//		}
//
////		public void repaint(long tm, int x, int y, int width, int height) {
////				System.err.println("repaint: " + tm + " " + x + " " + y + " " + width + " " + height);
////				super.repaint(tm,x,y,width,height);
////		}
//
//		public void update(Graphics g) {
////				System.err.println("update");
//				g.setColor(getForeground());
//				paint(g);
//		}
//
//		public void clear() {
//				Dimension mysize = size();
//
//				try {
////						g_store.setColor(getBackground());
//						g_store.setColor(zbcolor);
//						g_store.fillRect(0, 0,
//														 mysize.width, mysize.height);
//				}
//				catch (NullPointerException booga) {
//						System.err.println("No graphics in clear");
//				}
//				repaint();
//		}
//
//		public int getZForeground() 
//		{
//				return zforeground;
//		}
//		
//		public int getZBackground() 
//		{
//				return zbackground;
//		}
//		
//		public void setZForeground(int zcolor)
//		{
//				zforeground = zcolor;
//				setForeground(ZColor.getcolor(zcolor));
//		}
//		
//		public void setZBackground(int zcolor)
//		{
//				zbackground = zcolor;
////				setBackground(ZColor.getcolor(zcolor)); Yucky side effects
//				zbcolor = ZColor.getcolor(zcolor);
//		}
//		
//		public Frame getFrame() {
//			Component cursor;
//			
//			cursor = this;
//			while (!(cursor instanceof Frame))
//				cursor = cursor.getParent();
//				
//			return (Frame)cursor;
//		}
//		
//		public Dimension minimumSize() {
//				return new Dimension(100,100);
//		}
//
//		public Dimension preferredSize() {
//				return new Dimension(500,500);
//		}
}
