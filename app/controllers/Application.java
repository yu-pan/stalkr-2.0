/* Application.java */

package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import models.Blog;
import models.PostSet;
import utils.stalkr.Stalkr;
import utils.stalkr.HTMLTags;
import com.tumblr.jumblr.exceptions.JumblrException;
import java.util.List;
import views.html.*;

/*
 * Controller module
 */
public class Application extends Controller {
    
    final static Form<Blog> blogForm = Form.form(Blog.class);
    
    // Renders homepage
    public static Result index() {
        return ok(index.render(blogForm, ""));
    }
    
    // Handles form submission, redirects to searchResult() or notFoundError()
    public static Result submit() {
        Form<Blog> filledForm = blogForm.bindFromRequest();
        Blog created = filledForm.get();
        try {
            String intro = Stalkr.getInfo(created.blogName);
            return redirect(routes.Application.searchResult(created.blogName));
        } catch (JumblrException e) {
            return badRequest(index.render(blogForm, created.blogName + ".tumblr.com doesn't exist."));
        } catch (Exception e2) {
            return redirect(routes.Application.notFoundError());
        }
    }

    // Renders search result of BLOGNAME
    public static Result searchResult(String blogName) {
        try {
            Blog created = new Blog();
            created.blogName = blogName;
            String intro = Stalkr.getInfo(blogName);
            PostSet ps = Stalkr.getBlog(blogName, 0);
            return ok(submit.render(created, intro, ps));
        } catch (JumblrException e) {
            return ok(index.render(blogForm, "Something went wrong."));
        } catch (Exception e2) {
            return ok(error.render("Something is wrong. Let me know what happened."));
        }
    }
    
    // Renders error page
    public static Result notFoundError() {
        return ok(error.render("Something is wrong. Let me know what happened."));
    }
    
    // Renders in-between result pages
    public static Result getMore(String name, int currOffset) {
        String i = Stalkr.getInfo(name);
        PostSet p = Stalkr.getBlog(name, currOffset);
        if (p.posts().size() < 10) {
            return ok(end.render(name, i, p));
        } else {
            return ok(more.render(name, i, p));
        }
    }
    
    // Renders result pages when searching in reverse
    public static Result getLess(String name, int currOffset) {
        String i = Stalkr.getInfo(name);
        PostSet p = Stalkr.getBlogBack(name, currOffset);
        if (p.currOffset() == 0) {
            return ok(frontend.render(name, i, p));
        } else {
            return ok(more.render(name, i, p));
        }
    }
    
    // Renders About page
    public static Result about() {
        return ok(about.render());
    }

}
