<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Localisation.php';
require_once '../../config/Database.php';
require_once '../../vendor/autoload.php';

$db = (new Database())->getConnection();
$loc = new Localisation($db);

if (!isset($_GET['id']))
	die('Missing id');
$loc->id = $_GET['id'];
$res = $loc->find();
if ($res != false)
	echo json_encode($res);
else
{
	http_response_code(404);
	die();
}
	
?>