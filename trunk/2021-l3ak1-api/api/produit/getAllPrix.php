<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Produit.php';
require_once '../../models/Prix.php';
require_once '../../config/Database.php';

$db = (new Database())->getConnection();
$produit = new Produit($db);

if (!isset($_GET['codeBarres']))
	die('Missing barcode');
$produit->codeBarres = $_GET['codeBarres'];
$infos = $produit->find();
if ($infos == false)
{
	http_response_code(404);
	die('Unknown barcode');
}
// $res = [
// 	'Produit' => $infos,
// ];

$prix = new Prix($db);
$prix->codeBarres = $produit->codeBarres;
$allPrices = $prix->getPrixProduit();
$res = [];
foreach ($allPrices as $singlePrice)
	array_push($res, $singlePrice);
echo json_encode($res);
?>