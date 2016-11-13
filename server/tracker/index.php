<?php

//connect to the database
header('Content-type: application/json');

$host="";
$user="";
$dbname="";
$dbpassword="";

if (isset($_POST['dbhost'])) $host=$_POST['dbhost'];
if (isset($_POST['dbuser'])) $user=$_POST['dbuser'];
if (isset($_POST['dbname'])) $dbname=$_POST['dbname'];
if (isset($_POST['dbpassword'])) $dbpassword=$_POST['dbpassword'];

$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//user data

$username="";
$password="";
$email="";
$deviceid="";

if (isset($_POST['username'])) $username=$_POST['username'];
if (isset($_POST['password'])) $password=$_POST['password'];
if (isset($_POST['email'])) $email=$_POST['email'];
if (isset($_POST['deviceid'])) $deviceid=$_POST['deviceid'];

//check if connection to db is successful
if ($connection!=null) {
	//Check if email is existing in the database
	
	$result=mysqli_query($connection,"SELECT USR_EML FROM USER WHERE USR_EML='$email';");
	$row=mysqli_fetch_array($result);
	$data=$row[0];

	if ($data!=null) {
		//validates password
		
		$result=mysqli_query($connection,"SELECT USR_PSSWRD,USR_NM,ID,USR_FIREBASEID,USR_STATUS,USR_DEVICEID FROM USER WHERE USR_EML='$email';");

			$row=mysqli_fetch_array($result);
			$pw=$row[0];
			$username=$row[1];
			$userid=$row[2];
			$firebaseid=$row[3];
			$status=$row[4];
	
		if ($pw==$password) {
			
			if ($status==0) {
				//updates user status
				$sql = "UPDATE USER SET USR_STATUS=1 WHERE USR_EML='$email';";
				if ($connection->query($sql) === TRUE) {
					$response = ['code' 		=> 1, 
								 'message' 		=> 'message_login_successful', 
								 'userid' 		=> "$userid", 
								 'username' 	=> "$username", 
								 'firebaseid' 	=> "$firebaseid" 
					];
					echo json_encode( $response );
				}
			}
			else {
					$response = ['code' 		=> 71, 
								 'message' 		=> 'error_multiple_login_not_allowed'
					];
					echo json_encode( $response );
			}
		}
		else {
			$response = ['code' 		=> 0, 
						 'message' 		=> 'error_incorrect_password'
			];
			echo json_encode( $response );
		}
	}
	else {

		//Check if device is already registered
        $result=mysqli_query($connection,"SELECT USR_EML FROM USER WHERE USR_DEVICEID='$deviceid';");

		$row=mysqli_fetch_array($result);
		$data=$row[0];

		if ($data==null) {

			//insert values to database
			$sql="INSERT INTO USER VALUES(null,'$username','$password','$email',0,null,null);";
			if (mysqli_query($connection,$sql)) {
				$sql = "UPDATE USER SET USR_STATUS=1 WHERE USR_EML='$email';";
				if ($connection->query($sql) === TRUE) {

					$result=mysqli_query($connection,"SELECT ID,USR_FIREBASEID FROM USER WHERE USR_EML='$email';");

						$row=mysqli_fetch_array($result);
						$userid=$row[0];
						$firebaseid=$row[1];
						
						$response = ['code' 		=> 2, 
									 'message' 		=> 'message_reg_successful', 
									 'userid' 		=> "$userid", 
									 'username' 	=> "$username", 
									 'firebaseid' 	=> "$firebaseid" 
						];
						echo json_encode( $response );

				}
			}
			else {
				$response = ['code' 		=> 3, 
							'message' 		=> 'error_reg_failed'
				];
				echo json_encode( $response );
			}

		}
		else {
			$response = ['code' 		=> 23, 
						 'message' 		=> 'error_device_is_already_registered'
			];
			echo json_encode( $response );
		}
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