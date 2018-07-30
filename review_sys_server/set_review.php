<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$category = $_GET["category"];
	$username = $_GET["username"];
	$name = $_GET["name"];
	$comment = $_GET["comment"];
	$rating = floatval($_GET["rating"]);

	$coll = $db->selectCollection($category);
	
	$find = array("name"=>$name);
	
	$a = array('$pull'=>array("review"=>array("username"=>$username)));
	
	$res = $coll->update($find, $a);
	//echo json_encode($res);
	
	if($rating !== 0.0){
		$b = array('$push'=>array("review"=>array("username"=>$username, "stars"=>$rating, "comment"=>$comment)));
		$res = $coll->update($find, $b);
	
		echo "success";
	} else {
		echo "removed";
	}
?>