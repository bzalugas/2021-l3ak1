<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Prix.php';
require_once '../../config/Database.php';

$db = (new Database())->getConnection();
$prix = new Prix($db);

if (!isset($_GET['codeBarres']) || !isset($_GET['localisation_id']))
{
	http_response_code(404);
	die('Missing codeBarres or localisation_id');
}

$prix->codeBarres = $_GET['codeBarres'];
$prix->localisation_id = $_GET['localisation_id'];

$res = $prix->findPrixProduitLoc();
if ($res == null)
	http_response_code(404);
echo (json_encode($res));

?>