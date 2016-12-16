import java.util.ArrayList;
import java.util.*;

import java.util.Random;

public class Chromosome {
  public double score;
  public int collected_prize;
  public ArrayList<Integer> genes;
  public ArrayList<Integer> facilites_set;
  final Facilites facilites;
  int ITR_LIMIT = 100;

  Chromosome(CustomersAllocation cust_allocation, Facilites _facilites, int prize){
    genes = new ArrayList<Integer>();
    facilites = _facilites;
    facilites_set = new ArrayList<Integer> ();
    for(int i = 0; i < facilites.number_of_facilites; i++) facilites_set.add(i);
 
    collected_prize = 0;
    while(collected_prize < prize){
      int picked_facility = add_next_facility();
      collected_prize += cust_allocation.customers_per_facility[picked_facility];
    }
    shuffle_array(genes);
    score = evaluate(facilites.map, facilites.depot_dist);
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
    score = evaluate(facilites.map, facilites.depot_dist);
  }
  
  public int add_next_facility(){
    Random rand = new Random();
    int idx = rand.nextInt(facilites_set.size());
    int facility = facilites_set.remove(idx);
    genes.add(facility);
    return facility;
  }
  
  //Evaluates the total sum of distances between the genes/points of the chromosome
  // @param dist 2D array of distance between all the points
  public double evaluate(double[][] dist, double[] depot_dist){
    int size = genes.size();
    double sum = depot_dist[genes.get(0)] + depot_dist[genes.get(size - 1)];
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
  
  private static void swap(ArrayList<Integer> list, int firstInd, int secondInd ){
     int temp = list.set( firstInd, list.get( secondInd ) ) ;
     list.set( secondInd, temp ) ;
  }
  
  public void recover(int prize, CustomersAllocation ca){
    remove_duplicates();
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    unvisited_facilites = ca.sort(unvisited_facilites);

    collected_prize = 0;
    for(int i =0; i < genes.size(); i++) collected_prize += ca.customers_per_facility[genes.get(i)];
    while(collected_prize < prize){
      int facility = unvisited_facilites.remove(0);
      genes.add(facility);
      collected_prize += ca.customers_per_facility[facility];
    }
    score = evaluate(facilites.map, facilites.depot_dist);
  }
  
  public void two_opt(){
    double cur_score;
    Random rand = new Random();
    int opt1, opt2, range = genes.size();
    ArrayList<Integer> initial_genes = genes;

    for(int i = 0; i < ITR_LIMIT; i++){
      opt1 = rand.nextInt(range);
      opt2 = rand.nextInt(range);
      if(opt1 == opt2) continue;

      swap(genes, opt1, opt2);
      cur_score = evaluate(facilites.map, facilites.depot_dist);
      if(cur_score >= score) genes = initial_genes;
      else {
        //better solution was found
        //original genes are already swapped
        initial_genes = genes;
        i = 0; //reset iterator
        score = cur_score;
      }
    }
  }
  
  public void drop_and_procedures(CustomersAllocation ca, int prize){
    Random rand = new Random();
    int removed_idx = rand.nextInt(genes.size());
    
    int removed_facility = genes.remove(removed_idx);
    int removal_cost = ca.customers_per_facility[removed_facility];
    ArrayList<Integer> unvisited_facilites = unvisited_facilites();
    unvisited_facilites.add(removed_facility);

    boolean found = false;
    for(int i = 0; i < unvisited_facilites.size() && !found; i++){
      int itr_facility = unvisited_facilites.get(i);
      int itr_cost = ca.customers_per_facility[itr_facility];
      
      if(collected_prize - removal_cost + itr_cost > prize){
        //feasible solution
        int prev_facility = genes.get(get_prev_facility(removed_idx));
        int next_facility = genes.get(get_next_facility(removed_idx));
        double dist = facilites.map[prev_facility][removed_facility] +
                   facilites.map[removed_facility][next_facility] -
                   facilites.map[prev_facility][itr_facility] -
                   facilites.map[itr_facility][next_facility];
        
        if(dist < 0){
          //score would improve
          score -= dist;
          genes.add(removed_idx, itr_facility);
          collected_prize -= removal_cost;
          collected_prize += itr_cost;
          found = true;
        }
      }
    }
    
    //if better solution was not found put back removed facility
    if(!found) genes.add(removed_facility);
  }
  
  private int get_prev_facility(int idx){
    if(idx == 0) return genes.size() - 1;
    else return idx - 1;
  }
  
  // facility was removed at idx. current facility at idx
  // is next facility
  private int get_next_facility(int idx){
    if(idx == genes.size()) return 0;
    else return idx;
  }
  
  public void remove_duplicates(){
    ArrayList<Integer> dup = new ArrayList<Integer>();
    Iterator<Integer> iterator = genes.iterator();
    while(iterator.hasNext()){
      int gene = (int) iterator.next();
      if(!dup.contains(gene)) dup.add(gene);
    }
    
    genes = dup;
  }
  
  public ArrayList<Integer> unvisited_facilites(){
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
