<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$category = $_GET["category"];
	$user = $_GET["user"];
	$coll = $db->selectCollection($category);
	
	$map = new MongoCode("function() { ".
	"for(i in this.review){".
	"if(this.review[i].username == '".$user."'){".
	"emit(this.name , this.location); }}}");
	$reduce = new MongoCode("function(key, value){return val;}");
	$mapReduceComm = $db->command(array(
    "mapreduce" => $category,
    "map" => $map,
    "reduce" => $reduce,
    "out" => array("merge"=>"mapred1")));
	//print_r($mapReduceComm);
	$rresult = $db->selectCollection($mapReduceComm['result'])->find();
	$t["stuff"] = array();
	foreach($rresult as $c){
		$s=array();
		$s["name"] = $c["_id"];
		$s["lat"] = $c["value"][0]["lat"];
		$s["long"] = $c["value"][0]["long"];
		array_push($t["stuff"],$s);
	}
	$db->$mapReduceComm['result']->drop();
	echo json_encode($t);
?>