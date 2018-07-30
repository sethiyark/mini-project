<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$category = $_GET["category"];
	$name = $_GET["name"];
	$coll = $db->selectCollection($category);
	
	$rresult = $coll->find(array("name"=>$name));
	$t["stuff"] = array();
	foreach($rresult as $c){
		$s=array();
		$s["name"] = $c["name"];
		$s["lat"] = $c["location"][0]["lat"];
		$s["long"] = $c["location"][0]["long"];
		array_push($t["stuff"],$s);
	}
	echo json_encode($t);
?>