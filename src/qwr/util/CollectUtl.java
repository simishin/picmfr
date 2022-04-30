/**
 * Сборник всевозможных утилит без использования классов
 * начата 130322
 */
package qwr.util;

public class CollectUtl {
    public  static boolean  prnq(String s){System.out.println(s); return true;}
    public  static boolean  prnt(String s){System.out.print(s); return true;}
    public  static boolean  prne(String s){System.err.println(s); return true;}
    //определяю разделитель элементов пути определенных операционной системой
    public  static final String fileSeparator=System.getProperty("file.separator");
    public  static final String sepr="\t";  //разделитель значений в файлах данных
    /*
     * Задание 7
     * Показать битовое представление значения переменной
     * типа int, используя только один цикл, управляющую пере-
     * менную, вывод на консоль и битовые операции.
     * Не использовать строки и любые другие готовые функ-
     * ции (методы).
     */
    public static void Job7() {
        int r=675;
        int j=r;
        prnq(j+"\t");
        for (int i=0; i<(4*8); i++){
            if ((j & 0x80000000) != 0) prnt("1");
            else prnt("0");
            if ((i+1)%4 ==0 ) prnt("_");
            j&=0x7FFFFFFF;
            j<<=1;
        }
    }//Job

    /**
     * Получение бинарного представления числа в виде строки
     * @param x число для перобразования в строку
     * @return бинарное представление Х
     */
    public static String prnBinLong(long x){
        char[] y=new char[80];
        for (int i = y.length-1; i >= 0; i--) {
            if (i==40){         y[i]='"'; continue; }
            if ((i)%10 ==0 ) {  y[i]='.'; continue; }
            if ((i)%5 ==0 ) {   y[i]='`'; continue; }
                if ((x & 1) == 0) y[i] = '0';
                else y[i] = '1';
                x >>>= 1;
        }//for
        return new String(y);
    }//prnBolLong

}//class CollectUtl==================================================

