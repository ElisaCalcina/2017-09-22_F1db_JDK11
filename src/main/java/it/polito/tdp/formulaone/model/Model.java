package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO dao;
	private Graph<Race, DefaultWeightedEdge> grafo;
	private Map<Integer, Race> idMap;
	private List<Race> vertici;
	
	public Model() {
		dao= new FormulaOneDAO();
		idMap= new HashMap<>();
	}

	public List<Integer> getSeason(){
		return dao.getSeason();
	}
	
	public void creaGrafo(Integer anno) {
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		vertici= dao.getRaces(idMap, anno);
		
		Graphs.addAllVertices(grafo, vertici);
		
		
		for(Adiacenze a: dao.getArchi(idMap)) {
			if(a.getR1()!=a.getR2() && this.grafo.containsVertex(a.getR1()) && this.grafo.containsVertex(a.getR2())) {
				Graphs.addEdgeWithVertices(grafo, a.getR1(), a.getR2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato con"+ this.grafo.vertexSet().size()+"vertici e con "+ this.grafo.edgeSet().size());
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenze> getPeso(){
		int pesoMax=0;
		
		List<Adiacenze> result= new ArrayList<>();
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pesoMax) {
				pesoMax= (int) this.grafo.getEdgeWeight(e);
			}
		}
		
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)==pesoMax) {
				result.add(new Adiacenze(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), pesoMax));
			}
		}
		return result;
		
		
	}
}
