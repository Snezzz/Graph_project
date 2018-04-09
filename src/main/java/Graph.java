/**
 * Created by Снежа on 11.03.2018.
 */

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

public class Graph extends JFrame {
    static LogicClass logicClass;
    static Map <String,String> coordinates=new TreeMap<String, String>();
    static GetRestaurants R;
    static String neighbor;
    static String[] restaurants;
    static String[] random_points;
   // static View view;
    static Object v1,v2;
    static double x,y;
    static double getX,getY;
    static JFrame frame,frame2;
    static JLabel text,text2,text3,for_x,for_y;
    static JPanel panel,panel2;
    static Point point;
    static JTextField x_coordinates,y_coordinates;
    static int width=1000,height=1000;
    double print_X,print_Y;
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        long start_time = System.currentTimeMillis();

        logicClass=new LogicClass();
        R=new GetRestaurants();
        logicClass.go();
        R.go();

        random_points=new String[100];
        Iterator iterator2 =GetRestaurants.ten_objects.entrySet().iterator();
        int i=0;
        while (iterator2.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator2.next();
            restaurants[i]=pair.getKey().toString();
            i++;
        }

            frame = new Graph();
        Iterator iterator =logicClass.adjacency.entrySet().iterator();
        Random rand=new Random();
        rand.setSeed(10);
        int position=rand.nextInt();
        int step=0;
        int k=0;
        while ((iterator.hasNext())&&(k!=100)) {
            Map.Entry pair = (Map.Entry) iterator.next();
            if(step==step+position){
                random_points[k]=pair.getKey().toString();
                k++;
            }
            step++;
        }
        //тестирование алгоритмов на 100 точек равномерно распределенных
        test(random_points);
       // frame = new Graph();
        // /frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        text3.setBounds(20,160,1000,20);
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
        JButton take=new JButton("Let's go!");
        take.setVisible(true);
        take.setBounds(20,140,100,20);

        panel.add(text);
        panel.add(text2);
        panel.add(text3);
        panel.add(for_x);
        panel.add(x_coordinates);
        panel.add(for_y);
        panel.add(y_coordinates);
        panel.add(take);

        ActionListener actionListener = new TakeActionListener();
        take.addActionListener(actionListener);

        frame2.add(panel);
        //frame.add(frame2);
//        frame2.add(panel);
        //frame.add(frame2);
        frame.setVisible(true);
        frame2.setVisible(true);

       // logicClass.D(logicClass.adjacency,"1008916451","1009191490");
/*


        x=width/2;
        y=height/2;

        Graph frame = new Graph();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);

        Panel panel=new Panel();
        panel.setVisible(true);
        frame.add(panel);
        frame.setVisible(true);
        */

        long finish_time = System.currentTimeMillis();
        System.out.format("%d ms", finish_time - start_time);

    }

    public Graph()
    {
        super("Graph");

        final mxGraph graph = new mxGraph();
        final Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();

        int k=20,z=20;
        Random rand=new Random();
        boolean decision=false;
        boolean decision2=false;
        Map <String,Integer> search=new HashMap<String,Integer>();
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
            v1=graph.insertVertex(parent, null,"",  print_X, print_Y, 1, 1);
            related = (logicClass.adjacency.get(pair.getKey())).split(";");
            Object []v=new Object[related.length];
            for(int i=0;i<related.length;i++){
                param=logicClass.coordinates.get(related[i]).split(",");
                new View(Double.parseDouble(param[0]),Double.parseDouble(param[1])); //lat lon
                set_coordinates();
                print_X=get_variable("euclid_X");
                print_Y=get_variable("euclid_Y");
                coordinates.put(related[i],print_X+","+print_Y);
                v[i]= graph.insertVertex(parent, null,"",  print_X, print_Y, 1, 1,"fillColor=brown");
            }
            for(int i=0;i<related.length;i++){
                graph.insertEdge(parent, null, "", v1, v[i]); //v1->v2
            }


        }
        Object v4;
        while (iterator2.hasNext()) {

            Map.Entry pair = (Map.Entry) iterator2.next();
            String[] related;
            related = (GetRestaurants.objects_coordinates.get(pair.getKey())).split(",");

                new View(Double.parseDouble(related[0]),Double.parseDouble(related[1])); //lat lon
                set_coordinates();
                print_X=get_variable("euclid_X");
                print_Y=get_variable("euclid_Y");
                coordinates.put(pair.getKey().toString(),print_X+","+print_Y);
                v4= graph.insertVertex(parent, null,"restaurant",  print_X, print_Y, 2, 2,"fillColor=red");

        }

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
                int x, y;
                //получили координаты
                getX = e.getX();
                getY = e.getY();
                x = (int) getX;
                y = (int) getY;

                point= new Point(x, y);
                System.out.println(point);
                graph.removeCellsFromParent();
                graph.removeSelectionCell(v2);
                neighbor=logicClass.get_place(x,y,coordinates); //определяем ближайшего к точке соседа
                logicClass.adjacency.put("current",neighbor); //занесли текущую точку и ее соседа
                v2=graph.insertVertex(parent, null,"", point.x , point.y, 3   , 3,"fillColor=red");
               // graph.getModel().endUpdate();
            }
        });
        graph.getModel().endUpdate();

       // getContentPane().add(graphControl);
        getContentPane().add(graphComponent);

        panel2.setBackground(Color.cyan);


    }
    static void shortest_distance(String x,Map <String,String> map,String function){
        //x-откуда
        //map - набор всех ресторанов, расстояние до которых мы ищем

       if(function=="Dijkstra") {
           long start_time = System.currentTimeMillis();
           logicClass.Dijkstra2(logicClass.adjacency,logicClass.result_map,x,restaurants,true);
           long finish_time = System.currentTimeMillis();
           System.out.format("Время выполения алгоритма Дейкстры %d ms", finish_time - start_time);
       }
        if(function=="Levita") {
            long start_time = System.currentTimeMillis();
            logicClass.Levita(logicClass.adjacency,logicClass.result_map,x,restaurants);
            long finish_time = System.currentTimeMillis();
            System.out.format("Время выполения алгоритма Левита %d ms", finish_time - start_time);
        }
        if(function=="A*") {
            double min_distance=0;
            double []distance=new double[10];
            int current=0;
            int i=0;
            long start_time = System.currentTimeMillis();
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[0],true);
            distance[0]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            //есть путь и значение пути
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[1],true);
            distance[1]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[2],true);
            distance[2]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[3],true);
            distance[3]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[4],true);
            distance[4]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[5],true);
            distance[5]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[6],true);
            distance[6]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[7],true);
            distance[7]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[8],true);
            distance[8]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,x,restaurants[9],true);
            distance[9]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            for(i=0;i<distance.length;i++){
                if(distance[i]<min_distance){
                    min_distance=distance[i];
                    current=i;
                }
            }
            logicClass.near_restaurant=restaurants[i];
            long finish_time = System.currentTimeMillis();
            System.out.format("Время выполения алгоритма А* %d ms", finish_time - start_time);
        }


    }
    static void test(String [] points){
        long time1[]=new long[100];
        long time2[]=new long[100];
        long time3[]=new long[100];
        double min_distance=0;
        double []distance=new double[10];
        int current_way=0;
        int current=0;
        int i=0;
        for(i=0;i<100;i++) {
            long start_time = System.currentTimeMillis();
            logicClass.Dijkstra2(logicClass.adjacency,logicClass.result_map,points[i],restaurants,true);
            long finish_time = System.currentTimeMillis();
            time1[i]=start_time-finish_time;
            start_time = System.currentTimeMillis();
            logicClass.Levita(logicClass.adjacency,logicClass.result_map,points[i],restaurants);
            finish_time = System.currentTimeMillis();
            time2[i]=start_time-finish_time;
            start_time = System.currentTimeMillis();
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[1],true);
            distance[1]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[2],true);
            distance[2]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[3],true);
            distance[3]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[4],true);
            distance[4]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[5],true);
            distance[5]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[6],true);
            distance[6]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[7],true);
            distance[7]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[8],true);
            distance[8]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            logicClass.A2(logicClass.adjacency,logicClass.result_map,points[i],restaurants[9],true);
            distance[9]=Double.parseDouble(logicClass.ways_to_restaurant.get("width"));
            for(i=0;i<distance.length;i++){
                if(distance[i]<min_distance){
                    min_distance=distance[i];
                    current=i;
                }
            }
            logicClass.near_restaurant=restaurants[i];
            finish_time = System.currentTimeMillis();
            time3[i]=start_time-finish_time;
        }
        long sum1=0,sum2=0,sum3=0;
        for(i=0;i<100;i++) {
            sum1=sum1+time1[i];
            sum2=sum2+time2[i];
            sum3=sum3+time3[i];
        }
        long middle_time1=0,middle_time2=0,middle_time3=0;
        middle_time1=sum1/100;
        middle_time2=sum2/100;
        middle_time3=sum3/100;
        long []result={sum1,sum2,sum3};
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
        if(result[0]==sum1){
            System.out.println("Быстрее работает алгоритм Дейкстры");
        }
        else if(result[0]==sum2){
            System.out.println("Быстрее работает алгоритм Левита");
        }
        else if(result[0]==sum3){
            System.out.println("Быстрее работает алгоритм А*");
        }
    }
    void set_coordinates(){
        View.euclid_X = (View.euclid_X - View.X) * View.Multiplier+x;
        View.euclid_Y = (View.euclid_Y - View.Y) *View.Multiplier+y;
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
        String x,y;
        public void actionPerformed(ActionEvent e) {
            x=x_coordinates.getText();
            y=y_coordinates.getText();

        }
    }

}
