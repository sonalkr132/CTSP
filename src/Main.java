public class Main {
  public static void main(String[] args) {
    int NUMBER_OF_FACILITES = 5;
    int NUMBER_OF_CUSTOMERS = 15;
    int POPULATION_SIZE = 40;
    int PRIZE = 12;
    int NUM_OF_ITR = 80000;
    Population p = new Population(NUMBER_OF_FACILITES, NUMBER_OF_CUSTOMERS, POPULATION_SIZE, PRIZE); //number of points, population size
    p.fixed_point_facilites();
    p.allocate_customers();
 
    p.initialize_population();
    
    for(int i = 0; i < NUM_OF_ITR; i++){
      p.next_generation();
    }
    System.out.print("some");
  }
}
