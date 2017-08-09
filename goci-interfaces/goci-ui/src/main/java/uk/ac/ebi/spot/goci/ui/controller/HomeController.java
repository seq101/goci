package uk.ac.ebi.spot.goci.ui.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.spot.goci.model.SearchResult;

/**
 * Created by cinzia on 05/08/2017.
 */
@Controller
public class HomeController {
    @RequestMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE) String search(Model model,
                                                                                           @RequestParam(required = false) String version) {
        String testVersion = "0";
        if (version != null) {
            testVersion = version;
        }
        model.addAttribute("testVersion", testVersion);
        return "index";
    }
}
