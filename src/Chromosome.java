import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int prize, number_of_facilites;
  ArrayList<Integer> genes;

  Chromosome(CustomersAllocation cust_allocation, Facilites facilites, int prize){
    genes = new ArrayList<Integer>();
    ArrayList<Integer> facilites_set = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) facilites_set.add(i);
 
    int tmp_score = 0;
    int last_facility = pick_next_facility(facilites_set);
    while(tmp_score < prize){
      int cur_facility = pick_next_facility(facilites_set);
      tmp_score += facilites.map[last_facility][cur_facility];
    }
    
    shuffleArray(genes);
    score = evaluate(facilites.map);
  }
  
  private int pick_next_facility(ArrayList<Integer> facilites_set){
    Random rand = new Random();
    int idx = rand.nextInt(facilites_set.size());
    genes.add(facilites_set.get(idx));
    facilites_set.remove(idx);
    return idx;
  }
  
  //Evaluates the total sum of distances between the genes/points of the chromosome
  // @param dist 2D array of distance between all the points
  public double evaluate(int[][] dist){
    int size = genes.size();
    double sum = dist[genes.get(0)][genes.get(size - 1)];
    for(int i = 1; i < size; i++){
      sum += dist[genes.get(i)][genes.get(i - 1)];
    }
    return sum;
  }
  
  private void shuffleArray(ArrayList<Integer> a) {
    int n = a.size() ;
    Random random = new Random();
    random.nextInt();
    for (int i = 0; i < n; i++) {
      int change = i + random.nextInt(n - i);
      swap(a, i, change);
    }
  }
  
  public static void swap( ArrayList<Integer> list, int firstInd, int secondInd ){
     int temp = list.set( firstInd, list.get( secondInd ) ) ;
     list.set( secondInd, temp ) ;
  }
  
  public void recover(int prize, CustomersAllocation ca){
    remove_duplicates();
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    unvisited_facilites = ca.sort(unvisited_facilites);
    
    while(score < prize){
      genes.add(unvisited_facilites.get(0));
      unvisited_facilites.remove(0);
    }
  }
  
  private void remove_duplicates(){
    ArrayList<Integer> dup = new ArrayList<Integer>();
    Iterator iterator = genes.iterator();
    while(iterator.hasNext()){
      int gene = (int) iterator.next();
      if(!dup.contains(gene)) dup.add(gene);
    }
  }
  
  private ArrayList<Integer> unvisited_facilites(){
    ArrayList<Integer> unvisited_facilites = new ArrayList<Integer> ();
    int[] facilites_set = new int[number_of_facilites];
    for(int i = 0; i < number_of_facilites; i++) {
      while(!genes.contains(i)) unvisited_facilites.add(i);
    }
    
    return unvisited_facilites;
  }
}
