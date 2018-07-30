<?php

	$m = new MongoClient();
	
	$db = $m->ReviewSys;
	
	$pending = $db->new_password_pending;
	
	$password = $_GET["password"];
	
	$email = $_GET["email"];
	
	$findarray = array(
	"email"=>$email);
	
	$get_details = $pending->find($findarray);
	
	$u["s"] = array();
	foreach($get_details as $c){
		$s = array();
		$s["username"] = $c["username"];
		array_push($u["s"],$s);
	}
	if(count($u["s"]) !== 0){
		$coll = $db->Registered_Users;
		$coll->update($u["s"][0],array('$set'=>array("password"=>$password)));
		$pending->remove($findarray);
		echo "success";
	}else{
		echo "fail";
	}

?>