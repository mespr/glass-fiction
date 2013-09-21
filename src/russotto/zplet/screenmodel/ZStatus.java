/* Zplet, a Z-Machine interpreter in Java */
/* Copyright 1996,2001 Matthew T. Russotto */
/* As of 23 February 2001, this code is open source and covered by the */
/* Artistic License, found within this package */

package russotto.zplet.screenmodel;

import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.applet.Applet;

public class ZStatus {
		 boolean timegame;
		 boolean initialized;
		 boolean chronograph;
		 String location;
		 int score;
		 int turns;
		 int hours;
		 int minutes;
		
		 public ZStatus() {
		 }

		 public void update_score_line(String location, int score, int turns) {
				 this.timegame = false;
				 this.location = location;
				 this.score = score;
				 this.turns = turns;
		 }
		
		 public void update_time_line(String location, int hours, int minutes) {
				 String meridiem;

				 this.timegame = true;
				 this.location = location;
				 this.hours = hours;
				 this.minutes = minutes;
		 }
/*		
		 public Dimension minimumSize() {
				 return new Dimension(100,10);
		 }
		
		 public Dimension preferredSize() {
				 return new Dimension(500,20);
		 }
*/		 
 }


