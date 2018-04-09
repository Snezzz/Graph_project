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

import static java.util.Arrays.fill;

/**
 * Created by Снежа on 10.03.2018.
 */
public class LogicClass extends DefaultHandler {
    static int tagLength = 0;
    static int ndLength = 0;
    static String way_id;
    static String value;
    static String last = "";
    static String start_point = "";
    static String near_restaurant;
    static int end = 0;
    static boolean second = false;
    public static double minlon, minlat, maxlat, maxlon;
    static boolean yes = false;
    static int step = 0;//для начала точки
    static boolean left = false, right = false, both = false;
    static int V;
    static Map<String, String> nodes = new TreeMap();
    static Map<String, String> crosses = new TreeMap();
    static Map<String, String> start = new TreeMap();
    static Map<String, String> end_ = new TreeMap();
    static Map<String, String> adjacency = new TreeMap();
    static Map<String, String> closed = new TreeMap();
    static Map<String, String> both_way = new TreeMap();
    static Map<String, String> coordinates = new TreeMap();
    static Map<String, String> points = new TreeMap();
    static Map<String, String> result_map = new TreeMap<String, String>();
    static Map <String,String> for_test=new TreeMap<String, String>();
    static Map <String,String> distance=new TreeMap<String, String>();
    static Map <String,String> distance3=new TreeMap<String, String>();
    static Map <String,String> ways_to_restaurant=new TreeMap<String, String>();
    static public double[][]distance2;
    public static void go() throws ParserConfigurationException, IOException, SAXException {
/*
        //список смежности
        for_test.put("0","1;2");
        for_test.put("1","0;2;6;7");
        for_test.put("2","0;1;4;5");
        for_test.put("3","5");
        for_test.put("4","2;5");
        for_test.put("5","2;3;4;6;9");
        for_test.put("6","1;5;8");
        for_test.put("7","1");
        for_test.put("8","6");
        for_test.put("9","5");
        //расстояния
        distance.put("7+1","5");
        distance.put("8+6","8");
        distance.put("9+5","2");
        distance.put("0+1","1");
        distance.put("0+2","2");
        distance.put("1+0","1");
        distance.put("1+2","4");
        distance.put("1+6","7");
        distance.put("1+7","5");
        distance.put("2+0","2");
        distance.put("2+1","4");
        distance.put("2+4","5");
        distance.put("2+5","8");
        distance.put("3+5","3");
        distance.put("4+2","5");
        distance.put("4+5","10");
        distance.put("5+2","8");
        distance.put("5+3","3");
        distance.put("5+4","10");
        distance.put("5+6","4");
        distance.put("5+9","2");
        distance.put("6+1","7");
        distance.put("6+5","4");
        distance.put("6+8","8");
        distance3.put("0-9","20");
        distance3.put("1-9","18");
        distance3.put("2-9","18");
        distance3.put("3-9","17");
        distance3.put("4-9","16");
        distance3.put("5-9","15");
        distance3.put("6-9","9");
        distance3.put("7-9","0");
        distance3.put("8-9","0");
        distance3.put("9-9","0");
        Levita(for_test,distance,"0","9");
        A2(for_test,distance,"0","9",true);
        Dijkstra2(for_test,distance,"0","9",true);
*/
        System.out.print("Ждем граф...");
        DefaultHandler handler = new LogicClass();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File("map2.xml"), handler);
        System.out.println("Количество нужных тегов=" + tagLength);

        //смежность
        get(crosses, start);
        get(crosses, end_);
        get(crosses, both_way);
        //  get(crosses, closed);

        //все ли точки рассмотрены в новом мэпе adjacency
        search(adjacency, start);
        search(adjacency, end_);
        search(adjacency, both_way);

        //   search(adjacency, closed);

        //Dijkstra2(adjacency,"1008916451","1009191490");
        Iterator it =GetRestaurants.ten_objects.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String id=pair.getKey().toString();
          //  Dijkstra2(adjacency,"1008916451",adjacency.get("restaurant_"+id));
        }
        //запись списка в .csv
        Iterator iterator = adjacency.entrySet().iterator();
        String csv = "data.csv";
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
        parser.parse(new File("map2.xml"), handler);
       // make(adjacency,adjacency.size());
       // distance2=new double[adjacency.size()][adjacency.size()];
        Iterator iter = adjacency.entrySet().iterator();
        int i=0;

        String csv2 = "data2.csv";
        /*
        CSVWriter writer2 = new CSVWriter(new FileWriter(csv2));
        String[] ColumnNamesList2 = {"строка-столбец", "=" + "значение веса"};
        writer2.writeNext(ColumnNamesList);
        while (iter.hasNext()){
            double w=0;
            int []keys=new int[adjacency.size()];
            Map.Entry pair = (Map.Entry) iter.next();

            //если нет какой-либо точки в списке смежности - 0
            String[] s = (pair.getValue()).toString().split(";");
            //получаем порядковый номер наших смежных вершин
           // for(int j=0;j<s.length;j++){
            //    keys[j]=get_it(s[j]);
            //}
            //для каждого соседа
          //  Set <String> set=adjacency.keySet();
            if (s.length==1){

                keys[0]=get_it(s[0],adjacency);
                //заполняем каждую строку размером
                //взяли координаты соседей
                String[] h = coordinates.get(s[0]).split(",");
                //считаем показатели
                // h[0]= h[0].replace(".",",");
                //h[1]=h[1].replace(".",",");
                double lan1 = Double.parseDouble(h[0]);
                double lon1 = Double.parseDouble(h[1]);
                //взяли координаты отца
                String[] h2 = coordinates.get(pair.getKey()).split(",");
                // h2[0]= h2[0].replace(".",",");
                //  h2[1]=h2[1].replace(".",",");
                //считаем показатели
                double lan2 = Double.parseDouble(h2[0]);
                double lon2 = Double.parseDouble(h2[1]);
                //считаем вес
                w = (Math.sqrt(Math.pow((lan1 - lan2)*1000, 2) + Math.pow(lon1 - lon2, 2)*1000));
                // System.out.println();

                result_map.put(i+"-"+keys[0],w);

                String[] result2 = new String[]{i + "+"+ keys[0]+"="+w};
                writer2.writeNext(result2);
            }
            else {
                for (int k = 0; k < s.length; k++) {

                    keys[k] = get_it(s[k],adjacency);
                    //заполняем каждую строку размером
                    //взяли координаты соседей
                    String[] h = coordinates.get(s[k]).split(",");
                    //считаем показатели
                    // h[0]= h[0].replace(".",",");
                    //h[1]=h[1].replace(".",",");
                    double lan1 = Double.parseDouble(h[0]);
                    double lon1 = Double.parseDouble(h[1]);
                    //взяли координаты отца
                    String[] h2 = coordinates.get(pair.getKey()).split(",");
                    // h2[0]= h2[0].replace(".",",");
                    //  h2[1]=h2[1].replace(".",",");
                    //считаем показатели
                    double lan2 = Double.parseDouble(h2[0]);
                    double lon2 = Double.parseDouble(h2[1]);
                    //считаем вес
                    w = (Math.sqrt(Math.pow((lan1 - lan2) * 1000, 2) + Math.pow(lon1 - lon2, 2) * 1000));
                    // System.out.println();

                    result_map.put(i + "-" + keys[k], w);

                    String[] result2 = new String[]{i + "+" + keys[k] + "=" + w};
                    writer2.writeNext(result2);
                }
            }

            i++;



        }
        writer2.close();
*/

        char cvsSplitBy = '+';
        CSVReader reader = new CSVReader(new FileReader(csv2),cvsSplitBy);
        String[] stringOfData;
        while ((stringOfData = reader.readNext()) != null) {
           result_map.put(stringOfData[0].split("=")[0],stringOfData[0].split("=")[1]);
        }

    }
    public static int get_it(String k,Map<String,String>map){
        int i=0;
        Collection<String> collection= map.keySet();

        //Object k=new Object();//что хотим найти
        for (String key : collection) {
           // Object obj = adjacency.get(key);
            if (key != null) {
                //System.out.print(key);

                if (k.equals(key)) {
                    return i;
                }
                i++;
            }
        }
        return 0;

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
                for (int i = 0; i < s.length; i++) {
                    if (!map1.containsKey(s[i])) {
                        size++;
                        map1.put(s[0], s[1]); //начальную точку с конечной
                    }
                }
            } else {
                if (!map1.containsKey(pair.getValue())) {
                    if (map2 == start) {
                        map1.put(pair.getValue().toString(), end_.get(pair.getKey()).toString()); //начальную точку с конечной
                    } else if (map2 == end_) {
                        map1.put(pair.getValue().toString(), start.get(pair.getKey()).toString()); //начальную точку с конечной
                    } else if (map2 == closed) {
                        //  System.out.println(pair.getValue().toString()+"="+closed.get(pair.getKey()).toString());
                        map1.put(pair.getValue().toString(), closed.get(pair.getKey()).toString()); //начальную точку с конечной

                    }
                }
            }
        }
        //System.out.println("size of search not founded="+size);
    }

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
            }
            //перебираем все точки для каждого пути
            if (qName.equalsIgnoreCase("nd")) {
                step = step + 1;
                ndLength++;
                //id точек
                value = attributes.getValue(0);
                if (step == 1) {
                    start_point = attributes.getValue(0);
                }
                last = attributes.getValue(0);
                //проверка на пересечение
                //если этот ключ есть в к1 и его нет в к2
                if ((nodes.containsKey(value)) && (!crosses.containsKey(value))) {
                    //в новую коллекцию вставляем точку как ключ и две дороги, что она объединяет - как значение
                    //замкнутый путь(если указанный путь уже существует)
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
            if ((qName.equalsIgnoreCase("tag")) && (yes == true)) {
                //если замкнутый путь
                int len = attributes.getLength();
                //перебираем все атрибуты тега tag
                String k = attributes.getQName(0);
                String oneway = attributes.getValue(0);
                //обращаемся к атрибуту по поводу размера дороги
                if ((k.equals("k")) && (oneway.equals("oneway"))) {
                    type = attributes.getValue(1);
                    //односторонняя ---->
                    if (type.equals("yes")) {
                        right = true;
                        start.put(way_id, start_point);
                        end_.put(way_id, last);
                    }
                    //двусторонняя <---->
                    else if (type.equals("no")) {
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
                } else {
                    if (!last.equals(start_point)) {
                        both_way.put(way_id, start_point + "<->" + last);
                    } else {
                        closed.put(way_id, last);
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName == "nd") {
            end++;
            yes = true;

        } else if (qName == "way") {
            if (last.equals(start_point)) {
                closed.put(way_id, last);
            }
            yes = false;
        }
    }


public static String get_place(int x,int y,Map<String,String> map){

        Iterator iterator=map.entrySet().iterator();
        double prev_distance=0,current_distance=0;
        String point="";
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            String [] coordinates=pair.getValue().toString().split(",");//0-x,1-y
            String id=pair.getKey().toString();
            current_distance=Math.sqrt(Math.pow((x-Integer.valueOf(coordinates[0])),2)
                    +Math.pow((y-Integer.valueOf(coordinates[1])),2));
            if(current_distance<prev_distance){
                point=id; //запоминаем ближайшую точку
                prev_distance=current_distance;
            }

            prev_distance=current_distance;
        }
        result_map.put("current",String.valueOf(current_distance)); //занесли расстояние
        return point;
}
    //формируем карту веса для вершин U,V
    public static void make(Map<String, String> map, int size) {
        int w;
        Iterator iterator = map.entrySet().iterator();
        //для каждой вершины графа
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //получаем смежные
            String[] s = pair.getValue().toString().split(";");
            for (int i = 0; i < s.length; i++) {

                String[] h = coordinates.get(s[i]).split(",");
                //считаем показатели
               // h[0]= h[0].replace(".",",");
                //h[1]=h[1].replace(".",",");
                double lan1 = Double.parseDouble(h[0]);
                double lon1 =  Double.parseDouble(h[1]);
                String[] h2 = coordinates.get(pair.getKey()).split(",");
               // h2[0]= h2[0].replace(".",",");
              //  h2[1]=h2[1].replace(".",",");
                //считаем показатели
                double lan2 =  Double.parseDouble(h2[0]);
                double lon2 =  Double.parseDouble(h2[1]);
                //считаем вес
                w = (int) (Math.sqrt(Math.pow((lan1 - lan2), 2) + Math.pow(lon1 - lon2, 2)));
                //result_map.put(pair.getKey() + "+" + s[i], w);
            }
        }
    }

    //обход в ширину
    //String [] visited=new String [adjacency.size()]; //список посещенных вершин
    Map<String, String> visited = new TreeMap<String, String>();
    // Map<Integer, String> way = new TreeMap<Integer, String>();
    Map<Integer, String> every_way = new TreeMap<Integer, String>();
    ArrayDeque<String> queue = new ArrayDeque<String>();

    //v - узел


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

    public static void Dijkstra2(Map<String, String> map,Map<String, String> map2,String s,String[] t,boolean solve) {

        int length = map.size(); //10
        int t_length=t.length;
        boolean[] visited = new boolean[length];
        Map <Integer,String> visited2=new TreeMap<Integer, String>();
        Map <String,String> neighbour=new TreeMap<String, String>();
        Map <Integer,Integer> p=new TreeMap<Integer, Integer>();
        //Vector<Integer>p=new Vector<Integer>();
        int end=0;
        double[] d = new double[length];
        double new_d,help=0;
        double min;
        int dist[] = new int[length];
        Iterator iterator3= map.entrySet().iterator();
        int s2=0,place=0;
        int []place2=new int[t_length];
        int t2=0;
        int i=0;
        //определяем положение начальной точки и конечных точек(индексы)
        while (iterator3.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator3.next();
            String ids=pair.getKey().toString();
            if(ids.equals(s)){
                place=s2;
            }
            if(ids.equals(t[i])){
                place2[i]=t2; //каждому ресторану ставим в соответствие номер положения в карте
                i++;
            }
            s2++;
            t2++;
        }
        //initialization
        d[place] = 0;
        // visited[s] = false;
        int current,index=0;
        current = place;
        //пометка непосещенных вершин
        for (int k  = 0; k < length; k++) {
            if (k != current) {
                visited[k] = false;
                d[k] = 2000000000;
            }
        }
        //обращаемся к каждой вершине
        for (i = 0; i < length; i++) {
            min = 2000000000;
            //среди всех вершин находим с минимальным d
            for (int q = 0; q < length; q++) {
                //ищем непосещенную с минимальным показателем
                if ((visited[q] == false) && (d[q] <= min)) {
                    min = d[q]; //v=i - определили вершину с минимальным весом
                    index = q;
                }
            }
            //u=index; //вершина
            current=index;
            visited[current]=true; //и отмечаем ее использованной

            //поиск расположения элемента
            Iterator iterator = map.entrySet().iterator();
            int z=0;
            String id="";
            boolean cont=true;
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                if(z==current){
                    id=pair.getKey().toString();
                }
                z++;
            }

            visited2.put(current,"visited");
            //определяем смежные
            String children[] = map.get(id).split(";"); // id соседей
            int l=children.length;

            int h = 0;
            //+СМЕЖНЫЕ
            int sp=current;
            for (int j = 0; j < length; j++) {

                if (((visited[j] == false)) && ((map2.get(current + "+" + j) != null))) {
                    // System.out.println("yyeah");
                    // если вершина не помечена
                    if (!map2.get(current + "+" + j).equals("0.0")) {
                        h++;
                        //если мы их еще не посетили
                        if (visited2.get(j) == null) {
                            sp=j;
                            //от текущего до соседа
                            new_d = d[current] + Double.valueOf(map2.get(current + "+" + j));
                            if (new_d < d[j]) {
                                d[j] = new_d; //длины кратчайших путей до всех вершин
                                p.put(j,current);
                            }
                        }
                    }
                }
                //конец
                if(h==l){
                    j=length-1;
                }
            }
        }
        int from=0;
        if(p.size()==0){
            System.out.println("Дороги не существует!");
        }
        /*
        //вывод кратчайшего пути
      Vector<Integer> path=new Vector<Integer>();

        for (int v=place2; v!=0; v=p.get(v))
            path.add(v);
        for(int i=0;i<path.size();i++)
        System.out.println(path.get(i)+"-"+d[path.get(i)]);
        System.out.println(place);
*/
      //  Collection<Integer> result = p.values();
      //  System.out.println("Список смежности:" + result);
//поиск всех кратчайшх путей и вывод самого меньшего
        Vector<Integer> path=new Vector<Integer>();
        double min_distance=0;
        double distance=0;
        int current_way=0;
        //для каждой конечной точки
        for(i=0;i<t_length;i++) {

            //получаем предков до начальной точки
            for (int v = place2[i]; v != 0; v = p.get(v))
                path.add(v);

            //cчитаем длину кратчайшего расстояния
            for (int j = 0; j < path.size();j++) {
                System.out.println(path.get(j) + "-" + d[path.get(j)]);
                distance=distance+d[path.get(j)]; //считаем расстояние
                if(ways_to_restaurant.containsKey(t[i])) {
                    ways_to_restaurant.put(t[i],ways_to_restaurant.get(t[i])+"-"+ String.valueOf(path.get(j))); //для отображения на карте
                }
                else{
                    ways_to_restaurant.put(t[i], String.valueOf(path.get(j)));
                }
            }
            //если текущее расстояние меньше минмального,запоминаем ресторан
            if(distance<min_distance){
                current_way=i;
                min_distance=distance;
            }
            System.out.println(place);

        }
        near_restaurant=t[current_way]; //нашли ближайший ресторан

    }
    static class RMQ {
        int n;
        int[] val;
        int[] ind;

        RMQ(int size) {
            n = size;
            val = new int[2 * n];
            ind = new int[2 * n];
            fill(val, INF);
            for (int i = 0; i < n; i++)
                ind[n + i] = i;
        }

        void set(int index, int value) {
            val[n + index] = value;
            for (int v = (n + index) / 2; v > 0; v /= 2) {
                int l = 2 * v;
                int r = l + 1;
                if (val[l] <= val[r]) {
                    val[v] = val[l];
                    ind[v] = ind[l];
                } else {
                    val[v] = val[r];
                    ind[v] = ind[r];
                }
            }
        }

        int minIndex() {
            return val[1] < INF ? ind[1] : -1;
        }
    }

    public static void Levita(Map<String,String> map1,Map<String,String> map2,String s,String[] t){

        int size=map1.size();
        Map<Integer,Double> d = new TreeMap<Integer, Double>(); //кратчайшие пути
        Map<Double,Double> p = new TreeMap<Double, Double>(); //кратчайшие пути
            for(int i=0;i<size;i++){
                d.put(i,2000000000000.0);
            }
        double new_d=0;
        int s2=0,place=0;
        int t_length=t.length;
        int []place2=new int[t.length];
        int t2=0;
        int i=0;
        Iterator iterator= map1.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String ids=pair.getKey().toString();
            if(ids.equals(s)){
                place=s2;
            }
             if(ids.equals(t[i])){
             place2[i]=t2; //каждому ресторану ставим в соответствие номер положения в карте
            i++;
        }
            s2++;
            t2++;
        }
        //initialization

        ArrayDeque<Double>  q = new ArrayDeque<Double>();
        Map<Double,Double>id=new TreeMap<Double, Double>();
        for(i=0;i<size;i++){
            id.put((double)i,0.0);
        }
        double current;
        current = place;

          q.addLast(current);
          d.put((int)place,0.0);
        //2 = M0
        while(!q.isEmpty()){
            current=q.getFirst(); //берем верхний элемент - v
            q.pop();
            // M0.add(current);
            id.put(current,1.0); //M1
            int h=0;
            Iterator iterator2 = map1.entrySet().iterator();
            int z=0;
            String id_s="";
            boolean cont=true;
            while (iterator2.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator2.next();
                if(z==current){
                    id_s=pair.getKey().toString();
                }
                z++;
            }

            //определяем смежные
            String children[] = map1.get(id_s).split(";"); // id соседей
            int l=children.length;
//соседи
            for (int j = 0; j < size; j++) {

               if(map2.get((int)current + "+" + j) != null) {

                    // если вершина не помечена
                    if (!map2.get((int)current + "+" + j).equals("0.0")) {
                        h++;
                        //если мы их еще не посетили
                        //System.out.print(Double.parseDouble(map2.get((int)current + "+" + j)));
                            //от текущего до соседа
                            new_d = d.get((int)current) + Double.parseDouble(map2.get((int)current + "+" + j));
                            if (new_d < d.get(j)) {
                                d.put(j, new_d); //длины кратчайших путей до всех вершин
                                if(id.get((double)j)==0.0) //M2
                                q.addLast((double)j);
                                else if(id.get((double)j)==1.0)
                                    q.addFirst((double)j);
                                p.put((double)j,current);
                                id.put((double)j,1.0);
                            }

                    }
                }
                //конец
                if(h==l){
                    j=size-1;
                    System.out.println(q.size());
                }
            }


         /*
*/
        }
        Vector<Integer> path=new Vector<Integer>();
        double min_distance=0;
        double distance=0;
        int current_way=0;
        //для каждой конечной точки
        for(i=0;i<t_length;i++) {

            //получаем предков до начальной точки
            for (double v=(double)place2[i]; v!=0; v=p.get((double)v))
                path.add((int)v);

            //cчитаем длину кратчайшего расстояния
            for (int j = 0; j < path.size();j++) {
                System.out.println(path.get(j) + "-" +d.get(path.get(j)));
                distance=distance+d.get(path.get(j)); //считаем расстояние
                if(ways_to_restaurant.containsKey(t[i])) {
                    ways_to_restaurant.put(t[i],ways_to_restaurant.get(t[i])+"-"+ String.valueOf(path.get(j))); //для отображения на карте
                }
                else{
                    ways_to_restaurant.put(t[i], String.valueOf(path.get(j)));
                }
            }

            //если текущее расстояние меньше минмального,запоминаем ресторан
            if(distance<min_distance){
                current_way=i;
                min_distance=distance;
            }
            System.out.println(place);

        }
        near_restaurant=t[current_way]; //нашли ближайший ресторан
    }
    public static double heuristic(double a, double b) {
        double a_x,a_y,b_x,b_y;
        String []l=coordinates.get(a).split(",");
        a_x=Double.parseDouble(l[0])*1000;
        a_y=Double.parseDouble(l[1])*1000;
        String []l2=coordinates.get(b).split(",");
        b_x=Double.parseDouble(l2[0])*1000;
        b_y=Double.parseDouble(l2[1])*1000;

        return Math.abs(a_x-b_x)+Math.abs(a_y-b_y);
    }

    public static void A2(Map<String,String> map,Map<String,String> map2,String s,String t,boolean solve){
        int size=map.size();
        PriorityQueue<Integer> openQueue=new PriorityQueue<Integer>(); //ождающие рассматроения
        PriorityQueue<Integer> clothedQueue=new PriorityQueue<Integer>(); //прошедшие рассмотрение
        boolean [] visited=new boolean[size];
        Map <Integer,String> visited2=new TreeMap<Integer, String>();
        double new_d=0;
        double priority=0;
        double o=0;
        Map <Integer,Integer> p=new TreeMap<Integer, Integer>();
        Map <Double,Double> d=new TreeMap<Double, Double>();
        double g[]=new double[size];
        double f[]=new double[size];
        int place=0,place2=0;
        //double  []d=new double[size];

        int current;
        Iterator iterator3=map.entrySet().iterator();
        int s2=0,t2=0;
        while (iterator3.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator3.next();
            String ids=pair.getKey().toString();
            if(ids.equals(s)){
                place=s2;
            }
            if(ids.equals(t)){
                place2=t2;
            }
            s2++;
            t2++;
        }
        g[place]=0;
        if(solve==true) {
            f[place] = g[place] + Double.parseDouble(distance3.get(place + "-" + place2));
        }
        else{
            f[place] = g[place] + heuristic(place,place2);
        }
        for (int k  = 0; k <size; k++) {
            if (k != place) {
                visited[k] = false;
                g[k] = 2000000000;
                f[k]= 2000000000;
            }
        }
        double min=0;
        int index=0;
        for (int i = 0; i < size; i++) {
            min = 2000000000;
            //среди всех вершин находим с минимальным d
            for (int q = 0; q < size; q++) {
                //ищем непосещенную с минимальным показателем
                if ((visited[q] == false) && (f[q] <= min)) {
                    min = f[q]; //v=i - определили вершину с минимальным весом
                    index = q;
                }
            }
            //вершина
            current=index;
            visited[current]=true; //и отмечаем ее использованной

            // visited[current]=true;

            int h=0;
            Iterator iterator = map.entrySet().iterator();
            int z=0;
            String id="";
            boolean cont=true;
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                if(z==current){
                    id=pair.getKey().toString();
                }
                z++;
            }
            clothedQueue.add(current);
            visited2.put(current,"visited");

            //определяем смежные
           String children[] = map.get(id).split(";"); // id соседей
            int l=children.length;
            Iterator iterator2 = map.entrySet().iterator();
            int position=0;
            for(int j=0;j<size;j++){

                if (((visited[j]==false)) && ((map2.get(current + "+" + j) != null))) {
                    // System.out.println("yyeah");
                    // если вершина не помечена
                    if (!map2.get(current + "+" + j).equals("0.0")) {
                        h++;
                        if (visited2.get(j) == null) {
                            new_d = g[current] + Double.valueOf(map2.get(current + "+" + j));
                            //от текущего до соседа
                            // new_d = d.get(current) + Double.valueOf(result_map.get(current + "+" + j));
                            if (new_d < g[j]) {
                                g[j] = new_d;
                                if(solve=true) {
                                    f[j] = g[j] + Double.valueOf(distance3.get(j + "-" + place2));

                                }
                                else {
                                    f[j] = g[j] + heuristic(j, place2);
                                }
                                p.put(j, current);
                            }
                        }
                    }
                    h++;
                }

                //конец
                if(h==l){
                    j=size-1;
                }
            }


        }
        Vector<Integer> path=new Vector<Integer>();
        double distance=0;

        for (int v=place2; v!=0; v=p.get(v))
            path.add(v);
        for (int j = 0; j < path.size();j++) {
            System.out.println(path.get(j) + "-" +d.get(path.get(j)));
            distance=distance+d.get(path.get(j)); //считаем расстояние
            if(ways_to_restaurant.containsKey(t)) {
                ways_to_restaurant.put(t,ways_to_restaurant.get(t)+"-"+ String.valueOf(path.get(j))); //для отображения на карте
            }
            else{
                ways_to_restaurant.put(t, String.valueOf(path.get(j)));
            }
        }
        ways_to_restaurant.put("width",String.valueOf(distance));

    }
}





