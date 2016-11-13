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

//sql update statement
if (isset($_POST['sqlStatement'])) $sql=$_POST['sqlStatement'];

//check if connection to db is successful
if ($connection!=null) {

	if ($connection->query($sql) === TRUE) {
		$response = ['code' 		=> 5, 
					'message' 		=> 'message_update_successful'
		];
		echo json_encode( $response );
	} else {
		$response = ['code' 		=> 6, 
					'message' 		=> 'error_update_failed'
		];
		echo json_encode( $response );
	}
	$connection->close();
}

?>