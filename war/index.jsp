<!--
Copyright (C) 2013 Okappi, Inc.
-->
<%@ page import="com.google.api.client.auth.oauth2.Credential" %>
<%@ page import="com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore" %>
<%@ page import="com.google.glassware.MirrorClient" %>
<%@ page import="com.google.glassware.WebUtil" %>
<%@ page import="com.google.glassware.AuthUtil" %>
<%@ page import="java.util.List" %>

<%@ page import="com.google.glassware.MainServlet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<%
	if (AuthUtil.getUserId(request) == null
	|| AuthUtil.getCredential(AuthUtil.getUserId(request)) == null
	|| AuthUtil.getCredential(AuthUtil.getUserId(request)).getAccessToken() == null) {
		// redirect to auth flow
		response.sendRedirect(WebUtil.buildUrl(request, "/oauth2callback"));
	} 
%>
<html>
<head>
	<title>Glass Fiction</title>
	<script language="javascript">
	var action = "";
	function init() {
		document.getElementById("storySelect").onclick = function() {
			var val = document.getElementById("storySelect").value.split(",");
			document.getElementById("story").value = val[0];
			if (val[1] != null) {
				action = val[1];
			}
		}
		document.getElementById("startBtn").onclick = function() {
			var story = document.getElementById("story").value;
			if (story == null || story == "") {
				alert("you have to select a story first. How about Zork?")
				document.getElementById("story").value = "http://mirror.ifarchive.org/if-archive/games/zcode/zdungeon.z5";
				action = "look";
				return;
			}
			var req = new XMLHttpRequest();

			var url = "<%= WebUtil.buildUrl(request, "/run") %>";
			url = url + "?story="+story;
			if (action != null || action != "") {
				url = url + "&action="+action;
			}
			req.open("GET", url, true);
			req.setRequestHeader("Content-Type", "application/json");
			req.send();
			document.getElementById("statusDisplay").innerHTML = "Your story is on it's way to Glass...";

	/*
			req.onreadystatechange = function (evt) {
				if (req.readyState == 4) {
					if (req.status == 200) {
						var info = JSON.parse(req.responseText);
						if (info.status == "OK") {
							document.getElementById("statusDisplay").innerHTML = "Confirmed!"
						} else {
							document.getElementById("statusDisplay").innerHTML = "Error. Are you logged into your google account?";
						}
					}
				}
			}
	*/
		};
	}
	</script>
	<style>
		BODY {
			height:100%;
			font-family:arial;
			color:#EEEEEE;
			background-color:#333333
		}
		A {
			color:white;
		}
		#main {
			position:relative;
		}
		#main-content {
		    margin-left: auto;
		    margin-right: auto;
		    position: relative;
		    width: 960px;
   		}
   		#footer {
   			position:absolute;
   			height:40px;
   			padding-top:10px;
   			bottom:0;
   			width:100%;
   			border-top:1px solid #cccccc;
   			font-size:0.8em;
   		}
   		#footer-content {
		    margin-left: auto;
		    margin-right: auto;
		    position: relative;
		    width: 960px;
   		}
	</style>
</head>
<body onload="init()">
	<div id="main">
		<div id="main-content">
			<h1>Glass Fiction</h1>
			<p>This is a port to Google Glass of the Interactive Fiction player that was popular throughout the
			eighties and nineties. Hundreds of stories were written for the z-machine, a story engine designed
			originally to simplify porting Zork and it's successors to diverse hardware.
			<p>It needs some polishing but is a wonderfully apt application for Glass. Many IF games of the time
			were desperately seeking more bling in text layout, beeps and fonts. The authors yearned for the future we
			have now with 3D immersive graphics. I've had to strip that all out which may cause problems with some stories.
			However, a well written story is all it really takes, and Glass let's it become like an interactive audio
			book. Have fun!

			<p>Select a story:
			<select id="storySelect" name="storySelect">
				<option value="http://mirror.ifarchive.org/if-archive/games/zcode/zdungeon.z5,look">Zork</option>
				<option value="http://mirror.ifarchive.org/if-archive/games/zcode/zdungeon.z5">Zork (with intro credits)</option>
				<option value="http://mirror.ifarchive.org/if-archive/games/zcode/geb.z5,look">Goose Egg, Badger. An eccentric girls birthday</option>
				<option value="http://mirror.ifarchive.org/if-archive/games/zcode/bear.z5, ">A Bear's Night Out</option>
				<option value="http://mirror.ifarchive.org/if-archive/games/competition95/weather.z5">A Change in the Weather</option>
			</select>
			<p><input type="text" id="story" name="story" value="" size="60" placeholder="enter url or select from above">
			<button id="startBtn" class="btn">Start Story</button> <span id="statusDisplay"></span>

			<p>For more information on Interactive Fiction see <a href="http://ifarchive.org">http://ifarchive.org</a>.
		</div>
	</div>
	<div id="footer">
		<div id="footer-content">
			Copyright &copy; 2013, Michael Sprague
		</div>
	</div>
</body>
</html>
