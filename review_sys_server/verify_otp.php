<?php

	$m = new MongoClient();
	
	$db = $m->ReviewSys;
	
	$pending = $db->Pending_Users;
	
	$otp_entered = $_GET["otp_entered"];
	
	$username = $_GET["username"];
	
	$findarray = array(
	"username"=>$username,
	"otp"=>$otp_entered);
	
	$get_details = $pending->find($findarray);
	
	$u["s"] = array();
	foreach($get_details as $c){
		$s = array();
		$s["username"] = $c["username"];
		$s["name"] = $c["name"];
		$s["email"] = $c["email"];
		$s["password"] = $c["password"];
		$s["phone_num"] = $c["phone_num"];
		array_push($u["s"],$s);
	}
	if(count($u["s"]) !== 0){
		$coll = $db->Registered_Users;
		$coll->insert($u["s"][0]);
		$pending->remove($findarray);
		echo "success";
	}else{
		echo "fail";
	}

?>