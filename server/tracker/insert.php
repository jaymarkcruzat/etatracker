<?php

//connect to the database

$host=$_POST['dbhost'];
$user=$_POST['dbuser'];
$dbname=$_POST['dbname'];
$dbpassword=$_POST['dbpassword'];
$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//sql insert statement
$sql=$_POST['sqlStatement'];

//check if connection to db is successful
if ($connection!=null) {

	if (mysqli_query($connection,$sql)) {
		$response["code"] = 9;	
		$response["message"] = "message_insert_successful";
	} else {
		$response["code"] = 10;	
		$response["message"] = "error_insert_failed";
	}
	$connection->close();
}
die(json_encode($response));

?>