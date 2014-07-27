/* HTMLTags */

package utils.stalkr;

import java.io.*;
import com.tumblr.jumblr.types.Dialogue;
import java.util.List;

/*  
 *  Formats blog and post information with HTML.
 */
public class HTMLTags {
    
    static String startTags = "<!DOCTYPE html>\n" +
                              "<html>\n" +
                              "    <head>\n" +
                              "        <meta charset=\"UTF-8\">\n" +
                              "        <link type=\"text/css\" rel=\"stylesheet\" href=\"style.css\"/>\n";
                              
    static String midTags =   "    </head>\n" +
                              "    <body>\n" +
                              "        <div id=\"content\">\n";
    
    static String endTags =   "\n        </div>\n" +
                              "        <div id=\"footer\">\n" +
                              "            <p>developed by Yuki Pan, UCB c/o 2017</p>\n" +
                              "        </div>\n" +
                              "    </body>\n" +
                              "</html>";
                              
    static String chatLabel1 = "<li><div class=\"chat-speaker\">";
    static String chatLabel2 = "</div>";
    
    // Formats basic profile information
    public static String setInfo(String blogName, String avatarURL, String desc) {
        String info = "            <div id=\"blog-header\"><div id=\"avatar\">\n" +
                      "                <img src=\"" + avatarURL + "\" />\n" +
                      "            </div>\n" +
                      "            <h1>" + blogName + "</h1>\n" +
                      "            " + desc + "</div>\n\n";
        return info;
    }
    
    // Formats <title> tag
    public static String setTitle(String title) {
        return "        <title>" + title + " | stalkr.me</title>";
    }
    
    // Formats text post
    public static String setTextPost(String title, String body) {
        String post = "";
        if (title != null) {
            post += "<h2>" + title + "</h2>\n";
        }
        post += body;
        return post;
    }
    
    // Formats single-photo post
    public static String setPhotoPost(String photoURL, String caption) {
        String post = "<img class=\"post-img\" src=\"" + photoURL + "\" /><br />\n" +
                      caption + "\n";
        return post;
    }
    
    // Formats photoset post
    public static String setPhotoSet(String[] URLs, String caption) {
        String post = "";
        for (String s : URLs) {
            post += "<img class=\"post-img\" src=\"" + s + "\" /><br />\n";
        }
        post += caption + "\n";
        return post;
    }

    // Formats quote post
    public static String setQuotePost(String quote, String source) {
        String post = "<blockquote class=\"quote-post\">" + quote + "</blockquote>\n" +
                      "<div class=\"quote-source\">" + source + "</div>";
        return post;
    }
    
    // Formats link post
    public static String setLinkPost(String title, String link, String desc) {
        if (title == null) {
            title = link;
        }
        String post = "<div class=\"link-post\"><a href=\"" + link + "\">" + title + "</a></div>" +
                      desc;
        return post;
    }
    
    // Formats chat post
    public static String setChatPost(String title, List<Dialogue> d) {
        String post = "";
        if (title != null) {
            post += "<h2>" + title + "</h2>\n";
        }
        post += "<ul class=\"chat-post\">";

        for (int i = 0; i < d.size(); i++) {
            Dialogue line = d.get(i);
            post += chatLabel1 + line.getLabel() + chatLabel2 + line.getPhrase() + "</li>\n";
        }
        post += "</ul>";
        return post;
    }
    
    // Formats ask-answer post
    public static String setAnswerPost(String URL, String name, String q, String a) {
        String post = "<div class=\"question-box\"><div class=\"question-side\"><div class=\"question-asker\">from ";
        if (URL == null) {
            post += "Anonymous";
        } else {
            post += "<a href=\"" + URL + "\">" + name + "</a>";
        }
        post += "</div><div class=\"question\">" + q + "</div></div>" + a + "</div>";
        return post;
    }
    
    // Formats video post
    public static String setVideoPost(String embed, String caption) {
        String post = "<div class=\"video-post\"><div class=\"video\">" +
                      embed + "</div>" + caption + "</div>";
        return post;
    }
    
    // Formats audio post
    public static String setAudioPost(String embed, String track, String album, String artist,
                                      Integer year, Integer plays) {
        String post = "<div class=\"audio-post\"><div class=\"audio-player\">" + embed +
                      "</div><table class=\"audio-info\"><tr class=\"odd\"><td>Track Name: ";
        if (track != null) {post += track;}
        post += "</td></tr><tr class=\"even\"><td>Album Name: ";
        if (album != null) {post += album;}
        post += "</td></tr><tr class=\"odd\"><td>Artist: ";
        if (artist != null) {post += artist;}
        post += "</td></tr><tr class=\"even\"><td>Year: ";
        if (year != null) {post += year.intValue();}
        post += "</td></tr><tr class=\"odd\"><td>Plays: ";
        if (plays != null) {post += plays.intValue();}
        post += "</td></tr></table></div>";
        return post;
    }
    
}