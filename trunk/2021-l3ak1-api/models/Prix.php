<?php

class Prix
{
	private $db = null;
	public $id;
	public $codeBarres;
	public $prix;
	public $datePrix;
	public $localisation_id;

	public function __construct($db)
	{
		$this->db = $db;
	}

	public function setAttributes($infos = [])
	{
		$this->id = $infos['id'] ?? null;
		$this->codeBarres = $infos['produit_codebarres'];
		$this->prix = $infos['prix'];
		$this->datePrix = $infos['dateprix'];
		$this->localisation_id = $infos['localisation_id'];
	}

	public function insert()
	{
		$sql = "INSERT INTO Prix VALUES (DEFAULT, :Produit_codeBarres, :prix, :datePrix, :localisation_id)";
		try{
			$res = $this->db->prepare($sql);
			$res->execute([
				'Produit_codeBarres' => $this->codeBarres,
				'prix' => $this->prix,
				'datePrix' => $this->datePrix,
				'localisation_id' => $this->localisation_id
			]);
			return $res->rowCount();
		} catch (Exception $e){
			die("Erreur dans Prix : " . $e->getMessage());
		}
	}

	public function find()
	{
		$sql = "SELECT * FROM Prix WHERE id = :id";
		try
		{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				"id" => $this->id
			]);
			$res = $statement->fetch(PDO::FETCH_ASSOC);
			if ($res != false)
			{
				$this->codeBarres = $res['produit_codebarres'];
				$this->prix = $res['prix'];
				$this->datePrix = $res['dateprix'];
				$this->localisation_id = $res['localisation_id'];
			}
			return $res;
		}
		catch (Exception $e)
		{
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function findAllInfos()
	{
		$sql = "SELECT produit_codebarres, marque, Produit.nom as produit_nom, contenu, imagepath, prix, dateprix, localisation_id, latitude, longitude, Localisation.nom as localisation_nom
		FROM Prix 
		INNER JOIN Produit ON (id = :id AND Prix.produit_codebarres = Produit.codebarres)
		INNER JOIN Localisation ON (Prix.localisation_id = Localisation.id)";
		try
		{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				"id" => $this->id
			]);
			$res = $statement->fetch(PDO::FETCH_ASSOC);
			if ($res != false)
			{
				$this->codeBarres = $res['produit_codebarres'];
				$this->prix = $res['prix'];
				$this->datePrix = $res['dateprix'];
				$this->localisation_id = $res['localisation_id'];
			}
			return $res;
		}
		catch (Exception $e)
		{
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function getPrixProduit()
	{
		$sql = "SELECT * FROM Prix WHERE Produit_codeBarres = :codeBarres ORDER BY datePrix DESC";
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				'codeBarres' => $this->codeBarres
			]);
			$res = $statement->fetchAll(PDO::FETCH_ASSOC);
			return $res;
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function getPrixProduitLoc()
	{

	}
}
?>