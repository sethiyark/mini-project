<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$coll = $db->Registered_Users;
	
	$username = $_GET["username"];
	$password = $_GET["password"];
	
	$finduser = array(
	"username"=>$username);
	
	$findboth = array(
	"username"=>$username,
	"password"=>$password);
	
	$response["stuff"] = array();
	
	$uresult = $coll->find($finduser);
	$u["s"] = array();
	foreach($uresult as $c){
		$s = array();
		$s["username"] = $c["username"];
		array_push($u["s"],$s);
	}
	
	if(count($u["s"]) !== 0){
		
		$bresult = $coll->find($findboth);
		foreach($bresult as $c){
			$s = array();
			$s["name"] = $c["name"];
			$s["username"] = $c["username"];
			$s["email"] = $c["email"];
			$s["phone_num"] = $c["phone_num"];
			array_push($response["stuff"],$s);
		}
		
		if(count($response["stuff"]) !== 0){
			$response["result"] = "success";
			echo (json_encode($response));
		} else{
			$response["result"] = "wrong password";
			echo (json_encode($response));
		}
		
	} else{
		$response["result"] = "no user";
		echo (json_encode($response));
	}
	
?>