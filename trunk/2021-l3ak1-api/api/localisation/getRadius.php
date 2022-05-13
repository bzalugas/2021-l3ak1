<?php
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');

require_once '../../models/Localisation.php';
require_once '../../config/Database.php';

$db = (new Database())->getConnection();
$loc = new Localisation($db);

if (!isset($_GET['latitude']) || !isset($_GET['longitude']) || !isset($_GET['radius']))
{
	http_response_code(404);
	die('Missing latitude or longitude or radius');
}

$radius = $_GET['radius'];
$loc->lat = $_GET['latitude'];
$loc->long = $_GET['longitude'];

// if ($loc->findByLatLong() == false)
// {
// 	http_response_code(404);
// 	die('Could not find location');
// }

$locs = $loc->findAllByRadius($radius);

$metric = 'm';
if (isset($_GET['metric']))
	$metric = $_GET['metric'];

foreach($locs as &$l)
{
	$l['distance'] = $loc->distance($l['latitude'], $l['longitude'], $metric);
}

echo json_encode($locs);

?>