<?php
	$m = new MongoClient();
	$db = $m->ReviewSys;
	$username = $_GET["username"];
	$coll = $db->Registered_Users;
	$res = $coll->find(array("username"=>$username));
	foreach($res as $r){
		echo $r["name"];
	}
?>