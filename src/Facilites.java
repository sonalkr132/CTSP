import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class Facilites {
  public int[][] map;
  public int number_of_facilites;
  private int MAX_DIST = 1000000;
  public int[] depot_dist;
  
  Facilites(int num){
    number_of_facilites = num;
  }
  
   //generates a random 2d matrix for map
   public void gen_random_facilites(int dist_range){
     map = new int[number_of_facilites][number_of_facilites];
     Random rand = new Random();
  
     for(int i = 0; i < number_of_facilites; i++){
       for(int j = i; j < number_of_facilites; j++){
         if(Math.random() > 0.2 && i != j){
           int rand_num = 1 + rand.nextInt(dist_range + 1);
           map[i][j] = rand_num;
           map[j][i] = rand_num;
         }
         else{
           map[i][j] = MAX_DIST;
           map[j][i] = MAX_DIST;
         }
       }
     }
   }
   
   // loads the fixed map of cities
   public void load_fixed_distances(){
     map = new int[][]{ {MAX_DIST,       26, MAX_DIST, MAX_DIST,       4},
                        {      26, MAX_DIST,        3,       17, MAX_DIST},
                        {MAX_DIST,        3, MAX_DIST,        9,       21},
                        {MAX_DIST,       17,        9, MAX_DIST,        7},
                        {       4, MAX_DIST,       21,        7, MAX_DIST}
                      };
   }
   
   public void load_tsplib(String filename){
     try {
       Scanner input = new Scanner(new File(filename));
       map = new int[number_of_facilites][number_of_facilites];
  
       //only useful info in first six lines is dimension
       for (int i = 0; i < 7; i++) input.nextLine();
       
       for (int i = 0; i < number_of_facilites; i++){
         for (int j = i; j < number_of_facilites; j++){
           // fill in upper-diagonal, row-ordered
           map[i][j] = input.nextInt();

           // mirror only if we're not on the diagonal
           if (i != j) map[j][i] = map[i][j];
         }
       }
     
       input.close();
     } catch (Exception e) {
       System.out.println("Something went wrong in loading tsplib:" + e.getMessage());
     }
   }
   
   public void load_tsplib_points(String filename){
     int[][] ary = new int[number_of_facilites][2];
     int []depot = new int[2];
     try {
       Scanner input = new Scanner(new File(filename));
       //only useful info in first six lines is dimension
       for (int i = 0; i < 7; i++) input.nextLine();
       
       //storing depot
       input.nextInt(); //skip first number on each row
       depot[0] = input.nextInt();
       depot[1] = input.nextInt();
       
       for (int i = 0; i < number_of_facilites; i++){
         input.nextInt(); //skip first number on each row
         ary[i][0] = input.nextInt();
         ary[i][1] = input.nextInt();
       }
       input.close();
     } catch (Exception e){
       System.out.println("Something went wrong in loading tsplib:" + e.getMessage());
     }

     set_map(ary, depot);
   }
   
   private void set_map(int[][] ary, int[] depot){
     map = new int[number_of_facilites][number_of_facilites];
     depot_dist = new int[number_of_facilites];
     for(int i = 0; i < number_of_facilites; i++){
       for(int j = 0; j < number_of_facilites; j++){
         Double dist = distance_between(ary[i], ary[j]);
         map[i][j] = dist.intValue();
       }
       
       //set depot distance
       Double depot_dist_i = distance_between(ary[i], depot);
       depot_dist[i] = depot_dist_i.intValue();
     }
   }
   
   
   private double distance_between(int[] point_i, int[] point_j){
     int dx = point_i[0] - point_j[0];
     int dy = point_i[1] - point_j[1];
     return Math.sqrt(dx * dx + dy * dy);
   }
}
