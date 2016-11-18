import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int collected_prize;
  ArrayList<Integer> genes;
  Facilites facilites;
  int ITR_LIMIT = 100;

  Chromosome(CustomersAllocation cust_allocation, Facilites _facilites, int prize){
    genes = new ArrayList<Integer>();
    facilites = _facilites;
    ArrayList<Integer> facilites_set = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) facilites_set.add(i);
 
    collected_prize = 0;
    while(collected_prize < prize){
      int picked_facility = pick_next_facility(facilites_set);
      collected_prize += cust_allocation.customers_per_facility[picked_facility];
    }
    shuffle_array(genes);
    score = evaluate(facilites.map);
  }
  
  Chromosome(double _score, int _clone_prize, ArrayList<Integer> _genes, Facilites _facilites){
    score = _score;
    collected_prize = _clone_prize;
    genes = new ArrayList<Integer>(_genes);
    facilites = _facilites;
  }
  
  public void set_prize_and_score(CustomersAllocation ca){
    collected_prize = 0;
    for(int i =0; i < genes.size(); i++) collected_prize += ca.customers_per_facility[genes.get(i)];
    score = evaluate(facilites.map);
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
      int facility = unvisited_facilites.remove(0);
      genes.add(facility);
      collected_prize += ca.customers_per_facility[facility];
    }
    score = evaluate(facilites.map);
  }
  
  public void two_opt(){
    double initial_score = score, cur_score;
    Random rand = new Random();
    int opt1, opt2, range = genes.size();
    ArrayList<Integer> cur_genes = genes;

    for(int i = 0; i < ITR_LIMIT; i++){
      opt1 = rand.nextInt(range);
      opt2 = rand.nextInt(range);
      
      swap(genes, opt1, opt2);
      cur_score = evaluate(facilites.map);
      if(cur_score >= initial_score) genes = cur_genes;
      else {
        //better solution was found
        //original genes are already swapped
        cur_genes = genes;
        i = 0; //reset iterator
        score = cur_score;
      }
    }
  }
  
  public void drop_and_procedures(){
    
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
      if(!genes.contains(i)) unvisited_facilites.add(i);
    }
    
    return unvisited_facilites;
  }
  
  public static Chromosome mutate(CustomersAllocation cust_allocation, int prize, Chromosome to_clone){
    Chromosome clone = new Chromosome(to_clone.score, to_clone.collected_prize, to_clone.genes, to_clone.facilites);
    Random rand = new Random();
    int idx = rand.nextInt(to_clone.genes.size());
    
    int facility = clone.genes.remove(idx);
    clone.collected_prize -= cust_allocation.customers_per_facility[facility];
    clone.recover(prize, cust_allocation);
    return clone;
  }
}
