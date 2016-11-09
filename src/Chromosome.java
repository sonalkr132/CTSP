import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int collected_prize;
  ArrayList<Integer> genes;
  Facilites facilites;

  Chromosome(CustomersAllocation cust_allocation, Facilites _facilites, int prize){
    genes = new ArrayList<Integer>();
    facilites = _facilites;
    ArrayList<Integer> facilites_set = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) facilites_set.add(i);
 
    int collected_prize = 0;
    while(collected_prize < prize){
      int picked_facility = pick_next_facility(facilites_set);
      collected_prize += cust_allocation.customers_per_facility[picked_facility];
    }
    shuffle_array(genes);
    score = evaluate(facilites.map);
  }
  
  Chromosome(double _score, int _prize, ArrayList<Integer> _genes){
    score = _score;
    collected_prize = _prize;
    genes = _genes;
  }
  
  private int pick_next_facility(ArrayList<Integer> facilites_set){
    Random rand = new Random();
    int idx = rand.nextInt(facilites_set.size());
    int facility = facilites_set.remove(idx);
    genes.add(facility);
    return facility;
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
  
  private void shuffle_array(ArrayList<Integer> a) {
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
    
    while(collected_prize < prize){
      int facility = unvisited_facilites.get(0);
      genes.add(facility);
      collected_prize += ca.customers_per_facility[facility];
      unvisited_facilites.remove(0);
    }
    
    score = evaluate(facilites.map);
  }
  
  private void remove_duplicates(){
    ArrayList<Integer> dup = new ArrayList<Integer>();
    Iterator<Integer> iterator = genes.iterator();
    while(iterator.hasNext()){
      int gene = (int) iterator.next();
      if(!dup.contains(gene)) dup.add(gene);
    }
    
    genes = dup;
  }
  
  private ArrayList<Integer> unvisited_facilites(){
    ArrayList<Integer> unvisited_facilites = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) {
      while(!genes.contains(i)) unvisited_facilites.add(i);
    }
    
    return unvisited_facilites;
  }
  
  public Chromosome mutate(CustomersAllocation cust_allocation, int prize){
    Chromosome clone = new Chromosome(score, collected_prize, genes);
    Random rand = new Random();
    int idx = rand.nextInt(genes.size());
    
    int facility = clone.genes.remove(idx);
    clone.collected_prize -= cust_allocation.customers_per_facility[facility];
    recover(prize, cust_allocation);
    return clone;
  }
}
