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
		"marque" => $tmp['brands'] ?? "Unknown brand",
		"nom" => $tmp['product_name_fr'] ?? $tmp['product_name'],
		"contenu" => $tmp['quantity'] ?? "",
		"imagePath" => $tmp['image_url']
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
			"marque" => $res->products[0]->brand,
			"nom" => $res->products[0]->title,
			"contenu" => "",
			"imagePath" => $res->products[0]->images[0]
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
		die(json_encode(["exists" => false]));
	}
	$produit->setAttributes($infos);
	$produit->insert();
	echo json_encode($produit->getAttributes());
}
?>