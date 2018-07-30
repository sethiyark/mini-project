<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$coll = $db->Registered_Users;
	
	$email = $_GET["email"];
	$pfind = array(
	"email"=>$email);
	$check = $coll->find($pfind);
	$u["s"] = array();
	foreach($check as $c){
		$s = array();
		$s["username"] = $c["username"];
		array_push($u["s"],$s);
	}
	if(count($u["s"]) !== 0){
		$otp = "1234";
		$changereq = $db->password_change_request;
		$req = array(
		"time" => time(),
		"username"=>$u["s"][0]["username"],
		"email"=>$email,
		"otp"=>$otp);
		$changereq->insert($req);
		echo "otp sent";
	} else {
		echo "unregistered";
	}
?>