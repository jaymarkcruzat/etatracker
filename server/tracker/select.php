<?php

//connect to the database

$host=$_POST['dbhost'];
$user=$_POST['dbuser'];
$dbname=$_POST['dbname'];
$dbpassword=$_POST['dbpassword'];
$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//sql update statement
$sql=$_POST['sqlStatement'];

//check if connection to db is successful
if ($connection!=null) {
	
	//execute select statement
	$result=mysqli_query($connection,$sql);
	if (mysqli_num_rows($result) > 0) {
		
		$response["code"] = 13;
		$response["message"] = "message_select_successful";
		$row_json="";
		while($row = mysqli_fetch_assoc($result)) {
			$row_json .= json_encode($row) . ",";
		}
		$row_json = substr($row_json,0,strlen($row_json)-1);
		$row_json = "{\"data\": [" . $row_json . "]}";
		$response["result"] = $row_json;
	} else {
			$response["code"] = 15;
			$response["message"] = "message_select_empty";
	}

	$connection->close();
}
else {
	$response["code"] = 4;
	$response["message"] = "error_server_down";
}
die(json_encode($response));

?>