<?php

class Produit
{
	private $db = null;
	public $codeBarres;
	public $marque;
	public $nom;
	public $quantite;
	public $imagePath;

	public function __construct($db)
	{
		$this->db = $db;
	}

	public function minAttributesSet()
	{
		return ($this->codeBarres != null && $this->marque != null && $this->nom != null);
	}

	public function setAttributes($infos = [])
	{
		if (!isset($this->codeBarres))
			$this->codeBarres = $infos['codeBarres'];
		$this->marque = $infos['marque'];
		$this->nom = $infos['nom'];
		$this->quantite = $infos['quantite'];
		$this->imagePath = $infos['imagePath'];
	}

	public function getAttributes()
	{
		$attributes = [
			'codebarres' => $this->codeBarres,
			'marque' => $this->marque,
			'nom' => $this->nom,
			'quantite' => $this->quantite,
			'imagepath' => $this->imagePath
		];
		return $attributes;
	}

	public function insert()
	{
		if (!$this->minAttributesSet())
			return false;
		$sql = "INSERT INTO Produit VALUES (:codeBarres, :marque, :nom, :quantite, :imagePath)";
		try{
			$res = $this->db->prepare($sql);
			$res->execute([
				'codeBarres' => $this->codeBarres,
				'marque' => $this->marque ?? NULL,
				'nom' => $this->nom,
				'quantite' => $this->quantite ?? NULL,
				'imagePath' => $this->imagePath ?? NULL
			]);
			return $res->rowCount();
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function findAll()
	{
		$sql = "SELECT * FROM Produit";
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute();
			$res = $statement->fetchAll(PDO::FETCH_ASSOC);
			return $res;
		} catch(Exception $e) {
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function find()
	{
		$sql = "SELECT * FROM Produit WHERE codeBarres = :codeBarres";
		try{
			$statement = $this->db->prepare($sql);
			$statement->execute([
				'codeBarres' => $this->codeBarres
			]);
			$res = $statement->fetch(PDO::FETCH_ASSOC);
			if ($res != false)
			{
				$this->codeBarres = $res['codebarres'];
				$this->marque = $res['marque'];
				$this->nom = $res['nom'];
				$this->quantite = $res['quantite'];
				$this->imagePath = $res['imagepath'];
			}
			return $res;
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function update(Array $infos)
	{
		$sql = "UPDATE Produit
				SET marque = :marque, nom = :nom, quantite = :quantite, imagePath = :imagePath
				WHERE codeBarres = :codeBarres";
		try{
			$res = $this->db->prepare($sql);
			$res->execute([
				'codeBarres' => $this->codeBarres,
				'marque' => $infos['marque'],
				'nom' => $infos['nom'],
				'quantite' => $infos['quantite'] ?? null,
				'imagePath' => $infos['imagePath'] ?? null
			]);
			return $res->rowCount();
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function delete()
	{
		$sql = "DELETE FROM Produit WHERE codeBarres = :codeBarres";
		try {
			$res = $this->db->prepare($sql);
			$res->execute([
				'codeBarres' => $this->codeBarres
			]);
			return $res->rowCount();
		} catch (Exception $e){
			die ('Erreur : ' . $e->getMessage());
		}
	}

	public function getAllPrix()
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
}

?>