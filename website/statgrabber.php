<?php
	$port = 6666;
	$sendShake = "GrabName";
	$receiveShake = "NameGrabber";
	header("Content-Type: text/json");

	$first = false;
	$last = false;

	if (array_key_exists("first", $_GET) && array_key_exists("last", $_GET)) {
		$first = $_GET["first"];
		$last  = $_GET["last"];
	}

	if (!$first || !$last || strlen($first) < 2 || strlen($last) < 2) {
		die ('{"error": "Bad input names"}');
	}

	if (!($sock = socket_create(AF_INET, SOCK_STREAM, 0))) {
		die ('{"error": "Failed to open socket"}');
	}

	if (!socket_connect($sock, "localhost", $port)) {
		die ('{"error": "Failed to connect socket", "details": "'.socket_strerror(socket_last_error($sock)).'"}');
	}

	echo performSocket($sock, $first, $last, $receiveShake, $sendShake);
	socket_close($sock);


	function performSocket($sock, $first, $last, $receiveShake, $sendShake) {
		$timeStr = strval(time());
		$b64 = base64_encode(pack("H*", md5($timeStr . $sendShake)));
		$out = "$timeStr|$b64\n";
		if (!socket_write($sock, $out)) {
			return ('{"error": "Failed to send handshake"}');
		}
		
		if (!($read = socket_read($sock, 100))) {
			return ('{"error": "Failed to receive handshake"}');
		}

		$parts = preg_split("/[|]/", $read);
		if (sizeof($parts) != 2) {
			return ('{"error": "Bad returned handshake"}');
		}
		$calcHash = pack("H*", md5($parts[0] . $receiveShake));
		$recHash = base64_decode($parts[1]);

		if ($recHash != $calcHash) {
			return('{"error": "Bad returned handshake"}');
		}

		if (!socket_write($sock, "$first|$last\n")) {
			return ('{"error": "Failed to send names"}');
		}

		echo '{"data": ';

		while (($str = socket_read($sock, 8196))) {
			echo $str;
			if (strlen($str) == 0) break;
		}

		if ($str === false) {
			return (', "error": "Failed to read socket"}');
		} else {
			return "}";
		}
	}
?>