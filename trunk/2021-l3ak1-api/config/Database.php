<?php

class Database
{
	// private $dotenv;
	private $dbConn = null;

	public function __construct()
	{
		// $this->dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
		// $this->dotenv->load();
		// $host = $_ENV['DB_HOST'];
		// $port = $_ENV['DB_PORT'];
		// $db = $_ENV['DB_DATABASE'];
		// $user = $_ENV['DB_USERNAME'];
		// $pass = $_ENV['DB_PASSWORD'];

		$db = parse_url(getenv("DATABASE_URL"));

		try
		{
			$this->dbConn = new PDO("pgsql:" . sprintf(
				"host=%s;port=%s;user=%s;password=%s;dbname=%s",
				$db["host"],
				$db["port"],
				$db["user"],
				$db["pass"],
				ltrim($db["path"], "/")
				)
			);
		}
		catch (Exception $e)
		{
			die('Error in' . __CLASS__ . ' : '.$e->getMessage());
		}

		// try
		// {
		// 	$this->dbConn = new PDO(
		// 		"pgsql:host=$host;port=$port;charset=utf8;dbname=$db",
		// 		$user,
		// 		$pass,
		// 		[PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION],
		// 	);
		// }
		// catch (Exception $e)
		// {
		// 	die('Error in' . __CLASS__ . ' : '.$e->getMessage());
		// }
	}

	public function getConnection()
	{
		return $this->dbConn;
	}
}

?>