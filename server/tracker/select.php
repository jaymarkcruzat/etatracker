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
	
	//execute select statement
	$result=mysqli_query($connection,$sql);
	
	if (mysqli_num_rows($result) > 0) {
		
		$row_json="";
		while($row = mysqli_fetch_assoc($result)) {
			$row_json .= json_encode($row) . ",";
		}
		$row_json = substr($row_json,0,strlen($row_json)-1);
		$row_json = "{\"data\": [" . $row_json . "]}";
		
		$response = ['code' 		=> 13, 
					'message' 		=> 'message_select_successful',
					'result' 		=> "$row_json"
		];
		echo json_encode( $response );
	} else {

			$response = ['code' 		=> 15, 
						'message' 		=> 'message_select_empty'
			];
			echo json_encode( $response );
	}

	$connection->close();
}
else {
	$response = ['code' 		=> 4, 
				'message' 		=> 'error_server_down'
	];
	echo json_encode( $response );
}

?>