package soa.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Controller
public class SearchController {

    private static String MAX_NUMBERS = "CamelTwitterCount";
    private static String SEARCH_KEYWORDS = "CamelTwitterKeywords";
    private static String MAX_QUERY = "max:";
    private static String QUERY_SEPARATOR =  " ";

	@Autowired
	  private ProducerTemplate producerTemplate;

	@RequestMapping("/")
    public String index() {
        return "index";
    }


    @RequestMapping(value="/search")
    @ResponseBody
    public Object search(@RequestParam("q") String q) {
        return producerTemplate.requestBodyAndHeader("direct:search", "", getHeadersWithMax(q.split(QUERY_SEPARATOR)));
    }

    //return the headers for the query, with the max number if required.
    private static Map<String, Object> getHeadersWithMax(String[] searchWords){
        Map<String, Object> headers = new HashMap<String, Object>();
        String max = null;
        String keywordsWithoutMax="";

        for(String keyword: searchWords){
            if(isThisKeywordTheMaxQuery(keyword)){
                max = getMaxNumbers(keyword);
            }
            else{
                keywordsWithoutMax += keyword + QUERY_SEPARATOR;
            }
        }

        headers.put(SEARCH_KEYWORDS,keywordsWithoutMax);
        if(max!=null){
            headers.put(MAX_NUMBERS,max);
        }

        return headers;
    }

    //return if the parameter keyword is the one for the max number of the query
    private static boolean isThisKeywordTheMaxQuery(String keyword){
        return  keyword.toLowerCase().contains(MAX_QUERY);
    }

    //Return the max number in the query
    private static String getMaxNumbers(String maxQuery){
        return maxQuery.split(":")[1];
    }

}