<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Prix.php';
require_once '../../config/Database.php';

$db = (new Database())->getConnection();
$prix = new Prix($db);

if (!isset($_GET['codeBarres']))
{
	http_response_code(404);
	die("Missing barcode");
}

$prix->codeBarres = $_GET['codeBarres'];
$res = $prix->findCheapestForBarcode();

if ($res == null)
	http_response_code(404);

echo json_encode($res);
?>