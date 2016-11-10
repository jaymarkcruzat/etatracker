<?php

//connect to the database

$host=$_POST['dbhost'];
$user=$_POST['dbuser'];
$dbname=$_POST['dbname'];
$dbpassword=$_POST['dbpassword'];
$connection=mysqli_connect($host,$user,$dbpassword,$dbname);

//user data

$username=$_POST['username'];
$password=$_POST['password'];
$email=$_POST['email'];
$deviceid=$_POST['deviceid'];

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
		$data=$row[0];
		$username=$row[1];
		$userid=$row[2];
		$firebaseid=$row[3];
		$status=$row[4];
		if ($data==$password) {
			
			if ($status==0) {
				//updates user status
				$sql = "UPDATE USER SET USR_STATUS=1 WHERE USR_EML='$email';";
				if ($connection->query($sql) === TRUE) {
					$response["code"] = 1;
					$response["message"] = "message_login_successful";
					$response["userid"] = $userid;
					$response["username"] = $username;
					$response["firebaseid"] = $firebaseid;
				}
			}
			else {
					$response["code"] = 71;
					$response["message"] = "error_multiple_login_not_allowed";
			}
		}
		else {
			$response["code"] = 0;	
			$response["message"] = "error_incorrect_password";
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

					$response["code"] = 2;
					$response["message"] = "message_reg_successful";
					$response["userid"] = $userid;
					$response["username"] = $username;
					$response["firebaseid"] = $firebaseid;
				}
			}
			else {
				$response["code"] = 3;
				$response["message"] = "error_reg_failed";
			}

		}
		else {
			$response["code"] = 23;
            $response["message"] = "error_device_is_already_registered";
		}
	}
	$connection->close();
}
else {
	$response["code"] = 4;	
	$response["message"] = "error_server_down";
}
die(json_encode($response));

?>