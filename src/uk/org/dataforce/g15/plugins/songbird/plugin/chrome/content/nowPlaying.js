var serverSocket;

// Close the socket!
function NowPlaying_onUnload( aEvent ) {
	if (serverSocket) serverSocket.close();
}
window.addEventListener("unload", NowPlaying_onUnload, false);

// Open the socket!
function NowPlaying_onLoad() {
	var listener = {
		onSocketAccepted : function(socket, transport) {
			try {
				var outputString = "Playing: "+SBDataGetStringValue("faceplate.playing")+"\n" +
				                   "Paused: "+SBDataGetStringValue("faceplate.paused")+"\n" +
				                   "Artist: "+SBDataGetStringValue("metadata.artist")+"\n" +
				                   "Title: "+SBDataGetStringValue("metadata.title")+"\n" +
				                   "Album: "+SBDataGetStringValue("metadata.album")+"\n" +
				                   "Genre: "+SBDataGetStringValue("metadata.genre")+"\n" +
				                   "Position: "+SBDataGetStringValue("metadata.position")+"\n" +
				                   "Length: "+SBDataGetStringValue("metadata.length")+"\n" +
				                   "PositionStr: "+SBDataGetStringValue("metadata.position.str")+"\n" +
				                   "LengthStr: "+SBDataGetStringValue("metadata.length.str")+"\n";
				                   "Shuffle: "+SBDataGetStringValue("playlist.shuffle")+"\n";
				                   "Repeat: "+SBDataGetStringValue("playlist.repeat")+"\n";
				var stream = transport.openOutputStream(0,0,0);
				stream.write(outputString,outputString.length);
				stream.close();
			} catch(ex2){ dump("::"+ex2); }
		},
		onStopListening : function(socket, status){}
	};

	try {
		serverSocket = Components.classes["@mozilla.org/network/server-socket;1"].createInstance(Components.interfaces.nsIServerSocket);

		serverSocket.init(7055,false,-1);
		serverSocket.asyncListen(listener);
	} catch(ex){ dump(ex); }
}

NowPlaying_onLoad();