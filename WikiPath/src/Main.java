import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import a6.Heap;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        String start= "Columbia,_South_Carolina,_Sesquicentennial_half_dollar";
        String d= "GameStop";
        // String[] links= pullLinks(start);

        // String v= new Page(start);
        // String end= new Page(d);
        long startTime= System.currentTimeMillis();
        String[] answer= wikiShortest(start, d);
        long time= System.currentTimeMillis() - startTime;
        System.out.println(time);
        String o= "[";
        for (String s : answer) {
            o+= s;
            o+= ", ";
        }
        o+= "]";
        System.out.println(o);
    }

    public static String[] wikiShortest(String v, String end)
        throws IOException, InterruptedException {
        Heap<String> F= new Heap<>(true);

        HashMap<String, Info> mapSF= new HashMap<>();

        F.insert(v, 0);
        mapSF.put(v, new Info(null, 0));
        if (v.equals(end)) { return path(mapSF, end); }
        String prev= v;
        while (F.size() != 0) {
            String f= F.poll();
            System.out.println(f);
            if (f.equals(end)) { return path(mapSF, end); }
            String[] linksFromF= pullLinks(f);
            if (linksFromF != null) {

                for (String w : linksFromF) {

                    if (w != null) {
                        if (w != prev) {

                            if (!mapSF.containsKey(w)) {
                                int dW= mapSF.get(f).dist + 1;
                                F.insert(w, dW);
                                mapSF.put(w, new Info(f, dW));
                            } else if (mapSF.get(f).dist + 1 < mapSF.get(w).dist) {
                                F.changePriority(w, mapSF.get(f).dist + 1);
                                mapSF.get(w).dist= mapSF.get(f).dist + 1;
                                mapSF.get(w).bkptr= f;
                            }
                            if (w.equals(end)) {

                                return path(mapSF, end);
                            }
                        }
                    }
                }
                prev= f;
            }
        }
        return new String[0];
    }

    public static String[] pullLinks(String title) throws IOException, InterruptedException {
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request= HttpRequest.newBuilder()
            .uri(URI.create(
                "https://en.wikipedia.org/w/api.php?action=query&prop=links&pllimit=max&format=json&titles=" +
                    title))
            .GET() // GET is default
            .build();

        HttpResponse<String> response= client.send(request,
            HttpResponse.BodyHandlers.ofString());
        String jsonString= response.body();
        JSONObject obj= new JSONObject(jsonString);
        Object query= obj.get("query");
        try {
            Object pages= ((JSONObject) query).get("pages");
            String n= ((JSONObject) pages).keys().next();
            Object numbers= ((JSONObject) pages).get(n);
            Object l= ((JSONObject) numbers).get("links");
            JSONArray links= ((JSONObject) numbers).getJSONArray("links");
            String[] linksActual= new String[links.length()];
            for (int i= 0; i < links.length(); i++ ) {
                if (links.getJSONObject(i).getInt("ns") == 0) {
                    String myString= links.getJSONObject(i).getString("title").replaceAll(" ", "_");
                    // System.out.println(myString);
                    linksActual[i]= myString;
                }
            }
            return linksActual;
        } catch (

        JSONException e) {

        }
        return null;

    }

    public static String[] path(HashMap<String, Info> mapSF, String end) {
        List<String> path= new LinkedList<>();
        String p= end;
        // invariant: All the nodes from p's successor to node last are in
        // path, in reverse order.
        while (p != null) {
            path.add(0, p);
            // System.out.println(p);
            p= mapSF.get(p).bkptr;
        }
        String[] out= path.toArray(new String[path.size()]);
        return out;
    }

    private static class Info {
        /** shortest known distance from the start node to this one. */
        private int dist;
        /** backpointer on path (with shortest known distance) from <br>
         * start node to this one */
        private String bkptr;

        /** Constructor: an instance with dist d from the start node and<br>
         * backpointer p. */
        private Info(String p, int d) {
            dist= d;     // Distance from start node to this one.
            bkptr= p;    // Backpointer on the path (null if start node)
        }

        /** = a representation of this instance. */
        @Override
        public String toString() {
            return "dist " + dist + ", bckptr " + bkptr;
        }
    }

//    /** An instance contains the title of the page and a list of outgoings link. */
//    public static class Page {
//        String title;
//        String[] outLinks;
//
//        public Page(String t) throws IOException, InterruptedException {
//            title= t;
//            outLinks= pullLinks(t);
//        }
//
//        public String[] getLinks() {
//            return outLinks;
//        }
//
//    }
}
