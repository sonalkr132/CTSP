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
  
  public void allocate_customers(){
    cust_allocation = new CustomersAllocation(no_of_customers, no_of_facilites);
    cust_allocation.allocate_customers();
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
    //crossover();
    
    //mutation();
    set_best_score();
  }
  
  //selection has elitism of 4.
  // It uses roulette wheel selection
  public void selection(){
    Chromosome[] parents = new Chromosome[population_size];
    parents[0] = current_best_chromosome;
    parents[1] = alltime_best_chromosome;
    parents[2] = alltime_best_chromosome;
    parents[3] = alltime_best_chromosome;

    set_roulette();

    Random prang = new Random();
    for(int i = 4; i < population_size; i++){
      parents[i] = chromosomes[spin_wheel(prang.nextInt(100))];
    }

    chromosomes = parents;
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
      alltime_best_chromosome = chromosomes[current_best_idx];
      best_score = current_best;
    }
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
