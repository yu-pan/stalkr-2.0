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
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;

public class Application extends Controller {
    
    final static Form<Blog> blogForm = Form.form(Blog.class);
    
    public static Result index() {
        return ok(index.render(blogForm, ""));
    }
    
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
    
    public static Result notFoundError() {
        System.out.println(3);
        return badRequest(error.render("Something is wrong. Let me know what happened."));
    }

    // Handles loading posts for infinite scroll
    public static Result loadPosts(String name, int currOffset) {
        System.out.println(name);
        String i = Stalkr.getInfo(name);
        PostSet p = Stalkr.getBlog(name, currOffset);

        ObjectNode postJson = Json.newObject();
        postJson.put("blog", name);
        postJson.put("currOffset", currOffset);
        postJson.put("nextOffset", p.nextOffset());
        JsonNode postList = Json.toJson(p.posts());
        postJson.put("posts", postList);

        if (p.posts().size() < 10) {
            postJson.put("end", true);
        } else {
            postJson.put("end", false);
        }

        return ok(postJson);
    }
    
    public static Result about() {
        return ok(about.render());
    }

}
