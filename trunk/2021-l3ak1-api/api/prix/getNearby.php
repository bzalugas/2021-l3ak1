<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Prix.php';
require_once '../../models/Localisation.php';
require_once '../../models/Produit.php';
require_once '../../config/Database.php';

$db = (new Database())->getConnection();
$produit = new Produit($db);
$prix = new Prix($db);
$loc = new Localisation($db);

if (!isset($_GET['codeBarres']) || !isset($_GET['latitude']) || !isset($_GET['longitude']) || !isset($_GET['radius']))
{
	http_response_code(404);
	die('Missing codeBarres or localisation_id or radius');
}

$loc->lat = $_GET['latitude'];
$loc->long = $_GET['longitude'];
$prix->codeBarres = $_GET['codeBarres'];

$locList = $loc->findAllByRadius($_GET['radius']);
$locIds = [];
foreach ($locList as $l)
	array_push($locIds, $l['id']);

$prices = $prix->findPrixProduitAllLoc($locIds);

if ($prices == null)
	http_response_code(404);

echo (json_encode($prices));

?>