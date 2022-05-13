<?php

define("RAYON", 6371000);
class Localisation
{
	private $db = null;
	public $id;
	//latitude
	public $lat;
	//longitude
	public $long;
	public $nom;

	public function __construct($db)
	{
		$this->db = $db;
	}

	public function setAttributes($infos = [])
	{
		$this->id = $infos['id'] ?? null;
		$this->lat = $infos['latitude'];
		$this->long = $infos['longitude'];
		$this->nom = $infos['nom'];
	}

	public function insert()
	{
		$sql = "INSERT INTO Localisation VALUES (DEFAULT, :lat, :long, :nom)";
		try {
			$res = $this->db->prepare($sql);
			$res->execute([
				'lat' => $this->lat,
				'long' => $this->long,
				'nom' => $this->nom
			]);
			return $res->rowCount();
		} catch (Exception $e) {
			die ("Error in " . __CLASS__ . ' : ' . $e->getMessage());
		}
	}

	public function find()
	{
		$sql = "SELECT * FROM Localisation WHERE id = :id";
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				'id' => $this->id
			]);
			$res = $statement->fetch(PDO::FETCH_ASSOC);
			if ($res != false)
			{
				$this->lat = $res['latitude'];
				$this->long = $res['longitude'];
				$this->nom = $res['nom'];
			}
			return $res;
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function findByLatLong()
	{
		$sql = "SELECT * FROM Localisation WHERE latitude = :lat AND longitude = :long";
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				'lat' => $this->lat,
				'long' => $this->long
			]);
			$res = $statement->fetch(PDO::FETCH_ASSOC);
			if ($res != false)
			{
				$this->id = $res['id'];
				$this->nom = $res['nom'];
			}
			return $res;
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
		
	}

	public function findAllByRadius($radius)
	{
		$sql = 
		"SELECT * FROM Localisation
		WHERE (latitude between :leftLat AND :rightLat) AND (longitude between :leftLong AND :rightLong)";
		$leftLat = $this->lat - $radius / 111000;
		$rightLat = $this->lat + $radius / 111000;
		$leftLong = $this->long - $radius / (111000*cos(deg2rad($this->lat)));
		$rightLong = $this->long + $radius / (111000*cos(deg2rad($this->lat)));
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				'leftLat' => $leftLat,
				'rightLat' => $rightLat,
				'leftLong' => $leftLong,
				'rightLong' => $rightLong,
			]);
			$res = $statement->fetchAll(PDO::FETCH_ASSOC);
			return $res;
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function distance($lat, $long, $unit = 'm')
	{
		// $d = floatval(RAYON * (pi() / 2 - asin(sin($lat1) * sin($lat2) + cos($long1 - $long2) * cos($lat1) * cos($lat2))));
		$lat1 = deg2rad($this->lat);
		$long1 = deg2rad($this->long);
		$lat2 = deg2rad($lat);
		$long2 = deg2rad($long);
		$dlo = ($long2 - $long1) / 2;
        $dla = ($lat2 - $lat1) / 2;
        $a = (sin($dla) * sin($dla)) + cos($lat1) * cos($lat2) * (sin($dlo) * sin($dlo));
        $d = 2 * atan2(sqrt($a), sqrt(1 - $a));
		$meter = RAYON * $d;
		if ($unit == 'k')
			return $meter / 1000;
		return $meter;
	}

	public static function distanceLocs($lat1, $long1, $lat2, $long2, $unit = 'm')
	{
		$lat1 = deg2rad($lat1);
		$long1 = deg2rad($long1);
		$lat2 = deg2rad($lat2);
		$long2 = deg2rad($long2);
		$dlo = ($long2 - $long1) / 2;
        $dla = ($lat2 - $lat1) / 2;
        $a = (sin($dla) * sin($dla)) + cos($lat1) * cos($lat2) * (sin($dlo) * sin($dlo));
        $d = 2 * atan2(sqrt($a), sqrt(1 - $a));
		$meter = RAYON * $d;
		if ($unit == 'k')
			return $meter / 1000;
		return $meter;
	}
}
?>