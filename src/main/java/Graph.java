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
    static Object v1;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        logicClass=new LogicClass();
        logicClass.go();
        Graph frame = new Graph();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
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
        while (iterator.hasNext()) {
                k=k+20;
                z=z+1000;
            Map.Entry pair = (Map.Entry) iterator.next();
            //перекрестки
            String[] related;
                related = (logicClass.adjacency.get(pair.getKey())).split(";"); //разрезаем id перекрестка = id точек пересечения
//проверка на повторение
            if (search.get(pair.getKey().toString()) != null) {
                decision2 = false;
            } else {
                decision2 = true;
                search.put(pair.getKey().toString(), 1);
            }
            for (int i = 0; i < related.length; i++) {
                if (search.size() == 0) {
                    search.put(related[i], i + rand.nextInt());
                } else {
                   // if(related[0]=="0"){
                     //   decision = true;
                       // search.put(pair.getKey().toString(), i + rand.nextInt());
                    //}
                    //else {
                        if (search.get(related[i]) != null) {
                            decision = false;

                        } else {
                            decision = true;
                            search.put(related[i], i + rand.nextInt());
                        }
                    }
               // }
            }

            //рассматриваемые id начальной точки без повторений
           if(decision2==true){
               v1= graph.insertVertex(parent, null,pair.getKey().toString(),  rand.nextInt(50000), rand.nextInt(50000), 5, 5); //строим все точки

           }
            Object[] v = new Object[logicClass.V];
           if(decision==true) {
               //строим точки пересечения
               /*
                       for (int i = 0; i < related.length; i++) {
                           rand.setSeed(z*20);
                           for (Map.Entry entry : logicClass.crosses.entrySet()) {
                               if( entry.getKey()==related[i]){
                                   key=entry.getKey().toString();
                               }
                           }
                           v[i] = graph.insertVertex(parent, null, key, rand.nextInt(50000) , rand.nextInt(50000), 5, 5);
                           z = z + 30;
                       }


                   else {
                   */
               for (int i = 0; i < related.length; i++) {
                   rand.setSeed(z * 20);
                   v[i] = graph.insertVertex(parent, null, pair.getKey().toString(), rand.nextInt(50000), rand.nextInt(50000), 5, 5);
                   get.put(related[i],v[i]);
                   z = z + 30;
                   //  }
               }
           }
         // if(related[0]!="0")
            if(decision==true) {
                for (int i = 0; i < related.length; i++) {

                    //соединяем их с рассматриваемой вершиной
                    graph.insertEdge(parent, null, "", v1, v[i]); //v1->v2
                }
                z = z + 30;
            }
            else{
                for (int i = 0; i < related.length; i++) {

                    //соединяем их с рассматриваемой вершиной
                    if(get.get(related[i])!=null) {
                        v[i] = get.get(related[i]);
                        graph.insertEdge(parent, null, "", v1, v[i]); //v1->v2
                    }
                    else{
                        v[i] = graph.insertVertex(parent, null, pair.getKey().toString(), rand.nextInt(50000), rand.nextInt(50000), 5, 5);
                        graph.insertEdge(parent, null, "", v1, v[i]); //v1->v2
                        get.put(related[i],v[i]);
                    }
                }
            }
        }
        graph.getModel().endUpdate();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

    }

}
