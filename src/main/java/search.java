import java.util.Vector;

public class search {

    public search(double [][]d,int n,int m) {

        Vector<Integer> way = new Vector<Integer>();
        int end = 0;
        int t1=0,t2=0;
        while (end != 7) {
            if(way.size()!=0){
                t1=way.get(end-1);
                t2=way.get(end);
            }
            //2

            double[] str_min = new double[n];
            //для каждой строки
            for (int i = 0; i < n; i++) {

                double min=2000; //первый элемент
                //в каждом столбце

                    for (int j = 0; j < m; j++) {
                        if(d[i][j]==-1){
                            continue;
                        }

                            if (min > d[i][j]) {
                                min = d[i][j];
                            }


                    }
                    str_min[i] = min;

            }
            for (int i = 0; i < n; i++) {
                 System.out.println("минимум строки:" + str_min[i]);
            }
            //редукция строк
            for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        if(d[i][j]==-1){
                            continue;
                        }
                            d[i][j] = d[i][j] - str_min[i];
                        }

            }
            System.out.println("После редукции 1");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                         System.out.print(d[i][j]+" ");
                }
                System.out.println();
            }
            //4.минимум по столбцам
            double[] column_min = new double[n];
            //для каждой строки
            for (int i = 0; i < m; i++) {
                double min = 10000; //первый элемент столбца
                //в каждой строке
                for (int j = 0; j < n; j++) {
                    if(d[j][i]==-1){
                        continue;
                    }
                    if (min > d[j][i]) {
                        min = d[j][i];
                    }
                }
                column_min[i] = min;
            }
            for (int i = 0; i < n; i++) {
                System.out.println("минимум столбца:" + column_min[i]);
            }
            //редукция столбцов
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if(d[j][i]==-1){
                        continue;
                    }
                    d[j][i] = d[j][i] - column_min[i];
                }
            }
            System.out.println("После редукции 2");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                           System.out.print(d[i][j]+" ");
                }
                 System.out.println();
            }


            //находим минимальный элемент по строке и по столбцу для каждого нуля
            int[][] where = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (d[i][j] == 0) {
                        where[i][j] = 1;
                    }
                }

            }
            System.out.println("where:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(where[i][j]);
                }
                System.out.println();
                }
            Vector<Integer> iskl = new Vector<Integer>();
            int[] l = new int[n];
            int col = 0;
            for (int i = 0; i < n; i++) {
                    iskl.add(i);

                col = 0;
                for (int j = 0; j < m; j++) {

                    if (where[i][j] == 1) {
                        iskl.add(j); //последовательное исключение
                        col++;
                    }
                }

                l[i] = col;
            }

            double[][] min_sum = new double[n][m];
            int column_to_cross = 0;

            for (int i = 0; i < n; i++) {

                int step = 0;
                double min = 2000;
                // int current=1;
                int current1 = iskl.firstElement();
                iskl.remove(iskl.firstElement());
                System.out.println("строка:"+current1);
                while (step < l[i]) {
             /*  if((where[i][j]==1)&&(current<2)){
                    current++;
                    continue;
                }
                */
                    //строка

                    //столбец
                    int current = iskl.firstElement();
                    iskl.remove(iskl.firstElement());
                    column_to_cross = current;
                    System.out.println("столбец:"+current);
                    double min2 = 0;
                    for (int k = 0; k < m; k++) {
                        if(d[i][k]==-1){
                            continue;
                        }
                        if (k != current) {
                            if (min > d[i][k]) {
                                min = d[i][k]; //минимум в строке
                            }

                        } else {


                            min2 = 200;
                            //перебираем все строки
                            for (int h = 0; h < n; h++) {
                               // System.out.println("h="+h+", k="+k);
                                if(d[h][k]==-1){
                                    continue;
                                }
                                if (h != current1) {
                                    if (min2 > d[h][k]) {
                                        min2 = d[h][k]; //минимум в строке
                                    }

                                }
                              //  System.out.println(min2);
                            }
                         //   System.out.println("итог:минимальный элемент столбца ="+min2);
                        }

                    }
                  //  System.out.println("итог:минимальный элемент строки:"+i+" ="+min);

                    //System.out.println("здесь min строки="+min+", min столбца="+min2);
                    min_sum[i][column_to_cross] = min + min2;
                    //System.out.println("min=" + min_sum[i][column_to_cross]);
                    step++;
                }
            }
            for (int i = 0; i < min_sum.length; i++) {
                for (int j = 0; j < min_sum.length; j++) {
                    System.out.print(min_sum[i][j]);
                }
                System.out.println();
            }
            //редукция матрицы
            double max = -2;
            int str_to_cross = 0;
            for (int i = 0; i < min_sum.length; i++) {
                for (int j = 0; j < min_sum.length; j++) {
                    if(min_sum[i][j]==0){
                        continue;
                    }
                    if (max < min_sum[i][j]) {
                        max = min_sum[i][j];
                        str_to_cross = i;
                        column_to_cross = j;
                    }
                }
            }
            System.out.println("mAX=" + max + " строка=" + str_to_cross + ", столбец=" + column_to_cross);
            d[str_to_cross][column_to_cross] = -1;
            d[column_to_cross][str_to_cross] =-1;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    d[str_to_cross][j]=-1;

                }
                d[i][column_to_cross]=-1;
                }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print(d[i][j]+" ");
                }
                System.out.println();
            }
            way.add(str_to_cross);
            way.add(column_to_cross);
            end++;
        }
        System.out.println("Путь:"+way);
    }
}
