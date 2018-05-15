/**
 * Created by Снежа on 11.03.2018.
 */

import au.com.bytecode.opencsv.CSVWriter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Graph extends JFrame {
    static LogicClass logicClass;
    static Map <String,String> coordinates=new TreeMap<String, String>();
    static GetRestaurants R;
    static String neighbor;
    static String[] restaurants;
    static Map<String,String> restaurants2=new TreeMap<String, String>();
    static String[] random_points;
   // static View view;
    static Object v1,v2;
    static boolean second_step=false;
    static Object vertexes[];
    static double x,y;
    static double getX,getY;
    static JFrame frame,frame2;
    static JLabel text,text2,text3,for_x,for_y;
    static JPanel panel,panel2;
    static Point point;
    static String [] id_points;
    static mxGraph graph;
    static Object parent;
    static JTextField x_coordinates,y_coordinates;
    static int width=1000,height=1000;

    double print_X,print_Y;
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        long start_time = System.currentTimeMillis();
        logicClass=new LogicClass();
        R=new GetRestaurants();
        logicClass.go();
        R.go();
        restaurants=new String[10];
        random_points=new String[100];
        //БЛИЖАЙШИЕ точки в качестве значений
        Iterator iterator2 =GetRestaurants.ten_objects.entrySet().iterator();
        int i=0;
        while (iterator2.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator2.next();
            //String []places=pair.getValue().toString().split(";");
            restaurants[i]=pair.getValue().toString();
            restaurants2.put(pair.getValue().toString(),String.valueOf(i));
            i++;
        }

        Iterator iterator =logicClass.adjacency.entrySet().iterator();
        Random rand=new Random();
        rand.setSeed(10);
        int position=rand.nextInt(100);
        int step=0;
        int k=0;
        int f=0;
            frame = new Graph();

        //тестирование алгоритмов на 100 точек равномерно распределенных
        while ((iterator.hasNext())&&(k!=100)) {
            Map.Entry pair = (Map.Entry) iterator.next();
            if(step==f+position){
                random_points[k]=pair.getKey().toString();
                k++;
                f=step;
            }
            step++;
        }


//        test(random_points);

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame2=new JFrame();
        frame2.setSize(200,230);

        frame2.setLocation(20,40);
        frame2.setUndecorated(true);
        text=new JLabel("Введите координаты");
        text2=new JLabel("вашего местоположения");
        text3=new JLabel("или нажмите на карте");
        text.setBounds(20,0,1000,10);
        text2.setBounds(20,10,1000,10);
        text3.setBounds(20,200,1000,20);
        panel=new JPanel();
        panel.setBackground(Color.white);

        panel.setLayout(null);
        for_x=new JLabel("x:");
        for_y=new JLabel("y:");

        for_x.setBounds(20,100,10,10);
        for_y.setBounds(70,93,10,20);
       // for_x.setVisible(true);

        x_coordinates=new JTextField();
        y_coordinates=new JTextField();

        x_coordinates.setBounds(35,95,20 ,20);
        y_coordinates.setBounds(85,95,20,20);
        JButton take=new JButton("Получить ближайший ресторан!");
        JButton take_all_ways=new JButton("Получить путь от текущего склада!");
        take.setVisible(true);
        take_all_ways.setVisible(true);
        take.setBounds(20,140,170,20);
        take_all_ways.setBounds(20,170,170,20);
        panel.add(text);
        panel.add(text2);
        panel.add(text3);
        panel.add(for_x);
        panel.add(x_coordinates);
        panel.add(for_y);
        panel.add(y_coordinates);
        panel.add(take);
        panel.add(take_all_ways);

        ActionListener actionListener = new TakeActionListener();
        take.addActionListener(actionListener);
        ActionListener actionListener2 = new TakeActionListener2();
        take_all_ways.addActionListener(actionListener2);
        frame2.add(panel);
        frame.setVisible(true);
        frame2.setVisible(true);

        long finish_time = System.currentTimeMillis();
        System.out.format("%d ms", finish_time - start_time);

    }

    public Graph()
    {
        super("Graph");

        graph = new mxGraph();
        parent = graph.getDefaultParent();


        graph.getModel().beginUpdate();
        /*
        Map<String, Object> edge = new HashMap<String, Object>();
        edge.put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
        edge.put(STYLE_STROKECOLOR, "#7d2f58");
        mxStylesheet edgeStyle = new mxStylesheet();
        edgeStyle.setDefaultEdgeStyle(edge);
        graph.setStylesheet(edgeStyle);
        */
        int k=20,z=20;
        Random rand=new Random();
        boolean decision=false;
        boolean decision2=false;
        final Map <String,Integer> search=new HashMap<String,Integer>();
        Map <String,Object> get=new HashMap<String,Object>();

        Iterator iterator =logicClass.adjacency.entrySet().iterator();
        Iterator iterator2 =GetRestaurants.ten_objects.entrySet().iterator();
        String key="";
        View.X=View.mercX(LogicClass.minlon);
        View.Y=View.mercY(LogicClass.minlat);

        while (iterator.hasNext()) {


            Map.Entry pair = (Map.Entry) iterator.next();

            //перекрестки
            String[] related;
            related = (logicClass.adjacency.get(pair.getKey())).split(";"); //разрезаем id перекрестка = id точек пересечения
            String[] param = new String[2];
            String vertex=pair.getKey().toString();//вершина
           //координаты

            param=logicClass.coordinates.get(vertex).split(",");
            new View(Double.parseDouble(param[0]),Double.parseDouble(param[1])); //lat lon
            set_coordinates();
            print_X=get_variable("euclid_X");
            print_Y=get_variable("euclid_Y");
            coordinates.put(vertex,print_X+","+print_Y); //запсываем координаты
            v1=graph.insertVertex(parent, null,"",  print_X, print_Y, 11, 1,"fillColor=gold");
            //System.out.println(pair.getKey()+":"+logicClass.adjacency.get(pair.getKey()));
            related = (logicClass.adjacency.get(pair.getKey())).split(";");
            Object []v=new Object[related.length];
            for(int i=0;i<related.length;i++){
             //   System.out.println("-"+related[i]);
                param=logicClass.coordinates.get(related[i]).split(",");

                new View(Double.parseDouble(param[0]),Double.parseDouble(param[1])); //lat lon
                set_coordinates();
                print_X=get_variable("euclid_X");
                print_Y=get_variable("euclid_Y");
                coordinates.put(related[i],print_X+","+print_Y);
                v[i]= graph.insertVertex(parent, null,"",  print_X, print_Y, 1, 1,"fillColor=gold");
            }
            for(int i=0;i<related.length;i++){
                graph.insertEdge(parent, null, "", v1, v[i],"strokeColor=mediumpurple"); //v1->v2
            }


        }
        Object v4;
        int col=0;
        String value="";
        while (iterator2.hasNext()) {
            switch (col){
                case 0:
                    value="Ф и Я";
                    break;
                case 1:
                    value="Лас-Вегас";
                    break;
                case 2:
                    value="Davidoff";
                    break;
                case 3:
                    value="Восвояси";
                    break;
                case 4:
                    value="Пирог";
                    break;
                case 5:
                    value="Злата";
                    break;
                case 6:
                    value="Шинок";
                    break;
                case 7:
                    value="Family";
                    break;
                case 8:
                    value="IL";
                    break;
                case 9:
                    value="Маруся";
                    break;

            }
            Map.Entry pair = (Map.Entry) iterator2.next();
            String[] related;
            System.out.println(pair.getKey()+"="+value);
            related = (GetRestaurants.objects_coordinates.get(pair.getKey())).split(",");

                new View(Double.parseDouble(related[0]),Double.parseDouble(related[1])); //lat lon
                set_coordinates();
                print_X=get_variable("euclid_X");
                print_Y=get_variable("euclid_Y");
                coordinates.put(pair.getKey().toString(),print_X+","+print_Y);
                v4= graph.insertVertex(parent, null,value,  print_X, print_Y, 2, 2,"fillColor=bisque");
col++;
        }
System.out.println("количество="+col);
        panel2=new JPanel();
        add(panel2);


        //graph.insertVertex(parent, null,"right",20   , 100, 20   , 20);

        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        final Object cell;

        //запрос на точку
        mxGraphComponent.mxGraphControl graphControl = graphComponent.getGraphControl();
        graphControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //удалить предыдущую вершину
              //  graph.getModel().beginUpdate();
            /*  if(second_step){
                    for(int i=0;i<id_points.length;i++) {

                            String[] coord = coordinates.get(id_points[i]).split(",");
                            vertexes[i] = graph.insertVertex(parent, null, "  ", Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 3, 3, "fillColor=yellow");
                            if (i > 0) {
                                graph.insertEdge(parent, null, "",  vertexes[i],vertexes[i - 1], "strokeColor=mediumpurple"); //v1->v2
                            }
                        }
                }
*/
                int x, y;
                //получили координаты
                getX = e.getX();
                getY = e.getY();
                x = (int) getX;
                y = (int) getY;
                try {
                  //  search_way(x,y);
                    search_way_from(x,y); //3 задание
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        graph.getModel().endUpdate();
       // getContentPane().add(graphControl);
        getContentPane().add(graphComponent);
        panel2.setBackground(Color.cyan);


    }


    public static void search_way(int x,int y) throws IOException {

        /*if(second_step){
            for(int i=0;i<id_points.length;i++) {

                String[] coord = coordinates.get(id_points[i]).split(",");
                vertexes[i] = graph.insertVertex(parent, null, "  ", Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 3, 3, "fillColor=yellow");
                if (i > 0) {
                    graph.insertEdge(parent, null, "",  vertexes[i],vertexes[i - 1], "strokeColor=mediumpurple"); //v1->v2
                }
            }
        }
                */
        point= new Point(x, y);
        System.out.println(point);
        //graph.removeCellsFromParent();
        //  graph.removeSelectionCell(v2);
        neighbor=logicClass.get_place(x,y,coordinates); //определяем ближайшего к точке соседа
        //logicClass.adjacency.put("current",neighbor); //занесли текущую точку и ее соседа
        v2=graph.insertVertex(parent, null,"fromHere", point.x , point.y, 3   , 3,"fillColor=firebrick");
        String []dh=coordinates.get(neighbor).toString().split(",");
        String []dh2=LogicClass.coordinates.get(neighbor).toString().split(",");
        graph.insertVertex(parent, null,"from", Double.parseDouble(dh[0]) ,Double.parseDouble(dh[1]), 10   , 10,"fillColor=firebrick");

        shortest_distance(neighbor, logicClass.H2,"Dijkstra");
    //    shortest_distance(neighbor, logicClass.H2,"Levita");
      //  shortest_distance(neighbor, logicClass.H2,"A*");

        //отображение короткого пути

        String csv3 = "data6.csv";

        CSVWriter writer2 = new CSVWriter(new FileWriter(csv3));
        if(logicClass.near_restaurant!=null){

            vertexes=new Object[id_points.length];

            for(int i=0;i<id_points.length;i++) {

                //  dh2=LogicClass.coordinates.get(id_points[i]).toString().split(",");
                System.out.println(id_points[i]);
                if(coordinates.containsKey(id_points[i])) {
                    String[] coord = coordinates.get(id_points[i]).split(",");
                    vertexes[i] = graph.insertVertex(parent, null, i, Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 3, 3, "fillColor=yellow");
                    if (i > 0) {
                        graph.insertEdge(parent, null, "",  vertexes[i],vertexes[i - 1], "strokeColor=red"); //v1->v2
                    }
                }
                else{
                    graph.insertEdge(parent, null, "",  v2,vertexes[id_points.length-2], "strokeColor=red"); //v1->v2

                }
            }

            second_step=true; }

        else{
            System.out.println("Пути не существует");
        }
        writer2.close();

    }

    //громадный путь из маленьких путей
    public static void search_way_from(int x,int y) throws IOException {
        point= new Point(x, y);
        neighbor=logicClass.get_place(x,y,coordinates); //определяем ближайшего к точке соседа
        //logicClass.adjacency.put("current",neighbor); //занесли текущую точку и ее соседа
        v2=graph.insertVertex(parent, null,"fromHere", point.x , point.y, 3   , 3,"fillColor=firebrick");
        String []dh=coordinates.get(neighbor).toString().split(",");
        String []dh2=LogicClass.coordinates.get(neighbor).toString().split(",");
        graph.insertVertex(parent, null,"from", Double.parseDouble(dh[0]) ,Double.parseDouble(dh[1]), 10   , 10,"fillColor=firebrick");
        long start_time = System.currentTimeMillis();
        logicClass.neighbor_algorithm(neighbor);
        long finish_time = System.currentTimeMillis();
        System.out.println("Время поиска путей:"+(finish_time-start_time));
        if(logicClass.near_restaurant!=null) {
//визуализация пути
            Iterator iterator3 = logicClass.main_way.entrySet().iterator();

            int step=1;
            while (iterator3.hasNext()) {
                System.out.println(step);
                Map.Entry pair = (Map.Entry) iterator3.next();
                String[] points = pair.getValue().toString().split("-"); //получаем каждый путь
                vertexes = new Object[points.length];
                //для каждой вершины данного пути производим прорисовку пути


                    for (int i = 0; i < points.length; i++) {
                        if (coordinates.containsKey(points[i])) {
                            String[] coord = coordinates.get(points[i]).split(",");
                            if (i == 0) {
                                vertexes[i] = graph.insertVertex(parent, null, "step="+step, Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 3, 3, "fillColor=yellow");
                            } else {
                                vertexes[i] = graph.insertVertex(parent, null, "", Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), 3, 3, "fillColor=yellow");
                            }
                            if (i > 0) {
                                graph.insertEdge(parent, null, "", vertexes[i - 1], vertexes[i], "strokeColor=red"); //v1->v2
                            }
                        } else {
                            graph.insertEdge(parent, null, "", v2, vertexes[points.length - 2], "strokeColor=red"); //v1->v2

                        }
                    }
                    step++;
                }


            }
        }
    static double get_time(double d){
        //111км в одном градусе
        //умножаю координаты на 1000. 1PX = 1000 m =1 km
        //в метрах!
      //  System.out.println("расстояние="+d);
        int hours=0;
        int seconds=0;
        double minutes=0;
        int finish_seconds=0;
        int km=0;
        double start =d;
        double ost=0;

     //   String s=String.valueOf(d);
     //   String []dd=s.split(".");
        minutes=60*d/40;
        //System.out.println("результат="+minutes);
        int m=(int)minutes;
        double s=minutes-m;
        s=s*100;
        seconds=(int)s;

        while((seconds-60)>0){
            minutes=minutes+1;
            seconds=seconds-60;
        }
        seconds=seconds/100;

        //считаем, сколько всего км до ближайшего ресторана
        //while((d/1000)!=0) {
            //ost=ost+d%1000;
            //int m=(int)(ost*1000);
            //km =km+ (int)(d/1000);
            //d=km+ost; //в километры

        // }
       /* while(d!=0) {
            // километры
            if (d - 40 >= 0) {
                hours = hours + 1;
                d = d - 40;
            }
            //метры
            else if (d - 40 < 0) {
                //км/ч в м/с
                if ( d * 1000 - 40 * 0.277777 > 0) {
                    seconds = seconds + 1;
                    d=d-d*0.001;
                }
            }
        }
        while(seconds!=0){
           if(seconds%60!=0){
               minutes=minutes+1;
               seconds=seconds-60;
           }
           else {
               seconds=seconds-1;
               finish_seconds=seconds+1;
           }
        }
        */

       double result=minutes+seconds;
       result=BigDecimal.valueOf(result).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        return result;
    }
    static void shortest_distance(String x,Map <Long,Map<Long,Double>> map,String function) throws IOException {
    //static void shortest_distance(String x,Map <String,String> map,String function){
        //x-откуда
        //map - набор всех ресторанов, расстояние до которых мы ищем

       if(function.equals("Dijkstra")) {
           long start_time = System.currentTimeMillis();
          // logicClass.Dijkstra2(logicClass.adjacency,logicClass.result_map,x,restaurants,true);
           logicClass.Dijkstra(map,logicClass.result_map,x,restaurants);
           long finish_time = System.currentTimeMillis();
           if(logicClass.your_way!=null) {
               String[] points = logicClass.your_way.split("-");
               id_points = new String[points.length];
               for(int i=0;i<points.length;i++){
                   id_points[i]=points[i];
               }
           }

           System.out.format("Время выполения алгоритма Дейкстры %d ms", finish_time - start_time);
       }
        if(function.equals("Levita")) {
            long start_time = System.currentTimeMillis();
           // logicClass.Levita(logicClass.adjacency,logicClass.result_map,x,restaurants);
           // logicClass.Levita(logicClass.H,logicClass.result_map,x,restaurants);
            logicClass.Levita(map,logicClass.result_map,x,restaurants);
            long finish_time = System.currentTimeMillis();
            if(logicClass.your_way!=null) {
                String[] points = logicClass.your_way.split("-");
                id_points = new String[points.length];
                for(int i=0;i<points.length;i++){
                    id_points[i]=points[i];
                }
            }

            System.out.format("Время выполения алгоритма Левита %d ms", finish_time - start_time);
        }
        if(function.equals("A*")) {
            double min_distance=0;
            double []distance=new double[10];
            int current=0;

            long start_time = System.currentTimeMillis();
            for(int i=0;i<10;i++) {
                logicClass.A2(map, logicClass.result_map, x, restaurants[i], true);
                distance[i] = Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            }
            min_distance=distance[0];
            for(int i=0;i<distance.length;i++){
                if(distance[i]<min_distance){
                    min_distance=distance[i];
                    current=i;
                }
            }
            if(min_distance==2.0E9){
                logicClass.near_restaurant=null;
                logicClass.your_way=null;
                System.out.println("увы, не проберетесь");
            }
            else {
                logicClass.near_restaurant = restaurants[current];
                logicClass.your_way = logicClass.ways_to_restaurant.get(restaurants[current]);//?
                String[] points = logicClass.your_way.split("-");
                id_points = new String[points.length];
                for (int i = 0; i < points.length; i++) {
                    id_points[i] = points[i];
                }
            }
            long finish_time = System.currentTimeMillis();
            System.out.format("Время выполения алгоритма А* %d ms", finish_time - start_time);
        }


    }
    //ТЕСТИРОВАНИЕ
    static void test(String [] points) throws IOException {
        long time1[]=new long[100];
        long time2[]=new long[100];
        long time3[]=new long[100];
        double min_distance=0;
        double []distance=new double[10];
        double []time_to_way1=new double[100];
        double []time_to_way2=new double[100];
        double []time_to_way3=new double[100];
        int current_way=0;
        int current=0;
        int i=0;
        for(i=0;i<100;i++) {
            System.out.println("i="+i);
            long start_time = System.currentTimeMillis();
           logicClass.Dijkstra(logicClass.H2,logicClass.result_map,points[i],restaurants);
            long finish_time = System.currentTimeMillis();
            time1[i]=finish_time-start_time;
            time_to_way1[i]=logicClass.time;
            start_time = System.currentTimeMillis();
            logicClass.Levita(logicClass.H2,logicClass.result_map,points[i],restaurants);
            finish_time = System.currentTimeMillis();
            time2[i]=finish_time-start_time;
            time_to_way2[i]=logicClass.time;
            start_time = System.currentTimeMillis();
            for(int k=0;k<10;k++) {
                logicClass.A2(logicClass.H2, logicClass.result_map, points[i], restaurants[k], true);
                distance[k] = Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            }
            for(int g=0;g<distance.length;g++){
                if(distance[g]<min_distance){
                    min_distance=distance[g];
                    current=g;
                }
            }
            if(min_distance==2.0E9){
                logicClass.near_restaurant=null;
                logicClass.your_way=null;
                System.out.println("увы, не проберетесь");
            }
            else {
                logicClass.near_restaurant = restaurants[current];
                logicClass.your_way = logicClass.ways_to_restaurant.get(restaurants[current]);//?

            }
            finish_time = System.currentTimeMillis();
            time3[i]=finish_time-start_time;
            time_to_way3[i]=get_time(min_distance);
          //  get_time()
        }
        long sum1=0,sum2=0,sum3=0;
        double s_t1=0,s_t2=0,s_t3=0;
        for(i=0;i<100;i++) {
            sum1=sum1+time1[i];
            sum2=sum2+time2[i];
            sum3=sum3+time3[i];
            s_t1=s_t1+time_to_way1[i];
            s_t2=s_t2+time_to_way2[i];
            s_t3=s_t3+time_to_way3[i];

        }
        long middle_time1=0,middle_time2=0,middle_time3=0;
        double middle_way_time1=0,middle_way_time2=0,middle_way_time3=0;
        middle_time1=sum1/100;
        middle_time2=sum2/100;
        middle_time3=sum3/100;
        middle_way_time1=s_t1/100;
        middle_way_time2=s_t2/100;
        middle_way_time3=s_t3/100;
        long []result={middle_time1,middle_time2,middle_time3};
        double [] time_result={ middle_way_time1, middle_way_time2, middle_way_time3};
        //сортировка
        for(i=0;i<result.length;i++){
            for(int j=1;j<result.length;j++){
                if(result[i]<result[j]) {
                    long temp = result[i];
                    result[i] = result[i];
                    result[j] = temp;
                }
            }
        }

        System.out.println("Дейкстры:"+middle_time1+" время="+middle_way_time1);
        System.out.println("Левита:"+middle_time2+" время="+middle_way_time2);
        System.out.println("А*:"+middle_time3+" время="+middle_way_time3);;
        if(result[0]==middle_time1) {
            System.out.println("Быстрее работает алгоритм Дейкстры:"+middle_time1);
            System.out.println("Среднее время подъезда=" + middle_way_time1);
        }else if(result[0]==middle_time2){
            System.out.println("Быстрее работает алгоритм Левита:"+middle_time2);
            System.out.println("Среднее время подъезда="+middle_way_time2);
        }
        else if(result[0]==middle_time3){
            System.out.println("Быстрее работает алгоритм А*:"+middle_time3);
            System.out.println("Среднее время подъезда="+middle_way_time3);
        }
    }
    void set_coordinates(){

      //  View.euclid_X = (View.euclid_X - View.X) * View.Multiplier+x;
        View.euclid_X = (View.euclid_X - View.X) * View.Multiplier;
        //View.euclid_Y = (View.euclid_Y - View.Y) *View.Multiplier+y;
        View.euclid_Y = (View.euclid_Y - View.Y) *View.Multiplier;
        if(View.euclid_X <0){
     //       System.out.print("j");
        }
        if(View.euclid_Y <0){
      //      System.out.print("j");
        }
    }
    double get_variable(String variable_name){
        if(variable_name=="lat")
            return View.lat;
        if(variable_name=="lon")
            return View.lon;
        if(variable_name=="euclid_X")
            return View.euclid_X;
        if(variable_name=="euclid_Y")
            return View.euclid_Y;

        return 0;
    }
    public static class TakeActionListener implements ActionListener{
        int x,y;
        public void actionPerformed(ActionEvent e) {
            x=Integer.parseInt(x_coordinates.getText());
            y=Integer.parseInt(y_coordinates.getText());
            try {
                search_way(x,y);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
    public static class TakeActionListener2 implements ActionListener{
        int x,y;
        public void actionPerformed(ActionEvent e) {
            x=Integer.parseInt(x_coordinates.getText());
            y=Integer.parseInt(y_coordinates.getText());
            try {
            search_way_from(x,y);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

}
