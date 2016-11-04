 import java.util.*;

public class CustomersAllocation {
  public int[][]allocation;
  int[] customers_per_facility;
  Integer[] facilites_set;
  public int number_of_customers, number_of_facilites;
  
  CustomersAllocation(int _number_of_customers, int _number_of_facilites){
    number_of_customers = _number_of_customers;
    number_of_facilites = _number_of_facilites;
    
    allocation = new int[number_of_facilites][number_of_customers];
    customers_per_facility = new int[number_of_facilites];
    allocate_customers();
  }
  
  public void allocate_customers(){
    Random rand = new Random();
    
    for(int i =0; i < number_of_facilites; i++) facilites_set[i] = Integer.valueOf(i);
    for(int i = 0; i < number_of_customers; i++){
      int idx = rand.nextInt(number_of_facilites);
      allocation[idx][i] = 1;
      customers_per_facility[idx]++;
    }
  }
  
  public ArrayList<Integer> sort(ArrayList<Integer> facilites){
    //sorts in increasing order
    Integer[] dup_facilites_set = facilites_set;
    
    Arrays.sort(dup_facilites_set, new NumberOfCustomersComparator());

    ArrayList<Integer> sorted_facilites = new ArrayList<Integer>();
    for(int i = (number_of_facilites - 1); i >= 0 ; i++){
      int facility = dup_facilites_set[i];
      if(facilites.contains(facility)) sorted_facilites.add(facility);
    }
    
    return sorted_facilites;
  }
  
  public class NumberOfCustomersComparator implements Comparator<Integer> {
    public int compare(final Integer o1, final Integer o2) {
      return Float.compare(customers_per_facility[o1], customers_per_facility[o2]);
    }
  }
}
