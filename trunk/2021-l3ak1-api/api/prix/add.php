<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json/');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type,Access-Control-Allow-Methods, Authorization, X-Requested-With');

require_once '../../config/Database.php';
require_once '../../models/Prix.php';

$db = (new Database())->getConnection();
$prix = new Prix($db);

if (isset($_POST['produit_codebarres']))
	$data = $_POST;
else
	$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['produit_codebarres']) || !isset($data['prix']) || !isset($data['dateprix']) || !isset($data['localisation_id']))
	die('Missing argument');

$prix->setAttributes($data);
echo json_encode(['rows inserted' => $prix->insert()]);
?>