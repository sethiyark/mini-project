<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$category = $_GET["category"];
	$username = $_GET["username"];
	$name = $_GET["name"];
	$coll = $db->selectCollection($category);
	
	$finddetails = array("name" => $name);
	
	$details = $coll->find($finddetails);
	
	$response["details"] = array();
	foreach($details as $d){
		$s = array();
		$s["name"] = $d["name"];
		$s["review"] = $d["review"];
		$s["description"] = $d["description"];
		$s["picture"] = $d["picture"];
		array_push($response["details"], $s);
	}
	
	$response["udetails"] = array();
	if($username){
		foreach($response["details"][0]["review"] as $r){
			if($r["username"]==$username){
				$s = array();
				$s["username"] = $r["username"];
				$s["stars"] = $r["stars"];
				$s["comment"] = $r["comment"];
				array_push($response["udetails"], $s);
			}
		}
	}
	
	/*
	if($username){
		$map = new MongoCode("function(){".
			"for(i in this.review){".
			"var stars = this.review[i].stars;".
			"var username = this.review[i].username;".
			"var comment = this.review[i].comment;".
			"emit(username, {stars:stars, comment:comment});}}");
		$reduce = new MongoCode("function(key, value){".
		"return ({stars:value.stars, comment:value.comment});}");
		$mapReduceComm = $db->command(array(
		"mapreduce" => $category,
		"map" => $map,
		"reduce" => $reduce,
		"out" => array("merge"=>"mapred1")));
		echo json_encode($mapReduceComm);
		$result = $db->selectCollection($mapReduceComm['result'])->find();
		foreach($result as $r){
			$s = array();
			$s["username"] = $r["_id"];
			$s["rating"] = $r["value"];
			array_push($response["udetails"],$s);
		}
		$db->$mapReduceComm['result']->drop();
	}*/
	
	$map1 = new MongoCode("function() { for(i in this.review){ var stars = this.review[i].stars;".
	"emit(this.name ,{count : 1, stars : stars}); }}");
	$reduce1 = new MongoCode("function(key, value){".
	"var val={count :0,stars:0};".
	"for(i in value){".
	"val.stars += value[i].stars;".
	"val.count += value[i].count;}".
	"return val;}");
	$finalize1 = new MongoCode("function(name, value){ value.stars = (value.stars/value.count); return value}");
	
	$mapReduceComm1 = $db->command(array(
    "mapreduce" => $category,
    "map" => $map1,
    "reduce" => $reduce1,
	"finalize" => $finalize1,
    "out" => array("merge"=>"mapred1")));
	//print_r($mapReduceComm);
	$result1 = $db->selectCollection($mapReduceComm1['result'])->find(array("_id"=>$name));
	$response["avgstar"] = array();
	foreach($result1 as $r){
		$s = array();
		$s["username"] = $r["_id"];
		$s["rating"] = $r["value"];
		array_push($response["avgstar"],$s);
	}
	$db->$mapReduceComm1['result']->drop();
	echo json_encode($response);
		
	
?>