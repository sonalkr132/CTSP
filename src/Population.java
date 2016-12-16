import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {
  public Chromosome[] chromosomes;
  public int population_size, no_of_facilites, prize, no_of_customers;
  private double crossover_prob, mutation_prob;
  public double[] roulette;
  public Facilites facilites;
  public CustomersAllocation cust_allocation;
  public Chromosome current_best_chromosome, alltime_best_chromosome;
  public double best_score;
  
  Population(int _facilites_size, int _customers_size, int _population_size, int _prize){
    population_size = _population_size;
    crossover_prob = 0.9;
    mutation_prob = 0.01;
    no_of_facilites = _facilites_size;
    no_of_customers = _customers_size;
    prize = _prize;

    chromosomes = new Chromosome[population_size];
  }
  
  // loads 5x5 matrix of cities' map
  public void fixed_point_facilites(){
    facilites = new Facilites(no_of_facilites);
    facilites.load_fixed_distances();
  }
  
  // Generates @points_size number of random cities in given range
  public void random_point_facilites(int dist_range){
    facilites = new Facilites(no_of_facilites);
    facilites.gen_random_facilites(dist_range);
  }
  
  //Loads cities data from tsplib file
  public void tsplib_cities(String filename, String type){
    facilites = new Facilites(no_of_facilites);
    if(type == "dist") facilites.load_tsplib(filename);
    else if(type == "points") facilites.load_tsplib_points(filename);
  }
  
  public void allocate_customers(){
    cust_allocation = new CustomersAllocation(no_of_customers, no_of_facilites);
    cust_allocation.allocate_customers();
  }
  
  //only supported type is points
  public void allocate_fixed_customers(String filename, int num){
    cust_allocation = new CustomersAllocation(no_of_customers, no_of_facilites);
    cust_allocation.allocate_fixed_customers(filename, num);
  }
    
   //Intializes chromosomes with random chromosomes
   // also set the best scores array, best_score and best chromosome for current generation
  public void initialize_population(){
    for(int i = 0; i < population_size; i++){
      Chromosome chromosome = new Chromosome(cust_allocation, facilites, prize);
      chromosomes[i] = chromosome;
    }
  
    set_best_score();
  }
  
  public void next_generation(){
    selection();
    crossover();
    mutation();
    local_search();
    set_best_score();
  }
 
  private void local_search(){
    Random rand = new Random();
    int two_opt;
    for(int i = 0; i < population_size; i++) {
      two_opt = rand.nextInt(2);
      if(two_opt == 1){
        chromosomes[i].two_opt();
      } else {
        chromosomes[i].drop_and_procedures(cust_allocation, prize);
      }
    }
  }
  
  //selection has elitism of 4.
  // It uses roulette wheel selection
  private void selection(){
    Chromosome[] parents = new Chromosome[population_size];
    parents[0] = new Chromosome(current_best_chromosome.score,
        current_best_chromosome.collected_prize, 
        current_best_chromosome.genes, 
        current_best_chromosome.facilites);
    parents[1] = new Chromosome(alltime_best_chromosome.score,
        alltime_best_chromosome.collected_prize, 
        alltime_best_chromosome.genes, 
        alltime_best_chromosome.facilites);
    parents[2] = Chromosome.mutate(cust_allocation, prize, alltime_best_chromosome);
    parents[3] = Chromosome.mutate(cust_allocation, prize, alltime_best_chromosome);

    set_roulette();

    Random prang = new Random();
    for(int i = 4; i < population_size; i++){
      Chromosome c = chromosomes[spin_wheel(prang.nextInt(100))];
      parents[i] = new Chromosome(c.score, c.collected_prize, c.genes, c.facilites);
    }

    chromosomes = parents;
  }
  
  private void mutation(){
    for(int i = 0; i < population_size; i++) {
      if(Math.random() < mutation_prob) {
        chromosomes[i] = Chromosome.mutate(cust_allocation, prize, chromosomes[i]);
        i--;
      }
    }
  }
  
  private void crossover(){
    ArrayList<Integer> queue = new ArrayList<Integer>();
    
    for(int i=0; i< population_size; i++) {
      if( Math.random() < crossover_prob ) {
        queue.add(i);
      }
    }

    Collections.shuffle(queue);
    for(int i = 0; i < queue.size() -1; i += 2) {
      do_crossover(queue.get(i), queue.get(i + 1));
    }
  }
  
  private void do_crossover(int x, int y){
    Chromosome parent1 = chromosomes[x];
    Chromosome parent2 = chromosomes[y];
    
    Random rand = new Random();
    int smaller_size = get_smaller_size(parent1.genes, parent2.genes);

    int m = rand.nextInt(smaller_size);
    copy_genes(parent1, parent2, m, smaller_size);
    
    parent1.recover(prize, cust_allocation);
    parent2.recover(prize, cust_allocation);
  }
  
  public void copy_genes(Chromosome parent1, Chromosome parent2, int cp, int end){
    ArrayList<Integer> p2_genes = parent2.genes;
    ArrayList<Integer> p1_genes = parent1.genes;
    
    //generating first child genes
    ArrayList<Integer> child_left = new ArrayList<Integer>(p1_genes.subList(0, cp));
    ArrayList<Integer> child_right = new ArrayList<Integer>(p2_genes.subList(cp, end));
    child_left.addAll(child_right);
    
    if(p2_genes.size() > p1_genes.size()) {
      //parent2.genes has more elements after `end`, we need to copy them as well
      ArrayList<Integer> child_longer = new ArrayList<Integer>(p2_genes.subList(end, p2_genes.size()));
      child_left.addAll(child_longer);
    }
    parent2.genes = child_left;
    
    
    //generating second child genes
    child_left = new ArrayList<Integer>(p2_genes.subList(0, cp));
    child_right = new ArrayList<Integer>(p1_genes.subList(cp, end));
    child_left.addAll(child_right);
    
    if(p2_genes.size() < p1_genes.size()) {
      //parent1.genes has more elements after `end`, we need to copy them as well
      ArrayList<Integer> child_longer = new ArrayList<Integer>(p1_genes.subList(end, p1_genes.size()));
      child_left.addAll(child_longer);
    }
    parent1.genes = child_left;
  }
  
  private int get_smaller_size(ArrayList<Integer> a, ArrayList<Integer> b){
    return (a.size() > b.size() ? b.size() : a.size());
  }
  
  //evaluates the best score of current population and changes
  // the alltime best chromosome and best score if required
  private void set_best_score(){
   double current_best = chromosomes[0].score;
    int current_best_idx = 0;
    for(int i = 0; i < population_size; i++){
      if(chromosomes[i].score < current_best){
        current_best_idx = i;
        current_best = chromosomes[i].score;
      }
    }
    
    current_best_chromosome = chromosomes[current_best_idx];
  
    if (alltime_best_chromosome == null || best_score > current_best){
      alltime_best_chromosome = new Chromosome(chromosomes[current_best_idx].score, 
          chromosomes[current_best_idx].collected_prize, 
          chromosomes[current_best_idx].genes, 
          chromosomes[current_best_idx].facilites);
      best_score = current_best;
    }
  }
  
  public double best_score_fraction(){
    int best_score_counter = 0;
    for(int i = 0; i < population_size; i++){
      if(chromosomes[i].score == best_score) best_score_counter++;
    }
    
    return best_score_counter/population_size;
  }
  
  //creates the roulette wheel used in selection
  // Fitness of chromosome, F(x) = 1/score(x)
  // Assume you have 10 items to choose from and you choose by generating
  // a random number between 0 and 1. You divide the range 0 to 1 up into
  // ten non-overlapping segments, each proportional to the fitness of one
  // of the ten items. For example, this might look like this:
  //0 - 0.3 is item 1
  //0.3 - 0.4 is item 2
  //0.4 - 0.5 is item 3
  //0.5 - 0.57 is item 4...  
  private void set_roulette(){
    double sum = 0.0;
    double[] fitness = new double[population_size];
    for(int i = 0; i < population_size; i++){
      fitness[i] = 1.0 / chromosomes[i].score;
      sum += fitness[i];
    }

    roulette = new double[population_size];
    for(int i = 0; i < population_size; i++) roulette[i] = (fitness[i]/sum)*100;
    for(int i = 1; i < population_size; i++) roulette[i] += roulette[i - 1];
  }

  private int spin_wheel(int random_number){
    int i;
    for(i = 0; i < population_size; i++){
      if(random_number < roulette[i]) break; 
    }
    return i;
  }
}
