package qwr.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static qwr.util.BgFile.prnq;
import static qwr.util.BgFile.prnt;

public class DateTim {
    public  static final long begSecund = 1610643835L;//2021-01-15T00:03точка отсчета
    static DateTimeFormatter zdtfm=DateTimeFormatter.ofPattern("dd-MM-yy HH:mm (O)");
    static DateTimeFormatter dtfl=DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    public static String currentG(){return zdtfm.format(ZonedDateTime.now());}
    public static String currentL(){return dtfl.format(ZonedDateTime.now());}
    public static boolean previous(String s){//создана ранее
        try { return ZonedDateTime.parse(s,zdtfm).isBefore(ZonedDateTime.now());
        }catch (Exception ex){ return true; }
    }//previous
    public static boolean previous(String s,String n){//создана ранее
        try {
        return ZonedDateTime.parse(s,zdtfm).isBefore(ZonedDateTime.parse(n,zdtfm));
        }catch(Exception ex){ return true; }
    }//previous
    //проверка длинны строки
    public static boolean tstlnd(String s){//соответствие длинны строки на формату даты
        return s.length()<22 && s.length()>23;
    }//tstlnd
    public static String tstlnds(String s){//соответствие длинны строки на формату даты
        return s.length()<22 && s.length()>23 ? s : "";
    }//tstlnd
    //------------------------------------------------------------------------------
    //определение количества секунд прошедших до данного момента 1.01.1970
    public static int  newSeconds() {
        return Math.toIntExact(Instant.now().getEpochSecond()-begSecund);//cек
    }//newSeconds
    //------------------------------------------------------------------------------
    //получение даты на основе количества прошедших секунд с  1.01.1970
    public static String newDateFromSeconds(long i){
        return zdtfm.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(i),
                ZonedDateTime.now().getZone()));
    }//newDateFromSeconds
    //-------------------------------------------------------------------------------
    public static String newDateFromSeconds(int i){
        long j = begSecund +i;
        return zdtfm.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(j),
                ZonedDateTime.now().getZone()));
    }//newDateFromSeconds
    //-----------------------------------------------------------------------------
    //тестирование работы со временем
    public static void newDateFromSecondst(long i){
        Instant dd = Instant.ofEpochSecond(begSecund);
        ZonedDateTime nw = ZonedDateTime.now();
        ZoneId z= nw.getZone();
        ZonedDateTime td = ZonedDateTime.ofInstant(dd, z);
        prnq("qq "+dd+"    "+td+"   "+z);
    }//newDateFromSecondst
    //-------------------------------------------------------------------------------
    public int cnvertd(String s){
        try {
            LocalDate ld =LocalDate.parse(s,DateTimeFormatter.ofPattern("dd.MM.yy"));
            return (int) ChronoUnit.DAYS.between(LocalDate.of(1899, 12, 30), ld);
        } catch (Exception e){return 0;} }//cnvertd
    //------------------------------------------------------------
    public double cnvert(String s){
        return Double.parseDouble(s.replace(".","")
                .replace(".","").replace(',','.')); }//cnvert


    @Deprecated //устаревший метод---------------------------------------------------
    public static boolean convertString( String s) {
        //очищаю строку от лишних символов
//        prnq("- "+s);
        int length = s.length();
        char[] y = new char[length + 1];//создаю пустой массив
        int i=0;
        char cf = 32;
        for (int j = 0; j < length; j++) {
            char ch = s.charAt(j);
            if (ch<32 || ch==32 && cf==ch) continue;
            y[i++]=ch;
            cf=ch;
        }//цикл по строке
        String z = new String(y, 0, i);
        prnq("~"+z);
        //Определяю количество "ОТ"
        String[] ars=new String[5];
        char[] ct= new char[33];//временный массив для предварительной сборки
        int[] ari = new int[5];
        int n=0;
        int k=0;//количество записей
        int d=-1;
        int m=-1;
        int g=-1;
        int t=0;
        short p=0;//номер прохода
        for (int j = 0; j < i; j++) {
            switch (p) {
                case 0: if (y[j]>32 && y[j]!='/') { n=0; p=1; ct[n++]=y[j]; }//перехожу к следующему шагу
                    break;
                case 1: if (y[j]=='о'){  p=2; ars[k]=new String(ct); }
                    ct[n++]=y[j];
                    break;
                case 2:  if (y[j]=='т')  p=3; break;
                case 3:  if (y[j]>='0' && y[j]<='9') { t=(y[j]-'0'); p=4;} break;
                case 4:  if (y[j]>='0' && y[j]<='9')  {t=(y[j]-'0')+t*10;}
                else { d=t; p=5;}
                    break;
                case 5:  if (y[j]>='0' && y[j]<='9') { t=(y[j]-'0'); p=6;} break;
                case 6:  if (y[j]>='0' && y[j]<='9')  {t=(y[j]-'0')+t*10;}
                else { m=t; p=7;}
                    break;
                case 7:  if (y[j]>='0' && y[j]<='9') { t=(y[j]-'0'); p=8;} break;
                case 8:  if (y[j]>='0' && y[j]<='9')  {t=(y[j]-'0')+t*10;}
                else {  p=0;
                    g=t<100 ? t+2000 : t;
                    ari[k++]= (int) ChronoUnit.DAYS.between(
                            LocalDate.of(1899, 12, 30),
                            LocalDate.of(g, m, d)); }
                    //ari[k++]= g+m*10000+d*1000000; }
                    break;
                default: prnq("Error while");
            }//switch
            if (k>5) { prnq("Error k>5 "); break; }//превышение размера буфера
        }//цикл по строке
        prnq("****");
        for (int j=0; j<k; j++){ prnq(" "+ars[j]+"   "+ari[j]); }
        return false;
    }//convertString
    //-----------------------------------------------------------------------------
    public static boolean convertStringQ( String s) {//предыдущая версия очищения
        //очищаю строку от лишних символов
        prnq(" >"+s+"< ");
        //Определяю количество "ОТ"
        String[] ars=new String[5];
        char[] ct= new char[33];//временный массив для предварительной сборки
        int[] ari = new int[5];
        int n=0;
        int k=0;//количество записей
        int d=-1;
        int m=-1;
        int g=-1;
        int t=0;
        short p=0;//номер прохода
        char cb=32;
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            if ((c<32 || c==32 && cb==c)) continue; //обхожу непечатные символы
            cb=c;
            switch (p) {
                case 0: if (c>32 && c!='/') { n=0; p=1; ct[n++]=c; }//перехожу к следующему шагу
                    break;
                case 1: if (c=='о'){  p=2; ars[k]=new String(ct); }
                    ct[n++]=c;
                    break;
                case 2:  if (c=='т')  p=3; break;
                case 3:  if (c>='0' && c<='9'){ t=(c-'0'); p=4;} break;
                case 4:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { d=t; p=5;}
                    break;
                case 5:  if (c>='0' && c<='9'){ t=(c-'0'); p=6;} break;
                case 6:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { m=t; p=7;}
                    break;
                case 7:  if (c>='0' && c<='9'){ t=(c-'0'); p=8;} break;
                case 8:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else {  p=0;
                    g=t<100 ? t+2000 : t;
                    ari[k++]= (int) ChronoUnit.DAYS.between(
                            LocalDate.of(1899, 12, 30),
                            LocalDate.of(g, m, d)); }
                    //ari[k++]= g+m*10000+d*1000000; }
                    break;
                default: prnq("Error while");
            }//switch
            if (k>5) { prnq("Error k>5 "); break; }//превышение размера буфера
        }//цикл по строке
        prnq("****"+p);
        for (int j=0; j<k; j++){ prnq(" "+ars[j]+"   "+ari[j]); }
        return false;
    }//convertString-----------------------------------------------------------------

    /**
     * Преобразование строки содержащую дату в число дней прошедших с 1900 года
     * @param s строка содержащая дату начинающуюся с перфикса "от"
     * возвращает количество дней от 1990 года и строку перед датой без пробелов
     * !!! для нормальной работы строка должна заканчиваться символом после даты !!!
     * @param eld массив для записи результата распознования строки и количества дней
     * от 1990 года
     * @return возвращает количество обнаруженных и распознаных дат
     */
    public static int convertStringR(String s, EinStr[] eld) {//предыдущая версия очищения
        assert prnt(" >"+s+"< ");
        //Определяю количество "ОТ"
//        String[] ars=new String[5];
        char[] ct= new char[s.length()];//временный массив для предварительной сборки
        //char[] ct= new char[33];//временный массив для предварительной сборки
//        int[] ari = new int[5];
        int n=0;
        int k=0;//количество записей
        int d=-1;
        int m=-1;
        int g=-1;
        int t=0;
        short p=0;//номер прохода
        char cb=32;
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
//            prnt("\n"+j+"\t"+p+"~\t"+(int)cb+"\t"+(int)c+"   \t"+c+"\t");
            if ((c<32 || c==32 && cb==c)) continue; //обхожу непечатные символы
            cb=c;
            switch (p) {
                case 0: if (c>32 && c!='/') { n=0; p=1; ct[n++]=c; }//перехожу к следующему шагу
                    break;
                case 1: if (c=='о'){  p=2; eld[k].s=new String(ct); }
//                case 1: if (c=='о'){  p=2; ars[k]=new String(ct); }
                    ct[n++]=c;
                    break;
                case 2:  if (c=='т')  p=3; break;
                case 3:  if (c>='0' && c<='9'){ t=(c-'0'); p=4;} break;
                case 4:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { d=t; p=5;}
                    break;
                case 5:  if (c>='0' && c<='9'){ t=(c-'0'); p=6;} break;
                case 6:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { m=t; p=7;}
                    break;
                case 7:  if (c>='0' && c<='9'){ t=(c-'0'); p=8;} break;
                case 8:  if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                            else { p=0;
                                g=t<100 ? t+2000 : t;
                                eld[k++].i= (int) ChronoUnit.DAYS.between(
//                                ari[k++]= (int) ChronoUnit.DAYS.between(
                                LocalDate.of(1899, 12, 30),
                                LocalDate.of(g, m, d));
                                assert prnq("$ convertStringR >"+g+" "+m+" "+d);
                            }
                    //ari[k++]= g+m*10000+d*1000000; }
                    break;
                default: prnq("Error while");
            }//switch
            if (k>eld.length) { prnq("Error array is less "); break; }//превышение размера буфера
//            prnq("~");
        }//цикл по строке
        return k;
    }//convertString-----------------------------------------------------------------
    /**
     * Преобразование строки содержащую дату в число дней прошедших с 1900 года
     * @param s строка содержащая дату начинающуюся с перфикса "от"
     * возвращает количество дней от 1990 года
     * !!! для нормальной работы строка должна заканчиваться символом после даты !!!
     * @return количества дней от 1990 года
     */
    public static int convertStringR(String s) {//предыдущая версия очищения
//        assert prnt(" >"+s+"< ");
        //Определяю количество "ОТ"
        char[] ct= new char[s.length()];//временный массив для предварительной сборки
        int n=0;
        int k=0;//количество записей
        int d=-1;
        int m=-1;
        int g=-1;
        int t=0;
        short p=0;//номер прохода
        char cb=32;
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
//            prnt("\n"+j+"\t"+p+"~\t"+(int)cb+"\t"+(int)c+"   \t"+c+"\t");
            if ((c<32 || c==32 && cb==c)) continue; //обхожу непечатные символы
            cb=c;
            switch (p) {
                case 0: if (c>32 && c!='/') { n=0; p=1; ct[n++]=c; }//перехожу к следующему шагу
                    break;
                case 1: if (c=='о'){  p=2; }//здесь было сохранение строки до ОТ
                    ct[n++]=c;
                    break;
                case 2: if (c=='т')  p=3; break;
                case 3: if (c>='0' && c<='9'){ t=(c-'0'); p=4;} break;
                case 4: if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { d=t; p=5;}
                    break;
                case 5: if (c>='0' && c<='9'){ t=(c-'0'); p=6;} break;
                case 6: if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                else { m=t; p=7;}
                    break;
                case 7: if (c>='0' && c<='9'){ t=(c-'0'); p=8;} break;
                case 8: if (c>='0' && c<='9'){ t=(c-'0')+t*10;}
                        else {  p=0;
                           // g=t;
                        g=t<100 ? t+2000 : t;
//                            assert prnq("$ convertStringR >"+g+" "+m+" "+d);
                            return  (int) ChronoUnit.DAYS.between(
                                    LocalDate.of(1899, 12, 30),
                                    LocalDate.of(g, m, d));
                        }
                    break;
                default: prnq("Error while");
            }//switch
        }//цикл по строке
        return 0;
    }//convertString-----------------------------------------------------------------

    /**
     * определения присутствия цифр в строке
     * @param s исходная стока
     * @return наличие цифр
     */
    public static boolean isNumbr(String s){
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            if (c>='0' && c<='9') return true;
        }//for
        return false;
    }//testNumbr

}//class DateTim