<?php

//connect to the database
header('Content-type: application/json');

$host="";
$user="";
$dbname="";
$dbpassword="";
$sql="";

if (isset($_POST['dbhost'])) $host=$_POST['dbhost'];
if (isset($_POST['dbuser'])) $user=$_POST['dbuser'];
if (isset($_POST['dbname'])) $dbname=$_POST['dbname'];
if (isset($_POST['dbpassword'])) $dbpassword=$_POST['dbpassword'];

$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//sql insert statement
if (isset($_POST['sqlStatement'])) $sql=$_POST['sqlStatement'];

//check if connection to db is successful
if ($connection!=null) {

	if (mysqli_query($connection,$sql)) {
		$response = ['code' 		=> 9, 
					'message' 		=> 'message_insert_successful'
		];
		echo json_encode( $response );
	} else {
		$response = ['code' 		=> 10, 
					'message' 		=> 'error_insert_failed'
		];
		echo json_encode( $response );
	}
	$connection->close();
}

?>