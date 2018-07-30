<?php

	$m = new MongoClient();
	
	$db = $m->ReviewSys;
	
	$pending = $db->password_change_request;
	
	$otp_entered = $_GET["otp"];
	
	$email = $_GET["email"];
	
	$findarray = array(
	"email"=>$email,
	"otp"=>$otp_entered);
	
	$get_details = $pending->find($findarray);
	
	$u["s"] = array();
	foreach($get_details as $c){
		$s = array();
		$s["username"] = $c["username"];
		$s["email"] = $c["email"];
		array_push($u["s"],$s);
	}
	if(count($u["s"]) !== 0){
		$coll = $db->new_password_pending;
		$coll->insert($u["s"][0]);
		$pending->remove($findarray);
		echo "success";
	}else{
		echo "fail";
	}

?>