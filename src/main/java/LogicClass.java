import au.com.bytecode.opencsv.CSVWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Снежа on 10.03.2018.
 */
public class LogicClass extends DefaultHandler {
    static int tagLength=0;
    static int ndLength=0;
    static String way_id;
    static String value;
    static String last="";
    static String start_point="";
    static int end=0;
    static boolean second=false;
    public static double minlon,minlat,maxlat,maxlon;
    static boolean yes=false;
    static int step=0;//для начала точки
    static boolean left=false,right=false,both=false;
    static int V;
    static Map <String, String>nodes=new TreeMap();
    static Map <String, String>crosses=new TreeMap();
    static Map <String, String>start=new TreeMap();
    static Map <String, String>end_=new TreeMap();
    static Map <String, String>adjacency=new TreeMap();
    static Map <String, String>closed=new TreeMap();
    static Map <String, String>both_way=new TreeMap();
    static Map <String, String>coordinates=new TreeMap();
    static Map <String, String>points=new TreeMap();

   public static void go() throws ParserConfigurationException, IOException, SAXException {

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


       //запись списка в .csv
       Iterator iterator = adjacency.entrySet().iterator();
       String csv = "data.csv";
       CSVWriter writer = new CSVWriter(new FileWriter(csv));
       String [] ColumnNamesList = {"Id вершины","="+"Id смежных с ней вершин"};
       writer.writeNext(ColumnNamesList);

       while (iterator.hasNext()) {
           Map.Entry pair = (Map.Entry) iterator.next();
           String [] n=pair.getValue().toString().split(";");
           for(int i=0;i<n.length;i++){
               if(points.get(n[i])==null) {
                   points.put(n[i], n[i]);
               }

           }
           String h=pair.getKey().toString();
           if(!points.containsKey(h)){
               points.put(h, h);
           }
           String [] s=pair.getValue().toString().split("'");
           String [] s2=pair.getKey().toString().split(",");
           String [] result=new String[]{s2[0]+"="+s[0]};
           writer.writeNext(result);
       }
       writer.close();
       Collection<String> result =points.values();
       System.out.println("Список смежности:" + result);
       V = result.size(); //число вершин
       second=true;
       handler = new LogicClass();
       factory = SAXParserFactory.newInstance();
       factory.setValidating(false);
       parser = factory.newSAXParser();
       parser.parse(new File("map2.xml"), handler);



   }


public static void get(Map<String,String> map1,Map<String,String> map2){

    Iterator iterator=map1.entrySet().iterator();
    String next;
    int size=0;
    int step=0;
    while (iterator.hasNext()) {
        Map.Entry pair = (Map.Entry) iterator.next();
        //разрезаем на пути
        String[] s = (map1.get(pair.getKey())).split("="); //way id
        String[] ray;
        String[] ss;
        int k=0;

        if (map2 == both_way) {

            ray= new String[s.length*2];
            for (int j = 0; j < s.length*2; j++) {
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
            }
        else {
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
                        adjacency.put(ray[i],adjacency.get(ray[i]) + ";" + next);
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
    public static void search(Map<String,String> map1,Map<String,String> map2){
    int size=0;
        Iterator iterator =map2.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //если нет какой-либо точки в списке смежности - 0
            String []s=(pair.getValue()).toString().split("<->");
            if(map2==both_way){
                for (int i=0;i<s.length;i++){
                if (!map1.containsKey(s[i])) {
                    size++;
                    map1.put(s[0],s[1]) ; //начальную точку с конечной
                }
                }
            }
            else {
                if (!map1.containsKey(pair.getValue())) {
                    if (map2 == start) {
                        map1.put(pair.getValue().toString(), end_.get(pair.getKey()).toString()); //начальную точку с конечной
                    } else if (map2 == end_) {
                        map1.put(pair.getValue().toString(), start.get(pair.getKey()).toString()); //начальную точку с конечной
                    }
                    else if (map2 == closed) {
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
        String id="";
        String lat="";
        String lon="";
        if(second==true){
            //перебираем все точки
            if(qName=="bounds") {
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
                for(int j=0;j<length;j++) {

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
                if(points.get(id)!=null){
                    coordinates.put(id,lat+","+lon);
                }

            }
        }
else {
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

        if(qName=="nd") {
            end++;
            yes=true;

        }
        else  if(qName=="way") {
                 if (last.equals(start_point)) {
                     closed.put(way_id, last);
                 }
            yes=false;
        }
    }
}