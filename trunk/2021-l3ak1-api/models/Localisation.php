<?php

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
		$this->lat = $infos['lat'];
		$this->long = $infos['long'];
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
}
?>