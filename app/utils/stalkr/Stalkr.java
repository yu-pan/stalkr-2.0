/** Stalkr.java **/

package utils.stalkr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.*;
import models.PostSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Stalkr {
    
    final static JumblrClient client = new JumblrClient(System.getenv("CONSUMER_KEY"), System.getenv("CONSUMER_SECRET"));
    final static int NUMPOSTS = 10; // 10 posts displayed on each result page
    
   /*  Retrieve basic profile information of a blog.
    *  @param blogName: the name associated with a blog (<blogName>.tumblr.com)
    *  @return a String of HTML containing blog info 
    */
    public static String getInfo(String blogName) {
        Blog b = client.blogInfo(blogName + ".tumblr.com");
        String avatarUrl = "http://api.tumblr.com/v2/blog/" + blogName + ".tumblr.com/avatar/512";
        return HTMLTags.setInfo(b.getTitle(), avatarUrl, b.getDescription());
    }

   /*  Retrieve ten original posts by filtering all posts, newest to oldest.
    *  @param blogName: the name associated with a blog (<blogName>.tumblr.com)
    *         offset: the post offset passed to Tumblr API
    *  @return a PostSet holding ten posts, and current and next offsets
    */
	public static PostSet getBlog(String blogName, int offset) {
		Blog blog = client.blogInfo(blogName + ".tumblr.com");
		int currOffset = offset;
		List<Post> originals = new ArrayList<Post>();
		
		gettingOP:
		while (originals.size() < NUMPOSTS) {
			List<Post> returnedPosts = getPosts(blog, offset, 20);
			if (returnedPosts.size() != 0) {
			    for (int i = 0; i < returnedPosts.size() + 1; i++) {
				    if (originals.size() == NUMPOSTS) {
				        offset += i;
					    break gettingOP;
				    } else if (i == returnedPosts.size()) {
				        if (returnedPosts.size() == 20 ) {
				            break;
				        } else {
				            offset += i;
				            break gettingOP;
				        }
				    }
				    int tempOffset = offset + i;
				    Post curr = returnedPosts.get(i);
				    if (curr.getRebloggedFromName() == null) {
					    originals.add(curr);
				    }
			    }
			    offset += 20;
			} else {
			    break;
			}
		}
		return new PostSet(toHTML(originals), currOffset, offset);
	}
	
   /*  Retrieve ten original posts by filtering all posts, oldest to newest.
    *  @param blogName: the name associated with a blog (<blogName>.tumblr.com)
    *         forwardOffset: the post offset passed to Tumblr API
    *  @return a PostSet holding ten posts, and current and next offsets
    */
	public static PostSet getBlogBack(String blogName, int forwardOffset) {
	    int offset = forwardOffset - 20;
	    int limit = 20;
	    if (offset < 0) {
	        limit = 20 + offset;
	        offset = 0;
	    }
	    Blog blog = client.blogInfo(blogName + ".tumblr.com");
	    List<Post> originals = new ArrayList<Post>();
	    
	    gettingOP:
	    while (offset >= 0 && originals.size() < NUMPOSTS) {
	        List<Post> returnedPosts = getPosts(blog, offset, limit);
	        if (returnedPosts.size() != 0) {
	            for (int i = returnedPosts.size() - 1 ; i >= -1; i--) {
	                if (originals.size() == NUMPOSTS) {
	                    offset += i + 1;
	                    break gettingOP;
	                } else if (i == -1) {
	                    if (returnedPosts.size() == 20) {
	                        break;
	                    } else {
	                        break gettingOP;
	                    }
	                }
	                int tempOffset = offset + i;
	                Post curr = returnedPosts.get(i);
	                if (curr.getRebloggedFromName() == null) {
	                    originals.add(0, curr);
	                }
	            }
	            offset -= 20;
	            if (offset < 0) {
	               limit = 20 + offset;
	               offset = 0;
	            }
	        } else {
	            break;
	        }
	    }
	    return new PostSet(toHTML(originals), offset, forwardOffset);
	}
	
   /*  Retrieve basic information on each post--permalink, notes info.
    *  @param posts: a list of posts
    *  @return a list of Strings of HTML representing the posts
    */
	private static List<String> toHTML(List<Post> posts) {
	    List<String> tenPosts = new ArrayList<String>();
	    
	    for (int i = 0; i < posts.size(); i++) {
	        Post post = posts.get(i);
	        String postHTML = "<div class=\"post\">" +
        			          extract(post) +
				              "<br /><div style=\"text-align: right;\"><small><a href=\"" + post.getPostUrl() + "\">♥ " + post.getNoteCount() + "</a></small><br />";
			List<String> tags = post.getTags();
			if (tags.size() > 0) {
			    postHTML += "<small>";
			    for (String t : tags) {
			        postHTML += " #" + t;
			    }
			    postHTML += "</small><br />";
	        }
			postHTML += "<small>posted: " + post.getDateGMT() + "</small></div></div>";
			tenPosts.add(postHTML);
	    }
	    return tenPosts;
	}
	
   /*  HELPER for getBlog() and getBlogBack(). Pass each Tumblr Post object to HTMLTags
    *  class to determine display format.
    *  @param p: Post object from Tumblr API
    *  @return a String of HTML
    */
	private static String extract(Post p) {
		String result = "";
		if (p instanceof TextPost) {
			TextPost tp = (TextPost) p;
			result = HTMLTags.setTextPost(tp.getTitle(), tp.getBody());
		} else if (p instanceof PhotoPost &&
				   !((PhotoPost) p).isPhotoset())  {
			PhotoPost pp = (PhotoPost) p;
			Photo photo = pp.getPhotos().get(0);
			String link = photo.getSizes().get(0).getUrl();
			result = HTMLTags.setPhotoPost(link, pp.getCaption());
		} else if (p instanceof PhotoPost) {
			PhotoPost psp = (PhotoPost) p;
			List<Photo> photos = psp.getPhotos();
			String[] URLs = new String[photos.size()];
			for (int i = 0; i < photos.size(); i++) {
			    URLs[i] = photos.get(i).getSizes().get(0).getUrl();
			}
			result = HTMLTags.setPhotoSet(URLs, psp.getCaption());
		} else if (p instanceof QuotePost) {
		    QuotePost qp = (QuotePost) p;
		    result = HTMLTags.setQuotePost(qp.getText(), qp.getSource());
		} else if (p instanceof LinkPost) {
		    LinkPost lp = (LinkPost) p;
		    result = HTMLTags.setLinkPost(lp.getTitle(), lp.getLinkUrl(), lp.getDescription());
		} else if (p instanceof ChatPost) {
		    ChatPost cp = (ChatPost) p;
		    result = HTMLTags.setChatPost(cp.getTitle(), cp.getDialogue());
		} else if (p instanceof AnswerPost) {
		    AnswerPost ap = (AnswerPost) p;
		    result = HTMLTags.setAnswerPost(ap.getAskingUrl(), ap.getAskingName(),
		                                    ap.getQuestion(), ap.getAnswer());
		} else if (p instanceof VideoPost) {
		    VideoPost vp = (VideoPost) p;
		    Video v = vp.getVideos().get(2);
		    result = HTMLTags.setVideoPost(v.getEmbedCode(), vp.getCaption());
		} else if (p instanceof AudioPost) {
		    AudioPost aup = (AudioPost) p;
		    result = HTMLTags.setAudioPost(aup.getEmbedCode(), aup.getTrackName(),
		             aup.getAlbumName(), aup.getArtistName(), aup.getYear(), aup.getPlayCount());
		}
		return result;
	} 

  /** Getting list of posts from Tumblr
   **/
    private static List<Post> getPosts(Blog b, int offset, int limit) {
        Map<String, Object> request = new HashMap<String, Object>();
		request.put("limit", limit);
		request.put("offset", offset);
		request.put("reblog_info", true);
		return b.posts(request);
    }

}