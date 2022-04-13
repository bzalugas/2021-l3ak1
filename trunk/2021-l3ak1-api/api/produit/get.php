<?php

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Produit.php';
require_once '../../config/Database.php';
require_once '../../vendor/autoload.php';

define("TOKEN_BARCODE_LOOKUP", "zh7hswujxmkc6gh00wgawy08emyc27");

$db = (new Database())->getConnection();
$produit = new Produit($db);

function getFromApi($produit)
{
	$res = getFromOpenFFApi($produit);
	if ($res == false)
		$res = getFromBarcodeLookupApi($produit);
	return $res;
}

function getFromOpenFFApi($produit)
{
	$apiFood = new OpenFoodFacts\Api('food', 'fr');
	$apiBeauty = new OpenFoodFacts\Api('beauty', 'fr');
	$apiPet = new OpenFoodFacts\Api('pet', 'fr');
	try
	{
		$tmp = $apiFood->getProduct($produit->codeBarres)->getData();
	}
	catch(OpenFoodFacts\Exception\ProductNotFoundException)
	{
		try
		{
			$tmp = $apiBeauty->getProduct($produit->codeBarres)->getData();
		}
		catch(OpenFoodFacts\Exception\ProductNotFoundException)
		{
			try
			{
				$tmp = $apiPet->getProduct($produit->codeBarres)->getData();
			}
			catch(OpenFoodFacts\Exception\ProductNotFoundException)
			{
				return false;
			}
		}
	}
	$infos = [
		"marque" => $tmp['brands'] ?? null,
		"nom" => $tmp['product_name_fr'] ?? $tmp['product_name'],
		"quantite" => $tmp['quantity'] ?? null,
		"imagePath" => $tmp['image_url'] ?? null
	];
	return $infos;
}

function getFromBarcodeLookupApi($produit)
{
	$curl = curl_init();

	curl_setopt_array($curl, [
	CURLOPT_URL => "https://api.barcodelookup.com/v3/products?barcode=$produit->codeBarres&formatted=y&key=".TOKEN_BARCODE_LOOKUP,
	CURLOPT_RETURNTRANSFER => true,
	CURLOPT_CUSTOMREQUEST => "GET"
	]);

	$res = json_decode(curl_exec($curl));
	$err = curl_error($curl);

	curl_close($curl);

	if ($err || $res == null)
		return false;
	else
	{
		$tmp = [
			"marque" => $res->products[0]->brand ?? null,
			"nom" => $res->products[0]->title,
			"quantite" => null,
			"imagePath" => $res->products[0]->images[0] ?? null
		];
		return $tmp;
	}
}

if (!isset($_GET['codeBarres']))
	die('Missing barcode');
$produit->codeBarres = $_GET['codeBarres'];
$res = $produit->find();
if ($res != false)
	echo json_encode($res);
else
{
	$infos = getFromApi($produit);
	if ($infos == false)
	{
		http_response_code(404);
		die(json_encode(["exists" => false]));
	}
	$produit->setAttributes($infos);
	$test = $produit->insert();
	if ($test == false)
	{
		http_response_code(404);
		die("Error inserting data");
	}
	echo json_encode($produit->getAttributes());
}
?>