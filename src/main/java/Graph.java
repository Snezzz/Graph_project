/**
 * Created by Снежа on 11.03.2018.
 */

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class Graph extends JFrame {
    static LogicClass logicClass;
   // static View view;
    static Object v1;
    static double x,y;
    static int width=1000,height=1000;
    double print_X,print_Y;
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        long start_time = System.currentTimeMillis();
        logicClass=new LogicClass();
       // view=new View();
        logicClass.go();
        x=width/2;
        y=height/2;
        Graph frame = new Graph();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
        System.out.println("Граф построен");
        long finish_time = System.currentTimeMillis();
        System.out.format("%d ms", finish_time - start_time);
    }

    public Graph()
    {
        super("Graph");
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        int k=20,z=20;
        Random rand=new Random();
        boolean decision=false;
        boolean decision2=false;
        Map <String,Integer> search=new HashMap<String,Integer>();
        Map <String,Object> get=new HashMap<String,Object>();
        Iterator iterator =logicClass.adjacency.entrySet().iterator();
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
            v1=graph.insertVertex(parent, null,"",  print_X, print_Y, 1, 1);
            related = (logicClass.adjacency.get(pair.getKey())).split(";");
            Object []v=new Object[related.length];
            for(int i=0;i<related.length;i++){
                param=logicClass.coordinates.get(related[i]).split(",");
                new View(Double.parseDouble(param[0]),Double.parseDouble(param[1])); //lat lon
                set_coordinates();
                print_X=get_variable("euclid_X");
                print_Y=get_variable("euclid_Y");
                v[i]= graph.insertVertex(parent, null,"",  print_X, print_Y, 1, 1);
            }
            for(int i=0;i<related.length;i++){
                graph.insertEdge(parent, null, "", v1, v[i]); //v1->v2
            }


        }

        graph.getModel().endUpdate();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

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
}
