import java.util.*;

public class FacilitiesAllocation {
  public int[][]allocation;
  public int[] facility_coverage;
  Integer[] facilities_set;
  public int number_of_facilities;
  
  
  FacilitiesAllocation(int _number_of_facilites){
    number_of_facilities = _number_of_facilites;
    
    facility_coverage = new int[number_of_facilities];
    facilities_set = new Integer[number_of_facilities];
    for(int i =0; i < number_of_facilities; i++) facilities_set[i] = Integer.valueOf(i);
  }
  
  // copy constructor
  FacilitiesAllocation(FacilitiesAllocation original_ca){
    number_of_facilities = original_ca.number_of_facilities;
    
    facility_coverage = original_ca.facility_coverage.clone();
    facilities_set = original_ca.facilities_set.clone();
    allocation = new int[number_of_facilities][number_of_facilities];
    for(int i = 0; i < number_of_facilities; i++){
      allocation[i] = original_ca.allocation[i].clone(); 
    }
  }
  
  public void allocate_facilities(int num, double[][] map){
    load_tsplib_points(num, map);
    for(int i = 0; i < number_of_facilities; i++) facility_coverage[i] = num;
  }
  
  public ArrayList<Integer> sort(ArrayList<Integer> facilites){
    Integer[] dup_facilites_set = facilities_set;
    
    //sorts in increasing order
    Arrays.sort(dup_facilites_set, new NumberOfFacilitiesComparator());
    
    ArrayList<Integer> sorted_facilites = new ArrayList<Integer>();
    
    //itr over sorted facilites and add facility to sorted list
    //when matching facility in list was found
    for(int i = (number_of_facilities - 1); i >= 0 ; i--){
      int facility = dup_facilites_set[i];
      if(facilites.contains(facility)) sorted_facilites.add(facility);
    }
    
    return sorted_facilites;
  }
  
  public class NumberOfFacilitiesComparator implements Comparator<Integer> {
    public int compare(final Integer o1, final Integer o2) {
      return Float.compare(facility_coverage[o1], facility_coverage[o2]);
    }
  }
  
  public void load_tsplib_points(int num, double[][] map){
    allocation = new int[number_of_facilities][number_of_facilities];
    for(int i=0; i < number_of_facilities; i++){
      double[] map_i = map[i].clone();
      ArrayIndexComparator comparator = new ArrayIndexComparator(map_i);
      Integer[] indexes = comparator.createIndexArray();
      Arrays.sort(indexes, comparator);
      
      for(int c = 0; c < num; c++) allocation[i][indexes[c]] = 1;
    }
  }  
  
  public class ArrayIndexComparator implements Comparator<Integer> {
      private final double[] array;

      public ArrayIndexComparator(double[] array) {  this.array = array; }

      public Integer[] createIndexArray(){
          Integer[] indexes = new Integer[array.length];
          for (int i = 0; i < array.length; i++) indexes[i] = i; // Autoboxing
          return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2){
           // Autounbox from Integer to int to use as array indexes
          return Double.compare(array[index1], array[index2]);
      }
  }
}
