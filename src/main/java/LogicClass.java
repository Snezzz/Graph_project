import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Снежа on 10.03.2018.
 */
public class LogicClass extends DefaultHandler {
    static int tagLength = 0;
    static CSVWriter writer1;
    static int num=0;
    static int gh = 0;
    static boolean right_tag = false;
    static int ndLength = 0;
    static String way_id;
    static String value;
    static String last = "";
    static String start_point = "";
    static String near_restaurant;
    static int end = 0;
    static boolean second = false;
    static double time;
    public static double minlon, minlat, maxlat, maxlon;
    static boolean yes = false;
    static int step = 0;//для начала точки
    static boolean left = false, right = false, both = false;
    static int V, c = 0;
    ;
    static String your_way;
    static boolean is_one_way = false;
    static String oneway2 = "";
    static boolean is_way = false;
    static Map<String, String> nodes = new TreeMap();
    static Map<String, String> crosses = new TreeMap();
    static Map<String, String> start = new TreeMap();
    static Map<String, String> end_ = new TreeMap();
    static Map<String, String> adjacency = new HashMap();
    static Map<String, String> closed = new TreeMap();
    static Map<String, String> both_way = new TreeMap();
    static Map<String, String> coordinates = new TreeMap();
    static Map<String, Double> neighbour = new TreeMap<String, Double>();
    static Map<Long, Map<String, Double>> H = new HashMap<Long, Map<String, Double>>();
    static Map<Long, Map<Long, Double>> H2 = new HashMap<Long, Map<Long, Double>>();
    static Map<Long, Map<Long, Double>> for_test_2 = new HashMap<Long, Map<Long, Double>>();
    static Map<Long, Double> neighbour2 = new TreeMap<Long, Double>();
    static Map<String, String> points = new TreeMap();
    static Map<String, Double> result_map = new TreeMap<String, Double>();
    static Map<String, String> all_points = new TreeMap<String, String>();
    // static  Map <String,Vector<String>> H2=new TreeMap<String, Vector<String>>();
    static Map<String, String> for_test = new TreeMap<String, String>();
    static Map<String, Double> distance = new TreeMap<String, Double>();
    static Map<String, String> distance3 = new TreeMap<String, String>();
    static Map<Integer, Integer> g = new TreeMap<Integer, Integer>();
    static Map<String, String> ways_to_restaurant = new TreeMap<String, String>();
    static public double[][] distance2;

    public static void go() throws ParserConfigurationException, IOException, SAXException {


        System.out.print("Ждем граф...");
        DefaultHandler handler = new LogicClass();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File("map3.xml"), handler);


        get(crosses, start);
        get(crosses, end_);
        get(crosses, both_way);
        get(crosses, closed);

        //все ли точки рассмотрены в новом мэпе adjacency
        search(adjacency, start);
        search(adjacency, end_);
        search(adjacency, both_way);
        search(adjacency, closed);

        Iterator iteration = adjacency.entrySet().iterator();
        while (iteration.hasNext()) {
            Map.Entry pair = (Map.Entry) iteration.next();
            String id = pair.getKey().toString();
            String[] neighbours = pair.getValue().toString().split(";");
            if (!all_points.containsKey(id)) {
                all_points.put(id, id);
            }
            for (int i = 0; i < neighbours.length; i++) {
                if (!adjacency.containsKey(neighbours[i])) {
                    all_points.put(neighbours[i], neighbours[i]);
                }
            }
        }
        //  System.out.println( all_points.get(""));
        System.out.println("start" + start.size());
        System.out.println("end" + end_.size());
        System.out.println("both_way" + both_way.size());
        System.out.println("closed" + closed.size());
        System.out.println("adjency" + adjacency.size());
        int dhd = start.size() + adjacency.size() + end_.size() + both_way.size() + closed.size();
        //System.out.println(ways_amount);



        Iterator it = GetRestaurants.ten_objects.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String id = pair.getKey().toString();
            //  Dijkstra2(adjacency,"1008916451",adjacency.get("restaurant_"+id));
        }
        //запись списка в .csv
        Iterator iterator = adjacency.entrySet().iterator();
        String csv = "data.csv";
        String csv6= "data6.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
        String[] ColumnNamesList = {"Id вершины", "=" + "Id смежных с ней вершин"};
        writer.writeNext(ColumnNamesList);

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String[] n = pair.getValue().toString().split(";");
            for (int i = 0; i < n.length; i++) {
                if (points.get(n[i]) == null) {
                    points.put(n[i], n[i]);
                }

            }
            String h = pair.getKey().toString();
            if (!points.containsKey(h)) {
                points.put(h, h);
            }
            String[] s = pair.getValue().toString().split("'");
            String[] s2 = pair.getKey().toString().split(",");
            String[] result = new String[]{s2[0] + "=" + s[0]};
            writer.writeNext(result);
        }
        writer.close();
        Collection<String> result = points.values();


        System.out.println("Список смежности:" + result);
        V = result.size(); //число вершин
        second = true;
        handler = new LogicClass();
        factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        parser = factory.newSAXParser();
        parser.parse(new File("map3.xml"), handler);

        String csv2 = "data2.csv";
        char cvsSplitBy = '+';
        CSVReader reader = new CSVReader(new FileReader(csv2), cvsSplitBy);
        String[] stringOfData;
        stringOfData = reader.readNext();
        while ((stringOfData = reader.readNext()) != null) {
            result_map.put(stringOfData[0].split("=")[0], Double.parseDouble(stringOfData[0].split("=")[1]));
        }
        System.out.println("ready");
        writer1= new CSVWriter(new FileWriter(csv6));
    }


    public static void get(Map<String, String> map1, Map<String, String> map2) {

        Iterator iterator = map1.entrySet().iterator();
        String next;
        int size = 0;
        int step = 0;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //разрезаем на пути

            String[] s = (map1.get(pair.getKey())).split("="); //way id
            String[] ray;
            String[] ss;
            int k = 0;
            if (pair.getKey().toString().equals("1062709190")) {

                System.out.println("");
            }
            if (map2 == both_way) {


                ray = new String[s.length * 2];
                for (int j = 0; j < s.length * 2; j++) {
                    //нашли точки <-> в both_way s[k] = way_id
                    if (map2.get(s[k]) != null) {
                        ss = map2.get(s[k]).split("<->"); //ss[0]//ss[1]
                        ray[j] = ss[0];
                        ray[j + 1] = ss[1];
                        k++;
                        j++;
                        //size++;
                    }

                }
            } else {
                ray = new String[s.length];
                for (int i = 0; i < s.length; i++) {
                    ray[i] = map2.get(s[i]); //id начальных точек рассматриваемых путей

                }
            }
            for (int i = 0; i < ray.length; i++) {
                next = pair.getKey().toString();
                //если такая начальная точка уже была рассмотрена
                if (ray[i] != null) {

                    if ((adjacency.containsKey(ray[i])) && (adjacency.get(ray[i]) != next)) {

                        adjacency.put(ray[i], adjacency.get(ray[i]) + ";" + next);


                        //если такая конечная точка уже была рассомотрена
                    } else if (adjacency.get(ray[i]) == next) {

                        adjacency.put(ray[i], adjacency.get(ray[i]));
                        //если такая конечная точка уже была рассомотрена
                    } else {
                        //если еще не были рассмотрены дынные точки
                        adjacency.put(ray[i], pair.getKey().toString()); //ключ = точки значения = точки пересрестка

                    }
                    if (!adjacency.containsKey(pair.getKey().toString())) {
                        adjacency.put(pair.getKey().toString(), ray[i]); //ключ = точки значения = точки пересрестка
                    } else {
                        adjacency.put(pair.getKey().toString(), adjacency.get(pair.getKey().toString()) + ";" + ray[i]); //ключ = точки значения = точки пересрестка

                    }
                }
            }

        }
        //System.out.println("size of search founded="+size);
    }

    public static void search(Map<String, String> map1, Map<String, String> map2) {

        int size = 0;
        Iterator iterator = map2.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //если нет какой-либо точки в списке смежности - 0
            String[] s = (pair.getValue()).toString().split("<->");

            if (map2 == both_way) {
                //  for (int i = 0; i < s.length; i++) {
                if (!map1.containsKey(s[0])) {
                    size++;
                    map1.put(s[0], s[1]); //начальную точку с конечной

                }
                if (!map1.containsKey(s[1])) {
                    map1.put(s[1], s[0]);
                }
                // }
            } else {
                if (!map1.containsKey(pair.getValue())) {
                    if (map2 == start) {
                        map1.put(pair.getValue().toString(), end_.get(pair.getKey()).toString()); //начальную точку с конечной
                    } else if (map2 == end_) {
                        map1.put(pair.getValue().toString(), start.get(pair.getKey()).toString()); //начальную точку с конечной
                    } else if (map2 == closed) {
                        c++;
                        //  System.out.println(pair.getValue().toString()+"="+closed.get(pair.getKey()).toString());
                        map1.put(pair.getValue().toString(), closed.get(pair.getKey()).toString()); //начальную точку с конечной

                    }
                }
            }
        }
        //System.out.println("size of search not founded="+size);
    }

    static int ways_amount = 0;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        //localName
        //qName = название тега
        //attributes = все атрибуты
        int length = attributes.getLength();
        //поиск координат для точек
        String id = "";
        String lat = "";
        String lon = "";


        right_tag = false;

        if (second == true) {
            //перебираем все точки
            if (qName == "bounds") {
                for (int j = 0; j < length; j++) {

                    if (attributes.getQName(j) == "minlat") {
                        minlat = Double.valueOf(attributes.getValue(attributes.getQName(j)));
                    }
                    if (attributes.getQName(j) == "minlon") {
                        minlon = Double.valueOf(attributes.getValue(attributes.getQName(j)));
                    }
                    if (attributes.getQName(j) == "maxlon") {
                        maxlon = Double.valueOf(attributes.getValue(attributes.getQName(j)));
                    }
                    if (attributes.getQName(j) == "maxlat") {
                        maxlat = Double.valueOf(attributes.getValue(attributes.getQName(j)));
                    }
                }
            }
            if (qName == "node") {
                //для каждой атрибуты
                for (int j = 0; j < length; j++) {

                    if (attributes.getQName(j) == "id") {

                        id = attributes.getValue(attributes.getQName(j));
                    }
                    if (attributes.getQName(j) == "lat") {
                        lat = attributes.getValue(attributes.getQName(j));
                    }
                    if (attributes.getQName(j) == "lon") {
                        lon = attributes.getValue(attributes.getQName(j));
                    }
                }
                // if (points.get(id) != null) {
                coordinates.put(id, lat + "," + lon);
                //}

            }
        } else {
            // перебираем каждый тег <way>
            if (qName == "way") {
                // перебираем каждый путь
                way_id = attributes.getValue(0);
                step = 0;
                ndLength = 0;
                tagLength++;
                is_way = true;

            }
            //перебираем все точки для каждого пути
            if ((qName.equalsIgnoreCase("nd")) && (is_way)) {
                step = step + 1;
                ndLength++;
                //id точек
                value = attributes.getValue(0);
                if (step == 1) {
                    start_point = attributes.getValue(0);
                }
                last = attributes.getValue(0);
                //проверка на пересечение точка- путь
                //если этот ключ есть в к1 и его нет в к2
                if ((nodes.containsKey(value)) && (!crosses.containsKey(value))) {
                    //в новую коллекцию вставляем точку как ключ и две дороги, что она объединяет - как значение
                    //замкнутый путь(если указанный путь уже существует)
                   /* if(closed.containsKey(nodes.get(value))){
                        System.out.println(nodes.get(value));
                    }
                   */
                    if (!(nodes.get(value) == way_id)) {


                        crosses.put(value, nodes.get(value) + "=" + way_id);
                    }
                } else if (crosses.containsKey(value)) {
                    crosses.put(value, crosses.get(value) + "=" + way_id);
                } else {
                    // такого ключа нет
                    nodes.put(value, way_id);//одному пути ставится в соответствие множество точек
                }
            }
            String type;
            if (((qName.equalsIgnoreCase("tag")) && (yes == true)) && (is_way)) {
                //если замкнутый путь
                int len = attributes.getLength();
                //перебираем все атрибуты тега tag

                for (int i = 0; i < len; i++) {
                    String k = attributes.getQName(i); //атрибут
                    if (k.equals("k")) {
                        String oneway = attributes.getValue(i); //значение
                        if (oneway.equals("oneway")) {
                            String v = attributes.getValue(i + 1);
                            oneway2 = v;
                            if (v.equals("yes")) {
                                is_one_way = true;
                            } else {
                                is_one_way = false;
                            }
                            right_tag = false;
                        }


                        if (oneway.equals("highway")) {
                            String v = attributes.getValue(i + 1);
                            //   if ((v.equals("track")) || (v.equals("path")) || ((v.equals("service")))) {
                            // System.out.println("ready");
                            gh++;
                            //    right_tag = false;
                            //   }
                            right_tag = true;
                        } else {
                            right_tag = false;
                        }
                    }

                }
            }

            //обращаемся к атрибуту по поводу размера дороги
            //    if ((is_one_way)&&(right_tag)) {
            if (right_tag) {

                ways_amount++;
                if (is_one_way) {
                    //односторонняя ---->
                    if (!last.equals(start_point)) {
                        right = true;
                        start.put(way_id, start_point);
                        end_.put(way_id, last);
                    } else {
                        closed.put(way_id, last);

                    }
                }
                // }
                //двусторонняя <---->
                else if (oneway2.equals("no")) {
                    both = true;
                    if (!last.equals(start_point)) {
                        both_way.put(way_id, start_point + "<->" + last);
                    } else {
                        closed.put(way_id, last);

                    }
                }

                //в обратном направлении <------
                else {
                    left = true;
                    //обновляем
                    end_.put(way_id, start_point);
                    start.put(way_id, last);
                }
            } /*else if(!(is_one_way)&&(is_way)){
                    if (!last.equals(start_point)) {
                        both_way.put(way_id, start_point + "<->" + last);
                    } else {
                        closed.put(way_id, last);

                    }
                }*/
        }

    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName == "nd") {
            end++;
            yes = true;

        } else if ((qName == "way") && (right_tag)) {
            if (last.equals(start_point)) {
                //    closed.put(way_id, last);
            }
            yes = false;
            is_way = false;
        }
    }


    public static String get_place(int x, int y, Map<String, String> map) {

        Iterator iterator = map.entrySet().iterator();
        double prev_distance = 0, current_distance = 0, min_distance = 0;
        String point = "";
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String[] coordinates = pair.getValue().toString().split(",");//0-x,1-y
            String id = pair.getKey().toString();


            current_distance = Math.sqrt(Math.pow((x - Double.valueOf(coordinates[0])), 2)

                    //Integer.valueOf(coordinates[0])),2)
                    + Math.pow((y - Double.valueOf(coordinates[1])), 2));
            if (i == 0) {
                min_distance = current_distance;
            }
            if (current_distance < min_distance) {
                point = id; //запоминаем ближайшую точку
                min_distance = current_distance;

            }
            i++;
            //prev_distance=current_distance;
        }
        result_map.put("current", min_distance); //занесли расстояние
        return point;
    }




    static int INF = Integer.MAX_VALUE / 2; // "Бесконечность"
    static Map<Integer, String> way = new TreeMap<Integer, String>();
    static Map<String, String> P = new TreeMap<String, String>();
    static String value1;
    static ArrayDeque<String> queue1 = new ArrayDeque<String>();

    static int st = 0, k = 0, step1;

    public static void func(String s, String t) {

        String[] h = adjacency.get(s).split(";");
        Vector<String> points = new Vector<String>();

        String current;
        boolean[] visited = new boolean[adjacency.size()];
//для каждого приемника текущего узла
        //если есть соседи
        int r = h.length;
            /*
            //пополняем очередь смежными вершинами
        for (int i = 0; i < h.length; i++) {
            if(queue1.contains(h[i])){
                P.put(h[i],h[i]);
            }
            else {
                queue1.add(h[i]);

            }
        }
        */
        for (int i = 0; i < h.length; i++) {

            current = h[i];
            points.add(current);

            if (h[i] == t) {
                way.put(st, s);
                for (int j = 1; j < points.size(); j++)
                    way.put(st, way.get(st) + "-" + points.elementAt(j));
                st++;
                points.remove(points.size());
                P.remove(t);


            } //else  if((s!=h[i])&&(!P.containsValue(h[i]))) {
            else if (s != h[i]) {
                visited[step] = true;
                step1++;
                func(current, t);
            }

            //удаляем из вектора путь до точки разрыва
            if (s == h[i]) {
                int size = points.size() - 1;
                for (int j = points.size(); j > size; j--) {
                    points.remove(j);
                }

            }

        }
    }

    public static void Dijkstra(Map<Long, Map<Long, Double>> map, Map<String, Double> map2, String s, String[] t) throws IOException {
        //map1 - текущая точка
        //map2 - расстояния между всеми точками
        //s-начальная точка
        //t- набор кафе
        int length = map.size(); //10
        boolean end_of_algorithm = false;
        int t_length = t.length;
        int go_to_end = 0;
        int go = 0;
        boolean[] visited = new boolean[length];
        Map<Long, Boolean> visited2 = new TreeMap<Long, Boolean>();
        Map<Long, Long> p = new TreeMap<Long, Long>();
        int end = 0;
        Map<Long, Double> d = new TreeMap<Long, Double>(); //кратчайшие пути
        Iterator iterator1 = map.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator1.next();
            d.put(Long.parseLong(pair.getKey().toString()), 2000000000000.0);
            visited2.put(Long.parseLong(pair.getKey().toString()), false);
        }
        double new_d, help = 0;
        int h = 0;
        double min;
        int s2 = 0, place = 0;
        //определяем положение начальной точки и конечных точек(индексы)
        int i = 0;
        //place2= new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        //place=0;

        d.put(Long.parseLong(s), 0.0);
        long current, index = 0;
        current = Long.parseLong(s);
        System.out.println("current=" + current);
        //обращаемся к каждой вершине
        Set <Long> q=new LinkedHashSet<Long>();
        q.add(current);
        while(!q.isEmpty()){
            current = q.iterator().next();
            q.remove(current);
            Map<Long, Double> current_map = map.get(current); //получили карту с соседями и расстояниями до них
            Iterator iterat = current_map.entrySet().iterator();
            long to = 0;
            //для каждого соседа current_map
            while (iterat.hasNext()) {
                Map.Entry pair4 = (Map.Entry) iterat.next();
                String[] inf = pair4.getKey().toString().split("-");
                to = Long.parseLong(pair4.getKey().toString());
                if (d.containsKey(to)) {
                    if (visited2.get(to).equals(false)) {
                        new_d = d.get(current) + Double.parseDouble(pair4.getValue().toString());
                        if (new_d < d.get(to)) {
                            // q.remove(to);
                            d.put(to, new_d); //длины кратчайших путей до всех вершин
                            p.put(to, current);
                            q.add(to);
                        }
                    }
                }
            }
        }
     get_way(s,t,p,d);
    }

    public static void get_way(String s,String []t,Map <Long,Long> p,Map<Long,Double> d) throws IOException {

        int t_length=t.length;
        Vector<Long> path=new Vector<Long>();
        double min_distance=0;
        double distance=0;
        int current_way=0;
        String csv3 = "data5.csv";
        ways_to_restaurant=new TreeMap<String, String>();
        CSVWriter writer2 = new CSVWriter(new FileWriter(csv3));
        //для каждой конечной точки
        for(int j=0;j<t_length;j++) {
            path = new Vector<Long>();
            //получаем предков до начальной точки
            for (long v=Long.parseLong(t[j]); v!=Long.parseLong(s); v=p.get(v)) {
                path.add(v);
                if (!p.containsKey(v)) {
                 //   System.out.print("Нет пути");
                    break;
                }
            }
            path.add(Long.parseLong(s));
            //cчитаем длину кратчайшего расстояния
            for (int k = 0; k< path.size();k++) {
                if(ways_to_restaurant.containsKey(t[j])) {
                    ways_to_restaurant.put(t[j],ways_to_restaurant.get(t[j])+"-"+ String.valueOf(path.get(k))); //для отображения на карте
                }
                else{
                    ways_to_restaurant.put(t[j], String.valueOf(path.get(k)));
                }

            }

                String[] result2 = new String[]{t[j] + ":" + ways_to_restaurant.get(t[j])};
                //  String[] result2 = new String[]{pair.getKey().toString() + "+" +s[k] + "=" + w};
                writer2.writeNext(result2);

            distance = d.get(path.firstElement());
            if (j == 0) {
                min_distance = distance;

            }
            //если текущее расстояние меньше минмального,запоминаем ресторан
            if(distance<min_distance){
                current_way=j;
                min_distance=distance;
            }
          //  System.out.println("минимальная дистанция=" + min_distance);
        }
        writer2.close();
        if(min_distance==2.0E9){
            near_restaurant=null;
            your_way=null;
        }
        else {
            near_restaurant = t[current_way]; //нашли ближайший ресторан
            your_way = ways_to_restaurant.get(t[current_way]); //получили список индексов точек
            time=Graph.get_time(min_distance);
            String csv = "data6.csv";


            String[] result3 = new String[]{"до "+num +"й точки:"+ ways_to_restaurant.get(t[current_way])};
            //  String[] result2 = new String[]{pair.getKey().toString() + "+" +s[k] + "=" + w};
            writer1.writeNext(result3);
            num++;
            if(num==10)
            writer1.close();
        }


    }


    public static void Levita(Map<Long,Map<Long,Double>> map1,Map<String,Double> map2,String s,String[] t) throws IOException {

        int size=map1.size();
        Map<Long,Double> d = new TreeMap<Long, Double>(); //кратчайшие пути
        Map<Long,Long> p = new TreeMap<Long, Long>(); //кратчайшие пути
        Map<Long,Double>id=new TreeMap<Long, Double>();
        Iterator iterator1= map1.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator1.next();
            d.put(Long.parseLong(pair.getKey().toString()), 2000000000000.0);
            id.put(Long.parseLong(pair.getKey().toString()),0.0);
        }
        double new_d=0;
        int s2=0,place=0;
        int f=0;
        int t_length=t.length;
        int []place2=new int[t.length];
        int t2=0;
        long i=0;

        Set <Long> priorityQueue=new LinkedHashSet<Long>();
        Queue<Long> mainQueue= new ArrayDeque<Long>();
        long current;
        current = Long.parseLong(s);
        id.put(current,1.0);
        mainQueue.add(current);
        d.put(current,0.0);

        while(!((mainQueue.isEmpty()&&priorityQueue.isEmpty()))) {
            // current=q1.getFirst(); //берем верхний элемент - v
            if (priorityQueue.isEmpty()) {
                current = mainQueue.remove();//O(n)
            } else {

                current = priorityQueue.iterator().next();
                priorityQueue.remove(current);

            }

            id.put(current,1.0); //M1
            int h = 0;
            Iterator iterator2 = map1.entrySet().iterator();
            int z = 0;
            String id_s = "";
            boolean cont = true;
             Map<Long, Double> current_map = map1.get(current); //получили карту с соседями и расстояниями до них
            Iterator iterat = current_map.entrySet().iterator();
            int position = 0;
            long to = 0;
            long id_neighbour;
            //для каждого соседа current_map

            while (iterat.hasNext()){
                Map.Entry pair = (Map.Entry) iterat.next();
                String[] inf = pair.getKey().toString().split("-");
                to = Long.parseLong(pair.getKey().toString());
                new_d = d.get(current) + Double.parseDouble(pair.getValue().toString());
                if (d.containsKey(to)) {
                    if (new_d < d.get(to)) {
                        d.put(to, new_d); //длины кратчайших путей до всех вершин
                        if (id.get(to) == 0.0) //Если T принадлежит M2,
                            mainQueue.add(to); //O(1)

                        else if (id.get(to) == 1.0)//Если T принадлежит M0
                            priorityQueue.add(to);

                        p.put(to, current);
                        id.put(to, 1.0);
                    }
                }
            }
        }
        get_way(s,t,p,d);
    }
    public static double heuristic(long a, long b) {
        double a_x,a_y,b_x,b_y;
        String []l=coordinates.get(String.valueOf(a)).split(",");
        a_x=Double.parseDouble(l[0]);
        a_y=Double.parseDouble(l[1]);
        String []l2=coordinates.get(String.valueOf(b)).split(",");
        b_x=Double.parseDouble(l2[0]);
        b_y=Double.parseDouble(l2[1]);

        return Math.abs(a_x-b_x)+Math.abs(a_y-b_y);
    }

    public static void A2(Map<Long, Map <Long,Double>> map,Map<String,Double> map2,String s,String t,boolean solve){
        int size=map.size();
        Queue<Long> openQueue=new ArrayDeque<Long>(); //ождающие рассматроения
        Set<Long> clothedQueue=new HashSet<Long>(); //прошедшие рассмотрение
        boolean [] visited=new boolean[size];
        Map <Long,Boolean> visited2=new TreeMap<Long,Boolean>();
        double new_d=0;
        double priority=0;
        double o=0;
        boolean found=false;
        Map <Long,Long> p=new TreeMap<Long,Long>();
        Map <Long,Double> d=new TreeMap<Long, Double>();
        Map <Long,Double>g=new TreeMap<Long, Double>();
        Map <Long,Double>f=new TreeMap<Long, Double>();
        int place=0,place2=0;
        //double  []d=new double[size];
        long current;


        if(solve==true) {
            //f[place] = g[place] + Double.parseDouble(distance3.get(place + "-" + place2));
        }
        else{
            f.put(Long.parseLong(s),g.get(Long.parseLong(s)) + heuristic(Long.parseLong(s),Long.parseLong(t)));
        }
        Iterator iterator1= map.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator1.next();
            d.put(Long.parseLong(pair.getKey().toString()), 2000000000000.0);
            f.put(Long.parseLong(pair.getKey().toString()), 2000000000000.0);
            g.put(Long.parseLong(pair.getKey().toString()), 2000000000000.0);
            visited2.put(Long.parseLong(pair.getKey().toString()), false);
        }
        visited2.put(Long.parseLong(s),true);
        g.put(Long.parseLong(s),0.0);
        double min=0;
        int index=0;
        current=Long.parseLong(s);
        openQueue.add(current);
        while((!openQueue.isEmpty())&&(!found)){
            current = openQueue.iterator().next();
            openQueue.remove(current);
            clothedQueue.add(current);
            if(openQueue.contains(Long.parseLong(t))){
                found=true;
                break;
            }
            Map<Long, Double> current_map = map.get(current); //получили карту с соседями и расстояниями до них
            Iterator iterat = current_map.entrySet().iterator();
            long to = 0;
            //для каждого соседа current_map
            while (iterat.hasNext()) {
                Map.Entry pair4 = (Map.Entry) iterat.next();
                String[] inf = pair4.getKey().toString().split("-");
                to = Long.parseLong(pair4.getKey().toString());
                if(clothedQueue.contains(to)){
                    continue;
                }
                if (g.containsKey(to)) {
                    if (visited2.get(to).equals(false)) {
                        new_d = g.get(current) + Double.parseDouble(pair4.getValue().toString());
                      if (new_d < g.get(to)) {
                            g.put(to, new_d); //длины кратчайших путей до всех вершин
                            p.put(to, current);
                            f.put(to, g.get(to) + heuristic(to, Long.parseLong(t)));
                            if(openQueue.contains(to)){
                                openQueue.remove(to);
                            }
                            openQueue.add(to);
                        }
                    }
                }
            }
            }

        Vector<Long> path=new Vector<Long>();
        double distance=0;
        for (long v=Long.parseLong(t); v!=Long.parseLong(s); v=p.get(v)) {
            path.add(v);
            if (!p.containsKey(v)) {
           //     System.out.print("Нет пути");
                break;
            }
        }
        path.add(Long.parseLong(s));
       distance=g.get(path.firstElement());
        for (int k = 0; k< path.size();k++) {
            if(ways_to_restaurant.containsKey(t)) {
                ways_to_restaurant.put(t,ways_to_restaurant.get(t)+"-"+ String.valueOf(path.get(k))); //для отображения на карте
            }
            else{
                ways_to_restaurant.put(t, String.valueOf(path.get(k)));
            }
        }
        ways_to_restaurant.put("width",String.valueOf(distance));

    }
}





