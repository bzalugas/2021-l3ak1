<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json/');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type,Access-Control-Allow-Methods, Authorization, X-Requested-With');

require_once '../../config/Database.php';
require_once '../../models/Localisation.php';

$db = (new Database())->getConnection();
$loc = new Localisation($db);

if (isset($_POST['latitude']))
	$data = $_POST;
else
	$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['latitude']) || !isset($data['longitude']) || !isset($data['nom']))
{
	http_response_code(404);
	die('Missing argument');
}

$loc->setAttributes($data);

echo json_encode(['rows inserted' => $loc->insert()]);

?>