<?php

	$m = new MongoClient();
	$db = $m->ReviewSys;
	$category = $_GET["category"];
	$sortby = $_GET["sortby"];
	$asc = $_GET["asc"];
	$user = $_GET["user"];
	$coll = $db->selectCollection($category);
	
	$map = new MongoCode("function() { ".
	"for(i in this.review){".
	"if(this.review[i].username == '".$user."'){".
	" var stars = this.review[i].stars;".
	"emit(this.name ,{count : 1, stars : stars}); }}}");
	$reduce = new MongoCode("function(key, value){".
	"var val={count :0,stars:0};".
	"for(i in value){".
	"val.stars += value[i].stars;".
	"val.count += value[i].count;}".
	"return val;}");
	$finalize = new MongoCode("function(name, value){ if(value.count != 0){value.stars = (value.stars/value.count); return value;}else{return value}}");
	
	$mapReduceComm = $db->command(array(
    "mapreduce" => $category,
    "map" => $map,
    "reduce" => $reduce,
	"finalize" => $finalize,
    "out" => array("merge"=>"mapred1")));
	//print_r($mapReduceComm);
	$nsort = array();
	if($sortby == "name" && $asc == "asc"){
		$a = array('$sort'=>array("_id"=>1));
	} else if ($sortby == "name" && $asc == "des"){
		$a = array('$sort'=>array("_id"=>-1));
	} else if($sortby == "rating" && $asc == "asc"){
			$a = array('$sort'=>array("value.stars"=>1));
	} else if($sortby == "rating" && $asc == "des"){
			$a = array('$sort'=>array("value.stars"=>-1));
	} else if ($sortby == "name") {
		$a = array('$sort'=>array("_id"=>1));
	} else {
			$a = array('$sort'=>array("value.stars"=>-1));
	}
	array_push($nsort,$a);
	$rresult = $db->selectCollection($mapReduceComm['result'])->aggregate($nsort);
	$t["stuff"] = array();
	$found = array();
	foreach($rresult as $res){
		array_push($found,$res);
	}
	foreach($found[0] as $c){
		$s=array();
		$s["name"] = $c["_id"];
		$s["rating"] = $c["value"]["stars"];
		array_push($t["stuff"],$s);
	}
	$db->$mapReduceComm['result']->drop();
	echo json_encode($t);
?>