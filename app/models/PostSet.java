/* PostSet.java */

package models;
import java.util.List;

/*  
 *  Wrapper class for tumblr posts and offsets
 */
public class PostSet {
    
    List<String> posts;
    int currOffset, nextOffset;

    public PostSet(List<String> p, int ci, int ni) {
        posts = p;
        currOffset = ci;
        nextOffset = ni;
        System.out.println("curr offset: " + currOffset);
        System.out.println("next offset: " + nextOffset);
    }
    
   /*  
    *  @return list of posts
    */
    public List<String> posts() {
        return posts;
    }
    
   /*  
    *  @return currOffset
    */
    public int currOffset() {
        return currOffset;
    }
    
   /*  
    *  @return nextOffset
    */
    public int nextOffset() {
        return nextOffset;
    }
}