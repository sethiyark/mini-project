<?php
	$m = new MongoClient();
	$db = $m->ReviewSys;
	$coll = $db->Registered_Users;
	
	$username = $_GET["username"];
	$name = $_GET["name"];
	$phone = $_GET["phone"];
	
	$pcheck = $coll->find(["phone_num"=>$phone]);
	$p = array();
	foreach($pcheck as $c){
		$s = array();
		$s["phone_num"] = $c["phone_num"];
		$s["username"] = $c["username"];
		array_push($p,$s);
	}
	if (count($p) !== 0) {
		if($p[0]["username"] == $username){
			$coll->update(array("username"=>$username),array('$set'=>array("name"=>$name, "phone_num"=>$phone)));
			echo "success";
		}
		else{
			echo "phone exists";
		}
	} else {
		$coll->update(array("username"=>$username),array('$set'=>array("name"=>$name, "phone_num"=>$phone)));
		echo "success";
	}
?>