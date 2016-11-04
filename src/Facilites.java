import java.util.Random;

public class Facilites {
  public int[][] map;
  public int number_of_facilites;
  private int MAX_DIST = 1000000;
  
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
}
