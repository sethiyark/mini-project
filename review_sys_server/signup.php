<?php
		
	$m = new MongoClient();
	
	$db = $m->ReviewSys;
	
	$coll = $db->Registered_Users;
	
	$pending = $db->Pending_Users;
	
	$name = $_GET["name"];
	
	$email = $_GET["email"];
	
	$phone_num = $_GET["phone_num"];
	
	$username = $_GET["username"];
	
	$password = $_GET["password"];
	
	$otp = "1234";
	
	$ucheck = $coll->find(["username"=>$username]);
	$u["s"] = array();
	foreach($ucheck as $c){
		$s = array();
		$s["username"] = $c["username"];
		array_push($u["s"],$s);
	}
	
	$echeck = $coll->find(["email"=>$email]);
	$e["s"] = array();
	foreach($echeck as $c){
		$s = array();
		$s["email"] = $c["email"];
		array_push($e["s"],$s);
	}
	
	$pcheck = $coll->find(["phone_num"=>$phone_num]);
	$p["s"] = array();
	foreach($pcheck as $c){
		$s = array();
		$s["phone_num"] = $c["phone_num"];
		array_push($p["s"],$s);
	}
	//$r["success"] = 1;
	//echo json_encode($r);
	if (count($u["s"]) !== 0) {
		echo "user name exists";
	} elseif (count($e["s"]) !== 0) {
		echo "email exists";
	} elseif (count($p["s"]) !== 0) {
		echo "phone exists";
	} else {
		$details = array(
		"time" => time(),
		"otp" => $otp,
		"name" => $name,
		"email" => $email,
		"phone_num" => $phone_num,
		"username" => $username,
		"password" => $password);
		
		$pending->insert($details);
		$a["thedata"] = array("data"=>"otp sent");
		echo "otp sent";
	}

?>