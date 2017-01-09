import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Population {
  public Chromosome[] chromosomes;
  public int population_size, nc;
  private double crossover_prob, mutation_prob;
  public double[] roulette;
  public Facilities facilities;
  public FacilitiesAllocation f_allocation;
  public Chromosome current_best_chromosome, alltime_best_chromosome;
  public double best_score;
  
  Population(int facilites_size, int _population_size, int _nc, String filename){
    population_size = _population_size;
    crossover_prob = 0.9;
    mutation_prob = 0.05;
    nc = _nc;

    chromosomes = new Chromosome[population_size];
    facilities = new Facilities(facilites_size);
    f_allocation = new FacilitiesAllocation(facilites_size);
    facilities.load_csp_points(filename);
    f_allocation.allocate_facilities(nc, facilities.map);
  }
  
  Population(int p_size, Facilities f, FacilitiesAllocation fa){
    population_size = p_size;
    facilities = f;
    f_allocation = fa;
    chromosomes = new Chromosome[population_size];
  }
   
   //Intializes chromosomes with random chromosomes
   // also set the best scores array, best_score and best chromosome for current generation
  public void initialize_population(){
    for(int i = 0; i < population_size; i++){
      Chromosome chromosome = new Chromosome(f_allocation, facilities);
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
    survivor_selection();
  }
  
  private void survivor_selection(){
    
  }
 
  private void local_search(){
    Random rand = new Random();
    int two_opt;
    for(int i = 0; i < population_size; i++) {
      two_opt = rand.nextInt(2);
      if(two_opt == 1){
        chromosomes[i].two_opt();
      } else {
        chromosomes[i].drop_and_procedures(f_allocation);
      }
    }
  }
  
  //selection has elitism of 4.
  // It uses roulette wheel selection
  private void selection(){
    Chromosome[] childrens = new Chromosome[population_size];
    childrens[0] = new Chromosome(current_best_chromosome);
    childrens[1] = new Chromosome(alltime_best_chromosome);
    childrens[2] = new Chromosome(alltime_best_chromosome);
    childrens[2].mutate(f_allocation);
    childrens[3] = new Chromosome(current_best_chromosome);
    childrens[3].mutate(f_allocation);

    set_roulette();

    Random prang = new Random();
    for(int i = 4; i < population_size; i++){
      Chromosome c = chromosomes[spin_wheel(prang.nextInt(100))];
      childrens[i] = new Chromosome(c);
    }

    chromosomes = childrens;
  }
  
  private void mutation(){
    for(int i = 0; i < population_size; i++) {
      if(Math.random() < mutation_prob) {
        chromosomes[i].mutate(f_allocation);
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
    Chromosome smaller_parent = get_smaller_parent(parent1, parent2);
    int smaller_size = smaller_parent.genes.size();

    int m = rand.nextInt(smaller_size);
    copy_genes(parent1, parent2, m, smaller_size);
    
    parent1.recover(f_allocation);
    parent2.recover(f_allocation);
  }
  
  public void copy_genes(Chromosome parent1, Chromosome parent2, int cp, int end){
    ArrayList<Integer> p2_genes = new ArrayList<Integer>(parent2.genes);
      
    ArrayList<Integer> child_left = new ArrayList<Integer>(parent1.genes.subList(0, cp));
    ArrayList<Integer> child_right = new ArrayList<Integer>(parent2.genes.subList(cp, end));
    child_left.addAll(child_right);
    if(parent2.genes.size() > parent1.genes.size()) {
      ArrayList<Integer> child_longer = new ArrayList<Integer>(parent2.genes.subList(end, parent2.genes.size()));
      child_left.addAll(child_longer);
    }
    parent2.genes = child_left;
    
    child_left = new ArrayList<Integer>(p2_genes.subList(0, cp));
    child_right = new ArrayList<Integer>(parent1.genes.subList(cp, end));
    child_left.addAll(child_right);
    if(p2_genes.size() < parent1.genes.size()) {
      ArrayList<Integer> child_longer = new ArrayList<Integer>(parent1.genes.subList(end, parent1.genes.size()));
      child_left.addAll(child_longer);
    }
    parent1.genes = child_left;
  }
  
  private Chromosome get_smaller_parent(Chromosome a, Chromosome b){
    return (a.genes.size() > b.genes.size() ? b : a);
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
      alltime_best_chromosome = new Chromosome(chromosomes[current_best_idx]);
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
