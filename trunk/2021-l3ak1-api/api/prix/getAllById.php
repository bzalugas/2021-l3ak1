<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Prix.php';
require_once '../../config/Database.php';
require_once '../../vendor/autoload.php';

$db = (new Database())->getConnection();
$prix = new Prix($db);

$nb = $_GET['nbPrix'] ?? die("missing argument nbPrix");
$data = json_decode(file_get_contents("php://input"), true);
$allInfos = array();
foreach ($data as $id)
{
	$prix->id = $id;
	$res = $prix->findAllInfos();
	if ($res == false)
		die("Error");
	array_push($allInfos, $res);
}

echo json_encode($allInfos);

?>