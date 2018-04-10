/**
 * Created by Снежа on 30.03.2018.
 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class GetRestaurants  extends DefaultHandler {

    private static int amount=0;
    private static int step=0;
    private static String value;
    private static int tag_count=0;
    private static int count=0;
    private static boolean go=false;
    private static boolean tags=false;
    private static boolean tag=false;
    private static String point;
    private static int nd_count=0;
    private static String way_id;
    private static ArrayList values=new ArrayList();
    public static Map<String,String>objects_more=new TreeMap<String,String>();
    public static Map<String,String>objects=new TreeMap<String,String>();
    public static Map<String,String>ten_objects=new TreeMap<String,String>();
    public static Map<String,String>objects_coordinates=new TreeMap<String,String>();
    public static Map<String,Integer>c=new TreeMap<String, Integer>();
    public static Map<String,String>nodes=new TreeMap<String, String>();
    public static Map<String,String>names=new TreeMap<String, String>();
    public static Map<String,String>coordinates=new TreeMap<String, String>();
    public static Map <String,String> restaurant_position=new TreeMap<String, String>();
    public static void go() throws ParserConfigurationException, IOException, SAXException {
        System.out.print("Ждем ..");
        DefaultHandler handler = new GetRestaurants();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File("map3.xml"), handler);
        factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        parser = factory.newSAXParser();
        go=true;
        parser.parse(new File("map3.xml"), handler);
        get(objects,ten_objects);
      //  get(objects_more,ten_objects);

        Iterator iterator = ten_objects.entrySet().iterator();
        int j=0;
        String way_id,point="";

        while (iterator.hasNext() ) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String id=pair.getKey().toString(); //точка ресторана
            String value=pair.getValue().toString(); //ближайшая точка к ресторану

           // if(LogicClass.nodes.containsKey(id))
            //way_id=LogicClass.nodes.get(id); //получаем путь, по которому доедем до кафе

            //else{
                way_id=LogicClass.nodes.get(value);
            //}
          /*if(way_id==null){
              restaurant_position.put(id,"not");
          }
            else
            */
            if(LogicClass.start.containsKey(way_id)){

                point=LogicClass.start.get(way_id);//startpoint
                restaurant_position.put(value,point); //от point к value
              if(!LogicClass.adjacency.containsKey(value)){
                  if(!point.equals(id))
                      LogicClass.adjacency.put(value,point+";"+id);//делаем их смежными
                  else
                      LogicClass.adjacency.put(value,id);//делаем их смежными
              }
              else{
                  if(!point.equals(id))
                      //дополняем расположением ресторана
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+point+";"+id);

                  else
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+id);
              }

            }
            else if(LogicClass.end_.containsKey(way_id)){
                point=LogicClass.end_.get(way_id);//endpoint
                restaurant_position.put(value,point);
              if(!LogicClass.adjacency.containsKey(value)){
                  if(!point.equals(id))
                  LogicClass.adjacency.put(value,point+";"+id);//делаем их смежными
                  else
                      LogicClass.adjacency.put(value,id);//делаем их смежными
              }
              else{
                  if(!point.equals(id)) {
                      //дополняем расположением ресторана
                      LogicClass.adjacency.put(value, LogicClass.adjacency.get(value) + ";" + point + ";" + id);

                  }
                  else {
                      LogicClass.adjacency.put(value, LogicClass.adjacency.get(value) + ";" + id);
                  }
              }

            }
            else if(LogicClass.both_way.containsKey(way_id)) {
                point = LogicClass.both_way.get(way_id);//endpoint
              String s[]=point.split("<->");
                restaurant_position.put(value,s[0]);
              if(!LogicClass.adjacency.containsKey(value)){
                  if(!s[0].equals(id))
                      LogicClass.adjacency.put(value,s[0]+";"+id);//делаем их смежными
                  else
                      LogicClass.adjacency.put(value,id);//делаем их смежными
              }
              else{
                  if(!s[0].equals(id))
                      //дополняем расположением ресторана
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+s[0]+";"+id);
                  else
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+id);
              }
            }
            //начальных точек в adjency нет
            else if(LogicClass.closed.containsKey(way_id)) {
                point = LogicClass.closed.get(way_id);//начальная точка пути, ее нет в adgency
                restaurant_position.put(value, point);
            //если точка не рассмотрена
              if(!LogicClass.adjacency.containsKey(value)){
                  if(!point.equals(id)) {
                      //ближайшей к ресторану точке ставим в соответсвие начальную точку пути и расположение кафе
                      LogicClass.adjacency.put(value, point + ";" + id);//делаем их смежными
                     // System.out.println("point="+LogicClass.adjacency.get(point));
                     LogicClass.adjacency.put(point,value);
                  }
                  else {
                      LogicClass.adjacency.put(value, id);//делаем их смежными
                      //System.out.println("point="+LogicClass.adjacency.get(point));
                      LogicClass.adjacency.put(point, id);
                  }
              }
              //если содержит точку
              else{
                  if(!point.equals(id))
                      //дополняем расположением ресторана
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+point+";"+id);
                  else
                      LogicClass.adjacency.put(value,LogicClass.adjacency.get(value)+";"+id);
              }

            }
            else{
                System.out.println("Не найден путь");
            }


        }
        Map <Long, Double>ne;
        double w;
        Iterator iterator3 = LogicClass.adjacency.entrySet().iterator();
        while (iterator3.hasNext()) {

            ne=new TreeMap<Long, Double>();
            Map.Entry pair = (Map.Entry) iterator3.next();
            String [] neighbours=pair.getValue().toString().split(";");

            //для каждого соседа
            for(int i=0;i<neighbours.length;i++){
                int to=0;
                int position=0;
                String[] h =  coordinates.get(neighbours[i]).split(",");
                double lan1 = Double.parseDouble(h[0]);
                double lon1 = Double.parseDouble(h[1]);
//взяли координаты отца
                String[] h2 = coordinates.get(pair.getKey().toString()).split(",");
//считаем показатели
                double lan2 = Double.parseDouble(h2[0]);
                double lon2 = Double.parseDouble(h2[1]);
//считаем вес
               // w = (Math.sqrt(Math.pow((lan1 - lan2), 2) + Math.pow((lon1 - lon2), 2)));
                w = (Math.sqrt(Math.pow((lan1 - lan2)*111.3, 2) + Math.pow((lon1 - lon2)*62.25, 2)));
                if(w!=0) {
                    ne.put(Long.parseLong(neighbours[i]), w);
                }
            }
            LogicClass.H2.put(Long.parseLong(pair.getKey().toString()),ne);
        }


    }
    public static void get(Map<String,String> map1, Map<String,String> map2){
        Iterator iterator = map1.entrySet().iterator();
        int j=0;
        /*
        while ((iterator.hasNext()) ){
            Map.Entry pair = (Map.Entry) iterator.next();
            //Х.О.
            if(pair.getKey().toString().equals("3938428654")){
                map2.put(pair.getKey().toString(),"4278751799"); //связываем точку кафе с ближайшей точкой
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }
            //Красный Лев++++
            if(pair.getKey().toString().equals("2824824491")){
                map2.put(pair.getKey().toString(),"4032540070");

                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }
            //Старый двор+
            if(pair.getKey().toString().equals("3037395584")){
                map2.put(pair.getKey().toString(),"3704224375");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Колизей++
            if(pair.getKey().toString().equals("1224333897")){
                map2.put(pair.getKey().toString(),"2386827168");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Роза Ветров+
            if(pair.getKey().toString().equals("1667384467")){
                map2.put(pair.getKey().toString(),"1228491043");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Дубинин+
            if(pair.getKey().toString().equals("2888549560")){
                map2.put(pair.getKey().toString(),"4420626171");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Семейный ресторан+
            if(pair.getKey().toString().equals("2391545016")){
                map2.put(pair.getKey().toString(),"3826001558");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Арай+
            if(pair.getKey().toString().equals("2447278579")){
                map2.put(pair.getKey().toString(),"4340763429");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Регистан
            if(pair.getKey().toString().equals("2808978617")){
                map2.put(pair.getKey().toString(),"2884716596");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Золотой причал+
            if(pair.getKey().toString().equals("3687106408")){
                map2.put(pair.getKey().toString(),"3095995559");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            j++;
        }
        */
        while ((iterator.hasNext()) ){
            Map.Entry pair = (Map.Entry) iterator.next();
            //Х.О.
            if(pair.getKey().toString().equals("3878857179")){
                map2.put(pair.getKey().toString(),"2597265927"); //связываем точку кафе с ближайшей точкой
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }
            //Красный Лев++++
            if(pair.getKey().toString().equals("3080046437")){
                map2.put(pair.getKey().toString(),"2597182860");

                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }
            //Старый двор+
            if(pair.getKey().toString().equals("3659785102")){
                map2.put(pair.getKey().toString(),"1004298152");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Колизей++
            if(pair.getKey().toString().equals("2445088760")){
                map2.put(pair.getKey().toString(),"2445088779");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Роза Ветров+
            if(pair.getKey().toString().equals("2494712787")){
                map2.put(pair.getKey().toString(),"2499750076");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Дубинин+
            if(pair.getKey().toString().equals("1272959450")){
                map2.put(pair.getKey().toString(),"2499939827");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Семейный ресторан+
            if(pair.getKey().toString().equals("2504444218")){
                map2.put(pair.getKey().toString(),"2504445178");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Арай+
            if(pair.getKey().toString().equals("2503395216")){
                map2.put(pair.getKey().toString(),"2533523851");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Регистан
            if(pair.getKey().toString().equals("1045191608")){
                map2.put(pair.getKey().toString(),"1064712710");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            //Золотой причал+
            if(pair.getKey().toString().equals("1376123482")){
                map2.put(pair.getKey().toString(),"2441039196");
                objects_coordinates.put(pair.getKey().toString(),coordinates.get(pair.getKey().toString()));
            }

            j++;
        }
    }
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
    step++;
    boolean solve=false;
    //tag=false;
    String lat,lon;

    if(go==true){
        if (qName == "node") {
            //если найдена id точки
            if(objects_more.containsKey(attributes.getValue(0))){
                //берем ее расположение
                lat = attributes.getValue(1);
                lon = attributes.getValue(2);
                objects.put(attributes.getValue(0), lat + "," + lon);
                nd_count++;
            }
        }
    }
    else {
        if (qName == "node") {
            value = attributes.getValue(0);
            lat = attributes.getValue(1);
            lon = attributes.getValue(2);

            if ((attributes.getQName(0).equals("id"))) {
                nodes.put(value, value);
                coordinates.put(value, lat + "," + lon);
            }
        }
        if (qName == "way") {
            way_id = attributes.getValue(0);
            tags = true;
        }
        if (qName == "nd") {
            values.add(attributes.getValue(0));
            count++; //считаем число nd(точек)
            //    solve=true;
        }

        if (qName == "tag") {

            if ((attributes.getValue(0).equals("amenity")) && ((attributes.getValue(1).equals("restaurant")))) {
                tag = true;
                String id = nodes.get(value);//получили id
                objects.put(id, coordinates.get(value));
                //случай <way><tag>
                if (tags == true) {
                    point = String.valueOf(values.get(count - 1)); //id одной из точек области
                    objects_more.put(point, "restaurant");
                }
                //if (amount != 10) {

                //objects.put();
                //     amount++;
                //  }
            }


        }


    }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
/*
        if(qName=="way"){
            nd_count=count;
            count=0;
            c.put(way_id,nd_count);
            System.out.println(tag_count);
        }
*/
    }
}

