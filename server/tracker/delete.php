<?php

//connect to the database

$host=$_POST['dbhost'];
$user=$_POST['dbuser'];
$dbname=$_POST['dbname'];
$dbpassword=$_POST['dbpassword'];
$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//sql delete statement
$sql=$_POST['sqlStatement'];

//check if connection to db is successful
if ($connection!=null) {

	if ($connection->query($sql) === TRUE) {
		$response["code"] = 11;	
		$response["message"] = "message_delete_successful";
	} else {
		$response["code"] = 12;	
		$response["message"] = "error_delete_failed";
	}
	$connection->close();
}
die(json_encode($response));

?>