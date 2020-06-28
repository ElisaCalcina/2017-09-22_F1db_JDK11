package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.formulaone.model.Adiacenze;
import it.polito.tdp.formulaone.model.Race;
import it.polito.tdp.formulaone.model.Season;

public class FormulaOneDAO {

	public List<Season> getAllSeasons() {
		String sql = "SELECT year, url FROM seasons ORDER BY year";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			List<Season> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Season(rs.getInt("year"), rs.getString("url")));
			}
			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> getSeason(){
		String sql="SELECT DISTINCT s.year as y " + 
				"FROM seasons AS s " + 
				"ORDER BY s.year asc ";
		
		List<Integer> result= new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				result.add(rs.getInt("y"));
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//vertici
	public List<Race> getRaces(Map<Integer, Race> idMap, Integer year){
		String sql="SELECT * " + 
				"FROM races AS r " + 
				"WHERE r.year=? ";
		
		List<Race> result= new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("raceId"))){
					Race r= new Race(rs.getInt("raceId"),
									Year.of(rs.getInt("Year")),
									rs.getInt("round"),
									rs.getInt("circuitId"),
									rs.getString("name"),
									rs.getDate("date").toLocalDate(),
									null,
									rs.getString("url"));
					idMap.put(r.getRaceId(), r);
					result.add(r);
									
				}
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenze> getArchi(Map<Integer, Race> idMap){
		String sql="SELECT r1.raceId as id1, r2.raceId as id2, COUNT(DISTINCT r1.driverId) AS peso " + 
				"FROM results r1, results r2 " + 
				"WHERE r1.raceId>r2.raceId " + 
				"		AND r1.statusId=r2.statusId " + 
				"		AND r1.statusId='1' " + 
				"		AND r1.driverId=r2.driverId " + 
				"GROUP BY r1.raceId, r2.raceId ";
		
		List<Adiacenze> result= new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				
				Race rc1= idMap.get(rs.getInt("id1"));
				Race rc2= idMap.get(rs.getInt("id2"));
				
				if(rc1!=null && rc2!=null) {
					Adiacenze a = new Adiacenze(rc1, rc2, rs.getInt("peso"));
					result.add(a);
				}
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
				
	}
}

