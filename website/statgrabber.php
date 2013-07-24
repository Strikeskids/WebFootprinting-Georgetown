<?php
	$port = 6666;
	$sendShake = "GrabName";
	$receiveShake = "NameGrabber";
	// header("Content-Type: text/json");

	$first = $_GET["first"];
	$last  = $_GET["last"];

	if (strlen($first) < 2 || strlen($last) < 2) {
		die ('{"error": "Bad input names"}');
	}

	if (!($sock = socket_create(AF_INET, SOCK_STREAM, 0))) {
		die ('{"error": "Failed to open socket"}');
	}

	if (!socket_connect($sock, "localhost", $port)) {
		die ('{"error": "Failed to connect socket", "details": "'.socket_strerror(socket_last_error($sock)).'"}');
	}
	$timeStr = strval(time());
	$b64 = base64_encode(pack("H*", md5($timeStr . $sendShake)));
	$out = "$timeStr|$b64\n";
	if (!socket_write($sock, $out)) {
		die ('{"error": "Failed to send handshake"}');
	}
	
	if (!($read = socket_read($sock, 100))) {
		die ('{"error": "Failed to receive handshake"}');
	}

	$parts = preg_split("/[|]/", $read);
	if (sizeof($parts) != 2) {
		die ('{"error": "Bad returned handshake"}');
	}
	$calcHash = pack("H*", md5($parts[0] . $receiveShake));
	$recHash = base64_decode($parts[1]);

	if ($recHash != $calcHash) {
		die ('{"error": "Bad returned handshake"}');
	}

	if (!socket_write($sock, "$first|$last\n")) {
		die ('{"error": "Failed to send names"}');
	}

	echo '{"data": ';

	while (($str = socket_read($sock, 8196))) {
		echo $str;
		if (strlen($str) == 0) break;
	}

	if ($str === false) {
		die (', "error": "Failed to read socket"}');
	} else {
		echo "}";
	}

?>